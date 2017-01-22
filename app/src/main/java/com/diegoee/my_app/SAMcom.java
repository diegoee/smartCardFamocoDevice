package com.diegoee.my_app;

import android.util.Log;

import com.famoco.secommunication.SmartcardReader;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class SAMcom {

    public static final String LOG_TAG = "log_app";

    //Claves estáticas:
    public final static byte[][] KEYS_A_B = {
            new byte[]{(byte) 0x1F, (byte) 0x71, (byte) 0x12, (byte) 0x24 ,(byte) 0x84, (byte) 0xC1},
            new byte[]{(byte) 0x3B, (byte) 0xE5, (byte) 0x33, (byte) 0x10 ,(byte) 0x68, (byte) 0x2A}
    };

    public final static byte[] KEYS_A_Color =
            new byte[]{(byte) 0x0A, (byte) 0x41, (byte) 0x59, (byte) 0x4D ,(byte) 0x55, (byte) 0x52};

    // key_A = true and key_B = false
    public boolean[] ab;

    public byte[][] keys;

    //FAMOCO
    private SmartcardReader mSmartcardReader;

    private boolean isDeviceAbleToRunSmartcardReader;

    public SAMcom(){
        this.isDeviceAbleToRunSmartcardReader=false;
        this.mSmartcardReader=null;
        keys = new byte[15][6];
        ab = new boolean[15];
        this.initVar();
    }

    public void initVar(){
        for (int j = 0; j < 15; j++) {// 15 Sectors
            for (int i = 0; i < 6; i++) {
                this.keys[j][i] =  KEYS_A_B[0][i];
            }
        }
        for (int j = 0; j < 15; j++) {// 15 Sectors
            this.ab[j] =  true;
        }
    }

    public String init(){
        String console="";

        try {
            // obtain smartcard reader instance. Ver uso del librería.
            mSmartcardReader = SmartcardReader.getInstance();
            // open smartcard reader.
            isDeviceAbleToRunSmartcardReader = mSmartcardReader.openReader();
            // power on smartcard reader
            if (isDeviceAbleToRunSmartcardReader) {
                console = console + "\nDispositivo con Módulo SAM";
                //byte[] atr = mSmartcardReader.powerOn();
                //console=console+"\nSAM ->ATR: " + TdmCard.bytesToHexString(atr);
                //init SAM
                mSmartcardReader.powerOn();
                console = console + this.startSAM();

            } else {
                console = console + "\nDispositivo sin Módulo SAM";
            }
        }catch (Exception e){
            console = console+"\n"+e.toString();
        }

        return console;
    }

    public void closeSAM(){
        if (isDeviceAbleToRunSmartcardReader) {
            // power off smartcard reader
            mSmartcardReader.powerOff();
            // close smartcard reader
            mSmartcardReader.closeReader();
        }
    }

    public void setKeysFromSAM(boolean colorCard, byte[] uid, byte[] btVers){
        //GET_MIF1K_KEYS --- APDU;
        this.initVar();

        if (colorCard) {

            byte[] apduRequest, apduResponse;
            byte[] aux;
            int[] pos;
            byte bv = (byte) 0x02;

            if ((Character.toString(TdmCard.bytesToHexString(btVers).charAt(1))).equals("1")){
                bv = (byte) 0x01;
            }
            if ((Character.toString(TdmCard.bytesToHexString(btVers).charAt(1))).equals("2")){
                bv = (byte) 0x02;
            }

            apduRequest = new byte[]{
                    (byte) 0x90,
                    (byte) 0x48,
                    bv,
                    (byte) 0x00,
                    (byte) 0x04,
                    uid[0], uid[1], uid[2], uid[3]
            };

            String c = "-> " + TdmCard.bytesToHexString(apduRequest);
            apduResponse = mSmartcardReader.sendApdu(apduRequest);
            c = c + "\n<- " + TdmCard.bytesToHexString(apduResponse);

            //Log.v(LOG_TAG,c);

            /*

            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[0]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[1]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[2],apduResponse[3],apduResponse[4],
                apduResponse[5],apduResponse[6],apduResponse[7]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[15]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[16],apduResponse[17],apduResponse[18],
                apduResponse[19],apduResponse[20],apduResponse[21]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[85]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[86],apduResponse[87],apduResponse[88],
                apduResponse[89],apduResponse[90],apduResponse[91]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[92]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[93],apduResponse[94],apduResponse[95],
                apduResponse[96],apduResponse[97],apduResponse[98]
            }));
            Log.v(LOG_TAG,TdmCard.bytesToHexString(new byte[]{
                apduResponse[99],apduResponse[100]
            }));
            */


            if (apduResponse[0]==((byte) 0x0E)) {
                pos = new int[]{
                     2,  9, 16, 23, 30,
                    37, 44, 51, 58, 65,
                    72, 79, 86, 93
                };

                for (int i = 0; i < 6; i++) {
                    keys[0][i] =  KEYS_A_Color[i];
                    ab[0] = true;
                }

                //Log.v(LOG_TAG,"Start");
                for (int j = 1; j <= pos.length; j++) {
                    for (int i = 0; i < 6; i++) {
                        keys[j][i] =  apduResponse[pos[j-1] + i];
                    }

                    aux = new byte[]{
                        apduResponse[pos[j-1]-1]
                    };

                    //Log.v(LOG_TAG,Integer.toString(j)+" - "+TdmCard.bytesToHexString(aux));
                    //Log.v(LOG_TAG,Character.toString(TdmCard.bytesToHexString(aux).charAt(0)));
                    //Log.v(LOG_TAG,""+TdmCard.bytesToHexString(aux).charAt(0));

                    if ((Character.toString(TdmCard.bytesToHexString(aux).charAt(0))).equals("0")){
                        //0 - KEY_B
                        this.ab[j] =  true;
                    }else{
                        //4 - KEY_B
                        this.ab[j] =  false;
                    }

                }
            }

            /*
            // - EJEMPLO -
            -> 9048000004BDAAA4E6
            <- 0E01CB08DBC12E5902F7CB2B69587443AF9A48C3ACF444B750F8E9E83F45F29597C353FE462CC23B78656D472A10761D6D08489FC443A9781A499C759288CB164A69C68D7768C60B5BE071E8BFB90CD5EF905368390D8D68C2B599000E1BDA3566595F9000
            Esta es correcta y la interpretación es la siguiente (Viene en el documento de comandos del SAM de GMV)
            0E - 15 Claves devueltas
            01 - Clave A del sector 1
            CB08DBC12E59 - La clave indicada en el byte anterior
            02 - Clave A del sector 2
            F7CB2B695874  - La clave indicada en el byte anterior
            43 - Clave B del sector 3
            AF9A48C3ACF4 - La clave indicada en el byte anterior
            44 - Clave B del sector 4
            B750F8E9E83F   - La clave indicada en el byte anterior
            45 - Clave B del sector 5
            F29597C353FE   - La clave indicada en el byte anterior
            46 - Clave B del sector 6
            2CC23B78656D  - La clave indicada en el byte anterior
            47 - Clave B del sector 7
            2A10761D6D08 - La clave indicada en el byte anterior
            48 - Clave B del sector 8
            9FC443A9781A  - La clave indicada en el byte anterior
            49 - Clave B del sector 9
            9C759288CB16  - La clave indicada en el byte anterior
            4A - Clave B del sector 10
            69C68D7768C6  - La clave indicada en el byte anterior
            0B - Clave A del sector 11
            5BE071E8BFB9  - La clave indicada en el byte anterior
            0C - Clave A del sector 12
            D5EF90536839   - La clave indicada en el byte anterior
            0D - Clave A del sector 13
            8D68C2B59900  - La clave indicada en el byte anterior
            0E - Clave A del sector 14
            1BDA3566595F  - La clave indicada en el byte anterior
            9000 - Respuesta correcta del SAM
            */
        }
    }

    private byte[] tdes(byte[] info, byte[] keyAtr) throws Exception{

        SecretKey key = new SecretKeySpec(keyAtr, "DESede");
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        IvParameterSpec iv = new IvParameterSpec(new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        });
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] encrypted = cipher.doFinal(info);

        return encrypted;
    }

    public String startSAM(){

        String console="";
        int i;

        byte[] apduRequest,apduResponse;

        byte[] btRh,bt80,btVar1,btVar2,btRc,btKs1,btKs2,btKs,btKa,btCh,btCc;


        bt80 = new byte[]{
                (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        btKa = new byte[]{
                (byte) 0x76, (byte) 0x8E, (byte) 0x92, (byte) 0x78,
                (byte) 0x28, (byte) 0x75, (byte) 0xAC, (byte) 0xAC,
                (byte) 0xF6, (byte) 0x8E, (byte) 0x59, (byte) 0x48,
                (byte) 0x04, (byte) 0x5F, (byte) 0xD5, (byte) 0x90
        };
        btVar1  = new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        btVar2  = new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        apduRequest = new byte[]{
                (byte) 0x00,
                (byte) 0xA4,
                (byte) 0x04,
                (byte) 0x00,
                (byte) 0x08,
                (byte) 0xF0,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x41,
                (byte) 0x59,
                (byte) 0x4D,
                (byte) 0x0D
        };

        //console = console + "\nPaso 0: ";
        //console = console + "\n1 - (SAM_SelectSAMApp)";
        //console = console +"\n\t->"+TdmCard.bytesToHexString(apduRequest);
        apduResponse = mSmartcardReader.sendApdu(apduRequest);
        //console = console + "\n\t<-" + TdmCard.bytesToHexString(apduResponse);

        if (("6999").equals(TdmCard.bytesToHexString(apduResponse))){
            console=console+"\nSAM Bloqueada";
            return console;
        }


        apduRequest = new byte[]{
                (byte) 0x90,
                (byte) 0x10,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x14
        };

        //console=console+"\n1.1 - (Opt)(SAM_GetSAMProps)\n\t->" + TdmCard.bytesToHexString(apduRequest);
        apduResponse = mSmartcardReader.sendApdu(apduRequest);
        //console=console+"\n\t<-" + TdmCard.bytesToHexString(apduResponse);

        //console = console + "\n2 - (SAM_Autenticate)";


        //console=console+"\nPaso 1: Initialize Update";
        btRh = new byte[]{
                (byte) 0xA0, (byte) 0x74, (byte) 0x4A, (byte) 0x3C,
                (byte) 0xB5, (byte) 0xF1, (byte) 0x5E, (byte) 0xDE
        };

        //console = console + "\n\t1º Gen. random para Rh";
        SecureRandom csprng = new SecureRandom();
        csprng.nextBytes(btRh);
        //console=console+"\n\tRh  = "+TdmCard.bytesToHexString(btRh);


        apduRequest = new byte[]{
                //(byte) 0xBD, (byte) 0x00,
                (byte) 0x80, (byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x08,
                btRh[0], btRh[1], btRh[2], btRh[3],
                btRh[4], btRh[5], btRh[6], btRh[7]
                //,(byte) 0x10
        };

        //console = console + "\n\t-> " + TdmCard.bytesToHexString(apduRequest);

        apduResponse = mSmartcardReader.sendApdu(apduRequest);
        //console = console + "\n\t<- " + TdmCard.bytesToHexString(apduResponse);

        btRc = new byte[]{
                apduResponse[0],
                apduResponse[1],
                apduResponse[2],
                apduResponse[3],
                apduResponse[4],
                apduResponse[5],
                apduResponse[6],
                apduResponse[7]
        };
        //console=console+"\n\tRc  = "+TdmCard.bytesToHexString(btRc);

        btCc = new byte[]{
                apduResponse[8],
                apduResponse[9],
                apduResponse[10],
                apduResponse[11],
                apduResponse[12],
                apduResponse[13],
                apduResponse[14],
                apduResponse[15]
        };
        //console=console+"\n\tCc  = "+TdmCard.bytesToHexString(btCc);

        btVar1 = new byte[]{
                btRc[4],
                btRc[5],
                btRc[6],
                btRc[7],
                btRh[0],
                btRh[1],
                btRh[2],
                btRh[3]
        };
        try {
            btKs1 = tdes(btVar1, btKa);
        }catch (Exception e){
            console=console+"\n\tError using tdes()";
            btKs1  = new byte[]{
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
        }
        //console=console+"\n\tKs1 = "+TdmCard.bytesToHexString(btVar1)+" TDES Ka = "+TdmCard.bytesToHexString(btKs1);

        btVar1 = new byte[]{
                btRc[0],
                btRc[1],
                btRc[2],
                btRc[3],
                btRh[4],
                btRh[5],
                btRh[6],
                btRh[7]
        };
        try {
            btKs2 = tdes(btVar1, btKa);
        }catch (Exception e){
            console=console+"\n\tError using tdes()";
            btKs2  = new byte[]{
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
        }
        //console=console+"\n\tKs2 = "+TdmCard.bytesToHexString(btVar1)+" TDES Ka = "+TdmCard.bytesToHexString(btKs2);
        btKs  = new byte[]{
                btKs1[0],btKs1[1],btKs1[2],btKs1[3],btKs1[4],btKs1[5],btKs1[6],btKs1[7],
                btKs2[0],btKs2[1],btKs2[2],btKs2[3],btKs2[4],btKs2[5],btKs2[6],btKs2[7]
        };

        //console=console+"\n\tKs = "+TdmCard.bytesToHexString(btKs);

        //console=console+"\n\tCálculo de Cc.";

        try {
            btVar1 = tdes(btRh, btKs);
        }catch (Exception e){
            console=console+"\n\tError using tdes()";
            btVar1  = new byte[]{
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
        }
        //console=console+"\n\tCc'    = "+TdmCard.bytesToHexString(btRh)+" TDES Ks = "+TdmCard.bytesToHexString(btVar1);

        for (i = 0; i < 8; i++)
            btVar2[i] = (byte) (btVar1[i] ^ btRc[i]);

        //console=console+"\n\tCc''   = "+TdmCard.bytesToHexString(btVar1)+" XOR Rc  = "+TdmCard.bytesToHexString(btVar2);

        try {
            btVar1 = tdes(btVar2, btKs);
        }catch (Exception e){
            console=console+"\n\tError using tdes()";
            btVar1  = new byte[]{
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
        }
        //console=console+"\n\tCc'''  = "+TdmCard.bytesToHexString(btVar2)+" TDES Ks = "+TdmCard.bytesToHexString(btVar1);

        for (i = 0; i < 8; i++)
            btVar2[i] = (byte) (btVar1[i] ^ bt80[i]);

        //console=console+"\n\tCc'''' = "+TdmCard.bytesToHexString(btVar1)+" XOR 80... = "+TdmCard.bytesToHexString(btVar2);

        try {
            btVar1 = tdes(btVar2, btKs);
        }catch (Exception e){
            console=console+"\n\tError using tdes()";
            btVar1  = new byte[]{
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
        }
        //console=console+"\n\tCc = "+TdmCard.bytesToHexString(btVar2)+" TDES Ks = "+TdmCard.bytesToHexString(btVar1);
        //console=console+"\n\tCc -> "+TdmCard.bytesToHexString(btVar1)+"  == "+TdmCard.bytesToHexString(btCc);


        //console=console+"\n\tCálculo de Ch.";

        try {
            btVar1 = tdes(btRc, btKs);
        }catch (Exception e){
            console=console+"\n\tError using tdes()";
            btVar1  = new byte[]{
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
        }
        //console=console+"\n\tCh'    = "+TdmCard.bytesToHexString(btRh)+" TDES Ks = "+TdmCard.bytesToHexString(btVar1);

        for (i = 0; i < 8; i++)
            btVar2[i] = (byte) (btVar1[i] ^ btRh[i]);

        //console=console+"\n\tCh''   = "+TdmCard.bytesToHexString(btVar1)+" XOR Kh  = "+TdmCard.bytesToHexString(btVar2);

        try {
            btVar1 = tdes(btVar2, btKs);
        }catch (Exception e){
            console=console+"\n\tError using tdes()";
            btVar1  = new byte[]{
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
        }
        //console=console+"\n\tCh'''  = "+TdmCard.bytesToHexString(btVar2)+" TDES Ks = "+TdmCard.bytesToHexString(btVar1);

        for (i = 0; i < 8; i++)
            btVar2[i] = (byte) (btVar1[i] ^ bt80[i]);

        //console=console+"\n\tCh'''' = "+TdmCard.bytesToHexString(btVar1)+" XOR 80... = "+TdmCard.bytesToHexString(btVar2);

        try {
            btVar1 = tdes(btVar2, btKs);
        }catch (Exception e){
            console=console+"\n\tError using tdes()";
            btVar1  = new byte[]{
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
        }
        btCh = btVar1;
        //console=console+"\n\tCh = "+TdmCard.bytesToHexString(btVar2)+" TDES Ks = "+TdmCard.bytesToHexString(btCh);


        //console=console+"\nPaso 2: External Authenticate";

        apduRequest = new byte[]{
                //(byte) 0xBD, (byte) 0x00,
                (byte) 0x80, (byte) 0x82,(byte) 0x00, (byte) 0x00,(byte) 0x08,
                btVar1[0],btVar1[1],btVar1[2],btVar1[3],
                btVar1[4],btVar1[5],btVar1[6],btVar1[7]
        };
        //console=console+"\n\t-> "+TdmCard.bytesToHexString(apduRequest);


        apduResponse = mSmartcardReader.sendApdu(apduRequest);
        //console=console+"\n\t<- "+TdmCard.bytesToHexString(apduResponse);

        if (("9000").equals(TdmCard.bytesToHexString(apduResponse))) {
            console=console+"\nSAM inicializada correctamente.";
        }else{
            console=console+"\nError Fatal: SAM mal inicializada no volver a ejecutar Aplicación";
        }

        return console;

    }

}
