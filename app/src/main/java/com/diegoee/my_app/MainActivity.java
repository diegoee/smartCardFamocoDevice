package com.diegoee.my_app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.famoco.secommunication.SmartcardReader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LOG_TAG = "log_app";
    private final static byte[][] KEYS_A_B = {
            new byte[]{(byte) 0x1F, (byte) 0x71, (byte) 0x12, (byte) 0x24 ,(byte) 0x84, (byte) 0xC1},
            new byte[]{(byte) 0x3B, (byte) 0xE5, (byte) 0x33, (byte) 0x10 ,(byte) 0x68, (byte) 0x2A}
    };

    private NfcAdapter mNfcAdapter;

    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] intentFiltersArray;

    private Fragment fragment;
    private String console;
    private byte[] key_SAM;
    private boolean isDeviceAbleToRunSmartcardReader;

    private TdmCard tdmCard;

    //FAMOCO
    private SmartcardReader mSmartcardReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //init varibale
        console = "";
        isDeviceAbleToRunSmartcardReader=true;
        tdmCard = new TdmCard();

        //adding component
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

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

        //init varibale
        console = "Dispositivo Listo para lectura.";
        isDeviceAbleToRunSmartcardReader=true;

        // adding methods
        if (mNfcAdapter != null) {
            console = console +"\nNFC disponible en el dispositivo.";
        } else {
            console = console +"\nNFC NO disponible en el dispositivo.";
        }

        try {
            // obtain smartcard reader instance
            mSmartcardReader = SmartcardReader.getInstance();
            // open smartcard reader.
            mSmartcardReader.openReader();
            // power on smartcard reader

            if (mSmartcardReader.isCardPresent()) {
                console = console + "\nSAM presente.";
                byte[] atr = mSmartcardReader.powerOn();
                //console = console + "\n\tATR = " + bytesToHexString(atr);
                //Log.v(LOG_TAG, "ATR = " + bytesToHexString(atr));
            } else {
                console = console + "\nSAM NO presente";
            }

            //console = console + "\n\nExample:";
            //byte[] val = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x00};
            // sendApdu(val,isDeviceAbleToRunSmartcardReader);
        }catch (Exception e){
            isDeviceAbleToRunSmartcardReader=false;
            //Log.v(LOG_TAG,"NO famoco librery running");
            console = console + "\nNO famoco librery running";
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
        //text.setText(console);

        fragment = new MainFragment(console,tdmCard,MainFragment.MAIN);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean fragmentTransaction = false;
        Fragment fragment = null;

        if (id == R.id.nav_main) {
            fragment = new MainFragment(console,tdmCard,MainFragment.MAIN);
            fragmentTransaction = true;
        } else if (id == R.id.nav_detail_mov) {
            fragment = new MainFragment(console,tdmCard,MainFragment.DETAIL_MOV);
            fragmentTransaction = true;
        } else if (id == R.id.nav_detail_card) {
            fragment = new MainFragment(console,tdmCard,MainFragment.DETAIL_CARD);
            fragmentTransaction = true;
        } else if (id == R.id.nav_detail_ctrl) {
            fragment = new MainFragment(console,tdmCard,MainFragment.DETAIL_CTRL);
            fragmentTransaction = true;
        } else if (id == R.id.nav_setting) {
            Toast.makeText(getApplicationContext(),"Datos Borrados", Toast.LENGTH_SHORT).show();
            tdmCard.eraseInfo();
            onStop();
            onStart();
        } else if (id == R.id.nav_contact) {
            fragment = new MainFragment(console,tdmCard,MainFragment.CONTACT);
            fragmentTransaction = true;
        }

        if(fragmentTransaction) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean sendApdu(byte[] apdu,boolean exe) {
        if (exe) {
            // send APDU
            //Log.v(LOG_TAG,"APDU => " + bytesToHexString(apdu));
            //console = console + "\nAPDU => " + bytesToHexString(apdu);
            key_SAM = mSmartcardReader.sendApdu(apdu);
            //Log.v(LOG_TAG,"APDU <= " + bytesToHexString(key_SAM));
            //console = console + "\nAPDU <= " + bytesToHexString(key_SAM);
        }
        return true;
    }

    @Override
    public void onNewIntent(Intent intent) {
        console = "";
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Bundle bundle;
            console = console + "Tarjeta descubierta";
            //console = console + intent.toString();
            //console = console + "\n\nEXTRAS:";
            bundle = intent.getExtras();
            for (String key : bundle.keySet()) {
                if (key.equals("android.nfc.extra.ID")) {
                    byte [] val = bundle.getByteArray(key);
                    //console = console + String.format("\n\t-KEY: %s",key);
                    console = console + String.format("\n\tID-NFC: %s",tdmCard.bytesToHexString(val));
                }
                //else{
                //    Object value = bundle.get(key);
                //    console = console + String.format("\n\t-KEY: %s",key);
                //    console = console + String.format("\n\t\t*VALUE: %s",value.toString());
                //}
            }
        }
        if (isDeviceAbleToRunSmartcardReader) {
            console = console + resolveIntent(intent);
        }

        console = console + "\n"+tdmCard.getInfo();

        Fragment fragment = new MainFragment(console,tdmCard,MainFragment.MAIN);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private String resolveIntent(Intent intent) {
        String console = "\nLeyendo datos...";
        tdmCard.eraseInfo();
        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();
        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 4) Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;
            try {
                //  5.1) Connect to card
                mfc.connect();
                boolean auth = false;
                String cardData = null;
                // 5.2) and get the number of sectors this card has..and loop thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;
                //console = console+ "\nKey_A";
                for (int j = 0; j < secCount; j++) {// 16 Sectors
                    //console = console +"\n\tSector_"+String.format("%d",j)+":";
                    //Log.v(LOG_TAG,"Sector_"+String.format("%d",j+1));
                    // 6.1) authenticate the sector
                    auth = mfc.authenticateSectorWithKeyA(j, KEYS_A_B[0]);
                    if (auth) {
                        // 6.2) In each sector - get the block count
                        bCount = mfc.getBlockCountInSector(j);
                        bIndex = 0;
                        for (int i = 0; i < bCount; i++) {// 4 Blocks
                            bIndex = mfc.sectorToBlock(j);
                            //Log.v(LOG_TAG, String.format("%d",bIndex+i));
                            // 6.3) Read the block
                            data = mfc.readBlock(bIndex+i);
                            // 7) Convert the data into a string from Hex format.
                            tdmCard.append(data);
                            //console = console +"\n"+ bytesToHexString(data);
                        }
                    } else {
                        console = console +"\n\tError de Autentificación";
                        return console;
                    }
                }

            } catch (IOException e) {
                console = console +"\n"+e.getLocalizedMessage();
                Log.v(LOG_TAG, e.getLocalizedMessage());
                console = console +"\nError en la lectura de los datos. Volver a realizar la lectura.";
                return console;
            }
        }// End of method
        console = console +"\n\tDatos de la tarjeta leidos.";
        return console;
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
