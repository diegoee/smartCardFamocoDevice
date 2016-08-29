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
/* Framework utilizados para el método: getKeysFromSAM()
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
*/

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
            console = console + "\nNO famoco librery running";
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


    /*Métodos SAM

    //TODO: Metodod utilizado en la comunicación con la SAM
    private byte[] tdes(byte[] info, byte[] keyAtr) throws Exception{

        byte [] aa=keyAtr;
        if (keyAtr.length<=24){
            aa = new byte[]{
                    (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                    (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                    (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                    (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                    (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                    (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00
            };
            for (int i=0;i<keyAtr.length;i++){
                aa[i]=keyAtr[i];
            }
        }

        DESedeKeySpec keyspec = new DESedeKeySpec(aa);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        SecretKey key = keyfactory.generateSecret(keyspec);

        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(info);

        return encrypted;
    }

    //TODO: Metodo para comunicar con laSAM y obtener la key para decodificar tarjeta.
    private byte[] getKeysFromSAM() {

        byte[] key = KEYS_A_B[0];
        byte[] apdu;

        try {
            apdu = new byte[]{
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

            console = console + "\n1 - (SAM_SelectSAMApp)";
            console = console +"\n\t->"+TdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console = console + "\n\t<-" + TdmCard.bytesToHexString(key);


            apdu = new byte[]{
                    (byte) 0x90,
                    (byte) 0x10,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x14
            };

            console=console+"\n1.1 - (Opt)(SAM_GetSAMProps)\n\t->" + TdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console=console+"\n\t<-" + TdmCard.bytesToHexString(key);

            console = console + "\n2 - (SAM_Autenticate)";

            //1º Generamos un número aleatorio (Rh)
            console = console + "\n\t1º Gen. random para Rh";
            SecureRandom csprng = new SecureRandom();
            byte[] btRh = new byte[8];
            csprng.nextBytes(btRh);

            //2º Enviado
            apdu = new byte[]{
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
            console = console + "\n\t2º Initialize Update";
            console = console + "\n\t->" + TdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console = console + "\n\t<-" + TdmCard.bytesToHexString(key);

            byte[] btKa = new byte[]{
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

            //3º obtnemos Rc  por tanto tenemos Rh, Rc y Ka
            console = console + "\n\t3º Rc tenemos Rh, Rc y Ka";
            byte[] btRc;
            if (key[key.length-2]==(byte)0x90) {
                btRc = new byte[]{
                        key[0],
                        key[1],
                        key[2],
                        key[3],
                        key[4],
                        key[5],
                        key[6],
                        key[7]
                };
            }else{
                btRc = new byte[]{
                        (byte) 0x11,
                        (byte) 0x22,
                        (byte) 0x33,
                        (byte) 0x44,
                        (byte) 0x55,
                        (byte) 0x66,
                        (byte) 0x77,
                        (byte) 0x88
                };
            }

            console = console + "\n\t4º calculamos Ks para Ch";
            // 4º calculamos Ks que nos hará falta para calcular Ch
            byte[] aux;
            aux = new byte[]{
                    btRc[4],
                    btRc[5],
                    btRc[6],
                    btRc[7],
                    btRh[0],
                    btRh[1],
                    btRh[2],
                    btRh[3]
            };

            byte[] btKs1 = tdes(aux, btKa);

            aux = new byte[]{
                    btRc[0],
                    btRc[1],
                    btRc[2],
                    btRc[3],
                    btRh[4],
                    btRh[5],
                    btRh[6],
                    btRh[7]
            };

            byte[] btKs2= tdes(aux, btKa);

            //Ks = Ks1 + Ks2
            byte[] btKs = new byte[]{
                    btKs1[0], btKs1[1], btKs1[2], btKs1[3],
                    btKs1[4], btKs1[5], btKs1[6], btKs1[7],
                    btKs2[0], btKs2[1], btKs2[2], btKs2[3],
                    btKs2[4], btKs2[5], btKs2[6], btKs2[7],
            };

            console = console + "\n\t5º Cálculo de Ch";
            //5º Cálculo de Ch
            //Ch' = Rc TDES Ks
            aux = tdes(btRc, btKs);
            //Ch'' = Ch' XOR Rh
            int i;
            for (i = 0; i < 8; i++) {
                aux[i] = (byte) (aux[i] ^ btRh[i]);
            }
            //Ch''' = Ch'' TDES Ks
            aux = tdes(aux, btKs);

            //Ch'''' = Ch''' XOR 80 00 00 00 00 00 00 00
            byte[] btOch = new byte[]{
                    (byte) 0x80,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00
            };

            for (i = 0; i < 8; i++)
                aux[i] = (byte) (aux[i] ^ btOch[i]);

            //Ch = Ch'''' TDES Ks
            byte[] btCh;

            btCh = tdes(aux, btKs);

            //-------------------
            console = console + "\n\t6º external authenticate";
            //6º external authenticate
            apdu = new byte[]{
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

            console = console + "\n\t->" + TdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console = console + "\n\t<-" + TdmCard.bytesToHexString(key);


            apdu = new byte[]{
                    (byte) 0x90,
                    (byte) 0x48,
                    (byte) 0x01,
                    (byte) 0x00,
                    (byte) 0x04,
                    (byte) 0xBC,
                    (byte) 0xB1,
                    (byte) 0x34,
                    (byte) 0x1B,
                    (byte) 0x00
            };

            console = console + "\n3 - (SAM_GetMIF1KKeys)\n\t->" + TdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console = console + "\n\t<-" + TdmCard.bytesToHexString(key);

        }catch(Exception e){
            console = console + "\n"+ e;

        }
        Log.v(LOG_TAG, console);
        return key;
    }
*/
}

