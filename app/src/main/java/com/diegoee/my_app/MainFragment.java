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
    public static int DETAIL_CTRL=4;
    public static int CONTACT=5;

    View myView;
    String console;
    TdmCard tdmcard;
    int load;

    public MainFragment() {
        super();
        this.console="";
        this.myView = null;
        this.tdmcard = new TdmCard();
        this.load = MainFragment.MAIN;
    }

    public MainFragment(String console,TdmCard tdmcard, int load) {
        super();
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

        TextView textView1 = (TextView) myView.findViewById(R.id.tittle);
        TextView textView2 = (TextView) myView.findViewById(R.id.result);

        if (load==MainFragment.MAIN){
            tittle=getText(R.string.menu_main).toString();
            result= tdmcard.getMainData()+"\n"+console;
            //textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
        if (load==MainFragment.DETAIL_MOV){
            tittle=getText(R.string.menu_mov).toString();
            result= tdmcard.getMovData();
        }
        if (load==MainFragment.DETAIL_CARD){
            tittle=getText(R.string.menu_card).toString();
            result= tdmcard.getCardData();
        }
        if (load==MainFragment.DETAIL_CTRL){
            tittle=getText(R.string.menu_ctrl).toString();
            result= tdmcard.getCtrlData();
        }
        if (load==MainFragment.CONTACT){
            tittle=getText(R.string.menu_cont).toString();
            result = getText(R.string.contact).toString();
        }

        textView1.setText(tittle);
        textView2.setText(result);

        return myView;
    }



}
