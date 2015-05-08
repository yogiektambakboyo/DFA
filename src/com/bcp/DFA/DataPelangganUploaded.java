package com.bcp.DFA;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 8/12/14
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataPelangganUploaded {
    private String ShipTo;
    private String Perusahaan;

    private String Faktur;
    private String Kodenota;
    private String Keterangan;
    private String TotalBayar;
    private String StatusKirim;


    public DataPelangganUploaded(String ShipTo, String Perusahaan, String Alamat, String Faktur, String Kodenota, String TotalBayar, String StatusKirim, String Keterangan){
        this.ShipTo = ShipTo;
        this.Perusahaan = Perusahaan;
        this.Faktur = Faktur;
        this.Kodenota = Kodenota;
        this.TotalBayar = TotalBayar;
        this.Keterangan = Keterangan;
        this.StatusKirim = StatusKirim;
    }

    public String getShipTo() {
        return ShipTo;
    }

    public String getPerusahaan() {
        return Perusahaan;
    }


    public String getFaktur() {
        return Faktur;
    }

    public String getKodenota() {
        return Kodenota;
    }

    public String getTotalBayar() {
        return TotalBayar;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public String getStatusKirim() {
        return StatusKirim;
    }
}
