package com.tcts.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.formdata.CreateBankFormData;
import com.tcts.formdata.EditBankFormData;
import org.springframework.stereotype.Component;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteAdmin;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.UserType;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;


/**
 * The (one and only) implementation of DatabaseFacade.
 */
@Component
public class MySQLDatabase implements DatabaseFacade {
    private final static String userFields =
            "user_id, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status";
    private final static String eventFields =
            "event_id, teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id";
    private final static String bankFields =
            "bank_id, bank_name, bank_admin";
    private final static String schoolFields =
            "school_id, school_name, school_addr1, school_addr2, school_city, school_zip, school_county, school_district, school_state, school_phone, school_lmi_eligible";

    private final static String getUserByIdSQL =
            "select " + userFields + " from User where user_id = ?";
    
    private final static String getUserByTypeSQL =
            "select " + userFields + " from User where access_type = ?";
    
    private final static String getAllUserSQL =
            "select " + userFields + " from User";
    
    private final static String getUserByEmailSQL =
            "select " + userFields + " from User where email = ?";
    private final static String getVolunteersByBankSQL =
            "select " + userFields + " from User where access_type = 'V' and organization_id = ?";
    private final static String getBankAdminByBankSQL =
            "select " + userFields + " from User where access_type = 'BA' and organization_id = ?";
    private final static String getEventsByTeacherSQL =
            "select " + eventFields + " from Event where teacher_id = ?";
    private final static String getEventsByVolunteerSQL =
            "select " + eventFields + " from Event where volunteer_id = ?";
    private final static String getAllAvailableEventsSQL =
    		"select " + eventFields + " from Event where volunteer_id is null";
    private final static String getBankByIdSQL =
            "select " + bankFields + " from Bank where bank_id = ?";
    private final static String getAllBanksSQL =
            "select " + bankFields + " from Bank";
    private final static String getSchoolByIdSQL =
            "select " + schoolFields + " from School where school_id = ?";
    private final static String getAllSchoolsSQL =
            "select " + schoolFields + " from School";
    private final static String getAllowedDatesSQL =
            "select event_date from AllowedDates order by event_date";
    private final static String getAllowedTimesSQL =
            "select event_time from AllowedTimes order by sort_order";
    private final static String getLastInsertIdSQL =
            "select last_insert_id() as last_id";
    private final static String modifyUserPersonalFieldsSQL =
            "update User set email=?, first_name=?, last_name=?, phone_number=? where user_id=?";
    private final static String insertUserSQL =
            "insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String insertEventSQL =
            "insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (?, ?, ?, ?, ?, ?, ?)";
    private final static String volunteerForEventSQL =
            "update Event set volunteer_id = ? where event_id = ?";
    
    private final static String insertSchoolSQL  =
    		"insert into School (school_name,school_addr1,school_addr2,school_city,school_zip,school_county,school_district,school_state,school_phone,school_lmi_eligible) VALUES (?,?,?,?,?,?,?,?,?,?)";
    
    private final static String insertBankSQL =
    		"insert into Bank (bank_name) VALUES (?)";
    private final static String modifyBankByIdSQL =
            "update Bank set bank_name = ?,bank_admin = ? where bank_id = ?";

    private final static String deleteUsersByBankId =
            "delete from User where access_type = 'BA' or access_type = 'V' and organization_id = ?";
    private final static String deleteBankSQL =
    		"delete from Bank where bank_id = ?";
    
    private final static String deleteSchooldSQL =
    		"delete from School where schoold_id = ?";
      
    private final static String deleteVolunteerSQL =
    		"delete from User where user_id = ? ";
        
    private final static String deleteEventSQL =
    		"delete from Event where event_id=? ";

    private final static String getEventsSQL =
            "select " + eventFields + " from Event ";
    
    private final static String getEventByIdSQL =
            "select " + eventFields + " from Event where event_id = ?";
    
    private final static String updateEventByIdSQL = 
    		"UPDATE Event SET teacher_id = ?,event_date = ?,event_time = ?,grade = ?,number_students = ?,notes = ?,volunteer_id =  ? WHERE event_id = ?";
    
