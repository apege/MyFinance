package myfinance;

import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * DAO untuk autentikasi user.
 * Dipanggil dari LoginPanel saat user login.
 *
 * Schema tabel user yang dibutuhkan (auto-create di DBConnection):
 *   users(id, username, password_hash, nama_lengkap)
 *   umkm(id, user_id, nama_usaha)
 */
public class AuthDAO {

    // ── Model ─────────────────────────────────────────────────────────────────
    public static class LoginResult {
        public boolean sukses;
        public long    userId;
        public long    umkmId;
        public String  namaLengkap;
        public String  namaUsaha;
        public String  pesan;

        /** Hasil gagal */
        static LoginResult gagal(String msg) {
            LoginResult r = new LoginResult();
            r.sukses = false;
            r.pesan  = msg;
            return r;
        }

        /** Hasil sukses */
        static LoginResult ok(long userId, long umkmId, String nama, String usaha) {
            LoginResult r = new LoginResult();
            r.sukses      = true;
            r.userId      = userId;
            r.umkmId      = umkmId;
            r.namaLengkap = nama;
            r.namaUsaha   = usaha;
            return r;
        }
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    /**
     * Coba login dengan username + password.
     * Password di-compare dengan SHA-256 hash yang tersimpan.
     * Kalau kolom password bukan hash (plaintext), juga di-compare langsung sebagai fallback.
     */
    public LoginResult login(String username, String password) throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            // Demo mode kalau DB tidak tersedia
            return demoLogin(username, password);
        }

        String sql =
            "SELECT u.id AS user_id, u.nama_lengkap, u.password_hash," +
            "       k.id AS umkm_id, k.nama_usaha" +
            " FROM users u" +
            " LEFT JOIN umkm k ON k.user_id = u.id" +
            " WHERE u.username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return LoginResult.gagal("Username tidak ditemukan.");
            }

            String storedHash = rs.getString("password_hash");
            // Coba SHA-256 dulu, lalu plaintext fallback
            boolean match = sha256(password).equals(storedHash)
                         || password.equals(storedHash);

            if (!match) return LoginResult.gagal("Password salah.");

            return LoginResult.ok(
                rs.getLong("user_id"),
                rs.getLong("umkm_id"),
                rs.getString("nama_lengkap"),
                rs.getString("nama_usaha")
            );

        } catch (SQLException e) {
            System.err.println("[AuthDAO.login] " + e.getMessage());
            // Fallback ke demo mode kalau tabel belum ada
            return demoLogin(username, password);
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────
    /**
     * Daftarkan user baru + umkm-nya.
     * @return true kalau berhasil
     */
    public boolean register(String username, String password,
                            String namaLengkap, String namaUsaha) throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password_hash, nama_lengkap) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, sha256(password));
            ps.setString(3, namaLengkap);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (!keys.next()) return false;
            long userId = keys.getLong(1);

            try (PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO umkm (user_id, nama_usaha) VALUES (?,?)")) {
                ps2.setLong(1, userId);
                ps2.setString(2, namaUsaha);
                ps2.executeUpdate();
            }
            return true;
        }
    }

    // ── Demo mode (tanpa DB) ──────────────────────────────────────────────────
    /**
     * Login demo: username=admin, password=admin123
     * Dipakai saat DB tidak tersedia atau tabel belum dibuat.
     */
    private LoginResult demoLogin(String username, String password) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            System.out.println("[AuthDAO] Demo mode: masuk sebagai admin");
            return LoginResult.ok(1L, 1L, "Administrator", "MyFinance Demo");
        }
        return LoginResult.gagal("Username atau password salah. (Demo: admin / admin123)");
    }

    // ── Util ──────────────────────────────────────────────────────────────────
    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return input; // fallback plaintext
        }
    }
}
