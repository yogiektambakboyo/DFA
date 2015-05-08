package com.bcp.DFA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.*;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActivityMain extends Activity {
    //DB Handler
    private FN_DBHandler db,dbmst;
    private FN_NetConStatus netStatus;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_SETTING="SETTING";
    private String      DB_MASTER="MASTER";
    private String      TAG_PREF="SETTINGPREF";
    private final String TAG_SETTINGSTATUS="SETSTATUS";
    private final String TAG_STATUS = "status";
    private final String TAG_WEB = "web";
    private final String TAG_APPVERSION = "appversion";
    private final String TAG_DBVERSION = "dbversion";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_MODEAPP = "modeapp";

    // Tabel Pelanggan
    private final String TAG_KODE = "Kode";
    private final String TAG_PERUSAHAAN = "Perusahaan";
    private final String TAG_ALAMAT = "Alamat";

    //Tabel ActivityDeliveryOrder
    private final String TAG_DELIVERYORDER = "deliverydata";
    private final String TAG_TGL = "Tgl";
    private final String TAG_KODENOTA = "Kodenota";
    private final String TAG_SOPIR = "Sopir";
    private final String TAG_FAKTUR = "Faktur";
    private final String TAG_SHIPTO = "ShipTo";
    private final String TAG_BRG = "Brg";
    private final String TAG_HINT = "Hint";
    private final String TAG_JML = "Jml";
    private final String TAG_HARGASATUAN = "HrgSatuan";
    private final String TAG_NOPICKLIST = "NoPicklist";
    private final String TAG_JMLCRT = "JmlCRT";
    private final String TAG_DISCRP = "DiscRp";
    private final String TAG_TOTALBAYAR = "TotalBayar";
    private final String TAG_KETERANGAN = "Keterangan";
    private final String TAG_TGLPICKLIST = "TglPickList";
    private final String TAG_RASIOMAX = "RasioMax";

    //Tabel Login
    private final String TAG_LOGIN = "logindata";
    private final String TAG_NAMA = "Nama";
    private final String TAG_DAYLOGIN = "DayLogin";
    private final String TAG_DEVICEID = "DeviceID";

    int status;
    String Web,AppVersion,DBVersion,LastLogin,NameLogin,ModeApp;
    String UserInput="";
    String StatusRequest="0";

    //Form
    EditText InputUsername,InputPassword;
    CheckBox CheckUpdate;
    Button BtnSubmit;

    JSONArray DeliveryOrderArray = null;
    JSONArray LoginArray = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_main);

        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3399FF")));

        netStatus = new FN_NetConStatus();

        // Starting Service
        Intent ServiceIn  = new Intent(this,ServiceLocation.class);
        startService(ServiceIn);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        InputUsername = (EditText) findViewById(R.id.Login_InputUsername);
        InputPassword = (EditText) findViewById(R.id.Login_InputPassword);


        // If DayLogin different Today
        CheckUpdate = (CheckBox) findViewById(R.id.MainMenu_CBUpdate);
        if (!getPref(TAG_DAYLOGIN).equals(getToday())){
            CheckUpdate.setChecked(true);
        }

        BtnSubmit = (Button) findViewById(R.id.Login_BtnSubmit);

        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((InputUsername.getText().toString().equals(InputPassword.getText().toString()))&&((InputUsername.getText().length())>0)){
                    UserInput=InputUsername.getText().toString();
                    if(CheckUpdate.isChecked()){
                        if(netStatus.getConnectivityStatusString(getApplicationContext())!=0){
                            new CekLogin(ActivityMain.this).execute();
                        }else{
                            Toast.makeText(getApplicationContext(),"Pastikan Koneksi WiFi Aktif!!",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(InputUsername.getText().toString().equals(LastLogin)){
                            setPrefLogin(InputUsername.getText().toString(),NameLogin);
                            Intent in = new Intent(getApplicationContext(), ActivityMainMenu.class);
                            in.putExtra(TAG_WEB,Web);
                            startActivity(in);
                        }else{
                            Toast.makeText(getApplicationContext(),"Username/Password Salah!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Username/Password Salah!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Get Data From DB SETTING
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SETTING);
        File dbFile = new File(DB_PATH+"/"+DB_SETTING);

        // Get Data From DB MASTER
        dbmst = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFileMaster = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject MenuJSON = null;

        if(dbFile.exists()){
            try {
                MenuJSON = db.GetSetting();
                status = MenuJSON.getInt(TAG_STATUS);
                Web = MenuJSON.getString(TAG_WEB);
                AppVersion = MenuJSON.getString(TAG_APPVERSION);
                DBVersion = MenuJSON.getString(TAG_DBVERSION);
                LastLogin = MenuJSON.getString(TAG_LASTLOGIN);
                NameLogin = MenuJSON.getString(TAG_NAMELOGIN);
                ModeApp = MenuJSON.getString(TAG_MODEAPP);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!dbFileMaster.exists()){
                dbmst.CreateMaster();
            }

            if(dbmst.cekColumnExist("DeliveryOrder","transferbank").equals("0")){
                dbmst.addColumn("DeliveryOrder","transferbank","TEXT");
                dbmst.addColumn("DeliveryOrder","transferjml","FLOAT");
            }

            if(dbmst.cekColumnExist("DeliveryOrder","startentry").equals("0")){
                dbmst.addColumn("DeliveryOrder","startentry","DATETIME");
                dbmst.addColumn("DeliveryOrder","finishentry","DATETIME");
            }

            if(dbmst.cekColumnExist("DeliveryMaster","rasiomax").equals("0")){
                dbmst.addColumn("DeliveryMaster","rasiomax","INTEGER");
            }

            if(dbmst.cekColumnExist("DeliveryOrder","tolakan").equals("0")){
                dbmst.addColumn("DeliveryOrder","tolakan","TEXT");
            }

            if(ModeApp.equals("1")){
                Intent in = new Intent(getApplicationContext(),ActivityMainChecker.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityMain.this.finish();
                startActivity(in);
            }

        }else{
            File DFADir = new File(DB_PATH);
            DFADir.mkdirs();
            db.CreateSetting();
            Intent in = new Intent(getApplicationContext(), ActivitySetting.class);
            in.putExtra(TAG_SETTINGSTATUS,"1");
            startActivity(in);
        }
        db.close();
        dbmst.close();

        setPref(Web,AppVersion,DBVersion);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        setPrefDeviceID((telephonyManager.getDeviceId()).substring(6,13));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dfa_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                DialodSetting();
                break;
/*            case R.id.action_update:
                Intent in = new Intent(getApplicationContext(),ActivityMaps.class);
                startActivity(in);
                break;*/
            default:break;
        }

        return true;
    }

    public class UpdateMaster extends AsyncTask<Void, Integer, Void> {

        Context context;
        Handler handler;
        Dialog dialog;
        TextView txtLoadingProgress;
        int PelangganSize=0;
        int DeliveryOrderSize=0;
        int showDialog=0;

        UpdateMaster(Context context, Handler handler){
            this.context=context;
            this.handler=handler;
        }

        UpdateMaster(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // create dialog
            dialog=new Dialog(context);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.p_gifloading);
            txtLoadingProgress =(TextView) dialog.findViewById(R.id.txtLoading2);
            txtLoadingProgress.setText("Downloading . .");

            String path = "file:///android_asset/gambar.gif";
            WebView wV = (WebView) dialog.findViewById(R.id.webView);
            wV.loadUrl(path);
            wV.setScrollBarStyle(WebView.OVER_SCROLL_NEVER);
            // disable scroll on touch
            wV.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });
            wV.setVerticalScrollBarEnabled(false);
            wV.setHorizontalScrollBarEnabled(false);

            dialog.show();
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "http://"+Web+"/ws/delivery.php?sopir="+UserInput;
            final FN_JSONParser jParser = new FN_JSONParser();

            try {

                JSONObject json = jParser.getJSONFromUrl(url);
                StatusRequest = json.getString("STATUS");

                if(StatusRequest.equals("1")){
                    showDialog=2;
                    // Get JSON KKPDV
                    dbmst.DeleteDeliveryMaster();
                    DeliveryOrderArray = json.getJSONArray(TAG_DELIVERYORDER);

                    DeliveryOrderSize = DeliveryOrderArray.length();

                    for (int j=0;j<DeliveryOrderArray.length();j++){
                        JSONObject d = DeliveryOrderArray.getJSONObject(j);
                        dbmst.InsertDeliveryMaster(d.getString(TAG_TGL),d.getString(TAG_KODENOTA),d.getString(TAG_SOPIR),d.getString(TAG_FAKTUR),d.getString(TAG_SHIPTO),d.getString(TAG_PERUSAHAAN),d.getString(TAG_ALAMAT),d.getString(TAG_BRG),d.getString(TAG_HINT),Integer.parseInt(d.getString(TAG_JML)),Float.parseFloat(d.getString(TAG_HARGASATUAN)),d.getString(TAG_NOPICKLIST),d.getString(TAG_JMLCRT),Float.parseFloat(d.getString(TAG_DISCRP)),Float.parseFloat(d.getString(TAG_TOTALBAYAR)),d.getString(TAG_KETERANGAN),d.getString(TAG_TGLPICKLIST),Integer.parseInt(d.getString(TAG_RASIOMAX)));
                        publishProgress(j);
                    }
                }else{
                    Log.e("0","Status 0");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showDialog=0;
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (showDialog){
                case 0:{
                    dialog.dismiss();
                    break;
                }
                case 1:{
                    txtLoadingProgress.setText("Receiving Pelanggan "+values[0]+"/"+PelangganSize);
                    break;
                }
                case 2:{
                    txtLoadingProgress.setText("Receiving Data "+values[0]+"/"+DeliveryOrderSize);
                    break;
                }
                default:break;
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            setPrefLogin(InputUsername.getText().toString(),NameLogin);
            if(StatusRequest.equals("1")){
                dbmst.DeleteAllDeliveryOrder();
                Intent in = new Intent(getApplicationContext(), ActivityMainMenu.class);
                in.putExtra(TAG_WEB,Web);
                startActivity(in);
            }else{
                Toast.makeText(getApplicationContext(),"Login Gagal",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CekLogin extends AsyncTask<Void, Integer, Void> {

        Context context;
        Handler handler;
        Dialog dialog;
        TextView txtLoadingProgress;
        //android.widget.ProgressBar ProgressBar;
        int PelangganSize=0;
        int DeliveryOrderSize=0;
        int showDialog=0;

        CekLogin(Context context, Handler handler){
            this.context=context;
            this.handler=handler;
        }

        CekLogin(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // create dialog
            dialog=new Dialog(context);
            //dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.p_gifloading);
            txtLoadingProgress =(TextView) dialog.findViewById(R.id.txtLoading2);
            txtLoadingProgress.setText("Logging in . .");

            String path = "file:///android_asset/kotak.gif";
            WebView wV = (WebView) dialog.findViewById(R.id.webView);
            wV.loadUrl(path);
            wV.setScrollBarStyle(WebView.OVER_SCROLL_NEVER);
            // disable scroll on touch
            wV.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });
            wV.setVerticalScrollBarEnabled(false);
            wV.setHorizontalScrollBarEnabled(false);

            dialog.show();
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "http://"+Web+"/ws/login.php?sopir="+UserInput+"&securecode="+getPref(TAG_DEVICEID);
            FN_JSONParser jParser = new FN_JSONParser();

            try {

                JSONObject json = jParser.getJSONFromUrl(url);
                StatusRequest = json.getString("STATUS");

                if(StatusRequest.equals("1")){
                    LoginArray = json.getJSONArray(TAG_LOGIN);
                    for (int i=0;i<LoginArray.length();i++){
                        JSONObject c = LoginArray.getJSONObject(i);
                        LastLogin = c.getString(TAG_KODE);
                        NameLogin = c.getString(TAG_NAMA);
                    }
                }
                showDialog = 1;

            } catch (JSONException e) {
                e.printStackTrace();
                showDialog=0;
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (showDialog){
                case 0:{
                    dialog.dismiss();
                    break;
                }
                case 1:{
                    txtLoadingProgress.setText("User Verified");
                    break;
                }
                default:break;
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            setPrefLogin(InputUsername.getText().toString(),NameLogin);
            if(StatusRequest.equals("1")){
                if (LastLogin.equals("0")){
                    Toast.makeText(getApplicationContext(),"Username/Password Salah!!",Toast.LENGTH_SHORT).show();
                }else{
                    setPrefDayLogin(getToday());
                    setPrefLogin(LastLogin,NameLogin);
                    db.UpdateSetting(LastLogin,NameLogin);
                    new UpdateMaster(ActivityMain.this).execute();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Login Gagal",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void DialodSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Keamanan");
        builder.setIcon(R.drawable.dfa_info_ups);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine();
        input.setHint("Masukkan Password");
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(11);
        input.setFilters(FilterArray);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equals(db.getToday()+"DFA")){
                    Intent in = new Intent(getApplicationContext(), ActivitySetting.class);
                    in.putExtra(TAG_SETTINGSTATUS,"0");
                    startActivity(in);
                }else{
                    Toast.makeText(getApplicationContext(),"Password Salah!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    public void setPref(String Web, String AppVersion, String DBVersion ){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor SettingPrefEditor = SettingPref.edit();
        SettingPrefEditor.putString(TAG_WEB,Web);
        SettingPrefEditor.putString(TAG_APPVERSION,AppVersion);
        SettingPrefEditor.putString(TAG_DBVERSION,DBVersion);
        SettingPrefEditor.commit();
    }

    public void setPrefLogin(String LastLogin,String NameLogin){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor SettingPrefEditor = SettingPref.edit();
        SettingPrefEditor.putString(TAG_NAMELOGIN,NameLogin);
        SettingPrefEditor.putString(TAG_LASTLOGIN,LastLogin);
        SettingPrefEditor.commit();
    }

    public void setPrefDayLogin(String TodayLogin){
        SharedPreferences DayLogin = getSharedPreferences(TAG_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor DayLoginEditor = DayLogin.edit();
        DayLoginEditor.putString(TAG_DAYLOGIN, TodayLogin);
        DayLoginEditor.commit();
    }

    public void setPrefDeviceID(String DeviceID){
        SharedPreferences Device = getSharedPreferences(TAG_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor DeviceIDEditor = Device.edit();
        DeviceIDEditor.putString(TAG_DEVICEID, DeviceID);
        DeviceIDEditor.commit();
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ActivityMain.this.finish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Yakin ingin keluar?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }
}
