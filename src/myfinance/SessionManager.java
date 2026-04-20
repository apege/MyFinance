package myfinance;

public class SessionManager {

    private static long   userId;
    private static long   umkmId;
    private static String namaLengkap;
    private static String namaUsaha;
    private static boolean loggedIn = false;

    // Dipanggil saat login berhasil (dari LoginPanel)
    public static void login(long userId, long umkmId, String namaLengkap, String namaUsaha) {
        SessionManager.userId      = userId;
        SessionManager.umkmId      = umkmId;
        SessionManager.namaLengkap = namaLengkap;
        SessionManager.namaUsaha   = namaUsaha;
        SessionManager.loggedIn    = true;
    }

    public static void logout() {
        userId = 0; umkmId = 0;
        namaLengkap = null; namaUsaha = null;
        loggedIn = false;
    }

    public static long   getUserId()     { return userId; }
    public static long   getUmkmId()     { return umkmId; }
    public static String getNamaLengkap(){ return namaLengkap; }
    public static String getNamaUsaha()  { return namaUsaha; }
    public static boolean isLoggedIn()   { return loggedIn; }
}