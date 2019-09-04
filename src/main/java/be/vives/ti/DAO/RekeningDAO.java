package be.vives.ti.DAO;

import be.vives.ti.DAO.connect.ConnectionManager;
import be.vives.ti.databag.Rekening;
import be.vives.ti.datatype.RekeningStatus;
import be.vives.ti.datatype.Rekeningnummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Bevat alle functionaliteit op de DAO-tabel rekening. - zoeken van alle
 * rekeningen van een bepaalde klant - zoeken van alle open rekeningen van een
 * bepaalde klant - zoeken van alle gesloten rekeningen van een bepaalde klant -
 * zoeken van het aantal open rekeningen van een bepaalde klant - zoeken van een
 * rekening adhv rekeningnummer - wijzigen van een rekening - toevoegen van een
 * rekening - schrappen op een rekening
 */
public class RekeningDAO {

    /**
     * Zoekt adhv van het rekeningnummer een rekening op. Wanneer geen rekening
     * werd gevonden wordt null teruggeven
     *
     * @param rekeningnummer rekeningnummer van de rekening die gezocht moet
     *                       worden.
     * @return rekening die gezocht wordt, null indien de rekening niet werd
     * gevonden.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public Rekening zoekRekening(String rekeningnummer) throws DBException {
        if (rekeningnummer != null) {
            Rekening returnRekening = null;
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select rekeningnummer"
                                + " , saldo"
                                + " , status"
                                + " , eigenaar "
                                + " from rekening "
                                + " where rekeningnummer = ?")) {
                    stmt.setString(1, rekeningnummer);
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        // er werd een rekeningnummer gevonden
                        if (r.next()) {
                            returnRekening = getRekeningUitDatabase(r);
                        } // er werd geen rekeningnummer gevonden
                        return returnRekening;
                    } catch (ApplicationException ae) {
                        //wanneer bij het zoeken in de DB de constructor van rekeningnummer een exception gooit,
                        // dan zat er een foutief rekeningnummer in de DAO. De gebruiker kan dit onmogelijk
                        // oplossen, dus DBException
                        throw new DBException("Databasefout: fout rekeningnummer gevonden.");
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekRekening - resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekRekening - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in zoekRekening - connection" + sqlEx);
            }
        } else {
            return null;
        }
    }

    /**
     * Geeft alle rekeningen van een gegeven klant met een meegegeven RekeningStatus terug in een lijst
     *
     * @param eigenaar       de eigenaar van de rekeningen die gezocht worden
     * @param rekeningStatus de status waaraan de geretourneerde rekeningen moeten voldoen
     * @return lijst van rekeningen die gesloten zijn, leeg indien geen gevonden.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    private ArrayList<Rekening> zoekRekeningMetStatus(RekeningStatus rekeningStatus, int eigenaar) throws DBException {
        if (rekeningStatus != null) {
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                    prepareStatement(
                        "select rekeningnummer"
                            + " , saldo"
                            + " , status"
                            + " , eigenaar "
                            + " from rekening "
                            + " where status = ? "
                            + "   and eigenaar = ?")) {
                    stmt.setString(1, rekeningStatus.name());
                    stmt.setInt(2, eigenaar);
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        // van alle rekeningen uit de DAO Rekening-objecten maken
                        // en in een lijst steken
                        return getRekeningenUitDatabase(r);
                    } catch (ApplicationException ae) {
                        //wanneer bij het zoeken in de DB de constructor van rekeningnummer een exception gooit,
                        // dan zat er een foutief rekeningnummer in de DAO. De gebruiker kan dit onmogelijk
                        // oplossen, dus DBException
                        throw new DBException("Databasefout: fout rekeningnummer gevonden.");
                    } catch (SQLException sqlEx) {
                        throw new DBException(
                            "SQL-exception in zoekRekeningMetStatus - resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException(
                        "SQL-exception in zoekRekeningMetStatus - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                    "SQL-exception in zoekRekeningMetStatus - connection" + sqlEx);
            }
        }
        return new ArrayList<>();
    }

    /**
     * Geeft alle gesloten rekeningen van een gegeven klant terug in een lijst
     *
     * @param eigenaar de eigenaar van de rekeningen die gezocht worden
     * @return lijst van rekeningen die gesloten zijn
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public ArrayList<Rekening> zoekGeslotenRekeningen(int eigenaar) throws DBException {
        return zoekRekeningMetStatus(RekeningStatus.GESLOTEN, eigenaar);
    }

    /**
     * Geeft alle open rekeningen van een gegeven klant terug in een lijst
     *
     * @param eigenaar de eigenaar van de rekeningen die gezocht worden
     * @return lijst van rekeningen die open zijn
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public ArrayList<Rekening> zoekOpenRekeningen(int eigenaar) throws DBException {
        return zoekRekeningMetStatus(RekeningStatus.OPEN, eigenaar);
    }

    /**
     * Geeft het aantal open rekeningen van een gegeven klant
     *
     * @param eigenaar de eigenaar van de rekeningen die gezocht worden
     * @return aantal rekeningen die nog open staan
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public int zoekAantalOpenRekeningen(int eigenaar) throws DBException {
        // connectie tot stand brengen (en automatisch sluiten)
        try (Connection conn = ConnectionManager.getConnection()) {
            int aantal = 0;
            // preparedStatement opstellen (en automatisch sluiten)
            try (PreparedStatement stmt = conn.
                    prepareStatement(
                            "select count(*) as aantal "
                                    + " from rekening "
                                    + " where status = ? "
                                    + "   and eigenaar = ? "
                                    + " group by eigenaar")) {
                stmt.setString(1, RekeningStatus.OPEN.name());
                stmt.setInt(2, eigenaar);
                stmt.execute();
                // result opvragen (en automatisch sluiten)
                try (ResultSet r = stmt.getResultSet()) {
                    // van alle rekeningen uit de DAO Rekening-objecten maken
                    // en in een lijst steken
                    if (r.next()) {
                        aantal = r.getInt("aantal");
                    }
                    return aantal;

                } catch (SQLException sqlEx) {
                    throw new DBException(
                            "SQL-exception in zoekAantalOpenRekeningen - resultset" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in zoekAantalOpenRekeningen -statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in zoekAantalOpenRekeningen - connection" + sqlEx);
        }
    }

    /**
     * Geeft alle rekeningen van een bepaalde klant terug.
     *
     * @param eigenaar de eigenaar van de rekeningen die gezocht worden
     * @return lijst van alle rekeningen van een bepaalde klant
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public ArrayList<Rekening> zoekAlleRekeningen(int eigenaar) throws DBException {
        // connectie tot stand brengen (en automatisch sluiten)
        try (Connection conn = ConnectionManager.getConnection()) {

            // preparedStatement opstellen (en automatisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "select rekeningnummer"
                            + " , saldo"
                            + " , status"
                            + " , eigenaar "
                            + " from rekening "
                            + " where eigenaar = ?")) {
                stmt.setInt(1, eigenaar);
                stmt.execute();
                // result opvragen (en automatisch sluiten)
                try (ResultSet r = stmt.getResultSet()) {
                    // van alle rekeningen uit de DAO Rekening-objecten maken
                    // en in een lijst steken
                    return getRekeningenUitDatabase(r);
                } catch (ApplicationException ae) {
                    //wanneer bij het zoeken in de DB de constructor van rekeningnummer een exception gooit,
                    // dan zat er een foutief rekeningnummer in de DAO. De gebruiker kan dit onmogelijk
                    // oplossen, dus DBException
                    throw new DBException("Databasefout: fout rekeningnummer gevonden.");
                } catch (SQLException sqlEx) {
                    throw new DBException(
                            "SQL-exception in zoekAlleRekeningen - resultset" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in zoekAlleRekeningen - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in zoekAlleRekeningen - connection" + sqlEx);
        }
    }

    /**
     * Sluit een rekening met meegegeven rekeningnummer.
     *
     * @param rekeningnummer rekeningnummer van de rekening die gesloten moet
     *                       worden.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public void verwijderRekening(String rekeningnummer) throws DBException {
        if (rekeningnummer != null) {
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                        prepareStatement(
                                "update rekening "
                                        + " set status = ? "
                                        + " where rekeningnummer = ?")) {

                    stmt.setString(1, RekeningStatus.GESLOTEN.name());
                    stmt.setString(2, rekeningnummer);
                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in verwijderRekening" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in verwijderRekening - connection" + sqlEx);
            }
        }

    }

    /**
     * Voegt meegegeven rekening toe.
     *
     * @param rekening rekening die moet worden toegevoegd
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public void toevoegenRekening(Rekening rekening) throws DBException, ApplicationException {
        if (rekening != null) {
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                        prepareStatement("insert into rekening("
                                + " rekeningnummer"
                                + " , saldo"
                                + " , status"
                                + " , eigenaar) "
                                + " values(?,?,?,?)")) {

                    // als het rekeningnummer null is, kan deze niet zomaar omgezet worden naar een String
                    // je mag er niet vanuit gaan dat de service-laag dit controleert. Als dit vergeten wordt,
                    // treedt hier een NullPointerException op. Dit moet vermeden worden!
                    // Aangezien het niet doorgeven van een rekeningnummer op te lossen is door de gebruiker
                    // moet er een ApplicationException gegooid worden.
                    if (rekening.getRekeningnummer() != null) {
                        stmt.setString(1, rekening.getRekeningnummer().toString());
                    } else {
                        throw new ApplicationException(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());
                    }
                    // als het saldo null is, zal setBigDecimal dit opvangen. Er is dus geen controle nodig
                    stmt.setBigDecimal(2, rekening.getSaldo());
                    stmt.setString(3, RekeningStatus.OPEN.name());
                    stmt.setInt(4, rekening.getEigenaar());
                    stmt.execute();

                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenRekening" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in toevoegenRekening - connection" + sqlEx);
            }
        }
    }

    /**
     * Wijzigt een het saldo van een rekening
     *
     * @param rekeningnummer rekeningnumer van rekening waarvan saldo gewijzigd moet worden
     * @param nieuwSaldo     nieuw saldo voor de rekening
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public void wijzigenSaldoRekening(String rekeningnummer, BigDecimal nieuwSaldo) throws DBException {
        if ((rekeningnummer != null) && (nieuwSaldo != null)) {
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                        prepareStatement("update rekening "
                                + " set saldo =? "
                                + " where rekeningnummer = ?")) {

                    stmt.setBigDecimal(1, nieuwSaldo);
                    stmt.setString(2, rekeningnummer);

                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in wijzigenSaldoRekening" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in wijzigenSaldoRekening - connection" + sqlEx);
            }
        }
    }

    private ArrayList<Rekening> getRekeningenUitDatabase(ResultSet r) throws SQLException, ApplicationException {
        ArrayList<Rekening> rekeningen = new ArrayList<>();
        while (r.next()) {
            Rekening rb = getRekeningUitDatabase(r);
            rekeningen.add(rb);
        }
        return rekeningen;
    }

    private Rekening getRekeningUitDatabase(ResultSet r) throws SQLException, ApplicationException {
        Rekening rekening = new Rekening();
        rekening.setRekeningnummer(new Rekeningnummer(r.getString("rekeningnummer")));
        rekening.setSaldo(r.getBigDecimal("saldo"));
        rekening.setEigenaar(r.getInt("eigenaar"));
        rekening.setStatus(RekeningStatus.valueOf(r.getString("status")));

        return rekening;
    }

}
