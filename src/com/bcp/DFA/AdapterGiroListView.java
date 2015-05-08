package com.bcp.DFA;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 9/17/14
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdapterGiroListView extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    List<DataGiro> girodatalist = null;
    ArrayList<DataGiro> arraylist;

    public AdapterGiroListView(Context context, List<DataGiro> girodatalist) {
        mContext = context;
        this.girodatalist = girodatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<DataGiro>();
        this.arraylist.addAll(girodatalist);

    }

    public class ViewHolder {
        TextView No;
        TextView Nominal;
        TextView Tgl;
        ImageButton ImgBtnDel;
    }

    @Override
    public int getCount() {
        return girodatalist.size();
    }

    @Override
    public DataGiro getItem(int position) {
        return girodatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_pembayarangiro, null);
            // Locate the TextViews in listview_item.xml
            holder.No = (TextView) view.findViewById(R.id.Giro_No);
            holder.Nominal = (TextView) view.findViewById(R.id.Giro_Nominal);
            holder.Tgl = (TextView) view.findViewById(R.id.Giro_Tgl);
            holder.ImgBtnDel = (ImageButton) view.findViewById(R.id.ImgBtn_DelGiro);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.No.setText(girodatalist.get(position).getNo());

        Float nominal = Float.parseFloat(girodatalist.get(position).getNominal());

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        //
        // Set the new DecimalFormatSymbols into formatter object.
        //

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(nominal);

        holder.Nominal.setText(currency);
        holder.Tgl.setText(girodatalist.get(position).getTgl());

        holder.ImgBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityPembayaranGiro.setTxtTotalRp(girodatalist.get(position).getNominal());
                girodatalist.remove(position);
                notifyDataSetChanged();
                ActivityPembayaranGiro.setTxtTotalGiro(girodatalist.size()+"");
            }
        });

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        girodatalist.clear();
        if (charText.length() == 0) {
            girodatalist.addAll(arraylist);
        }
        else
        {
            for (DataGiro brg : arraylist)
            {
                if (brg.getNo().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    girodatalist.add(brg);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<DataGiro> getGirodatalist() {
        return girodatalist;
    }
}