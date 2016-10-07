package com.diegoee.my_app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private View myView;
    private String console;
    private TdmCard tdmcard;
    private int load;

    public void setConsole(String console){
        this.console=console;
    }
    public void setTdmCard(TdmCard tdmcard){
        this.tdmcard=tdmcard;
    }
    public void setLoad(int load){
        this.load=load;
    }

    public String getConsole(){
        return console;
    }
    public TdmCard getTdmCard(){
        return tdmcard;
    }
    public int getLoad(){
        return load;
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
            result= console+"\n"+tdmcard.getMainData();
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
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
