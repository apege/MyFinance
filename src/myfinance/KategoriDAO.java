package myfinance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel kategori.
 * Menyimpan daftar kategori pengeluaran/pemasukan per UMKM.
 */
public class KategoriDAO {

    private final long umkmId;

    public KategoriDAO(long umkmId) {
        this.umkmId = umkmId;
    }

    // ── Model ─────────────────────────────────────────────────────────────────
    public static class Kategori {
        public long   kategoriId;
        public String namaKategori;
        public String tipe;  // 'PEMASUKAN', 'PENGELUARAN', 'SEMUA'

        public Kategori(long id, String nama, String tipe) {
            this.kategoriId    = id;
            this.namaKategori  = nama;
            this.tipe          = tipe;
        }
    }

    // ── Ambil semua kategori pengeluaran ──────────────────────────────────────
    public List<Kategori> getPengeluaran() throws SQLException {
        List<Kategori> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return getDefault();   // fallback kalau DB tidak tersedia

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT kategori_id, nama_kategori, tipe FROM kategori" +
                " WHERE (umkm_id = ? OR umkm_id IS NULL)" +
                " AND tipe IN ('PENGELUARAN','SEMUA')" +
                " ORDER BY nama_kategori")) {
            ps.setLong(1, umkmId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Kategori(
                    rs.getLong("kategori_id"),
                    rs.getString("nama_kategori"),
                    rs.getString("tipe")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[KategoriDAO.getPengeluaran] " + e.getMessage());
            return getDefault();
        }

        // Kalau tabel kosong, pakai default
        return list.isEmpty() ? getDefault() : list;
    }

    /** Ambil semua kategori (pemasukan + pengeluaran) */
    public List<Kategori> getAll() throws SQLException {
        List<Kategori> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return getDefault();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT kategori_id, nama_kategori, tipe FROM kategori" +
                " WHERE umkm_id = ? OR umkm_id IS NULL" +
                " ORDER BY jenis_kategori, nama_kategori")) {
            ps.setLong(1, umkmId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Kategori(
                    rs.getLong("kategori_id"),
                    rs.getString("nama_kategori"),
                    rs.getString("tipe")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[KategoriDAO.getAll] " + e.getMessage());
            return getDefault();
        }
        return list.isEmpty() ? getDefault() : list;
    }

    /** Tambah kategori baru */
    public long tambah(String namaKategori, String tipe) throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return -1;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO kategori (umkm_id, nama_kategori, tipe) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, umkmId);
            ps.setString(2, namaKategori);
            ps.setString(3, tipe);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getLong(1) : -1;
        }
    }

    /**
     * Fallback: kembalikan kategori default kalau tabel belum ada / DB tidak connect.
     * ID = -1 menandakan data belum dari DB.
     */
    private List<Kategori> getDefault() {
        String[] defaults = {
            "Makanan & Minuman", "Transport", "Belanja",
            "Tagihan", "Hiburan", "Kesehatan", "Lainnya"
        };
        List<Kategori> list = new ArrayList<>();
        for (int i = 0; i < defaults.length; i++) {
            list.add(new Kategori(-(i + 1), defaults[i], "PENGELUARAN"));
        }
        return list;
    }
}
