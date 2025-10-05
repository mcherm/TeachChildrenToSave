package com.tcts.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.tcts.common.CachedList;
import com.tcts.common.CachedValue;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.ApprovalStatus;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Document;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteStatistics;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.UserType;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AllowedValueAlreadyInUseException;
import com.tcts.exception.BankHasVolunteersException;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.EventAlreadyHasAVolunteerException;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchAllowedValueException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.exception.NoSuchUserException;
import com.tcts.exception.TeacherHasEventsException;
import com.tcts.exception.VolunteerHasEventsException;
import com.tcts.formdata.AddAllowedDateFormData;
import com.tcts.formdata.CreateBankFormData;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditBankFormData;
import com.tcts.formdata.EditEventFormData;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.formdata.EditSchoolFormData;
import com.tcts.formdata.EditVolunteerPersonalDataFormData;
import com.tcts.formdata.EventRegistrationFormData;
import com.tcts.formdata.NewBankAdminFormData;
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;


/**
 * This is a database implementation which is intended to wrap a real database
 * implementation. It caches some of the most frequent calls, invalidating the
 * caches when the data has become invalid.
 */
public class CachingDatabase implements DatabaseFacade {

    private final DatabaseFacade database;

    /** Refresh values every 1 hour even if we think they're still accurate. */
    private static final long REFRESH_IN_MILLIS = 60 * 60 * 1000;