    private final static String updateSchoolByIdSQL =
    		"UPDATE School SET " +
    		"school_name = ?,school_addr1 = ?,school_addr2 = ?,school_city = ?,school_zip = ?,school_county = ?," +
    		"school_district = ?,school_state = ?,school_phone = ?,school_lmi_eligible = ? WHERE school_id = ?";

    private final static String updateUserCredentialsByIdSQL =
    		"update User set password_salt = ?, password_hash = ? where user_id = ?";
    
    

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        return getUserByIdOrEmail(userId, getUserByIdSQL);
    }


    @Override
    public User getUserByEmail(String email) throws SQLException, InconsistentDatabaseException {
        return getUserByIdOrEmail(email, getUserByEmailSQL);
    }

    private User getUserByIdOrEmail(String key, String sql) throws SQLException, InconsistentDatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            User user = null;
            while (resultSet.next()) {
                numberOfRows++;
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                switch(userType) {
                    case VOLUNTEER: {
                        Volunteer volunteer = new Volunteer();
                        volunteer.populateFieldsFromResultSetRow(resultSet);
                        user = volunteer;
                    } break;
                    case TEACHER: {
                        Teacher teacher = new Teacher();
                        teacher.populateFieldsFromResultSetRow(resultSet);
                        user = teacher;
                    } break;
                    case BANK_ADMIN: {
                        BankAdmin bankAdmin = new BankAdmin();
                        bankAdmin.populateFieldsFromResultSetRow(resultSet);
                        user = bankAdmin;
                    } break;
                    case SITE_ADMIN: {
                        SiteAdmin siteAdmin = new SiteAdmin();
                        siteAdmin.populateFieldsFromResultSetRow(resultSet);
                        user = siteAdmin;
                    } break;
                    default: {
                        throw new RuntimeException("This should never occur.");
                    }
                }
            }
            if (numberOfRows < 1) {
                return null; // No user found
            } else if (numberOfRows > 1) {
                throw new InconsistentDatabaseException("Multiple rows for ID or email of '" + key + "'.");
            } else {
                return user;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public User modifyUserPersonalFields(String userId, EditPersonalDataFormData formData)
            throws SQLException, EmailAlreadyInUseException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(modifyUserPersonalFieldsSQL);
            preparedStatement.setString(1, formData.getEmail());
            preparedStatement.setString(2, formData.getFirstName());
            preparedStatement.setString(3, formData.getLastName());
            preparedStatement.setString(4, formData.getPhoneNumber());
            preparedStatement.setString(5, userId);
            try {
                preparedStatement.executeUpdate();
            } catch(MySQLIntegrityConstraintViolationException err) {
                if (err.getMessage().contains("Duplicate entry") && err.getMessage().contains("for key 'ix_email'")) {
                    throw new EmailAlreadyInUseException();
                } else {
                    throw err;
                }
            }
        } finally {
            closeSafely(connection, preparedStatement, null);
        }
        return getUserById(userId);
    }

    @Override
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException {
        return getEventsByTeacherOrVolunteer(teacherId, getEventsByTeacherSQL);
    }

    @Override
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException {
        return getEventsByTeacherOrVolunteer(volunteerId, getEventsByVolunteerSQL);
    }

    /**
     * Common code to get events by either teacher or volunteer.
     *
     * @param userId the teacherId or volunteerId
     * @param sql either getEventsByTeacherSQL or getEventsByVolunteerSQL
     * @return the list of events
     * @throws SQLException
     */
    public List<Event> getEventsByTeacherOrVolunteer(String userId, String sql) throws SQLException {
        List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event();
                event.populateFieldsFromResultSetRow(resultSet);
                events.add(event);
            }
            return events;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public List<Event> getAllAvailableEvents() throws SQLException {
        List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllAvailableEventsSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event();
                event.populateFieldsFromResultSetRow(resultSet);
                events.add(event);
            }
            return events;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(insertEventSQL);
            preparedStatement.setString(1, teacherId);
            preparedStatement.setDate(2, new java.sql.Date(formData.getEventDate().getTime()));
            preparedStatement.setString(3, formData.getEventTime());
            preparedStatement.setString(4, formData.getGrade());
            preparedStatement.setInt(5, Integer.parseInt(formData.getNumberStudents()));
            preparedStatement.setString(6, formData.getNotes());
            preparedStatement.setString(7, null); // volunteerId
            preparedStatement.executeUpdate();
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public void volunteerForEvent(String eventId, String volunteerId) throws SQLException, NoSuchEventException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(volunteerForEventSQL);
            preparedStatement.setString(1, volunteerId);
            preparedStatement.setString(2, eventId);
            preparedStatement.executeUpdate();
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException {
        List<Volunteer> volunteers = new ArrayList<Volunteer>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getVolunteersByBankSQL);
            preparedStatement.setString(1, bankId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                if (userType != UserType.VOLUNTEER) {
                    throw new RuntimeException("This should not occur.");
                }
                Volunteer volunteer = new Volunteer();
                volunteer.populateFieldsFromResultSetRow(resultSet);
                volunteers.add(volunteer);
            }
            return volunteers;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public BankAdmin getBankAdminByBank(String bankId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getBankAdminByBankSQL);
            preparedStatement.setString(1, bankId);
            resultSet = preparedStatement.executeQuery();
            BankAdmin bankAdmin = null;
            int resultCount = 0;
            while (resultSet.next()) {
                resultCount += 1;
                if (resultCount > 1) {
                    throw new InconsistentDatabaseException("Multiple bank admins for bank " + bankId);
                }
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                if (userType != UserType.BANK_ADMIN) {
                    throw new RuntimeException("This should not occur.");
                }
                bankAdmin = new BankAdmin();
                bankAdmin.populateFieldsFromResultSetRow(resultSet);
            }
            return bankAdmin;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public Bank getBankById(String bankId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getBankByIdSQL);
            preparedStatement.setString(1, bankId);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            Bank bank = null;
            while (resultSet.next()) {
                numberOfRows++;
                bank = new Bank();
                bank.populateFieldsFromResultSetRow(resultSet);
            }
            if (numberOfRows < 1) {
                return null; // No bank found
            } else if (numberOfRows > 1) {
                throw new InconsistentDatabaseException("Multiple rows for bank '" + bankId + "'.");
            } else {
                return bank;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public School getSchoolById(String schoolId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getSchoolByIdSQL);
            preparedStatement.setString(1, schoolId);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            School school = null;
            while (resultSet.next()) {
                numberOfRows++;
                school = new School();
                school.populateFieldsFromResultSetRow(resultSet);
            }
            if (numberOfRows < 1) {
                return null; // No school found
            } else if (numberOfRows > 1) {
                throw new InconsistentDatabaseException("Multiple rows for school '" + schoolId + "'.");
            } else {
                return school;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public List<School> getAllSchools() throws SQLException {
        List<School> schools = new ArrayList<School>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllSchoolsSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                School school = new School();
                school.populateFieldsFromResultSetRow(resultSet);
                schools.add(school);
            }
            return schools;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public List<Bank> getAllBanks() throws SQLException {
        List<Bank> banks = new ArrayList<Bank>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllBanksSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Bank bank = new Bank();
                bank.populateFieldsFromResultSetRow(resultSet);
                banks.add(bank);
            }
            return banks;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }



    @Override
    public List<Date> getAllowedDates() throws SQLException {
        List<Date> dates = new ArrayList<Date>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllowedDatesSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Date date = resultSet.getDate("event_date");
                dates.add(new PrettyPrintingDate(date));
            }
            return dates;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        List<String> times = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllowedTimesSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String time = resultSet.getString("event_time");
                times.add(time);
            }
            return times;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    		
	private User insertNewUser(String hashedPassword, String salt, String email,
			String firstName, String lastName, UserType userType,
			String organizationId, String phoneNumber)
        throws SQLException, EmailAlreadyInUseException
    {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = ConnectionFactory.getConnection();
			preparedStatement = connection.prepareStatement(insertUserSQL);

			preparedStatement.setString(1, salt);
            preparedStatement.setString(2, hashedPassword);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, firstName);
			preparedStatement.setString(5, lastName);
			preparedStatement.setString(6, userType.getDBValue());
			preparedStatement.setString(7, organizationId);
			preparedStatement.setString(8, phoneNumber);
			preparedStatement.setInt(9, 0); // FIXME: This represents
												// "not approved"
			try {
				preparedStatement.executeUpdate();
			} catch (MySQLIntegrityConstraintViolationException err) {
				// FIXME: Check if it matches
				// "Duplicate entry '.*' for key 'ix_email'"
				throw new EmailAlreadyInUseException();
			}
			preparedStatement.close();
			preparedStatement = connection.prepareStatement(getLastInsertIdSQL);
			resultSet = preparedStatement.executeQuery();
			String userId = null;
			int numberOfRows = 0;
			while (resultSet.next()) {
				numberOfRows++;
				userId = resultSet.getString(1);
			}
			if (numberOfRows < 1) {
				throw new RuntimeException("This should never happen.");
			} else if (numberOfRows > 1) {
				throw new RuntimeException("This should never happen.");
			} else {
				return getUserById(userId);
			}
		} finally {
			closeSafely(connection, preparedStatement, resultSet);
		}
	}
    
    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return (Teacher) insertNewUser(hashedPassword, salt, formData.getEmail(),
                formData.getFirstName(), formData.getLastName(), UserType.TEACHER, formData.getSchoolId(),
                formData.getPhoneNumber());
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt)
            throws SQLException, NoSuchBankException, EmailAlreadyInUseException
    {
        return (Volunteer) insertNewUser(hashedPassword, salt, formData.getEmail(),
                formData.getFirstName(), formData.getLastName(), UserType.VOLUNTEER, formData.getBankId(),
                formData.getPhoneNumber());
    }

    
    /**
     * Safely close several things that might be open or not or might not even exist.
     */
    private void closeSafely(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (Exception err) {
            // ignore problems closing
        }
        try {
            preparedStatement.close();
        } catch (Exception err) {
            // ignore problems closing
        }
        try {
            connection.close();
        } catch (Exception err) {
            // ignore problems closing
        }
    }


	@Override
	public List<? super User> getUsersByType(String userAccessType) throws SQLException {
		return getUserByType(userAccessType, getUserByTypeSQL);
	}
	
	private List<? super User> getUserByType(String key, String sql) throws SQLException, InconsistentDatabaseException {
        Connection connection = null;
        List<? super User> usersList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            while (resultSet.next()) {
                numberOfRows++;
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                switch(userType) {
                    case VOLUNTEER: {
                        Volunteer volunteer = new Volunteer();
                        volunteer.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(volunteer);
                    } break;
                    case TEACHER: {
                        Teacher teacher = new Teacher();
                        teacher.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(teacher);
                    } break;
                    case BANK_ADMIN: {
                        BankAdmin bankAdmin = new BankAdmin();
                        bankAdmin.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(bankAdmin);
                    } break;
                    case SITE_ADMIN: {
                        SiteAdmin siteAdmin = new SiteAdmin();
                        siteAdmin.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(siteAdmin);
                    } break;
                    default: {
                        throw new RuntimeException("This should never occur.");
                    }
                }
             
            }
            if (numberOfRows < 1) {
                return null; // No user found
            } else {
                return usersList;
            } 
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


	@Override
	public boolean deleteSchool(String schoolId) throws SQLException,
			InconsistentDatabaseException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(deleteSchooldSQL);
            preparedStatement.setString(1, schoolId);
            int success =preparedStatement.executeUpdate();
            if (success == 0)
            	return false;
            else return true;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public School updateSchool(School school) throws SQLException,
			InconsistentDatabaseException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(updateSchoolByIdSQL);
            preparedStatement.setString(1, school.getName());
            preparedStatement.setString(2, school.getAddressLine1());
            preparedStatement.setString(3, school.getAddressLine2());
            preparedStatement.setString(4, school.getCity());
            preparedStatement.setString(5, school.getZip());
            preparedStatement.setString(6, school.getCounty());
            preparedStatement.setString(7, school.getSchoolDistrict());
            preparedStatement.setString(8, school.getState());
            preparedStatement.setString(9, school.getPhone());
            preparedStatement.setInt(10, school.isLmiEligible()==true?1:0);
            preparedStatement.setString(11, school.getSchoolId());
            preparedStatement.executeUpdate();
        } 
        finally {
            closeSafely(connection, preparedStatement, null);
            
        }
        return getSchoolById(school.getSchoolId());
	}


	@Override
	public void deleteBank(String bankId) throws SQLException, NoSuchBankException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionFactory.getConnection();

            // --- Delete all users ---
            preparedStatement = connection.prepareStatement(deleteUsersByBankId);
            preparedStatement.setString(1, bankId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                // It would have to have at LEAST the bank admin
                throw new NoSuchBankException();
            }
            preparedStatement.close();

            // --- Delete the bank ---
            preparedStatement = connection.prepareStatement(deleteBankSQL);
            preparedStatement.setString(1, bankId);
            rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new NoSuchBankException();
            }
        } finally {
            closeSafely(connection, preparedStatement, null);
        }
	}


	@Override
	public boolean deleteVolunteer(String volunteerId) throws SQLException,
			InconsistentDatabaseException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(deleteVolunteerSQL);
            preparedStatement.setString(1, volunteerId);
            int success =preparedStatement.executeUpdate();
            if (success == 0)
            	return false;
            else return true;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public User updateVolunteer(Volunteer volunteer) throws SQLException,
			EmailAlreadyInUseException {
		// TODO Auto-generated method stub
		EditPersonalDataFormData formData = new EditPersonalDataFormData();
		formData.setEmail(volunteer.getEmail());
		formData.setFirstName(volunteer.getFirstName());
		formData.setLastName(volunteer.getLastName());
		formData.setPhoneNumber(volunteer.getPhoneNumber());
		return modifyUserPersonalFields(volunteer.getUserId(), formData);
	}


	@Override
	public Event updateEvent(Event event) throws SQLException,
			InconsistentDatabaseException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(updateEventByIdSQL);
            preparedStatement.setString(1, event.getTeacherId());
            preparedStatement.setDate(2, (java.sql.Date) event.getEventDate());
            preparedStatement.setString(3, event.getEventTime());
            preparedStatement.setString(4, event.getGrade());
            preparedStatement.setInt(5, event.getNumberStudents());
            preparedStatement.setString(6, event.getNotes());
            preparedStatement.setString(7, event.getVolunteerId());
            preparedStatement.setString(8, event.getEventId());
            preparedStatement.executeUpdate();
        } 
        finally {
            closeSafely(connection, preparedStatement, null);
            
        }
        return getEventById(event.getEventId());
	}


	@Override
	public void deleteEvent(String eventId) throws SQLException, NoSuchEventException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(deleteEventSQL);
            preparedStatement.setString(1, eventId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new NoSuchEventException();
            } else if (rowsAffected > 1) {
                throw new RuntimeException("Cannot happen: eventId is the primary key!");
            } else {
                return;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public List<Event> getEvents() throws SQLException,
			InconsistentDatabaseException {
		List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getEventsSQL);
            
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event();
                event.setEventId(resultSet.getString("event_id"));
                event.setTeacherId(resultSet.getString("teacher_id"));
                event.setEventDate(resultSet.getDate("event_date"));
                event.setEventTime(resultSet.getString("event_time"));
                event.setGrade(resultSet.getString("grade"));
                event.setNumberStudents(resultSet.getInt("number_students"));
                event.setNotes(resultSet.getString("notes"));
                event.setVolunteerId(resultSet.getString("volunteer_id"));
                events.add(event);
            }
            return events;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public Event getEventById(String eventId) throws SQLException {
		Event event = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getEventByIdSQL);
            preparedStatement.setString(1, eventId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                event = new Event();
                event.setEventId(resultSet.getString("event_id"));
                event.setTeacherId(resultSet.getString("teacher_id"));
                event.setEventDate(resultSet.getDate("event_date"));
                event.setEventTime(resultSet.getString("event_time"));
                event.setGrade(resultSet.getString("grade"));
                event.setNumberStudents(resultSet.getInt("number_students"));
                event.setNotes(resultSet.getString("notes"));
                event.setVolunteerId(resultSet.getString("volunteer_id"));
               
            }
            return event;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();

            // --- Insert the bank ---
            preparedStatement = connection.prepareStatement(insertBankSQL);
            preparedStatement.setString(1, formData.getBankName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Should never happen: we inserted one row!");
            }
            preparedStatement.close();

            // --- Find out its bankId ---
            preparedStatement = connection.prepareStatement(getLastInsertIdSQL);
            resultSet = preparedStatement.executeQuery();
            String bankId = null;
            int numberOfRows = 0;
            while (resultSet.next()) {
                numberOfRows++;
                bankId = resultSet.getString(1);
            }
            if (numberOfRows < 1) {
                throw new RuntimeException("This should never happen.");
            } else if (numberOfRows > 1) {
                throw new RuntimeException("This should never happen.");
            }
            preparedStatement.close();

            // --- Insert the bank admin ---
            String salt = null;
            String hashedPassword = null;
            preparedStatement = connection.prepareStatement(insertUserSQL);
            preparedStatement.setString(1, salt);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, formData.getEmail());
            preparedStatement.setString(4, formData.getFirstName());
            preparedStatement.setString(5, formData.getLastName());
            preparedStatement.setString(6, UserType.BANK_ADMIN.getDBValue());
            preparedStatement.setString(7, bankId);
            preparedStatement.setString(8, formData.getPhoneNumber());
            preparedStatement.setInt(9, 0); // not meaningful
            affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Should never happen: we inserted one row!");
            }
            preparedStatement.close();

            // FIXME: Once we make the bank_admin column on Bank optional, the code can stop here.

            // --- Find out its userId ---
            preparedStatement = connection.prepareStatement(getLastInsertIdSQL);
            resultSet = preparedStatement.executeQuery();
            String bankAdminUserId = null;
            numberOfRows = 0;
            while (resultSet.next()) {
                numberOfRows++;
                bankAdminUserId = resultSet.getString(1);
            }
            if (numberOfRows < 1) {
                throw new RuntimeException("This should never happen.");
            } else if (numberOfRows > 1) {
                throw new RuntimeException("This should never happen.");
            }
            preparedStatement.close();

            // --- And update the Bank ---
            preparedStatement = connection.prepareStatement(modifyBankByIdSQL);
            preparedStatement.setString(1, formData.getBankName());
            preparedStatement.setString(2, bankAdminUserId);
            preparedStatement.setString(3, bankId);
            affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("How could the bank be deleted before we finish adding it?");
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


    @Override
    public void modifyBankAndBankAdmin(
            EditBankFormData formData
        ) throws SQLException, EmailAlreadyInUseException, NoSuchBankException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();

            // --- Check if Bank Admin Exists ---
            preparedStatement = connection.prepareStatement(getBankAdminByBankSQL);
            preparedStatement.setString(1, formData.getBankId());
            resultSet = preparedStatement.executeQuery();
            BankAdmin bankAdmin = null;
            int resultCount = 0;
            while (resultSet.next()) {
                resultCount += 1;
                if (resultCount > 1) {
                    throw new InconsistentDatabaseException("Multiple bank admins for bank " + formData.getBankId());
                }
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                if (userType != UserType.BANK_ADMIN) {
                    throw new RuntimeException("This should not occur.");
                }
                bankAdmin = new BankAdmin();
                bankAdmin.populateFieldsFromResultSetRow(resultSet);
            }
            String bankAdminId = bankAdmin == null ? null : bankAdmin.getUserId();

            if (bankAdminId == null) {
                // --- Insert new Bank Admin ---
                String salt = null;
                String hashedPassword = null;
                preparedStatement = connection.prepareStatement(insertUserSQL);
                preparedStatement.setString(1, salt);
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, formData.getEmail());
                preparedStatement.setString(4, formData.getFirstName());
                preparedStatement.setString(5, formData.getLastName());
                preparedStatement.setString(6, UserType.BANK_ADMIN.getDBValue());
                preparedStatement.setString(7, formData.getBankId());
                preparedStatement.setString(8, formData.getPhoneNumber());
                preparedStatement.setInt(9, 0); // not meaningful
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows != 1) {
                    throw new RuntimeException("Should never happen: we inserted one row!");
                }
                preparedStatement.close();

                // --- Find out its userId ---
                preparedStatement = connection.prepareStatement(getLastInsertIdSQL);
                resultSet = preparedStatement.executeQuery();
                int numberOfRows = 0;
                while (resultSet.next()) {
                    numberOfRows++;
                    bankAdminId = resultSet.getString(1);
                }
                if (numberOfRows < 1) {
                    throw new RuntimeException("This should never happen.");
                } else if (numberOfRows > 1) {
                    throw new RuntimeException("This should never happen.");
                }
                preparedStatement.close();

            } else {
                // --- Modify existing Bank Admin ---
                preparedStatement = connection.prepareStatement(modifyUserPersonalFieldsSQL);
                preparedStatement.setString(1, formData.getEmail());
                preparedStatement.setString(2, formData.getFirstName());
                preparedStatement.setString(3, formData.getLastName());
                preparedStatement.setString(4, formData.getPhoneNumber());
                preparedStatement.setString(5, formData.getBankId());
                try {
                    preparedStatement.executeUpdate();
                } catch(MySQLIntegrityConstraintViolationException err) {
                    if (err.getMessage().contains("Duplicate entry") && err.getMessage().contains("for key 'ix_email'")) {
                        throw new EmailAlreadyInUseException();
                    } else {
                        throw err;
                    }
                }
                preparedStatement.close();
            }

            // --- Modify Bank ---
            preparedStatement = connection.prepareStatement(modifyBankByIdSQL);
            preparedStatement.setString(1, formData.getBankName());
            preparedStatement.setString(2, bankAdminId);
            preparedStatement.setString(3, formData.getBankId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("How could the bank be deleted before we finish adding it?");
            }

        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
	public void insertSchool(School school) throws SQLException,
			InconsistentDatabaseException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(insertSchoolSQL);
            preparedStatement.setString(1, school.getName());
            preparedStatement.setString(2, school.getAddressLine1());
            preparedStatement.setString(3, school.getAddressLine2());
            preparedStatement.setString(4, school.getCity());
            preparedStatement.setString(5, school.getZip());
            preparedStatement.setString(6, school.getCounty());
            preparedStatement.setString(7, school.getSchoolDistrict());
            preparedStatement.setString(8, school.getState());
            preparedStatement.setString(9, school.getPhone());
            preparedStatement.setInt(10, 1);

            preparedStatement.execute();

        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public List<? super User> getAllUsers() throws SQLException,
			InconsistentDatabaseException {
		Connection connection = null;
        List<? super User> usersList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllUserSQL);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            while (resultSet.next()) {
                numberOfRows++;
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                switch(userType) {
                    case VOLUNTEER: {
                        Volunteer volunteer = new Volunteer();
                        volunteer.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(volunteer);
                    } break;
                    case TEACHER: {
                        Teacher teacher = new Teacher();
                        teacher.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(teacher);
                    } break;
                    case BANK_ADMIN: {
                        BankAdmin bankAdmin = new BankAdmin();
                        bankAdmin.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(bankAdmin);
                    } break;
                    case SITE_ADMIN: {
                        SiteAdmin siteAdmin = new SiteAdmin();
                        siteAdmin.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(siteAdmin);
                    } break;
                    default: {
                        throw new RuntimeException("This should never occur.");
                    }
                }
             
            }
            if (numberOfRows < 1) {
                return null; // No user found
            } else {
            	return usersList;
            } 
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}
	
	@Override
	public void updateUserCredential(String userId, String hashedPassword, String salt)
            throws SQLException, InconsistentDatabaseException
    {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(updateUserCredentialsByIdSQL);
            preparedStatement.setString(1, salt);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, userId);
            preparedStatement.executeUpdate();
        } 
        finally {
            closeSafely(connection, preparedStatement, null);
            
        }
        //return getEventById(event.getEventId());
	}


}