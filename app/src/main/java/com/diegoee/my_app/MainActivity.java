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
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LOG_TAG = "log_app";

    //Claves estáticas:
    private final static byte[][] KEYS_A_B = {
            new byte[]{(byte) 0x1F, (byte) 0x71, (byte) 0x12, (byte) 0x24 ,(byte) 0x84, (byte) 0xC1},
            new byte[]{(byte) 0x3B, (byte) 0xE5, (byte) 0x33, (byte) 0x10 ,(byte) 0x68, (byte) 0x2A}
    };

    private NfcAdapter mNfcAdapter;

    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] intentFiltersArray;
    private NavigationView navigationView;

    private int exit;
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
        exit=1;
        console = "";
        isDeviceAbleToRunSmartcardReader=true;
        tdmCard = new TdmCard();

        //adding component
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //INTENT management
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
        exit=1;

        // adding methods
        if (mNfcAdapter == null) {
            console = console +"\nNFC NO disponible en el dispositivo.";
        }

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
                this.startSAM();

            } else {
                console = console + "\nDispositivo sin Módulo SAM";
            }
        }catch (Exception e){
            console = console+"\n"+e.toString();
        }

        navigationView.getMenu().getItem(0).setChecked(true);
        MainFragment fragment = new MainFragment();
        fragment.setConsole(console);
        fragment.setTdmCard(tdmCard);
        fragment.setLoad(MainFragment.MAIN);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            exit=1;
        } else {
            if (exit==0) {
                super.onBackPressed();
            }else{
                exit--;
                Toast.makeText(this,getText(R.string.Toast_exit).toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        boolean fragmentTransaction = false;
        MainFragment fragment = new MainFragment();
        fragment.setConsole(console);
        fragment.setTdmCard(tdmCard);

        if (id == R.id.nav_main) {
            fragment.setLoad(MainFragment.MAIN);
            fragmentTransaction = true;
        } else if (id == R.id.nav_detail_mov) {
            fragment.setLoad(MainFragment.DETAIL_MOV);
            fragmentTransaction = true;
        } else if (id == R.id.nav_detail_card) {
            fragment.setLoad(MainFragment.DETAIL_CARD);
            fragmentTransaction = true;
        } else if (id == R.id.nav_detail_ctrl) {
            fragment.setLoad(MainFragment.DETAIL_CTRL);
            fragmentTransaction = true;
        } else if (id == R.id.nav_contact) {
            fragment.setLoad(MainFragment.CONTACT);
            fragmentTransaction = true;
        }

        if(fragmentTransaction) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNewIntent(Intent intent) {
        console = "";
        byte [] uid = new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        Bundle bundle = intent.getExtras();
        console = console + "Tarjeta descubierta ->";
        for (String key : bundle.keySet()) {
            if (key.equals("android.nfc.extra.ID")) {
                uid = bundle.getByteArray(key);
                console = console + String.format(" ID-NFC: %s",TdmCard.bytesToHexString(uid));
            }
        }

        console = console +"\n"+resolveIntent(intent,uid);

        navigationView.getMenu().getItem(0).setChecked(true);

        MainFragment fragment = new MainFragment();
        fragment.setConsole(console);
        fragment.setTdmCard(tdmCard);
        fragment.setLoad(MainFragment.MAIN);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private String resolveIntent(Intent intent, byte [] uid) {
        String cons = "";
        tdmCard.eraseInfo();

        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();

        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) { //ACTION_TECH_DISCOVERED
            try {
                //  3) Get an instance of the TAG from the NfcAdapter
                Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] data;
                // 4) Get an instance of the Mifare classic card from this TAG intent
                MifareClassic mfc = MifareClassic.get(tagFromIntent);

                // 5.1) Connect to card
                mfc.connect();
                boolean auth = false;

                // 5.2) and get the number of sectors this card has..and loop thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;

                key_SAM = this.getKeysFromSAM(uid);
                auth = mfc.authenticateSectorWithKeyA(0,key_SAM);

                for (int j = 0; j < secCount; j++) {// 16 Sectors
                    // 6.1) authenticate the sector
                    auth = mfc.authenticateSectorWithKeyA(j,key_SAM);
                    cons = cons +"\nSECTOR_"+j+" Auth="+auth+" KEY_A_USED="+tdmCard.bytesToHexString(key_SAM);

                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = 0;
                    for (int i = 0; i < bCount; i++) {// 4 Blocks
                        bIndex = mfc.sectorToBlock(j);
                        // 6.3) Read the block
                        data = mfc.readBlock(bIndex+i);
                        // 7) Convert the data into a string from Hex format.
                        //tdmCard.append(data);
                        cons = cons +"\n"+ tdmCard.bytesToHexString(data);
                    }

                    /*
                    if (!auth) {
                        key_SAM = this.getKeysFromSAM(uid);
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
                            // 6.3) Read the block
                            data = mfc.readBlock(bIndex+i);
                            // 7) Convert the data into a string from Hex format.
                            tdmCard.append(data);
                            //cons = cons +"\n"+ tdmCard.bytesToHexString(data);
                        }
                    } else {
                        cons = cons +"\nError de Autentificación";
                        return cons;
                    }
                    */
                }
            } catch (IOException e) {
                cons = cons +"\nIOException Error:";
                cons = cons +"\n"+e.getLocalizedMessage();
                return cons;
            } catch (NullPointerException e) {
                cons = cons +"\nNullPointerException Error:";
                cons = cons +"\n"+e.toString();
                return cons;
            }
            //cons = cons +"\nDatos de la tarjeta leidos.";
            //Descomentar si se quieren ver los Bytes por Sector.
            //cons = cons + "\nDatos en hexadecimal:\n"+tdmCard.getInfoHexByte();
        }else {
            cons = cons +"\nDatos de la NO tarjeta leidos.";
        }
        Log.v(LOG_TAG,cons);
        return cons;
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

    private byte[] getKeysFromSAM(byte [] uid){
        //GET_MIF1K_KEYS --- APDU;
        byte[] key = KEYS_A_B[0];

        byte [] btVers = new byte[]{
                (byte) 0x00
        };

        byte[] apduRequest,apduResponse;

        apduRequest = new byte[]{
                (byte) 0x90,
                (byte) 0x48,
                btVers[0],
                (byte) 0x00,
                (byte) 0x04,
                uid[0],uid[1],uid[2],uid[3]
        };

        String c="-> "+TdmCard.bytesToHexString(apduRequest);
        apduResponse = mSmartcardReader.sendApdu(apduRequest);
        c=c+"\n<- "+TdmCard.bytesToHexString(apduResponse);
        /*
        key = new byte[]{
                apduResponse[6],
                apduResponse[7],
                apduResponse[8],
                apduResponse[9],
                apduResponse[10],
                apduResponse[11]
        };
        */
        //c=c+"\nkey = "+TdmCard.bytesToHexString(key);
        Log.v(LOG_TAG,c);
        return key;
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

    private void startSAM(){

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

    }

}











