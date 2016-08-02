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
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

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
        if (mNfcAdapter == null) {
            console = console +"\nNFC NO disponible en el dispositivo.";
        }

        try {
            // obtain smartcard reader instance
            mSmartcardReader = SmartcardReader.getInstance();
            // open smartcard reader.
            isDeviceAbleToRunSmartcardReader = mSmartcardReader.openReader();
            // power on smartcard reader
            if (isDeviceAbleToRunSmartcardReader) {
                //console = console + "\nOpen SAM";
                byte[] atr = mSmartcardReader.powerOn();
                //console=console+"\nATR: " + tdmCard.bytesToHexString(atr);
                this.getKeysFromSAM();

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

    private byte[] tdes(byte[] info, byte[] keyAtr) throws Exception{


        console = console + "\n---";
        DESedeKeySpec keyspec = new DESedeKeySpec(keyAtr);

        console = console + "\n---";
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");

        console = console + "\n---";
        SecretKey key = keyfactory.generateSecret(keyspec);

        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(info);


        return encrypted;
    }


    //TODO: Implemetar método
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

            console = console + "\n1 - (SAM_SelectSAMApp)\nAPDU => " + tdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console = console + "\nAPDU <= " + tdmCard.bytesToHexString(key);


            apdu = new byte[]{
                    (byte) 0x90,
                    (byte) 0x10,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x14
            };

            console=console+"\n1.1 - (Opt)(SAM_GetSAMProps)\nAPDU => " + tdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console=console+"\nAPDU <= " + tdmCard.bytesToHexString(key);

            console = console + "\n2 - (SAM_Autenticate)";

            //1º Generamos un número aleatorio (Rh)
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

            console = console + "\nAPDU => " + tdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console = console + "\nAPDU <= " + tdmCard.bytesToHexString(key);


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
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00
                };
            }


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

            console = console + "\nAPDU => " + tdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console = console + "\nAPDU <= " + tdmCard.bytesToHexString(key);


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

            console = console + "\n3 - (SAM_GetMIF1KKeys)\nAPDU => " + tdmCard.bytesToHexString(apdu);
            key = mSmartcardReader.sendApdu(apdu);
            console = console + "\nAPDU <= " + tdmCard.bytesToHexString(key);

        }catch(Exception e){
            console = console + "\n"+ e.toString();

        }
        Log.v(LOG_TAG, console);
        return key;
    }

    @Override
    public void onNewIntent(Intent intent) {
        console = "";
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Bundle bundle;
            console = console + "Tarjeta descubierta ->";
            //console = console + intent.toString();
            //console = console + "\n\nEXTRAS:";
            bundle = intent.getExtras();
            for (String key : bundle.keySet()) {
                if (key.equals("android.nfc.extra.ID")) {
                    byte [] val = bundle.getByteArray(key);
                    //console = console + String.format("\n\t-KEY: %s",key);
                    console = console + String.format(" ID-NFC: %s",tdmCard.bytesToHexString(val));
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

        console = console + "\n";//+tdmCard.getInfoHexByte();

        navigationView.getMenu().getItem(0).setChecked(true);
        Fragment fragment = new MainFragment(console,tdmCard,MainFragment.MAIN);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private String resolveIntent(Intent intent) {
        String console = "";
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
                        console = console +"\nError de Autentificación";
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
        console = console +"\nDatos de la tarjeta leidos.";
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



/*

/// <summary>
/// Comando que autentifica el SAM
/// </summary>
/// <returns>0 si ok, -1 si falla</returns>
public int SAM_Autenticate()
{
	int iRes = -1, i = 0;
	bool bRet = false;

	try
	{
		byte[] m_InputData = new byte[128];
		byte[] m_OutputData = new byte[255];

		byte[] btRh; //Reto aleatorio
		byte[] btRc; //Reto cifrado
		byte[] btCc; //Criptograma
		byte[] btRh1; //Almacena la 1º parte (4 bytes) de btRh
		byte[] btRh2; //Almacena la 2º parte (4 bytes) de btRh
		byte[] btRc1; //Almacena la 1º parte (4 bytes) de btRc
		byte[] btRc2; //Almacena la 2º parte (4 bytes) de btRc
		byte[] btKa; //Almacena la clave de autenticacion (16 bytes)
		byte[] btKs; //Almacena la clave de sesion (16 bytes)
		byte[] btKs1; //Almacena la 1º parte (8 bytes) de la clave de sesion
		byte[] btKs2; //Almacena la 2º parte (8 bytes) de la clave de sesion
		byte[] btCcGen; // Almacena el Cc generado a partir de Rh, Ks y Rc
		byte[] btCh; //Almacena el criptograma del terminal, que hay que enviar al SAM

		btRh = new byte[8];
		btRc = new byte[8];
		btCc = new byte[8];
		btRh1 = new byte[4];
		btRh2 = new byte[4];
		btRc1 = new byte[4];
		btRc2 = new byte[4];
		btKa = new byte[16];
		btKs = new byte[16];
		btKs1 = new byte[8];
		btKs2 = new byte[8];
		btCcGen = new byte[8];
		btCh = new byte[8];

		RNGCryptoServiceProvider rng = new RNGCryptoServiceProvider();
		rng.GetBytes(btRh);

		//INITIALIZE UPDATE
		m_InputData[0] = 0x0E; //Longitud de los datos de despues!!!!!!!
		m_InputData[1] = 0x80; //APDU - cla
		m_InputData[2] = 0x50; //APDU - ins
		m_InputData[3] = 0x00; //APDU - P1
		m_InputData[4] = 0x00; //APDU - P2
		m_InputData[5] = 0x08; //APDU - LC : num bytes que se envian
		m_InputData[6] = btRh[0]; //APDU - datos: valor rh
		m_InputData[7] = btRh[1]; //APDU - datos: valor rh
		m_InputData[8] = btRh[2]; //APDU - datos: valor rh
		m_InputData[9] = btRh[3]; //APDU - datos: valor rh
		m_InputData[10] = btRh[4]; //APDU - datos: valor rh
		m_InputData[11] = btRh[5]; //APDU - datos: valor rh
		m_InputData[12] = btRh[6]; //APDU - datos: valor rh
		m_InputData[13] = btRh[7]; //APDU - datos: valor rh
		m_InputData[14] = 0x10; //APDU - le, datos de respuesta

		if (m_RFReader.SendSAMCommand(true, m_InputData))
			iRes = 0;

		bRet = m_RFReader.GetSAMData(m_OutputData);

		int longData = m_OutputData[0];
		if (m_OutputData[longData - 1] == 0x90)
		{
			//Obtener Rc(reto) y Cc(criptograma calculado por la SAM)
			Array.Copy(m_OutputData, 1, btRc, 0, 8);
			Array.Copy(m_OutputData, 9, btCc, 0, 8);
		}

		//Obtener Ks(clave de sesion calculado a partir de Rh, Rc y clave de autenticacion)
		//Partir en 2 Rh:
		//Rh1
		Array.Copy(btRh, 0, btRh1, 0, 4);
		//Rh2
		Array.Copy(btRh, 4, btRh2, 0, 4);

		//Partir en 2 Rc
		//Rc1
		Array.Copy(btRc, 0, btRc1, 0, 4);
		//Rc2
		Array.Copy(btRc, 4, btRc2, 0, 4);

		//LCR 01.08.2013. Nueva clave de produccion
		//clave autenticacion - son 16 bytes -->  00 11 22 33 44 55 66 77 88 99 AA BB CC DD EE FF
		//btKa = new byte[16] { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99, 0xAA, 0xBB, 0xCC, 0xDD, 0xEE, 0xFF };
		btKa = new byte[16] { 0x76, 0x8E, 0x92, 0x78, 0x28, 0x75, 0xAC, 0xAC, 0xF6, 0x8E, 0x59, 0x48, 0x04, 0x5F, 0xD5, 0x90 };

		//Calculo de Clave de sesion  Ks1 y Ks2
		//Ks1 = Rc2 Rh1 TDES Ka
		byte[] btKs1Aux = new byte[8];
		Array.Copy(btRc2, 0, btKs1Aux, 0, 4);
		Array.Copy(btRh1, 0, btKs1Aux, 4, 4);
		btKs1 = Encriptar(btKs1Aux, btKa);

		//Ks2 = Rc1 Rh2 TDES Ka
		byte[] btKs2Aux = new byte[8];
		Array.Copy(btRc1, 0, btKs2Aux, 0, 4);
		Array.Copy(btRh2, 0, btKs2Aux, 4, 4);
		btKs2 = Encriptar(btKs2Aux, btKa);

		//Ks = Ks1 + Ks2
		Array.Copy(btKs1, 0, btKs, 0, 8);
		Array.Copy(btKs2, 0, btKs, 8, 8);

		//Generar Cc para comparar con el Cc obtenido con el comando btCcGen
		//Cc' = Rh TDES Ks
		byte[] btCcGenAux1 = new byte[8];
		btCcGenAux1 = Encriptar(btRh, btKs);

		//Cc'' = Cc' XOR Rc
		byte[] btCcGenAux2 = new byte[8];
		for (i = 0; i < 8; i++)
			btCcGenAux2[i] = (byte)(btCcGenAux1[i] ^ btRc[i]);

		//Cc''' = Cc'' TDES Ks
		byte[] btCcGenAux3 = new byte[8];
		btCcGenAux3 = Encriptar(btCcGenAux2, btKs);

		//Cc'''' = Cc''' XOR 80 00 00 00 00 00 00 00
		byte[] btCcGenAux4 = new byte[8];
		byte[] btOch = new byte[8] { 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		for (i = 0; i < 8; i++)
			btCcGenAux4[i] = (byte)(btCcGenAux3[i] ^ btOch[i]);

		//Cc = Cc'''' TDES Ks
		btCcGen = Encriptar(btCcGenAux4, btKs);

		//Hay que comparar si el btCcGen generado por nosotros y btCc devuelto por el SAM son el mismo - TODO OK
		for (int cm = 0; cm < 8; cm++)
		{
			if (btCcGen[cm] != btCc[cm])
			{
				return -1;
			}
		}

		//Calcular Ch para comando external authenticate(criptograma calculado en funcion de Rc, Rh, y clave de sesion)
		//Ch' = Rc TDES Ks
		byte[] btChAux1 = new byte[8];
		btChAux1 = Encriptar(btRc, btKs);

		//Ch'' = Ch' XOR Rh
		byte[] btChAux2 = new byte[8];
		for (i = 0; i < 8; i++)
			btChAux2[i] = (byte)(btChAux1[i] ^ btRh[i]);

		//Ch''' = Ch'' TDES Ks
		byte[] btChAux3 = new byte[8];
		btChAux3 = Encriptar(btChAux2, btKs);

		//Ch'''' = Ch''' XOR 80 00 00 00 00 00 00 00
		byte[] btChAux4 = new byte[8];
		btOch = new byte[8] { 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		for (i = 0; i < 8; i++)
			btChAux4[i] = (byte)(btChAux3[i] ^ btOch[i]);

		//Ch = Ch'''' TDES Ks
		btCh = Encriptar(btChAux4, btKs);
		//-------------------


		//EXTERNAL AUTENTICATE
		m_InputData[0] = 0x0D; //Longitud de los datos de despues!!!!!!!
		m_InputData[1] = 0x80; //APDU - cla
		m_InputData[2] = 0x82; //APDU - ins
		m_InputData[3] = 0x00; //APDU - P1
		m_InputData[4] = 0x00; //APDU - P2
		m_InputData[5] = 0x08; //APDU - LC : num bytes que se envian
		m_InputData[6] = btCh[0]; //APDU - datos: valor rh
		m_InputData[7] = btCh[1]; //APDU - datos: valor rh
		m_InputData[8] = btCh[2]; //APDU - datos: valor rh
		m_InputData[9] = btCh[3]; //APDU - datos: valor rh
		m_InputData[10] = btCh[4]; //APDU - datos: valor rh
		m_InputData[11] = btCh[5]; //APDU - datos: valor rh
		m_InputData[12] = btCh[6]; //APDU - datos: valor rh
		m_InputData[13] = btCh[7]; //APDU - datos: valor rh

		if (m_RFReader.SendSAMCommand(true, m_InputData))
			iRes = 0;

		bRet = m_RFReader.GetSAMData(m_OutputData);

		longData = m_OutputData[0];
		if (m_OutputData[longData - 1] == 0x90)
		{
			iRes = 0;
		}
		else
		{
			iRes = -1;
		}

		return iRes;
	}
	catch (Exception ex)
	{
		return -1;
	}
}
*/