package com.bcp.DFA;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class AdapterPelangganUploadedListView extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<DataPelangganUploaded> pelanggandatalist = null;
    private ArrayList<DataPelangganUploaded> arraylist;

    int[] flags = new int[]{
            R.drawable.arrow_left,
            R.drawable.dfa_check,
            R.drawable.dfa_upload
    };

    public AdapterPelangganUploadedListView(Context context, List<DataPelangganUploaded> pelanggandatalist) {
        mContext = context;
        this.pelanggandatalist = pelanggandatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<DataPelangganUploaded>();
        this.arraylist.addAll(pelanggandatalist);

    }

    public class ViewHolder {
        TextView ShipTo;
        TextView Perusahaan;
        TextView Faktur;
        TextView TotalBayar;
        TextView Pembayaran;
        ImageView StatusKirim;
    }

    @Override
    public int getCount() {
        return pelanggandatalist.size();
    }

    @Override
    public DataPelangganUploaded getItem(int position) {
        return pelanggandatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_deliverysuccess, null);
            // Locate the TextViews in listview_item.xml
            holder.ShipTo = (TextView) view.findViewById(R.id.DeliverySucces_ShipTo);
            holder.Perusahaan = (TextView) view.findViewById(R.id.DeliverySucces_Perusahaan);
            holder.Faktur = (TextView) view.findViewById(R.id.DeliverySucces_Faktur);
            holder.TotalBayar = (TextView) view.findViewById(R.id.DeliverySucces_TotalBayar);
            holder.Pembayaran = (TextView) view.findViewById(R.id.DeliverySucces_Pembayaran);
            holder.StatusKirim = (ImageView) view.findViewById(R.id.DeliverySucces_Image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.ShipTo.setText(pelanggandatalist.get(position).getShipTo());
        holder.Perusahaan.setText(pelanggandatalist.get(position).getPerusahaan());
        holder.Faktur.setText(pelanggandatalist.get(position).getFaktur());
        holder.Pembayaran.setText(pelanggandatalist.get(position).getKeterangan());
        holder.StatusKirim.setBackgroundResource(flags[Integer.parseInt(pelanggandatalist.get(position).getStatusKirim())]);

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        //
        // Set the new DecimalFormatSymbols into formatter object.
        //

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(Float.parseFloat(pelanggandatalist.get(position).getTotalBayar()));

        holder.TotalBayar.setText(currency);

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        pelanggandatalist.clear();
        if (charText.length() == 0) {
            pelanggandatalist.addAll(arraylist);
        }
        else
        {
            for (DataPelangganUploaded brg : arraylist)
            {
                if (brg.getKodenota().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    pelanggandatalist.add(brg);
                }
            }
        }
        notifyDataSetChanged();
    }
}