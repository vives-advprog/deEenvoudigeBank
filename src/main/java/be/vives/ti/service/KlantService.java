package be.vives.ti.service;

import be.vives.ti.DAO.KlantDAO;
import be.vives.ti.databag.Klant;
import be.vives.ti.datatype.KlantStatus;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Bevat alle functionaliteit van een klant, met de nodige checks. - toevoegen
 * van een klant - schrappen van een klant - wijzigen van een klant
 */
public class KlantService {

    private KlantDAO klantDAO;
    private RekeningService rekeningService;

    public KlantService(KlantDAO klantDAO, RekeningService rekeningService) {
        this.klantDAO = klantDAO;
        this.rekeningService = rekeningService;
    }

    /**
     * Voegt een klant toe
     *
     * @param klant klant die moet worden toegevoegd.
     * @return id van de pas toegevoegde klant
     * @throws ApplicationException Wordt gegooid wanneer geen klant
     *                              werd opgegeven, wanneer niet alle velden (correct) ingevuld zijn, of
     *                              wanneer een klant al bestaat.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public Integer toevoegenKlant(Klant klant) throws ApplicationException, DBException {
        // parameter ingevuld?
        if (klant == null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_NULL.getMessage());
        }
        // alle gegevens ingevuld?
        checkAlleVeldenIngevuld(klant);

        // id mag niet ingevuld zijn
        if (klant.getId() != null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_IDWORDTGEGENEREERD.getMessage());
        }

        // bestaat klant?
        if (bestaatKlant(klant)) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_BESTAATAL.getMessage());
        }

        //toevoegen
        return klantDAO.toevoegenKlant(klant);
    }

    /**
     * Verwijdert klant adhv zijn id
     *
     * @param id id van de klant die verwijderd moet worden.
     * @throws ApplicationException Wordt gegooid wanneer de klant
     *                              niet bestaat, wanneer de klant al uitgeschreven is, of wanneer de klant
     *                              nog rekeningen heeft die open staan.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public void verwijderKlant(int id) throws ApplicationException, DBException {

        // bestaat de klant?
        Klant kb = zoekKlant(id);
        if (kb == null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_BESTAATNIET.getMessage());
        }

        // is de klant nog ingeschreven?
        if (KlantStatus.UITGESCHREVEN.equals(kb.getStatus())) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_UITGESCHREVEN.getMessage());
        }

        // heeft de klant openstaande rekeningen?
        int aantal = rekeningService.zoekAantalOpenRekeningen(id);
        if (aantal > 0) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_HEEFTNOGREKENINGEN.getMessage());
        }

        // verwijderen
        klantDAO.verwijderKlant(kb.getId());
    }

    /**
     * Wijzigt de velden naam, voornaam, adres, postcode en gemeente van de
     * klant op basis van zijn id
     *
     * @param klant nieuwe gegevens van de klant die gewijzigd moet worden
     * @throws ApplicationException Wordt gegooid wanneer er geen
     *                              klant werd opgegeven, niet alle velden van de klant (correct) zijn
     *                              ingevuld, wanneer de klant al uitgeschreven is, wanneer er al een klat
     *                              bestaat met deze gegevens, of wanneer de klant met dat id niet bestaat.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public void wijzigenKlant(Klant klant) throws ApplicationException, DBException {
        // parameter ingevuld?
        if (klant == null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_NULL.getMessage());
        }

        //check velden ingevuld
        checkAlleVeldenIngevuld(klant);

        // zit de klant die gewijzigd moet worden in de DB?
        Klant teWijzigenKlant = zoekKlant(klant.getId());
        if (teWijzigenKlant == null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_BESTAATNIET.getMessage());
        }

        // is originele klant nog ingeschreven?
        if (teWijzigenKlant.getStatus().equals(KlantStatus.UITGESCHREVEN)) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_UITGESCHREVEN.getMessage());
        }

        // bestaat er al een klant met deze nieuwe gegevens?
        // voornaam, naam, adres

        if (bestaatKlant(klant)) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_BESTAATAL.getMessage());
        }

        // klant met id van originele klant wijzigen
        klantDAO.wijzigenKlant(klant);
    }

    /**
     * Controleert of alle velden in het object k ingevuld zijn (id niet)
     * <p>
     * Gooit een be.vives.exception bij: - naam niet ingevuld - voornaam niet ingevuld -
     * adres niet ingevuld - postcode niet ingevuld - gemeente niet ingevuld -
     * status niet ingevuld of status = UITGESCHREVEN
     */
    private void checkAlleVeldenIngevuld(Klant klant) throws ApplicationException {

        if (StringUtils.isBlank(klant.getNaam())) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_NAAM_LEEG.getMessage());
        }
        if (StringUtils.isBlank(klant.getVoornaam())) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_VOORNAAM_LEEG.getMessage());
        }
        if (StringUtils.isBlank(klant.getAdres())) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_ADRES_LEEG.getMessage());
        }
        if (StringUtils.isBlank(klant.getPostcode())) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_POSTCODE_LEEG.getMessage());
        }
        if (StringUtils.isBlank(klant.getGemeente())) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_GEMEENTE_LEEG.getMessage());
        }
        // de status mag niet opgegeven worden. Dit is automatisch INGESCHREVEN.
        if (klant.getStatus() != null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_MOETINGESCHREVENZIJN.getMessage());
        }
    }

    /**
     * Zoekt adhv de naam, voornaam, adres, postcode en gemeente een klant op.
     * Wanneer geen klant werd gevonden, wordt null teruggegeven.
     *
     * @param klant klant die gezocht moet worden (naam, voornaam, adres, postcode,
     *              gemeente)
     * @return klant die gezocht wordt, null indien de klant niet werd gevonden.
     * @throws DBException          Exception die duidt op een verkeerde
     *                              installatie van de be.vives.DAO of een fout in de query.
     * @throws ApplicationException Wordt gegooid wanneer geen klant
     *                              werd opgegeven.
     */
    public boolean bestaatKlant(Klant klant) throws DBException, ApplicationException {
        // parameter ingevuld?
        if (klant == null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_NULL.getMessage());
        }
        return klantDAO.bestaatKlant(klant);
    }

    /**
     * Zoekt adhv een id een klant op. Wanneer geen klant werd gevonden, wordt
     * null teruggegeven.
     *
     * @param id id van de klant die gezocht wordt (kan null zijn,
     * @return klant die gezocht wordt, null indien de klant niet werd gevonden.
     * @throws DBException          Exception die duidt op een verkeerde
     *                              installatie van de be.vives.DAO of een fout in de query.
     * @throws ApplicationException Wordt gegooid wanneer geen id werd
     *                              opgegeven.
     */
    public Klant zoekKlant(Integer id) throws DBException, ApplicationException {
        // parameter ingevuld?
        if (id == null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_ID.getMessage());
        }
        return klantDAO.zoekKlant(id);
    }

    /**
     * Geeft alle ingeschreven klanten terug in een lijst, gesorteerd op naam,
     * voornaam
     *
     * @return lijst van klanten die ingeschreven zijn
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de be.vives.DAO of een fout in de query.
     */
    public ArrayList<Klant> zoekIngeschrevenKlanten() throws DBException {
        return klantDAO.zoekIngeschrevenKlanten();
    }

    public void valideerKlant(Integer id) throws ApplicationException, DBException {
        // bestaat klant?
        Klant klant = zoekKlant(id);
        if (klant == null) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_BESTAATNIET.getMessage());
        }

        // is klant nog ingeschreven?
        if (KlantStatus.UITGESCHREVEN.equals(klant.getStatus())) {
            throw new ApplicationException(ApplicationExceptionType.KLANT_UITGESCHREVEN.getMessage());
        }
    }

}
