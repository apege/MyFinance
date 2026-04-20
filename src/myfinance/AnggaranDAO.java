package myfinance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnggaranDAO {

    private final long umkmId;

    public AnggaranDAO(long umkmId) {
        this.umkmId = umkmId;
    }

    // ── Model ─────────────────────────────────────────────────────────────────
    public static class AnggaranItem {
        public long   anggaranId;
        public long   kategoriId;
        public String namaKategori;
        public int    bulan;
        public int    tahun;
        public double limitAnggaran;
        public double spent;

        public double pct()        { return limitAnggaran == 0 ? 0 : spent / limitAnggaran * 100; }
        public double sisa()       { return limitAnggaran - spent; }
        public boolean isWarning() { return pct() >= 90; }

        public String getBulanStr() {
            String[] b = {"","Januari","Februari","Maret","April","Mei","Juni",
                          "Juli","Agustus","September","Oktober","November","Desember"};
            return (bulan >= 1 && bulan <= 12) ? b[bulan] + " " + tahun : "-";
        }
    }

    // ── Ambil semua anggaran + spent aktual dari transaksi ────────────────────
    public List<AnggaranItem> getAll() throws SQLException {
        String sql =
            "SELECT a.anggaran_id," +
            "       k.kategori_id," +
            "       k.nama_kategori," +
            "       a.bulan," +
            "       a.tahun," +
            "       a.limit_anggaran," +
            "       COALESCE(SUM(t.jumlah), 0) AS spent" +
            " FROM anggaran a" +
            " JOIN kategori k ON k.kategori_id = a.kategori_id" +
            " LEFT JOIN transaksi t" +
            "   ON t.kategori_id    = a.kategori_id" +
            "   AND t.umkm_id       = a.umkm_id" +
            "   AND MONTH(t.tanggal) = a.bulan" +
            "   AND YEAR(t.tanggal)  = a.tahun" +
            "   AND t.tipe = 'PENGELUARAN'" +
            " WHERE a.umkm_id = ?" +
            " GROUP BY a.anggaran_id, k.kategori_id, k.nama_kategori, a.bulan, a.tahun, a.limit_anggaran" +
            " ORDER BY a.tahun DESC, a.bulan DESC, k.nama_kategori";

        List<AnggaranItem> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setLong(1, umkmId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AnggaranItem item  = new AnggaranItem();
                item.anggaranId    = rs.getLong("anggaran_id");
                item.kategoriId    = rs.getLong("kategori_id");
                item.namaKategori  = rs.getString("nama_kategori");
                item.bulan         = rs.getInt("bulan");
                item.tahun         = rs.getInt("tahun");
                item.limitAnggaran = rs.getDouble("limit_anggaran");
                item.spent         = rs.getDouble("spent");
                list.add(item);
            }
        }
        return list;
    }

    // ── Tambah anggaran baru ──────────────────────────────────────────────────
    public void tambah(long kategoriId, double limit, int bulan, int tahun)
            throws SQLException {
        String sql =
            "INSERT INTO anggaran (umkm_id, kategori_id, bulan, tahun, limit_anggaran)" +
            " VALUES (?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE limit_anggaran = VALUES(limit_anggaran)";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setLong(1, umkmId);
            ps.setLong(2, kategoriId);
            ps.setInt(3, bulan);
            ps.setInt(4, tahun);
            ps.setDouble(5, limit);
            ps.executeUpdate();
        }
    }

    // ── Update anggaran ───────────────────────────────────────────────────────
    public void update(long anggaranId, long kategoriId, double limit, int bulan, int tahun)
            throws SQLException {
        String sql =
            "UPDATE anggaran SET kategori_id=?, bulan=?, tahun=?, limit_anggaran=?" +
            " WHERE anggaran_id=? AND umkm_id=?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setLong(1, kategoriId);
            ps.setInt(2, bulan);
            ps.setInt(3, tahun);
            ps.setDouble(4, limit);
            ps.setLong(5, anggaranId);
            ps.setLong(6, umkmId);
            ps.executeUpdate();
        }
    }

    // ── Hapus anggaran ────────────────────────────────────────────────────────
    public void hapus(long anggaranId) throws SQLException {
        String sql = "DELETE FROM anggaran WHERE anggaran_id=? AND umkm_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setLong(1, anggaranId);
            ps.setLong(2, umkmId);
            ps.executeUpdate();
        }
    }

    // ── Spent per kategori bulan ini (untuk BudgetProgressPanel) ─────────────
    public static class KategoriSpent {
        public String namaKategori;
        public double spent;
        public double limit;
    }

    public List<KategoriSpent> getSpentBulanIni() throws SQLException {
        String sql =
            "SELECT k.nama_kategori," +
            "       COALESCE(SUM(t.jumlah), 0) AS spent," +
            "       a.limit_anggaran" +
            " FROM anggaran a" +
            " JOIN kategori k ON k.kategori_id = a.kategori_id" +
            " LEFT JOIN transaksi t" +
            "   ON t.kategori_id    = a.kategori_id" +
            "   AND t.umkm_id       = a.umkm_id" +
            "   AND MONTH(t.tanggal) = a.bulan" +
            "   AND YEAR(t.tanggal)  = a.tahun" +
            "   AND t.tipe = 'PENGELUARAN'" +
            " WHERE a.umkm_id = ?" +
            "   AND a.bulan = MONTH(CURDATE())" +
            "   AND a.tahun = YEAR(CURDATE())" +
            " GROUP BY k.nama_kategori, a.limit_anggaran" +
            " ORDER BY k.nama_kategori";

        List<KategoriSpent> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setLong(1, umkmId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                KategoriSpent ks = new KategoriSpent();
                ks.namaKategori  = rs.getString("nama_kategori");
                ks.spent         = rs.getDouble("spent");
                ks.limit         = rs.getDouble("limit_anggaran");
                list.add(ks);
            }
        }
        return list;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    public static String bulanToStr(int bulan) {
        String[] b = {"","Januari","Februari","Maret","April","Mei","Juni",
                      "Juli","Agustus","September","Oktober","November","Desember"};
        return (bulan >= 1 && bulan <= 12) ? b[bulan] : "";
    }
}