package com.bcp.DFA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
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

public class ActivityCheckerTolakan extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";

    private final String TAG_FAKTUR = "faktur";

    private final String TAG_BRG = "brg";
    private final String TAG_KETERANGAN = "keterangan";
    private final String TAG_RASIOMAX = "rasiomax";
    private final String TAG_JML = "jml";
    private final String TAG_JMLPCS = "jmlpcs";
    private final String TAG_CREATEBY = "createby";
    private final String TAG_REASONCODE = "reasoncode";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LASTLOGIN = "lastlogin";

    // Declare Variables
    ListView list;
    AdapterBarangTolakan adapter;
    EditText editsearch;
    TextView TxtSKUTotal,TxtSKUChecked;
    Button   BtnSubmit;
    ImageView ImgBtnFilter;

    String[] Brg,Jml,Assigned,AssignedImg,Keterangan,JmlCRT,JmlPCS,RasioMax,Dummy,CreateBy,ReasonCode;
    String[] BrgAdd,JmlAdd,AssignedAdd,AssignedImgAdd,KeteranganAdd,JmlCRTAdd,JmlPCSAdd,RasioMaxAdd,DummyAdd;
    ArrayList<DataBarangTolakan> BarangList = new ArrayList<DataBarangTolakan>();

    JSONArray BarangArray,BrgAddArray;
    private final String TAG_BARANGDATA= "BarangData";

    int[] flags = new int[]{
            R.drawable.dfa_check,R.drawable.dfa_error,R.drawable.dfa_none
    };

    private String faktur;

    List FilterList;
    String[] AlasanArray;
    String alasan="";

    private List<DataBarangTolakan> barangChecker = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_tolakan);

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
                BarangJSON = db.GetTolakanCheckerVSMaster(in.getStringExtra(TAG_FAKTUR));
                // Getting Array of Barang
                BarangArray = BarangJSON.getJSONArray(TAG_BARANGDATA);

                Jml = new String[BarangArray.length()];
                Keterangan = new String[BarangArray.length()];
                Assigned = new String[BarangArray.length()];
                AssignedImg = new String[BarangArray.length()];
                JmlCRT = new String[BarangArray.length()];
                JmlPCS = new String[BarangArray.length()];
                RasioMax = new String[BarangArray.length()];
                Dummy = new String[BarangArray.length()];
                Brg = new String[BarangArray.length()];
                CreateBy = new String[BarangArray.length()];
                ReasonCode = new String[BarangArray.length()];

                // looping through All Barang
                for(int i = 0; i < BarangArray.length(); i++){
                    JSONObject c = BarangArray.getJSONObject(i);

                    String brg = c.getString(TAG_BRG);
                    String jml = c.getString(TAG_JML);
                    String jmlpcs = c.getString(TAG_JMLPCS);
                    String keterangan = c.getString(TAG_KETERANGAN);
                    String rasiomax = c.getString(TAG_RASIOMAX);
                    String createby = c.getString(TAG_CREATEBY);
                    String reasoncode = c.getString(TAG_REASONCODE);

                    Jml[i] = jml;
                    Keterangan[i] = keterangan;
                    Assigned[i]="0";
                    AssignedImg[i]=Integer.toString(flags[2]);
                    JmlCRT[i] = "0";
                    JmlPCS[i] = jmlpcs;
                    RasioMax[i] = rasiomax;
                    Dummy[i] = "0";
                    Brg[i] = brg;
                    CreateBy[i] = createby;
                    ReasonCode[i] = reasoncode;
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
            DataBarangTolakan brg = new DataBarangTolakan(Brg[i],ReasonCode[i]+CreateBy[i],Jml[i],Dummy[i],Assigned[i], AssignedImg[i], Keterangan[i], Dummy[i],JmlCRT[i],JmlPCS[i],RasioMax[i],Jml[i]);
            BarangList.add(brg);
        }

        // Pass results to ListViewAdapter Class
        adapter = new AdapterBarangTolakan(this, BarangList);

        TxtSKUTotal = (TextView) findViewById(R.id.Tolakan_TxtBarangListPrice);
        TxtSKUTotal.setText(" - ");
        TxtSKUChecked = (TextView) findViewById(R.id.Tolakan_TxtBarangListChecked);
        TxtSKUChecked.setText(adapter.BrgTolakan2()+" ");

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                InputTolakan(adapter.getItem(position).getSKU(),adapter.getItem(position).getKeterangan(),adapter.getItem(position).getJml(),position,adapter.getItem(position).getJmlCRT(), adapter.getItem(position).getJmlPCS(), adapter.getItem(position).getRasioMax(), adapter.getItem(position).getJmlFakturCRT());
                adapter.notifyDataSetChanged();
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
                if(adapter.BrgTolakan2()>0){
                    KonfirmasiChecker(faktur);
                }else{
                    Toast.makeText(getApplicationContext(),"Pilih dahulu barang yang akan di tolak",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImgBtnFilter = (ImageView) findViewById(R.id.Tolakan_ImgFilter);
        ImgBtnFilter.setImageResource(R.drawable.imgbtn_plus);
        ImgBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTambahTolakan(faktur);
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
        final Dialog dialog = new Dialog(ActivityCheckerTolakan.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Input Tolakan");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_tolakan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.d_tolakan, null);

        TextView TxtSKU = (TextView) dialog.findViewById(R.id.DTolakan_TxtSKU);
        TxtSKU.setText(SKU);

        TextView TxtJmlCRT = (TextView) dialog.findViewById(R.id.DTolakan_TxtJmlFCRT);
        TxtJmlCRT.setText("("+JmlFakturCRT.replace("(","").replace(")","")+")");

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
                if((Integer.parseInt(InputPCS.getText().toString())+(Integer.parseInt(InputCRT.getText().toString().replace("(","").replace(")",""))*Integer.parseInt(RasioMaks)))>Integer.parseInt(JmlMaks.replace("(","").replace(")",""))){
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

                    TxtSKUChecked.setText(adapter.BrgTolakan2()+" ");
/*                    TxtSKUTotal.setText(currency+" ");

                    if((InputCRT.getText().toString().equals("0"))&&(InputPCS.getText().toString().equals("0"))){
                        adapter.getItem(Position).setAssignedImg(Integer.toString(flags[2]));
                    }else{
                        adapter.getItem(Position).setAssignedImg(Integer.toString(flags[0]));
                    }*/
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

    private void showTambahTolakan(String Faktur){
        JSONObject BarangJSONAdd = null;
        try {
            BarangJSONAdd = db.GetTolakanTambahBarang(Faktur);
            // Getting Array of Barang
            BrgAddArray = BarangJSONAdd.getJSONArray(TAG_BARANGDATA);

            JmlAdd = new String[BrgAddArray.length()];
            KeteranganAdd = new String[BrgAddArray.length()];
            AssignedAdd = new String[BrgAddArray.length()];
            AssignedImgAdd = new String[BrgAddArray.length()];
            JmlCRTAdd = new String[BrgAddArray.length()];
            JmlPCSAdd = new String[BrgAddArray.length()];
            RasioMaxAdd = new String[BrgAddArray.length()];
            DummyAdd = new String[BrgAddArray.length()];
            BrgAdd = new String[BrgAddArray.length()];

            // looping through All Barang
            for(int i = 0; i < BrgAddArray.length(); i++){
                JSONObject c = BrgAddArray.getJSONObject(i);

                String brgAdd = c.getString(TAG_BRG);
                String jmlAdd = c.getString(TAG_JML);
                String jmlpcsAdd = c.getString(TAG_JMLPCS);
                String keteranganAdd = c.getString(TAG_KETERANGAN);
                String rasiomaxAdd = c.getString(TAG_RASIOMAX);

                JmlAdd[i] = jmlAdd;
                KeteranganAdd[i] = keteranganAdd;
                AssignedAdd[i]="0";
                AssignedImgAdd[i]=Integer.toString(flags[2]);
                JmlCRTAdd[i] = "0";
                JmlPCSAdd[i] = jmlpcsAdd;
                RasioMaxAdd[i] = rasiomaxAdd;
                DummyAdd[i] = "0";
                BrgAdd[i] = brgAdd;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(BrgAddArray != null){
            final Dialog dialog = new Dialog(ActivityCheckerTolakan.this, android.R.style.Theme_Dialog);
            dialog.setTitle("Tambah Barang Tolakan");
            dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
            dialog.setContentView(R.layout.d_tambahtolakan);
            Context dContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            final Spinner SpnArea = (Spinner) dialog.findViewById(R.id.TambahTolakan_SpnSKU);
            final Spinner SpnAlasan = (Spinner) dialog.findViewById(R.id.TambahTolakan_SpnAlasan);

            final TextView TxtKodeSKU = (TextView) dialog.findViewById(R.id.TambahTolakan_SKU);
            final TextView TxtJmlMaks = (TextView) dialog.findViewById(R.id.TambahTolakan_JmlMaksSKU);
            final TextView TxtRasioMaks = (TextView) dialog.findViewById(R.id.TambahTolakan_RasioMaks);
            final EditText InputJml = (EditText) dialog.findViewById(R.id.TambahTolakan_Jml);


            ArrayAdapter dataAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, KeteranganAdd);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpnArea.setAdapter(dataAdapter);

            ArrayAdapter dataAAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, AlasanArray);
            dataAAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpnAlasan.setAdapter(dataAAdapter);

            SpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TxtKodeSKU.setText(BrgAdd[position]);
                    TxtJmlMaks.setText("("+JmlAdd[position]+")");
                    TxtRasioMaks.setText(RasioMaxAdd[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

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
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            dialog.show();
            dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dfa_filter);

            Button BtnTutup = (Button) dialog.findViewById(R.id.TambahTolakan_BtnBatal);
            BtnTutup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button BtnSubmit = (Button) dialog.findViewById(R.id.TambahTolakan_BtnSubmit);
            BtnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(InputJml.getText().toString().length()<=0){
                        InputJml.setText("0");
                    }
                    if(Integer.parseInt(InputJml.getText().toString())<=0){
                        Toast.makeText(getApplicationContext(),"Jml SKU harus lebih dari 0",Toast.LENGTH_SHORT).show();
                    }else{
                        if(Integer.parseInt(InputJml.getText().toString())>(Integer.parseInt(TxtJmlMaks.getText().toString().replace("(","").replace(")","")))){
                            Toast.makeText(getApplicationContext(),"Jumlah yang di input melebihi jumlah order toko",Toast.LENGTH_SHORT).show();
                        }else{
                            if((adapter.AddDataBarang(TxtKodeSKU.getText().toString(),alasan+getPref(TAG_LASTLOGIN),TxtJmlMaks.getText().toString(),"0","0","0",SpnArea.getSelectedItem().toString(),"0","0",InputJml.getText().toString(),TxtRasioMaks.getText().toString(),TxtJmlMaks.getText().toString()))==1){
                                Toast.makeText(getApplicationContext(),TxtKodeSKU.getText().toString()+" berhasil ditambahkan",Toast.LENGTH_SHORT).show();
                                TxtSKUChecked.setText(adapter.BrgTolakan2()+" ");
                                dialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(),"SKU "+TxtKodeSKU.getText().toString()+" sudah ada",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Tidak ada barang lain untuk ditambahkan",Toast.LENGTH_SHORT).show();
        }


    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(getApplicationContext(), ActivityMainMenuChecker.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi");
            builder.setIcon(R.drawable.dfa_info_ups);
            builder.setMessage("Apakah anda akan membatalkan transaksi?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }

    public void KonfirmasiChecker(final String Faktur){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtMessage = new TextView(this);
        TxtMessage.setText("Apakah anda akan memproses tolakan faktur : "+Faktur+"?");

        final TextView TxtStatus = new TextView(this);
        TxtStatus.setText("Status : ");

        final String[] Status =new String[2];
        Status[0] = "Tidak Valid";
        Status[1] = "Valid";
        final Spinner SpnStatus = new Spinner(this);
        final String[] STS =new String[1];
        STS[0] = "1";

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        db.DeleteCheckerApproved(Faktur);
                        barangChecker = adapter.FinalDataBarangChecker();
                        for(int i=0;i<barangChecker.size();i++){
                            db.InsertCheckerApproved(faktur,barangChecker.get(i).getSKU(),Integer.parseInt(barangChecker.get(i).getJmlPCS()),barangChecker.get(i).getHint().substring(3,barangChecker.get(i).getHint().length()),barangChecker.get(i).getHint().substring(0,3),getPref(TAG_LASTLOGIN),STS[0],"1");
                        }
                        Toast.makeText(getApplicationContext(),"Tolakan Faktur : "+Faktur+" berhasil diproses",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ActivityMainMenuChecker.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };



        ArrayAdapter dataAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, Status);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnStatus.setAdapter(dataAdapter);
        SpnStatus.setSelection(1);

        SpnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   STS[0]  =  Integer.toString(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });


        layout.addView(TxtMessage,params);
        layout.addView(TxtStatus,params);
        layout.addView(SpnStatus,params);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setIcon(R.drawable.dfa_info_ups);
        builder.setPositiveButton("Ya", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener);

        builder.setView(layout);
        builder.show();


    }


}