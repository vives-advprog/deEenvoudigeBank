package be.vives.ti.extra;

import be.vives.ti.DAO.connect.ConnectionManager;
import be.vives.ti.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtraQueries {
    /**
     * Zoekt de eerste ongebruikte id van een klant in de databank.
     *
     * @return Een geldige, maar ongebruikte id van een rit. -1 indien geen
     * geldige ID gevonden kon worden.
     * @throws DBException Exception die duidt op een verkeerde installatie van
     *                     de DAO (draait de webserver?) of een fout in de query.
     */
    public static Integer getOngebruiktKlantID() throws DBException {
        Integer ongebruikteKlantID = -1;
        //connectie maken met db
        try (Connection conn = ConnectionManager.getConnection()) {
            // sql-statement aanmaken
            try (PreparedStatement stmt = conn.prepareStatement("select "
                    + "max(id)"
                    + "from klant")) {

                stmt.execute();
                try (ResultSet r = stmt.getResultSet()) {
                    while (r.next()) {
                        ongebruikteKlantID = r.getInt(1);
                    }
                } catch (SQLException ex) {
                    throw new DBException("SQL-exception in getOngebruiktKlantID (resultset)" + ex);
                }
            } catch (SQLException ex) {
                throw new DBException("SQL-exception in getOngebruiktKlantID (statement)" + ex);
            }

        } catch (SQLException ex) {
            throw new DBException("SQL-exception in getOngebruiktKlantID (connection)" + ex);
        }
        return ++ongebruikteKlantID;
    }
}
