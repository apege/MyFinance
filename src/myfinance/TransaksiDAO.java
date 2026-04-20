package myfinance;

import java.sql.*;
import java.util.*;

/**
 * DAO untuk tabel transaksi.
 * Semua method static; panggil langsung TransaksiDAO.getAll(), dst.
 */
public class TransaksiDAO {

    private static final String[] BULAN_NAMES = {
        "Januari","Februari","Maret","April","Mei","Juni",
        "Juli","Agustus","September","Oktober","November","Desember"
    };

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /** Semua transaksi milik umkm tertentu, diurutkan terbaru */
    public static List<Transaksi> getAll(long umkmId) {
        List<Transaksi> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT t.*, k.nama_kategori " +
                "FROM transaksi t " +
                "JOIN kategori k ON k.kategori_id = t.kategori_id " +
                "WHERE t.umkm_id = ? " +
                "ORDER BY t.tanggal DESC, t.transaksi_id DESC")) {
            ps.setLong(1, umkmId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.getAll] " + e.getMessage());
        }
        return list;
    }

    /** N transaksi terbaru milik umkm tertentu */
    public static List<Transaksi> getRecent(long umkmId, int limit) {
        List<Transaksi> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT t.*, k.nama_kategori " +
                "FROM transaksi t " +
                "JOIN kategori k ON k.kategori_id = t.kategori_id " +
                "WHERE t.umkm_id = ? " +
                "ORDER BY t.tanggal DESC, t.transaksi_id DESC " +
                "LIMIT ?")) {
            ps.setLong(1, umkmId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.getRecent] " + e.getMessage());
        }
        return list;
    }

    /** Simpan transaksi baru, transaksi_id di-set otomatis */
    public static boolean insert(Transaksi t) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO transaksi (umkm_id, kategori_id, tanggal, deskripsi, jumlah, tipe)" +
                " VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, t.getUmkmId());
            ps.setLong(2, t.getKategoriId());
            ps.setDate(3, t.getTanggal());
            ps.setString(4, t.getDeskripsi());
            ps.setLong(5, t.getJumlah());
            ps.setString(6, t.getTipe());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) t.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.insert] " + e.getMessage());
            return false;
        }
    }

    /** Hapus transaksi by transaksi_id */
    public static boolean delete(int id) {  // tetap int, sesuai class Transaksi
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM transaksi WHERE transaksi_id = ?")) {  // ganti dari "id"
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.delete] " + e.getMessage());
            return false;
        }
    }

    // ── Aggregates ───────────────────────────────────────────────────────────

    /** Total pemasukan bulan & tahun tertentu (rupiah) */
    public static long getTotalPemasukan(long umkmId, int month, int year) {
        return queryTotal(umkmId, "PEMASUKAN", month, year);
    }

    /** Total pengeluaran bulan & tahun tertentu (rupiah) */
    public static long getTotalPengeluaran(long umkmId, int month, int year) {
        return queryTotal(umkmId, "PENGELUARAN", month, year);
    }

    private static long queryTotal(long umkmId, String tipe, int month, int year) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(SUM(jumlah),0) FROM transaksi" +
                " WHERE umkm_id=? AND tipe=? AND MONTH(tanggal)=? AND YEAR(tanggal)=?")) {
            ps.setLong(1, umkmId);
            ps.setString(2, tipe);
            ps.setInt(3, month);
            ps.setInt(4, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.queryTotal] " + e.getMessage());
        }
        return 0;
    }

    /** Saldo all-time = total pemasukan - total pengeluaran */
    public static long getTotalSaldo(long umkmId) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(SUM(CASE WHEN tipe='PEMASUKAN' THEN jumlah ELSE 0 END),0)" +
                "     - COALESCE(SUM(CASE WHEN tipe='PENGELUARAN' THEN jumlah ELSE 0 END),0)" +
                " FROM transaksi WHERE umkm_id=?")) {
            ps.setLong(1, umkmId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.getTotalSaldo] " + e.getMessage());
        }
        return 0;
    }

    /**
     * Data pemasukan per bulan untuk N bulan terakhir.
     * result[0] = paling lama, result[n-1] = bulan ini.
     */
    public static double[] getMonthlyIncome(long umkmId, int numMonths) {
        return getMonthlyData(umkmId, "PEMASUKAN", numMonths);
    }

    /** Data pengeluaran per bulan untuk N bulan terakhir */
    public static double[] getMonthlyExpense(long umkmId, int numMonths) {
        return getMonthlyData(umkmId, "PENGELUARAN", numMonths);
    }

    private static double[] getMonthlyData(long umkmId, String tipe, int numMonths) {
        double[] result = new double[numMonths];
        Connection conn = DBConnection.getConnection();
        if (conn == null) return result;

        Map<String, Integer> monthMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        for (int i = numMonths - 1; i >= 0; i--) {
            String key = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1);
            monthMap.put(key, i);
            cal.add(Calendar.MONTH, -1);
        }

        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -(numMonths - 1));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        java.sql.Date startDate = new java.sql.Date(cal.getTimeInMillis());

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT MONTH(tanggal) AS m, YEAR(tanggal) AS y, SUM(jumlah) AS total" +
                " FROM transaksi WHERE umkm_id=? AND tipe=? AND tanggal >= ?" +
                " GROUP BY y, m")) {
            ps.setLong(1, umkmId);
            ps.setString(2, tipe);
            ps.setDate(3, startDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getInt("y") + "-" + rs.getInt("m");
                if (monthMap.containsKey(key)) {
                    result[monthMap.get(key)] = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.getMonthlyData] " + e.getMessage());
        }
        return result;
    }

    /**
     * Pengeluaran per kategori bulan & tahun tertentu.
     * Return: LinkedHashMap<nama_kategori, total_rupiah> diurutkan terbesar.
     */
    public static Map<String, Double> getExpenseByCategory(long umkmId, int month, int year) {
        Map<String, Double> map = new LinkedHashMap<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) return map;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT k.nama_kategori AS kategori, SUM(t.jumlah) AS total " +
                "FROM transaksi t " +
                "JOIN kategori k ON k.kategori_id = t.kategori_id " +
                "WHERE t.umkm_id=? AND t.tipe='PENGELUARAN' " +
                "AND MONTH(t.tanggal)=? AND YEAR(t.tanggal)=? " +
                "GROUP BY k.nama_kategori ORDER BY total DESC")) {
            ps.setLong(1, umkmId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getString("kategori"), rs.getDouble("total"));
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.getExpenseByCategory] " + e.getMessage());
        }
        return map;
    }

    /**
     * Pengeluaran kategori tertentu untuk bulan & tahun (untuk anggaran spent).
     */
    public static double getSpentByCategory(long umkmId, long kategoriId, int bulanMonth, int bulanYear) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(SUM(jumlah),0) FROM transaksi" +
                " WHERE umkm_id=? AND tipe='PENGELUARAN' AND kategori_id=?" +
                " AND MONTH(tanggal)=? AND YEAR(tanggal)=?")) {
            ps.setLong(1, umkmId);
            ps.setLong(2, kategoriId);
            ps.setInt(3, bulanMonth);
            ps.setInt(4, bulanYear);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO.getSpentByCategory] " + e.getMessage());
        }
        return 0;
    }

    /**
     * Parse "Januari 2025" → int[]{month=1, year=2025}.
     * Fallback ke bulan sekarang kalau format tidak dikenal.
     */
    public static int[] parseBulanStr(String bulan) {
        for (int i = 0; i < BULAN_NAMES.length; i++) {
            if (bulan != null && bulan.startsWith(BULAN_NAMES[i])) {
                try {
                    int yr = Integer.parseInt(bulan.substring(BULAN_NAMES[i].length()).trim());
                    return new int[]{i + 1, yr};
                } catch (NumberFormatException ignored) {}
            }
        }
        Calendar c = Calendar.getInstance();
        return new int[]{c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)};
    }

    /**
     * Label bulan untuk N bulan terakhir.
     * result[0] = terlama, result[n-1] = bulan ini. Contoh: ["Nov","Des","Jan"...]
     */
    public static String[] getMonthLabels(int numMonths) {
        String[] names = {"Jan","Feb","Mar","Apr","Mei","Jun",
                          "Jul","Agu","Sep","Okt","Nov","Des"};
        String[] labels = new String[numMonths];
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -(numMonths - 1));
        for (int i = 0; i < numMonths; i++) {
            labels[i] = names[cal.get(Calendar.MONTH)];
            cal.add(Calendar.MONTH, 1);
        }
        return labels;
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private static Transaksi mapRow(ResultSet rs) throws SQLException {
    Transaksi t = new Transaksi();
    t.setId(rs.getInt("transaksi_id"));       // ganti dari "id"
    t.setUmkmId(rs.getLong("umkm_id"));       // tambahan baru
    t.setKategoriId(rs.getLong("kategori_id")); // tambahan baru
    t.setKategori(rs.getString("nama_kategori")); // dari JOIN
    t.setTanggal(rs.getDate("tanggal"));
    t.setDeskripsi(rs.getString("deskripsi"));
    t.setJumlah(rs.getLong("jumlah"));
    t.setTipe(rs.getString("tipe"));
    // metode dibiarkan null, kolom tidak ada di DB
    return t;
    }
}

