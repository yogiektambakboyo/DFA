package com.bcp.DFA;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ActivityMainMenuChecker extends ListActivity {

    private final String TAG_MENU = "menu";
    private final String TAG_ICON = "1";
    private final String TAG_ID = "id";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_WEB = "web";

    String Web;

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.dfa_list,
            R.drawable.dfa_delivery,
            R.drawable.dfa_sync,
            R.drawable.dfa_success
    };

    TextView TxtUser,TxtTime;
    ImageView ImgInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_mainmenu);


        TxtUser = (TextView) findViewById(R.id.MainMenu_TxtUser);
        TxtTime = (TextView) findViewById(R.id.MainMenu_TxtTime);

        ImgInfo = (ImageView) findViewById(R.id.MainMenu_Info);
        ImgInfo.setVisibility(View.GONE);

        TxtUser.setText(getPref(TAG_NAMELOGIN));
        TxtTime.setText(getPref(TAG_LASTLOGIN));

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);


        // Hashmap for ListView
        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> mapdeliverylist = new HashMap<String, String>();
        mapdeliverylist.put(TAG_ICON, Integer.toString(flags[0]));
        mapdeliverylist.put(TAG_ID, "0");
        mapdeliverylist.put(TAG_MENU, "Tolakan Delivery");

        HashMap<String, String> mapsync = new HashMap<String, String>();
        mapsync.put(TAG_ICON, Integer.toString(flags[2]));
        mapsync.put(TAG_ID, "1");
        mapsync.put(TAG_MENU, "Sinkronisasi");

        HashMap<String, String> mapdeliverysuccess = new HashMap<String, String>();
        mapdeliverysuccess.put(TAG_ICON, Integer.toString(flags[3]));
        mapdeliverysuccess.put(TAG_ID, "2");
        mapdeliverysuccess.put(TAG_MENU, "Tolakan Success");

        OperatorMenuList.add(mapdeliverylist);
        OperatorMenuList.add(mapsync);
        OperatorMenuList.add(mapdeliverysuccess);

        /**
         * Updating parsed JSON data into ListView
         * */
        ListAdapter adapter = new AdapterCustomSimple(this, OperatorMenuList,
                R.layout.l_mainmenu,
                new String[] { TAG_ICON, TAG_MENU, TAG_ID },
                new int[] { R.id.imageViewOP, R.id.MainMenuNama, R.id.MainMenuID });

        setListAdapter(adapter);

        // selecting single ListView item
        ListView lv = getListView();

        // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String nama = ((TextView) view.findViewById(R.id.MainMenuNama)).getText().toString();
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();

                if(menuid.equals("0")){
                    Toast.makeText(getApplicationContext(),"Tolakan Delivery",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(),ActivityCheckerOrder.class);
                    startActivity(in);
                }

                if(menuid.equals("1")){
                    Toast.makeText(getApplicationContext(),"Sinkron",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(),ActivityCheckerSinkron.class);
                    startActivity(in);
                }

                if(menuid.equals("2")){
                    Toast.makeText(getApplicationContext(),"Tolakan Success",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(),ActivityCheckerSuccess.class);
                    startActivity(in);
                }


            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi Log Out");
            builder.setMessage("Yakin ingin logout?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}