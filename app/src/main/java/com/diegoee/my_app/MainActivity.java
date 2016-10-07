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
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;


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

    //TODO: Metodod utilizado en la comunicación con la SAM
    private byte[] tdes(byte[] info, byte[] keyAtr) throws Exception{

        byte [] keyVar=keyAtr;
        byte [] aa = new byte[]{
                (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
                (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00
        };

        if (keyVar.length<=24){
            for (int i=0;i<keyVar.length;i++){
                aa[i]=keyVar[i];
            }
        }

        //Log.v(LOG_TAG,"1-TDES "+aa.length);
        DESedeKeySpec keyspec = new DESedeKeySpec(aa);
        //Log.v(LOG_TAG,"2-TDES");
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        //Log.v(LOG_TAG,"3-TDES");
        SecretKey key = keyfactory.generateSecret(keyspec);
        //Log.v(LOG_TAG,"4-TDES");
        Cipher cipher = Cipher.getInstance("DESede");
        //Log.v(LOG_TAG,"5-TDES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //Log.v(LOG_TAG,"6-TDES");
        byte[] encrypted = cipher.doFinal(info);
        //Log.v(LOG_TAG,"7-TDES");

        return encrypted;
    }

    //TODO: Metodo para comunicar con laSAM y obtener la key para decodificar tarjeta.
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
            console = console + "\n1 - SAM_SelectSAMApp";
            console = console +"\n->"+TdmCard.bytesToHexString(apduRequest);
            apduResponse = mSmartcardReader.sendApdu(apduRequest);
            console = console + "\n<-" + TdmCard.bytesToHexString(apduResponse);


            apduRequest = new byte[]{
                    (byte) 0x90,
                    (byte) 0x10,
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x14
            };

            console=console+"\n2 - (Opt)SAM_GetSAMProps";
            console=console+"\n->"+TdmCard.bytesToHexString(apduRequest);
            apduResponse = mSmartcardReader.sendApdu(apduRequest);
            console=console+"\n<-" + TdmCard.bytesToHexString(apduResponse);


            //1º Generamos un número aleatorio (Rh)
            console = console + "\n\n**Autenticación**";
            console = console + "\n1º Gen. random Rh";
            SecureRandom csprng = new SecureRandom();
            btRh = new byte[8];
            csprng.nextBytes(btRh);

            console = console + "\n\tRh=" + TdmCard.bytesToHexString(btRh);

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
            apduResponse = mSmartcardReader.sendApdu(apduRequest);
            console = console + "\n<-" + TdmCard.bytesToHexString(apduResponse);

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
                btCc = new byte[]{
                    key[8],
                    key[9],
                    key[10],
                    key[11],
                    key[12],
                    key[13],
                    key[14],
                    key[15]
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
                btCc = new byte[]{
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

            console = console + "\n\tRc=" + TdmCard.bytesToHexString(btRc);
            console = console + "\n\ttCc=" + TdmCard.bytesToHexString(btCc);

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
            console = console + "\n\tKa=" + TdmCard.bytesToHexString(btKa);

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
            console = console + "\n\tKs1=" + TdmCard.bytesToHexString(btKs1);

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
            console = console + "\n\tKs2=" + TdmCard.bytesToHexString(btKs2);

            console = console + "\n\tKs = Ks1 + Ks2";
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

            console = console + "\n\tKs=" + TdmCard.bytesToHexString(btKs);


            console = console + "\n4º Comprobar Cc";
            btVar = tdes(btRh, btKs);
            for (i = 0; i < 8; i++) {
                btVar[i] = (byte) (btVar[i] ^ btRc[i]);
            }
            btVar = tdes(btVar, btKs);

            for (i = 0; i < 8; i++)
                btVar[i] = (byte) (btVar[i] ^ bt80[i]);

            btVar = tdes(btVar, btKs);

            console = console + "\n\tCc (cal)=" + TdmCard.bytesToHexString(btVar);
            console = console + "\n\tCc (ori)=" + TdmCard.bytesToHexString(btRc);

            console = console + "\n5º Cálculamos en Ch";
            btVar = tdes(btRc, btKs);
            for (i = 0; i < 8; i++) {
                btVar[i] = (byte) (btVar[i] ^ btRh[i]);
            }
            btVar = tdes(btVar, btKs);

            for (i = 0; i < 8; i++)
                btVar[i] = (byte) (btVar[i] ^ bt80[i]);

            btCh = tdes(btVar, btKs);

            console = console + "\n\tCh=" + TdmCard.bytesToHexString(btCh);

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
            apduResponse = mSmartcardReader.sendApdu(apduRequest);
            console = console + "\n<-" + TdmCard.bytesToHexString(apduResponse);


            console = console + "\n7º GET_MIF1K_KEYS";
            console = console + "\n\n\n\n";

        }catch(Exception e){
            console = console + "\n"+ e;

        }
        Log.v(LOG_TAG, console);
        return key;
    }


    /*
    {
	//Llamada a todos los métodos de inicializacion y autenticacion del modulo SAM

	//Iniciar SAM
	blRes = SAM_Open();
	if (blRes)
	{
		LogManager.GetLogger("LibreriaFisica").Debug("SAM_Open OK");
		blRes = SAM_SelectSAMApp();
		if (blRes)
		{
			LogManager.GetLogger("LibreriaFisica").Debug("SAM_SelectSAMApp OK");
			iRes = SAM_GetSAMProps();
			if (iRes == 0)
			{
				LogManager.GetLogger("LibreriaFisica").Debug("SAM_GetSAMProps OK");
				iRes = SAM_Autenticate();
				LogManager.GetLogger("LibreriaFisica").Debug("SAM_Autenticate ires=" + iRes.ToString());
			}
		}
	}
}

public bool SAM_Open()
{
	bool bIsSAM = true;     // true = SAM slot, false = IC slot
	byte byCardType = 0x02; // 0x02 = General speed ISO7816
	byte bySAMSlot = 0;     // Slot number = 0 ~ 3
	byte byMode = 0x47;     // 0x47 = General ISO7816 mode
	byte[] abyInputParam = new byte[2] { 0x01, 0x11 };  // Default baudrate : 0x11= 9600
	byte[] abyChangePPS = new byte[4] { 0x03, 0xF1, 0x11, 0x94 };  // change PPS : 0x13 = 38400( refer to datasheet of card manufacturer )
	uint dwBaudRate = 38400;

	m_strMsg = "SAM Open";

	if (m_RFReader.OpenICComm())// Open IC port
	{
		if (m_RFReader.SAMDefType(byCardType, bySAMSlot, m_abyResBuf))    // Config IC device
		{
			if (m_RFReader.SAMSlotIOMode(true, byMode, m_abyResBuf)) //Establece el modo IO de la tarjeta del slot (si es SAM o no)
			{
				if (m_RFReader.ICPowerOn(bIsSAM, abyInputParam, m_abyResBuf)) // Power on to the card
				{
					return true;
				}
			}
		}
	}
	return false;
}

public bool SAM_SelectSAMApp()
{
	try
	{
		bool bRet = false, blRes = false;
		m_strMsg = "Send SAM Commands: ";
		byte[] m_InputData = new byte[128];
		byte[] m_OutputData = new byte[255];

		m_InputData[0] = 0x0D; // Longitud de los datos de despues
		m_InputData[1] = 0x00; //APDU - cla
		m_InputData[2] = 0xA4; //APDU - ins
		m_InputData[3] = 0x04; //APDU - P1
		m_InputData[4] = 0x00; //APDU - P2
		m_InputData[5] = 0x08; //APDU - LC: tamaño de los datos
		m_InputData[6] = 0xF0; //APDU - datos: id de la aplicacion a seleccionar
		m_InputData[7] = 0x00; //APDU - datos: id de la aplicacion a seleccionar
		m_InputData[8] = 0x00; //APDU - datos: id de la aplicacion a seleccionar
		m_InputData[9] = 0x00; //APDU - datos: id de la aplicacion a seleccionar
		m_InputData[10] = 0x00; //APDU - datos: id de la aplicacion a seleccionar
		m_InputData[11] = 0x41; //APDU - datos: id de la aplicacion a seleccionar
		m_InputData[12] = 0x59; //APDU - datos: id de la aplicacion a seleccionar
		m_InputData[13] = 0x4D; //APDU - datos: id de la aplicacion a seleccionar

		if (m_RFReader.SendSAMCommand(true, m_InputData))
			bRet = true;

		//Resultado devuelto
		blRes = m_RFReader.GetSAMData(m_OutputData);

		int lenRes = m_OutputData[0];
		if (m_OutputData[lenRes - 1] == 0x90 && m_OutputData[lenRes] == 0x00)
			blRes = true;
		else
			blRes = false;

		return bRet;
	}
	catch (Exception ex)
	{
		return false;
	}
}


/// <summary>
/// Comando que obtiene las propiedades del SAM
/// </summary>
/// <returns>0 si ok, -1 si falla</returns>
public int SAM_GetSAMProps()
{
	int iRes = -1, c = 0;
	bool bRet = false;

	try
	{
		byte[] m_InputData = new byte[128];
		byte[] m_OutputData = new byte[255];

		m_InputData[0] = 0x05; //Longitud de los datos de despues!!!!!!!
		m_InputData[1] = 0x90; //APDU - cla
		m_InputData[2] = 0x10; //APDU - ins
		m_InputData[3] = 0x00; //APDU - P1
		m_InputData[4] = 0x00; //APDU - P2
		m_InputData[5] = 0x14; //APDU - LE : longitud de datos a obtener

		if (m_RFReader.SendSAMCommand(true, m_InputData))
			iRes = 0;

		bRet = m_RFReader.GetSAMData(m_OutputData);

		int longData = m_OutputData[0];
		if (m_OutputData[longData - 1] == 0x90)
		{
			for (int i = 1; i < 15; i++)
			{
				btTexto[c] = m_OutputData[i];
				c++;
			}

			Array.Copy(m_OutputData, 13, btVersion, 0, 2);

			btTry = m_OutputData[15];
			btState = m_OutputData[16]; //Almacena el estado del Applet

			Array.Copy(m_OutputData, 17, btSamNumb, 0, 2);
			Array.Copy(m_OutputData, 19, btCodEmpr, 0, 2);

			string strIdAux = OBID.FeHexConvert.ByteArrayToHexString(btSamNumb);
			strSamNumb = OBID.FeHexConvert.HexStringToLong(strIdAux).ToString();
		}
		return iRes;
	}
	catch (Exception ex)
	{
		return -1;
	}
}


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


/// <summary>
/// Comando que obtiene las claves diversificadas
/// </summary>
/// <returns>0 si ok, -1 si falla</returns>
public int SAM_GetMIF1KKeys(byte[] uid, byte btVers)
{
	int iRes = -1, iBack = -1;
	bool bRet = false;
	int iNumKeys = 0;
	byte[] btClaves = null;
	int iKey = 0;

	try
	{
		m_strMsg = "Send SAM Commands: ";
		byte[] m_InputData = new byte[128];
		byte[] m_OutputData = new byte[255];

		m_InputData[0] = 0x0A; //Longitud de los datos de despues!!!!!!!
		m_InputData[1] = 0x90; //APDU - cla
		m_InputData[2] = 0x48; //APDU - ins
		m_InputData[3] = btVers; //APDU - P1 - version de claves a utilizar
		m_InputData[4] = 0x00; //APDU - P2
		m_InputData[5] = 0x04; //APDU - LC : longitud de datos que se envian
		m_InputData[6] = uid[0]; //APDU - datos: valor rh
		m_InputData[7] = uid[1]; //APDU - datos: valor rh
		m_InputData[8] = uid[2]; //APDU - datos: valor rh
		m_InputData[9] = uid[3]; //APDU - datos: valor rh
		m_InputData[10] = 0x00; //APDU - le, datos de respuesta

		if (m_RFReader.SendSAMCommand(true, m_InputData))
			iRes = 0;

		bRet = m_RFReader.GetSAMData(m_OutputData);
		int longData = m_OutputData[0];
		LogManager.GetLogger("LibreriaFisica").Debug("SAM_GetMIF1KKeys: Resultado " + m_OutputData[longData - 1].ToString());
		if (m_OutputData[longData - 1] == 0x90)
		{
			LogManager.GetLogger("LibreriaFisica").Debug("Actualizamos las claves de la tarjeta");
			//indicamos los bytes de uid --> HAY QUE CAMBIARLO PARA DEVOLVER LOS DATOS QUE NECESITAMOS
			// Array.Copy(m_OutputData, 1, m_abySAMNumber, 0, longData - 2);

			bool first = true;
			int indArrayClaves = 0;

			iNumKeys = Convert.ToInt32(m_OutputData[1]);
			_ClavesTarjeta = new Dictionary<int, BluebirdRFID.KeyStruct>();
			//LCR 06.08.2013. Coge mal la clave 14
			//btClaves = new byte[iNumKeys * 7];
			btClaves = new byte[iNumKeys * 7 + 1];
			first = false;
			//LCR 06.08.2013. Coge mal la clave 14
			//Array.Copy(m_OutputData, 2, btClaves, indArrayClaves, longData - 4);
			Array.Copy(m_OutputData, 2, btClaves, indArrayClaves, longData - 3);
			//indArrayClaves = indArrayClaves + longData - 3;
			indArrayClaves = indArrayClaves + longData - 2;


			for (int i = 0; i < iNumKeys; i++)
			{
				BluebirdRFID.KeyStruct key = new BluebirdRFID.KeyStruct();

				string binaryval = OBID.FeHexConvert.ByteToHexString(btClaves[i * 7]);
				long lngBinario = OBID.FeHexConvert.HexStringToLong(binaryval);
				string valBinario = Dec2Bin(lngBinario, 8);
				string strKey = valBinario.Substring(1, 1);
				string strSect = valBinario.Substring(2, 6);

				key.KeyNumber = Convert.ToInt32(Bin2Dec(strKey));
				key.KeySect = Convert.ToInt32(Bin2Dec(strSect));

				iKey = 0;
				for (int j = (i * 7) + 1; j < (i * 7) + 7; j++)
				{
					key.KeyArray[iKey] = btClaves[j];
					iKey++;
				}

				_ClavesTarjeta.Add(i, key);
			}
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

/// <summary>
///
/// </summary>
/// <param name="fields"></param>
/// <param name="strOperation"></param>
/// <param name="strMac"></param>
/// <returns></returns>
public int SAM_Generate_Mac(Dictionary<string, string> fields, out string strOperation, out string strMac)
{
	int iRes = -1, longData = 0, i = 0, j = 0, index=0, cnt= 0;
	string strData = "";
	byte[] btOper = new byte[4];
	byte[] btMac = new byte[4];
	byte[] btData;
	bool bRet = false, blRes = false;

	try
	{
		byte[] m_InputData = new byte[128];
		byte[] m_OutputData = new byte[255];

		//Componer el campo datos con lo que viene en el parametro fields
		strData = fields["tipoOperacion"] +
				  fields["fechaOperacion"] +
				  fields["idEquipo"] +
				  fields["tipoEquipo"] +
				  fields["linea"] +
				  fields["titulo"] +
				  fields["parada"] +
				  fields["numeroSerie"] +
				  fields["uidTarjeta"] +
				  fields["lineaOrigen"] +
				  fields["operadorOrigen"] +
				  fields["importe"] +
				  fields["carga"] +
				  fields["saldoPrevio"] +
				  fields["saldoFinal"] +
				  fields["fechaCad"] +
				  fields["ntTarjeta"];

		btData = OBID.FeHexConvert.HexStringToByteArray(strData);

		//Comando TCC: Zócalo = 1, Header = (CLA:90h, INS:50h, P1:00h, P2:00h, P3:00h), Datos = nn nn nn nn , Le = 10
		m_InputData[0] = 0x44; //Longitud de los datos de despues
		m_InputData[1] = 0x90; //APDU - cla
		m_InputData[2] = 0x50; //APDU - ins
		m_InputData[3] = 0x00; //APDU - P1
		m_InputData[4] = 0x00; //APDU - P2
		m_InputData[5] = Convert.ToByte(btData.Length); //APDU - LC: tamaño de los datos
		index = 6;
		for (cnt = 0; cnt < btData.Length; cnt++)
		{
			m_InputData[index] = btData[cnt]; //APDU - datos: Concatenacion de info
			index++;
		}
		m_InputData[index] = 0x10; //APDU - LE

		if (m_RFReader.SendSAMCommand(true, m_InputData))
			iRes = 0;

		bRet = m_RFReader.GetSAMData(m_OutputData);
		longData = m_OutputData[0];
		LogManager.GetLogger("LibreriaFisica").Debug("SAM_Generate_Mac: Resultado " + m_OutputData[longData - 1].ToString());
		if (m_OutputData[longData - 1] == 0x61 || m_OutputData[longData - 1] == 0x90)
		{
			//Devuelve la secuencia de operacion (4 bytes) y la firma (4 bytes)
			j = 0;
			for (i = 1; i < 5; i++)
			{
				btOper[j] = m_OutputData[i];
				j++;
			}

			j = 0;
			for (i = 4; i < 8; i++)
			{
				btMac[j] = m_OutputData[i];
				j++;
			}
			iRes = 0;
			blRes = true;
		}
		else
		{
			iRes = -1;
		}

		if (blRes)
		{
			strOperation = OBID.FeHexConvert.ByteArrayToHexString(btOper);
			strMac = OBID.FeHexConvert.ByteArrayToHexString(btMac);
		}
		else
		{
			strOperation = "";
			strMac = "";
		}

		return iRes;
	}
	catch (Exception exc)
	{
		LogManager.GetLogger("LibreriaFisica").Error("SAM_Generate_Mac", exc);
		strOperation = "";
		strMac = "";
		return -1;
	}
}
     */

}

