package myfinance;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Model class untuk data transaksi.
 */
public class Transaksi {

    private int id;
    private Date tanggal;
    private String deskripsi;
    private String kategori;
    private String metode;
    private long jumlah;
    private String tipe; // "pemasukan" atau "pengeluaran"
    private long umkmId;
    private long kategoriId;

    public Transaksi() {}

    public Transaksi(Date tanggal, String deskripsi, String kategori,
                     String metode, long jumlah, String tipe) {
        this.tanggal   = tanggal;
        this.deskripsi = deskripsi;
        this.kategori  = kategori;
        this.metode    = metode;
        this.jumlah    = jumlah;
        this.tipe      = tipe;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }

    public Date getTanggal()            { return tanggal; }
    public void setTanggal(Date t)      { this.tanggal = t; }

    public String getDeskripsi()        { return deskripsi; }
    public void setDeskripsi(String s)  { this.deskripsi = s; }

    public String getKategori()         { return kategori; }
    public void setKategori(String s)   { this.kategori = s; }

    public String getMetode()           { return metode; }
    public void setMetode(String s)     { this.metode = s; }

    public long getJumlah()             { return jumlah; }
    public void setJumlah(long v)       { this.jumlah = v; }

    public String getTipe()             { return tipe; }
    public void setTipe(String s)       { this.tipe = s; }
    
    public long getUmkmId()              { return umkmId; }
    public void setUmkmId(long v)        { this.umkmId = v; }
    public long getKategoriId()          { return kategoriId; }
    public void setKategoriId(long v)    { this.kategoriId = v; }

    /** true kalau tipe = pemasukan */
    public boolean isPemasukan() { return "PEMASUKAN".equalsIgnoreCase(tipe); }

    /** Display format: "13 Jan 2025" */
    public String getTanggalDisplay() {
        if (tanggal == null) return "";
        return new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(tanggal);
    }

    /** Display format pendek: "13 Jan" */
    public String getTanggalShort() {
        if (tanggal == null) return "";
        return new SimpleDateFormat("dd MMM", Locale.ENGLISH).format(tanggal);
    }
}
