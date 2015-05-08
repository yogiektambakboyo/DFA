package com.bcp.DFA;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 9/25/14
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class AdapterMenuPembayaranListView extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<DataMenuPembayaran> menupembayaranlist = null;
    private ArrayList<DataMenuPembayaran> arrayList;

    public AdapterMenuPembayaranListView(Context context, List<DataMenuPembayaran> menupembayaranlist){
        mContext = context;
        this.menupembayaranlist = menupembayaranlist;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<DataMenuPembayaran>();
        this.arrayList.addAll(menupembayaranlist);
    }

    public class ViewHolder{
        TextView TxtNamaTask;
        TextView TxtID;
        ImageView ImgIcon;
        ImageView ImgAssigned;
    }

    @Override
    public int getCount() {
        return menupembayaranlist.size();
    }

    @Override
    public DataMenuPembayaran getItem(int position) {
        return menupembayaranlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view==null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_pembayaran,null);
            holder.TxtID = (TextView) view.findViewById(R.id.Pembayaran_ID);
            holder.TxtNamaTask = (TextView) view.findViewById(R.id.Pembayaran_Nama);
            holder.ImgIcon = (ImageView) view.findViewById(R.id.PembayaranimageViewOP);
            holder.ImgAssigned = (ImageView) view.findViewById(R.id.Pembayaran_ArrowNav);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.ImgIcon.setBackgroundResource(Integer.parseInt(menupembayaranlist.get(position).getIcon()));
        holder.TxtID.setText(menupembayaranlist.get(position).getID());
        holder.TxtNamaTask.setText(menupembayaranlist.get(position).getNama());
        holder.ImgAssigned.setBackgroundResource(Integer.parseInt(menupembayaranlist.get(position).getAssignedImg()));

        return view;
    }
}