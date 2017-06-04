package com.diegoee.my_app;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import static com.diegoee.my_app.MainActivity.LOG_TAG;


/**
 * Created by Diego on 2/7/16.
 */
public class MainFragment extends Fragment {

    public static int MAIN_TEXT=1;
    public static int MAIN_BTN=2;
    public static int DETAIL_MOV=4;
    public static int DETAIL_CARD=5;
    public static int ACTION_USER=6;

    private String text;
    private int load;

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setLoad(int load){
        this.load=load;
    }

    private InterfaceFragmentActivity listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Nos aseguramos de que la actividad contenedora haya implementado la
        // interfaz de retrollamada. Si no, lanzamos una excepción
        try {
            listener = (InterfaceFragmentActivity) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " debe implementar InterfaceFragmentActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.main_fragment, container, false);


        WebView webView = (WebView) myView.findViewById(R.id.webView);
        webView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String tittle="";

        myView.findViewById(R.id.buttonOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null) {
                    listener.writeActionUser(true);
                }
            }
        });
        myView.findViewById(R.id.buttonNOOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null) {
                    listener.writeActionUser(false);
                }
            }
        });

        if (load==MainFragment.MAIN_TEXT){
            myView.findViewById(R.id.webView).setVisibility(View.GONE);
            myView.findViewById(R.id.buttonsView).setVisibility(View.GONE);
            myView.findViewById(R.id.textView).setVisibility(View.VISIBLE);

            tittle=getText(R.string.menu_main).toString();
        }
        if (load==MainFragment.MAIN_BTN){
            myView.findViewById(R.id.webView).setVisibility(View.VISIBLE);
            myView.findViewById(R.id.buttonsView).setVisibility(View.VISIBLE);
            myView.findViewById(R.id.textView).setVisibility(View.GONE);

            tittle=getText(R.string.menu_main).toString();
            webView.loadUrl("file:///android_asset/webMainScreen/index.html?"+text);
        }
        if (load==MainFragment.DETAIL_MOV){
            myView.findViewById(R.id.webView).setVisibility(View.GONE);
            myView.findViewById(R.id.buttonsView).setVisibility(View.GONE);
            myView.findViewById(R.id.textView).setVisibility(View.VISIBLE);

            tittle=getText(R.string.menu_mov).toString();
        }
        if (load==MainFragment.DETAIL_CARD){
            myView.findViewById(R.id.webView).setVisibility(View.GONE);
            myView.findViewById(R.id.buttonsView).setVisibility(View.GONE);
            myView.findViewById(R.id.textView).setVisibility(View.VISIBLE);

            tittle=getText(R.string.menu_card).toString();
        }
        if (load==MainFragment.ACTION_USER){

            myView.findViewById(R.id.webView).setVisibility(View.VISIBLE);
            myView.findViewById(R.id.buttonsView).setVisibility(View.GONE);
            myView.findViewById(R.id.textView).setVisibility(View.GONE);

            tittle=getText(R.string.menu_user).toString();

            webView.loadUrl("file:///android_asset/webUser/index.html?obj="+text);
        }

        Log.v(LOG_TAG,text);

        ((TextView) myView.findViewById(R.id.tittle)).setText(tittle);
        ((TextView) myView.findViewById(R.id.result)).setText(text);

        return myView;
    }

}
