package be.vives.ti.DAO.connect;

import be.vives.ti.exception.DBException;

import java.util.Properties;

public class DBProp {

    private static String dbUrl;
    private static String driver;
    private static String login;
    private static String paswoord;

    /**
     * Haalt de URL, driver paswoord en login uit het bestand DB.properties en
     * vult deze in in de overeenkomstige velden
     */
    private DBProp() throws DBException {
        Properties appProperties = new Properties();
        try {
            appProperties.load(this.getClass().getResourceAsStream(
                    "/database/DB.properties"));
            dbUrl = appProperties.getProperty("dbUrl");
            driver = appProperties.getProperty("driver");
            login = appProperties.getProperty("login");
            paswoord = appProperties.getProperty("paswoord");

        } catch (java.io.IOException ex) {
            throw new DBException(
                    "Bestand (DB.properties) met gegevens over DB niet gevonden.");
        }
    }

    /**
     * @return the dbUrl
     * @throws DBException wanneer DB.properties niet toegankelijk is
     */
    public static String getDbUrl() throws DBException {
        if (dbUrl == null) {
            DBProp db = new DBProp();
        }
        return dbUrl;
    }

    /**
     * @return the driver
     * @throws DBException wanneer DB.properties niet toegankelijk is
     */
    public static String getDriver() throws DBException {
        if (driver == null) {
            DBProp db = new DBProp();
        }
        return driver;
    }

    /**
     * @return the login
     * @throws DBException wanneer DB.properties niet toegankelijk is
     */
    public static String getLogin() throws DBException {
        if (login == null) {
            DBProp db = new DBProp();
        }
        return login;
    }

    /**
     * @return the paswoord
     * @throws DBException wanneer DB.properties niet toegankelijk is
     */
    public static String getPaswoord() throws DBException {
        if (paswoord == null) {
            DBProp db = new DBProp();
        }
        return paswoord;
    }
}
