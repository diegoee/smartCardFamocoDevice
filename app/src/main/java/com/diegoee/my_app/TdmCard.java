package com.diegoee.my_app;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TdmCard {


    //public static final List<String> DESC_STATION = Arrays.asList(
            /*
            "Estadio Nueva Condomina	2	153	A12-2	Universidades
            Estadio Nueva Condomina	1	114	A12-1	Universidades
            La Ladera	1	118	A11-1	Universidades
            Infantas	1	122	A10-1	Universidades
            UCAM-Los Jerónimos
            Príncipe Felipe	1	123	A9-1	Universidades
            UCAM-Los Jerónimos
            Churra	1	125	A8-1	Universidades
            UCAM-Los Jerónimos
            Alameda	1	126	A7-1	Universidades
            UCAM-Los Jerónimos
            Los Cubos	1	128	A6-1	Universidades
            UCAM-Los Jerónimos
            Santiago y Zaraiche	1	129	A5-1	Universidades
            UCAM-Los Jerónimos
            Príncipe de Asturias	1	130	A4-1	Universidades
            UCAM-Los Jerónimos
            Abenarabi	1	131	A3-1	Universidades
            UCAM-Los Jerónimos
            Marina Española	1	132	A2-1	Universidades
            UCAM-Los Jerónimos
            Plaza Circular	1	134	A1-1	Universidades
            UCAM-Los Jerónimos
            Juan Carlos I	1	155	B1-1	Universidades
            UCAM-Los Jerónimos
            Biblioteca Regional	1	157	B2-1	Universidades
            UCAM-Los Jerónimos
            Senda de Granada	1	158	B3-1	Universidades
            UCAM-Los Jerónimos
            Parque Empresarial	1	159	B4-1	Universidades
            UCAM-Los Jerónimos
            El Puntal	1	161	B5-1	Universidades
            UCAM-Los Jerónimos
            Espinardo	1	162	B6-1	Universidades
            UCAM-Los Jerónimos
            Los Rectores - Terra Natura	1	164	B7-1	Universidades
            UCAM-Los Jerónimos
            Universidad de Murcia	1	187	C1-1	Estadio Nueva Condomina
            Servicios de Investigación	1	188	C2-1	Estadio Nueva Condomina
            Centro Social	1	189	C3-1	Estadio Nueva Condomina
            Biblioteca General	1	190	C4-1	Estadio Nueva Condomina
            Residencia Universitaria	1	191	C5-1	Estadio Nueva Condomina
            Los Rectores - Terra Natura	2	177	B7-2	Estadio Nueva Condomina
            Espinardo	2	179	B6-2	Estadio Nueva Condomina
            El Puntal	2	180	B5-2	Estadio Nueva Condomina
            Parque Empresarial	2	182	B4-2	Estadio Nueva Condomina
            Senda de Granada	2	183	B3-2	Estadio Nueva Condomina
            Biblioteca Regional	2	184	B2-2	Estadio Nueva Condomina
            Juan Carlos I	2	186	B1-2	Estadio Nueva Condomina
            Plaza Circular	2	135	A1-2	Estadio Nueva Condomina
            Marina Española	2	137	A2-2	Estadio Nueva Condomina
            Abenarabi	2	138	A3-2	Estadio Nueva Condomina
            Príncipe de Asturias	2	139	A4-2	Estadio Nueva Condomina
            Santiago y Zaraiche	2	140	A5-2	Estadio Nueva Condomina
            Los Cubos	2	141	A6-2	Estadio Nueva Condomina
            Alameda	2	143	A7-2	Estadio Nueva Condomina
            Churra	2	144	A8-2	Estadio Nueva Condomina
            Príncipe Felipe	2	146	A9-2	Estadio Nueva Condomina
            Infantas	2	147	A10-2	Estadio Nueva Condomina
            La Ladera	2	149	A11-2	Estadio Nueva Condomina
            Guadalupe	1	204	B8-1	Los Rectores-Terra Natura
            UCAM-Los Jerónimos
            Estadio Nueva Condomina
            Reyes Católicos	1	169	B9-1	Los Rectores-Terra Natura
            UCAM-Los Jerónimos
            Estadio Nueva Condomina
            El Portón	1	170	B10-1	Los Rectores-Terra Natura
            UCAM-Los Jerónimos
            Estadio Nueva Condomina
            UCAM - Los Jerónimos	1	192	B11-1	Los Rectores-Terra Natura
            Estadio Nueva Condomina
            UCAM - Los Jerónimos	2	172	B11-2	Los Rectores-Terra Natura
            Estadio Nueva Condomina
            Talleres y Cocheras	de 3 a 11	65535	Cocheras	Ninguno
            Los Rectores - Terra Natura	3	195	B7-3	Ninguno
            Desconocido	n/a	1	Desconocido	Ninguno"*/
    //);

/*
    Estadio Nueva Condomina	2	153	A12-2	Universidades
    Estadio Nueva Condomina	1	114	A12-1	Universidades
    La Ladera	1	118	A11-1	Universidades
    Infantas	1	122	A10-1	Universidades
    UCAM-Los Jerónimos
    Príncipe Felipe	1	123	A9-1	Universidades
    UCAM-Los Jerónimos
    Churra	1	125	A8-1	Universidades
    UCAM-Los Jerónimos
    Alameda	1	126	A7-1	Universidades
    UCAM-Los Jerónimos
    Los Cubos	1	128	A6-1	Universidades
    UCAM-Los Jerónimos
    Santiago y Zaraiche	1	129	A5-1	Universidades
    UCAM-Los Jerónimos
    Príncipe de Asturias	1	130	A4-1	Universidades
    UCAM-Los Jerónimos
    Abenarabi	1	131	A3-1	Universidades
    UCAM-Los Jerónimos
    Marina Española	1	132	A2-1	Universidades
    UCAM-Los Jerónimos
    Plaza Circular	1	134	A1-1	Universidades
    UCAM-Los Jerónimos
    Juan Carlos I	1	155	B1-1	Universidades
    UCAM-Los Jerónimos
    Biblioteca Regional	1	157	B2-1	Universidades
    UCAM-Los Jerónimos
    Senda de Granada	1	158	B3-1	Universidades
    UCAM-Los Jerónimos
    Parque Empresarial	1	159	B4-1	Universidades
    UCAM-Los Jerónimos
    El Puntal	1	161	B5-1	Universidades
    UCAM-Los Jerónimos
    Espinardo	1	162	B6-1	Universidades
    UCAM-Los Jerónimos
    Los Rectores - Terra Natura	1	164	B7-1	Universidades
    UCAM-Los Jerónimos
    Universidad de Murcia	1	187	C1-1	Estadio Nueva Condomina
    Servicios de Investigación	1	188	C2-1	Estadio Nueva Condomina
    Centro Social	1	189	C3-1	Estadio Nueva Condomina
    Biblioteca General	1	190	C4-1	Estadio Nueva Condomina
    Residencia Universitaria	1	191	C5-1	Estadio Nueva Condomina
    Los Rectores - Terra Natura	2	177	B7-2	Estadio Nueva Condomina
    Espinardo	2	179	B6-2	Estadio Nueva Condomina
    El Puntal	2	180	B5-2	Estadio Nueva Condomina
    Parque Empresarial	2	182	B4-2	Estadio Nueva Condomina
    Senda de Granada	2	183	B3-2	Estadio Nueva Condomina
    Biblioteca Regional	2	184	B2-2	Estadio Nueva Condomina
    Juan Carlos I	2	186	B1-2	Estadio Nueva Condomina
    Plaza Circular	2	135	A1-2	Estadio Nueva Condomina
    Marina Española	2	137	A2-2	Estadio Nueva Condomina
    Abenarabi	2	138	A3-2	Estadio Nueva Condomina
    Príncipe de Asturias	2	139	A4-2	Estadio Nueva Condomina
    Santiago y Zaraiche	2	140	A5-2	Estadio Nueva Condomina
    Los Cubos	2	141	A6-2	Estadio Nueva Condomina
    Alameda	2	143	A7-2	Estadio Nueva Condomina
    Churra	2	144	A8-2	Estadio Nueva Condomina
    Príncipe Felipe	2	146	A9-2	Estadio Nueva Condomina
    Infantas	2	147	A10-2	Estadio Nueva Condomina
    La Ladera	2	149	A11-2	Estadio Nueva Condomina
    Guadalupe	1	204	B8-1	Los Rectores-Terra Natura
    UCAM-Los Jerónimos
    Estadio Nueva Condomina
    Reyes Católicos	1	169	B9-1	Los Rectores-Terra Natura
    UCAM-Los Jerónimos
    Estadio Nueva Condomina
    El Portón	1	170	B10-1	Los Rectores-Terra Natura
    UCAM-Los Jerónimos
    Estadio Nueva Condomina
    UCAM - Los Jerónimos	1	192	B11-1	Los Rectores-Terra Natura
    Estadio Nueva Condomina
    UCAM - Los Jerónimos	2	172	B11-2	Los Rectores-Terra Natura
    Estadio Nueva Condomina
    Talleres y Cocheras	de 3 a 11	65535	Cocheras	Ninguno
    Los Rectores - Terra Natura	3	195	B7-3	Ninguno
    Desconocido	n/a	1	Desconocido	Ninguno
*/
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
        String auxStr;
        String result = "Esperando lectura...";

        if (infoByte.size()==64) { //1024bits = 64Bytes
            // SALDO Sector11 bloque 0 byte 12 y parte del 13
            auxBytes = new byte[]{infoByte.get(44)[12], infoByte.get(44)[13]};
            result = "Saldo Actual: " + bytesToHexString(auxBytes);

            auxBytes = new byte[]{infoByte.get(44)[1], infoByte.get(44)[2], infoByte.get(44)[3]};
            result = result + "\nÚltima parada: "+ bytesToHexString(auxBytes);

            auxBytes = new byte[]{infoByte.get(44)[6], infoByte.get(44)[7]};
            result = result + "\nFecha de última parada: "+ bytesToHexString(auxBytes);

            auxBytes = new byte[]{infoByte.get(44)[4], infoByte.get(44)[5]};
            result = result + "\nNº de Viajeros: "+ bytesToHexString(auxBytes);
        }
        return result;
    }

    public String getCardData(){

        byte[] auxBytes;
        String auxStr;
        String result = "Esperando lectura...";

        if (infoByte.size()==64) {
            // Nº de tarjeta Sector1 bloque 0 byte 0,1,2 y 3
            auxBytes = new byte[]{infoByte.get(4)[0], infoByte.get(4)[1], infoByte.get(4)[2], infoByte.get(4)[3]};
            result = "N º de Tarjeta: " + bytesToHexString(auxBytes);

            //tipo de tarjeta sector 1 bloque 0 byte 4
            auxBytes = new byte[]{infoByte.get(4)[4]};
            auxStr = bytesToHexString(auxBytes);
            if (auxStr.equals("41")) {
                auxStr = "Anónima-c";
            }
            if (auxStr.equals("45")) {
                auxStr = "Anónima-p";
            }
            if (auxStr.equals("4d")) {
                auxStr = "General";
            }
            if (auxStr.equals("51")) {
                auxStr = "Joven";
            }
            if (auxStr.equals("49")) {
                auxStr = "Estudiante";
            }
            if (auxStr.equals("5d")) {
                auxStr = "Campus";
            }
            if (auxStr.equals("61")) {
                auxStr = "fngu";
            }
            result = result + "\nTipo de tarjeta: " + auxStr;

            //Propietario sector 1 bloque 0 byte 5
            auxBytes = new byte[]{infoByte.get(4)[5]};
            auxStr = bytesToHexString(auxBytes);
            if (auxStr.equals("01")) {
                auxStr = "EPT";
            }
            if (auxStr.equals("02")) {
                auxStr = "TDM";
            }
            result = result + "\nPropietario: " + auxStr;

            //FEcha de Emisión sector 1 bloque 0 byte 6y7
            auxBytes = new byte[]{infoByte.get(4)[6], infoByte.get(4)[7]};
            auxStr = bytesToHexString(auxBytes);
            //int valor = Integer.decode("0x"+auxStr);
            //Log.v(MainActivity.LOG_TAG, String.format("%d",valor));
            result = result + "\nFecha de Emisión: 0x" + auxStr;

            //FEcha de Caducidad sector 1 bloque 0 byte 8y9
            auxBytes = new byte[]{infoByte.get(4)[8], infoByte.get(4)[9]};
            auxStr = bytesToHexString(auxBytes);
            result = result + "\nFecha de Caducidad: 0x" + auxStr;
        }

        return result;
    }

    public String getMovData(){
        byte[] auxBytes;
        String auxStr;
        String result = "Esperando lectura...";
        if (infoByte.size()==64) {
            result="";
            int[] pos =new int[]{44,45,46,48,49,50,52,53,54,56,57};
            for (int i=0;i<pos.length;i++){
                result = result + "\n"+(i+1)+"º Movimiento";
                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Título: " + bytesToHexString(auxBytes);
                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Operación: "+ bytesToHexString(auxBytes);
                auxBytes = new byte[]{infoByte.get(pos[i])[1], infoByte.get(pos[i])[2], infoByte.get(pos[i])[3]};
                result = result + "\n\t- Fecha de la Acción: "+ bytesToHexString(auxBytes);
                auxBytes = new byte[]{infoByte.get(pos[i])[4], infoByte.get(pos[i])[5]};
                result = result + "\n\t- Nº de Viajeros: "+ bytesToHexString(auxBytes);
                auxBytes = new byte[]{infoByte.get(pos[i])[12], infoByte.get(pos[i])[13]};
                result = result + "\n\t- Saldo final: "+ bytesToHexString(auxBytes);
            }
        }
        return result;
    }

    public String bytesToHexString(byte[] bArray) {
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
}
