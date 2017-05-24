package com.diegoee.my_app;

/**
 * Created by Diego on 24/5/17.
 */

public class ActionUser {

    private String id;
    private boolean validationOK;

    public ActionUser() {
        super();
        this.id = "";
        this.validationOK = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isValidationOK() {
        return validationOK;
    }

    public void setValidationOK(boolean validationOK) {
        this.validationOK = validationOK;
    }
}
