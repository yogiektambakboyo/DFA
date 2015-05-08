package com.bcp.DFA;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 9/25/14
 * Time: 9:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataMenuPembayaran {
    String ID,Nama,Icon,AssignedImg;
    public DataMenuPembayaran(String ID,String Nama, String Icon,String AssigendImg){
        this.ID = ID;
        this.Nama = Nama;
        this.Icon = Icon;
        this.AssignedImg = AssigendImg;
    }

    public String getAssignedImg() {
        return AssignedImg;
    }

    public String getIcon() {
        return Icon;
    }

    public String getID() {
        return ID;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public void setAssignedImg(String assignedImg) {
        AssignedImg = assignedImg;
    }
}
