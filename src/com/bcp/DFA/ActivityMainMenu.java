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

public class ActivityMainMenu extends ListActivity {

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

    FN_DBHandler dbmst;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/DFA";
    private String      DB_MASTER="MASTER";

    private final String TAG_KKPDV = "kkpdv";
    private final String TAG_KKPDVRP = "kkpdvrp";
    private final String TAG_FAKTUR = "faktur";
    private final String TAG_FAKTURFIN = "fakturfin";
    private final String TAG_FAKTURRP = "fakturrp";
    private final String TAG_FAKTURFINTUN = "fakturfintun";
    private final String TAG_FAKTURTUNRP = "fakturtunrp";
    private final String TAG_FAKTURFINBG = "fakturfinbg";
    private final String TAG_FAKTURBGRP = "fakturbgrp";
    private final String TAG_FAKTURTRN = "fakturtrn";
    private final String TAG_FAKTURTRNRP = "fakturtrnrp";
    private final String TAG_FAKTURTUNBG = "fakturtunbg";
    private final String TAG_FAKTURTUNBGRP = "fakturtunbgrp";
    private final String TAG_FAKTURTT = "fakturtt";
    private final String TAG_FAKTURTKIRIM = "fakturtakkirim";
    private final String TAG_FAKTURSTEMPEL = "fakturstempel";
    private final String TAG_FAKTURTOLAKAN = "fakturtlk";

