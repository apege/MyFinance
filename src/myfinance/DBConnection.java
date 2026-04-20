package myfinance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Singleton connection ke MySQL.
 * Tabel transaksi & anggaran akan dibuat otomatis kalau belum ada.
 *
 * ⚠ SESUAIKAN konfigurasi di bawah:
 *   - USER     : username MySQL lo (default "root")
 *   - PASSWORD : password MySQL lo (default kosong)
 *   - DB_NAME  : nama database (default "myfinance", akan auto-create)
 */
public class DBConnection {

    // ── Konfigurasi ──────────────────────────────────────────────────────────
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DB_NAME  = "db_myfinance";
    private static final String USER     = "root";
    private static final String PASSWORD = "";   // ← ganti kalau ada password

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME +
        "?useSSL=false" +
        "&serverTimezone=Asia/Jakarta" +
        "&allowPublicKeyRetrieval=true" +
        "&createDatabaseIfNotExist=true";

    private static Connection connection;

    /**
     * Ambil singleton connection.
     * Kalau belum ada / sudah closed, buat koneksi baru dan init tabel.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                initTables();
                System.out.println("[DB] Koneksi ke MySQL berhasil.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] Driver tidak ditemukan! " +
                "Pastikan MySQL Connector/J sudah di-add ke Libraries project.\n" + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB] Gagal koneksi ke MySQL: " + e.getMessage());
        }
        return connection;
    }

    /** Buat semua tabel yang dibutuhkan kalau belum ada */
    private static void initTables() throws SQLException {
    // kosong
}

    /** Tutup koneksi (panggil di WindowClosing) */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                System.err.println("[DB] Error closing: " + e.getMessage());
            }
        }
    }
}
