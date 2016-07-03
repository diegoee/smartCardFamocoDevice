package com.diegoee.my_app;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Diego on 2/7/16.
 */
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
        String auxStr;
        String result = "Esperando lectura...";

        if (infoByte.size()==64) { //1024bits = 64Bytes
            // SALDO Sector11 bloque 0 byte 12 y parte del 13
            auxBytes = new byte[]{infoByte.get(11)[12], infoByte.get(11)[13]};
            result = "Saldo Actual: " + bytesToHexString(auxBytes);
            result = result + "\nÚltima parada: ";
            result = result + "\nFecha de última parada: ";
            result = result + "\nNº de Viajeros: ";
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
                result = result + "\n"+i+" Movimiento";
                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Título: " + bytesToHexString(auxBytes);
                result = result + "\n\t- Operación: ";
                result = result + "\n\t- Fecha de la Acción: ";
                result = result + "\n\t- Nº de Viajeros: ";
                result = result + "\n\t- Saldo final: ";
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
