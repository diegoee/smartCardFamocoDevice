package com.diegoee.my_app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class TdmCard {

    public static final String START_STR = "...";

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

    public String getInfoHexByte(){
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
        int startTittle = 0;
        String result = START_STR;
        String aux = "";
        int auxInt=0;

        if (infoByte.size()==64) {

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
            aux = decoData(auxBytes, TdmCard.CTRL_TITTLE_CURRENT);

            if (aux.equals("0")) {
                startTittle = 12;
                auxInt=1;
            }
            if (aux.equals("1")) {
                startTittle = 20;
                auxInt=2;
            }
            if (aux.equals("2")) {
                startTittle = 28;
                auxInt=3;
            }
            if (aux.equals("3")) {
                startTittle = 36;
                auxInt=4;
            }

            result= "\n****** Datos de Títulos ****** \nÚltimo Título en Uso: \n\t"+auxInt ;

            auxBytes = new byte[]{infoByte.get(4)[8], infoByte.get(4)[9]};
            result = result + "\nFecha de Caducidad (Datos de Tarjeta):\n\t" + decoData(auxBytes,TdmCard.CARD_DATE)+"\n";

            int val1,val2;
            for (int ii=0;ii<4;ii++){

                if (ii==0) { startTittle = 12; }
                if (ii==1) { startTittle = 20; }
                if (ii==2) { startTittle = 28; }
                if (ii==3) { startTittle = 36; }
                result = result + "\nTítulo n=" + (ii + 1);

                int i=0;
                auxBytes = new byte[]{infoByte.get(startTittle + i + 5)[0], infoByte.get(startTittle + i + 5)[1], infoByte.get(startTittle + i + 5)[2]};
                val1 = Integer.parseInt(hex2Binary(bytesToHexString(auxBytes)), 2);
                i=1;
                auxBytes = new byte[]{infoByte.get(startTittle + i + 5)[0], infoByte.get(startTittle + i + 5)[1], infoByte.get(startTittle + i + 5)[2]};
                val2 = Integer.parseInt(hex2Binary(bytesToHexString(auxBytes)), 2);

                if (val1<val2){
                    i=0;
                }else{
                    i=1;
                }
                // FIJOS
                result = result + "\n\tFijos: ";

                auxBytes = new byte[]{infoByte.get(startTittle + i + 2)[0], infoByte.get(startTittle + i + 2)[1]};
                result = result + "\n\t\tCódigo Título:\n\t\t\t" + decoData(auxBytes, TdmCard.MAIN_CODE_TITTLE);

                auxBytes = new byte[]{infoByte.get(startTittle + i + 2)[2]};
                result = result + "\n\t\tTipo:\n" +
                        "\t\t\t" + decoData(auxBytes, TdmCard.MAIN_TYPE_TITTLE);

                auxBytes = new byte[]{infoByte.get(startTittle + i + 5)[5], infoByte.get(startTittle + i + 5)[6], infoByte.get(startTittle + i + 5)[7]};
                result = result + "\n\t\tFecha inicio caducidad:\n" +
                        "\t\t\t" + decoData(auxBytes, TdmCard.MAIN_DATE_EXPIRED);


                auxBytes = new byte[]{infoByte.get(startTittle + i + 2)[8],infoByte.get(startTittle + i + 2)[9]};
                result = result + "\n\t\tNº periodo Caducidad:\n" +
                        "\t\t\t" + decoData(auxBytes, TdmCard.MAIN_N_EXPIRED);

                auxBytes = new byte[]{infoByte.get(startTittle + i + 2)[8],infoByte.get(startTittle + i + 2)[9]};
                result = result + "\n\t\tTipo per. de cad.:\n" +
                        "\t\t\t" + decoData(auxBytes, TdmCard.MAIN_TYPE_EXPIRED);

                result = result + "\n\tVariable: ";

                auxBytes = new byte[]{infoByte.get(startTittle + i + 5)[0], infoByte.get(startTittle + i + 5)[1], infoByte.get(startTittle + i + 5)[2]};
                result = result + "\n\t\tFecha/hora Inicio Viaje:\n" +
                        "\t\t\t" + decoData(auxBytes, TdmCard.MAIN_DATE_TRAVEL);

                auxBytes = new byte[]{infoByte.get(startTittle + i + 5)[4], infoByte.get(startTittle + i + 5)[5]};
                result = result + "\n\t\tÚltima Linea:\n" +
                        "\t\t\t" + decoData(auxBytes, TdmCard.MAIN_LASTLINE);

                auxBytes = new byte[]{infoByte.get(startTittle + i + 5)[10], infoByte.get(startTittle + i + 5)[11]};
                result = result + "\n\t\tViajeros:\n" +
                        "\t\t\t" + decoData(auxBytes, TdmCard.MAIN_TRAVELLER);


                result = result + "\n\tSaldo: ";
                auxBytes = new byte[]{
                        infoByte.get(startTittle + i)[0],
                };

                result = result + "\n\t\tViajes o Monedero:\n" +
                        "\t\t" + decoData(auxBytes, TdmCard.MAIN_CAST);

                result = result + "\n";
            }

        }
        return result;
    }

    public String getCtrlData(){

        byte[] auxBytes;
        int selSector = 0;
        String result = START_STR;
        if (infoByte.size()==64) {
            if(Arrays.equals(infoByte.get(8),infoByte.get(9))){
                selSector=8;
            }
            if(Arrays.equals(infoByte.get(9),infoByte.get(10))){
                selSector=9;
            }
            if(Arrays.equals(infoByte.get(8),infoByte.get(10))){
                selSector=8;
            }

            auxBytes = new byte[]{infoByte.get(selSector)[0], infoByte.get(selSector)[1]};
            result = "Número de Transacción:\n\t"+ decoData(auxBytes,TdmCard.CTRL_NUMBER);

            auxBytes = new byte[]{infoByte.get(selSector)[2]};
            result = result + "\nMovimiento Actual:\n\t"+ decoData(auxBytes,TdmCard.CTRL_MOV);

            auxBytes = new byte[]{infoByte.get(selSector)[3]};
            result = result + "\nTitulo Activo:\n\t" + decoData(auxBytes,TdmCard.CTRL_TITTLE);

            auxBytes = new byte[]{infoByte.get(selSector)[3]};
            result = result + "\nÚltimo Título en Uso:\n\t" + decoData(auxBytes,TdmCard.CTRL_TITTLE_CURRENT);

            auxBytes = new byte[]{infoByte.get(selSector)[3]};
            result = result + "\nRegitro de fin de transacción:\n\t" + decoData(auxBytes,TdmCard.CTRL_FLAG_END);;

            auxBytes = new byte[]{infoByte.get(selSector)[3]};
            result = result + "\nCopia en historia:\n\t" + decoData(auxBytes,TdmCard.CTRL_FLAG_HIS);;

            auxBytes = new byte[]{infoByte.get(selSector)[4]};
            result = result + "\nPrioridades:\n\t(Hex.)" + bytesToHexString(auxBytes)+"  -  "+ decoData(auxBytes,TdmCard.CTRL_PRIO);

            auxBytes = new byte[]{infoByte.get(selSector)[5]};
            result = result + "\nOrden de Uso:\n\t" + decoData(auxBytes,TdmCard.CTRL_ORDER);

            auxBytes = new byte[]{infoByte.get(selSector)[7]};
            result = result + "\nTítulo Propio:\n\t" + decoData(auxBytes,TdmCard.CTRL_TITTLE_OWN);

            auxBytes = new byte[]{infoByte.get(selSector)[8],infoByte.get(selSector)[9]};
            result = result + "\nIdentificador de última recarga:\n\t" + decoData(auxBytes,TdmCard.CTRL_ID_RECHARGE);

            auxBytes = new byte[]{infoByte.get(selSector)[10],infoByte.get(selSector)[11]};
            result = result + "\nFecha Anulación/Recuperación:\n\t" + decoData(auxBytes,TdmCard.CTRL_DATE);
        }

        return result;
    }

    public String getCardData(){

        byte[] auxBytes;
        String result = START_STR;

        if (infoByte.size()==64) {
            // Nº de tarjeta Sector1 bloque 0 byte 0,1,2 y 3
            auxBytes = new byte[]{infoByte.get(4)[0], infoByte.get(4)[1], infoByte.get(4)[2], infoByte.get(4)[3]};
            result = "Número de Tarjeta:\n\t(Hex.)" + bytesToHexString(auxBytes)+"  -  "+ decoData(auxBytes,TdmCard.CARD_NUMBER);

            //tipo de tarjeta sector 1 bloque 0 byte 4
            auxBytes = new byte[]{infoByte.get(4)[4]};
            result = result + "\nTipo de tarjeta:\n\t" + decoData(auxBytes,TdmCard.CARD_TYPE);

            //Propietario sector 1 bloque 0 byte 5
            auxBytes = new byte[]{infoByte.get(4)[5]};
            result = result + "\nPropietario:\n\t" + decoData(auxBytes,TdmCard.CARD_OWNER);

            //FEcha de Emisión sector 1 bloque 0 byte 6y7
            auxBytes = new byte[]{infoByte.get(4)[6], infoByte.get(4)[7]};
            result = result + "\nFecha de Emisión:\n\t" + decoData(auxBytes,TdmCard.CARD_DATE);

            //FEcha de Caducidad sector 1 bloque 0 byte 8y9
            auxBytes = new byte[]{infoByte.get(4)[8], infoByte.get(4)[9]};
            result = result + "\nFecha de Caducidad:\n\t" + decoData(auxBytes,TdmCard.CARD_DATE);
        }

        return result;
    }

    public String getMovData(){
        byte[] auxBytes;
        String result = START_STR;

        class Mov implements Comparable<Mov> {
            int date;
            String str;

            public Mov(int date, String str) {
                this.date = date;
                this.str = str;
            }

            @Override
            public int compareTo(Mov o) {
                return date < o.date ? -1 : date > o.date ? 1 : 0;
            }
        }

        List<Mov> movList = new ArrayList<Mov>();
        int auxInt;

        if (infoByte.size()==64) {
            result="";
            int[] pos =new int[]{44,45,46,48,49,50,52,53,54,56,57};
            for (int i=0;i<pos.length;i++){
                result = (i+1)+"º  ******Movimiento******:";

                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Títulos: " + decoData(auxBytes,TdmCard.MOV_TITTLE);

                auxBytes = new byte[]{infoByte.get(pos[i])[0]};
                result = result + "\n\t- Operación: "+ decoData(auxBytes,TdmCard.MOV_OPER);

                auxBytes = new byte[]{infoByte.get(pos[i])[1], infoByte.get(pos[i])[2], infoByte.get(pos[i])[3]};
                result = result + "\n\t- Fecha/hora: "+ decoData(auxBytes,TdmCard.MOV_DATE);

                auxInt=hex2decimal(bytesToHexString(auxBytes));

                auxBytes = new byte[]{infoByte.get(pos[i])[4], infoByte.get(pos[i])[5]};
                result = result + "\n\t- Viajeros: "+ decoData(auxBytes,TdmCard.MOV_TRAVELER);

                auxBytes = new byte[]{infoByte.get(i)[8], infoByte.get(pos[i])[9]};
                result = result + "\n\t- Última Linea: "+decoData(auxBytes,TdmCard.MOV_LASTLINE);

                auxBytes = new byte[]{infoByte.get(i)[8], infoByte.get(pos[i])[9]};
                result = result + "\n\t- Sentido: "+decoData(auxBytes,TdmCard.MOV_WAY);

                auxBytes = new byte[]{infoByte.get(i)[8], infoByte.get(pos[i])[9]};
                result = result + "\n\t- Parada: "+decoData(auxBytes,TdmCard.MOV_STOP);

                auxBytes = new byte[]{infoByte.get(pos[i])[12], infoByte.get(pos[i])[13]};
                result = result + "\n\t- Saldo Final: "+ decoData(auxBytes,TdmCard.MOV_CAST);

                movList.add(new Mov(auxInt,result));

            }

            Collections.sort(movList, Collections.reverseOrder());

            result="Movimientos Ordenados por fecha:\n";
            for (Mov s : movList){
                result =result+s.str+"\n";
            }

        }
        return result;
    }

    private static final int MOV_TITTLE=1;
    private static final int MOV_OPER=2;
    private static final int MOV_DATE=3;
    private static final int MOV_TRAVELER=4;
    private static final int MOV_LASTLINE=5;
    private static final int MOV_WAY=6;
    private static final int MOV_STOP=7;
    private static final int MOV_CAST=8;

    private static final int CARD_NUMBER=9;
    private static final int CARD_TYPE=10;
    private static final int CARD_OWNER=11;
    private static final int CARD_DATE=12;

    private static final int CTRL_NUMBER=13;
    private static final int CTRL_MOV=14;
    private static final int CTRL_TITTLE=15;
    private static final int CTRL_TITTLE_CURRENT=16;
    private static final int CTRL_FLAG_END=17;
    private static final int CTRL_FLAG_HIS=18;
    private static final int CTRL_PRIO=19;
    private static final int CTRL_ORDER=20;
    private static final int CTRL_TITTLE_OWN =21;
    private static final int CTRL_ID_RECHARGE=22;
    private static final int CTRL_DATE=23;

    private static final int MAIN_CODE_TITTLE=24;
    private static final int MAIN_TYPE_TITTLE=25;
    private static final int MAIN_DATE_EXPIRED=26;
    private static final int MAIN_N_EXPIRED=27;
    private static final int MAIN_TYPE_EXPIRED=28;
    private static final int MAIN_DATE_TRAVEL=29;
    private static final int MAIN_LASTLINE=30;
    private static final int MAIN_TRAVELLER=31;
    private static final int MAIN_CAST=32;

    public static String decoData(byte[] bArray, int type) {
        String val = "";
        int auxInt;

        // ----------------------------PRINCIPAL
        if (type==TdmCard.MAIN_CODE_TITTLE)
        {
            val=String.format("%d",hex2decimal(bytesToHexString(bArray)));
            if (val.equals("0")){ val="0"; }
            if (val.equals("100")){ val="Billete sencillo zona urbana"; }
            if (val.equals("103")){ val="Billete sencillo zona interurbana"; }
            if (val.equals("104")){ val="Sin billete"; }
            if (val.equals("110")){ val="Bono 10 CARTON"; }
            if (val.equals("111")){ val="Bono 10 PVC"; }
            if (val.equals("116")){ val="Bono 100"; }
            if (val.equals("117")){ val="Unibono General"; }
            if (val.equals("120")){ val="Estudiante Universitario"; }
            if (val.equals("121")){ val="Bono Campus"; }
            if (val.equals("122")){ val="FNE (familia numerosa especial)"; }
            if (val.equals("123")){ val="FNG (familia numerosa general)"; }
            if (val.equals("901")||val.equals("902")){ val="Sanción"; }
            if (val.equals("903")){ val="Empleado TDM"; }
            if (val.equals("904")){ val="Bono 2"; }
            if (val.equals("905")){ val="Bono Evento"; }
            if (val.equals("906")){ val="Devolución de viajes"; }
            if (val.equals("907")){ val="Devolución de días"; }
            if (val.equals("2501")){ val="Bono Urbano"; }
            if (val.equals("2511")){ val="B100 Ayuntamiento"; }
            if (val.equals("2521")){ val="FNE Ayuntamiento"; }
            if (val.equals("2531")){ val="FNG Ayuntamiento"; }
            if (val.equals("2551")){ val="Bono Murcia Estudiante"; }

        }
        if (type==TdmCard.MAIN_N_EXPIRED) {
            val = hex2Binary(bytesToHexString(bArray)).substring(0, 6);
            val = String.format("%d", Integer.parseInt(val, 2));
        }

        if (type==TdmCard.MAIN_TYPE_EXPIRED) {
            val = hex2Binary(bytesToHexString(bArray)).substring(6, 12);
            val = String.format("%d", Integer.parseInt(val, 2));
            if (val.equals("2")){
                val="Día(s)";
            }
            if (val.equals("0")){
                val="Año(s)";
            }
        }

        if ((type==TdmCard.MAIN_TRAVELLER)){
            val = hex2Binary(bytesToHexString(bArray)).substring(4, 10);
            //Log.v(MainActivity.LOG_TAG,bytesToHexString(bArray));
            //Log.v(MainActivity.LOG_TAG,hex2Binary(bytesToHexString(bArray)));
            //Log.v(MainActivity.LOG_TAG,hex2Binary(bytesToHexString(bArray)).substring(4, 10));
            val = String.format("%d", Integer.parseInt(val, 2));
        }


        if (type==TdmCard.MAIN_TYPE_TITTLE) {
            val = bytesToHexString(bArray).substring(0,1);
            if (val.equals("1")||val.equals("0")) {
                val = "Viajes";
            }else if (val.equals("2")) {
                val = "Tiempo";
            }else if (val.equals("3")) {
                val = "Monedero";
            }else{
                val = "(Hex.) "+val;
            }
        }

        if ((type==TdmCard.MAIN_CAST)){
            //TODO
            val = bytesToHexString(bArray);
            val = hex2Binary(bytesToHexString(bArray));
            //val = String.format("%d", Integer.parseInt(val, 2));
            val = String.format("%.2f", ((double) hex2decimal(bytesToHexString(bArray)))/100)+" €";
        }

        // ----------------------------HISTORICO DE TARJETA
        if ((type==TdmCard.MOV_TITTLE)||(type==TdmCard.CTRL_TITTLE)||(type==TdmCard.CTRL_MOV)){
            val = bytesToHexString(bArray).substring(0,1);
            if (val.equals("0")) { val = "Ninguno";  }
            if (val.equals("1")) { val = "4";  }
            if (val.equals("2")) { val = "3";  }
            if (val.equals("3")) { val = "3 y 4";  }
            if (val.equals("4")) { val = "2";  }
            if (val.equals("5")) { val = "2 y 4";  }
            if (val.equals("6")) { val = "2 y 3";  }
            if (val.equals("7")) { val = "2,3 y 4";  }
            if (val.equals("8")) { val = "1";  }
            if (val.equals("9")) { val = "1 y 4";  }
            if (val.equals("A")) { val = "1 y 3";  }
            if (val.equals("B")) { val = "1,3 y 4";  }
            if (val.equals("C")) { val = "1 y 2";  }
            if (val.equals("D")) { val = "1,2 y 4";  }
            if (val.equals("E")) { val = "1,2 y 3";  }
            if (val.equals("F")) { val = "1,2,3 y 4";  }
        }

        if (type==TdmCard.MOV_OPER){
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

        if ((type==TdmCard.CARD_DATE)||(type==TdmCard.MOV_DATE)
                ||(type==TdmCard.CTRL_DATE)||(type==TdmCard.MAIN_DATE_EXPIRED)
                ||(type==TdmCard.MAIN_DATE_TRAVEL)){
            Calendar c1 = GregorianCalendar.getInstance();
            c1.set(2000, Calendar.JANUARY, 1,0,0,0);
            SimpleDateFormat format1;
            if ((type==TdmCard.CARD_DATE)||(type==TdmCard.CTRL_DATE)){
                c1.add(Calendar.DAY_OF_YEAR, hex2decimal(bytesToHexString(bArray)));
                format1 = new SimpleDateFormat("dd-MM-yyyy");
            }else{
                c1.add(Calendar.MINUTE, hex2decimal(bytesToHexString(bArray)));
                format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            }

            val=format1.format(c1.getTime());
        }

        if (type==TdmCard.MOV_TRAVELER) {
            val = hex2Binary(bytesToHexString(bArray)).substring(5,10);
            val = String.format("%d",Integer.parseInt(val,2));
        }

        if ((type==TdmCard.MOV_LASTLINE)||(type==TdmCard.MAIN_LASTLINE)) {
            val = hex2Binary(bytesToHexString(bArray)).substring(0,14);
            val = String.format("%d",Integer.parseInt(val,2));
        }

        if (type==TdmCard.MOV_WAY) {
            val = hex2Binary(bytesToHexString(bArray)).substring(14,16);

            val = String.format("%d",Integer.parseInt(val,2));
            if (val.equals("1")) {
                val = "Ida";
            }else if (val.equals("2")) {
                val = "Vuelta";
            }else {
                val = "identificar: " + val;
            }
        }

        if (type==TdmCard.MOV_STOP) {
            //Log.v(MainActivity.LOG_TAG,"STATION0: " + bytesToHexString(bArray));
            //Log.v(MainActivity.LOG_TAG,"STATION1: " + hex2decimal(bytesToHexString(bArray)));
            auxInt = ID_STATION.lastIndexOf(hex2decimal(bytesToHexString(bArray)));
            //Log.v(MainActivity.LOG_TAG,"STATION2: " + auxInt);
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

        if (type==TdmCard.MOV_CAST){
            val = String.format("%.2f", ((double) hex2decimal(bytesToHexString(bArray)))/100)+" €";
        }


        // ----------------------------DATOS DE TARJETA
        if ((type==TdmCard.CARD_NUMBER)||(type==TdmCard.CTRL_NUMBER)
                ||(type==TdmCard.CTRL_PRIO)||(type==TdmCard.CTRL_ORDER)
                ||(type==TdmCard.CTRL_ID_RECHARGE))
        {
            val=String.format("%d",hex2decimal(bytesToHexString(bArray)));
        }

        if (type==TdmCard.CARD_TYPE){
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

        if (type==TdmCard.CARD_OWNER){
            val = bytesToHexString(bArray);
            if (val.equals("01")) {
                val = "EPT";
            }
            if (val.equals("02")){
                val = "TDM";
            }
        }


        // ----------------------------DATOS DE CONTROL
        if (type==TdmCard.CTRL_TITTLE_CURRENT){
            val = hex2Binary(bytesToHexString(bArray)).substring(4,6);
            val = String.format("%d",Integer.parseInt(val,2));
        }

        if ((type==TdmCard.CTRL_FLAG_END)||(type==TdmCard.CTRL_FLAG_HIS)){
            val = bytesToHexString(bArray).substring(1,2);
            if (type==TdmCard.CTRL_FLAG_HIS) {
                if (val.equals("0")) { val = "NO"; }
                if (val.equals("1")) { val = "SI"; }
                if (val.equals("2")) { val = "NO"; }
                if (val.equals("3")) { val = "SI"; }
                if (val.equals("4")) { val = "NO"; }
                if (val.equals("5")) { val = "SI"; }
                if (val.equals("6")) { val = "NO"; }
                if (val.equals("7")) { val = "SI"; }
                if (val.equals("8")) { val = "NO"; }
                if (val.equals("9")) { val = "SI"; }
                if (val.equals("A")) { val = "NO"; }
                if (val.equals("B")) { val = "SI"; }
                if (val.equals("C")) { val = "NO"; }
                if (val.equals("D")) { val = "SI"; }
                if (val.equals("E")) { val = "NO"; }
                if (val.equals("F")) { val = "SI"; }
            }
            if (type==TdmCard.CTRL_FLAG_END) {
                if (val.equals("0")) { val = "NO"; }
                if (val.equals("1")) { val = "NO"; }
                if (val.equals("2")) { val = "SI"; }
                if (val.equals("3")) { val = "SI"; }
                if (val.equals("4")) { val = "NO"; }
                if (val.equals("5")) { val = "NO"; }
                if (val.equals("6")) { val = "SI"; }
                if (val.equals("7")) { val = "SI"; }
                if (val.equals("8")) { val = "NO"; }
                if (val.equals("9")) { val = "NO"; }
                if (val.equals("A")) { val = "SI"; }
                if (val.equals("B")) { val = "SI"; }
                if (val.equals("C")) { val = "NO"; }
                if (val.equals("D")) { val = "NO"; }
                if (val.equals("E")) { val = "SI"; }
                if (val.equals("F")) { val = "SI"; }
            }
        }

        if (type==TdmCard.CTRL_TITTLE_OWN){
            val = hex2Binary(bytesToHexString(bArray)).substring(4,8);
            val = String.format("%d",Integer.parseInt(val,2));
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
