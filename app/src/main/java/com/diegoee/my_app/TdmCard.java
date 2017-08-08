package com.diegoee.my_app;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static com.diegoee.my_app.MainActivity.LOG_TAG;

public class TdmCard {

    //Aux. class
    class Mov implements Comparable<Mov> {
        int pos;
        String titulo;
        String operacion;
        String fechaHora;
        int fechaHoraInt;
        String tramos;
        String viajeros;
        String viajeTransbordo;
        String ultimaLinea;
        String ultimoSentido;
        String parada;
        String autobusTranvia;
        String saldo;
        String operador;

        @Override
        public int compareTo(Mov o) {
            return fechaHoraInt < o.fechaHoraInt ? -1 : fechaHoraInt > o.fechaHoraInt ? 1 : 0;
        }
    }

    //Atributes
    private boolean isInfo;
    private ArrayList<byte[]> infoByte;

    List<Mov> movList;

    String uid;

    String ntarjeta;
    String propietario;
    String fechaEmision;
    String fechaCaducidad;

    String codigoTitulo;
    String tipo;
    String fechaInicioCaducidad;
    String saldo;

    private List<String> paradaId;
    private List<String> paradaCode;

    private List<String> codigoTituloId;
    private List<String> codigoTituloDesc;


    //Constructor;
    public TdmCard(Context ctx) {
        super();
        this.isInfo = false;
        infoByte = new ArrayList<byte[]>();
        movList = new ArrayList<Mov>();
        paradaId = new ArrayList<String>();
        paradaCode = new ArrayList<String>();
        codigoTituloId = new ArrayList<String>();
        codigoTituloDesc = new ArrayList<String>();
        readDataJSON("data/dataTrain.json",ctx);
    }

    public void eraseInfo(){
        this.isInfo = false;
        infoByte = new ArrayList<byte[]>();
        movList = new ArrayList<Mov>();
        uid = "";
        ntarjeta = "";
        propietario = "";
        fechaEmision = "";
        fechaCaducidad = "";
        codigoTitulo = "";
        tipo = "";
        fechaInicioCaducidad = "";
        saldo = "";
    }

    public boolean isInfo() {
        return isInfo;
    }

    public void append(byte[] data){
        this.infoByte.add(data);
        this.isInfo = true;
    }

