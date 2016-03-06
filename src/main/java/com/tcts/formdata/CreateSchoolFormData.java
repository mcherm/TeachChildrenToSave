package com.tcts.formdata;

import com.tcts.database.DatabaseField;

/**
 * The data fields needed to create a school.
 */
public class CreateSchoolFormData extends ValidatedFormData<RuntimeException> {
    private String schoolName;
    private String schoolAddress1;
    private String city;
    private String zip;
    private String county;
    private String district;
    private String state;
    private String phone;
    private String lmiEligible;
    private String SLC;

    /** Just a utility to quickly test a string. */
    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    protected void validationRules(Errors errors) {
        if (isEmpty(schoolName)) {
            errors.addError("You need to enter a name for the school.");
        }
        if (isEmpty(schoolAddress1)) {
            errors.addError("Enter the adddress line for the school.");
        }
        if (isEmpty(city)) {
            errors.addError("Specify the City for the school's address.");
        }
        if (isEmpty(zip)) {
            errors.addError("Specify a zip code for the school.");
        }
        if (isEmpty(state)) {
            errors.addError("Enter the state for the address.");
        } else {
            if (state.length() != 2 || !Character.isLetter(state.charAt(0)) || !Character.isLetter(state.charAt(1))) {
                errors.addError("Please enter a valid state (e.g. \"DE\")");
            }
        }
        if (isEmpty(SLC)) {
            errors.addError("Enter the valid SLC or \"N/A\" if the school does not have an SLC.");
        }
		validateLength(schoolName, DatabaseField.school_name, errors);
		validateLength(schoolAddress1, DatabaseField.school_addr1, errors);
		validateLength(city, DatabaseField.school_city, errors);
		validateLength(zip, DatabaseField.school_zip, errors);
		validateLength(county, DatabaseField.school_county, errors);
		validateLength(district, DatabaseField.school_district, errors);
		validateLength(state, DatabaseField.school_state, errors);
		validateLength(phone, DatabaseField.school_phone, errors);
		validateLength(SLC, DatabaseField.school_slc, errors);
    }

    public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getSchoolAddress1() {
		return schoolAddress1;
	}
	public void setSchoolAddress1(String schoolAddress1) {
		this.schoolAddress1 = schoolAddress1;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getLmiEligible() {
		return lmiEligible;
	}
	public void setLmiEligible(String lmiEligible) {
		this.lmiEligible = lmiEligible;
	}
    public String getSLC() {
        return SLC;
    }
    public void setSLC(String SLC) {
        this.SLC = SLC;
    }
}
