package com.bcp.DFA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ActivityCheckerOrder extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";

    private final String TAG_TGL = "tgl";
    private final String TAG_KODE = "kode";
    private final String TAG_CREATEBY = "createby";
    private final String TAG_REASONCODE = "reasoncode";
    private final String TAG_NAMA = "nama";
    private final String TAG_STATUST = "statuskirim";
    private final String TAG_FAKTUR = "faktur";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_SKU = "sku";

    private final String TAG_KKPDVDATA = "KKPDVData";

    // Declare Variables
    ListView list;
    AdapterPelangganListView adapter;
    Spinner  SpnSopir;
    ImageView ImgBtnFilter;
    TextView TxtLabelSopirSpn,TxtLabelHeader;

    String[] Sopir,Tgl,CreateBy,ReasonCode,Nama,StatusT,Faktur,Perusahaan,Kode,SKU,Dummy;
    ArrayList<DataPelanggan> PelangganList = new ArrayList<DataPelanggan>();

    JSONArray PelangganArray,SopirArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_ENTRYTIME = "entrytime";

    List FilterList;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_deliveryorder);

        FilterList = new ArrayList();

        // Get Data From DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject PelangganJSON = null;
        JSONObject KKPDVJSON = null;


        final TextView TxtFakturT = (TextView) findViewById(R.id.DeliveryOrder_FakturTotal);
        final TextView TxtFakturTerproses = (TextView) findViewById(R.id.DeliveryOrder_FakturTerproses);
        final TextView TxtJdlFakturT = (TextView) findViewById(R.id.DeliveryOrder_JdlInv);
        final TextView TxtJdlFakturTerproses = (TextView) findViewById(R.id.DeliveryOrder_JdlInvPro);

        TxtFakturT.setVisibility(View.GONE);
        TxtJdlFakturT.setVisibility(View.GONE);
        TxtFakturTerproses.setVisibility(View.GONE);
        TxtJdlFakturTerproses.setVisibility(View.GONE);

        TxtLabelSopirSpn = (TextView) findViewById(R.id.DeliveryOrder_LabelSpn);
        TxtLabelSopirSpn.setText("Sopir");

        TxtLabelHeader = (TextView) findViewById(R.id.DeliveryOrder_LblDevOr);
        TxtLabelHeader.setText("TOLAKAN DELIVERY");

        if(dbFile.exists()){
            try {
                PelangganJSON = db.GetPelangganChecker();
                // Getting Array of Pelanggan
                PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);
                int StatusPelanggan = PelangganJSON.getInt("status");

                if(StatusPelanggan==0){
                    DataPelanggan plg = new DataPelanggan("Error","Error" ,"Error", "Error", "Error", "0.0", "Error", "0","");
                    PelangganList.add(plg);
                    Sopir = new String[1];
                    Sopir[0] = "0";
                }else{
                    Perusahaan = new String[PelangganArray.length()];
                    Tgl = new String[PelangganArray.length()];
                    Faktur = new String[PelangganArray.length()];
                    CreateBy =  new String[PelangganArray.length()];
                    ReasonCode = new String[PelangganArray.length()];
                    Nama = new String[PelangganArray.length()];
                    StatusT = new String[PelangganArray.length()];
                    Kode = new String[PelangganArray.length()];
                    SKU  = new String[PelangganArray.length()];
                    Dummy = new String[PelangganArray.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < PelangganArray.length(); i++){

                        JSONObject c = PelangganArray.getJSONObject(i);


                        String FakturS = c.getString(TAG_FAKTUR);
                        String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                        String TglS = c.getString(TAG_TGL);
                        String CreateByS = c.getString(TAG_CREATEBY);
                        String ReasonCodeS = c.getString(TAG_REASONCODE);
                        String NamaS = c.getString(TAG_NAMA);
                        String KodeS = c.getString(TAG_KODE);
                        String StatusTS = c.getString(TAG_STATUST);
                        String SKUS = c.getString(TAG_SKU);


                        Tgl[i] = TglS;
                        Perusahaan[i] = PerusahaanS;
                        Faktur[i] = FakturS;
                        CreateBy[i] = CreateByS;
                        ReasonCode[i] = ReasonCodeS;
                        Nama[i] = NamaS;
                        StatusT[i] = StatusTS;
                        Kode[i] = KodeS;
                        SKU[i] = SKUS;
                        Dummy[i] = "0";
                    }


                    for (int i = 0; i < PelangganArray.length(); i++)
                    {
                        DataPelanggan plg = new DataPelanggan(Kode[i], Perusahaan[i], Tgl[i], Faktur[i], CreateBy[i], SKU[i], Nama[i], StatusT[i],Dummy[i]);
                        // Binds all strings into an array
                        PelangganList.add(plg);
                    }

                    KKPDVJSON = db.GetSopirTolakan();
                    SopirArray = KKPDVJSON.getJSONArray(TAG_KKPDVDATA);
                    Sopir = new String[SopirArray.length()];
                    for(int j=0;j<SopirArray.length();j++){
                        JSONObject d = SopirArray.getJSONObject(j);

                        String KodenotaS = d.getString("createby") +" - "+d.getString("nama");
                        Sopir[j] = KodenotaS;
                    }

                    //Set Data for Spinner KKPDV
                    SpnSopir = (Spinner) findViewById(R.id.DeliveryOrder_SpnKKPDV);

                    ArrayAdapter adapterSPn = new ArrayAdapter(this,android.R.layout.simple_spinner_item, Sopir);
                    adapterSPn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    SpnSopir.setAdapter(adapterSPn);

                    SpnSopir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            adapter.filterTolakan(Sopir[position]);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            db.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        list = (ListView) findViewById(R.id.DeliveryOrderListView);

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,"1");


        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(adapter.getItem(position).getStatusKirim().equals("0")){
                    DialogKonfirmasiProses(adapter.getItem(position).getFaktur(),adapter.getItem(position).getTotalBayar(),SpnSopir.getSelectedItem().toString());
                }else if (adapter.getItem(position).getStatusKirim().equals("1")){
                    DialogKonfirmasiEdit(adapter.getItem(position).getFaktur(),SpnSopir.getSelectedItem().toString());
                }else{
                    Toast.makeText(getApplicationContext(),"Faktur : "+adapter.getItem(position).getFaktur()+" telah diupload ke server ",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImgBtnFilter = (ImageView) findViewById(R.id.DeliveryOrder_imgBtnFilter);
        ImgBtnFilter.setVisibility(View.GONE);

    }


    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
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

    public void setPrefEntryTime(String EntryTime){
        SharedPreferences EntryTimePref = getSharedPreferences(TAG_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor EntryTimeEditor = EntryTimePref.edit();
        EntryTimeEditor.putString(TAG_ENTRYTIME, EntryTime);
        EntryTimeEditor.commit();
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public void DialogKonfirmasiProses(final String Faktur,final String Tolakan, final String Sopir){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Tolakan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtKodeNota = new TextView(this);
        TxtKodeNota.setText("Apakah anda yakin akan memproses tolakan ini?");

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);

        final TextView TxtTolakan = new TextView(this);
        TxtTolakan.setText("Tolakan : "+Tolakan+" SKU");


        final TextView TxtDelivery = new TextView(this);
        TxtDelivery.setText("Delivery : "+Sopir+" SKU");

        layout.addView(TxtKodeNota,params);
        layout.addView(TxtFaktur,params);
        layout.addView(TxtTolakan,params);
        layout.addView(TxtDelivery,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(getApplicationContext(),ActivityCheckerTolakan.class);
                in.putExtra(TAG_FAKTUR,Faktur);
                startActivity(in);
            }
        });

        builder.show();
    }

    public void DialogKonfirmasiEdit(final String Faktur, final String Sopir){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Tolakan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtKodeNota = new TextView(this);
        TxtKodeNota.setText("Apakah anda yakin akan mengubah tolakan ini?");

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);

        int Tolakan = 0;
        String Status = "Valid";
        try {
            Tolakan = db.GetCountTolakanEdit(Faktur);
            Status =  db.GetStatusTolakanEdit(Faktur);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final TextView TxtTolakan = new TextView(this);
        TxtTolakan.setText("Tolakan : "+Tolakan+" SKU");


        final TextView TxtDelivery = new TextView(this);
        TxtDelivery.setText("Delivery : "+Sopir+" SKU");

        final TextView TxtStatus = new TextView(this);
        TxtStatus.setText("Status : "+Status);

        layout.addView(TxtKodeNota,params);
        layout.addView(TxtFaktur,params);
        layout.addView(TxtTolakan,params);
        layout.addView(TxtDelivery,params);
        layout.addView(TxtStatus,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(getApplicationContext(),ActivityCheckerTolakan.class);
                in.putExtra(TAG_FAKTUR,Faktur);
                startActivity(in);
            }
        });

        builder.show();
    }
}