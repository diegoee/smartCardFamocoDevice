package com.diegoee.my_app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego on 2/7/16.
 */
public class MainFragment extends Fragment {

    public static int MAIN=1;
    public static int DETAIL_MOV=2;
    public static int DETAIL_CARD=3;
    public static int CONTACT=4;

    View myView;
    String console;
    TdmCard tdmcard;
    int load;

    public MainFragment() {
        this.console="";
        this.myView = null;
        this.tdmcard = new TdmCard();
        this.load = MainFragment.MAIN;
    }

    public MainFragment(String console,TdmCard tdmcard, int load) {
        this.console=console;
        this.myView = null;
        this.tdmcard=tdmcard;
        this.load=load;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView=inflater.inflate(R.layout.main_fragment, container, false);

        String tittle="";
        String result="";

        if (load==MainFragment.MAIN){
            tittle="Principal";
            result= tdmcard.getMainData();
        }
        if (load==MainFragment.DETAIL_MOV){
            tittle="Detalle de Movimientos";
            result= tdmcard.getMovData();
        }
        if (load==MainFragment.DETAIL_CARD){
            tittle="Detalle de Tarjeta";
            result= tdmcard.getCardData();

        }
        if (load==MainFragment.CONTACT){
            tittle="Información de contacto";
            result = getText(R.string.contact).toString();

        }
        TextView textView1 = (TextView) myView.findViewById(R.id.tittle);
        textView1.setText(tittle);


        TextView textView2 = (TextView) myView.findViewById(R.id.result);
        textView2.setText(result);

        TextView textView3 = (TextView) myView.findViewById(R.id.console);
        textView3.setText(console);

        return myView;
    }



}
