package com.bcp.DFA;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;


public class ActivitySetting extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_SETTING="SETTING";
    private final String TAG_SETTINGSTATUS="SETSTATUS";
    private final String TAG_STATUS = "status";
    private final String TAG_WEB = "web";
    private final String TAG_APPVERSION = "appversion";
    private final String TAG_DBVERSION = "dbversion";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_TGLLOGIN = "tgllogin";
    private final String TAG_MODE = "modeapp";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_DEVICEID = "DeviceID";

    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";

    private EditText InputWebServer;
    private ImageView ImgWebServer;
    private Button BtnSubmit;
    private TextView TxtDeviceScreen,TxtLocation,TxtSecurityCode;
    private ToggleButton TBMode;

    int status;
    private String Web,AppVersion="1",DBVersion="1",LastLogin,NameLogin,TglLogin,ModeApp="";
    private String Screen="",longitude="",latitude="";
    private String Checker = "0";

    String[] CabangArray;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_setting);

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SETTING);


        Intent in = getIntent();


        if(in.getStringExtra(TAG_SETTINGSTATUS).equals("1")){
            db.InsertSetting("",1,1,"0");
        }else{
            JSONObject SettingJSON = null;
            try{
                SettingJSON = db.GetSetting();
                status = SettingJSON.getInt(TAG_STATUS);
                Web = SettingJSON.getString(TAG_WEB);
                AppVersion = SettingJSON.getString(TAG_APPVERSION);
                DBVersion = SettingJSON.getString(TAG_DBVERSION);
                LastLogin = SettingJSON.getString(TAG_LASTLOGIN);
                NameLogin = SettingJSON.getString(TAG_NAMELOGIN);
                ModeApp = SettingJSON.getString(TAG_MODE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // Close DB Connection
        db.close();

        InputWebServer = (EditText) findViewById(R.id.Setting_InputWeb);

        InputWebServer.setText(Web);


        ImgWebServer = (ImageView) findViewById(R.id.Setting_InputWebMsg);

        ImgWebServer.setVisibility(View.GONE);

        TBMode = (ToggleButton) findViewById(R.id.SettingTBMode);
        if(ModeApp.equals("1")){
            TBMode.setChecked(true);
        }else{
            TBMode.setChecked(false);
        }
        TBMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Checker = "1";
                }else{
                    Checker = "0";
                }
            }
        });

        BtnSubmit = (Button) findViewById(R.id.Setting_BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InputWebServer.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Web Server harus Diisi!",Toast.LENGTH_SHORT).show();
                    ImgWebServer.setVisibility(View.VISIBLE);
                }else{

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    ImgWebServer.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"Pengaturan Tersimpan",Toast.LENGTH_SHORT).show();
                                    db.UpdateSettingFull(InputWebServer.getText().toString(),Checker);
                                    Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySetting.this);
                    builder.setTitle("Konfirmasi");
                    builder.setIcon(R.drawable.dfa_info_ups);
                    builder.setMessage("Simpan Pengaturan?").setPositiveButton("Ya", dialogClickListener)
                            .setNegativeButton("Tidak", dialogClickListener).show();
                }
            }
        });


        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                Screen = "Large";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                Screen = "Normal";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                Screen = "Small";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                Screen = "X-Large";
                break;
            default:
                Screen = "Undefined Screen";
        }

        TxtDeviceScreen = (TextView) findViewById(R.id.Setting_TxtDeviceScreen);
        TxtDeviceScreen.setText(" "+width+" x "+height+" ("+Screen+")");

        longitude = getPref(TAG_LONGITUDE);
        latitude = getPref(TAG_LATITUDE);

        TxtLocation = (TextView) findViewById(R.id.Setting_TxtLocation);
        TxtLocation.setText(" "+latitude+", "+longitude);


        TxtSecurityCode = (TextView) findViewById(R.id.Setting_TxtSecureCode);
        TxtSecurityCode.setText(getPref(TAG_DEVICEID));
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}