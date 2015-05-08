package com.bcp.DFA;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ActivityMenuDeliveryList extends ListActivity {

    private final String TAG_MENU = "menu";
    private final String TAG_ICON = "1";
    private final String TAG_ID = "id";

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.dfa_list_kkpdv,
            R.drawable.dfa_list_pl,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher
    };



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_menudeliverylist);

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> mapdeliverylist = new HashMap<String, String>();
        mapdeliverylist.put(TAG_ICON, Integer.toString(flags[0]));
        mapdeliverylist.put(TAG_ID, "0");
        mapdeliverylist.put(TAG_MENU, "KKPDV List");

        HashMap<String, String> mappicklist = new HashMap<String, String>();
        mappicklist.put(TAG_ICON, Integer.toString(flags[1]));
        mappicklist.put(TAG_ID, "1");
        mappicklist.put(TAG_MENU, "Pick List");


        OperatorMenuList.add(mapdeliverylist);
        OperatorMenuList.add(mappicklist);

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
                    Intent in = new Intent(getApplicationContext(), ActivityInvoice.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                }

                if(menuid.equals("1")){
                    Intent in = new Intent(getApplicationContext(), ActivityPickList.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return false;
    }
}