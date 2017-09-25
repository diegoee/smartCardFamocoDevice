package com.diegoee.my_app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class LoginActivity extends Activity {

    // Set the duration of the activity_login screen
    private static final long SPLASH_SCREEN_DELAY = 50;

    private int progressStatus ;

    private List<String> user;
    private List<String> pass;

    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        progressStatus = 0;
        login ="none";

        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.progressBarGone).setVisibility(View.VISIBLE);

        readDataPass("data/dataLogin.json");

        //login ="testUser";
        //loginOk();

        Button btn = (Button) findViewById(R.id.btnLogin);
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                EditText editUser = (EditText) findViewById(R.id.editUser);
                EditText editPass = (EditText) findViewById(R.id.editPass);
                TextView textView1 = (TextView) findViewById(R.id.textResultLogin);

                for(int i=0;i<user.size();i++) {
                    if(editUser.getText().toString().equals(user.get(i))){
                        if(editPass.getText().toString().equals(pass.get(i))){
                            login = user.get(i);
                            textView1.setText("Login correcto: "+login);
                            loginOk();
                            break;
                        }
                    }
                    textView1.setText("Usuario y/o contraseña incorrectos\nProceda a realizar nuevamente login en la App.");
                }
            }
        };
        btn.setOnClickListener(listener);
    }

    public void readDataPass(String inFile) {
        String jsonString = "";

        TextView textView1 = (TextView) findViewById(R.id.textResultLogin);

        this.user = new ArrayList<String>();
        this.pass = new ArrayList<String>();

        try {
            InputStream stream = getResources().getAssets().open(inFile);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            jsonString = new String(buffer);

            try {
                int len = (new JSONObject(jsonString)).optJSONArray("logins").length();
                for (int i=0; i<len; i++){
                    this.user.add((new JSONObject(jsonString)).optJSONArray("logins").getJSONObject(i).getString("user"));
                    this.pass.add((new JSONObject(jsonString)).optJSONArray("logins").getJSONObject(i).getString("pass"));
                }
                textView1.setText("Fichero de contraseñas leido correctamente.\nProceda a realizar login en la App.");
            } catch (final JSONException e) {
                textView1.setText("Error al al interpretar el archivo JSON de contraseñas.\nERROR:\n" + e.getMessage());
            }


        } catch (IOException e) {
            textView1.setText("Error al leer el archivo de contraseñas almacenadas.\nERROR:\n" + e.getMessage());
        }
    }

    public void loginOk() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBarGone).setVisibility(View.GONE);

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus = progressStatus + (int) (Math.random()*5+1);
                    ((ProgressBar) findViewById(R.id.progressBar)).setProgress(progressStatus);
                    try {
                        Thread.sleep(SPLASH_SCREEN_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Log.v(LOG_TAG,"Progress =" + String.format("%d",progressStatus));
                }
                Intent mainIntent = new Intent().setClass(LoginActivity.this, MainActivity.class);
                mainIntent.putExtra("login", login);
                startActivity(mainIntent);

                //Log.v(MainActivity.LOG_TAG,"finish Splash");
                progressStatus = 0;
                finish();
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressStatus = 0;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Disable back button..............
        return false;
    }

}