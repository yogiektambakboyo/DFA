package com.bcp.DFA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
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

public class ActivityBarang extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";

    private final String TAG_FAKTUR = "faktur";
    private final String TAG_KODENOTA = "kodenota";
    private final String TAG_NOPICKLIST = "nopicklist";
    private final String TAG_SHIPTO = "shipto";
    private final String TAG_TOTALBAYAR = "totalbayar";
    private final String TAG_PERUSAHAAN = "perusahaan";

    private final String TAG_SKU = "brg";
    private final String TAG_KETERANGAN = "keterangan";
    private final String TAG_HINT = "hint";
    private final String TAG_JML = "jml";
    private final String TAG_HRGSATUAN = "hrgsatuan";
    private final String TAG_DISCRP = "discrp";


    // Declare Variables
    ListView list;
    AdapterBarangListView adapter;
    EditText editsearch;
    TextView TxtSKUTotal,TxtSKUChecked;
    Button   BtnSubmit;

    String[] SKU,Hint,Jml,HrgSatuan,Assigned,AssignedImg,Keterangan,DiscRp;
    ArrayList<DataBarang> BarangList = new ArrayList<DataBarang>();

    JSONArray BarangArray;
    private final String TAG_BARANGDATA= "BarangData";

    private Float TotalRp = 0f;

    int[] flags = new int[]{
            R.drawable.dfa_check,R.drawable.dfa_error
    };

    private String kodenota,nopicklist,faktur,shipto,perusahaan,totalbayar;

    Float   TunaiTotal=0.0f,GiroRp=0.0f,TotalTransfer=0.0f;
    int     GiroTotal = 0;

    String  GiroResume="",BankTransfer="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_barang);

        Intent in = getIntent();
        kodenota = in.getStringExtra(TAG_KODENOTA);
        nopicklist = in.getStringExtra(TAG_NOPICKLIST);
        faktur = in.getStringExtra(TAG_FAKTUR);
        shipto = in.getStringExtra(TAG_SHIPTO);
        perusahaan = in.getStringExtra(TAG_PERUSAHAAN);
        totalbayar = in.getStringExtra(TAG_TOTALBAYAR);
        TunaiTotal = in.getFloatExtra("TunaiTotal",0.0f);
        GiroRp = in.getFloatExtra("GiroRp",0.0f);
        GiroTotal = in.getIntExtra("GiroTotal",0);
        GiroResume =  in.getStringExtra("GiroResume");
        TotalTransfer = in.getFloatExtra("TransferTotal",0.0f);
        BankTransfer = in.getStringExtra("TransferBank");

        TotalRp = 0f;

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject BarangJSON = null;

        if(dbFile.exists()){
            try {
                BarangJSON = db.GetBarang(in.getStringExtra(TAG_FAKTUR));
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

                // looping through All Barang
                for(int i = 0; i < BarangArray.length(); i++){
                    JSONObject c = BarangArray.getJSONObject(i);

                    String sku = c.getString(TAG_SKU);
                    String hint = c.getString(TAG_HINT);
                    String jml = c.getString(TAG_JML);
                    String hrgsatuan = c.getString(TAG_HRGSATUAN);
                    String keterangan = c.getString(TAG_KETERANGAN);
                    String discrp = c.getString(TAG_DISCRP);

                    SKU[i] = sku;
                    Hint[i] = hint;
                    Jml[i] = jml;
                    HrgSatuan[i] = hrgsatuan;
                    Keterangan[i] = keterangan;
                    Assigned[i]="0";
                    AssignedImg[i]=Integer.toString(flags[0]);
                    DiscRp[i] = discrp;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            db.close();
        }else{
            Toast.makeText(getApplicationContext(),"DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        list = (ListView) findViewById(R.id.listview);

        for (int i = 0; i < BarangArray.length(); i++)
       {

            DataBarang brg = new DataBarang(SKU[i],Hint[i],Jml[i],HrgSatuan[i],Assigned[i], AssignedImg[i], Keterangan[i], DiscRp[i]);
       // Binds all strings into an array
            BarangList.add(brg);
            TotalRp = TotalRp + (((Float.parseFloat(HrgSatuan[i]) * Integer.parseInt(Jml[i])) - ((Float.parseFloat(DiscRp[i])) * Integer.parseInt(Jml[i]))) * 1.1f);
        }

        // Pass results to ListViewAdapter Class
        adapter = new AdapterBarangListView(this, BarangList);

        TxtSKUTotal = (TextView) findViewById(R.id.Barang_TxtBarangListPrice);
        TxtSKUChecked = (TextView) findViewById(R.id.Barang_TxtBarangListChecked);

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        //
        // Set the new DecimalFormatSymbols into formatter object.
        //

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(TotalRp);

        TxtSKUTotal.setText(currency+" ");
        TxtSKUChecked.setText(BarangArray.length()+" ");

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
                editsearch.setHint(adapter.getItem(position).getKeterangan());

            }
        });

        // Locate the EditText in p_barang.xml
        editsearch = (EditText) findViewById(R.id.search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        BtnSubmit = (Button) findViewById(R.id.Barang_BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),ActivityPembayaran.class);
                in.putExtra(TAG_FAKTUR, faktur);
                in.putExtra(TAG_NOPICKLIST, nopicklist);
                in.putExtra(TAG_KODENOTA, kodenota);
                in.putExtra(TAG_SHIPTO, shipto);
                in.putExtra(TAG_PERUSAHAAN, perusahaan);
                in.putExtra(TAG_TOTALBAYAR, totalbayar);
                in.putExtra("GiroRp",GiroRp);
                in.putExtra("GiroTotal",GiroTotal);
                in.putExtra("GiroResume",GiroResume);
                in.putExtra("TunaiTotal",TunaiTotal);
                in.putExtra("TransferTotal",TotalTransfer);
                in.putExtra("TransferBank",BankTransfer);
                in.putExtra("TotalSKU",BarangArray.length()+"");
                startActivity(in);
            }
        });
    }

    // Not using options menu in this tutorial
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}