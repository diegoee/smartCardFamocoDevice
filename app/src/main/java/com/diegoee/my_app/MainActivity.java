package com.diegoee.my_app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import com.famoco.secommunication.SmartcardReader;

public class MainActivity extends Activity{

    private static final String LOG_TAG = "log_app";
    private final static byte[][] KEYS_A_B = {
            new byte[]{(byte) 0x1F, (byte) 0x71, (byte) 0x12, (byte) 0x24 ,(byte) 0x84, (byte) 0xC1},
            new byte[]{(byte) 0x3B, (byte) 0xE5, (byte) 0x33, (byte) 0x10 ,(byte) 0x68, (byte) 0x2A}
    };

    Button button;
    TextView text;
    Context context;
    NfcAdapter mNfcAdapter;
    SensorManager mSensorManager;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] intentFiltersArray;

    String show;
    byte[] key_SAM;
    boolean isDeviceAbleToRunSmartcardReader;


    //FAMOCO
    private SmartcardReader mSmartcardReader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init varibale
        context = this;
        show = "";
        isDeviceAbleToRunSmartcardReader=true;


        //adding component
        button = (Button) findViewById(R.id.b1);
        button.setText("Click: Reset app \nLongClick: clean screen");
        text = (TextView) findViewById(R.id.tv1);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //adding Action
        addButton1(button);


        // INTENT management
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        intentFiltersArray = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        // adding methods
        if (mNfcAdapter != null) {
            show = "NFC enabled.";
        } else {
            show = "NFC NOT enabled.";
        }

        try {
                        /*
            //List sensor
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
            show = show + "\n\nSENSORS:";
            if (deviceSensors.size() == 0) {
                show = show + "\nNo Sensor found";
            } else {
                for (int i = 0; i < deviceSensors.size(); i++) {
                    show = show + "\n" + deviceSensors.get(i).toString();
                }
            }
            */

            // obtain smartcard reader instance
            mSmartcardReader = SmartcardReader.getInstance();
            // open smartcard reader.
            mSmartcardReader.openReader();
            // power on smartcard reader
            show = show + "\n\nSmartCart:";
            if (mSmartcardReader.isCardPresent()) {
                byte[] atr = mSmartcardReader.powerOn();
                show = show + "\n\tATR = " + bytesToHexString(atr);
                Log.v(LOG_TAG, "ATR = " + bytesToHexString(atr));
            } else {
                show = show + "\n\tNo  SmartCart";
            }

            show = show + "\nExample:";
            byte[] val = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x00};
            sendApdu(val,isDeviceAbleToRunSmartcardReader);
        }catch (Exception e){
            isDeviceAbleToRunSmartcardReader=false;
            Log.v(LOG_TAG,"NO famoco librery running");
            show = show + "\nNO famoco librery running";
        }

        //Bono BIColor   ->ID = , (byte)0xBC, (byte)0xB1, (byte)0x34, (byte)0x1B
        //key A + ACs + Key B ->1F71122484C1+11223344+3BE53310682A
        //Bono MONOColor ->ID = , (byte)0x62, (byte)0xED, (byte)0x4A, (byte)0x92

        //Log.v(LOG_TAG,"1:");
        //val = new byte[]{(byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x00, (byte)0x00};
        //sendApdu(val);

        //Log.v(LOG_TAG,"2:");
        //val = new byte[]{(byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x00, (byte)0x08
        //        , (byte)0xF0,(byte)0x00, (byte)0x00 , (byte)0x00, (byte)0x00, (byte)0x46, (byte)0x52, (byte)0x4D
        //        , (byte)0x00};
        //sendApdu(val);


        //Respond:
        //6A82 - file not found
        //6A86 - Incorrect P1or P2 parameter
        //6D00 - Command (instruction) not supported
        //6700 - Length incorrect.
        //6E00 - Class not supported.

        //Display result
        text.setText(show);


    }

    private boolean sendApdu(byte[] apdu,boolean exe) {
        if (exe) {
            // send APDU
            //Log.v(LOG_TAG,"APDU => " + bytesToHexString(apdu));
            show = show + "\nAPDU => " + bytesToHexString(apdu);
            key_SAM = mSmartcardReader.sendApdu(apdu);
            //Log.v(LOG_TAG,"APDU <= " + bytesToHexString(key_SAM));
            show = show + "\nAPDU <= " + bytesToHexString(key_SAM);
        }
        return true;
    }

    private String bytesToHexString(byte[] bArray) {
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

    @Override
    public void onNewIntent(Intent intent) {
        show = "";
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Bundle bundle;
            show = show + intent.toString();
            show = show + "\n\nEXTRAS:";
            bundle = intent.getExtras();
            for (String key : bundle.keySet()) {
                if (key.equals("android.nfc.extra.ID")) {
                    byte [] val = bundle.getByteArray(key);
                    show = show + String.format("\n\t-KEY: %s",key);
                    show = show + String.format("\n\t\t*VALUE: %s",bytesToHexString(val));
                    // TODO: adpdu_cmd
                }else{
                    Object value = bundle.get(key);
                    show = show + String.format("\n\t-KEY: %s",key);
                    show = show + String.format("\n\t\t*VALUE: %s",value.toString());
                }
            }
        }
        if (isDeviceAbleToRunSmartcardReader) {
            show = show + resolveIntent(intent);
        }
        text.setText(show);
    }


    private String resolveIntent(Intent intent) {
        String show = "\n\nDATA:";
        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();
        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 4) Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;
            try {       //  5.1) Connect to card
                mfc.connect();
                boolean auth = false;
                String cardData = null;
                // 5.2) and get the number of sectors this card has..and loop thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;
                for (int j = 0; j < secCount; j++) {// 16 Sectors
                    // 6.1) authenticate the sector
                    auth = mfc.authenticateSectorWithKeyA(j, KEYS_A_B[0]);

                    //if (auth==false) {
                    //    try {
                    //        sendApdu(adpdu_cmd);
                    //        Log.v(LOG_TAG,"key_SAM: "+bytesToHexString(key_SAM));
                      //      auth = mfc.authenticateSectorWithKeyA(j,key_SAM);
                      //  } catch (Exception e) {
                      //      Log.v(LOG_TAG,e.toString());
                      //  }
                    //}

                    if (auth) {
                        show = show +"\n\tblock_(keyA)_"+String.format("%d",j+1)+":";
                        // 6.2) In each sector - get the block count
                        bCount = mfc.getBlockCountInSector(j);
                        bIndex = 0;
                        for (int i = 0; i < bCount; i++) {// 4 Blocks
                            bIndex = mfc.sectorToBlock(j);
                            // 6.3) Read the block
                            data = mfc.readBlock(bIndex);
                            // 7) Convert the data into a string from Hex format.
                            show = show +"\n"+ bytesToHexString(data);
                            bIndex++;
                        }
                    } else {
                        show = show +"\n\tAUTH. FAILED";
                        return show;
                    }
                }
            } catch (IOException e) {
                show = show +"\n"+e.getLocalizedMessage();
                Log.v(LOG_TAG, e.getLocalizedMessage());
            }
        }// End of method
        return show;
    }


    public void addButton1(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                onStart();
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                show="";
                text.setText("screen cleaned");
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, intentFiltersArray, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isDeviceAbleToRunSmartcardReader) {
            // power off smartcard reader
            mSmartcardReader.powerOff();
            // close smartcard reader
            mSmartcardReader.closeReader();
        }
    }


}









