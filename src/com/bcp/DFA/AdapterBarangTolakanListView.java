package com.bcp.DFA;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class AdapterBarangTolakanListView extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<DataBarang> barangdatalist = null;
    private ArrayList<DataBarang> arraylist;

    public AdapterBarangTolakanListView(Context context, List<DataBarang> barangdatalist) {
        mContext = context;
        this.barangdatalist = barangdatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<DataBarang>();
        this.arraylist.addAll(barangdatalist);

    }

    public class ViewHolder {
        TextView Keterangan;
        TextView Jml;
    }

    @Override
    public int getCount() {
        return barangdatalist.size();
    }

    @Override
    public DataBarang getItem(int position) {
        return barangdatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_barangdetailtolakan, null);
            // Locate the TextViews in listview_item.xml
            holder.Keterangan = (TextView) view.findViewById(R.id.BarangTolakan_TxtKet);
            holder.Jml = (TextView) view.findViewById(R.id.BarangTolakan_TxtJml);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.Keterangan.setText(barangdatalist.get(position).getKeterangan());
        holder.Jml.setText(barangdatalist.get(position).getJml());
        return view;
    }

}