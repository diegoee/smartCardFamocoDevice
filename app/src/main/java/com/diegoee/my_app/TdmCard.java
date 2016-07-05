package com.diegoee.my_app;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
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

        byte[] auxBytes;
        String result = "Esperando lectura...";

        if (infoByte.size()==64) { //1024bits = 64Bytes

            auxBytes = new byte[]{infoByte.get(44)[12], infoByte.get(44)[13]};
            result = "Saldo Actual: " + decoData(auxBytes,TdmCard.CAST);

            auxBytes = new byte[]{infoByte.get(44)[8], infoByte.get(44)[9]};
            result = result + "\nÚltima parada: "+ decoData(auxBytes,TdmCard.STATION);

            auxBytes = new byte[]{infoByte.get(44)[1], infoByte.get(44)[2], infoByte.get(44)[3]};
            result = result + "\nFecha: "+ decoData(auxBytes,TdmCard.DATE);

            auxBytes = new byte[]{infoByte.get(44)[4], infoByte.get(44)[5]};
            result = result + "\nNº de Viajeros: "+ decoData(auxBytes,TdmCard.NUMBER);
        }
        return result;
    }

    public String getCardData(){

        byte[] auxBytes;
        String result = "Esperando lectura...";

        if (infoByte.size()==64) {
            // Nº de tarjeta Sector1 bloque 0 byte 0,1,2 y 3
            auxBytes = new byte[]{infoByte.get(4)[0], infoByte.get(4)[1], infoByte.get(4)[2], infoByte.get(4)[3]};
            result = "N º de Tarjeta: " + bytesToHexString(auxBytes);

            //tipo de tarjeta sector 1 bloque 0 byte 4
            auxBytes = new byte[]{infoByte.get(4)[4]};
            result = result + "\nTipo de tarjeta: " + decoData(auxBytes,TdmCard.TYPE_OF_CARD);

            //Propietario sector 1 bloque 0 byte 5
            auxBytes = new byte[]{infoByte.get(4)[5]};
            result = result + "\nPropietario: " + decoData(auxBytes,TdmCard.OWNER);

            //FEcha de Emisión sector 1 bloque 0 byte 6y7
            auxBytes = new byte[]{infoByte.get(4)[6], infoByte.get(4)[7]};
            result = result + "\nFecha de Emisión: " + decoData(auxBytes,TdmCard.DATE);

            //FEcha de Caducidad sector 1 bloque 0 byte 8y9
            auxBytes = new byte[]{infoByte.get(4)[8], infoByte.get(4)[9]};
            result = result + "\nFecha de Caducidad: " + decoData(auxBytes,TdmCard.DATE);
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
                result = result + "\n"+(i+1)+"º Movimiento";

                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Títulos: " + decoData(auxBytes,TdmCard.TITTLE);

                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Operación: "+ decoData(auxBytes,TdmCard.OPERATION);

                auxBytes = new byte[]{infoByte.get(pos[i])[1], infoByte.get(pos[i])[2], infoByte.get(pos[i])[3]};
                result = result + "\n\t- Fecha: "+ decoData(auxBytes,TdmCard.DATE);

                auxBytes = new byte[]{infoByte.get(44)[8], infoByte.get(pos[i])[9]};
                result = result + "\n\t- Última parada: "+ decoData(auxBytes,TdmCard.STATION);

                auxBytes = new byte[]{infoByte.get(pos[i])[4], infoByte.get(pos[i])[5]};
                result = result + "\n\t- Nº de Viajeros: "+ decoData(auxBytes,TdmCard.NUMBER);

                auxBytes = new byte[]{infoByte.get(pos[i])[12], infoByte.get(pos[i])[13]};
                result = result + "\n\t- Saldo final: "+ decoData(auxBytes,TdmCard.CAST);
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

    public static String decoData(byte[] bArray, int type) {
        String val = "";
        int auxInt;

        if (type==TdmCard.OPERATION){
            val=String.format("%d",hex2decimal(bytesToHexString(bArray).substring(0,1)));
        }

        if (type==TdmCard.TITTLE){
            val=String.format("%d",hex2decimal(bytesToHexString(bArray).substring(1,2)));

        }

        if (type==TdmCard.DATE){
            val="0x"+bytesToHexString(bArray);
        }

        if (type==TdmCard.STATION){
            auxInt = ID_STATION.lastIndexOf(hex2decimal(bytesToHexString(bArray)));
            if (auxInt==-1) {
                val="No existe Id de parada";
            }else{
                val = DESC_STATION.get(ID_STATION.lastIndexOf(hex2decimal(bytesToHexString(bArray))));
            }
            if (val==null) {
                val="No existe Id de parada";
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
            if (val.equals("4d")) {
                val = "General";
            }
            if (val.equals("51")) {
                val = "Joven";
            }
            if (val.equals("49")) {
                val = "Estudiante";
            }
            if (val.equals("5d")) {
                val = "Campus";
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
            "Estadio Nueva Condomina <-> Andén 2 <-> Destino: Universidades",
            "Estadio Nueva Condomina <-> Andén 1 - Destino: Universidades",
            "La Ladera <-> Andén 1	118	A11-1	Universidades",
            "Infantas - Andén 1	122	A10-1	Universidades UCAM-Los Jerónimos",
            "Príncipe Felipe - Andén 1	123	A9-1	Universidades UCAM-Los Jerónimos",
            "Churra - Andén 1	125	A8-1	Universidades UCAM-Los Jerónimos",
            "Alameda - Andén 1	126	A7-1	Universidades UCAM-Los Jerónimos",
            "Los Cubos - Andén 1	128	A6-1	Universidades UCAM-Los Jerónimos",
            "Santiago y Zaraiche - Andén 1	129	A5-1	Universidades  UCAM-Los Jerónimos",
            "Príncipe de Asturias - Andén 1	130	A4-1	Universidades  UCAM-Los Jerónimos",
            "Abenarabi - Andén 1	131	A3-1	Universidades    UCAM-Los Jerónimos",
            "Marina Española - Andén 1	132	A2-1	Universidades        UCAM-Los Jerónimos",
            "Plaza Circular - Andén 1	134	A1-1	Universidades          UCAM-Los Jerónimos",
            "Juan Carlos I - Andén 1	155	B1-1	Universidades           UCAM-Los Jerónimos",
            "Biblioteca Regional - Andén 1	157	B2-1	Universidades            UCAM-Los Jerónimos",
            "Senda de Granada - Andén 1	158	B3-1	Universidades            UCAM-Los Jerónimos",
            "Parque Empresarial - Andén 1	159	B4-1	Universidades            UCAM-Los Jerónimos",
            "El Puntal <-> Andén 1	161	B5-1	Universidades            UCAM-Los Jerónimos",
            "Espinardo <-> Andén 1	162	B6-1	Universidades            UCAM-Los Jerónimos",
            "Los Rectores - Terra Natura <-> Andén 1	164	B7-1	Universidades            UCAM-Los Jerónimos",
            "Universidad de Murcia <-> Andén 1	187	C1-1	Estadio Nueva Condomina",
            "Servicios de Investigación <-> Andén 1	188	C2-1	Estadio Nueva Condomina",
            "Centro Social <-> Andén 1	189	C3-1	Estadio Nueva Condomina",
            "Biblioteca General <-> Andén 1	190	C4-1	Estadio Nueva Condomina",
            "Residencia Universitaria <-> Andén 1	191	C5-1	Estadio Nueva Condomina",
            "Los Rectores - Terra Natura <-> Andén 2	177	B7-2	Estadio Nueva Condomina",
            "Espinardo <-> Andén 2	179	B6-2	Estadio Nueva Condomina",
            "El Puntal <-> Andén 2	180	B5-2	Estadio Nueva Condomina",
            "Parque Empresarial	2	182	B4-2	Estadio Nueva Condomina",
            "Senda de Granada	2	183	B3-2	Estadio Nueva Condomina",
            "Biblioteca Regional	2	184	B2-2	Estadio Nueva Condomina",
            "Juan Carlos I	2	186	B1-2	Estadio Nueva Condomina",
            "Plaza Circular	2	135	A1-2	Estadio Nueva Condomina",
            "Marina Española	2	137	A2-2	Estadio Nueva Condomina",
            "Abenarabi	2	138	A3-2	Estadio Nueva Condomina",
            "Príncipe de Asturias <-> Andén 2	139	A4-2	Estadio Nueva Condomina",
            "Santiago y Zaraiche <-> Andén 2	140	A5-2	Estadio Nueva Condomina",
            "Los Cubos <-> Andén 2	141	A6-2	Estadio Nueva Condomina",
            "Alameda <-> Andén 2	143	A7-2	Estadio Nueva Condomina",
            "Churra	2 <-> Andén 144	A8-2	Estadio Nueva Condomina",
            "Príncipe Felipe	2	146	A9-2	Estadio Nueva Condomina",
            "Infantas <-> Andén 2	147	A10-2	Estadio Nueva Condomina",
            "La Ladera <-> Andén 2	149	A11-2	Estadio Nueva Condomina",
            "Guadalupe <-> Andén 1	204	B8-1	Los Rectores-Terra Natura            UCAM-Los Jerónimos            Estadio Nueva Condomina",
            "Reyes Católicos	1	169	B9-1	Los Rectores-Terra Natura            UCAM-Los Jerónimos            Estadio Nueva Condomina",
            "El Portón <-> Andén 1	170	B10-1	Los Rectores-Terra Natura            UCAM-Los Jerónimos            Estadio Nueva Condomina",
            "UCAM - Los Jerónimos <-> Andén 1	192	B11-1	Los Rectores-Terra Natura            Estadio Nueva Condomina",
            "UCAM - Los Jerónimos <-> Andén 2	172	B11-2	Los Rectores-Terra Natura            Estadio Nueva Condomina",
            "Talleres y Cocheras",
            "Los Rectores - Terra Natura <-> Andén 3	195	B7-3	Ninguno",
            "Desconocido <-> Andén n/a <-> Destino: Desconocido",
            "No Hay dato"
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
