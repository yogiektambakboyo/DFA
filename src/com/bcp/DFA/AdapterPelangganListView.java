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

public class AdapterPelangganListView extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<DataPelanggan> pelanggandatalist = null;
    private ArrayList<DataPelanggan> arraylist;
    String Checker;

    int[] flags = new int[]{
            R.drawable.arrow_left,
            R.drawable.dfa_check,
            R.drawable.dfa_upload
    };

    public AdapterPelangganListView(Context context, List<DataPelanggan> pelanggandatalist,String checker) {
        mContext = context;
        this.pelanggandatalist = pelanggandatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<DataPelanggan>();
        this.arraylist.addAll(pelanggandatalist);
        this.Checker = checker;
    }

    public class ViewHolder {
        TextView ShipTo;
        TextView Perusahaan;
        TextView Alamat;
        TextView Faktur;
        TextView TotalBayar;
        TextView NoPickList;
        TextView KetPem;
        ImageView StatusKirim;
        TextView LblAlamat;
        TextView LblTotal;
    }

    @Override
    public int getCount() {
        return pelanggandatalist.size();
    }

    @Override
    public DataPelanggan getItem(int position) {
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
            view = inflater.inflate(R.layout.l_pelanggan, null);
            // Locate the TextViews in listview_item.xml
            holder.ShipTo = (TextView) view.findViewById(R.id.Pelanggan_ShipTo);
            holder.Perusahaan = (TextView) view.findViewById(R.id.Pelanggan_Perusahaan);
            holder.Alamat = (TextView) view.findViewById(R.id.Pelanggan_Alamat);
            holder.Faktur = (TextView) view.findViewById(R.id.Pelanggan_Faktur);
            holder.TotalBayar = (TextView) view.findViewById(R.id.Pelanggan_TotalBayar);
            holder.NoPickList = (TextView) view.findViewById(R.id.Pelanggan_NoPickList);
            holder.KetPem = (TextView) view.findViewById(R.id.Pelanggan_KetPem);
            holder.StatusKirim = (ImageView) view.findViewById(R.id.Pelanggan_Image);
            holder.LblAlamat = (TextView) view.findViewById(R.id.DeliveryOrderList_TxtTgl);
            holder.LblTotal = (TextView) view.findViewById(R.id.DeliveryOrderList_TxtJmlTolakan);
            if(Checker.equals("1")){
                holder.LblAlamat.setText("Tgl      :");
                holder.LblTotal.setText("Tolakan  :");
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.ShipTo.setText(pelanggandatalist.get(position).getShipTo());
        holder.Perusahaan.setText(pelanggandatalist.get(position).getPerusahaan());
        holder.Alamat.setText(pelanggandatalist.get(position).getAlamat());
        holder.Faktur.setText(pelanggandatalist.get(position).getFaktur());
        holder.NoPickList.setText(pelanggandatalist.get(position).getNoPickList());
        holder.KetPem.setText(pelanggandatalist.get(position).getKetPem());
        holder.StatusKirim.setBackgroundResource(flags[Integer.parseInt(pelanggandatalist.get(position).getStatusKirim())]);

        if(Checker.equals("0")){
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
        }else{
            holder.TotalBayar.setText(pelanggandatalist.get(position).getTotalBayar());
        }


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
            for (DataPelanggan brg : arraylist)
            {
                if (brg.getKodenota().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    pelanggandatalist.add(brg);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterKetPem(String charText,String Kodenota) {
        charText = charText.toLowerCase(Locale.getDefault());
        Kodenota = Kodenota.toLowerCase(Locale.getDefault());
        pelanggandatalist.clear();
        if (charText.length() == 0) {
            //pelanggandatalist.addAll(arraylist);
            for (DataPelanggan brg : arraylist)
            {
                if (brg.getKodenota().toLowerCase(Locale.getDefault()).contains(Kodenota))
                {
                    pelanggandatalist.add(brg);
                }
            }
        }
        else
        {
            for (DataPelanggan brg : arraylist)
            {
                if (brg.getKetPem().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    //pelanggandatalist.add(brg);
                    if (brg.getKodenota().toLowerCase(Locale.getDefault()).contains(Kodenota))
                    {
                        pelanggandatalist.add(brg);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    // Filter Class
    public void filterTolakan(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        pelanggandatalist.clear();
        if (charText.length() == 0) {
            pelanggandatalist.addAll(arraylist);
        }
        else
        {
            for (DataPelanggan brg : arraylist)
            {
                String a = brg.getKodenota() +" - "+brg.getNoPickList();
                if (a.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    pelanggandatalist.add(brg);
                }
            }
        }
        notifyDataSetChanged();
    }
}