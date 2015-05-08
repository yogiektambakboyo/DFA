package com.bcp.DFA;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FN_DBHandler extends SQLiteOpenHelper {

    private final String TAG_KKPDVDATA = "KKPDVData";
    private final String TAG_PELANGANDATA= "PelangganData";
    private final String TAG_INVOICEDATA= "InvoiceData";
    private final String TAG_BARANGDATA= "BarangData";
    private final String TAG_CEKDATA= "CekData";
    private final String TAG_STATUS = "status";

    public FN_DBHandler(Context context, String DB_PATH, String DB_NAME) {
        super(context, DB_PATH+ File.separator +DB_NAME, null, 1);
    }

    // Date n Time

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getToday2(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }



    public String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedTime = df.format(c.getTime());
        return  formattedTime;
    }

    // End

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Setting
    public void CreateSetting(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Pengaturan");
        db.execSQL("CREATE TABLE IF NOT EXISTS Pengaturan "
                + "(lastlogin TEXT,namelogin TEXT,tgllogin DATETIME,tgllogout DATETIME,"
                + "web TEXT,modeapp TEXT,appversion INTEGER, dbversion INTEGER)");
    }

    public void InsertSetting(String WebT, int appversion, int dbversion,String ModeApp){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Pengaturan(lastlogin,namelogin,tgllogin,tgllogout,web,modeapp,appversion,dbversion) VALUES('','',NULL,NULL,'"+WebT+"','"+ModeApp+"',"+appversion+","+dbversion+")");
    }

    public void UpdateSettingAppVersion(String update){
        Integer appversion = Integer.parseInt(update);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET appversion="+appversion);
    }

    public void UpdateSettingDBVersion(String update){
        Integer dbversion = Integer.parseInt(update);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET dbversion="+dbversion);
    }
    public void UpdateSetting(String lastlogin,String namelogin){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET lastlogin='"+lastlogin+"',namelogin='"+namelogin+"',tgllogin=datetime('now', 'localtime')");
    }
    public void UpdateSettingFull(String WebT,String Mode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET web='"+WebT+"',modeapp='"+Mode+"'");
    }
    public JSONObject GetSetting() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT lastlogin,namelogin,web,appversion,dbversion,modeapp FROM Pengaturan";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        if (cursor.moveToFirst()){
            jsonresult.put("status",1);
            jsonresult.put("lastlogin", cursor.getString(cursor.getColumnIndex("lastlogin")));
            jsonresult.put("namelogin", cursor.getString(cursor.getColumnIndex("namelogin")));
            jsonresult.put("web", cursor.getString(cursor.getColumnIndex("web")));
            jsonresult.put("appversion", cursor.getInt(cursor.getColumnIndex("appversion")));
            jsonresult.put("dbversion", cursor.getInt(cursor.getColumnIndex("dbversion")));
            jsonresult.put("modeapp", cursor.getString(cursor.getColumnIndex("modeapp")));
        }
        else{
            jsonresult.put("status",0);
        }

        return jsonresult;
    }

    // End Setting

    //------------------Delivery Master----------------//

    public void CreateMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS DeliveryMaster");
        db.execSQL("DROP TABLE IF EXISTS DeliveryOrder");
        db.execSQL("DROP TABLE IF EXISTS Tolakan");
        db.execSQL("DROP TABLE IF EXISTS CheckerMaster");
        db.execSQL("DROP TABLE IF EXISTS CheckerApproved");
        db.execSQL("CREATE TABLE IF NOT EXISTS CheckerMaster(tgl DATETIME,faktur TEXT,brg TEXT,jml INTEGER,createby TEXT,reasoncode TEXT,nama TEXT,kode TEXT,perusahaan TEXT,keterangan TEXT,rasiomax TEXT,jmlpcs INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS CheckerApproved(faktur TEXT,brg TEXT,jml INTEGER,createby TEXT,reasoncode TEXT,operator TEXT,statust TEXT,statuskirim TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS DeliveryMaster(tgl DATETIME,kodenota TEXT,faktur TEXT,nopicklist TEXT,sopir TEXT,shipto TEXT,perusahaan TEXT,alamat TEXT,brg TEXT,hint TEXT,keterangan TEXT,jml INTEGER,jmlcrt TEXT,hrgsatuan FLOAT,discrp FLOAT,totalbayar FLOAT,tglpicklist DATETIME,rasiomax INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS DeliveryOrder(kodenota TEXT,nopicklist TEXT,faktur TEXT,tgl DATETIME,sopir TEXT,statuskirim INTEGER,tunai FLOAT,bg FLOAT,stempel INTEGER,tt INTEGER,keterangan TEXT,shipto TEXT,perusahaan TEXT,tglupload DATETIME,totalbayar FLOAT,longitude TEXT,latitude TEXT,transferbank TEXT,transferjml FLOAT,startentry DATETIME,finishentry DATETIME,tolakan TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Tolakan(faktur TEXT,brg TEXT,jml INTEGER,alasan TEXT)");
    }

    public void CekMasterTolakan(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS Tolakan(faktur TEXT,brg TEXT,jml INTEGER,alasan TEXT)");
    }

    public void CekCheckerMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS CheckerMaster(tgl DATETIME,faktur TEXT,brg TEXT,jml INTEGER,createby TEXT,reasoncode TEXT,nama TEXT,kode TEXT,perusahaan TEXT,keterangan TEXT,rasiomax TEXT,jmlpcs INTEGER)");
    }

    public void CekCheckerApproved(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS CheckerApproved(tgl DATETIME,faktur TEXT,brg TEXT,jml INTEGER,createby TEXT,reasoncode TEXT,operator TEXT,statust TEXT,statuskirim TEXT)");
    }

    public String GetCountTolakan(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select count(faktur) as jml from Tolakan where faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        String jml;
        jml = "0";

        if(cursor.moveToFirst()){
            jml = cursor.getString(cursor.getColumnIndex("jml"));
        }else{
            jml = "N/A";
        }
        return jml;
    }

    public JSONObject GetTolakanVSMaster(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT brg,hint,jml,hrgsatuan,keterangan,discrp,rasiomax,jmlcrt,'0' as jmlpcs from DeliveryMaster where faktur='"+Faktur+"' and brg not in (select brg from Tolakan where faktur='"+Faktur+"') union all select t.brg,m.hint,m.jml,m.hrgsatuan,m.keterangan,m.discrp,m.rasiomax,m.jmlcrt,t.jml as jmlpcs from Tolakan t join DeliveryMaster m on m.faktur=t.faktur and m.brg=t.brg where t.faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("brg",cursor.getString(cursor.getColumnIndex("brg")));
                JData.put("hint",cursor.getString(cursor.getColumnIndex("hint")));
                JData.put("jml",cursor.getString(cursor.getColumnIndex("jml")));
                JData.put("hrgsatuan",cursor.getString(cursor.getColumnIndex("hrgsatuan")));
                JData.put("keterangan",cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("discrp",cursor.getString(cursor.getColumnIndex("discrp")));
                JData.put("rasiomax",cursor.getString(cursor.getColumnIndex("rasiomax")));
                JData.put("jmlcrt",cursor.getString(cursor.getColumnIndex("jmlcrt")));
                JData.put("jmlpcs", cursor.getString(cursor.getColumnIndex("jmlpcs")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public String GetResumeTolakan(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select faktur,brg,jml,alasan from Tolakan where faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        String ResumeTolakan = "";

        if(cursor.moveToFirst()){
            do {
                ResumeTolakan = ResumeTolakan + cursor.getString(cursor.getColumnIndex("brg")) +"&"+ cursor.getString(cursor.getColumnIndex("jml")) +"&"+ cursor.getString(cursor.getColumnIndex("alasan")) +"#";
            }while (cursor.moveToNext());
        }else{
            ResumeTolakan = "";
        }
        return ResumeTolakan;
    }

    public void DeleteTolakan(String Faktur){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Tolakan WHERE faktur='"+Faktur+"'");
    }

    public void DeleteTolakanEdit(String Faktur){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Tolakan WHERE faktur='"+Faktur+"' and faktur not in (select faktur from DeliveryOrder)");
    }

    public void DeleteTolakanClear(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Tolakan WHERE faktur not in (select faktur from DeliveryOrder)");
    }

    public void InsertTolakan(String Faktur,String Barang, Integer Jml,String Alasan){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Tolakan(faktur,brg,jml,alasan) VALUES('"+Faktur+"','"+Barang+"',"+Jml+",'"+Alasan+"')");
    }

    public void InsertTolakanFull(String Faktur,String Alasan){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Tolakan(faktur,brg,jml,alasan) select faktur,brg,jml,'"+Alasan+"' from DeliveryMaster where faktur='"+Faktur+"'");
    }

    public void DeleteDeliveryMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM DeliveryMaster");
    }

    public void InsertDeliveryMaster(String Tgl,String Kodenota,String Sopir,String Faktur,String ShipTo,String Perusahaan,String Alamat,String Brg,String Hint,Integer Jml,Float HrgSatuan,String NoPickList, String JmlCRT, Float DiscRp, Float TotalBayar, String Keterangan,String TglPickList,Integer RasioMax){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO DeliveryMaster(tgl,kodenota,sopir,faktur,shipto,perusahaan,alamat,brg,hint,jml,hrgsatuan,nopicklist,jmlcrt,discrp,totalbayar,keterangan,tglpicklist,rasiomax) VALUES('"+Tgl+"','"+Kodenota+"','"+Sopir+"','"+Faktur+"','"+ShipTo+"','"+Perusahaan+"','"+Alamat+"','"+Brg+"','"+Hint+"',"+Jml+","+HrgSatuan+",'"+NoPickList+"','"+JmlCRT+"',"+DiscRp+","+TotalBayar+",'"+Keterangan+"','"+TglPickList+"',"+RasioMax+")");
    }


    public JSONObject GetPelanggan(String Sopir) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT DISTINCT m.kodenota,m.shipto,m.perusahaan,m.alamat,m.faktur,m.totalbayar,m.nopicklist,IFNULL(d.statuskirim,0) as statuskirim,IFNULL(d.tunai,'0') as tunai,IFNULL(d.bg,'0') as bg,IFNULL(d.stempel,'0') as stempel,IFNULL(d.tt,'0') as tt,IFNULL(d.transferjml,'0') as transfer FROM DeliveryMaster m left join DeliveryOrder d on d.kodenota = m.kodenota and d.faktur = m.faktur ORDER BY m.tgl,m.perusahaan,m.kodenota";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                JData.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                JData.put("faktur", cursor.getString(cursor.getColumnIndex("faktur")));
                JData.put("totalbayar", cursor.getString(cursor.getColumnIndex("totalbayar")));
                JData.put("nopicklist", cursor.getString(cursor.getColumnIndex("nopicklist")));
                JData.put("statuskirim", cursor.getString(cursor.getColumnIndex("statuskirim")));
                JData.put("tunai",cursor.getString(cursor.getColumnIndex("tunai")));
                JData.put("bg",cursor.getString(cursor.getColumnIndex("bg")));
                JData.put("stempel",cursor.getString(cursor.getColumnIndex("stempel")));
                JData.put("tt",cursor.getString(cursor.getColumnIndex("tt")));
                JData.put("transfer",cursor.getString(cursor.getColumnIndex("transfer")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetPelangganUploaded(String Sopir) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT DISTINCT kodenota,nopicklist,faktur,tgl,sopir,statuskirim,tunai,bg,stempel,tt,keterangan,shipto,perusahaan,tglupload,totalbayar,transferbank,transferjml,tolakan FROM DeliveryOrder where tglupload='"+getToday2()+"' and statuskirim=2 ORDER BY perusahaan,kodenota";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                JData.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("faktur", cursor.getString(cursor.getColumnIndex("faktur")));
                JData.put("totalbayar", cursor.getString(cursor.getColumnIndex("totalbayar")));
                JData.put("nopicklist", cursor.getString(cursor.getColumnIndex("nopicklist")));
                JData.put("statuskirim", cursor.getString(cursor.getColumnIndex("statuskirim")));
                JData.put("tunai",cursor.getString(cursor.getColumnIndex("tunai")));
                JData.put("bg",cursor.getString(cursor.getColumnIndex("bg")));
                JData.put("stempel",cursor.getString(cursor.getColumnIndex("stempel")));
                JData.put("tt",cursor.getString(cursor.getColumnIndex("tt")));
                JData.put("tgl",cursor.getString(cursor.getColumnIndex("tgl")));
                JData.put("tglupload",cursor.getString(cursor.getColumnIndex("tglupload")));
                JData.put("keterangan",cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("transferbank",cursor.getString(cursor.getColumnIndex("transferbank")));
                JData.put("transferjml",cursor.getString(cursor.getColumnIndex("transferjml")));
                JData.put("tolakan",cursor.getString(cursor.getColumnIndex("tolakan")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetKKPDV() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT DISTINCT kodenota FROM DeliveryMaster";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_KKPDVDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetKKPDVInfo() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select count(a.Kodenota) as KKPDV," +
                " al.KKPDVRp as KKPDVRp," +
                " b.jumFaktur as Faktur," +
                " c.jumFakturFin as FakturFin," +
                " d.jumFakturRp as FakturRp," +
                " e.jumFakturTun as FakturFinTun," +
                " f.jumFakturTunRp as FakturTunRp," +
                " g.jumFakturBg as FakturFinBg," +
                " h.jumFakturBgRp as FakturBgRp," +
                " i.jumFakturTunBg as FakturTunBg," +
                " j.jumFakturTunBgRp as FakturTunBgRp," +
                " k.jumFakturTT as FakturTT," +
                " l.jumFakturStempel as FakturStempel," +
                " m.jumFakturTakTerkirim as FakturTakKirim," +
                " n.jumFakturTrn as FakturTrn,"+
                " o.jumFakturTrnRp as FakturTrnRp,"+
                " p.tolakan as FakturTlk"+
                " from" +
                "(select distinct(kodenota) as Kodenota from DeliveryMaster) a," +
                "(select SUM(totalbayar) as KKPDVRp from (select totalbayar from deliveryMaster group by faktur)) al,"+
                "(select count(distinct faktur) as jumFaktur from DeliveryMaster) b," +
                "(select count(faktur) as jumFakturFin from DeliveryOrder) c," +
                "(select sum(tunai)+sum(bg)+sum(transferjml) as jumFakturRp from DeliveryOrder) d," +
                "(select count(faktur) as jumFakturTun from DeliveryOrder where tunai>1) e," +
                "(select sum(tunai) as jumFakturTunRp from DeliveryOrder where tunai>1) f," +
                "(select count(faktur) as jumFakturBg from DeliveryOrder where bg>1) g," +
                "(select sum(bg) as jumFakturBgRp from DeliveryOrder where bg>1) h," +
                "(select count(faktur) as jumFakturTunBg from DeliveryOrder where tunai>1 and bg>1) i," +
                "(select sum(tunai)+sum(bg) as jumFakturTunBgRp from DeliveryOrder where tunai>1 and bg>1) j," +
                "(select count(faktur) as jumFakturTT from DeliveryOrder where tt=1) k," +
                "(select count(faktur) as jumFakturStempel from DeliveryOrder where stempel=1) l,"+
                "(select count(faktur) as jumFakturTakTerkirim from DeliveryOrder where stempel=0 and tt=0 and bg=0 and tunai=0 and transferjml=0 and length(tolakan)<1) m,"+
                "(select count(faktur) as jumFakturTrn from DeliveryOrder where transferjml>1) n," +
                "(select sum(transferjml) as jumFakturTrnRp from DeliveryOrder where transferjml>1) o," +
                "(select count(distinct faktur) as tolakan from Tolakan) p" ;
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        //JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
                jResult.put("kkpdv", cursor.getString(cursor.getColumnIndex("KKPDV")));
                jResult.put("kkpdvrp", Float.toString(cursor.getFloat(cursor.getColumnIndex("KKPDVRp"))));
                jResult.put("faktur", cursor.getString(cursor.getColumnIndex("Faktur")));
                jResult.put("fakturfin", cursor.getString(cursor.getColumnIndex("FakturFin")));
                jResult.put("fakturrp", Float.toString(cursor.getFloat(cursor.getColumnIndex("FakturRp"))));
                jResult.put("fakturfintun", cursor.getString(cursor.getColumnIndex("FakturFinTun")));
                jResult.put("fakturtunrp", Float.toString(cursor.getFloat(cursor.getColumnIndex("FakturTunRp"))));
                jResult.put("fakturfinbg", cursor.getString(cursor.getColumnIndex("FakturFinBg")));
                jResult.put("fakturbgrp", Float.toString(cursor.getFloat(cursor.getColumnIndex("FakturBgRp"))));
                jResult.put("fakturtunbg", cursor.getString(cursor.getColumnIndex("FakturTunBg")));
                jResult.put("fakturtunbgrp", Float.toString(cursor.getFloat(cursor.getColumnIndex("FakturTunBgRp"))));
                jResult.put("fakturtt", cursor.getString(cursor.getColumnIndex("FakturTT")));
                jResult.put("fakturstempel", cursor.getString(cursor.getColumnIndex("FakturStempel")));
                jResult.put("fakturtakkirim", cursor.getString(cursor.getColumnIndex("FakturTakKirim")));
                jResult.put("fakturtrn", cursor.getString(cursor.getColumnIndex("FakturTrn")));
                jResult.put("fakturtrnrp", Float.toString(cursor.getFloat(cursor.getColumnIndex("FakturTrnRp"))));
                jResult.put("fakturtlk", cursor.getString(cursor.getColumnIndex("FakturTlk")));
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetOneKKPDVInfo(String KKPDV) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select a.faktur as Faktur,b.faktur as FakturTerproses from" +
                "(select count(distinct faktur) as faktur from DeliveryMaster where kodenota='"+KKPDV+"') a," +
                "(select count(distinct faktur) as faktur from DeliveryOrder where kodenota='"+KKPDV+"') b";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();

        if(cursor.moveToFirst()){
            jResult.put("faktur", cursor.getString(cursor.getColumnIndex("Faktur")));
            jResult.put("fakturterproses", cursor.getString(cursor.getColumnIndex("FakturTerproses")));
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }



    public JSONObject GetKKPDVUploaded() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT DISTINCT kodenota FROM DeliveryOrder";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_KKPDVDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetInvoice() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select distinct kodenota,tgl,count(distinct faktur) as jumlah from DeliveryMaster group by kodenota,tgl";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                JData.put("jumlah", cursor.getString(cursor.getColumnIndex("jumlah")));
                JData.put("tglpicklist", cursor.getString(cursor.getColumnIndex("tgl")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_INVOICEDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetPickList() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select distinct nopicklist,tglpicklist,count(distinct faktur) as jumlah from DeliveryMaster group by nopicklist,tglpicklist";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("nopicklist", cursor.getString(cursor.getColumnIndex("nopicklist")));
                JData.put("jumlah", cursor.getString(cursor.getColumnIndex("jumlah")));
                JData.put("tglpicklist", cursor.getString(cursor.getColumnIndex("tglpicklist")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_INVOICEDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }


    public JSONObject GetBarang(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT brg,hint,jml,hrgsatuan,keterangan,discrp,rasiomax,jmlcrt from DeliveryMaster where faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult= new JSONObject();
        JSONArray  jArray = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject jData = new JSONObject();
                jData.put("brg",cursor.getString(cursor.getColumnIndex("brg")));
                jData.put("hint",cursor.getString(cursor.getColumnIndex("hint")));
                jData.put("jml",cursor.getString(cursor.getColumnIndex("jml")));
                jData.put("hrgsatuan",cursor.getString(cursor.getColumnIndex("hrgsatuan")));
                jData.put("keterangan",cursor.getString(cursor.getColumnIndex("keterangan")));
                jData.put("discrp",cursor.getString(cursor.getColumnIndex("discrp")));
                jData.put("rasiomax",cursor.getString(cursor.getColumnIndex("rasiomax")));
                jData.put("jmlcrt",cursor.getString(cursor.getColumnIndex("jmlcrt")));
                jArray.put(jData);
            }while(cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public int GetCountBarang(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT count(brg) as jumlah from DeliveryMaster where faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);
        int Result=0;
        if(cursor.moveToFirst()){
            do {
                Result = cursor.getInt(cursor.getColumnIndex("jumlah"));
            }while(cursor.moveToNext());
        }else{
            Result=0;
        }
        return Result;
    }

    // End Delivery Master

    // Delivery Order

    public Cursor getPelangganRAW(String Sopir){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kodenota,nopicklist,faktur,tgl,sopir,tunai,bg,stempel,tt,keterangan,statuskirim,longitude,latitude,transferbank,transferjml,startentry,finishentry,tolakan FROM DeliveryOrder where sopir='"+Sopir+"' and statuskirim<>2 and tglupload=''";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public Cursor getTolakanRAW(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT faktur,brg,jml,createby,reasoncode,operator,statust FROM CheckerApproved where statuskirim=1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public String cekDeliveryUpload(String Sopir){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT count(*) as jumlah FROM DeliveryOrder where sopir='"+Sopir+"' and statuskirim=1 and tglupload=''";
        Cursor cursor = db.rawQuery(sql,null);

        String Jumlah = "0";

        if(cursor.moveToFirst()){
            do {
                Jumlah = cursor.getString(cursor.getColumnIndex("jumlah"));
            }while(cursor.moveToNext());
        }else{
            Jumlah ="0";
        }
        return Jumlah;
    }

    public String cekCheckerUpload(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT count(*) as jumlah FROM CheckerApproved where statuskirim=1";
        Cursor cursor = db.rawQuery(sql,null);

        String Jumlah = "0";

        if(cursor.moveToFirst()){
            do {
                Jumlah = cursor.getString(cursor.getColumnIndex("jumlah"));
            }while(cursor.moveToNext());
        }else{
            Jumlah ="0";
        }
        return Jumlah;
    }


    public void InsertDeliveryOrder(String Kodenota,String NoPickList,String Faktur,String Tgl,String Sopir,int StatusKirim,Float tunai,Float bg,int stempel,int tt,String Keterangan,String ShipTo,String Perusahaan,String TglUpload,Float TotalBayar, String Longitude, String Latitude, String TransferBank,Float TransferJml,String StartEntry, String FinishEntry, String Tolakan){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO DeliveryOrder(kodenota,nopicklist,faktur,tgl,sopir,statuskirim,tunai,bg,stempel,tt,keterangan,shipto,perusahaan,tglupload,totalbayar,longitude,latitude,transferbank,transferjml,startentry,finishentry,tolakan) VALUES('"+Kodenota+"','"+NoPickList+"','"+Faktur+"','"+Tgl+"','"+Sopir+"',"+StatusKirim+","+tunai+","+bg+","+stempel+","+tt+",'"+Keterangan+"','"+ShipTo+"','"+Perusahaan+"','"+TglUpload+"',"+TotalBayar+",'"+Longitude+"','"+Latitude+"','"+TransferBank+"',"+TransferJml+",'"+StartEntry+"','"+FinishEntry+"','"+Tolakan+"')");
    }

    public void DeleteDeliveryOrder(String Kodenota,String NoPickList,String Faktur,String Sopir){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM DeliveryOrder WHERE kodenota='"+Kodenota+"' and  nopicklist='"+NoPickList+"' and faktur='"+Faktur+"' and sopir='"+Sopir+"'");
    }

    public void DeleteAllDeliveryOrder(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM DeliveryOrder");
    }

    public void UpdateDeliveryOrderUpload(String Sopir,String Today){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE DeliveryOrder SET statuskirim=2,tglupload='"+Today+"' WHERE sopir='"+Sopir+"'");
    }

    public JSONObject CekDeliveryOrder(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT count(faktur) as jumlah,IFNULL(tunai,'0') as tunai,IFNULL(bg,'0') as bg,IFNULL(stempel,'0') as stempel,IFNULL(tt,'0') as tt,IFNULL(statuskirim,'0') as statuskirim,IFNULL(keterangan,'') as keterangan,IFNULL(transferjml,'0') as transferjml,IFNULL(transferbank,' ') as transferbank,IFNULL(tolakan,'') as tolakan from DeliveryOrder where faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult= new JSONObject();
        JSONArray  jArray = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject jData = new JSONObject();
                jData.put("jumlah",cursor.getString(cursor.getColumnIndex("jumlah")));
                jData.put("tunai",cursor.getString(cursor.getColumnIndex("tunai")));
                jData.put("bg",cursor.getString(cursor.getColumnIndex("bg")));
                jData.put("stempel",cursor.getString(cursor.getColumnIndex("stempel")));
                jData.put("tt",cursor.getString(cursor.getColumnIndex("tt")));
                jData.put("statuskirim",cursor.getString(cursor.getColumnIndex("statuskirim")));
                jData.put("keterangan",cursor.getString(cursor.getColumnIndex("keterangan")));
                jData.put("transferjml",cursor.getString(cursor.getColumnIndex("transferjml")));
                jData.put("transferbank",cursor.getString(cursor.getColumnIndex("transferbank")));
                jData.put("tolakan",cursor.getString(cursor.getColumnIndex("tolakan")));
                jArray.put(jData);
            }while(cursor.moveToNext());
            jResult.put(TAG_CEKDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetOneKKPDVInfoSuccess(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select a.tunai,b.bg,c.transfer,d.tolakan from" +
                "(select sum(tunai) as tunai from DeliveryOrder where faktur='"+Faktur+"') a," +
                "(select sum(bg) as bg from DeliveryOrder where faktur='"+Faktur+"') b," +
                "(select sum(transferjml) as transfer from DeliveryOrder where faktur='"+Faktur+"') c," +
                "(select count(faktur) as tolakan from Tolakan where faktur='"+Faktur+"') d";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();

        if(cursor.moveToFirst()){
            jResult.put("tunai", cursor.getString(cursor.getColumnIndex("tunai")));
            jResult.put("bg", cursor.getString(cursor.getColumnIndex("bg")));
            jResult.put("transfer", cursor.getString(cursor.getColumnIndex("transfer")));
            jResult.put("tolakan", cursor.getString(cursor.getColumnIndex("tolakan")));
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    // end Delivery Order

    public String cekColumnExist(String TabelName,String ColumnName){
        String result = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "PRAGMA table_info("+TabelName+")";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            do {
                if ((cursor.getString(cursor.getColumnIndex("name"))).equals(ColumnName)){
                    result="1";
                }
            } while (cursor.moveToNext());
        }
        else{
            result = "0";
        }
        return result;
    }

    public void addColumn(String TabelName,String ColumnName,String TipeData){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE "+TabelName+" ADD "+ColumnName+" "+TipeData);
    }

    public void InsertCheckerMaster(String Tgl,String Faktur,String Brg,Integer Jml,String CreateBy,String ReasonCode,String Nama,String Kode,String Perusahaan,String Keterangan,String RasioMax,Integer JmlPCS){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO CheckerMaster(tgl,faktur,brg,jml,createby,reasoncode,nama,kode,perusahaan,keterangan,rasiomax,jmlpcs) VALUES('"+Tgl+"','"+Faktur+"','"+Brg+"',"+Jml+",'"+CreateBy+"','"+ReasonCode+"','"+Nama+"','"+Kode+"','"+Perusahaan+"','"+Keterangan+"','"+RasioMax+"',"+JmlPCS+")");
    }

    public void DeleteCheckerMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CheckerMaster");
    }

   /* public void UpdateCheckerMaster(String Faktur,String SKU, Integer JmlPCS,String CreateBy,String ReasonCode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CheckerMaster SET jmlpcs="+JmlPCS+",createby='"+CreateBy+"',reasoncode='"+ReasonCode+"' WHERE faktur='"+Faktur+"' and brg='"+SKU+"'");
    }

    public void ResetUpdateCheckerMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CheckerMaster SET jmlpcs=0,createby='',reasoncode='' WHERE LENGTH(createby)<>6 and faktur not in (select faktur from CheckerApproved) ");
    }*/

    public JSONObject GetPelangganChecker() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT m.tgl,m.faktur,m.createby,m.reasoncode,m.nama,IFNULL(a.statuskirim,'0') as statuskirim,m.kode,m.perusahaan,count(m.brg) as sku FROM CheckerMaster m left join CheckerApproved a on a.faktur=m.faktur and a.brg=m.brg group by m.tgl,m.faktur,m.createby,m.reasoncode,m.nama,m.kode,m.perusahaan";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("tgl", cursor.getString(cursor.getColumnIndex("tgl")));
                JData.put("faktur", cursor.getString(cursor.getColumnIndex("faktur")));
                JData.put("createby", cursor.getString(cursor.getColumnIndex("createby")));
                JData.put("reasoncode", cursor.getString(cursor.getColumnIndex("reasoncode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                JData.put("statuskirim", cursor.getString(cursor.getColumnIndex("statuskirim")));
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("sku", cursor.getString(cursor.getColumnIndex("sku")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetPelangganCheckerUpload() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT m.tgl,m.faktur,m.createby,m.reasoncode,m.nama,IFNULL(a.statust,'0') as statuskirim,m.kode,m.perusahaan,count(m.brg) as sku FROM CheckerMaster m left join CheckerApproved a on a.faktur=m.faktur and a.brg=m.brg where a.statuskirim=2 group by m.tgl,m.faktur,m.createby,m.reasoncode,m.nama,m.kode,m.perusahaan";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("tgl", cursor.getString(cursor.getColumnIndex("tgl")));
                JData.put("faktur", cursor.getString(cursor.getColumnIndex("faktur")));
                JData.put("createby", cursor.getString(cursor.getColumnIndex("createby")));
                JData.put("reasoncode", cursor.getString(cursor.getColumnIndex("reasoncode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                JData.put("statuskirim", cursor.getString(cursor.getColumnIndex("statuskirim")));
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("sku", cursor.getString(cursor.getColumnIndex("sku")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetSopirTolakan() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT DISTINCT createBy,nama FROM CheckerMaster where LENGTH(createBy)=6";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("createby", cursor.getString(cursor.getColumnIndex("createby")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_KKPDVDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetTolakanCheckerVSMaster(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select  m.brg,m.faktur,m.jml,m.keterangan,m.rasiomax,a.jml as jmlpcs,a.createby,a.reasoncode from checkerapproved a join checkermaster m on m.faktur=a.faktur and m.brg=a.brg where a.faktur='"+Faktur+"'" +
                " union all " +
                "select   brg,faktur,jml,keterangan,rasiomax,jmlpcs,createby,reasoncode from checkermaster where  faktur='"+Faktur+"' and brg not in (select brg from CheckerApproved where faktur='"+Faktur+"') and jmlpcs>0";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("brg",cursor.getString(cursor.getColumnIndex("brg")));
                JData.put("faktur",cursor.getString(cursor.getColumnIndex("faktur")));
                JData.put("jml",cursor.getString(cursor.getColumnIndex("jml")));
                JData.put("jmlpcs",cursor.getString(cursor.getColumnIndex("jmlpcs")));
                JData.put("keterangan",cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("rasiomax",cursor.getString(cursor.getColumnIndex("rasiomax")));
                JData.put("createby",cursor.getString(cursor.getColumnIndex("createby")));
                JData.put("reasoncode",cursor.getString(cursor.getColumnIndex("reasoncode")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetTolakanTambahBarang(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT brg,faktur,jml,keterangan,rasiomax,jmlpcs from CheckerMaster where faktur='"+Faktur+"' and jmlpcs<=0 and jml>0";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("brg",cursor.getString(cursor.getColumnIndex("brg")));
                JData.put("faktur",cursor.getString(cursor.getColumnIndex("faktur")));
                JData.put("jml",cursor.getString(cursor.getColumnIndex("jml")));
                JData.put("jmlpcs",cursor.getString(cursor.getColumnIndex("jmlpcs")));
                JData.put("keterangan",cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("rasiomax",cursor.getString(cursor.getColumnIndex("rasiomax")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public void InsertCheckerApproved(String Faktur,String Brg,Integer Jml,String CreateBy,String ReasonCode,String Operator,String StatusT,String StatusKirim){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO CheckerApproved(faktur,brg,jml,createby,reasoncode,operator,statust,statuskirim) VALUES('"+Faktur+"','"+Brg+"',"+Jml+",'"+CreateBy+"','"+ReasonCode+"','"+Operator+"','"+StatusT+"','"+StatusKirim+"')");
    }

    public void DeleteCheckerApproved(String Faktur){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CheckerApproved where faktur='"+Faktur+"'");
    }

    public void DeleteAllCheckerApproved(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CheckerApproved");
    }

    public void UpdateCheckerApprovedStatusKirim(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CheckerApproved set statuskirim='2' where statuskirim<>2");
    }

    public int GetCountTolakanEdit(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT count(brg) as jml from CheckerApproved where faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        int jml = 0;

        if(cursor.moveToFirst()){
            jml =  cursor.getInt(cursor.getColumnIndex("jml"));
        }else{
            jml = 0;
        }
        return jml;
    }

    public float GetTotalBayar(String Faktur,int Mode) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "";
        if(Mode==0){
            sql="select distinct(totalbayar) as jml from deliverymaster where faktur='"+Faktur+"'";
        }else{
            sql="select (sum(t.jml*(m.hrgsatuan-m.discrp))*1.1) as jml from Tolakan t join DeliveryMaster m on m.faktur=t.faktur and m.brg=t.brg where t.faktur='"+Faktur+"'";
        }
        Cursor cursor = db.rawQuery(sql,null);

        float jml = 0.0f;

        if(cursor.moveToFirst()){
            do {
                jml =  cursor.getFloat(cursor.getColumnIndex("jml"));
            }while (cursor.moveToNext());
        }else{
            jml = 0.0f;
        }
        return jml;
    }

    public String GetStatusTolakanEdit(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT DISTINCT statust from CheckerApproved where faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        String Status = "Valid";

        if(cursor.moveToFirst()){
            do {
                String StatusT =  cursor.getString(cursor.getColumnIndex("statust"));
                if (StatusT.equals("1")){
                    Status = "Valid";
                }else{
                    Status = "Tidak Valid";
                }
            }while (cursor.moveToNext());
        }else{
            Status = "Tidak Valid";
        }
        return Status;
    }


    public JSONObject GetBarangTolakan(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select CASE WHEN IFNULL(m.keterangan,'')='' THEN t.brg ELSE m.keterangan END as keterangan,t.jml from Tolakan  t left join DeliveryMaster m on m.faktur=t.faktur and m.brg=t.brg where t.faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("jml",cursor.getString(cursor.getColumnIndex("jml")));
                JData.put("keterangan",cursor.getString(cursor.getColumnIndex("keterangan")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }

    public JSONObject GetBarangTolakanUpload(String Faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select CASE WHEN IFNULL(m.keterangan,'')='' THEN t.brg ELSE m.keterangan END as keterangan,t.jml,t.statust from CheckerApproved  t left join CheckerMaster m on m.faktur=t.faktur and m.brg=t.brg where t.faktur='"+Faktur+"'";
        Cursor cursor = db.rawQuery(sql,null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if(cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("jml",cursor.getString(cursor.getColumnIndex("jml")));
                JData.put("keterangan",cursor.getString(cursor.getColumnIndex("keterangan")));
                jArray.put(JData);
            }while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }else{
            jResult.put(TAG_STATUS,0);
        }
        return jResult;
    }


}
