package com.diegoee.my_app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * Created by Diego on 2/7/16.
 */
public class MainFragment extends Fragment {

    public static int MAIN=1;
    public static int DETAIL_MOV=2;
    public static int DETAIL_CARD=3;
    public static int DETAIL_CTRL=4;
    public static int CONTACT=5;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.main_fragment, container, false);

        String tittle="";

        WebView webView = (WebView) myView.findViewById(R.id.webView);
        webView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        if (load==MainFragment.MAIN){
            myView.findViewById(R.id.webView).setVisibility(View.GONE);
            myView.findViewById(R.id.buttonsView).setVisibility(View.VISIBLE);
            myView.findViewById(R.id.textView).setVisibility(View.VISIBLE);

            tittle=getText(R.string.menu_main).toString();
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

            webView.loadUrl("file:///android_asset/webUser/index.html?var="+text);
            //{data:[{id:BDAAA4E6, validation: n},{id:BDAAA4E6, validation: n},]}
            Log.v(MainActivity.LOG_TAG,text);
        }

        ((TextView) myView.findViewById(R.id.tittle)).setText(tittle);
        ((TextView) myView.findViewById(R.id.result)).setText(text);

        return myView;
    }



}
