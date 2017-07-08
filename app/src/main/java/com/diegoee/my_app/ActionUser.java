package com.diegoee.my_app;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActionUser {

    private String uid;
    private boolean valOK;
    private String fechaFiscalizada;
    private String fechaFiscalizadaSeg;
    private String user;

    public ActionUser() {
        super();
        this.uid = "";
        this.user = "";
        this.valOK = false;
        Calendar date = Calendar.getInstance();
        fechaFiscalizada = (new SimpleDateFormat("yyyy/MM/dd-HH:mm")).format(date.getTime());
        fechaFiscalizadaSeg = (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss")).format(date.getTime());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isValOK() {
        return valOK;
    }

    public void setValOK(boolean validationOK) {
        this.valOK = validationOK;
    }

    public String getFechaFiscalizadaSeg() {
        return fechaFiscalizadaSeg;
    }

    public String getFechaFiscalizada() {
        return fechaFiscalizada;
    }
}
