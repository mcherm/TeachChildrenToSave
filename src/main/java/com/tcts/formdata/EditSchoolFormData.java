package com.tcts.formdata;

/**
 * The data fields needed to edit a school.
 */
public class EditSchoolFormData extends CreateSchoolFormData {
    private String schoolId;

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

   
}
