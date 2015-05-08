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

public class ActivityPickList extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";
    private final String TAG_NOPICKLIST = "nopicklist";
    private final String TAG_TGLPICKLIST = "tglpicklist";
    private final String TAG_JUMLAH = "jumlah";


    // Declare Variables
    ListView list;
    AdapterInvoiceListView adapter;
    EditText TxtCari;
    //ImageView ImgFilter;

    String[] PickList,Jumlah,Tgl;
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
                InvoiceJSON = db.GetPickList();
                // Getting Array of Pelanggan
                InvoiceArray = InvoiceJSON.getJSONArray(TAG_INVOICEDATA);

                int StatusPickList = InvoiceJSON.getInt("status");

                if (StatusPickList==0){
                    PickList = new String[1];
                    Jumlah = new String[1];
                    Tgl = new String[1];

                    PickList[0] = "Kosong";
                    Jumlah[0] = "0";
                    Tgl[0] = "0000-00-00";

                    DataInvoice plg = new DataInvoice(PickList[0], Jumlah[0], Tgl[0]);
                    InvoiceList.add(plg);

                    Toast.makeText(getApplicationContext(),"Data Kosong",Toast.LENGTH_SHORT).show();
                }else {
                    PickList = new String[InvoiceArray.length()];
                    Jumlah = new String[InvoiceArray.length()];
                    Tgl = new String[InvoiceArray.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < InvoiceArray.length(); i++){
                        JSONObject c = InvoiceArray.getJSONObject(i);

                        //status = c.getInt(TAG_STATUS);
                        String NoPickListS = c.getString(TAG_NOPICKLIST);
                        String JumlahS= c.getString(TAG_JUMLAH);
                        String Tgls= c.getString(TAG_TGLPICKLIST);

                        PickList[i] = NoPickListS;
                        Jumlah[i] = JumlahS;
                        Tgl[i] = Tgls;
                    }

                    for (int i = 0; i < PickList.length; i++)
                    {
                        DataInvoice plg = new DataInvoice(PickList[i], Jumlah[i], Tgl[i]);
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


    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}