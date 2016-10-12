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
                //console = console + "\nOpen SAM";
                byte[] atr = mSmartcardReader.powerOn();
                console=console+"\nSAM ->ATR: " + TdmCard.bytesToHexString(atr);

            } else {
                console = console + "\nSAM NO presente";
            }

        }catch (Exception e){
            console = console + e.toString();
        }

        this.getKeysFromSAM();

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

        Bundle bundle = intent.getExtras();
        console = console + "Tarjeta descubierta ->";
        for (String key : bundle.keySet()) {
            if (key.equals("android.nfc.extra.ID")) {
                byte [] val = bundle.getByteArray(key);
                console = console + String.format(" ID-NFC: %s",TdmCard.bytesToHexString(val));
            }
        }

        console = console + resolveIntent(intent);

        navigationView.getMenu().getItem(0).setChecked(true);

        MainFragment fragment = new MainFragment();
        fragment.setConsole(console);
        fragment.setTdmCard(tdmCard);
        fragment.setLoad(MainFragment.MAIN);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private String resolveIntent(Intent intent) {
        String console = "";
        tdmCard.eraseInfo();
        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();
        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) { //ACTION_TECH_DISCOVERED
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] data;
            try {
                // 4) Get an instance of the Mifare classic card from this TAG intent
                MifareClassic mfc = MifareClassic.get(tagFromIntent);
                // 5.1) Connect to card
                mfc.connect();
                boolean auth = false;
                // 5.2) and get the number of sectors this card has..and loop thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;
                //console = console+ "\nKey_A";
                for (int j = 0; j < secCount; j++) {// 16 Sectors
                    // 6.1) authenticate the sector
                    key_SAM = KEYS_A_B[0];
                    auth = mfc.authenticateSectorWithKeyA(j,key_SAM);
                    //TODO: Cuando se tenga implementado: getKeysFromSAM()

                    //if (!auth) {
                    //    //IMPLEMENTAR: key_SAM = getKeysFromSAM();
                    //    if (key_SAM.length==6) {
                    //        auth = mfc.authenticateSectorWithKeyA(j, key_SAM);
                    //    }else{
                    //        auth=false;
                    //    }
                    //}
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
                            //console = console +"\n"+ bytesToHexString(data);
                        }
                    } else {
                        console = console +"\nError de Autentificación";
                        return console;
                    }
                }
            } catch (IOException e) {
                console = console +"\n"+e.getLocalizedMessage();
                Log.v(LOG_TAG, e.getLocalizedMessage());
                console = console +"\nError en la lectura de los datos. Volver a realizar la lectura.";
                return console;
            } catch (NullPointerException e) {
                console = console +"\n"+e.toString();
                Log.v(LOG_TAG,e.toString());
                return console;
            }
            console = console +"\nDatos de la tarjeta leidos.";

            //Descomentar si se quieren ver los Bytes por Sector.
            console = console + "\nDatos en hexadecimal:\n"+tdmCard.getInfoHexByte();

        }else {
            console = console +"\nDatos de la NO tarjeta leidos.";
        }

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

    //TODO: Metodo utilizado en la comunicación con la SAM
    private byte[] tdes(byte[] info, byte[] keyAtr) throws Exception{

        SecretKey key = new SecretKeySpec(keyAtr, "DESede");
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        IvParameterSpec iv= new IvParameterSpec(new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        });
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] encrypted = cipher.doFinal(info);

        return encrypted;
    }
    /*
    public byte[] Encriptar(byte[] message, byte[] btKey)
    {
            byte[] IV;
            byte[] Key;
            UTF8Encoding encoding = new UTF8Encoding();
            TripleDESCryptoServiceProvider criptoProvider = new TripleDESCryptoServiceProvider();
            //IV = criptoProvider.IV;
            IV = new byte[8] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
            //Key = criptoProvider.Key;
            Key = btKey;
            criptoProvider.Mode = CipherMode.CBC;
            criptoProvider.Padding = PaddingMode.None;
            ICryptoTransform criptoTransform = criptoProvider.CreateEncryptor(Key, IV);
            MemoryStream memoryStream = new MemoryStream();
            CryptoStream cryptoStream = new CryptoStream(memoryStream, criptoTransform, CryptoStreamMode.Write);
            cryptoStream.Write(message, 0, message.Length);
            cryptoStream.FlushFinalBlock();
            byte[] encriptado = memoryStream.ToArray();
            return encriptado;
        }
     */

    //TODO: Metodo para comunicar con la SAM y obtener la key para decodificar tarjeta.
    private byte[] getKeysFromSAM() {

        int i;

        byte[] key = KEYS_A_B[0];
        byte[] apduRequest;
        byte[] apduResponse;

        byte[] btRh;
        byte[] btRc;
        byte[] btKs1;
        byte[] btKs2;
        byte[] btKs;
        byte[] btKa;
        byte[] btCh;
        byte[] btCc;

        byte[] btVar;
        byte[] bt80 = new byte[]{
                (byte) 0x80,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00
        };

        try {

            btVar = new byte[]{
                (byte) 0xB4,
                (byte) 0x88,
                (byte) 0x94,
                (byte) 0x4A,
                (byte) 0x4D,
                (byte) 0xC6,
                (byte) 0x03,
                (byte) 0xC2
            };
            btKs = new byte[]{
                (byte) 0x84,
                (byte) 0x5F,
                (byte) 0x19,
                (byte) 0xA0,
                (byte) 0xB4,
                (byte) 0x88,
                (byte) 0x94,
                (byte) 0x4A,
                (byte) 0x22,
                (byte) 0xC1,
                (byte) 0xA4,
                (byte) 0xCE,
                (byte) 0x4D,
                (byte) 0xC6,
                (byte) 0x03,
                (byte) 0xC2
            };

            console=console+"\nRh  = "+TdmCard.bytesToHexString(btVar);
            console=console+"\nKs  = "+TdmCard.bytesToHexString(btKs);
            console=console+"\nRES = tdes(Rh, Ks)";
            btVar = tdes(btVar, btKs);
            console=console+"\nRES = "+TdmCard.bytesToHexString(btVar);
            console=console+"\nSOL = A64DF8232D4E7103";

            /*
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
            console = console + "\n\n**Pre- Autenticación**";
            console = console + "\n1º SAM_SelectSAMApp";
            console = console +"\n->"+TdmCard.bytesToHexString(apduRequest);
            //apduResponse = mSmartcardReader.sendApdu(apduRequest);
            apduResponse = new byte[]{
                    (byte) 0x90,
                    (byte) 0x00
            };
            console = console + "\n<-" + TdmCard.bytesToHexString(apduResponse);


            apduRequest = new byte[]{
                    (byte) 0x90,
                    (byte) 0x10,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x14
            };

            console=console+"\n2º (Opt)SAM_GetSAMProps";
            console=console+"\n->"+TdmCard.bytesToHexString(apduRequest);
            //apduResponse = mSmartcardReader.sendApdu(apduRequest);
            apduResponse = new byte[]{
                    (byte) 0x90,
                    (byte) 0x00
            };
            console=console+"\n<-" + TdmCard.bytesToHexString(apduResponse);


            //1º Generamos un número aleatorio (Rh)
            console = console + "\n\n**Autenticación**";
            console = console + "\n1º Gen. random Rh";
            //SecureRandom csprng = new SecureRandom();
            btRh = new byte[8];
            //csprng.nextBytes(btRh);
            btRh = new byte[]{
                    (byte) 0xB4,
                    (byte) 0x88,
                    (byte) 0x94,
                    (byte) 0x4A,
                    (byte) 0x4D,
                    (byte) 0xC6,
                    (byte) 0x03,
                    (byte) 0xC2
            };

            console = console + "\n\tRh = " + TdmCard.bytesToHexString(btRh);

            //2º Enviado
            apduRequest = new byte[]{
                    (byte) 0x80,
                    (byte) 0x50,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x08,
                    btRh[0],
                    btRh[1],
                    btRh[2],
                    btRh[3],
                    btRh[4],
                    btRh[5],
                    btRh[6],
                    btRh[7]
            };
            console = console + "\n2º Initialize Update";
            console = console + "\n->" + TdmCard.bytesToHexString(apduRequest);
            //apduResponse = mSmartcardReader.sendApdu(apduRequest);
            apduResponse = new byte[]{
                    (byte) 0x22,
                    (byte) 0xC1,
                    (byte) 0xA4,
                    (byte) 0xCE,
                    (byte) 0x84,
                    (byte) 0x5F,
                    (byte) 0x19,
                    (byte) 0xA0,
                    (byte) 0x2F,
                    (byte) 0xED,
                    (byte) 0x52,
                    (byte) 0xBF,
                    (byte) 0x23,
                    (byte) 0xFE,
                    (byte) 0xC3,
                    (byte) 0xA8,
                    (byte) 0x90,
                    (byte) 0x00
            };
            console = console + "\n<-" + TdmCard.bytesToHexString(apduResponse);

            btRc = new byte[8];
            btCc = new byte[8];
            if (apduResponse[apduResponse.length-2]==(byte)0x90) {
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
            }else{
                console = console + "\nError al obtener Respuesta APDU: Initialize Update";
            }

            console = console + "\n\tCc = " + TdmCard.bytesToHexString(btCc);
            console = console + "\n\tRc = " + TdmCard.bytesToHexString(btRc);
            console = console + "\n3º Clave de Sesión";

            btKa = new byte[]{
                    (byte) 0x76,
                    (byte) 0x8E,
                    (byte) 0x92,
                    (byte) 0x78,
                    (byte) 0x28,
                    (byte) 0x75,
                    (byte) 0xAC,
                    (byte) 0xAC,
                    (byte) 0xF6,
                    (byte) 0x8E,
                    (byte) 0x59,
                    (byte) 0x48,
                    (byte) 0x04,
                    (byte) 0x5F,
                    (byte) 0xD5,
                    (byte) 0x90
            };
            console = console + "\n\tKa  = " + TdmCard.bytesToHexString(btKa);

            btVar = new byte[]{
                    btRc[4],
                    btRc[5],
                    btRc[6],
                    btRc[7],
                    btRh[0],
                    btRh[1],
                    btRh[2],
                    btRh[3]
            };
            btKs1 = tdes(btVar, btKa);
            console = console + "\n\tKs1 = Rc2+Rh1 TDES Ka = " + TdmCard.bytesToHexString(btKs1);

            btVar = new byte[]{
                    btRc[0],
                    btRc[1],
                    btRc[2],
                    btRc[3],
                    btRh[4],
                    btRh[5],
                    btRh[6],
                    btRh[7]
            };
            btKs2= tdes(btVar, btKa);
            console = console + "\n\tKs2 = Rc1+Rh2 TDES Ka = " + TdmCard.bytesToHexString(btKs2);

            //Ks = Ks1 + Ks2
            btKs = new byte[]{
                    btKs1[0],
                    btKs1[1],
                    btKs1[2],
                    btKs1[3],
                    btKs1[4],
                    btKs1[5],
                    btKs1[6],
                    btKs1[7],
                    btKs2[0],
                    btKs2[1],
                    btKs2[2],
                    btKs2[3],
                    btKs2[4],
                    btKs2[5],
                    btKs2[6],
                    btKs2[7]
            };
            console = console + "\n\tKs  = Ks1 + Ks2 = " + TdmCard.bytesToHexString(btKs);


            console = console + "\n4º Cálculo de Cc:";

            btVar = tdes(btRh, btKs);
            console = console + "\n\t4.1 Cc'    = Rh     TDES Ks = " + TdmCard.bytesToHexString(btVar);


            for (i = 0; i < 8; i++) {
                btVar[i] = (byte) (btVar[i] ^ btRc[i]);
            }
            console = console + "\n\t4.2 Cc''   = Cc'    XOR  Rc = " + TdmCard.bytesToHexString(btVar);

            btVar = tdes(btVar, btKs);
            console = console + "\n\t4.3 Cc'''  = Cc''   TDES Ks = " + TdmCard.bytesToHexString(btVar);


            for (i = 0; i < 8; i++)
                btVar[i] = (byte) (btVar[i] ^ bt80[i]);
            console = console + "\n\t4.4 Cc'''' = Cc'''  XOR  80 = " + TdmCard.bytesToHexString(btVar);

            btVar = tdes(btVar, btKs);
            console = console + "\n\t4.5 Cc     = Cc'''' TDES Ks = " + TdmCard.bytesToHexString(btVar);

            console = console + "\n\tCc (calculado) = " + TdmCard.bytesToHexString(btVar);
            console = console + "\n\tCc (original)  = " + TdmCard.bytesToHexString(btRc);


            console = console + "\n5º Cálculamos en Ch";
            btVar = tdes(btRc, btKs);
            console = console + "\n\t5.1 Ch'    = Rc     TDES Ks = " + TdmCard.bytesToHexString(btVar);

            for (i = 0; i < 8; i++) {
                btVar[i] = (byte) (btVar[i] ^ btRh[i]);
            }
            console = console + "\n\t5.2 Ch''   = Ch'    XOR  Rh = " + TdmCard.bytesToHexString(btVar);

            btVar = tdes(btVar, btKs);
            console = console + "\n\t5.3 Ch'''  = Ch''   TDES Ks = " + TdmCard.bytesToHexString(btVar);


            for (i = 0; i < 8; i++) {
                btVar[i] = (byte) (btVar[i] ^ bt80[i]);
            }
            //console = console + "\n\t5.4 Ch'''' = Ch'''  XOR  80 = " + TdmCard.bytesToHexString(btVar);


            btCh = tdes(btVar, btKs);
            console = console + "\n\t5.5 Ch     = Ch'''' TDES Ks = " + TdmCard.bytesToHexString(btVar);

            console = console + "\n\tCh = " + TdmCard.bytesToHexString(btCh);

            console = console + "\n6º External authenticate";
            apduRequest = new byte[]{
                    (byte) 0x80,
                    (byte) 0x82,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x08,
                    btCh[0],
                    btCh[1],
                    btCh[2],
                    btCh[3],
                    btCh[4],
                    btCh[5],
                    btCh[6],
                    btCh[7]
            };
            console = console + "\n->" + TdmCard.bytesToHexString(apduRequest);
            //apduResponse = mSmartcardReader.sendApdu(apduRequest);
            apduResponse = new byte[]{
                    (byte) 0x69,
                    (byte) 0x99
            };
            console = console + "\n<-" + TdmCard.bytesToHexString(apduResponse);

        */
        }catch(Exception e){
            console = console + "\n"+ e;

        }
        Log.v(LOG_TAG, console);
        return key;
    }

}

