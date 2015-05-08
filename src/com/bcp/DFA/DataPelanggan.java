package com.bcp.DFA;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 8/12/14
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataPelanggan {
    private String ShipTo;
    private String Perusahaan;
    private String Alamat;
    private String Faktur;
    private String Kodenota;
    private String NoPickList;
    private String TotalBayar;
    private String StatusKirim;
    private String KetPem;


    public DataPelanggan(String ShipTo, String Perusahaan, String Alamat, String Faktur, String Kodenota, String TotalBayar, String NoPickList, String StatusKirim,String KetPem){
        this.ShipTo = ShipTo;
        this.Perusahaan = Perusahaan;
        this.Alamat = Alamat;
        this.Faktur = Faktur;
        this.Kodenota = Kodenota;
        this.TotalBayar = TotalBayar;
        this.NoPickList = NoPickList;
        this.StatusKirim = StatusKirim;
        this.KetPem = KetPem;
    }

    public String getShipTo() {
        return ShipTo;
    }

    public String getPerusahaan() {
        return Perusahaan;
    }

    public String getAlamat() {
        return Alamat;
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

    public String getNoPickList() {
        return NoPickList;
    }

    public String getStatusKirim() {
        return StatusKirim;
    }

    public String getKetPem() {
        return KetPem;
    }
}
