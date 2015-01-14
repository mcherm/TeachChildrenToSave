package com.tcts.formdata;

import com.tcts.database.DatabaseFacade;


/**
 * This is a parent that will be inherited by all FormData classes that wish to implement
 * validation. The advantage of moving the validation into the FormData object is that
 * if the same information is collected in multiple places (like a create and an edit)
 * they can share the same validation logic, making the validations consistent across
 * the site.
 */
public abstract class ValidatedFormData<E extends Throwable> {

    protected static DatabaseFacade database;

    /**
     * This static method is used to inject the database. It is explicitly invoked from
     * the Spring Bean context. The field is static so that all of the instances
     * can access the database if they need it, effectively sharing the same link. This
     * method exists to populate the static field because that is the intended way in
     * Spring to populate a static field with a bean. @Autowired will not work on a
     * static field (or even a normal field of an object not created by Spring itself).
     *
     * @param theDatabase the database bean to use
     */
    protected static void setDatabase(DatabaseFacade theDatabase) {
        database = theDatabase;
    }


    /**
     * Controllers call this to perform validation. It will return an Errors object
     * with the errors found (if any).
     */
    public Errors validate() throws E {
        Errors errors = new Errors();
        validationRules(errors);
        return errors;
    }

    /**
     * Subclasses will implement this to perform the actual validation.
     * If an error is found, they should call "addError" on the errors
     * object. Multiple errors can be added and all will be displayed.
     */
    protected abstract void validationRules(Errors errors) throws E;
}
