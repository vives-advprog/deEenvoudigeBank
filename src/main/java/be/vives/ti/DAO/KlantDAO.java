package be.vives.ti.DAO;

import be.vives.ti.DAO.connect.ConnectionManager;
import be.vives.ti.databag.Klant;
import be.vives.ti.datatype.KlantStatus;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;

import java.sql.*;
import java.util.ArrayList;

/**
 * Bevat alle functionaliteit op de DAO-tabel klant. - schrappen van een
 * klant - toevoegen van een klant - wijzigen van klant - zoeken van alle
 * klanten gesorteerd op naam, voornaam - zoeken van alle ingeschreven klanten
 * gesorteerd op naam, voornaam - zoeken van alle uitgeschreven klanten
 * gesorteerd op naam, voornaam - zoeken van een klant op id - zoeken van een
 * klant op naam, voornaam, adres, postcode en gemeente
 */
public class KlantDAO {

    /**
     * Zoekt adhv een id een klant op. Wanneer geen klant werd gevonden, wordt
     * null teruggegeven.
     *
     * @param id id van de klant die gezocht wordt (kan null zijn,
     * @return klant die gezocht wordt, null indien de klant niet werd gevonden.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public Klant zoekKlant(Integer id) throws DBException {
        if (id != null) {
            Klant returnKlant = null;
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                    "select id"
                        + " , naam"
                        + " , voornaam"
                        + " , adres"
                        + " , postcode"
                        + " , gemeente"
                        + " , status "
                        + " from klant "
                        + " where id = ?")) {

                    // parameters invullen in query
                    stmt.setInt(1, id);

                    // execute voert het SQL-statement uit
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        // van de klant uit de DAO een Klant-object maken
                        // er werd een klant gevonden
                        if (r.next()) {
                            returnKlant = getKlantUitDatabase(r);
                        }
                        return returnKlant;
                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in zoekKlant "
                            + "- resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in zoekKlant "
                        + "- statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekKlant "
                    + "- connection" + sqlEx);
            }
        }
        return null;
    }

    /**
     * Zoekt adhv de naam, voornaam, adres, postcode en gemeente een klant op.
     * Wanneer geen klant werd gevonden, wordt null teruggegeven.
     *
     * @param klant klant die gezocht moet worden (naam, voornaam, adres, postcode,
     *              gemeente)
     * @return true indien de klant werd gevonden, false indien de klant niet werd gevonden.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public boolean bestaatKlant(Klant klant) throws DBException {
        if (klant != null) {
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "select id"
                                + " , naam"
                                + " , voornaam"
                                + " , adres"
                                + " , postcode"
                                + " , gemeente"
                                + " , status "
                                + " from klant "
                                + " where naam = ? "
                                + "   and voornaam = ? "
                                + "   and adres = ? "
                                + "   and postcode = ? "
                                + "   and gemeente = ? ")) {
                    stmt.setString(1, klant.getNaam());
                    stmt.setString(2, klant.getVoornaam());
                    stmt.setString(3, klant.getAdres());
                    stmt.setString(4, klant.getPostcode());
                    stmt.setString(5, klant.getGemeente());
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        if (r.next()) {
                            return true;
                        }

                    } catch (SQLException sqlEx) {
                        throw new DBException("SQL-exception in bestaatKlant - resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in bestaatKlant - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in bestaatKlant - connection");
            }
        }
        // geen klant opgegeven
        return false;
    }

    /**
     * Geeft alle klanten met een meegegeven KlantStatus terug in een lijst, gesorteerd op naam,
     * voornaam
     *
     * @param klantStatus de status waaraan de geretourneerde klanten moeten voldoen
     * @return lijst van klanten die ingeschreven zijn, leeg indien geen gevonden.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    private ArrayList<Klant> zoekKlantMetStatus(KlantStatus klantStatus) throws DBException {
        if (klantStatus != null) {
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {

                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                    prepareStatement(
                        "select id "
                            + " , naam"
                            + " , voornaam"
                            + " , adres"
                            + " , postcode"
                            + " , gemeente"
                            + " , status "
                            + " from klant "
                            + " where status = ? "
                            + " order by naam"
                            + "        , voornaam")) {
                    stmt.setString(1, klantStatus.toString());
                    stmt.execute();
                    // result opvragen (en automatisch sluiten)
                    try (ResultSet r = stmt.getResultSet()) {
                        // van alle klanten uit de DAO Klant-objecten maken
                        // en in een lijst steken
                        return getKlantenUitDatabase(r);
                    } catch (SQLException sqlEx) {
                        throw new DBException(
                            "SQL-exception in zoekIngeschrevenKlanten - resultset" + sqlEx);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException(
                        "SQL-exception in zoekIngeschrevenKlanten - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                    "SQL-exception in zoekIngeschrevenKlanten - connection" + sqlEx);
            }
        }
        return new ArrayList<>();
    }

    /**
     * Geeft alle ingeschreven klanten terug in een lijst, gesorteerd op naam,
     *
     * @return lijst van klanten die ingeschreven zijn
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public ArrayList<Klant> zoekIngeschrevenKlanten() throws DBException {
        return zoekKlantMetStatus(KlantStatus.INGESCHREVEN);
    }

    /**
     * Geeft alle uitgeschreven klanten terug in een lijst, gesorteerd op naam,
     *
     * @return lijst van klanten die uitgeschreven zijn
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public ArrayList<Klant> zoekUitgeschrevenKlanten() throws DBException {
        return zoekKlantMetStatus(KlantStatus.UITGESCHREVEN);

    }

    /**
     * Geeft alle klanten terug in een lijst, gesorteerd op naam, voornaam
     *
     * @return lijst van alle klanten
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public ArrayList<Klant> zoekAlleKlanten() throws DBException {

        // connectie tot stand brengen (en automatisch sluiten)
        try (Connection conn = ConnectionManager.getConnection()) {
            // preparedStatement opstellen (en automatisch sluiten)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "select id"
                            + " , naam"
                            + " , voornaam"
                            + " , adres"
                            + " , postcode"
                            + " , gemeente"
                            + " , status "
                            + " from klant "
                            + " order by naam"
                            + "        , voornaam")) {
                stmt.execute();
                // result opvragen (en automatisch sluiten)
                try (ResultSet r = stmt.getResultSet()) {
                    // van alle klanten uit de DAO Klant-objecten maken
                    // en in een ljst steken
                    return getKlantenUitDatabase(r);
                } catch (SQLException sqlEx) {
                    throw new DBException(
                            "SQL-exception in zoekAlleKlanten - resultset" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in zoekAlleKlanten - statement" + sqlEx);
            }
        } catch (SQLException sqlEx) {
            throw new DBException(
                    "SQL-exception in zoekAlleKlanten - connection" + sqlEx);
        }
    }

    /**
     * Schrijft de klant met meegegeven id uit.
     *
     * @param id id van de klant die uitgeschreven moet worden
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    // OPM: parameter van type Integer (aangezien id in Klant null kan zijn
    // laten we dit hier ook best toe (voor wanneer id uit Klant wordt gelezen))
    public void verwijderKlant(Integer id) throws DBException {
        if (id != null) {
            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "update klant "
                                + " set status = ? "
                                + " where id = ?")) {
                    stmt.setString(1, KlantStatus.UITGESCHREVEN.toString());
                    stmt.setInt(2, id);
                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in verwijderKlant - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in verwijderKlant - connection" + sqlEx);
            }
        }
    }

    /**
     * Voegt een klant toe. Het id wordt automatisch gegenereerd door de
     * DAO
     *
     * @param klant de klant die toegevoegd moet worden
     * @return gegenereerd id van de klant die net werd toegevoegd of null
     * indien geen klant werd opgegeven.
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public Integer toevoegenKlant(Klant klant) throws DBException {
        if (klant != null) {
            Integer primaryKey = null;

            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "insert into klant(naam"
                                + " , voornaam"
                                + " , adres"
                                + " , postcode"
                                + " , gemeente"
                                + " , status"
                                + " ) values(?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, klant.getNaam());
                    stmt.setString(2, klant.getVoornaam());
                    stmt.setString(3, klant.getAdres());
                    stmt.setString(4, klant.getPostcode());
                    stmt.setString(5, klant.getGemeente());
                    stmt.setString(6, KlantStatus.INGESCHREVEN.toString());
                    stmt.execute();

                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        primaryKey = generatedKeys.getInt(1);
                    }
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in toevoegenKlant "
                            + "- statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException("SQL-exception in toevoegenKlant "
                        + "- connection" + sqlEx);
            }
            return primaryKey;
        } else {
            return null;
        }
    }

    /**
     * Wijzigt een klant adhv zijn id.
     *
     * @param klant klant die gewijzigd moet worden
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de DAO of een fout in de query.
     */
    public void wijzigenKlant(Klant klant) throws DBException, ApplicationException {
        if (klant != null) {

            // connectie tot stand brengen (en automatisch sluiten)
            try (Connection conn = ConnectionManager.getConnection()) {
                // preparedStatement opstellen (en automatisch sluiten)
                try (PreparedStatement stmt = conn.
                        prepareStatement("update klant "
                                + " set naam = ?"
                                + "   , voornaam =?"
                                + "   , adres = ?"
                                + "   , postcode = ?"
                                + "   , gemeente = ? "
                                + " where id = ?")) {

                    stmt.setString(1, klant.getNaam());
                    stmt.setString(2, klant.getVoornaam());
                    stmt.setString(3, klant.getAdres());
                    stmt.setString(4, klant.getPostcode());
                    stmt.setString(5, klant.getGemeente());
                    stmt.setObject(6, klant.getId(), Types.INTEGER);
                    stmt.execute();
                } catch (SQLException sqlEx) {
                    throw new DBException("SQL-exception in wijzigenKlant - statement" + sqlEx);
                }
            } catch (SQLException sqlEx) {
                throw new DBException(
                        "SQL-exception in wijzigenKlant - connection" + sqlEx);
            }
        }
    }

    private ArrayList<Klant> getKlantenUitDatabase(ResultSet r) throws SQLException {
        ArrayList<Klant> klanten = new ArrayList<>();
        while (r.next()) {
            Klant klant = getKlantUitDatabase(r);
            klanten.add(klant);
        }
        return klanten;
    }

    private Klant getKlantUitDatabase(ResultSet r) throws SQLException {
        Klant klant = new Klant();
        klant.setId(r.getInt("id"));
        klant.setNaam(r.getString("naam"));
        klant.setVoornaam(r.getString("voornaam"));
        klant.setAdres(r.getString("adres"));
        klant.setPostcode(r.getString("postcode"));
        klant.setGemeente(r.getString("gemeente"));
        klant.setStatus(KlantStatus.valueOf(r.getString("status")));
        return klant;
    }
}