    public static String bytesToHexString(byte[] bArray) {
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

    public static long hex2decimalLong(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        long val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

    public static String hex2Binary(String s) {
        String val="";
        String result="";

        //Log.v(MainActivity.LOG_TAG,s);

        for (int i=0;i<s.length();i++){
            val=String.format("%s",s.charAt(i));
            if (val.equals("0")) { val = "0000";  }
            if (val.equals("1")) { val = "0001";  }
            if (val.equals("2")) { val = "0010";  }
            if (val.equals("3")) { val = "0011";  }
            if (val.equals("4")) { val = "0100";  }
            if (val.equals("5")) { val = "0101";  }
            if (val.equals("6")) { val = "0110";  }
            if (val.equals("7")) { val = "0111";  }
            if (val.equals("8")) { val = "1000";  }
            if (val.equals("9")) { val = "1001";  }
            if (val.equals("A")) { val = "1010";  }
            if (val.equals("B")) { val = "1011";  }
            if (val.equals("C")) { val = "1100";  }
            if (val.equals("D")) { val = "1101";  }
            if (val.equals("E")) { val = "1110";  }
            if (val.equals("F")) { val = "1111";  }
            result = result+val;
        }

        //Log.v(MainActivity.LOG_TAG,result);

        return result;

    }

    public String getInfoHexByte(){
        String result = "";

        if (infoByte.size()==64) {
            result= "****** Datos Brutos en Hex. ****** \n";
            int i = 0;
            int j = 0;
            for (byte[] data : infoByte) {
                if (i%4==0){
                    result = result + "Sector_"+String.format("%d",j)+"\n";
                    j++;
                }
                result = result + bytesToHexString(data)+ "\n";
                i++;
            }
        }else{
            result = "";
        }
        return result;
    }

    public void readDataJSON(String inFile, Context ctx) {
        String jsonString = "";

        try {
            InputStream stream = ctx.getResources().getAssets().open(inFile);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            jsonString = new String(buffer);

            try {
                int len = (new JSONObject(jsonString)).optJSONArray("paradas").length();
                for (int i = 0; i < len; i++) {
                    paradaId.add((new JSONObject(jsonString)).optJSONArray("paradas").getJSONObject(i).getString("id"));
                    paradaCode.add((new JSONObject(jsonString)).optJSONArray("paradas").getJSONObject(i).getString("code"));
                }
            } catch (final JSONException e) {
                Log.v(LOG_TAG, e.toString());
            }

            try {
                int len = (new JSONObject(jsonString)).optJSONArray("codigosDeTitulos").length();
                for (int i = 0; i < len; i++) {
                    codigoTituloId.add((new JSONObject(jsonString)).optJSONArray("codigosDeTitulos").getJSONObject(i).getString("id"));
                    codigoTituloDesc.add((new JSONObject(jsonString)).optJSONArray("codigosDeTitulos").getJSONObject(i).getString("desc"));
                }
            } catch (final JSONException e) {
                Log.v(LOG_TAG, e.toString());
            }

        } catch (IOException e) {
            Log.v(LOG_TAG, e.toString());
        }
    }

    public void calData() {

        byte[] auxBytes;
        String auxString;
        int startTittle = 0;

        if (infoByte.size()==64) {

            //uid
            auxBytes = new byte[]{infoByte.get(0)[0], infoByte.get(0)[1], infoByte.get(0)[2], infoByte.get(0)[3]};
            uid = bytesToHexString(auxBytes);

            //ntarjeta
            auxBytes = new byte[]{infoByte.get(4)[0], infoByte.get(4)[1], infoByte.get(4)[2], infoByte.get(4)[3]};
            ntarjeta = String.format("%d",hex2decimalLong(bytesToHexString(auxBytes)));

            //propietario
            auxBytes = new byte[]{infoByte.get(4)[5]};
            auxString = String.format("%d",hex2decimal(bytesToHexString(auxBytes)));
            if (auxString.equals("1")){ auxString = "TM"; }
            if (auxString.equals("2")){ auxString = "TDM"; }
            if (auxString.equals("0")){ auxString = "TDM"; }
            if (auxString.equals("3")){ auxString = "LAT"; }
            propietario = auxString;

            //fechaEmision
            auxBytes = new byte[]{infoByte.get(4)[6], infoByte.get(4)[7]};

            Calendar c1 = GregorianCalendar.getInstance();
            c1.set(2000, Calendar.JANUARY, 1,0,0,0);
            SimpleDateFormat format1;
            c1.add(Calendar.DAY_OF_YEAR, hex2decimal(bytesToHexString(auxBytes)));
            format1 = new SimpleDateFormat("yyyy-MM-dd");
            auxString = format1.format(c1.getTime());
            fechaEmision = auxString;

            //fechaCaducidad
            auxBytes = new byte[]{infoByte.get(4)[8], infoByte.get(4)[9]};
            c1.set(2000, Calendar.JANUARY, 1,0,0,0);
            c1.add(Calendar.DAY_OF_YEAR, hex2decimal(bytesToHexString(auxBytes)));
            auxString = format1.format(c1.getTime());
            fechaCaducidad = auxString;

            startTittle = 8;
            //buscamos el inicio del Título
            if (Arrays.equals(infoByte.get(8), infoByte.get(9))) {
                startTittle = 8;
            }
            if (Arrays.equals(infoByte.get(9), infoByte.get(10))) {
                startTittle = 9;
            }
            if (Arrays.equals(infoByte.get(8), infoByte.get(10))) {
                startTittle = 8;
            }

            auxBytes = new byte[]{infoByte.get(startTittle)[3]};
            auxString = hex2Binary(bytesToHexString(auxBytes)).substring(4,6);
            auxString = String.format("%d",Integer.parseInt(auxString,2));
            auxBytes = new byte[]{infoByte.get(startTittle)[6],infoByte.get(startTittle)[7]};

            startTittle = 12;
            int aux =0;

            if (auxString.equals("0")) {startTittle = 12; aux=0;}
            if (auxString.equals("1")) {startTittle = 20; aux=3;}
            if (auxString.equals("2")) {startTittle = 28; aux=6;}
            if (auxString.equals("3")) {startTittle = 36; aux=9;}

            int auxSaldo = hex2decimal(hex2Binary(bytesToHexString(auxBytes)).substring(0+aux,1+aux));
            if (auxSaldo==0) {
                auxSaldo=0;
            }else{
                auxSaldo=1;
            }

            int auxDataTitulo = hex2decimal(hex2Binary(bytesToHexString(auxBytes)).substring(1+aux,2+aux));
            if (auxDataTitulo==0) {
                auxDataTitulo=2;
            }else{
                auxDataTitulo=3;
            }

            //codigoTitulo;
            auxBytes = new byte[]{infoByte.get(startTittle + auxDataTitulo)[0], infoByte.get(startTittle + auxDataTitulo)[1]};
            auxString = String.format("%d", ((int) hex2decimal(bytesToHexString(auxBytes))));
            if (codigoTituloId.indexOf(auxString)==-1){
                codigoTitulo = auxString;
            }else {
                codigoTitulo = codigoTituloDesc.get(codigoTituloId.indexOf(auxString));
            }

            //tipo;
            auxBytes = new byte[]{infoByte.get(startTittle + auxDataTitulo)[2]};
            auxString = String.format("%d", ((int) hex2decimal(bytesToHexString(auxBytes).substring(0,1))));

            if (auxString.equals("1")) {auxString="Viajes"; }
            if (auxString.equals("2")) {auxString="Tiempo"; }
            if (auxString.equals("3")) {auxString="Monedero"; }
            tipo = auxString;


            //fechaInicioCaducidad;
            //TODO leer flag y fecha
            c1.set(2000, Calendar.JANUARY, 1,0,0,0);
            c1.add(Calendar.MINUTE, hex2decimal(bytesToHexString(auxBytes)));
            format1 = new SimpleDateFormat("yyyy-MM-dd");
            String s = format1.format(c1.getTime());
            auxBytes = new byte[]{infoByte.get(startTittle + auxDataTitulo)[4]};
            auxString = hex2Binary(bytesToHexString(auxBytes)).substring(7,8);
            if(auxString.equals("0")){
                auxString = "Fecha Inicio: "+s;
            }else{
                auxString = "Fecha Caducidad: "+s;
            }
            fechaInicioCaducidad = auxString;


            int posSaldo = startTittle + auxSaldo;
            auxBytes = new byte[]{ infoByte.get(posSaldo)[3],infoByte.get(posSaldo)[2],infoByte.get(posSaldo)[1],infoByte.get(posSaldo)[0] };

            saldo = "0.00-Euros";
            if (tipo.equals("Viajes")||tipo.equals("Tiempo")){
                saldo = String.format("%d-Viajes", hex2decimal(bytesToHexString(auxBytes)));
            }else{
                saldo = String.format("%.2f-Euros", ((float) hex2decimal(bytesToHexString(auxBytes))/100));
            }


            //MOVIMINETOS
            movList = new ArrayList<Mov>();
            int[] pos = new int[]{44,45,46,48,49,50,52,53,54,56,57};

            for (int i=0;i<pos.length;i++){
                Mov mov = new Mov();

                //pos
                mov.pos = i+1;

                //operacion
                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                auxString = String.format("%d", (int) hex2decimal(bytesToHexString(auxBytes).substring(1,2)));
                if (auxString.equals("1")) { auxString = "Compra"; mov.fechaHoraInt = 9; }
                if (auxString.equals("2")) { auxString = "Recarga"; mov.fechaHoraInt = 8; }
                if (auxString.equals("3")) { auxString = "Validación"; mov.fechaHoraInt = 7; }
                if (auxString.equals("4")) { auxString = "Anulación"; mov.fechaHoraInt = 6; }
                if (auxString.equals("5")) { auxString = "Reactivación"; mov.fechaHoraInt = 5; }
                if (auxString.equals("6")) { auxString = "Eliminar"; mov.fechaHoraInt = 4; }
                if (auxString.equals("7")) { auxString = "Transbordo"; mov.fechaHoraInt = 3; }
                mov.operacion = auxString;

                //fechaHora y fechaHoraInt
                auxBytes = new byte[]{infoByte.get(pos[i])[1], infoByte.get(pos[i])[2], infoByte.get(pos[i])[3]};
                c1.set(2000, Calendar.JANUARY, 1,0,0,0);
                c1.add(Calendar.MINUTE, hex2decimal(bytesToHexString(auxBytes)));
                format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                auxString = format1.format(c1.getTime());
                mov.fechaHora = auxString;
                mov.fechaHoraInt = mov.fechaHoraInt + hex2decimal(bytesToHexString(auxBytes))*10;

                //tramos
                auxBytes = new byte[]{infoByte.get(pos[i])[4]};
                auxString = hex2Binary(bytesToHexString(auxBytes).substring(0,1));
                auxString = String.format("%d",Integer.parseInt(auxString,2));
                mov.tramos = auxString;

                //viajeros;
                auxBytes = new byte[]{infoByte.get(pos[i])[4], infoByte.get(pos[i])[5]};
                auxString = hex2Binary(bytesToHexString(auxBytes)).substring(5,10);
                auxString = String.format("%d",Integer.parseInt(auxString,2));
                mov.viajeros = auxString;

                //viajeTransbordo;
                auxBytes = new byte[]{infoByte.get(pos[i])[5]};
                auxString = hex2Binary(bytesToHexString(auxBytes)).substring(3,8);
                auxString = String.format("%d", ((int) hex2decimal(bytesToHexString(auxBytes))));
                mov.viajeTransbordo = auxString;

                //ultimaLinea;
                auxBytes = new byte[]{infoByte.get(pos[i])[6], infoByte.get(pos[i])[7]};
                auxString = hex2Binary(bytesToHexString(auxBytes)).substring(0,14);
                auxString = String.format("%d",Integer.parseInt(auxString,2));
                mov.ultimaLinea = auxString;

                //ultimoSentido;
                auxBytes = new byte[]{infoByte.get(i)[8], infoByte.get(pos[i])[9]};
                auxString = hex2Binary(bytesToHexString(auxBytes)).substring(14,16);
                auxString = String.format("%d",Integer.parseInt(auxString,2));
                if (auxString.equals("1")) {
                    auxString = "Ida";
                }
                if (auxString.equals("2")) {
                    auxString = "Vuelta";
                }
                mov.ultimoSentido = auxString;

                //parada;
                auxBytes = new byte[]{infoByte.get(i)[8], infoByte.get(pos[i])[9]};
                auxString = String.format("%d", ((int) hex2decimal(bytesToHexString(auxBytes))));
                if (paradaId.indexOf(auxString)!=-1){
                    auxString = paradaCode.get(paradaId.indexOf(auxString));
                }
                mov.parada = auxString;


                //autobusTranvia;
                auxBytes = new byte[]{infoByte.get(i)[10], infoByte.get(pos[i])[11]};
                auxString = String.format("%d", ((int) hex2decimal(bytesToHexString(auxBytes))));
                mov.autobusTranvia = auxString;

                //saldo;
                auxBytes = new byte[]{infoByte.get(pos[i])[12], infoByte.get(pos[i])[13]};
                if(tipo.equals("Viajes")){
                    auxString = "Viajes de la operación: "+String.format("%d", (int)hex2decimal(bytesToHexString(auxBytes)));
                }else if(tipo.equals("Tiempo")){
                    //String fechaEmision;
                    //String fechaCaducidad;

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar date = Calendar.getInstance();
                    String now = (new SimpleDateFormat("yyyy-MM-dd")).format(date.getTime());
                    try {
                        double dias = Math.floor((formatter.parse(fechaCaducidad).getTime() - formatter.parse(now).getTime()) / (1000 * 60 * 60 * 24));
                        //Log.v(LOG_TAG, String.format("%.2f", (double) dias));
                        if (dias>0){
                            auxString = "Dias de la operación: "+String.format("%d", (int) dias);
                        }else{
                            auxString = "Bono Caducado";
                        }
                    } catch (ParseException e) {
                        Log.v(LOG_TAG, e.toString());
                    }
                }else{
                    auxString = "Saldo de la operación: "+String.format("%.2f", ((double) hex2decimal(bytesToHexString(auxBytes)))/100)+" Euros";
                }
                mov.saldo = auxString;

                //operador;
                auxBytes = new byte[]{infoByte.get(i)[14]};
                auxString = String.format("%d",hex2decimal(bytesToHexString(auxBytes)));
                if (auxString.equals("1")){ auxString = "TM"; }
                if (auxString.equals("2")){ auxString = "TDM"; }
                if (auxString.equals("0")){ auxString = "TDM"; }
                if (auxString.equals("3")){ auxString = "LAT"; }


                mov.operador = auxString;

                movList.add(mov);
            }

            Collections.sort(movList, Collections.reverseOrder());

        }else{
            uid = "No hay datos";
        }

        this.isInfo = true;

        pruebaConsoleData();

    }

    public void pruebaConsoleData(){
        Log.v(LOG_TAG,"*** Prueba de Datos ***");

        byte[] auxBytes;
        String auxString;

        int[] pos = new int[]{44,45,46,48,49,50,52,53,54,56,57};
        for (int i=0;i<pos.length;i++) {

            auxBytes = new byte[]{infoByte.get(pos[i])[1], infoByte.get(pos[i])[2], infoByte.get(pos[i])[3]};
            Calendar c1 = GregorianCalendar.getInstance();
            c1.set(2000, Calendar.JANUARY, 1,0,0,0);
            c1.add(Calendar.MINUTE, hex2decimal(bytesToHexString(auxBytes)));
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            auxString = "date: "+format1.format(c1.getTime());

            auxBytes = new byte[]{infoByte.get(pos[i])[12], infoByte.get(pos[i])[13]};
            auxString += String.format(" saldo: %05d <-> %s <-> %s",hex2decimal(bytesToHexString(auxBytes)),bytesToHexString(auxBytes),hex2Binary(bytesToHexString(auxBytes)));

            auxBytes = new byte[]{infoByte.get(pos[i])[0]};
            auxString += String.format(" %s ",bytesToHexString(auxBytes));
            String aux = String.format("%d", (int) hex2decimal(bytesToHexString(auxBytes).substring(1,2)));
            if (aux.equals("1")) { aux = "Compra"; }
            if (aux.equals("2")) { aux = "Recarga"; }
            if (aux.equals("3")) { aux = "Validación"; }
            if (aux.equals("4")) { aux = "Anulación"; }
            if (aux.equals("5")) { aux = "Reactivación"; }
            if (aux.equals("6")) { aux = "Eliminar"; }
            if (aux.equals("7")) { aux = "Transbordo"; }
            auxString += " Operación: "+aux;


            Log.v(LOG_TAG,"Mov"+String.format("%02d",i+1)+": "+auxString);
        }

    }

    public String getMainScreenJSON(String login,String fecha){
        String s = "";
        Mov mov = new Mov();

        s = s+"uid="+uid;
        s = s+"&ntarjeta="+ntarjeta;
        s = s+"&fecha="+fecha;
        s = s+"&login="+login;

        //int i=1;
        //Log.v(LOG_TAG,"getMainScreenJSON");
        for (Mov m : movList){
            //Log.v(LOG_TAG,String.format("%02d",i));
            //i++;
            if (m.operacion.equals("Validación")){
                mov = m;
                break;
            }
        }

        s = s + "&fechaVal="+mov.fechaHora;
        s = s + "&paradaVal="+mov.parada ;
        s = s + "&nViajeros="+mov.viajeros ;
        s = s + "&tranvia="+mov.autobusTranvia ;
        s = s + "&tipoTarjeta="+codigoTitulo+" - "+tipo;
        s = s + "&operador="+propietario;

        s = s + "&saldo="+saldo;

        //Log.v(LOG_TAG,s);
        return s;
    }

    public String getAllData(){
        String result = "";

        if (isInfo) {
            result = result + "Número de tarjeta:\n\t" + ntarjeta + "\n";
            result = result + "Tipo de tarjeta:\n\t" + codigoTitulo + " -> " + tipo + "\n";
            result = result + "Propietario:\n \t" + propietario + "\n";
            result = result + "Fecha de Emisión:\n\t" + fechaEmision + "\n";
            result = result + "Fecha de Caducidad:\n\t" + fechaCaducidad + "\n";

            result = result + "\n";

            result = result + getInfoHexByte();
        }else{
            result = "";
        }

        return result;
    }

    public String getMovDataJSON(String fecha){
        String str="obj={\"data\":[";
        if (isInfo) {
            for (Mov s : movList) {
                String ss = movList.indexOf(s)<movList.size()-1? ",":"";

                str = str+"{\"pos\": \""+String.format("%02d", s.pos)+"\","+
                        " \"fechaHora\": \""+s.fechaHora+"\","+
                        " \"operacion\": \""+s.operacion+"\","+
                        " \"titulo\": \""+codigoTitulo+" - "+tipo+"\","+
                        " \"tramos\": \""+s.tramos+"\","+
                        " \"viajeros\": \""+s.viajeros+"\","+
                        " \"viajeTransbordo\": \""+s.viajeTransbordo+"\","+
                        " \"ultimaLinea\": \""+s.ultimaLinea+"\","+
                        " \"ultimoSentido\": \""+s.ultimoSentido+"\","+
                        " \"parada\": \""+s.parada+"\","+
                        " \"autobusTranvia\": \""+s.autobusTranvia+"\","+
                        " \"saldo\": \""+s.saldo+"\","+
                        " \"operador\": \""+ s.operador+"\"}"+ss;
            }
        }else{
            str = str+"]}";
        }
        str = str+"]}";

        str = str+"&fechaIniCad="+fechaInicioCaducidad;
        str = str+"&fechaAct="+fecha;
        str = str+"&fechaCad="+fechaCaducidad;
        str = str+"&titulo="+codigoTitulo;
        str = str+"&tipo="+tipo;

        return str;
    }
}
