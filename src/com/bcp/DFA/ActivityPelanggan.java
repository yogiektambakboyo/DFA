package com.bcp.DFA;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityPelanggan extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";
    private final String TAG_STATUS = "status";
    private final String TAG_SHIPTO = "shipto";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_ALAMAT = "alamat";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LASTLOGIN = "lastlogin";

    // Declare Variables
    ListView list;
    AdapterPelangganListView adapter;
    EditText TxtCari;
    ImageView ImgFilter;

    String[] ShipTo,Perusahaan,Alamat,Faktur,Kodenota,TotalBayar;
    ArrayList<DataPelanggan> PelangganList = new ArrayList<DataPelanggan>();

    JSONArray PelangganArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    List AreaList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_pelanggan);

        AreaList = new ArrayList();

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject PelangganJSON = null;

        if(dbFile.exists()){
            try {
                PelangganJSON = db.GetPelanggan(getPref(TAG_NAMELOGIN));
                // Getting Array of Pelanggan
                PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                ShipTo = new String[PelangganArray.length()];
                Perusahaan = new String[PelangganArray.length()];
                Alamat = new String[PelangganArray.length()];
                Kodenota = new String[PelangganArray.length()];
                TotalBayar = new String[PelangganArray.length()];

                // looping through All Pelanggan
                for(int i = 0; i < PelangganArray.length(); i++){
                    JSONObject c = PelangganArray.getJSONObject(i);

                    //status = c.getInt(TAG_STATUS);
                    String ShipToS = c.getString(TAG_SHIPTO);
                    String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                    String AlamatS= c.getString(TAG_ALAMAT);
                    String KodenotaS= c.getString("kodenota");
                    String TotalbayarS= c.getString("totalbayar");

                    ShipTo[i] = ShipToS;
                    Perusahaan[i] = PerusahaanS;
                    Alamat[i] = AlamatS;
                    Kodenota[i] = KodenotaS;
                    TotalBayar[i] = TotalbayarS;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            db.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }


        list = (ListView) findViewById(R.id.PelangganListView);


        for (int i = 0; i < ShipTo.length; i++)
        {
            //DataPelanggan plg = new DataPelanggan(ShipTo[i], Perusahaan[i], Alamat[i], Faktur[i], Kodenota[i],TotalBayar[i]);
            // Binds all strings into an array
            //PelangganList.add(plg);
        }

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,"0");

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent ints = new Intent(getApplicationContext(), ActivityInvoice.class);
                ints.putExtra(TAG_SHIPTO, ShipTo[position]);
                startActivity(ints);
            }
        });

        TxtCari = (EditText) findViewById(R.id.Pelanggan_Search);

        // Capture Text in EditText
        TxtCari.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = TxtCari.getText().toString().toLowerCase(Locale.getDefault());
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

        ImgFilter = (ImageView) findViewById(R.id.Pelanggan_ImgFilter);
        ImgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });
    }

    // Not using options menu in this tutorial
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void showFilter(){
        final Dialog dialog = new Dialog(ActivityPelanggan.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Filter Pelanggan");
        dialog.setContentView(R.layout.d_filterpelanggan);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        Spinner SpnArea = (Spinner) dialog.findViewById(R.id.Pelanggan_SpnArea);

        AreaList.clear();
        AreaList.add("Sidoarjo");
        AreaList.add("Surabaya");
        AreaList.add("Denpasar");

        ArrayAdapter dataAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, AreaList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnArea.setAdapter(dataAdapter);

        SpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(AreaList.get(position).equals("Sidoarjo")){
                    adapter.filter("korsal");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.show();
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}