    String kkpdv,kkpdvrp,faktur,fakturfin,fakturrp,fakturfintun,fakturtunrp,fakturfinbg,fakturbgrp,fakturtunbg,fakturtunbgrp,fakturtt,fakturstempel,fakturtakterkirim,fakturremain;
    String fakturtrn, fakturtrnrp, fakturtlk;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_mainmenu);

        dbmst = new FN_DBHandler(getApplicationContext(),DB_PATH,DB_MASTER);

        JSONObject KKPDVInfo = null;
        try {
            KKPDVInfo = dbmst.GetKKPDVInfo();

            kkpdv = KKPDVInfo.getString(TAG_KKPDV);

            DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
            symbol.setCurrencySymbol("");
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
            formatter.setDecimalFormatSymbols(symbol);

            kkpdvrp = formatter.format(Float.parseFloat(KKPDVInfo.getString(TAG_KKPDVRP)));
            faktur = KKPDVInfo.getString(TAG_FAKTUR);
            fakturfin = KKPDVInfo.getString(TAG_FAKTURFIN);
            fakturrp = formatter.format(Float.parseFloat(KKPDVInfo.getString(TAG_FAKTURRP)));
            fakturfintun = KKPDVInfo.getString(TAG_FAKTURFINTUN);
            fakturtunrp = formatter.format(Float.parseFloat(KKPDVInfo.getString(TAG_FAKTURTUNRP)));
            fakturfinbg = KKPDVInfo.getString(TAG_FAKTURFINBG);
            fakturbgrp = formatter.format(Float.parseFloat(KKPDVInfo.getString(TAG_FAKTURBGRP)));
            fakturtunbg = KKPDVInfo.getString(TAG_FAKTURTUNBG);
            fakturtunbgrp = formatter.format(Float.parseFloat(KKPDVInfo.getString(TAG_FAKTURTUNBGRP)));
            fakturtt = KKPDVInfo.getString(TAG_FAKTURTT);
            fakturstempel = KKPDVInfo.getString(TAG_FAKTURSTEMPEL);
            fakturtakterkirim = KKPDVInfo.getString(TAG_FAKTURTKIRIM);
            fakturremain = Integer.toString(Integer.parseInt(faktur) - Integer.parseInt(fakturfin));
            fakturtrn = KKPDVInfo.getString(TAG_FAKTURTRN);
            fakturtrnrp = formatter.format(Float.parseFloat(KKPDVInfo.getString(TAG_FAKTURTRNRP)));
            fakturtlk = KKPDVInfo.getString(TAG_FAKTURTOLAKAN);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        TxtUser = (TextView) findViewById(R.id.MainMenu_TxtUser);
        TxtTime = (TextView) findViewById(R.id.MainMenu_TxtTime);

        ImgInfo = (ImageView) findViewById(R.id.MainMenu_Info);
        ImgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDeliveryInfo(kkpdv,kkpdvrp,faktur,fakturfin,fakturrp,fakturfintun,fakturtunrp,fakturfinbg,fakturbgrp,fakturtunbg,fakturtunbgrp,fakturtt,fakturstempel,fakturtakterkirim,fakturremain,fakturtrn,fakturtrnrp, fakturtlk);
            }
        });

        TxtUser.setText(getPref(TAG_NAMELOGIN));
        TxtTime.setText(getPref(TAG_LASTLOGIN));

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);


        // Hashmap for ListView
        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> mapdeliverylist = new HashMap<String, String>();
        mapdeliverylist.put(TAG_ICON, Integer.toString(flags[0]));
        mapdeliverylist.put(TAG_ID, "0");
        mapdeliverylist.put(TAG_MENU, "Delivery & Pick List");

        HashMap<String, String> mapdelivery = new HashMap<String, String>();
        mapdelivery.put(TAG_ICON, Integer.toString(flags[1]));
        mapdelivery.put(TAG_ID, "1");
        mapdelivery.put(TAG_MENU, "Delivery Order");

        HashMap<String, String> mapsync = new HashMap<String, String>();
        mapsync.put(TAG_ICON, Integer.toString(flags[2]));
        mapsync.put(TAG_ID, "2");
        mapsync.put(TAG_MENU, "Sinkronisasi");

        HashMap<String, String> mapdeliverysuccess = new HashMap<String, String>();
        mapdeliverysuccess.put(TAG_ICON, Integer.toString(flags[3]));
        mapdeliverysuccess.put(TAG_ID, "3");
        mapdeliverysuccess.put(TAG_MENU, "Delivery Success");

        OperatorMenuList.add(mapdeliverylist);
        OperatorMenuList.add(mapdelivery);
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
                    Intent in = new Intent(getApplicationContext(), ActivityMenuDeliveryList.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                }

                if(menuid.equals("1")){
                    dbmst.DeleteTolakanClear();
                    Intent in = new Intent(getApplicationContext(), ActivityDeliveryOrder.class);                                                                                     //
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                }

                if(menuid.equals("2")){
                    Intent in = new Intent(getApplicationContext(),ActivitySinkron.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.putExtra(TAG_WEB,Web);
                    startActivity(in);
                }

                if(menuid.equals("3")){
                    Intent in = new Intent(getApplicationContext(),ActivityDeliverySuccess.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.putExtra(TAG_WEB,Web);
                    startActivity(in);
                }

            }
        });
        dbmst.close();
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
    private void Notif(){
        Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification n  = new Notification.Builder(getApplicationContext())
                .setContentTitle("New Message From Delivery")
                .setContentText("Kamu Nakal Yach :-p")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher, "Call", pIntent)
                .addAction(R.drawable.ic_launcher, "More", pIntent)
                .addAction(R.drawable.ic_launcher, "And more", pIntent).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    private void ShowDeliveryInfo(String KKPDV,String KKPDVRp,String Faktur,String FakturFin,String FakturRp, String FakturFinTun, String FakturFinTunRp, String FakturFinBg, String FakturFinBGRp,String FakturFinTunBG, String FakturFinTunBgRp, String FakturTT, String FakturStempel,String FakturTakTerkirim, String FakturRem,String FakturTrn, String FakturTrnRp, String FakturTlk){
        final Dialog dialog = new Dialog(ActivityMainMenu.this, android.R.style.Theme_Dialog);
        dialog.setTitle("Delivery Info");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_deliveryinfo);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.d_deliveryinfo, null);

        TextView TxtKKPDV = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoKKPDV);
        TxtKKPDV.setText("("+KKPDV+")");
        TextView TxtKKPDVRp = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoKKPDVRp);
        TxtKKPDVRp.setText("Rp. "+KKPDVRp);
        TextView TxtFaktur = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFaktur);
        TxtFaktur.setText(Faktur);
        TextView TxtFakturFin = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturFin);
        TxtFakturFin.setText("("+FakturFin+")");
        TextView TxtFakturRp = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturFinRp);
        TxtFakturRp.setText("Rp. "+FakturRp);
        TextView TxtFakturFinTun = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTun);
        TxtFakturFinTun.setText(FakturFinTun);
        TextView TxtFakturFinTunRp = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTunRp);
        TxtFakturFinTunRp.setText("Rp. "+FakturFinTunRp);
        TextView TxtFakturFinBg = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturBG);
        TxtFakturFinBg.setText(FakturFinBg);
        TextView TxtFakturFinBgRp = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturBGRp);
        TxtFakturFinBgRp.setText("Rp. "+FakturFinBGRp);
        TextView TxtFakturFinTunBg = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTunBG);
        TxtFakturFinTunBg.setText(FakturFinTunBG);
        TextView TxtFakturFinTunBgRp = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTunBGRp);
        TxtFakturFinTunBgRp.setText("Rp. "+FakturFinTunBgRp);
        TextView TxtFakturFinTT = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTT);
        TxtFakturFinTT.setText(FakturTT);
        TextView TxtFakturFinStempel = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturStempel);
        TxtFakturFinStempel.setText(FakturStempel);
        TextView TxtFakturRemain = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturRem);
        TxtFakturRemain.setText(FakturRem);
        TextView TxtFakturTakTerkirim = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTakTerkirim);
        TxtFakturTakTerkirim.setText(FakturTakTerkirim);

        TextView TxtFakturTolakan = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTolakan);
        TxtFakturTolakan.setText(FakturTlk);

        TextView TxtFakturTrn = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTransfer);
        TxtFakturTrn.setText(FakturTrn);

        TextView TxtFakturTrnRp = (TextView) dialog.findViewById(R.id.MainMenu_DeliveryInfoFakturTransferRp);
        TxtFakturTrnRp.setText("Rp. "+FakturTrnRp);

        final Button BtnBatal = (Button) dialog.findViewById(R.id.MainMenu_DeliveryInfoBtnClose);
        BtnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.dfa_info_down);
    }
}