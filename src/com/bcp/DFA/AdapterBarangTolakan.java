package com.bcp.DFA;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class AdapterBarangTolakan extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<DataBarangTolakan> barangdatalist = null;
    private ArrayList<DataBarangTolakan> arraylist;

    public AdapterBarangTolakan(Context context, List<DataBarangTolakan> barangdatalist) {
        mContext = context;
        this.barangdatalist = barangdatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<DataBarangTolakan>();
        this.arraylist.addAll(barangdatalist);
    }

    public class ViewHolder {
        TextView SKU;
        TextView Hint;
        //TextView HrgSatuan;
        TextView Assigned;
        TextView Jml;
        TextView JmlCRT;
        TextView JmlPCS;
        TextView RasioMax;
        TextView JmlFakturCRT;
        ImageView AssignedImg;
    }

    @Override
    public int getCount() {
        return barangdatalist.size();
    }

    @Override
    public DataBarangTolakan getItem(int position) {
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
            view = inflater.inflate(R.layout.l_tolakan, null);
            holder.SKU = (TextView) view.findViewById(R.id.Tolakan_TxtSKUCode);
            holder.Hint = (TextView) view.findViewById(R.id.Tolakan_TxtHint);
            holder.Jml = (TextView) view.findViewById(R.id.Tolakan_TxtJml);
            holder.JmlCRT = (TextView) view.findViewById(R.id.Tolakan_TxtCRT);
            holder.JmlPCS = (TextView) view.findViewById(R.id.Tolakan_TxtPCS);
            holder.Assigned = (TextView) view.findViewById(R.id.Tolakan_TxtAssigned);
            holder.RasioMax = (TextView) view.findViewById(R.id.Tolakan_TxtRasioMax);
            holder.JmlFakturCRT = (TextView) view.findViewById(R.id.Tolakan_TxtJmlFakturCRT);
            holder.AssignedImg = (ImageView) view.findViewById(R.id.Tolakan_ImgCheck);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.SKU.setText(barangdatalist.get(position).getSKU());
        holder.Hint.setText(barangdatalist.get(position).getKeterangan());
        holder.Jml.setText(barangdatalist.get(position).getJml());
        holder.JmlCRT.setText(barangdatalist.get(position).getJmlCRT());
        holder.JmlPCS.setText(barangdatalist.get(position).getJmlPCS());
        holder.Assigned.setText(barangdatalist.get(position).getAssigned());
        holder.AssignedImg.setBackgroundResource(Integer.parseInt(barangdatalist.get(position).getAssignedImg()));
        holder.RasioMax.setText(barangdatalist.get(position).getRasioMax());
        holder.JmlFakturCRT.setText(barangdatalist.get(position).getJmlFakturCRT());


        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        barangdatalist.clear();
        if (charText.length() == 0) {
            barangdatalist.addAll(arraylist);
        }
        else
        {
            for (DataBarangTolakan brg : arraylist)
            {
                if (brg.getKeterangan().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    barangdatalist.add(brg);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Filter Class
    public List<DataBarangTolakan> FinalDataBarangTolakan() {
        barangdatalist.clear();
            for (DataBarangTolakan brg : arraylist)
            {
                if ((Integer.parseInt(brg.getJmlCRT())>0)||(Integer.parseInt(brg.getJmlPCS())>0))
                {
                    barangdatalist.add(brg);
                }
            }
        notifyDataSetChanged();
        return barangdatalist;
    }

    public int AddDataBarang(String SKU,String Hint,String Jml, String HrgSatuan,String Assigned, String AssignedImg, String Keterangan, String DiscRp, String JmlCRT, String JmlPCS, String RasioMax, String JmlFakturCRT) {
        int valid = 1;
        for (int i=0;i<barangdatalist.size();i++)
        {
            if (barangdatalist.get(i).getSKU().toLowerCase(Locale.getDefault()).contains(SKU.trim().toLowerCase(Locale.getDefault())))
            {
                valid = 0;
            }
        }
        if(valid==1){
            DataBarangTolakan brg = new DataBarangTolakan(SKU,Hint,Jml,HrgSatuan,Assigned, AssignedImg, Keterangan, DiscRp,JmlCRT,JmlPCS,RasioMax,JmlFakturCRT);
            barangdatalist.add(brg);
        }
        notifyDataSetChanged();
        return valid;
    }

    public int BrgTolakan() {
        int jml=0;
        for (DataBarangTolakan brg : arraylist)
        {
            if ((Integer.parseInt(brg.getJmlCRT())>0)||(Integer.parseInt(brg.getJmlPCS())>0))
            {
                jml = jml+1;
            }
        }
        return jml;
    }

    public int BrgTolakan2() {
        int jml=0;
        for (int i=0;i<barangdatalist.size();i++)
        {
            if ((Integer.parseInt(barangdatalist.get(i).getJmlCRT())>0)||(Integer.parseInt(barangdatalist.get(i).getJmlPCS())>0))
            {
                jml = jml+1;
            }
        }
        return jml;
    }

    public float RpBrgTolakan() {
        float jml=0f;
        for (DataBarangTolakan brg : arraylist)
        {
            if ((Integer.parseInt(brg.getJmlCRT())>0)||(Integer.parseInt(brg.getJmlPCS())>0))
            {
                jml = jml + ((((Float.parseFloat(brg.getHrgSatuan()) - Float.parseFloat(brg.getDiscRp())) * Integer.parseInt(brg.getJmlPCS())) + ((Float.parseFloat(brg.getHrgSatuan()) - Float.parseFloat(brg.getDiscRp())) * Integer.parseInt(brg.getJmlCRT()) * Integer.parseInt(brg.getRasioMax())))) ;
            }
        }
        return jml;
    }

    public void FilterTolakan(int a) {
        barangdatalist.clear();
        if (a == 0) {
            barangdatalist.addAll(arraylist);
        }
        else
        {
            for (DataBarangTolakan brg : arraylist)
            {
                if ((Integer.parseInt(brg.getJmlCRT())>0)||(Integer.parseInt(brg.getJmlPCS())>0))
                {
                    barangdatalist.add(brg);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<DataBarangTolakan> FinalDataBarangChecker() {
        notifyDataSetChanged();
        return barangdatalist;
    }

}