    private final CachedValue<List<PrettyPrintingDate>,RuntimeException> allowedDates =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public List<PrettyPrintingDate> generateValue() {
                    return Collections.unmodifiableList(database.getAllowedDates());
                }
            };

    private final CachedValue<List<String>,RuntimeException> allowedTimes =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public List<String> generateValue() {
                    return Collections.unmodifiableList(database.getAllowedTimes());
                }
            };

    private final CachedValue<List<String>,RuntimeException> allowedGrades =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public List<String> generateValue() {
                    return Collections.unmodifiableList(database.getAllowedGrades());
                }
            };

    private final CachedValue<List<String>,RuntimeException> allowedDeliveryMethods =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public List<String> generateValue() {
                    return Collections.unmodifiableList(database.getAllowedDeliveryMethods());
                }
            };

    private final CachedValue<List<Bank>,RuntimeException> allBanks =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public List<Bank> generateValue() throws InconsistentDatabaseException {
                    return Collections.unmodifiableList(database.getAllBanks());
                }
            };

    private final CachedValue<List<School>,RuntimeException> allSchools =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public List<School> generateValue() throws InconsistentDatabaseException {
                    return Collections.unmodifiableList(database.getAllSchools());
                }
            };

    private final CachedList<Event, RuntimeException> availableEvents =
            new CachedList<>(REFRESH_IN_MILLIS) {
                @Override
                public List<Event> generateValue() throws InconsistentDatabaseException {
                    return database.getAllAvailableEvents();
                }
            };

    private final CachedValue<Map<String,String>, RuntimeException> siteSettings =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public Map<String, String> generateValue() {
                    return database.getSiteSettings();
                }
            };
            

    /** Constructor requires you to provide an actual database. */
    public CachingDatabase(DatabaseFacade database) {
        this.database = database;
    }


    @Override
    public int getFieldLength(DatabaseField field) {
        return database.getFieldLength(field);
    }

    @Override
    public User getUserById(String userId) {
        return database.getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) throws InconsistentDatabaseException {
        return database.getUserByEmail(email);
    }

    @Override
    public List<Event> getEventsByTeacher(String teacherId ) throws InconsistentDatabaseException {
        return database.getEventsByTeacher(teacherId);
    }

    @Override
    public List<Event> getAllAvailableEvents() throws InconsistentDatabaseException {
        return availableEvents.getCachedValue();
    }

    @Override
    public List<Event> getEventsByVolunteer(String volunteerId ) throws InconsistentDatabaseException {
        return database.getEventsByVolunteer(volunteerId);
    }

    @Override
    public List<Event> getEventsByVolunteerWithTeacherAndSchool (String volunteerId ) throws InconsistentDatabaseException {
        return database.getEventsByVolunteerWithTeacherAndSchool(volunteerId);
    }

    @Override
    public List<Volunteer> getVolunteersByBank(String bankId ) throws InconsistentDatabaseException {
        return database.getVolunteersByBank(bankId);
    }

    @Override
    public List<BankAdmin> getBankAdminsByBank(String bankId ) throws InconsistentDatabaseException {
        return database.getBankAdminsByBank(bankId);
    }

    @Override
    public Bank getBankById(String bankId ) throws InconsistentDatabaseException {
        return database.getBankById(bankId);
    }

    @Override
    public School getSchoolById(String schoolId ) throws InconsistentDatabaseException {
        return database.getSchoolById(schoolId);
    }

    @Override
    public List<School> getAllSchools() throws InconsistentDatabaseException {
        return allSchools.getCachedValue();
    }

    @Override
    public List<Bank> getAllBanks() throws InconsistentDatabaseException {
        return allBanks.getCachedValue();
    }

    @Override
    public List<User> getAllUsers() throws InconsistentDatabaseException {
        return database.getAllUsers();
    }

    @Override
    public List<PrettyPrintingDate> getAllowedDates() {
        return allowedDates.getCachedValue();
    }

    @Override
    public List<String> getAllowedTimes() {
        return allowedTimes.getCachedValue();
    }

    @Override
    public List<String> getAllowedGrades() {
        return allowedGrades.getCachedValue();
    }

    @Override
    public List<String> getAllowedDeliveryMethods() {
        return allowedDeliveryMethods.getCachedValue();
    }

    @Override
    public List<BankAdmin> getBankAdmins() throws InconsistentDatabaseException {
        return database.getBankAdmins();
    }

    @Override
    public List<Event> getAllEvents() throws InconsistentDatabaseException {
        return database.getAllEvents();
    }

    @Override
    public Event getEventById(String eventId ) throws InconsistentDatabaseException {
        return database.getEventById(eventId);
    }

    @Override
    public SiteStatistics getSiteStatistics() throws InconsistentDatabaseException {
        return database.getSiteStatistics();
    }

    @Override
    public void modifyUserPersonalFields(EditPersonalDataFormData formData) throws EmailAlreadyInUseException, InconsistentDatabaseException {
        database.modifyUserPersonalFields(formData);
        availableEvents.refreshNow();
    }

    @Override
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData) throws EmailAlreadyInUseException, InconsistentDatabaseException {
        database.modifyVolunteerPersonalFields(formData);
        availableEvents.refreshNow();
    }

    @Override
    public void modifyTeacherSchool(String userId, String schoolId) throws NoSuchSchoolException, NoSuchUserException
    {
       database.modifyTeacherSchool(userId, schoolId);
       allSchools.refreshNow();
    }


    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt)
            throws NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException, InconsistentDatabaseException
    {
        final Teacher result = database.insertNewTeacher(formData, hashedPassword, salt);
        allSchools.refreshNow();
        return result;
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData ) {
        database.insertEvent(teacherId, formData);
        availableEvents.refreshNow();
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt)
            throws NoSuchBankException, EmailAlreadyInUseException, InconsistentDatabaseException
    {
        final Volunteer result = database.insertNewVolunteer(formData, hashedPassword, salt);
        allBanks.refreshNow();
        return result;
    }

    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws AllowedValueAlreadyInUseException {
        database.insertNewAllowedDate(formData);
        allowedDates.refreshNow();
    }

    @Override
    public void insertNewAllowedTime(String newAllowedTime, String timeToInsertBefore)
            throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException
    {
        database.insertNewAllowedTime(newAllowedTime, timeToInsertBefore);
        allowedTimes.refreshNow();
    }

    @Override
    public void insertNewAllowedGrade(String newAllowedGrade, String gradeToInsertBefore) throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        database.insertNewAllowedGrade(newAllowedGrade, gradeToInsertBefore);
        allowedGrades.refreshNow();
    }

    @Override
    public void insertNewAllowedDeliveryMethod(String newAllowedDeliveryMethod, String deliveryMethodToInsertBefore) throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        database.insertNewAllowedDeliveryMethod(newAllowedDeliveryMethod, deliveryMethodToInsertBefore);
        allowedDeliveryMethods.refreshNow();
    }

    @Override
    public void volunteerForEvent(final String eventId, String volunteerId)
            throws NoSuchEventException, EventAlreadyHasAVolunteerException
    {
        database.volunteerForEvent(eventId, volunteerId);
        availableEvents.refreshNow();
    }

    @Override
    public void deleteSchool(String schoolId) throws NoSuchSchoolException {
        database.deleteSchool(schoolId);
        allSchools.refreshNow();
        availableEvents.refreshNow();
    }

    @Override
    public void deleteBankAndBankVolunteers(String bankId)
            throws NoSuchBankException, BankHasVolunteersException, VolunteerHasEventsException, InconsistentDatabaseException
    {
        database.deleteBankAndBankVolunteers(bankId);
        allBanks.refreshNow();
        availableEvents.refreshNow();
    }

    @Override
    public void deleteVolunteer(String volunteerId) throws NoSuchUserException, VolunteerHasEventsException {
        database.deleteVolunteer(volunteerId);
        allBanks.refreshNow();
        availableEvents.refreshNow();
    }

    @Override
    public void deleteTeacher(String teacherId)
            throws NoSuchUserException, TeacherHasEventsException, InconsistentDatabaseException
    {
        database.deleteTeacher(teacherId);
        allSchools.refreshNow();
        availableEvents.refreshNow();
    }

    @Override
    public void deleteEvent(String eventId) throws NoSuchEventException {
        database.deleteEvent(eventId);
        availableEvents.refreshNow();
    }

    @Override
    public void modifySchool(EditSchoolFormData school) throws NoSuchSchoolException {
        database.modifySchool(school);
        availableEvents.refreshNow();
        allSchools.refreshNow();
    }

    @Override
    public void insertNewBankAndAdmin(CreateBankFormData formData) throws EmailAlreadyInUseException, InconsistentDatabaseException {
        database.insertNewBankAndAdmin(formData);
        allBanks.refreshNow();
    }

    @Override
    public void insertNewBankAdmin(NewBankAdminFormData formData)
            throws EmailAlreadyInUseException, InconsistentDatabaseException
    {
        database.insertNewBankAdmin(formData);
        allBanks.refreshNow();
    }

    @Override
    public void modifyBank(EditBankFormData formData) throws NoSuchBankException {
        database.modifyBank(formData);
        allBanks.refreshNow();
    }

    @Override
    public void setUserType(String userId, UserType userType ) {
        database.setUserType(userId, userType);
        allBanks.refreshNow();
    }

    @Override
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData) throws NoSuchBankException {
        database.setBankSpecificFieldLabel(formData);
        allBanks.refreshNow();
    }

    @Override
    public void insertNewSchool(CreateSchoolFormData school ) {
        database.insertNewSchool(school);
        allSchools.refreshNow();
    }

    @Override
    public void modifyEventRegistration(EventRegistrationFormData formData) throws NoSuchEventException {
        database.modifyEventRegistration(formData);
        availableEvents.refreshNow();
    }

    @Override
    public void modifyEvent(EditEventFormData formData) throws NoSuchEventException {
        database.modifyEvent(formData);
        availableEvents.refreshNow();
    }

    @Override
    public void updateUserCredential(String userId, String hashedPassword, String salt ) {
        database.updateUserCredential(userId, hashedPassword, salt);
    }

    @Override
    public void updateResetPasswordToken(String userId, String resetPasswordToken ) {
        database.updateResetPasswordToken(userId, resetPasswordToken);
    }

    @Override
    public void updateApprovalStatusById(String volunteerId, ApprovalStatus approvalStatus ) {
        database.updateApprovalStatusById(volunteerId, approvalStatus);
    }

    @Override
    public void deleteAllowedTime(String time) throws NoSuchAllowedValueException {
        database.deleteAllowedTime(time);
        allowedTimes.refreshNow();
    }

    @Override
    public void deleteAllowedGrade(String grade) throws NoSuchAllowedValueException {
        database.deleteAllowedGrade(grade);
        allowedGrades.refreshNow();
    }

    @Override
    public void deleteAllowedDeliveryMethod(String deliveryMethod) throws NoSuchAllowedValueException {
        database.deleteAllowedDeliveryMethod(deliveryMethod);
        allowedDeliveryMethods.refreshNow();
    }

    @Override
    public void deleteAllowedDate(PrettyPrintingDate date) throws NoSuchAllowedValueException {
        database.deleteAllowedDate(date);
        allowedDates.refreshNow();
    }

    @Override
    public List<Volunteer> getVolunteersWithBankData() throws InconsistentDatabaseException {
        return database.getVolunteersWithBankData();
    }
    
    @Override
    public List<Teacher> getTeachersWithSchoolData() throws InconsistentDatabaseException {
        return database.getTeachersWithSchoolData();
    }

    @Override
    public List<Teacher> getTeachersBySchool(String schoolId) throws InconsistentDatabaseException {
        return database.getTeachersBySchool(schoolId);
    }

    @Override
    public List<Teacher> getMatchedTeachers() throws InconsistentDatabaseException {
       return database.getMatchedTeachers();
    }
    
    @Override
    public List<Teacher> getUnMatchedTeachers() throws InconsistentDatabaseException {
    	return database.getUnMatchedTeachers();
    }
    
    @Override
    public List<Volunteer> getMatchedVolunteers() throws InconsistentDatabaseException {
    	return database.getMatchedVolunteers();
    }
    
    @Override
    public List<Volunteer> getUnMatchedVolunteers() throws InconsistentDatabaseException {
    	return database.getUnMatchedVolunteers();
    }

    @Override
    public Map<String, String> getSiteSettings() {
        return siteSettings.getCachedValue();
    }

    @Override
    public void modifySiteSetting(String settingName, String settingValue) {
        database.modifySiteSetting(settingName, settingValue);
        siteSettings.refreshNow();
    }

    @Override
    public SortedSet<Document> getDocuments() {
        return database.getDocuments();
    }

    @Override
    public void createOrModifyDocument (Document document ) {
        database.createOrModifyDocument(document);
    }

    @Override
    public void deleteDocument(String documentName ) {
        database.deleteDocument(documentName);
    }

}
