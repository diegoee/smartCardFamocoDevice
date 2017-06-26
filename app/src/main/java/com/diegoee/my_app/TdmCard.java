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
    String tipoTarjeta;
    String tipoTarjetaSaldo;
    String propietario;
    String fechaEmision;
    String fechaCaducidad;

    private List<String> stationsId;
    private List<String> stationsCode;

    private List<String> titulosId;
    private List<String> titulosDesc;
    private List<String> titulosTipo;


    //Constructor;
    public TdmCard(Context ctx) {
        super();
        this.isInfo = false;
        infoByte = new ArrayList<byte[]>();
        movList = new ArrayList<Mov>();
        stationsId = new ArrayList<String>();
        stationsCode = new ArrayList<String>();
        titulosId = new ArrayList<String>();
        titulosDesc = new ArrayList<String>();
        titulosTipo = new ArrayList<String>();
        readDataJSON("data/dataTrain.json",ctx);
    }

    public void eraseInfo(){
        this.isInfo = false;
        infoByte = new ArrayList<byte[]>();
        movList = new ArrayList<Mov>();
        uid = "";
        ntarjeta = "";
        tipoTarjeta = "";
        tipoTarjetaSaldo = "";
        propietario = "";
        fechaEmision = "";
        fechaCaducidad = "";

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
                int len = (new JSONObject(jsonString)).optJSONArray("station").length();
                for (int i = 0; i < len; i++) {
                    stationsId.add((new JSONObject(jsonString)).optJSONArray("station").getJSONObject(i).getString("id"));
                    stationsCode.add((new JSONObject(jsonString)).optJSONArray("station").getJSONObject(i).getString("code"));
                }
            } catch (final JSONException e) {
                Log.v(LOG_TAG, e.toString());
            }

            try {
                int len = (new JSONObject(jsonString)).optJSONArray("titulos").length();
                for (int i = 0; i < len; i++) {
                    titulosId.add((new JSONObject(jsonString)).optJSONArray("titulos").getJSONObject(i).getString("id"));
                    titulosDesc.add((new JSONObject(jsonString)).optJSONArray("titulos").getJSONObject(i).getString("desc"));
                    titulosTipo.add((new JSONObject(jsonString)).optJSONArray("titulos").getJSONObject(i).getString("type"));
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

        if (infoByte.size()==64) {
            uid = bytesToHexString(new byte[]{infoByte.get(0)[0], infoByte.get(0)[1], infoByte.get(0)[2], infoByte.get(0)[3]});

            //ntarjeta
            auxBytes = new byte[]{infoByte.get(4)[0], infoByte.get(4)[1], infoByte.get(4)[2], infoByte.get(4)[3]};
            ntarjeta = String.format("%d",hex2decimal(bytesToHexString(auxBytes)));

            //tipoTarjeta
            auxBytes = new byte[]{infoByte.get(4)[4]};
            auxString = String.format("%d", ((int) hex2decimal(bytesToHexString(auxBytes))));
            if (titulosId.indexOf(auxString)==-1){
                tipoTarjeta = "No idetificada";
                tipoTarjetaSaldo = "Monedero";
            }else {
                tipoTarjeta = titulosDesc.get(titulosId.indexOf(auxString));
                tipoTarjetaSaldo = titulosTipo.get(titulosId.indexOf(auxString));
            }
            //propietario
            auxBytes = new byte[]{infoByte.get(4)[5]};
            auxString = bytesToHexString(auxBytes);
            if (auxString.equals("01")) {
                auxString = "EPT";
            }
            if (auxString.equals("02")){
                auxString = "TDM";
            }
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


            //MOVIMINETOS
            movList = new ArrayList<Mov>();
            int[] pos = new int[]{44,45,46,48,49,50,52,53,54,56,57};

            for (int i=0;i<pos.length;i++){
                Mov mov = new Mov();

                //pos
                mov.pos = i+1;

                //titulo
                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                auxString = bytesToHexString(auxBytes).substring(0,1);
                if (auxString.equals("0")) { auxString = "Ninguno";  }
                if (auxString.equals("1")) { auxString = "4";  }
                if (auxString.equals("2")) { auxString = "3";  }
                if (auxString.equals("3")) { auxString = "3 y 4";  }
                if (auxString.equals("4")) { auxString = "2";  }
                if (auxString.equals("5")) { auxString = "2 y 4";  }
                if (auxString.equals("6")) { auxString = "2 y 3";  }
                if (auxString.equals("7")) { auxString = "2,3 y 4";  }
                if (auxString.equals("8")) { auxString = "1";  }
                if (auxString.equals("9")) { auxString = "1 y 4";  }
                if (auxString.equals("A")) { auxString = "1 y 3";  }
                if (auxString.equals("B")) { auxString = "1,3 y 4";  }
                if (auxString.equals("C")) { auxString = "1 y 2";  }
                if (auxString.equals("D")) { auxString = "1,2 y 4";  }
                if (auxString.equals("E")) { auxString = "1,2 y 3";  }
                if (auxString.equals("F")) { auxString = "1,2,3 y 4";  }
                mov.titulo = auxString;

                //operacion
                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                auxString = bytesToHexString(auxBytes).substring(1,2);
                if (auxString.equals("1")) { auxString = "Compra"; }
                if (auxString.equals("2")) { auxString = "Recarga"; }
                if (auxString.equals("3")) { auxString = "Validación"; }
                if (auxString.equals("4")) { auxString = "Anulación"; }
                if (auxString.equals("5")) { auxString = "Reactivación"; }
                if (auxString.equals("6")) { auxString = "Eliminar"; }
                mov.operacion = auxString;

                //fechaHora y fechaHoraInt
                auxBytes = new byte[]{infoByte.get(pos[i])[1], infoByte.get(pos[i])[2], infoByte.get(pos[i])[3]};
                c1.set(2000, Calendar.JANUARY, 1,0,0,0);
                c1.add(Calendar.MINUTE, hex2decimal(bytesToHexString(auxBytes)));
                format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                auxString = format1.format(c1.getTime());
                mov.fechaHora = auxString;
                mov.fechaHoraInt = hex2decimal(bytesToHexString(auxBytes));


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
                if (stationsId.indexOf(auxString)==-1){
                    auxString = "?";
                }else {
                    auxString = stationsCode.get(stationsId.indexOf(auxString));
                }
                mov.parada = auxString;


                //autobusTranvia;
                auxBytes = new byte[]{infoByte.get(i)[10], infoByte.get(pos[i])[11]};
                auxString = String.format("%d", ((int) hex2decimal(bytesToHexString(auxBytes))));
                mov.autobusTranvia = auxString;

                //saldo;
                auxBytes = new byte[]{infoByte.get(pos[i])[12], infoByte.get(pos[i])[13]};
                if(tipoTarjetaSaldo.equals("Viajes")){
                    auxString = "Viajes: "+String.format("%d", (int)hex2decimal(bytesToHexString(auxBytes)));
                }else if(tipoTarjetaSaldo.equals("Temporal")){
                    //String fechaEmision;
                    //String fechaCaducidad;

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        double dias = Math.floor(formatter.parse(fechaCaducidad).getTime() - formatter.parse(fechaEmision).getTime() / (1000 * 60 * 60 * 24));
                        auxString = "Días Restantes: "+String.format("%d", (int) dias);
                    } catch (ParseException e) {
                        Log.v(LOG_TAG, e.toString());
                    }
                }else{
                    auxString = "Saldo: "+String.format("%.2f", ((double) hex2decimal(bytesToHexString(auxBytes)))/100)+" Euros";
                }
                mov.saldo = auxString;

                //operador;
                auxBytes = new byte[]{infoByte.get(i)[14]};
                auxString = bytesToHexString(auxBytes);
                if (auxString.equals("01")) {
                    auxString = "EPT";
                }
                if (auxString.equals("02")) {
                    auxString = "TDM";
                }
                mov.operador = auxString;

                movList.add(mov);
            }

            Collections.sort(movList, Collections.reverseOrder());

        }else{
            uid = "No hay datos";
        }

        this.isInfo = true;

    }

    public String getMainScreenJSON(String login,String fecha){
        String s = "";
        Mov mov = new Mov();

        s = s+"uid="+ntarjeta;
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
        s = s + "&saldo="+mov.saldo;
        s = s + "&tipoTarjeta="+tipoTarjetaSaldo;
        s = s + "&operador="+mov.operador;

        //Log.v(LOG_TAG,s);
        return s;
    }

    public String getAllData(){
        String result = "";

        if (isInfo) {
            result = result + "Número de tarjeta:\n\t" + ntarjeta + "\n";
            result = result + "Tipo de tarjeta:\n\t" + tipoTarjeta + " -> " + tipoTarjetaSaldo + "\n";
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

    public String getMovData(){
        String result = "";
        if (isInfo) {
            result = result + "Movimientos:\n";
            for (Mov s : movList) {
                result = result + String.format("%02d", s.pos) + " - " + s.fechaHora + " - " + s.operacion + "\n";
                result = result + "\tTitulos: " + s.titulo + "\n";
                result = result + "\tTramos: " + s.tramos + "\n";
                result = result + "\tViajeros: " + s.viajeros + "\n";
                result = result + "\tViajeros Transbordo: " + s.viajeTransbordo + "\n";
                result = result + "\tÚlt. Linea: " + s.ultimaLinea + "\n";
                result = result + "\tÚlt. Sentido: " + s.ultimoSentido + "\n";
                result = result + "\tParada: " + s.parada + "\n";
                result = result + "\tAutobus: " + s.autobusTranvia + "\n";
                result = result + "\t" + s.saldo + "\n";
                result = result + "\tOperador: " + s.operador + "\n";

            }
            result = result + "\n";
        }else{
            result = "";
        }
        return result;
    }

    public String getMovDataJSON(){
        String str="obj={\"data\":[";
        if (isInfo) {
            for (Mov s : movList) {
                String ss = movList.indexOf(s)<movList.size()-1? ",":"";

                str = str+"{\"pos\": \""+String.format("%02d", s.pos)+"\","+
                        " \"fechaHora\": \""+s.fechaHora+"\","+
                        " \"operacion\": \""+s.operacion+"\","+
                        " \"titulo\": \""+s.titulo+"\","+
                        " \"tramos\": \""+s.tramos+"\","+
                        " \"viajeros\": \""+s.viajeros+"\","+
                        " \"viajeTransbordo\": \""+s.viajeTransbordo+"\","+
                        " \"ultimaLinea\": \""+s.ultimaLinea+"\","+
                        " \"ultimoSentido\": \""+s.ultimoSentido+"\","+
                        " \"autobusTranvia\": \""+s.autobusTranvia+"\","+
                        " \"saldo\": \""+s.saldo+"\","+
                        " \"operador\": \""+ s.operador+"\"}"+ss;
            }
        }else{
            str = str+"]}";
        }
        str = str+"]}";

        return str;
    }
}
