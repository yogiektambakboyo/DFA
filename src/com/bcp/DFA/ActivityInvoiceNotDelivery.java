package com.bcp.DFA;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityInvoiceNotDelivery extends Activity {
    TextView TxtFaktur,TxtKKPDV,TxtPickList,TxtPerusahaan,TxtTotalBayar,TxtTotalSKU;
    Spinner  SpnAlasanRetur;
    Button   BtnSubmit;

    private final String TAG_SHIPTO = "shipto";
    private final String TAG_FAKTUR = "faktur";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_TOTALBAYAR = "totalbayar";
    private final String TAG_KODENOTA = "kodenota";
    private final String TAG_NOPICKLIST = "nopicklist";
    private final String TAG_KKPDVDATA = "KKPDVData";

    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_PREF="SETTINGPREF";

    private final String TAG_ENTRYTIME = "entrytime";

    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";

    String  faktur,nopicklist,kodenota,shipto,perusahaan,totalbayar,alasan="";
    int totalsku = 0;
    String[] AlasanArray;

    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";

    //Location
    String longitude,latitude;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_invoicenotdelivery);

        longitude = getPref(TAG_LONGITUDE);
        latitude = getPref(TAG_LATITUDE);

        Intent in = getIntent();
        faktur = in.getStringExtra(TAG_FAKTUR);
        nopicklist = in.getStringExtra(TAG_NOPICKLIST);
        kodenota = in.getStringExtra(TAG_KODENOTA);
        shipto = in.getStringExtra(TAG_SHIPTO);
        perusahaan = in.getStringExtra(TAG_PERUSAHAAN);
        totalbayar = in.getStringExtra(TAG_TOTALBAYAR);

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        try {
            totalsku = db.GetCountBarang(faktur);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TxtKKPDV = (TextView) findViewById(R.id.FakturTakTerkirim_TxtKKPDV);
        TxtKKPDV.setText(kodenota);
        TxtFaktur = (TextView) findViewById(R.id.FakturTakTerkirim_TxtFaktur);
        TxtFaktur.setText(faktur);
        TxtPickList = (TextView) findViewById(R.id.FakturTakTerkirim_TxtNoPickList);
        TxtPickList.setText(nopicklist);
        TxtPerusahaan = (TextView) findViewById(R.id.FakturTakTerkirim_TxtPelanggan);
        TxtPerusahaan.setText(perusahaan);
        TxtTotalBayar = (TextView) findViewById(R.id.FakturTakTerkirim_TxtTotalBayar);

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);

        TxtTotalBayar.setText("Rp. "+formatter.format(Float.parseFloat(totalbayar)));
        TxtTotalSKU = (TextView) findViewById(R.id.FakturTakTerkirim_TxtSKU);
        TxtTotalSKU.setText(""+totalsku);

        /*
        * Kode	keterangan
        * C01	Toko Tutup
        * C02	Truck Tidak Cukup
        * C03	Waktu Kirim Tidak Cukup
        * C04	Permintaan Toko / SR
        * C05	Menunggu Truck Yg Ke Depo Penuh
        * C06	Ekspedisi Pihak Ke-3 Terlambat
        * C07	Warehouse Salah Ambil Barang
        * C08	Tidak Sesuai FJP Delivery
        * C09	Toko Minta Pending (Stok Opname/Gudang Penuh/...)
        * C10	Delivery brkt kesiangan ( >08:30 )
        * */

        AlasanArray = new String[10];
        AlasanArray[0] = "Toko Tutup";
        AlasanArray[1] = "Truck Tidak Cukup";
        AlasanArray[2] = "Waktu Kirim Tidak Cukup";
        AlasanArray[3] = "Permintaan Toko / SR";
        AlasanArray[4] = "Menunggu Truck Yg Ke Depo Penuh";
        AlasanArray[5] = "Ekspedisi Pihak Ke-3 Terlambat";
        AlasanArray[6] = "Warehouse Salah Ambil Barang";
        AlasanArray[7] = "Tidak Sesuai FJP Delivery";
        AlasanArray[8] = "Toko Minta Pending (Stok Opname/Gudang Penuh)";
        AlasanArray[9] = "Delivery brkt kesiangan ( >08:30 )";

        SpnAlasanRetur = (Spinner) findViewById(R.id.FakturTakTerkirim_SpnAlasanRetur);
        ArrayAdapter adapterSPn = new ArrayAdapter(this,android.R.layout.simple_spinner_item, AlasanArray);
        adapterSPn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnAlasanRetur.setAdapter(adapterSPn);

        SpnAlasanRetur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{alasan = "C01"; break;}
                    case 1:{alasan = "C02";break;}
                    case 2:{alasan = "C03";break;}
                    case 3:{alasan = "C04";break;}
                    case 4:{alasan = "C05";break;}
                    case 5:{alasan = "C06";break;}
                    case 6:{alasan = "C07";break;}
                    case 7:{alasan = "C08";break;}
                    case 8:{alasan = "C09";break;}
                    case 9:{alasan = "C10";break;}
                    default:break;
                }
                //Toast.makeText(getApplicationContext(),"Position = "+alasan,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        BtnSubmit = (Button) findViewById(R.id.FakturTakTerkirim_BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogExTunai(kodenota,faktur,"Tidak Terkirim("+alasan+")");
            }
        });
        db.close();
    }

    public void DialogExTunai(final String Kodenota, final String Faktur, final String Ket){
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


        final TextView TxtNamaPelangan = new TextView(this);
        TxtNamaPelangan.setText("Perusahaan : "+perusahaan);

        final TextView TxtPembayaran = new TextView(this);
        TxtPembayaran.setText("Pembayaran : "+Ket);

        layout.addView(TxtNotif,params);
        layout.addView(TxtKodeNota,params);
        layout.addView(TxtNamaPelangan,params);
        layout.addView(TxtFaktur,params);
        layout.addView(TxtPembayaran,params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getBaseContext(),Ket,Toast.LENGTH_SHORT).show();
                db.DeleteDeliveryOrder(kodenota,nopicklist,faktur,getPref(TAG_LASTLOGIN));
                db.InsertDeliveryOrder(kodenota,nopicklist,faktur,getToday(),getPref(TAG_LASTLOGIN),1,0.0f,0.0f,0,0,alasan,shipto,perusahaan,"",Float.parseFloat(totalbayar),longitude,latitude,"",0.0f,getPref(TAG_ENTRYTIME),getToday(),"");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
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