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


public class ActivityCheckerSuccess extends Activity {
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
    ListView list,list2;
    AdapterPelangganListView adapter;
    AdapterBarangTolakanListView adapterBrg;
    Spinner  SpnSopir;
    ImageView ImgBtnFilter;
    TextView TxtLabelSopirSpn,TxtLabelHeader;

    String[] Sopir,Tgl,CreateBy,ReasonCode,Nama,StatusT,Faktur,Perusahaan,Kode,SKU,Dummy,Jml,NamaBrg;
    ArrayList<DataPelanggan> PelangganList = new ArrayList<DataPelanggan>();

    JSONArray PelangganArray,SopirArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_ENTRYTIME = "entrytime";

    List FilterList;
    ArrayList<DataBarang> BarangList = new ArrayList<DataBarang>();
    JSONArray BarangArray;

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
        TxtLabelHeader.setText("TOLAKAN SUCCESS");

        if(dbFile.exists()){
            try {
                PelangganJSON = db.GetPelangganCheckerUpload();
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
                ShowDetailTolakan(adapter.getItem(position).getFaktur(),adapter.getItem(position).getStatusKirim());
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

    private void ShowDetailTolakan(String Faktur,String StatusT){
        final Dialog dialog = new Dialog(ActivityCheckerSuccess.this, android.R.style.Theme_Dialog);
        if (StatusT.equals("1")){
            StatusT = "Valid";
        }else {
            StatusT = "Tidak Valid";
        }
        dialog.setTitle("Detail ("+StatusT+")");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.p_barangdetailtolakan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.p_barangdetailtolakan, null);

        BarangList.clear();

        JSONObject BarangJSON = null;

        try {
            BarangJSON = db.GetBarangTolakanUpload(Faktur);
            // Getting Array of Barang
            BarangArray = BarangJSON.getJSONArray("BarangData");

            Jml = new String[BarangArray.length()];
            NamaBrg = new String[BarangArray.length()];

            // looping through All Barang
            for(int i = 0; i < BarangArray.length(); i++){
                JSONObject c = BarangArray.getJSONObject(i);

                String jml = c.getString("jml");
                String keterangan = c.getString("keterangan");

                Jml[i] = jml;
                NamaBrg[i] = keterangan;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        list2 = (ListView) dialog.findViewById(R.id.listview);
        for (int i = 0; i < BarangArray.length(); i++)
        {

            DataBarang brg = new DataBarang("","",Jml[i],"0","0","0", NamaBrg[i], "0");
            // Binds all strings into an array
            BarangList.add(brg);
        }

        // Pass results to ListViewAdapter Class
        adapterBrg = new AdapterBarangTolakanListView(this, BarangList);

        list2.setAdapter(adapterBrg);

        Button BtnTutup = (Button) dialog.findViewById(R.id.BarangTolakan_BtnTutup);
        BtnTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dfa_info_down);
    }

}