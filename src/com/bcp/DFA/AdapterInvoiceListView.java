package com.bcp.DFA;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 8/25/14
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class AdapterInvoiceListView extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<DataInvoice> invoicedatalist = null;
    private ArrayList<DataInvoice> arraylist;

    public AdapterInvoiceListView(Context context, List<DataInvoice> invoicedatalist) {
        mContext = context;
        this.invoicedatalist = invoicedatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<DataInvoice>();
        this.arraylist.addAll(invoicedatalist);

    }

    public class ViewHolder {
        TextView Kodenota;
        TextView Faktur;
        TextView Tgl;
    }

    @Override
    public int getCount() {
        return invoicedatalist.size();
    }

    @Override
    public DataInvoice getItem(int position) {
        return invoicedatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_invoice, null);
            // Locate the TextViews in listview_item.xml
            holder.Faktur = (TextView) view.findViewById(R.id.Faktur_Faktur);
            holder.Kodenota = (TextView) view.findViewById(R.id.Faktur_KKDV);
            holder.Tgl = (TextView) view.findViewById(R.id.Faktur_Tgl);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.Kodenota.setText(invoicedatalist.get(position).getKodenota());
        holder.Faktur.setText(invoicedatalist.get(position).getJumlah());
        holder.Tgl.setText(invoicedatalist.get(position).getTgl());

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        invoicedatalist.clear();
        if (charText.length() == 0) {
            invoicedatalist.addAll(arraylist);
        }
        else
        {
            for (DataInvoice faktur : arraylist)
            {
                if (faktur.getKodenota().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    invoicedatalist.add(faktur);
                }
            }
        }
        notifyDataSetChanged();
    }
}