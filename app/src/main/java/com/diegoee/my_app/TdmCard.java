package com.diegoee.my_app;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TdmCard {

    private String info;
    ArrayList<byte[]> infoByte;

    public TdmCard() {
        super();
        this.info = null;
        infoByte = new ArrayList<byte[]>();
    }

    public void append(byte[] data){
        this.infoByte.add(data);
    }

    public String getInfo(){
        String result = "";
        if (infoByte.size()==64) {
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
            result = "No Data";
        }
        return result;
    }

    public void eraseInfo(){
        this.info = null;
        infoByte = new ArrayList<byte[]>();
    }

    public String getMainData(){
        String result = "";
        return result;
    }

    public String getCtrlData(){
        String result = "En desarrollo...";
        return result;
    }

    public String getCardData(){

        byte[] auxBytes;
        String result = "Esperando lectura...";

        if (infoByte.size()==64) {
            // Nº de tarjeta Sector1 bloque 0 byte 0,1,2 y 3
            auxBytes = new byte[]{infoByte.get(4)[0], infoByte.get(4)[1], infoByte.get(4)[2], infoByte.get(4)[3]};
            result = "Número de Tarjeta:\n\t(Hex.)" + bytesToHexString(auxBytes)+"  -  "+ decoData(auxBytes,TdmCard.NUMBER);

            //tipo de tarjeta sector 1 bloque 0 byte 4
            auxBytes = new byte[]{infoByte.get(4)[4]};
            result = result + "\nTipo de tarjeta:\n\t" + decoData(auxBytes,TdmCard.TYPE_OF_CARD);

            //Propietario sector 1 bloque 0 byte 5
            auxBytes = new byte[]{infoByte.get(4)[5]};
            result = result + "\nPropietario:\n\t" + decoData(auxBytes,TdmCard.OWNER);

            //FEcha de Emisión sector 1 bloque 0 byte 6y7
            auxBytes = new byte[]{infoByte.get(4)[6], infoByte.get(4)[7]};
            result = result + "\nFecha de Emisión:\n\t" + decoData(auxBytes,TdmCard.DATE);

            //FEcha de Caducidad sector 1 bloque 0 byte 8y9
            auxBytes = new byte[]{infoByte.get(4)[8], infoByte.get(4)[9]};
            result = result + "\nFecha de Caducidad:\n\t" + decoData(auxBytes,TdmCard.DATE);
        }

        return result;
    }

    public String getMovData(){
        byte[] auxBytes;
        String result = "Esperando lectura...";
        if (infoByte.size()==64) {
            result="";
            int[] pos =new int[]{44,45,46,48,49,50,52,53,54,56,57};
            for (int i=0;i<pos.length;i++){
                result = result + "\n"+(i+1)+"º  ******Movimiento******:";

                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Títulos: " + decoData(auxBytes,TdmCard.TITTLE);

                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Operación: "+ decoData(auxBytes,TdmCard.OPERATION);

                auxBytes = new byte[]{infoByte.get(pos[i])[1], infoByte.get(pos[i])[2], infoByte.get(pos[i])[3]};
                result = result + "\n\t- Fecha/hora: "+ decoData(auxBytes,TdmCard.DATE_MOV);

                auxBytes = new byte[]{infoByte.get(44)[8], infoByte.get(pos[i])[9]};
                result = result + "\n\t- Parada: "+decoData(auxBytes,TdmCard.STATION);

                //auxBytes = new byte[]{infoByte.get(pos[i])[4], infoByte.get(pos[i])[5]};
                //result = result + "\n\t- Nº Viajeros: "+ decoData(auxBytes,TdmCard.NUMBER);

                auxBytes = new byte[]{infoByte.get(pos[i])[12], infoByte.get(pos[i])[13]};
                result = result + "\n\t- Saldo Final: "+ decoData(auxBytes,TdmCard.CAST);
            }
        }
        return result;
    }

    private static final int STATION=1;
    private static final int CAST=2;
    private static final int DATE=3;
    private static final int TITTLE=4;
    private static final int OPERATION=5;
    private static final int TYPE_OF_CARD=6;
    private static final int OWNER=7;
    private static final int NUMBER=8;
    private static final int DATE_MOV=9;

    public static String decoData(byte[] bArray, int type) {
        String val = "";
        int auxInt;

        if (type==TdmCard.OPERATION){
            val=bytesToHexString(bArray).substring(1,2);
            if (val.equals("1")) {
                val = "Compra";
            }
            if (val.equals("2")) {
                val = "Recarga";
            }
            if (val.equals("3")) {
                val = "Validación";
            }
            if (val.equals("4")) {
                val = "Anulación";
            }
            if (val.equals("5")) {
                val = "Reactivación";
            }
            if (val.equals("6")) {
                val = "Eliminar";
            }
        }

        if (type==TdmCard.TITTLE){
            val=bytesToHexString(bArray).substring(0,1);
            if (val.equals("0")) { val = "(binario) 0000";  }
            if (val.equals("1")) { val = "(binario) 0001";  }
            if (val.equals("2")) { val = "(binario) 0010";  }
            if (val.equals("3")) { val = "(binario) 0011";  }
            if (val.equals("4")) { val = "(binario) 0100";  }
            if (val.equals("5")) { val = "(binario) 0101";  }
            if (val.equals("6")) { val = "(binario) 0110";  }
            if (val.equals("7")) { val = "(binario) 0111";  }
            if (val.equals("8")) { val = "(binario) 1000";  }
            if (val.equals("9")) { val = "(binario) 1001";  }
            if (val.equals("A")) { val = "(binario) 1010";  }
            if (val.equals("B")) { val = "(binario) 1011";  }
            if (val.equals("C")) { val = "(binario) 1100";  }
            if (val.equals("D")) { val = "(binario) 1101";  }
            if (val.equals("E")) { val = "(binario) 1110";  }
            if (val.equals("F")) { val = "(binario) 1111";  }
        }

        if ((type==TdmCard.DATE)||(type==TdmCard.DATE_MOV)){
            Calendar c1 = GregorianCalendar.getInstance();
            c1.set(2000, Calendar.JANUARY, 1,0,0,0);
            SimpleDateFormat format1;
            if (type==TdmCard.DATE){
                c1.add(Calendar.DAY_OF_YEAR, hex2decimal(bytesToHexString(bArray)));
                format1 = new SimpleDateFormat("dd-MM-yyyy");
            }else{
                c1.add(Calendar.MINUTE, hex2decimal(bytesToHexString(bArray)));
                format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            }

            //Log.v(MainActivity.LOG_TAG,format1.format(c1.getTime()));
            val=format1.format(c1.getTime());
        }

        if (type==TdmCard.STATION){
            auxInt = ID_STATION.lastIndexOf(hex2decimal(bytesToHexString(bArray)));
            if (auxInt==-1) {
                val="\n\t\tNo existe Id de parada";
            }else{
                val =
                    "\n\t\tCódigo: " +CODE_STATION.get(ID_STATION.lastIndexOf(hex2decimal(bytesToHexString(bArray))))+
                    "\n\t\tNombre: " +DESC_STATION.get(ID_STATION.lastIndexOf(hex2decimal(bytesToHexString(bArray))))+
                    "\n\t\tAndén: "  +LINE_STATION.get(ID_STATION.lastIndexOf(hex2decimal(bytesToHexString(bArray))))+
                    "\n\t\tDestino: "+DIR_STATION.get(ID_STATION.lastIndexOf(hex2decimal(bytesToHexString(bArray))));
            }
            if (val==null) {
                val="\n\t\tNo existe Id de parada";
            }
        }

        if (type==TdmCard.NUMBER){
            val=String.format("%d",hex2decimal(bytesToHexString(bArray)));
        }

        if (type==TdmCard.CAST){
            val=String.format("%d",hex2decimal(bytesToHexString(bArray)))+" €";
        }

        if (type==TdmCard.OWNER) {
            val = bytesToHexString(bArray);
            if (val.equals("01")) {
                val = "EPT";
            }
            if (val.equals("02")){
                val = "TDM";
            }
        }

        if (type==TdmCard.TYPE_OF_CARD) {
            val = bytesToHexString(bArray);
            if (val.equals("41")) {
                val = "Anónima-c";
            }
            if (val.equals("45")) {
                val = "Anónima-p";
            }
            if (val.equals("49")) {
                val = "b100";
            }
            if (val.equals("4B")) {
                val = "empleado";
            }
            if (val.equals("4D")) {
                val = "General";
            }
            if (val.equals("51")) {
                val = "estudiante-col";
            }
            if (val.equals("55")) {
                val = "estudiante-uni";
            }
            if (val.equals("59")) {
                val = "Campus";
            }
            if (val.equals("5D")) {
                val = "fne";
            }
            if (val.equals("61")) {
                val = "fngu";
            }
        }

        return val;
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

    public static final List<String> DESC_STATION = Arrays.asList(
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "La Ladera",
        "Infantas",
        "Príncipe Felipe",
        "Churra",
        "Alameda",
        "Los Cubos",
        "Santiago y Zaraiche",
        "Príncipe de Asturias",
        "Abenarabi",
        "Marina Española",
        "Plaza Circular",
        "Juan Carlos I",
        "Biblioteca Regional",
        "Senda de Granada",
        "Parque Empresarial",
        "El Puntal",
        "Espinardo",
        "Los Rectores",
        "Universidad de Murcia",
        "Servicios de Investigación",
        "Centro Social",
        "Biblioteca General",
        "Residencia Universitaria",
        "Los Rectores",
        "Espinardo",
        "El Puntal",
        "Parque Empresarial",
        "Senda de Granada",
        "Biblioteca Regional",
        "Juan Carlos I",
        "Plaza Circular",
        "Marina Española",
        "Abenarabi",
        "Príncipe de Asturias",
        "Santiago y Zaraiche",
        "Los Cubos",
        "Alameda",
        "Churra",
        "Príncipe Felipe",
        "Infantas",
        "La Ladera",
        "Guadalupe",
        "Reyes Católicos",
        "El Portón",
        "UCAM - Los Jerónimos",
        "UCAM - Los Jerónimos",
        "Talleres y Cocheras",
        "Los Rectores - Terra Natura",
        "Desconocido",
        "0"
    );

    public static final List<String> LINE_STATION = Arrays.asList(
        "2",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "1",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "2",
        "1",
        "1",
        "1",
        "1",
        "2",
        "Talleres y Cocheras",
        "3",
        "n/a",
        "n/a"
    );

    public static final List<String> DIR_STATION = Arrays.asList(
        "Universidades",
        "Universidades",
        "Universidades",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Uni. UCAM-Los Jerónimos",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Estadio Nueva Condomina",
        "Los Rectores-Terra Natura UCAM-Los Jerónimos Estadio Nueva Condomina",
        "Los Rectores-Terra Natura UCAM-Los Jerónimos Estadio Nueva Condomina",
        "Los Rectores-Terra Natura UCAM-Los Jerónimos Estadio Nueva Condomina",
        "Los Rectores-Terra Natura Estadio Nueva Condomina",
        "Los Rectores-Terra Natura Estadio Nueva Condomina",
        "Talleres y Cocheras",
        "Ninguno",
        "Desconocido",
        "Desconocido"
    );

    public static final List<String> CODE_STATION = Arrays.asList(
        "A12-2",
        "A12-1",
        "A11-1",
        "A10-1",
        "A9-1",
        "A8-1",
        "A7-1",
        "A6-1",
        "A5-1",
        "A4-1",
        "A3-1",
        "A2-1",
        "A1-1",
        "B1-1",
        "B2-1",
        "B3-1",
        "B4-1",
        "B5-1",
        "B6-1",
        "B7-1",
        "C1-1",
        "C2-1",
        "C3-1",
        "C4-1",
        "C5-1",
        "B7-2",
        "B6-2",
        "B5-2",
        "B4-2",
        "B3-2",
        "B2-2",
        "B1-2",
        "A1-2",
        "A2-2",
        "A3-2",
        "A4-2",
        "A5-2",
        "A6-2",
        "A7-2",
        "A8-2",
        "A9-2",
        "A10-2",
        "A11-2",
        "B8-1",
        "B9-1",
        "B10-1",
        "B11-1",
        "B11-2",
        "65535",
        "B7-3",
        "n/a",
        "0"
    );

    public static final List<Integer> ID_STATION = Arrays.asList(
        153,
        114,
        118,
        122,
        123,
        125,
        126,
        128,
        129,
        130,
        131,
        132,
        134,
        155,
        157,
        158,
        159,
        161,
        162,
        164,
        187,
        188,
        189,
        190,
        191,
        177,
        179,
        180,
        182,
        183,
        184,
        186,
        135,
        137,
        138,
        139,
        140,
        141,
        143,
        144,
        146,
        147,
        149,
        204,
        169,
        170,
        192,
        172,
        65535,
        195,
        1,
        0
    );
}
