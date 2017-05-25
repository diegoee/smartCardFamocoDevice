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
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LOG_TAG = "log_app";

    private NfcAdapter mNfcAdapter;

    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] intentFiltersArray;
    private NavigationView navigationView;

    private int exit;
    private String console;
    private String login;

    private TdmCard tdmCard;
    private SAMcom samCom;

    private List<ActionUser> actionUser;


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

        //init classes
        tdmCard = new TdmCard();
        samCom = new SAMcom();

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
                console = "";
                onStop();
                onStart();
            }
        });

        //init variable
        console = "Dispositivo Listo para lectura.";
        exit=1;
        login ="none";
        actionUser= new ArrayList<ActionUser>();

        // adding methods
        if (mNfcAdapter == null) {
            console = console +"\nNFC NO disponible en el dispositivo.";
        }

        //init SAM
        console = console + samCom.init();

        login = (String)getIntent().getExtras().getSerializable("login");
        TextView textView1 = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textMenu);
        textView1.setText("Usuario: "+login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.getMenu().getItem(0).setChecked(true);
        MainFragment fragment = new MainFragment();
        fragment.setText(console);
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
        String s ="";

        MainFragment fragment = new MainFragment();
        fragment.setLoad(MainFragment.MAIN);

        if (item.getItemId() == R.id.nav_main) {
            fragment.setLoad(MainFragment.MAIN);
            s = console+"\n"+tdmCard.getMainData();
        } else if (item.getItemId() == R.id.nav_detail_mov) {
            fragment.setLoad(MainFragment.DETAIL_MOV);
            s = tdmCard.getMovData();
        } else if (item.getItemId() == R.id.nav_detail_card) {
            fragment.setLoad(MainFragment.DETAIL_CARD);
            s = tdmCard.getCardData();
        } else if (item.getItemId() == R.id.nav_detail_ctrl) {
            fragment.setLoad(MainFragment.DETAIL_CTRL);
            s = tdmCard.getCtrlData();
        } else if (item.getItemId() == R.id.nav_contact) {
            fragment.setLoad(MainFragment.CONTACT);
        } else if (item.getItemId() == R.id.nav_action_user) {
            fragment.setLoad(MainFragment.ACTION_USER);
            for (ActionUser l : actionUser) {
                if (l.isValidationOK()){
                    s = s+"\nId:"+l.getId()+" Validado(SI/NO): SI";
                }else{
                    s = s+"\nId:"+l.getId()+" Validado(SI/NO): NO";
                }
            }
        }

        fragment.setText(s);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNewIntent(Intent intent) {
        console = "";
        byte [] uid = null;

        Bundle bundle = intent.getExtras();
        console = console + "Tarjeta descubierta ->";
        for (String key : bundle.keySet()) {
            if (key.equals("android.nfc.extra.ID")) {
                uid = bundle.getByteArray(key);
                console = console + String.format(" ID-NFC: %s",TdmCard.bytesToHexString(uid));
            }
        }

        console = console +"\n"+this.resolveIntent(intent);
        ActionUser actUserTemp =new ActionUser();
        actUserTemp.setId(String.format("%s",TdmCard.bytesToHexString(uid)));
        actUserTemp.setValidationOK(false);

        actionUser.add(actUserTemp);

        Log.v(LOG_TAG,actUserTemp.getId());

        navigationView.getMenu().getItem(0).setChecked(true);

        MainFragment fragment = new MainFragment();
        fragment.setText(console+"\n"+tdmCard.getMainData());
        fragment.setLoad(MainFragment.MAIN);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private String resolveIntent(Intent intent) {
        String cons = "";
        tdmCard.eraseInfo();

        boolean colorCard = false;
        boolean auth = false;

        byte[] uId = new byte[]{
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00
        };
        byte[] uIdInv = new byte[]{
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x00
        };
        byte[] bVers = new byte[]{
                (byte) 0x00
        };

        byte[] data,aux;

        data = new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xAA
        };
        aux = new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xAA
        };

        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();

        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) { //ACTION_TECH_DISCOVERED
            try {
                //  3) Get an instance of the TAG from the NfcAdapter
                Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                // 4) Get an instance of the Mifare classic card from this TAG intent
                MifareClassic mfc = MifareClassic.get(tagFromIntent);

                // 5.1) Connect to card
                mfc.connect();

                // 5.2) and get the number of sectors this card has..and loop thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;

                auth = mfc.authenticateSectorWithKeyA(0,samCom.KEYS_A_B[0]);
                if (!auth){
                    auth = mfc.authenticateSectorWithKeyA(0,samCom.KEYS_A_Color);
                    colorCard = true;
                }

                //getting uid and Version
                data = mfc.readBlock(mfc.sectorToBlock(0)+0);
                uId[0]=data[0];
                uId[1]=data[1];
                uId[2]=data[2];
                uId[3]=data[3];
                uIdInv[0]=data[3];
                uIdInv[1]=data[2];
                uIdInv[2]=data[1];
                uIdInv[3]=data[0];
                data = mfc.readBlock(mfc.sectorToBlock(0)+1);
                bVers[0]=data[0];


                cons = cons + "uId (invertido) (Hex): "+  tdmCard.bytesToHexString(uIdInv) + "\nVer: " + Character.toString(TdmCard.bytesToHexString(bVers).charAt(1)) + " \nTarjeta color: " + Boolean.toString(colorCard);
                cons = cons + samCom.setKeysFromSAM(colorCard,uId,bVers);

                if (auth) {
                    for (int j = 0; j < secCount; j++) {
                        //Log.v(LOG_TAG, Integer.toString(j) + " - " + tdmCard.bytesToHexString(samCom.keys[j]) + " - " + Boolean.toString(samCom.ab[j]));
                        auth = false;

                        // 6.1) authenticate the sector
                        if (samCom.ab[j]){
                            auth = mfc.authenticateSectorWithKeyA(j, samCom.keys[j]);
                            //cons = cons + "\nS_" + j + " \tA=" + tdmCard.bytesToHexString(samCom.keys[j])+ "  \tauth="+Boolean.toString(auth);
                        }else{
                            auth = mfc.authenticateSectorWithKeyB(j, samCom.keys[j]);
                            //cons = cons + "\nS_" + j + " \tB=" + tdmCard.bytesToHexString(samCom.keys[j])+ "  \tauth="+Boolean.toString(auth);
                        }

                        bCount = mfc.getBlockCountInSector(j);
                        bIndex = 0;

                        if (auth) {
                            for (int i = 0; i < bCount; i++) {// 4 Blocks
                                bIndex = mfc.sectorToBlock(j);
                                // 6.3) Read the block
                                data = mfc.readBlock(bIndex + i);
                                // 7) Convert the data into a string from Hex format.
                                tdmCard.append(data);
                                //TESTING
                                //if (j==3) {
                                    //Log.v(LOG_TAG, tdmCard.bytesToHexString(data));
                                //}
                                //cons = cons + "\n" + tdmCard.bytesToHexString(data);
                            }
                            aux = data;
                        } else if((auth==false)&&(j==secCount-1)){
                            for (int i = 0; i < 4; i++) {// 4 Blocks

                                if (i==3) {
                                    data = aux;
                                }else{
                                    data = new byte[]{
                                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xAA
                                    };
                                }
                                tdmCard.append(data);
                            }
                        }
                    }

                } else {
                    cons = cons +"\nError de Autentificación";
                    return cons;
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
        }else {
            cons = cons +"\nDatos de la tarjeta NO leidos.";
        }

        //Log.v(LOG_TAG, tdmCard.getInfoHexByte());

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
        samCom.closeSAM();
    }

}











