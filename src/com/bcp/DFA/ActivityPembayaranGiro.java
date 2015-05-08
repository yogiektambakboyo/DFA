package com.bcp.DFA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 9/16/14
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityPembayaranGiro extends Activity {
    private final String TAG_FAKTUR = "faktur";
    private final String TAG_KODENOTA = "kodenota";
    private final String TAG_NOPICKLIST = "nopicklist";
    private final String TAG_SHIPTO = "shipto";
    private final String TAG_TOTALBAYAR = "totalbayar";
    private final String TAG_PERUSAHAAN = "perusahaan";

    private final String TAG_PREF="SETTINGPREF";

    String kodenota,nopicklist,faktur;
    TextView TxtKodeNota;
    TextView TxtNoPickList;
    TextView TxtFaktur;
    static TextView TxtTotalGiro;
    static TextView TxtTotalRp;
    Button   BtnSubmit;
    ImageButton BtnTambah;
    ListView list;

    String No,Nominal,Tgl,GiroResume="",shipto,perusahaan,totalbayar;
    static Float  TotalRp=0.0f;
    int GiroTotal = 0;

    ArrayList<DataGiro> GiroList = new ArrayList<DataGiro>();

    AdapterGiroListView adapter;

    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";

    //Return GiroResume,TotalRp,GiroTotal

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_pembayarangiro);

        //Resetting Var
        TotalRp=0.0f;
        GiroTotal = 0;

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);

        Intent in = getIntent();
        kodenota = in.getStringExtra(TAG_KODENOTA);
        nopicklist = in.getStringExtra(TAG_NOPICKLIST);
        faktur = in.getStringExtra(TAG_FAKTUR);
        shipto = in.getStringExtra(TAG_SHIPTO);
        perusahaan = in.getStringExtra(TAG_PERUSAHAAN);
        totalbayar = in.getStringExtra(TAG_TOTALBAYAR);

        TxtFaktur = (TextView) findViewById(R.id.Giro_Faktur);
        TxtKodeNota = (TextView) findViewById(R.id.Giro_KKPDV);
        TxtNoPickList = (TextView) findViewById(R.id.Giro_PickList);

        BtnTambah = (ImageButton) findViewById(R.id.Giro_BtnTambah);
        BtnSubmit = (Button) findViewById(R.id.Giro_BtnSubmit);

        TxtFaktur.setText(faktur);
        TxtKodeNota.setText(kodenota);
        TxtNoPickList.setText(nopicklist);

        BtnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Tambah Giro", Toast.LENGTH_SHORT).show();
                InputGiro();
            }
        });

        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TotalRp>1f){
                    for(int i=0;i<adapter.getCount();i++){
                        /*if (!GiroResume.equals("")){
                            GiroResume = GiroResume+"#";
                        }*/
                        GiroResume = GiroResume+adapter.girodatalist.get(i).getNo()+"&"+adapter.girodatalist.get(i).getNominal()+"#";
                    }
                    Intent in = new Intent();
                    in.putExtra("GiroRp",TotalRp);
                    in.putExtra("GiroTotal",GiroTotal);
                    in.putExtra("GiroResume",GiroResume);
                    setResult(RESULT_OK,in);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Giro Harus Diisi!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        list = (ListView) findViewById(R.id.Giro_List);

        adapter = new AdapterGiroListView(this,GiroList);
        list.setAdapter(adapter);

        TxtTotalGiro = (TextView) findViewById(R.id.Giro_Total);
        TxtTotalRp = (TextView) findViewById(R.id.Giro_Rp);
        TxtTotalGiro.setText("0");
        TxtTotalRp.setText("0");
    }

    public static void setTxtTotalGiro(String Total) {
        TxtTotalGiro.setText(Total);
    }

    public static void setTxtTotalRp(String Total) {
        TotalRp = TotalRp - Float.parseFloat(Total);
        TxtTotalRp.setText(TotalRp.toString());
    }

    public String checkDigit(int number)
    {
        return number<=9?"0"+number:String.valueOf(number);
    }

    public void InputGiro(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtNo = new TextView(this);
        TxtNo.setText("No Cek/BG :");

        final TextView TxtNominal = new TextView(this);
        TxtNominal.setText("Nominal :");

        final TextView TxtTgl = new TextView(this);
        TxtTgl.setText("Tgl :");

        // Set up the input
        final EditText inputNo = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputNo.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNo.setSingleLine();
        inputNo.setHint("Masukkan No Cek/Giro");

        // Set up the input
        final EditText inputNominal = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputNominal.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputNominal.setSingleLine();
        inputNominal.setHint("Masukkan Nominal Uang");
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(8);
        inputNominal.setFilters(FilterArray);

        // Set up the input
        final EditText inputTgl = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputTgl.setInputType(InputType.TYPE_NULL);
        inputTgl.setSingleLine();
        inputTgl.setHint("Masukkan Tanggal");

        layout.addView(TxtNo,params);
        layout.addView(inputNo,params);
        layout.addView(TxtNominal,params);
        layout.addView(inputNominal,params);
        builder.setView(layout);

        inputTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Process to get Current Date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(ActivityPembayaranGiro.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                inputTgl.setText(year + "/" + checkDigit((monthOfYear + 1)) + "/" + checkDigit(dayOfMonth));
                            }
                        }, year, month, day);
                dpd.show();
            }
        });

        // Set up the buttons
        builder.setPositiveButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(inputNominal.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Nominal Tidak Boleh Kosong!!!",Toast.LENGTH_SHORT).show();
                }else if(inputNo.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"No Giro Harus Diisi!!",Toast.LENGTH_SHORT).show();
                }else if(Float.parseFloat(inputNominal.getText().toString())<=0f){
                    Toast.makeText(getApplicationContext(),"Nominal Harus Lebih dari 0!!",Toast.LENGTH_SHORT).show();
                }else{
                    No = inputNo.getText().toString();
                    Nominal = inputNominal.getText().toString();
                    Tgl = inputTgl.getText().toString();

                    DataGiro Giro = new DataGiro(No,Nominal,Tgl);
                    GiroList.add(Giro);
                    adapter.notifyDataSetChanged();

                    TxtTotalGiro.setText(GiroList.size()+"");
                    GiroTotal = GiroList.size();
                    TotalRp = TotalRp + Float.parseFloat(Nominal);
                    TxtTotalRp.setText(TotalRp.toString());
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
}