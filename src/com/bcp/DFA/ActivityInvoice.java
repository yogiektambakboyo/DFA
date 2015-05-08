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
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityInvoice extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";
    private final String TAG_STATUS = "status";
    private final String TAG_KODENOTA = "kodenota";
    private final String TAG_JUMLAH = "jumlah";
    private final String TAG_SHIPTO = "shipto";
    private final String TAG_TGLPICKLIST = "tglpicklist";

    // Declare Variables
    ListView list;
    AdapterInvoiceListView adapter;
    EditText TxtCari;
    //ImageView ImgFilter;

    String[] Kodenota,Faktur,Jumlah,Tgl;
    ArrayList<DataInvoice> InvoiceList = new ArrayList<DataInvoice>();

    JSONArray InvoiceArray;
    private final String TAG_INVOICEDATA= "InvoiceData";

    private final String TAG_PREF="SETTINGPREF";

    List AreaList;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_invoice);

        AreaList = new ArrayList();


        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject InvoiceJSON = null;

        if(dbFile.exists()){
            try {
                InvoiceJSON = db.GetInvoice();
                // Getting Array of Pelanggan
                InvoiceArray = InvoiceJSON.getJSONArray(TAG_INVOICEDATA);

                int StatusInvoice = InvoiceJSON.getInt("status");

                    if (StatusInvoice==0){
                        Kodenota = new String[1];
                        Jumlah = new String[1];
                        Tgl = new String[1];

                        Kodenota[0] = "Kosong";
                        Jumlah[0] = "0";
                        Tgl[0] = "0000-00-00";

                        DataInvoice plg = new DataInvoice(Kodenota[0], Jumlah[0], Tgl[0]);
                        InvoiceList.add(plg);

                        Toast.makeText(getApplicationContext(),"Data Kosong",Toast.LENGTH_SHORT).show();

                    }else{
                        Kodenota = new String[InvoiceArray.length()];
                        Jumlah = new String[InvoiceArray.length()];
                        Tgl = new String[InvoiceArray.length()];

                        // looping through All Pelanggan
                        for(int i = 0; i < InvoiceArray.length(); i++){
                            JSONObject c = InvoiceArray.getJSONObject(i);

                            String KodenotaS = c.getString(TAG_KODENOTA);
                            String JumlahS= c.getString(TAG_JUMLAH);
                            String Tgls= c.getString(TAG_TGLPICKLIST);

                            Kodenota[i] = KodenotaS;
                            Jumlah[i] = JumlahS;
                            Tgl[i] = Tgls;
                        }



                        for (int i = 0; i < Kodenota.length; i++)
                        {
                            DataInvoice plg = new DataInvoice(Kodenota[i], Jumlah[i], Tgl[i]);
                            // Binds all strings into an array
                            InvoiceList.add(plg);
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            db.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        list = (ListView) findViewById(R.id.FakturListView);


        // Pass results to ListViewAdapter Class
        adapter = new AdapterInvoiceListView(getApplicationContext(), InvoiceList);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        TxtCari = (EditText) findViewById(R.id.Faktur_Search);

        // Capture Text in EditText
        TxtCari.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = TxtCari.getText().toString().toLowerCase(Locale.getDefault());
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

    }

    private void showFilter(){
        final Dialog dialog = new Dialog(ActivityInvoice.this, android.R.style.Theme_Dialog);
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
                Toast.makeText(getApplicationContext(),"Area : "+AreaList.get(position),Toast.LENGTH_SHORT).show();
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