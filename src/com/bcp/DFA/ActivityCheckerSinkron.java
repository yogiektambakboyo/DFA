package com.bcp.DFA;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class ActivityCheckerSinkron extends ListActivity {
    private final String TAG_MENU = "menu";
    private final String TAG_ICON = "1";
    private final String TAG_ID = "id";


    String Menu;

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.dfa_up,
            R.drawable.dfa_down,
            R.drawable.dfa_manual,
            R.drawable.ic_launcher
    };

    private int serverResponseCode = 0;
    private String upLoadServerUri = "http://192.168.31.10:9020/ws/uploadfile.php";
    ProgressDialog dialog;
    private String FilePath= Environment.getExternalStorageDirectory()+"/foto.zip";


    //DB Handler
    private FN_DBHandler dbmst;
    private FN_NetConStatus netStatus;
    private final String DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private final String DB_MASTER="MASTER";

    private String TAG_PREF="SETTINGPREF";


    // Tabel Pelanggan
    private final String TAG_KODE = "Kode";


    private FN_DBHandler db;
    private String DB_PATH_CSV_TEMP=Environment.getExternalStorageDirectory()+"/DFA/CSV/TEMP";
    private String DB_PATH_CSV_SUCCESS=Environment.getExternalStorageDirectory()+"/DFA/CSV/SUCCESS";

    //Tabel CheckerMaster
    private final String TAG_DELIVERYORDER = "deliverydata";
    private final String TAG_TGL = "Tgl";
    private final String TAG_FAKTUR = "Faktur";
    private final String TAG_BRG = "Brg";
    private final String TAG_JML = "Jml";
    private final String TAG_JMLPCS = "JmlPCS";
    private final String TAG_CREATEBY = "CreateBy";
    private final String TAG_REASONCODE = "ReasonCode";
    private final String TAG_PERUSAHAAN = "Perusahaan";
    private final String TAG_KETERANGAN = "Keterangan";
    private final String TAG_WEB = "web";
    private final String TAG_RASIOMAX = "RasioMax";

    private final String TAG_NAMA = "Nama";

    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LASTLOGIN = "lastlogin";


    JSONArray DeliveryOrderArray = null;

    String StatusRequest="0",Web,filename;

    JSONArray UploadInfoArray = null;
    String kkpdv,faktur;

    TextView TxtFakturBelumTerproses,TxtLblFaktur;

    private String getDateTime(String format,int hari) {
        Calendar calendar = Calendar.getInstance();
        Date myDate = new Date();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, hari);
        myDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        return dateFormat.format(myDate);
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_sinkronisasi);

        netStatus = new FN_NetConStatus();

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);
        Web = getPref(TAG_WEB);

        upLoadServerUri = "http://"+Web+"/uploadfile.php";

        // Get Data From DB MASTER
        dbmst = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> mapupload = new HashMap<String, String>();

        mapupload.put(TAG_ICON, Integer.toString(flags[0]));
        mapupload.put(TAG_ID, "0");
        mapupload.put(TAG_MENU, "Upload");

        HashMap<String, String> mapdownload = new HashMap<String, String>();
        mapdownload.put(TAG_ICON, Integer.toString(flags[1]));
        mapdownload.put(TAG_ID, "1");
        mapdownload.put(TAG_MENU, "Download");

        HashMap<String, String> mapmanual = new HashMap<String, String>();
        mapmanual.put(TAG_ICON, Integer.toString(flags[2]));
        mapmanual.put(TAG_ID, "2");
        mapmanual.put(TAG_MENU, "Manual");

        OperatorMenuList.add(mapupload);
        OperatorMenuList.add(mapdownload);
        //OperatorMenuList.add(mapmanual);

        /**
         * Updating parsed JSON data into ListView
         * */
        ListAdapter adapter = new AdapterCustomSimple(this, OperatorMenuList,
                R.layout.l_mainmenu,
                new String[] { TAG_ICON, TAG_MENU, TAG_ID },
                new int[] { R.id.imageViewOP, R.id.MainMenuNama, R.id.MainMenuID });

        setListAdapter(adapter);

        // selecting single ListView item
        ListView lv = getListView();

        // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String nama = ((TextView) view.findViewById(R.id.MainMenuNama)).getText().toString();
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();

               if(menuid.equals("0")){
                    if (dbmst.cekCheckerUpload().equals("0")){
                        Toast.makeText(getApplicationContext(),"Tidak Ada Invoice Yang Di Upload",Toast.LENGTH_SHORT).show();
                    }else {
                        generateCSV(getPref(TAG_LASTLOGIN));
                        FilePath = DB_PATH_CSV_SUCCESS+"/"+filename;
                        dialog = ProgressDialog.show(ActivityCheckerSinkron.this, "", "Uploading file...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                uploadFile(FilePath,filename);
                            }
                        }).start();
                    }

                }

                if(menuid.equals("1")){
                    if(netStatus.getConnectivityStatusString(getApplicationContext())==1){
                        String c = dbmst.cekCheckerUpload();
                        if (c.equals("0")){
                            new UpdateMaster(ActivityCheckerSinkron.this).execute();
                        }else{
                            Toast.makeText(getApplicationContext(),"Masih ada tolakan yang harus di upload,silahkan upload dahulu!!",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Pastikan Koneksi WiFi Aktif!!",Toast.LENGTH_SHORT).show();
                    }
                }


                /*if(menuid.equals("2")){
                    String c = dbmst.cekCheckerUpload();
                    if (c.equals("0")){
                        Toast.makeText(getApplicationContext(),"Tidak Ada Tolakan Yang Di Sync",Toast.LENGTH_SHORT).show();
                    }else {
                        if(generateCSV(getPref(TAG_LASTLOGIN))){
                            Toast.makeText(getApplicationContext(),"Generate Success",Toast.LENGTH_SHORT).show();
                            //dbmst.UpdateDeliveryOrderUpload(getPref(TAG_LASTLOGIN),getToday());
                        }else{
                            Toast.makeText(getApplicationContext(),"Generate Gagal",Toast.LENGTH_SHORT).show();
                        }

                    }

                }*/

            }
        });

        TxtFakturBelumTerproses = (TextView) findViewById(R.id.DeliverySuccess_FakturBelumTerproses);
        TxtFakturBelumTerproses.setVisibility(View.GONE);

        TxtLblFaktur = (TextView) findViewById(R.id.Sinkron_Lbl);
        TxtLblFaktur.setVisibility(View.GONE);

        dbmst.close();
    }

    public int uploadFile(String sourceFileUri,final String filename) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :" + FilePath);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ActivityCheckerSinkron.this, "File Tidak Ditemukan " + FilePath, Toast.LENGTH_SHORT).show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String url = "http://"+Web+"/updatechecker.php?filename="+filename;
                            final FN_JSONParser jParser = new FN_JSONParser();

                            try {
                                JSONObject json = jParser.getJSONFromUrl(url);
                                UploadInfoArray = json.getJSONArray("deliverydata");

                                for (int i=0;i<UploadInfoArray.length();i++){
                                    JSONObject a = UploadInfoArray.getJSONObject(i);
                                    StatusRequest = a.getString("status");
                                }

                                if(StatusRequest.equals("1")){
                                    dbmst.UpdateCheckerApprovedStatusKirim();
                                    Toast.makeText(ActivityCheckerSinkron.this, "Unggah Data Berhasil.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ActivityCheckerSinkron.this, "Unggah Data Gagal 2."+" - "+UploadInfoArray.length(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(ActivityCheckerSinkron.this, "Unggah Data Gagal.", Toast.LENGTH_SHORT).show();
                                Log.e("123",e.getMessage());
                            }

                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivityCheckerSinkron.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivityCheckerSinkron.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    public class UpdateMaster extends AsyncTask<Void, Integer, Void> {

        Context context;
        Handler handler;
        Dialog dialog;
        TextView txtLoadingProgress;
        android.widget.ProgressBar ProgressBar;
        int PelangganSize=0;
        int showDialog=0;
        int DeliveryOrderSize=0;


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
            //ProgressBar =(ProgressBar)dialog.findViewById(R.id.progressBar);
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
            String url = "http://"+Web+"/ws/checker.php?sopir=3088";
            final FN_JSONParser jParser = new FN_JSONParser();

            try {
                JSONObject json = jParser.getJSONFromUrl(url);
                StatusRequest = json.getString("STATUS");

                if(StatusRequest.equals("1")){
                    showDialog=2;

                    // Get JSON KKPDV
                    dbmst.DeleteCheckerMaster();
                    DeliveryOrderArray = json.getJSONArray(TAG_DELIVERYORDER);

                    DeliveryOrderSize = DeliveryOrderArray.length();

                    for (int k=0;k<DeliveryOrderSize;k++){
                        JSONObject d = DeliveryOrderArray.getJSONObject(k);
                        dbmst.InsertCheckerMaster(d.getString(TAG_TGL),d.getString(TAG_FAKTUR),d.getString(TAG_BRG),d.getInt(TAG_JML),d.getString(TAG_CREATEBY),d.getString(TAG_REASONCODE),d.getString(TAG_NAMA),d.getString(TAG_KODE),d.getString(TAG_PERUSAHAAN),d.getString(TAG_KETERANGAN),d.getString(TAG_RASIOMAX),d.getInt(TAG_JMLPCS));
                        publishProgress(k);
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
            if(StatusRequest.equals("1")){
                Toast.makeText(getApplicationContext(),"Download Berhasil",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Download Gagal",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean generateCSV(String lastlogin){
        File SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        if(!SFAdircsv.exists()){
            SFAdircsv.mkdirs();
        }

/*        SFAdircsv = new File(DB_PATH_CSV_TEMP);
        if(!SFAdircsv.exists()){
            SFAdircsv.mkdirs();
        }*/

        //--------------------delete file 1 minggu
        SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        for (File f : SFAdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }
/*        SFAdircsv = new File(DB_PATH_CSV_TEMP);
        for (File f : SFAdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }*/
        //final JSONObject jresult = new JSONObject();
        filename=lastlogin+"_"+getDateTime("ddMMyyyy_HHmm",0)+".csv";

        //---------------------create file-----------------------------------
        try {
            db=new FN_DBHandler(getApplicationContext(),DB_PATH,DB_MASTER);
            Cursor cursor= db.getTolakanRAW();

            FileWriter fw = new FileWriter(DB_PATH_CSV_SUCCESS+"/"+filename);
            fw.append("faktur;");
            fw.append("brg;");
            fw.append("jml;");
            fw.append("createby;");
            fw.append("reasoncode;");
            fw.append("operator;");
            fw.append("statust;");
            fw.append("tambahan;");
            fw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    fw.append(cursor.getString(cursor.getColumnIndex("faktur"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("brg"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("jml"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("createby"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("reasoncode"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("operator"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("statust"))+";");
                    fw.append(""+"\n");
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // fw.flush();
            fw.close();
            db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Generate CSV Gagal!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean deleteCSV(String nama){
        File file = new File(DB_PATH_CSV_TEMP,nama);
        if(file.exists()){
            boolean deleted = file.delete();
            return deleted;
        }
        return true;
    }
    public boolean moveCSVsuccess(String nama){
        File from = new File(DB_PATH_CSV_TEMP,nama);
        File to = new File(DB_PATH_CSV_SUCCESS,nama);
        boolean status = from.renameTo(to);
        if(status){
            status=from.delete();
        }
        return status;
    }

    public boolean deleteMaster_temp(){
        File file = new File(DB_PATH,"MASTER");
        if(file.exists()){
            boolean deleted = file.delete();
            return deleted;
        }
        return true;
    }
    public boolean ceckexistMaster(){
        File file = new File(DB_PATH,"MASTER");
        if(file.exists()){
            return true;
        }
        return false;
    }
    public boolean renameMaster_temp(){
        File from = new File(DB_PATH,DB_MASTER);
        File to = new File(DB_PATH,"MASTER");
        boolean rename = from.renameTo(to);
        return rename;
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(getApplicationContext(), ActivityMainMenuChecker.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return false;
    }
}