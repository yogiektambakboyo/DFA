package com.bcp.DFA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
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

public class ActivityTolakan extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";

    private final String TAG_FAKTUR = "faktur";

    private final String TAG_SKU = "brg";
    private final String TAG_KETERANGAN = "keterangan";
    private final String TAG_HINT = "hint";
    private final String TAG_JML = "jml";
    private final String TAG_HRGSATUAN = "hrgsatuan";
    private final String TAG_DISCRP = "discrp";
    private final String TAG_RASIOMAX = "rasiomax";
    private final String TAG_JMLFAKTURCRT = "jmlcrt";


    // Declare Variables
    ListView list;
    AdapterBarangTolakan adapter;
    EditText editsearch;
    TextView TxtSKUTotal,TxtSKUChecked;
    Button   BtnSubmit;
    ImageView ImgBtnFilter;

    String[] SKU,Hint,Jml,HrgSatuan,Assigned,AssignedImg,Keterangan,DiscRp,JmlCRT,JmlPCS,RasioMax,JmlFakturCRT;
    ArrayList<DataBarangTolakan> BarangList = new ArrayList<DataBarangTolakan>();
    List<DataBarangTolakan> barangdatatolakan = null;

    JSONArray BarangArray;
    private final String TAG_BARANGDATA= "BarangData";

    int[] flags = new int[]{
            R.drawable.dfa_check,R.drawable.dfa_error,R.drawable.dfa_none
    };

    private String faktur,alasan="";

    List FilterList;
    String[] AlasanArray;
    //ArrayList<DataBarangTolakan> arraylisttolakan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_tolakan);


        //A01	Toko Tutup/Tidak terima Pengiriman
        //A02	Tolak Sebagian/semua (Toko tidak punya dana)
        //A04	Tidak sesuai alamat/Alamat tidak ditemukan
        //A05	Harga Salah/Selisih Diskon
        //A06	PO Mati/Tidak Ada PO
        //A07	Tidak Terkirim
        //A08	Barang Kosong Dari Gudang
        //A09	ED Dekat
        //A10	Tidak Sesuai Pesanan
        //A11	Salah Ketik
        //A12	Salah Picking Gudang
        //A13	Tidak Sesuai FJP Delivery
        //A14	Toko Tidak Order
        //A15	Barcode Salah

        AlasanArray = new String[15];
        AlasanArray[0] = "Toko Tutup/Tidak terima Pengiriman";
        AlasanArray[1] = "Tolak Sebagian/semua (Toko tidak punya dana)";
        AlasanArray[2] = "Tidak sesuai alamat/Alamat tidak ditemukan";
        AlasanArray[3] = "Tidak sesuai alamat/Alamat tidak ditemukan";
        AlasanArray[4] = "Harga Salah/Selisih Diskon";
        AlasanArray[5] = "PO Mati/Tidak Ada PO";
        AlasanArray[6] = "Tidak Terkirim";
        AlasanArray[7] = "Barang Kosong Dari Gudang";
        AlasanArray[8] = "ED Dekat";
        AlasanArray[9] = "Tidak Sesuai Pesanan";
        AlasanArray[10] = "Salah Ketik";
        AlasanArray[11] = "Salah Picking Gudang";
        AlasanArray[12] = "Tidak Sesuai FJP Delivery";
        AlasanArray[13] = "Toko Tidak Order";
        AlasanArray[14] = "Barcode Salah";


        Intent in = getIntent();
        faktur = in.getStringExtra(TAG_FAKTUR);


        FilterList = new ArrayList();

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject BarangJSON = null;

        if(dbFile.exists()){
            db.CekMasterTolakan();
            try {
                BarangJSON = db.GetTolakanVSMaster(in.getStringExtra(TAG_FAKTUR));
                // Getting Array of Barang
                BarangArray = BarangJSON.getJSONArray(TAG_BARANGDATA);

                SKU = new String[BarangArray.length()];
                Hint = new String[BarangArray.length()];
                Jml = new String[BarangArray.length()];
                HrgSatuan = new String[BarangArray.length()];
                Keterangan = new String[BarangArray.length()];
                Assigned = new String[BarangArray.length()];
                AssignedImg = new String[BarangArray.length()];
                DiscRp = new String[BarangArray.length()];
                JmlCRT = new String[BarangArray.length()];
                JmlPCS = new String[BarangArray.length()];
                RasioMax = new String[BarangArray.length()];
                JmlFakturCRT = new String[BarangArray.length()];

                // looping through All Barang
                for(int i = 0; i < BarangArray.length(); i++){
                    JSONObject c = BarangArray.getJSONObject(i);

                    String sku = c.getString(TAG_SKU);
                    String hint = c.getString(TAG_HINT);
                    String jml = c.getString(TAG_JML);
                    String hrgsatuan = c.getString(TAG_HRGSATUAN);
                    String keterangan = c.getString(TAG_KETERANGAN);
                    String discrp = c.getString(TAG_DISCRP);
                    String rasiomax = c.getString(TAG_RASIOMAX);
                    String jmlfakturcrt = c.getString(TAG_JMLFAKTURCRT);
                    String jmlPCS = c.getString("jmlpcs");

                    SKU[i] = sku;
                    Hint[i] = hint;
                    Jml[i] = jml;
                    HrgSatuan[i] = hrgsatuan;
                    Keterangan[i] = keterangan;
                    Assigned[i]="0";
                    AssignedImg[i]=Integer.toString(flags[2]);
                    DiscRp[i] = discrp;
                    JmlCRT[i] = "0";
                    JmlPCS[i] = jmlPCS;
                    RasioMax[i] = rasiomax;
                    JmlFakturCRT[i] = jmlfakturcrt;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        list = (ListView) findViewById(R.id.listview);

        for (int i = 0; i < BarangArray.length(); i++)
        {
            DataBarangTolakan brg = new DataBarangTolakan(SKU[i],Hint[i],Jml[i],HrgSatuan[i],Assigned[i], AssignedImg[i], Keterangan[i], DiscRp[i],JmlCRT[i],JmlPCS[i],RasioMax[i],JmlFakturCRT[i]);
            BarangList.add(brg);
        }

        // Pass results to ListViewAdapter Class
        adapter = new AdapterBarangTolakan(this, BarangList);

        TxtSKUTotal = (TextView) findViewById(R.id.Tolakan_TxtBarangListPrice);
        TxtSKUChecked = (TextView) findViewById(R.id.Tolakan_TxtBarangListChecked);

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        //
        // Set the new DecimalFormatSymbols into formatter object.
        //

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(adapter.RpBrgTolakan());

        TxtSKUTotal.setText(currency+" ");
        TxtSKUChecked.setText(adapter.BrgTolakan()+"");

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                InputTolakan(adapter.getItem(position).getSKU(),adapter.getItem(position).getKeterangan(),adapter.getItem(position).getJml(),position,adapter.getItem(position).getJmlCRT(), adapter.getItem(position).getJmlPCS(), adapter.getItem(position).getRasioMax(), adapter.getItem(position).getJmlFakturCRT());
                adapter.notifyDataSetChanged();
                editsearch.setHint(adapter.getItem(position).getHint());
            }
        });


        editsearch = (EditText) findViewById(R.id.search);

        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });

        BtnSubmit = (Button) findViewById(R.id.Tolakan_BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.BrgTolakan()>0){
                    InputAlasanTolakan();
                }else{
                    //Toast.makeText(getApplicationContext(),"Pilih dahulu barang yang akan di tolak",Toast.LENGTH_SHORT).show();
                    DialogKonfirmasiTolakanNol();
                }
            }
        });

        ImgBtnFilter = (ImageView) findViewById(R.id.Tolakan_ImgFilter);
        ImgBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });

        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void InputTolakan(String SKU, String Keterangan, final String JmlMaks,final int Position,String JmlCRT, String JmlPCS,final String RasioMaks,String JmlFakturCRT){
        final Dialog dialog = new Dialog(ActivityTolakan.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Input Tolakan");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_tolakan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.d_tolakan, null);

        TextView TxtSKU = (TextView) dialog.findViewById(R.id.DTolakan_TxtSKU);
        TxtSKU.setText(SKU);

        TextView TxtJmlCRT = (TextView) dialog.findViewById(R.id.DTolakan_TxtJmlFCRT);
        TxtJmlCRT.setText("("+JmlFakturCRT+")");

        TextView TxtKeterangan = (TextView) dialog.findViewById(R.id.DTolakan_TxtKeterangan);
        TxtKeterangan.setText(Keterangan);

        final EditText InputCRT = (EditText) dialog.findViewById(R.id.DTolakan_JmlCRT);
        final EditText InputPCS = (EditText) dialog.findViewById(R.id.DTolakan_JmlPCS);

        if(Integer.parseInt(JmlCRT)>0){
            InputCRT.setText(JmlCRT);
        }

        if(Integer.parseInt(JmlPCS)>0){
            InputPCS.setText(JmlPCS);
        }


        final Button BtnSubmit = (Button) dialog.findViewById(R.id.DTolakan_BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InputCRT.getText().toString().length()<=0){
                    InputCRT.setText("0");
                }
                if(InputPCS.getText().toString().length()<=0){
                    InputPCS.setText("0");
                }
                if((Integer.parseInt(InputPCS.getText().toString())+(Integer.parseInt(InputCRT.getText().toString())*Integer.parseInt(RasioMaks)))>Integer.parseInt(JmlMaks)){
                    Toast.makeText(getApplicationContext(),"Nilai yang di inputkan melebihi order toko!",Toast.LENGTH_SHORT).show();
                }else{
                    adapter.getItem(Position).setJmlCRT(Integer.toString(Integer.parseInt(InputCRT.getText().toString())));
                    adapter.getItem(Position).setJmlPCS(Integer.toString(Integer.parseInt(InputPCS.getText().toString())));

                    DecimalFormatSymbols symbol =
                            new DecimalFormatSymbols(Locale.GERMANY);
                    symbol.setCurrencySymbol("");

                    DecimalFormat formatter = (DecimalFormat)
                            NumberFormat.getCurrencyInstance(Locale.GERMANY);
                    formatter.setDecimalFormatSymbols(symbol);
                    String currency = formatter.format(adapter.RpBrgTolakan());

                    TxtSKUChecked.setText(adapter.BrgTolakan()+" ");
                    TxtSKUTotal.setText(currency+" ");

                    if((InputCRT.getText().toString().equals("0"))&&(InputPCS.getText().toString().equals("0"))){
                        adapter.getItem(Position).setAssignedImg(Integer.toString(flags[2]));
                    }else{
                        adapter.getItem(Position).setAssignedImg(Integer.toString(flags[0]));
                    }
                    dialog.dismiss();
                }
                adapter.notifyDataSetChanged();
            }
        });

        final  Button BtnBatal = (Button) dialog.findViewById(R.id.DTolakan_BtnBatal);
        BtnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dfa_info_down);
    }

    private void showFilter(){
        final Dialog dialog = new Dialog(ActivityTolakan.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Filter Tolakan");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_filtertolakan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        Spinner SpnArea = (Spinner) dialog.findViewById(R.id.Tolakan_SpnArea);

        FilterList.clear();
        FilterList.add("Semua");
        FilterList.add("Tolakan");

        ArrayAdapter dataAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, FilterList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnArea.setAdapter(dataAdapter);

        SpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(FilterList.get(position).equals("Semua")){
                    adapter.FilterTolakan(0);
                }else{
                    adapter.FilterTolakan(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dfa_filter);

        Button BtnTutup = (Button) dialog.findViewById(R.id.Tolakan_FilterBtnSubmit);
        BtnTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    private void InputAlasanTolakan(){
        final Dialog dialog = new Dialog(ActivityTolakan.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Input Alasan Tolakan");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_tolakanalasan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.d_tolakanalasan, null);

        Spinner SpnAlasan = (Spinner) dialog.findViewById(R.id.DTolakanAlasan_Alasan);


        ArrayAdapter adapterSPn = new ArrayAdapter(this,android.R.layout.simple_spinner_item, AlasanArray);
        adapterSPn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnAlasan.setAdapter(adapterSPn);

        SpnAlasan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{alasan = "A01"; break;}
                    case 1:{alasan = "A02";break;}
                    case 2:{alasan = "A04";break;}
                    case 3:{alasan = "A04";break;}
                    case 4:{alasan = "A05";break;}
                    case 5:{alasan = "A06";break;}
                    case 6:{alasan = "A07";break;}
                    case 7:{alasan = "A08";break;}
                    case 8:{alasan = "A09";break;}
                    case 9:{alasan = "A10";break;}
                    case 10:{alasan = "A11";break;}
                    case 11:{alasan = "A12";break;}
                    case 12:{alasan = "A13";break;}
                    case 13:{alasan = "A14";break;}
                    case 14:{alasan = "A15";break;}
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Button BtnSubmit = (Button) dialog.findViewById(R.id.DTolakan_AlasanBtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ResumeTolakan="";
                barangdatatolakan = adapter.FinalDataBarangTolakan();
                db.DeleteTolakan(faktur);
                for (DataBarangTolakan tlk : barangdatatolakan){
                    db.InsertTolakan(faktur,tlk.getSKU(),((Integer.parseInt(tlk.getJmlCRT())*Integer.parseInt(tlk.getRasioMax()))+Integer.parseInt(tlk.getJmlPCS())),alasan);
                    ResumeTolakan = ResumeTolakan + tlk.getSKU() +"&"+ ((Integer.parseInt(tlk.getJmlCRT())*Integer.parseInt(tlk.getRasioMax()))+Integer.parseInt(tlk.getJmlPCS())) +"&"+ alasan+"#";
                }
                dialog.dismiss();
                Intent in = new Intent();
                if(TxtSKUChecked.getText().toString().equals("0")){
                    TxtSKUChecked.setText("");
                }
                in.putExtra("SKUTolakan",TxtSKUChecked.getText().toString());
                in.putExtra("ResumeTolakan",ResumeTolakan);
                setResult(RESULT_OK,in);
                finish();
            }
        });

        final  Button BtnBatal = (Button) dialog.findViewById(R.id.DTolakan_AlasanBtnBatal);
        BtnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dfa_info_down);
    }

    private void DialogKonfirmasiTolakanNol(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Tolakan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        TextView Msg = new TextView(this);
        Msg.setText("Tidak ada barang yang di tolak,Apakah anda akan lanjut memproses?");

        layout.addView(Msg,params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                db.DeleteTolakan(faktur);
                String ResumeTolakan="";
                Intent in = new Intent();
                if(TxtSKUChecked.getText().toString().equals("0")){
                    TxtSKUChecked.setText("");
                }
                in.putExtra("SKUTolakan",TxtSKUChecked.getText().toString());
                in.putExtra("ResumeTolakan",ResumeTolakan);
                setResult(RESULT_OK,in);
                finish();
            }
        });

        builder.show();

    }
}