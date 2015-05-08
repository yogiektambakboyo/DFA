package com.bcp.DFA;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 8/25/14
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataInvoice {
    private String Kodenota,Jumlah,Tgl;
    public DataInvoice(String Kodenota, String Jumlah, String Tgl){
        this.Kodenota = Kodenota;
        this.Jumlah = Jumlah;
        this.Tgl = Tgl;
    }

    public String getKodenota() {
        return Kodenota;
    }

    public void setKodenota(String kodenota) {
        Kodenota = kodenota;
    }

    public String getJumlah() {
        return Jumlah;
    }

    public void setJumlah(String jumlah) {
        Jumlah = jumlah;
    }

    public String getTgl() {
        return Tgl;
    }
}
