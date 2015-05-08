package com.bcp.DFA;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 9/17/14
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataGiro {
    private String No;
    private String Nominal;
    private String Tgl;

    public DataGiro(String No,String Nominal, String Tgl){
        this.No = No;
        this.Nominal = Nominal;
        this.Tgl = Tgl;
    }

    public String getNo() {
        return No;
    }

    public String getNominal() {
        return Nominal;
    }

    public String getTgl() {
        return Tgl;
    }
}
