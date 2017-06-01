package com.diegoee.my_app;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.diegoee.my_app.MainActivity.LOG_TAG;

/**
 * Created by Diego on 24/5/17.
 */

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
        fechaFiscalizada = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        //Log.v(LOG_TAG,fechaFiscalizada);
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
