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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ActivityDeliverySuccess extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";
    private final String TAG_SHIPTO = "shipto";
    private final String TAG_FAKTUR = "faktur";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_TOTALBAYAR = "totalbayar";
    private final String TAG_KODENOTA = "kodenota";
    private final String TAG_KKPDVDATA = "KKPDVData";

    // Declare Variables
    ListView list,list2;
    AdapterPelangganUploadedListView adapter;
    AdapterBarangTolakanListView adapterBrg;
    Spinner SpnKKPDV;

    String[] KKPDV,ShipTo,Perusahaan,Faktur,Kodenota,TotalBayar,Jumlah,Keterangan,Tolakan,Jml,NamaBrg;
    ArrayList<DataPelangganUploaded> PelangganList = new ArrayList<DataPelangganUploaded>();

    JSONArray PelangganArray,KKPDVArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_NAMELOGIN = "namelogin";

    private final String TAG_STATUSKIRIM = "statuskirim";
    private final String TAG_TUNAI = "tunai";
    private final String TAG_BG = "bg";
    private final String TAG_STEMPEL = "stempel";
    private final String TAG_TT = "tt";
    private final String TAG_TOLAKAN = "tolakan";

    private String kodenota;

    JSONArray BarangArray;

    ArrayList<DataBarang> BarangList = new ArrayList<DataBarang>();


    List AreaList;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_deliverysucces);

        AreaList = new ArrayList();

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject PelangganJSON = null;
        JSONObject KKPDVJSON = null;

        if(dbFile.exists()){
            try {
                PelangganJSON = db.GetPelangganUploaded(getPref(TAG_NAMELOGIN));
                // Getting Array of Pelanggan
                PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                int StatusPelanggan = PelangganJSON.getInt("status");

                if(StatusPelanggan==0){
                    DataPelangganUploaded plg = new DataPelangganUploaded("Error","Error" ,"Error", "Error", "Error", "0.0", "Error", "2");
                    PelangganList.add(plg);

                    KKPDV = new String[1];
                    KKPDV[0] = "0";

                }else{
                    ShipTo = new String[PelangganArray.length()];
                    Perusahaan = new String[PelangganArray.length()];
                    Faktur = new String[PelangganArray.length()];
                    Kodenota = new String[PelangganArray.length()];
                    TotalBayar = new String[PelangganArray.length()];
                    Keterangan = new String[PelangganArray.length()];
                    Jumlah = new String[PelangganArray.length()];
                    Tolakan = new String[PelangganArray.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < PelangganArray.length(); i++){
                        JSONObject c = PelangganArray.getJSONObject(i);

                        //status = c.getInt(TAG_STATUS);
                        String ShipToS = c.getString(TAG_SHIPTO);
                        String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                        String FakturS = c.getString(TAG_FAKTUR);
                        String KodenotaS = c.getString(TAG_KODENOTA);
                        String TotalBayarS = c.getString(TAG_TOTALBAYAR);
                        String JumlahS = c.getString(TAG_STATUSKIRIM);
                        String TolakanS = c.getString(TAG_TOLAKAN);


                        ShipTo[i] = ShipToS;
                        Perusahaan[i] = PerusahaanS;
                        Faktur[i] = FakturS;
                        Kodenota[i] = KodenotaS;
                        TotalBayar[i] = TotalBayarS;
                        Tolakan[i] = TolakanS;

                        DecimalFormatSymbols symbol =
                                new DecimalFormatSymbols(Locale.GERMANY);
                        symbol.setCurrencySymbol("");

                        DecimalFormat formatter = (DecimalFormat)
                                NumberFormat.getCurrencyInstance(Locale.GERMANY);
                        formatter.setDecimalFormatSymbols(symbol);

                        if(Float.parseFloat(c.getString("transferjml"))>0f){
                            if ((Float.parseFloat(c.getString(TAG_TUNAI))>0f)&&(Float.parseFloat(c.getString(TAG_BG))>0f)){
                                Keterangan[i] = "Rp "+formatter.format((Float.parseFloat(c.getString(TAG_TUNAI)))+(Float.parseFloat(c.getString(TAG_BG)))+(Float.parseFloat(c.getString("transferjml"))))+" (Tunai+Transfer+BG)";
                            }else if(Float.parseFloat(c.getString(TAG_TUNAI))>0f){
                                Keterangan[i] = "Rp "+formatter.format((Float.parseFloat(c.getString(TAG_TUNAI)))+(Float.parseFloat(c.getString("transferjml"))))+" (Tunai+Transfer)";
                            }else if(Float.parseFloat(c.getString(TAG_BG))>0f){
                                Keterangan[i] = "Rp "+formatter.format((Float.parseFloat(c.getString(TAG_BG)))+(Float.parseFloat(c.getString("transferjml"))))+" (Transfer+BG)";
                            }else{
                                Keterangan[i] = "Rp "+formatter.format(Float.parseFloat(c.getString("transferjml")))+" (Transfer)";
                            }
                        }else if ((Float.parseFloat(c.getString(TAG_TUNAI))>0f)&&(Float.parseFloat(c.getString(TAG_BG))>0f)){
                            Keterangan[i] = "Rp "+formatter.format((Float.parseFloat(c.getString(TAG_TUNAI)))+(Float.parseFloat(c.getString(TAG_BG))))+" (Tunai+BG)";
                        }else if (Float.parseFloat(c.getString(TAG_TUNAI))>0f){
                            Keterangan[i] = "Rp "+formatter.format(Float.parseFloat(c.getString(TAG_TUNAI)))+" (Tunai)";
                        }else if (Float.parseFloat(c.getString(TAG_BG))>0f){
                            Keterangan[i] = "Rp "+formatter.format(Float.parseFloat(c.getString(TAG_BG)))+" (BG)";
                        }else if (c.getString(TAG_TT).equals("1")){
                            Keterangan[i] = "TT";
                        }else if( (c.getString(TAG_STEMPEL).equals("1"))){
                            Keterangan[i] = "Stempel";
                        }else if(TolakanS.length()>2){
                            Keterangan[i] = "Tolakan Penuh";
                        }else{
                            Keterangan[i] = "Belum Terkirim";
                        }
                        Jumlah[i] = JumlahS;

                    }

                    for (int i = 0; i < PelangganArray.length(); i++)
                    {
                        DataPelangganUploaded plg = new DataPelangganUploaded(ShipTo[i], Perusahaan[i]," ", Faktur[i], Kodenota[i], TotalBayar[i], Jumlah[i],Keterangan[i]);
                        PelangganList.add(plg);
                    }

                    KKPDVJSON = db.GetKKPDVUploaded();
                    KKPDVArray = KKPDVJSON.getJSONArray(TAG_KKPDVDATA);
                    KKPDV = new String[KKPDVArray.length()];
                    for(int j=0;j<KKPDVArray.length();j++){
                        JSONObject d = KKPDVArray.getJSONObject(j);

                        String KodenotaS = d.getString("kodenota");
                        KKPDV[j] = KodenotaS;
                    }

                    //Set Data for Spinner KKPDV
                    SpnKKPDV = (Spinner) findViewById(R.id.DeliverySuccess_SpnKKPDV);

                    ArrayAdapter adapterSPn = new ArrayAdapter(this,android.R.layout.simple_spinner_item, KKPDV);
                    adapterSPn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    SpnKKPDV.setAdapter(adapterSPn);

                    SpnKKPDV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getApplicationContext(),KKPDV[position],Toast.LENGTH_SHORT).show();
                            adapter.filter(KKPDV[position]);
                            kodenota = KKPDV[position];
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

        list = (ListView) findViewById(R.id.DeliverySuccessListView);

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPelangganUploadedListView(getApplicationContext(), PelangganList);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DialogDetail(adapter.getItem(position).getFaktur(),adapter.getItem(position).getPerusahaan());
            }
        });


    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public void DialogDetail(final String Faktur, final String Perusahaan){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Pembayaran");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);
        LinearLayout.LayoutParams paramstab = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramstab.setMargins(30, 0, 30, 0);
        String Tunai="",CekBG="",Transfer="",Tolakan="0";


        JSONObject FakturInfo = null;
        try {
            FakturInfo = db.GetOneKKPDVInfoSuccess(Faktur);

            DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
            symbol.setCurrencySymbol("");
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
            formatter.setDecimalFormatSymbols(symbol);

            Tunai = formatter.format(Float.parseFloat(FakturInfo.getString("tunai")));
            CekBG = formatter.format(Float.parseFloat(FakturInfo.getString("bg")));
            Transfer = formatter.format(Float.parseFloat(FakturInfo.getString("transfer")));
            Tolakan = FakturInfo.getString("tolakan");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String Tlk = Tolakan;


        final TextView TxtKodeNota = new TextView(this);
        TxtKodeNota.setText("Perusahaan : "+Perusahaan);

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);

        final TextView TxtPembayaran = new TextView(this);
        TxtPembayaran.setText("Pembayaran : ");

        final TextView TxtPembayaranTn = new TextView(this);
        TxtPembayaranTn.setText("Tunai    : Rp. "+Tunai);

        final TextView TxtPembayaranTr = new TextView(this);
        TxtPembayaranTr.setText("Transfer : Rp. "+Transfer);

        final TextView TxtPembayaranBG = new TextView(this);
        TxtPembayaranBG.setText("Cek/BG   : Rp. "+CekBG);

        final TextView TxtTolakan = new TextView(this);
        TxtTolakan.setText("Tolakan : "+Tolakan+" SKU");

        layout.addView(TxtFaktur,params);
        layout.addView(TxtKodeNota,params);
        layout.addView(TxtPembayaran,params);
        layout.addView(TxtPembayaranTn,paramstab);
        layout.addView(TxtPembayaranTr,paramstab);
        layout.addView(TxtPembayaranBG,paramstab);
        layout.addView(TxtTolakan,params);
        builder.setView(layout);

        builder.setPositiveButton("Detail Tolakan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(Tlk.equals("0")){
                    Toast.makeText(getApplicationContext(),"Tidak ada tolakan",Toast.LENGTH_SHORT).show();
                }else{
                    ShowDetailTolakan(Faktur);
                }
            }
        });

        builder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return false;
    }

    private void ShowDetailTolakan(String Faktur){
        final Dialog dialog = new Dialog(ActivityDeliverySuccess.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Detail Tolakan");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.p_barangdetailtolakan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.p_barangdetailtolakan, null);

        BarangList.clear();

        JSONObject BarangJSON = null;

        try {
            BarangJSON = db.GetBarangTolakan(Faktur);
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