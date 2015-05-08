package com.bcp.DFA;

public class DataBarang {
    private String SKU;
    private String Hint;
    private String Keterangan;
    private String Jml;
    private String HrgSatuan;
    private String Assigned;
    private String AssignedImg;
    private String DiscRp;

    public DataBarang(String SKU,String Hint,String Jml, String HrgSatuan,String Assigned, String AssignedImg, String Keterangan, String DiscRp){
        //this.Nama = Nama;
        this.Keterangan = Keterangan;
        this.SKU = SKU;
        this.Hint = Hint;
        this.Jml = Jml;
        this.HrgSatuan = HrgSatuan;
        this.Assigned = Assigned;
        this.AssignedImg = AssignedImg;
        this.DiscRp = DiscRp;
    }

    public String getHint() {
        return Hint;
    }

    public void setHint(String hint) {
        Hint = hint;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getJml() {
        return Jml;
    }

    public void setJml(String jml) {
        Jml = jml;
    }

    public String getHrgSatuan() {
        return HrgSatuan;
    }

    public void setHrgSatuan(String hrgSatuan) {
        HrgSatuan = hrgSatuan;
    }
    public String getKeterangan() {
        return Keterangan;
    }

    public String getAssigned() {
        return Assigned;
    }

    public void setAssigned(String assigned) {
        Assigned = assigned;
    }

    public String getAssignedImg() {
        return AssignedImg;
    }

    public void setAssignedImg(String assignedImg) {
        AssignedImg = assignedImg;
    }

    public String getDiscRp() {
        return DiscRp;
    }
}
