package com.tcts.formdata;

/**
 * The data fields needed to create a school.
 */
public class CreateSchoolFormData {
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
