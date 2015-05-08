package com.bcp.DFA;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityPembayaran extends Activity {


    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LASTLOGIN = "lastlogin";

    private final String TAG_FAKTUR = "faktur";
    private final String TAG_KODENOTA = "kodenota";
    private final String TAG_NOPICKLIST = "nopicklist";
    private final String TAG_SHIPTO = "shipto";
    private final String TAG_TOTALBAYAR = "totalbayar";
    private final String TAG_PERUSAHAAN = "perusahaan";

    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";

    private final String TAG_ENTRYTIME = "entrytime";

    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";


    //String Menu;

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.dfa_tunai,
            R.drawable.dfa_cek,
            R.drawable.dfa_tt,
            R.drawable.dfa_stempel,
            R.drawable.dfa_transfer,
            R.drawable.dfa_tolakan
    };

    TextView TxtFaktur,TxtPerusahaan,TxtTunai,TxtGiro,TxtJumGiro,TxtJumTransfer,TxtNamaBank;
    Button   BtnSubmit;
    ImageButton ImgBtnDelTunai,ImgBtnDelGiro,ImgBtnDelTransfer;
    ImageView ImgInfo;

    String kodenota,nopicklist,faktur,shipto,perusahaan,totalbayar,tranferbank="";
    Float TotalGiro=0.0f,TotalTunai=0.0f,TotalTransfer=0.0f;
    int JumGiro,TT,Stempel;
    String KetGiro;

    int[] flagass = new int[]{
            R.drawable.arrow_left,R.drawable.dfa_check
    };

    ArrayList<DataMenuPembayaran> MenuPembayaran = new ArrayList<DataMenuPembayaran>();
    ListView list;
    AdapterMenuPembayaranListView adapter;

    //Location
    String longitude,latitude;
    String TolakanResume="",TotalSKU = "0",alasan="";

    String[] AlasanArray;
    byte TolakanPenuh;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_pembayaran);

        TolakanPenuh = 0;
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

        longitude = getPref(TAG_LONGITUDE);
        latitude = getPref(TAG_LATITUDE);

        Intent in = getIntent();
        kodenota = in.getStringExtra(TAG_KODENOTA);
        nopicklist = in.getStringExtra(TAG_NOPICKLIST);
        faktur = in.getStringExtra(TAG_FAKTUR);
        shipto = in.getStringExtra(TAG_SHIPTO);
        perusahaan = in.getStringExtra(TAG_PERUSAHAAN);
        totalbayar = in.getStringExtra(TAG_TOTALBAYAR);
        TotalTransfer = in.getFloatExtra("TransferTotal", 0.0f);

        if(in.getStringExtra("TransferBank")==null){
            tranferbank = "";
        }else{
            tranferbank = in.getStringExtra("TransferBank");
        }

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);

        // Resetting Var
        TotalGiro =  in.getFloatExtra("GiroRp",0.0f);
        TotalTunai = in.getFloatExtra("TunaiTotal",0.0f);
        KetGiro = in.getStringExtra("GiroResume");
        JumGiro = in.getIntExtra("GiroTotal",0);

        // Form Initial
        TxtFaktur = (TextView) findViewById(R.id.Pembayaran_TxtFaktur);
        TxtPerusahaan = (TextView) findViewById(R.id.Pembayaran_TxtPelanggan);
        ImgInfo = (ImageView) findViewById(R.id.Pembayaran_Info);

        TxtTunai = (TextView) findViewById(R.id.Pembayaran_TxtTunai);
        TxtGiro = (TextView) findViewById(R.id.Pembayaran_TxtGiro);
        TxtJumGiro = (TextView) findViewById(R.id.Pembayaran_TxtJumGiro);
        TxtJumTransfer = (TextView) findViewById(R.id.Pembayaran_TxtTransfer);
        TxtNamaBank = (TextView) findViewById(R.id.Pembayaran_TxtNamaBank);


        // Form Setting
        TxtFaktur.setText(faktur);
        TxtPerusahaan.setText(perusahaan);

        DecimalFormatSymbols sym =
                new DecimalFormatSymbols(Locale.GERMANY);
        sym.setCurrencySymbol("");

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(sym);
        String cG = formatter.format(TotalGiro);
        String cT = formatter.format(TotalTunai);
        String cTr = formatter.format(TotalTransfer);

        TxtTunai.setText(cT);
        TxtGiro.setText(cG);
        TxtJumGiro.setText("("+JumGiro+")");
        TxtJumTransfer.setText(cTr);
        TxtNamaBank.setText(tranferbank);

        TT = 0;
        Stempel = 0;


        try {
            TotalSKU = db.GetCountTolakan(faktur);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DataMenuPembayaran mp0 = new DataMenuPembayaran("0","Tunai",Integer.toString(flags[0]),Integer.toString(flagass[0]));
        DataMenuPembayaran mp1 = new DataMenuPembayaran("1","Cek/BG",Integer.toString(flags[1]),Integer.toString(flagass[0]));
        DataMenuPembayaran mp2 = new DataMenuPembayaran("2","Tanda terima",Integer.toString(flags[2]),Integer.toString(flagass[0]));
        DataMenuPembayaran mp3 = new DataMenuPembayaran("3","Stempel",Integer.toString(flags[3]),Integer.toString(flagass[0]));
        DataMenuPembayaran mp4 = new DataMenuPembayaran("4","Transfer",Integer.toString(flags[4]),Integer.toString(flagass[0]));

        list = (ListView) findViewById(R.id.Pembayaran_list);
        MenuPembayaran.add(mp0);
        MenuPembayaran.add(mp1);
        MenuPembayaran.add(mp2);
        MenuPembayaran.add(mp3);
        MenuPembayaran.add(mp4);

        if(TotalSKU.equals("0")){
            DataMenuPembayaran mp5 = new DataMenuPembayaran("5","Tolakan",Integer.toString(flags[5]),Integer.toString(flagass[0]));
            MenuPembayaran.add(mp5);
        }else{
            DataMenuPembayaran mp5 = new DataMenuPembayaran("5","Tolakan"+" ("+TotalSKU+" SKU)",Integer.toString(flags[5]),Integer.toString(flagass[0]));
            MenuPembayaran.add(mp5);
        }





        adapter = new AdapterMenuPembayaranListView(this,MenuPembayaran);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected ListItem
                String menuid = adapter.getItem(position).getID();

                if(menuid.equals("0")){
                    Stempel = 0;
                    TT = 0;
                    adapter.getItem(position+2).setAssignedImg(Integer.toString(flagass[0]));
                    adapter.getItem(position+3).setAssignedImg(Integer.toString(flagass[0]));
                    InputTunai();
                }

                if(menuid.equals("1")){
                    Stempel = 0;
                    TT = 0;
                    adapter.getItem(position+1).setAssignedImg(Integer.toString(flagass[0]));
                    adapter.getItem(position+2).setAssignedImg(Integer.toString(flagass[0]));
                    Toast.makeText(getBaseContext(),"Cek BG",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(),ActivityPembayaranGiro.class);
                    in.putExtra(TAG_FAKTUR, faktur);
                    in.putExtra(TAG_NOPICKLIST, nopicklist);
                    in.putExtra(TAG_KODENOTA, kodenota);
                    in.putExtra(TAG_SHIPTO, shipto);
                    in.putExtra(TAG_PERUSAHAAN, perusahaan);
                    in.putExtra(TAG_TOTALBAYAR, totalbayar);
                    startActivityForResult(in,1);
                }

                if(menuid.equals("2")){
                    if ((TotalTunai>1.f)){
                        Stempel = 0;
                        TT = 0;
                        Toast.makeText(getApplicationContext(),"Gagal - Anda sudah mengisi pembayaran invoice ini dengan Tunai",Toast.LENGTH_SHORT).show();
                    }else if ((TotalGiro>1.f)){
                        Stempel = 0;
                        TT = 0;
                        Toast.makeText(getApplicationContext(),"Gagal - Anda sudah mengisi pembayaran invoice ini dengan Giro",Toast.LENGTH_SHORT).show();
                    }else if ((TotalTransfer>1.f)){
                        Stempel = 0;
                        TT = 0;
                        Toast.makeText(getApplicationContext(),"Gagal - Anda sudah mengisi pembayaran invoice ini dengan Transfer",Toast.LENGTH_SHORT).show();
                    }else{
                        if(adapter.getItem(position).getAssignedImg().equals(Integer.toString(flagass[0]))){
                            Stempel = 0;
                            TT = 1;
                            adapter.getItem(position).setAssignedImg(Integer.toString(flagass[1]));
                            adapter.getItem(position+1).setAssignedImg(Integer.toString(flagass[0]));
                            adapter.getItem(position+2).setAssignedImg(Integer.toString(flagass[0]));
                        }else{
                            TT = 0;
                            adapter.getItem(position).setAssignedImg(Integer.toString(flagass[0]));
                        }
                    }
                }

                if(menuid.equals("3")){
                    if (TotalTunai>1f){
                        Toast.makeText(getApplicationContext(),"Gagal - Anda sudah mengisi pembayaran invoice ini dengan Tunai",Toast.LENGTH_SHORT).show();
                    }else if ((TotalGiro>1.f)){
                        Toast.makeText(getApplicationContext(),"Gagal - Anda sudah mengisi pembayaran invoice ini dengan Giro",Toast.LENGTH_SHORT).show();
                    }else if ((TotalTransfer>1.f)){
                        Stempel = 0;
                        TT = 0;
                        Toast.makeText(getApplicationContext(),"Gagal - Anda sudah mengisi pembayaran invoice ini dengan Transfer",Toast.LENGTH_SHORT).show();
                    }else{
                        if(adapter.getItem(position).getAssignedImg().equals(Integer.toString(flagass[0]))){
                            Stempel = 1;
                            TT = 0;
                            adapter.getItem(position).setAssignedImg(Integer.toString(flagass[1]));
                            adapter.getItem(position-1).setAssignedImg(Integer.toString(flagass[0]));
                            adapter.getItem(position+1).setAssignedImg(Integer.toString(flagass[0]));
                        }else{
                            Stempel = 0;
                            adapter.getItem(position).setAssignedImg(Integer.toString(flagass[0]));
                        }
                        //DialogExTunai(kodenota,faktur,nopicklist,"Stempel");
                    }
                }


                // Transfer
                if(menuid.equals("4")){
                    Stempel = 0;
                    TT = 0;
                    adapter.getItem(position-1).setAssignedImg(Integer.toString(flagass[0]));
                    adapter.getItem(position-2).setAssignedImg(Integer.toString(flagass[0]));
                    InputTransfer();
                }


                if(menuid.equals("5")){
                    DialogTolakan(kodenota,faktur,(TotalTunai+TotalGiro+TotalTransfer),(TT+Stempel),TotalSKU);
                }
                adapter.notifyDataSetChanged();
            }
        });

        if((in.getStringExtra("SKUTolakan")==null)||(in.getStringExtra("SKUTolakan").equals("0"))){
            adapter.getItem(5).setAssignedImg(Integer.toString(flagass[0]));
        }else{
            adapter.getItem(5).setAssignedImg(Integer.toString(flagass[1]));
        }

        BtnSubmit = (Button) findViewById(R.id.Pembayaran_BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((TotalTunai+TotalGiro+TotalTransfer)>1f){
                    DialogExTunai(kodenota,faktur,Float.toString(TotalTunai+TotalGiro+TotalTransfer),TotalSKU);
                }else if(TT == 1){
                    DialogExTunai(kodenota,faktur,"TT",TotalSKU);
                }else if(Stempel == 1){
                    DialogExTunai(kodenota,faktur,"Stempel",TotalSKU);
                }else if(TolakanPenuh == 1){
                    DialogExTunai(kodenota,faktur,"Tolakan",TotalSKU);
                }else if(TolakanResume.length()>2){
                    Toast.makeText(getApplicationContext(),"Tolakan Sebagian harus ada nilai tunai/cekbg/tt/stempel/transfer",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Silahkan pilih pembayaran dahulu",Toast.LENGTH_SHORT).show();
                }
            }
        });


        ImgBtnDelTunai = (ImageButton) findViewById(R.id.Pembayaran_imgBtnDelPembayaranTunai);
        ImgBtnDelTunai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Reset Tunai",Toast.LENGTH_SHORT).show();
                TxtTunai.setText("0");
                TotalTunai = 0f;
                adapter.getItem(0).setAssignedImg(Integer.toString(flagass[0]));
                adapter.notifyDataSetChanged();
            }
        });
        ImgBtnDelGiro = (ImageButton) findViewById(R.id.Pembayaran_imgBtnDelPembayaranGiro);
        ImgBtnDelGiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getItem(1).setAssignedImg(Integer.toString(flagass[0]));
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"Reset Giro",Toast.LENGTH_SHORT).show();
                TxtGiro.setText("0");
                TxtJumGiro.setText("(0)");
                TotalGiro = 0f;
                JumGiro = 0;
                KetGiro = "";
            }
        });
        ImgBtnDelTransfer = (ImageButton) findViewById(R.id.Pembayaran_imgBtnDelPembayaranTransfer);
        ImgBtnDelTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Reset Transfer",Toast.LENGTH_SHORT).show();
                TxtJumTransfer.setText("0");
                TxtNamaBank.setText("-");
                TotalTransfer = 0f;
                adapter.getItem(4).setAssignedImg(Integer.toString(flagass[0]));
                adapter.notifyDataSetChanged();
            }
        });

        ImgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHarga(faktur);
            }
        });

        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if(resultCode == RESULT_OK){
                TotalGiro = TotalGiro + data.getFloatExtra("GiroRp",0.0f);
                JumGiro = JumGiro + data.getIntExtra("GiroTotal",0);
                KetGiro = KetGiro + data.getStringExtra("GiroResume");

                DecimalFormatSymbols symbol =
                        new DecimalFormatSymbols(Locale.GERMANY);
                symbol.setCurrencySymbol("");

                DecimalFormat formatter = (DecimalFormat)
                        NumberFormat.getCurrencyInstance(Locale.GERMANY);
                formatter.setDecimalFormatSymbols(symbol);
                String currency = formatter.format(TotalGiro);

                TxtGiro.setText(currency);
                TxtJumGiro.setText("("+Integer.toString(JumGiro)+")");

                adapter.getItem(1).setAssignedImg(Integer.toString(flagass[1]));
            }
        }
        if(requestCode == 2){
            if(resultCode == RESULT_OK){
                if (data.getStringExtra("SKUTolakan").length()>0){
                    adapter.getItem(5).setNama("Tolakan ("+data.getStringExtra("SKUTolakan")+" SKU)");
                    adapter.getItem(5).setAssignedImg(Integer.toString(flagass[1]));
                    TolakanResume = data.getStringExtra("ResumeTolakan");
                    try {
                        TotalSKU = db.GetCountTolakan(faktur);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void InputTunai(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setSingleLine();
        input.setHint("Masukkan Nominal Uang");
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(8);
        input.setFilters(FilterArray);

        layout.addView(input,params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                adapter.getItem(0).setAssignedImg(Integer.toString(flagass[0]));
            }
        });
        builder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Uang Tidak Boleh Kosong",Toast.LENGTH_SHORT).show();
                }else{
                    if(Float.parseFloat(input.getText().toString())>=1f){
                        adapter.getItem(0).setAssignedImg(Integer.toString(flagass[1]));
                        TotalTunai = TotalTunai + Float.parseFloat(input.getText().toString());

                        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
                        symbols.setCurrencySymbol("");

                        DecimalFormat formatters = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
                        formatters.setDecimalFormatSymbols(symbols);
                        String currencys = formatters.format(TotalTunai);

                        TxtTunai.setText(currencys);
                        //DialogExTunai(kodenota,faktur,nopicklist,input.getText().toString());
                        adapter.notifyDataSetChanged();
                    } else{
                        Toast.makeText(getApplicationContext(),"Uang Harus Lebih Dari 0",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.show();
    }

    public void DialogExTunai(final String Kodenota, final String Faktur, final String Ket, final String TotalSKU){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtNotif = new TextView(this);
        TxtNotif.setText("Apakah anda yakin akan memproses ?");


        final TextView TxtKodeNota = new TextView(this);
        TxtKodeNota.setText("KKPDV : "+Kodenota);

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);

        final TextView TxtTotalSKU = new TextView(this);
        TxtTotalSKU.setText("Tolakan : "+TotalSKU+" SKU");

        final TextView TxtNamaPelangan = new TextView(this);
        TxtNamaPelangan.setText("Perusahaan : "+perusahaan);

        final TextView TxtPembayaran = new TextView(this);

        if ((Ket.equals("TT"))||(Ket.equals("Stempel"))){
            TxtPembayaran.setText("Pembayaran : "+Ket);
        }else if(Ket.equals("Tolakan")){
            TxtPembayaran.setText("Pembayaran : Tolakan Penuh");
        }else{
            DecimalFormatSymbols symbol =
                    new DecimalFormatSymbols(Locale.GERMANY);
            symbol.setCurrencySymbol("");

            DecimalFormat formatter = (DecimalFormat)
                    NumberFormat.getCurrencyInstance(Locale.GERMANY);
            formatter.setDecimalFormatSymbols(symbol);
            String currency = formatter.format(Float.parseFloat(Ket));

            TxtPembayaran.setText("Pembayaran : Rp. "+currency);
        }

        layout.addView(TxtNotif,params);
        layout.addView(TxtKodeNota,params);
        layout.addView(TxtNamaPelangan,params);
        layout.addView(TxtFaktur,params);
        layout.addView(TxtPembayaran,params);
        layout.addView(TxtTotalSKU,params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.DeleteDeliveryOrder(kodenota,nopicklist,faktur,getPref(TAG_LASTLOGIN));
                if(Ket.equals("TT")){
                    db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,0.0f,0.0f,0,1,"",shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,"",TotalTransfer,getPref(TAG_ENTRYTIME),getToday(),TolakanResume);
                }else if(Ket.equals("Stempel")){
                    db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,0.0f,0.0f,1,0,"",shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,"",TotalTransfer,getPref(TAG_ENTRYTIME),getToday(),TolakanResume);
                }else{
                    if ((TotalTunai>1f)&&(TotalGiro<1f)&&(TotalTransfer<1f)){
                        db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,TotalTunai,0.0f,0,0,"",shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,"",0.0f,getPref(TAG_ENTRYTIME),getToday(),TolakanResume);
                        Toast.makeText(getApplicationContext(),"Submit Pembayaran Tunai - Rp "+TotalTunai,Toast.LENGTH_SHORT).show();
                    }else if((TotalTunai<1f)&&(TotalGiro>1f)&&(TotalTransfer<1f)){
                        db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,0.0f,TotalGiro,0,0,KetGiro,shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,"",0.0f,getPref(TAG_ENTRYTIME),getToday(),TolakanResume);
                        Toast.makeText(getApplicationContext(),"Submit Pembayaran Giro - Rp. "+ TotalGiro,Toast.LENGTH_SHORT).show();
                    }else if((TotalTunai<1f)&&(TotalGiro<1f)&&(TotalTransfer>1f)){
                        db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,0.0f,0.0f,0,0,"",shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,tranferbank,TotalTransfer,getPref(TAG_ENTRYTIME),getToday(),TolakanResume);
                        Toast.makeText(getApplicationContext(),"Submit Pembayaran Transfer - Rp. "+ TotalTransfer,Toast.LENGTH_SHORT).show();
                    }else if((TotalTunai>1f)&&(TotalGiro>1f)&&(TotalTransfer>1f)){
                        db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,TotalTunai,TotalGiro,0,0,KetGiro,shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,tranferbank,TotalTransfer,getPref(TAG_ENTRYTIME),getToday(),TolakanResume);
                        Toast.makeText(getApplicationContext(),"Submit Pembayaran Giro,Tunai dan Transfer - Rp. "+(TotalGiro+TotalTunai+TotalTransfer),Toast.LENGTH_SHORT).show();
                    }else if((TotalTunai>1f)||(TotalGiro>1f)||(TotalTransfer>1f)){
                        db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,TotalTunai,TotalGiro,0,0,KetGiro,shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,tranferbank,TotalTransfer,getPref(TAG_ENTRYTIME),getToday(),TolakanResume);
                        Toast.makeText(getApplicationContext(),"Submit Pembayaran Giro/Tunai/Transfer - Rp. "+(TotalGiro+TotalTunai+TotalTransfer),Toast.LENGTH_SHORT).show();
                    }else if(TolakanPenuh == 1){
                        try {
                            TolakanResume = db.GetResumeTolakan(faktur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,0.0f,0.0f,0,0,"",shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,"",0.0f,getPref(TAG_ENTRYTIME),getToday(),TolakanResume);
                        Toast.makeText(getApplicationContext(),"Tolakan Penuh",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Silahkan pilih pembayaran dahulu",Toast.LENGTH_SHORT).show();
                    }
                }
                Intent in = new Intent(getApplicationContext(),ActivityMainMenu.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void InputTransfer(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setSingleLine();
        input.setHint("Masukkan Nominal Uang");
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(8);
        input.setFilters(FilterArray);


        final TextView txtBank = new TextView(this);
        txtBank.setText("Nama Bank :");

        final TextView txtNominal = new TextView(this);
        txtNominal.setText("Nominal :");

        final EditText inputBank = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputBank.setSingleLine();
        inputBank.setHint("Masukkan Nama Bank");
        InputFilter[] FilterArrayText = new InputFilter[1];
        FilterArrayText[0] = new InputFilter.LengthFilter(30);
        inputBank.setFilters(FilterArrayText);

        layout.addView(txtBank,params);
        layout.addView(inputBank,params);
        layout.addView(txtNominal,params);
        layout.addView(input, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                adapter.getItem(4).setAssignedImg(Integer.toString(flagass[0]));
            }
        });
        builder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(inputBank.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Nama Bank Tidak Boleh Kosong",Toast.LENGTH_SHORT).show();
                }else if(input.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Uang Tidak Boleh Kosong",Toast.LENGTH_SHORT).show();
                }else{
                    if(Float.parseFloat(input.getText().toString())>=1f){
                        adapter.getItem(4).setAssignedImg(Integer.toString(flagass[1]));
                        TotalTransfer = Float.parseFloat(input.getText().toString());

                        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
                        symbols.setCurrencySymbol("");

                        DecimalFormat formatters = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
                        formatters.setDecimalFormatSymbols(symbols);
                        String currencys = formatters.format(TotalTransfer);

                        TxtJumTransfer.setText(currencys);
                        TxtNamaBank.setText(inputBank.getText().toString());
                        //DialogExTunai(kodenota,faktur,nopicklist,input.getText().toString());
                        adapter.notifyDataSetChanged();
                        tranferbank = TxtNamaBank.getText().toString();
                    } else{
                        Toast.makeText(getApplicationContext(),"Uang Harus Lebih Dari 0",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.show();
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public void DialogTolakan(final String Kodenota, final String Faktur, final Float TotalBGTT, final int TTST, final String TotalSKU){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Tolakan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtNotif = new TextView(this);
        TxtNotif.setText("PILIH JENIS TOLAKAN?");
        TxtNotif.setTypeface(null, Typeface.BOLD);


        final TextView TxtKodeNota = new TextView(this);
        TxtKodeNota.setText("KKPDV : "+Kodenota);

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);


        final TextView TxtNamaPelangan = new TextView(this);
        TxtNamaPelangan.setText("Perusahaan : "+perusahaan);

        final TextView TxtTotalSKU = new TextView(this);
        TxtTotalSKU.setText("Total SKU : "+TotalSKU);


        layout.addView(TxtNotif,params);
        layout.addView(TxtKodeNota, params);
        layout.addView(TxtNamaPelangan,params);
        layout.addView(TxtFaktur,params);
        if(Integer.parseInt(TotalSKU)>0){
             layout.addView(TxtTotalSKU,params);
        }
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Tolakan Penuh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if((TotalBGTT+TTST)>0.0f){
                    Toast.makeText(getApplicationContext(),"Gagal - Untuk Tolakan Penuh tidak boleh ada nilai di tunai/transfer/cekbg/stempel/TT",Toast.LENGTH_LONG).show();
                }else{
                    dialog.cancel();
                    InputAlasanTolakan();
                }
            }
        });
        builder.setNegativeButton("Tolakan Sebagian", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent in = new Intent(getApplicationContext(),ActivityTolakan.class);
                in.putExtra(TAG_FAKTUR, faktur);
                in.putExtra(TAG_NOPICKLIST, nopicklist);
                in.putExtra(TAG_KODENOTA, kodenota);
                in.putExtra(TAG_SHIPTO, shipto);
                in.putExtra(TAG_PERUSAHAAN, perusahaan);
                in.putExtra(TAG_TOTALBAYAR, totalbayar);
                startActivityForResult(in,2);
            }
        });

        builder.show();
    }

    private void InputAlasanTolakan(){
        final Dialog dialog = new Dialog(ActivityPembayaran.this, android.R.style.Theme_Dialog);
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
                db.DeleteTolakan(faktur);
                db.InsertTolakanFull(faktur, alasan);
                try {
                    TotalSKU = db.GetCountTolakan(faktur);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.getItem(5).setNama("Tolakan Penuh(" + TotalSKU + " SKU)");
                adapter.getItem(5).setAssignedImg(Integer.toString(flagass[1]));
                Toast.makeText(getApplicationContext(), "Tolakan Penuh", Toast.LENGTH_SHORT).show();
                TolakanPenuh = 1;
                adapter.notifyDataSetChanged();
                dialog.dismiss();
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

    public void ShowHarga(String Faktur){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Informasi Harga");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtFaktur = new TextView(this);
        TxtFaktur.setText("Faktur : "+Faktur);

        float Tolakan = 0.0f;
        float TotalBayar = 0.0f;
        try {
            TotalBayar = db.GetTotalBayar(Faktur,0);
            Tolakan = db.GetTotalBayar(Faktur,1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);

        final TextView TxtTolakan = new TextView(this);
        TxtTolakan.setText("Tolakan : Rp. "+formatter.format(Tolakan));

        final TextView TxtBayar = new TextView(this);
        TxtBayar.setText("Total Bayar : Rp "+formatter.format(TotalBayar));

        final TextView TxtEstimasi = new TextView(this);
        TxtEstimasi.setText("Estimasi Total : Rp "+formatter.format(TotalBayar-Tolakan));

        layout.addView(TxtFaktur,params);
        layout.addView(TxtBayar,params);
        layout.addView(TxtTolakan,params);
        layout.addView(TxtEstimasi,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            db.DeleteTolakanEdit(faktur);
                            Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
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

}