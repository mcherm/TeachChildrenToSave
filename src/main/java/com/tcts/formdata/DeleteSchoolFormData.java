package com.tcts.formdata;

import com.tcts.datamodel.Teacher;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the form data for Delete School page.
 * <p>
 * It contains a list of teachers belonging to the school to be
 * deleted. In order to delete the school the user must choose whether
 * to delete the teacher or reassign the teacher to a new school.
 * This information is stored in the school ID attribute of each
 * teacher record.
 */
public class DeleteSchoolFormData {


    public String schoolIdToBeDeleted;
    public List <Teacher> teachers;

    public DeleteSchoolFormData()
    {
        schoolIdToBeDeleted = null;
        teachers = null;
    }

    public DeleteSchoolFormData(String schoolIdToBeDeleted, List<Teacher> teachers) {
        this.schoolIdToBeDeleted = schoolIdToBeDeleted;
        this.teachers = teachers;
    }

    public String getSchoolIdToBeDeleted() {
        return schoolIdToBeDeleted;
    }

    public void setSchoolIdToBeDeleted(String schoolIdToBeDeleted) {
        this.schoolIdToBeDeleted = schoolIdToBeDeleted;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

}
