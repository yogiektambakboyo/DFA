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
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
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


public class ActivityDeliveryOrder extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";
    private final String TAG_SHIPTO = "shipto";
    private final String TAG_FAKTUR = "faktur";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_ALAMAT = "alamat";
    private final String TAG_TOTALBAYAR = "totalbayar";
    private final String TAG_KODENOTA = "kodenota";
    private final String TAG_NOPICKLIST = "nopicklist";
    private final String TAG_KKPDVDATA = "KKPDVData";

    // Declare Variables
    ListView list;
    AdapterPelangganListView adapter;
    Spinner  SpnKKPDV;
    ImageView ImgBtnFilter;

    String[] KKPDV,ShipTo,Perusahaan,Alamat,Faktur,Kodenota,TotalBayar,NoPickList,Jumlah,KetPem;
    ArrayList<DataPelanggan> PelangganList = new ArrayList<DataPelanggan>();

    JSONArray PelangganArray,KKPDVArray,CekExistDeliveryArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_ENTRYTIME = "entrytime";

    private final String TAG_CEKDATA= "CekData";
    private final String TAG_STATUSKIRIM = "statuskirim";
    private final String TAG_KETERANGAN = "keterangan";
    private final String TAG_JUMLAH = "jumlah";
    private final String TAG_TUNAI = "tunai";
    private final String TAG_BG = "bg";
    private final String TAG_STEMPEL = "stempel";
    private final String TAG_TT = "tt";
    private final String TAG_TRANSFER = "transfer";

    private final String TAG_FAKTURTERPROSES = "fakturterproses";

    private String kodenota,nopicklist,faktur,shipto,perusahaan,totalbayar;
    Float   TotalRp=0.0f,GiroRp=0.0f,TotalTransfer=0.0f;
    int     GiroTotal = 0;
    String  GiroResume="",BankTransfer="";
    String FakturTot="",FakturTerproses ="";

    List FilterList;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_deliveryorder);

        FilterList = new ArrayList();

        TotalRp=0.0f;
        GiroRp=0.0f;
        GiroTotal = 0;
        GiroResume="";

        // Get Data From DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject PelangganJSON = null;
        JSONObject KKPDVJSON = null;


        final TextView TxtFakturT = (TextView) findViewById(R.id.DeliveryOrder_FakturTotal);
        final TextView TxtFakturTerproses = (TextView) findViewById(R.id.DeliveryOrder_FakturTerproses);

        if(dbFile.exists()){
            try {
                PelangganJSON = db.GetPelanggan(getPref(TAG_NAMELOGIN));
                // Getting Array of Pelanggan
                PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                int StatusPelanggan = PelangganJSON.getInt("status");

                if(StatusPelanggan==0){
                    DataPelanggan plg = new DataPelanggan("Error","Error" ,"Error", "Error", "Error", "0.0", "Error", "0","0");
                    PelangganList.add(plg);

                    KKPDV = new String[1];
                    KKPDV[0] = "0";
                }else{
                    ShipTo = new String[PelangganArray.length()];
                    Perusahaan = new String[PelangganArray.length()];
                    Alamat = new String[PelangganArray.length()];
                    Faktur = new String[PelangganArray.length()];
                    Kodenota = new String[PelangganArray.length()];
                    TotalBayar = new String[PelangganArray.length()];
                    NoPickList = new String[PelangganArray.length()];
                    Jumlah = new String[PelangganArray.length()];
                    KetPem = new String[PelangganArray.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < PelangganArray.length(); i++){
                        JSONObject c = PelangganArray.getJSONObject(i);

                        String ShipToS = c.getString(TAG_SHIPTO);
                        String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                        String AlamatS = c.getString(TAG_ALAMAT);
                        String FakturS = c.getString(TAG_FAKTUR);
                        String KodenotaS = c.getString(TAG_KODENOTA);
                        String TotalBayarS = c.getString(TAG_TOTALBAYAR);
                        String NoPickListS = c.getString(TAG_NOPICKLIST);
                        String JumlahS = c.getString(TAG_STATUSKIRIM);

                        ShipTo[i] = ShipToS;
                        Perusahaan[i] = PerusahaanS;
                        Alamat[i] = AlamatS;
                        Faktur[i] = FakturS;
                        Kodenota[i] = KodenotaS;
                        TotalBayar[i] = TotalBayarS;
                        NoPickList[i] = NoPickListS;
                        Jumlah[i] = JumlahS;

                        String KetPemS = "";
                        if(Float.parseFloat(c.getString(TAG_TUNAI))>0f){
                            if(Float.parseFloat(c.getString(TAG_BG))>0f){
                                if(Float.parseFloat(c.getString(TAG_TRANSFER))>0f){
                                    KetPemS = "TunaiBGTransfer";
                                }else{
                                    KetPemS = "TunaiBG";
                                }
                            }else if(Float.parseFloat(c.getString(TAG_TRANSFER))>0f){
                                KetPemS = "TunaiTransfer";
                            }else{
                                KetPemS = "Tunai";
                            }
                        }else if(c.getString(TAG_STEMPEL).equals("1")){
                            KetPemS = "Stempel";
                        }else if(Float.parseFloat(c.getString(TAG_BG))>0f){
                            if(Float.parseFloat(c.getString(TAG_TRANSFER))>0f){
                                KetPemS = "BGTransfer";
                            }else{
                                KetPemS = "BG";
                            }
                        }else if(c.getString(TAG_TT).equals("1")){
                            KetPemS = "TT";
                        }else if(Float.parseFloat(c.getString(TAG_TRANSFER))>0f){
                            KetPemS = "Transfer";
                        }else if((c.getString(TAG_STATUSKIRIM).equals("1"))&&(c.getString(TAG_TT).equals("0"))&&(c.getString(TAG_STEMPEL).equals("0"))&&(Float.parseFloat(c.getString(TAG_BG))<1f)&&(Float.parseFloat(c.getString(TAG_TUNAI))<1f)){
                            KetPemS = "Tidak Terkirim";
                        }else{
                            KetPemS = "";
                        }

                        KetPem[i] = KetPemS;

                    }

                    for (int i = 0; i < ShipTo.length; i++)
                    {
                        DataPelanggan plg = new DataPelanggan(ShipTo[i], Perusahaan[i], Alamat[i], Faktur[i], Kodenota[i], TotalBayar[i], NoPickList[i], Jumlah[i], KetPem[i]);
                        // Binds all strings into an array
                        PelangganList.add(plg);
                    }

                    KKPDVJSON = db.GetKKPDV();
                    KKPDVArray = KKPDVJSON.getJSONArray(TAG_KKPDVDATA);
                    KKPDV = new String[KKPDVArray.length()];
                    for(int j=0;j<KKPDVArray.length();j++){
                        JSONObject d = KKPDVArray.getJSONObject(j);

                        String KodenotaS = d.getString("kodenota");
                        KKPDV[j] = KodenotaS;
                    }

                    //Set Data for Spinner KKPDV
                    SpnKKPDV = (Spinner) findViewById(R.id.DeliveryOrder_SpnKKPDV);

                    ArrayAdapter adapterSPn = new ArrayAdapter(this,android.R.layout.simple_spinner_item, KKPDV);
                    adapterSPn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    SpnKKPDV.setAdapter(adapterSPn);

                    SpnKKPDV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            adapter.filter(KKPDV[position]);
                            kodenota = KKPDV[position];
                            JSONObject KKPDVInfo = null;

                            try {
                                KKPDVInfo = db.GetOneKKPDVInfo(kodenota);
                                FakturTot = KKPDVInfo.getString(TAG_FAKTUR);
                                FakturTerproses = KKPDVInfo.getString(TAG_FAKTURTERPROSES);
                            } catch (JSONException e) {
                                e.getMessage();
                            }

                            TxtFakturTerproses.setText(FakturTerproses);
                            TxtFakturT.setText(FakturTot);
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
        adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,"0");

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                nopicklist = adapter.getItem(position).getNoPickList();
                faktur = adapter.getItem(position).getFaktur();
                shipto = adapter.getItem(position).getShipTo();
                perusahaan = adapter.getItem(position).getPerusahaan();
                totalbayar = adapter.getItem(position).getTotalBayar();

                JSONObject CekExistJSON = null;
                try {
                    CekExistJSON = db.CekDeliveryOrder(faktur);
                    CekExistDeliveryArray = CekExistJSON.getJSONArray(TAG_CEKDATA);

                    for(int i = 0; i < CekExistDeliveryArray.length(); i++){
                        JSONObject c = CekExistDeliveryArray.getJSONObject(i);

                        String Tunai = c.getString(TAG_TUNAI);
                        String BG = c.getString(TAG_BG);
                        String Stempel = c.getString(TAG_STEMPEL);
                        String TT = c.getString(TAG_TT);
                        String StatusKirim = c.getString(TAG_STATUSKIRIM);
                        String Transfer = c.getString("transferjml");
                        String Tolakan = c.getString("tolakan");

                        TotalRp = Float.parseFloat(c.getString(TAG_TUNAI));
                        GiroRp = Float.parseFloat(c.getString(TAG_BG));
                        GiroResume = c.getString(TAG_KETERANGAN);
                        TotalTransfer = Float.parseFloat(c.getString("transferjml"));
                        BankTransfer = c.getString("transferbank");

                        if (c.getString(TAG_JUMLAH).equals("0")){
                            DialogOrderNotDelivered(kodenota,faktur,nopicklist);
                        }else{
                            if (StatusKirim.equals("2")){
                                Toast.makeText(getApplicationContext(),"Invoice Sudah di Sync",Toast.LENGTH_SHORT).show();
                            }else{
                                String keterangan = "Hello",SKUTolakan = " - ";
                                    if(TT.equals("1")){
                                        keterangan = "TT";
                                    }else if(Float.parseFloat(Transfer)>0f){
                                        DecimalFormatSymbols symbol =
                                                new DecimalFormatSymbols(Locale.GERMANY);
                                        symbol.setCurrencySymbol("");

                                        DecimalFormat formatter = (DecimalFormat)
                                                NumberFormat.getCurrencyInstance(Locale.GERMANY);
                                        formatter.setDecimalFormatSymbols(symbol);

                                        String currency = "";
                                        if((Float.parseFloat(Tunai)>0f)||(Float.parseFloat(BG)>0f)){
                                            if ((Float.parseFloat(Tunai)>0f)&&(Float.parseFloat(BG)>0f)){
                                                currency = formatter.format(Float.parseFloat(Transfer)+Float.parseFloat(Tunai)+Float.parseFloat(BG));
                                                keterangan = "Transfer+Tunai+BG = Rp. "+currency;
                                            }else if (Float.parseFloat(Tunai)>0f){
                                                currency = formatter.format(Float.parseFloat(Transfer)+Float.parseFloat(Tunai));
                                                keterangan = "Transfer+Tunai = Rp. "+currency;
                                            }else{
                                                currency = formatter.format(Float.parseFloat(Transfer)+Float.parseFloat(BG));
                                                keterangan = "Transfer+BG = Rp. "+currency;
                                            }
                                        }else{
                                            currency = formatter.format(Float.parseFloat(Transfer));
                                            keterangan = "Transfer = Rp. "+currency;
                                        }

                                    }else if (Float.parseFloat(BG)>0f){
                                        DecimalFormatSymbols symbol =
                                                new DecimalFormatSymbols(Locale.GERMANY);
                                        symbol.setCurrencySymbol("");

                                        DecimalFormat formatter = (DecimalFormat)
                                                NumberFormat.getCurrencyInstance(Locale.GERMANY);
                                        formatter.setDecimalFormatSymbols(symbol);

                                        String currency = "";
                                        if(Float.parseFloat(Tunai)>0f){
                                            currency = formatter.format(Float.parseFloat(BG)+Float.parseFloat(Tunai));
                                            keterangan = "BG+Tunai = Rp. "+currency;
                                        }else{
                                            currency = formatter.format(Float.parseFloat(BG));
                                            keterangan = "BG = Rp. "+currency;
                                        }

                                    }else if (Stempel.equals("1")){
                                        keterangan ="Stempel";
                                    }else if(Float.parseFloat(Tunai)>0f) {

                                        DecimalFormatSymbols symbol =
                                                new DecimalFormatSymbols(Locale.GERMANY);
                                        symbol.setCurrencySymbol("");

                                        DecimalFormat formatter = (DecimalFormat)
                                                NumberFormat.getCurrencyInstance(Locale.GERMANY);
                                        formatter.setDecimalFormatSymbols(symbol);
                                        String currency = formatter.format(Float.parseFloat(Tunai));
                                        keterangan = "Tunai Rp. "+currency;
                                    }else if(Tolakan.length()>2){
                                        keterangan = "Tolakan Penuh";
                                    }else{
                                        keterangan = "Tidak Terkirim";
                                    }
                                // Cek Tolakan
                                if(Tolakan.length()>2){
                                    SKUTolakan = db.GetCountTolakan(faktur);
                                }
                                DialogOrderExist(kodenota,faktur,nopicklist,keterangan,shipto, perusahaan,totalbayar,SKUTolakan);
                            }
                        }

                    }
                }catch (JSONException e){
                    e.getMessage();
                    Toast.makeText(getApplicationContext(), "DB Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImgBtnFilter = (ImageView) findViewById(R.id.DeliveryOrder_imgBtnFilter);
        ImgBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });

    }

    private void showFilter(){
        final Dialog dialog = new Dialog(ActivityDeliveryOrder.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Filter Pembayaran");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_filterpelanggan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        Spinner SpnArea = (Spinner) dialog.findViewById(R.id.Pelanggan_SpnArea);

        FilterList.clear();
        FilterList.add("Semua");
        FilterList.add("Tunai");
        FilterList.add("BG");
        FilterList.add("TT");
        FilterList.add("Stempel");
        FilterList.add("Tidak Terkirim");
        FilterList.add("Transfer");
        //FilterList.add("Belum Terproses");

        ArrayAdapter dataAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, FilterList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnArea.setAdapter(dataAdapter);

        SpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(FilterList.get(position).equals("Semua")){
                    adapter.filterKetPem("",SpnKKPDV.getSelectedItem().toString());
                }else{
                    adapter.filterKetPem(FilterList.get(position).toString(),SpnKKPDV.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dfa_filter);

        Button BtnTutup = (Button) dialog.findViewById(R.id.DeliveryOrder_FilterBtnSubmit);
        BtnTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public void DialogOrderExist(final String Kodenota, final String Faktur, final String NoPickList, final String Ket, final String ShipTo, final String Perusahaan, final  String TotalBayar, final String SKUTolakan){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtKodeNota = new TextView(this);
        TxtKodeNota.setText("KKPDV : "+Kodenota);

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);

        final TextView TxtPickList = new TextView(this);
        TxtPickList.setText("No Pick List : "+NoPickList);

        final TextView TxtPembayaran = new TextView(this);
        TxtPembayaran.setText("Pembayaran : "+Ket);

        final TextView TxtTolakan = new TextView(this);
        TxtTolakan.setText("Tolakan : "+SKUTolakan+" SKU");

        layout.addView(TxtFaktur,params);
        layout.addView(TxtKodeNota,params);
        layout.addView(TxtPickList,params);
        layout.addView(TxtPembayaran,params);
        layout.addView(TxtTolakan,params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogOrderEdit(Kodenota,Faktur,NoPickList,ShipTo,Perusahaan, TotalBayar);
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

    public void DialogOrderEdit(final String Kodenota, final String Faktur, final String NoPickList, final String ShipTo, final String Perusahaan, final  String TotalBayar){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtKodeNota = new TextView(this);
        TxtKodeNota.setText("Apakah anda yakin mengubah invoice ini?");

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);

        layout.addView(TxtKodeNota,params);
        layout.addView(TxtFaktur,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent ints = new Intent(getApplicationContext(), ActivityBarang.class);
                GiroTotal = GiroResume.length() - GiroResume.replace("#", "").length();
                ints.putExtra("GiroRp",GiroRp);
                ints.putExtra("GiroTotal",GiroTotal);
                ints.putExtra("GiroResume",GiroResume);
                ints.putExtra("TunaiTotal",TotalRp);
                ints.putExtra("TransferTotal",TotalTransfer);
                ints.putExtra("TransferBank",BankTransfer);
                ints.putExtra(TAG_FAKTUR, Faktur);
                ints.putExtra(TAG_NOPICKLIST, NoPickList);
                ints.putExtra(TAG_KODENOTA, Kodenota);
                ints.putExtra(TAG_SHIPTO, shipto);
                ints.putExtra(TAG_PERUSAHAAN, perusahaan);
                ints.putExtra(TAG_TOTALBAYAR, totalbayar);
                startActivity(ints);
            }
        });

        builder.show();
    }

    public void DialogOrderNotDelivered(final String Kodenota, final String Faktur, final String NoPickList){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtKodeNota = new TextView(this);
        TxtKodeNota.setText("Apakah anda yakin akan memproses invoice ini?");

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);

        layout.addView(TxtKodeNota,params);
        layout.addView(TxtFaktur,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Tidak Terkirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPrefEntryTime(getToday());
                Intent ints = new Intent(getApplicationContext(), ActivityInvoiceNotDelivery.class);
                ints.putExtra(TAG_FAKTUR, faktur);
                ints.putExtra(TAG_NOPICKLIST, nopicklist);
                ints.putExtra(TAG_KODENOTA, kodenota);
                ints.putExtra(TAG_SHIPTO, shipto);
                ints.putExtra(TAG_PERUSAHAAN, perusahaan);
                ints.putExtra(TAG_TOTALBAYAR, totalbayar);
                startActivity(ints);
            }
        });
        builder.setNegativeButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPrefEntryTime(getToday());
                Intent ints = new Intent(getApplicationContext(), ActivityBarang.class);
                ints.putExtra(TAG_FAKTUR, faktur);
                ints.putExtra(TAG_NOPICKLIST, nopicklist);
                ints.putExtra(TAG_KODENOTA, kodenota);
                ints.putExtra(TAG_SHIPTO, shipto);
                ints.putExtra(TAG_PERUSAHAAN, perusahaan);
                ints.putExtra(TAG_TOTALBAYAR, totalbayar);
                ints.putExtra("GiroRp",GiroRp);
                ints.putExtra("GiroTotal",GiroTotal);
                ints.putExtra("GiroResume",GiroResume);
                ints.putExtra("TunaiTotal",TotalRp);
                startActivity(ints);
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
}