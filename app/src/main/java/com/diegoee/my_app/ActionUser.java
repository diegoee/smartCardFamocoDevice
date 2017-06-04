package com.diegoee.my_app;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.diegoee.my_app.MainActivity.LOG_TAG;

public class ActionUser {

    private String uid;
    private boolean valOK;
    private String fechaFiscalizada;
    private String user;

    public ActionUser() {
        super();
        this.uid = "";
        this.user = "";
        this.valOK = false;

        Calendar date = Calendar.getInstance();
        date.add(Calendar.YEAR, 7);
        date.add(Calendar.MONTH, 5);
        date.add(Calendar.DATE, -12);
        date.add(Calendar.HOUR, -3);
        date.add(Calendar.MINUTE, -23);
        fechaFiscalizada = (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss")).format(date.getTime());
    }

    public String getId() {
        return uid;
    }

    public void setId(String uid) {
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

    public String getFechaFiscalizada() {
        return fechaFiscalizada;
    }
}
