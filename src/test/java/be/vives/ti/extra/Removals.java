package be.vives.ti.extra;

import be.vives.ti.DAO.connect.ConnectionManager;
import be.vives.ti.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Removals {

    /**
     * Verwijdert de opgegeven klant uit de DAO, zonder enige controle
     *
     * @param id id van de klant die verwijderd moet worden
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public static void removeKlant(int id) throws DBException {

        // connectie tot stand brengen (en automatisch sluiten)
        try (Connection conn = ConnectionManager.getConnection()) {
            // preparedStatement opstellen (en automtisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "delete from klant where id = ?")) {
                stmt.setInt(1, id);
                // execute voert elke sql-statement uit, executeQuery enkel de select
                stmt.execute();
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in removeKlant - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in removeKlant - connection" + sqlEx);
        }
    }

    /**
     * Verwijdert de rekening uit de DAO zonder enige controle
     *
     * @param rekeningnummer rekeningnummer van de rekening die verwijderd moet worden
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public static void removeRekening(String rekeningnummer) throws DBException {

        // connectie tot stand brengen (en automatisch sluiten)
        try (Connection conn = ConnectionManager.getConnection()) {
            // preparedStatement opstellen (en automtisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "delete from rekening where rekeningnummer = ?")) {
                stmt.setString(1, rekeningnummer);
                // execute voert elke sql-statement uit, executeQuery enkel de select
                stmt.execute();
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in removeRekening - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in removeRekening - connection" + sqlEx);
        }
    }

}
