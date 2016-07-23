package com.diegoee.my_app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
    private NavigationView navigationView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getText(R.string.float_button_snackbar).toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                tdmCard.eraseInfo();
                onStop();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //init varibale
        console = "Dispositivo Listo para lectura.";
        isDeviceAbleToRunSmartcardReader=false;

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
            isDeviceAbleToRunSmartcardReader = mSmartcardReader.openReader();
            // power on smartcard reader
            if (isDeviceAbleToRunSmartcardReader) {
                console = console + "\nSAM presente.";
                byte[] atr = mSmartcardReader.powerOn();                this.getKeysFromSAM();

                //console = console + "\nATR = " + tdmCard.bytesToHexString(atr);
                //Log.v(LOG_TAG, "ATR = " + bytesToHexString(atr));
            } else {
                console = console + "\nSAM NO presente";
            }
            // sendApdu(val,isDeviceAbleToRunSmartcardReader);
        }catch (Exception e){
            console = console + "\nNO famoco librery running";
        }


        navigationView.getMenu().getItem(0).setChecked(true);
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

    //TODO: Implemetar método
    private byte[] getKeysFromSAM(){

        byte[] key;
        byte[] apdu = new byte[16];

        console=console+"\n";

        apdu[0]  = (byte) 0x0D; //Longitud de los datos de despues
        apdu[1]  = (byte) 0x00; //APDU - cla
        apdu[2]  = (byte) 0xA4; //APDU - ins
        apdu[3]  = (byte) 0x04; //APDU - P1
        apdu[4]  = (byte) 0x00; //APDU - P2
        apdu[5]  = (byte) 0x08; //APDU - LC: tamaño de los datos
        apdu[6]  = (byte) 0xF0; //APDU - datos: id de la aplicacion a seleccionar
        apdu[7]  = (byte) 0x00; //APDU - datos: id de la aplicacion a seleccionar
        apdu[8]  = (byte) 0x00; //APDU - datos: id de la aplicacion a seleccionar
        apdu[9]  = (byte) 0x00; //APDU - datos: id de la aplicacion a seleccionar
        apdu[10] = (byte) 0x00; //APDU - datos: id de la aplicacion a seleccionar
        apdu[11] = (byte) 0x41; //APDU - datos: id de la aplicacion a seleccionar
        apdu[12] = (byte) 0x59; //APDU - datos: id de la aplicacion a seleccionar
        apdu[13] = (byte) 0x4D; //APDU - datos: id de la aplicacion a seleccionar

        console=console+"\nAPDU => " + tdmCard.bytesToHexString(apdu);
        key = mSmartcardReader.sendApdu(apdu);
        console=console+"\nAPDU <= " + tdmCard.bytesToHexString(key);


        apdu = new byte[16];
        apdu[0]  = (byte) 0x05; //Longitud de los datos de despues!!!!!!!
        apdu[1]  = (byte) 0x90; //APDU - cla
        apdu[2]  = (byte) 0x10; //APDU - ins
        apdu[3]  = (byte) 0x00; //APDU - P1
        apdu[4]  = (byte) 0x00; //APDU - P2
        apdu[5]  = (byte) 0x14; //APDU - LE : longitud de datos a obtener

        console=console+"\nAPDU => " + tdmCard.bytesToHexString(apdu);
        key = mSmartcardReader.sendApdu(apdu);
        console=console+"\nAPDU <= " + tdmCard.bytesToHexString(key);


        apdu = new byte[16];
        byte btVers = (byte) 0x00;
        byte[] uid =  new byte[4];
        uid[0] = (byte) 0xBC;
        uid[1] = (byte) 0xB1;
        uid[2] = (byte) 0x34;
        uid[3] = (byte) 0x1B;

        apdu[0]  = (byte) 0x0A; //Longitud de los datos de despues!!!!!!!
        apdu[1]  = (byte) 0x90; //APDU - cla
        apdu[2]  = (byte) 0x48; //APDU - ins
        apdu[3]  = btVers; //APDU - P1 - version de claves a utilizar
        apdu[4]  = (byte) 0x00; //APDU - P2
        apdu[5]  = (byte) 0x04; //APDU - LC : longitud de datos que se envian
        apdu[6]  = uid[0]; //APDU - datos: valor rh
        apdu[7]  = uid[1]; //APDU - datos: valor rh
        apdu[8]  = uid[2]; //APDU - datos: valor rh
        apdu[9]  = uid[3]; //APDU - datos: valor rh
        apdu[10] = (byte)0x00; //APDU - le, datos de respuesta

        // send APDU
        console=console+"\nAPDU => " + tdmCard.bytesToHexString(apdu);
        key = mSmartcardReader.sendApdu(apdu);
        console=console+"\nAPDU <= " + tdmCard.bytesToHexString(key);

        key=KEYS_A_B[0];

        Log.v(LOG_TAG,console);

        return key;
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

        navigationView.getMenu().getItem(0).setChecked(true);
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
                    key_SAM = KEYS_A_B[0];
                    auth = mfc.authenticateSectorWithKeyA(j,key_SAM);
                    if (!auth) {
                        key_SAM = getKeysFromSAM();
                        if (key_SAM.length==6) {
                            auth = mfc.authenticateSectorWithKeyA(j, key_SAM);
                        }else{
                            auth=false;
                        }
                    }
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
