package be.vives.ti.service;

import be.vives.ti.DAO.RekeningDAO;
import be.vives.ti.databag.Rekening;
import be.vives.ti.datatype.RekeningStatus;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Bevat alle functionaliteit van een rekening, met de nodige checks. -
 * toevoegen van een rekening - schrappen van een rekening - geld storten op een
 * rekening - geld opnemen van een rekening
 */
public class RekeningService {

    private RekeningDAO rekeningDAO;

    public RekeningService(RekeningDAO rekeningDAO) {
        this.rekeningDAO = rekeningDAO;
    }

    /**
     * Verwijdert meegegeven rekening adhv zijn rekeningnummer
     *
     * @param rekeningnummer rekeniungnummer van de rekening die verwijderd moet
     *                       worden.
     * @throws ApplicationException Wordt gegooid wanneer het
     *                              rekeningnummer niet bestaat, de rekening al gesloten is, of wanneer er
     *                              nog geld op de rekening staat.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public void verwijderRekening(String rekeningnummer) throws
            ApplicationException, DBException {

        // bestaat rekening?
        Rekening rekening = zoekRekening(rekeningnummer);
        if (rekening == null) {
            throw new ApplicationException(ApplicationExceptionType.REK_BESTAAT_NIET.getMessage());
        }

        //is rekening open?
        checkRekeningOpen(rekening);

        //is saldo = 0?
        checkSaldoIsZero(rekening);

        // verwijder rekening
        rekeningDAO.verwijderRekening(rekeningnummer);
    }

    /**
     * Verhoogt het saldo van rekening r met waarde bedrag.
     *
     * @param rekeningnummer rekeningnummer van de rekening waarop geld moet
     *                       gestort worden.
     * @param bedrag         bedrag dat gestort moet worden
     * @throws ApplicationException Wordt gegooid wanneer het
     *                              rekeningnummer niet bestaat, de rekening al gesloten is, of wanneer het
     *                              te storten bedrag negatief is.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public void stortenRekening(String rekeningnummer, BigDecimal bedrag) throws
            ApplicationException, DBException {

        // bestaat rekening?
        Rekening rekening = zoekRekening(rekeningnummer);
        if (rekening == null) {
            throw new ApplicationException(ApplicationExceptionType.REK_BESTAAT_NIET.getMessage());
        }

        // is rekening open?
        checkRekeningOpen(rekening);

        //is bedrag positief?
        checkBedragPositief(bedrag);

        //wijzig saldo rekening
        // waarden van type Bigdecimal optellen doe je met add()
        BigDecimal nieuwSaldo = rekening.getSaldo().add(bedrag);
        // getal met twee cijfers na komma wegschrijven
        rekeningDAO.wijzigenSaldoRekening(rekeningnummer, nieuwSaldo.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Verlaagt het saldo van de rekening met waarde bedrag.
     *
     * @param rekeningnummer rekeningnummer van de rekening waarop geld moet
     *                       gestort worden.
     * @param bedrag         bedrag dat opgenomen moet worden
     * @throws ApplicationException Wordt gegooid wanneer het
     *                              rekeningnummer niet bestaat, de rekening al gesloten is, wanneer het op
     *                              te nemen bedrag negatief is, of wanneer het bedrag te groot is.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public void opnemenRekening(String rekeningnummer, BigDecimal bedrag) throws
            ApplicationException, DBException {

        // bestaat rekening?
        Rekening rekening = zoekRekening(rekeningnummer);
        if (rekening == null) {
            throw new ApplicationException(ApplicationExceptionType.REK_BESTAAT_NIET.getMessage());
        }

        // is rekening open?
        checkRekeningOpen(rekening);

        // is bedrag positief?
        checkBedragPositief(bedrag);

        // is bedrag niet groter dan saldo?
        checkBedragNietTeGroot(rekening, bedrag);

        // wijzig saldo rekening
        // waarden aftrekken van type Bigdecimal doe je met subtract()
        BigDecimal nieuwSaldo = rekening.getSaldo().subtract(bedrag);
        // getal met twee cijfers na komma wegschrijven
        rekeningDAO.wijzigenSaldoRekening(rekeningnummer, nieuwSaldo.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Voegt een rekening met gegeven rekeningnummer toe
     *
     * @param rekening rekening die toegevoegd moet worden
     * @throws ApplicationException Wordt gegooid wanneer de klant
     *                              niet bestaat, de klant al uitgeschreven is, geen rekening opgegeven werd,
     *                              niet alle velden voor de rekening ingevuld zijn, het rekeningnummer al in
     *                              gebruik is, het rekeningnummer niet geldig is.
     * @throws DBException          duidt op fouten vanuit de be.vives.DAO.
     */
    public void toevoegenRekening(Rekening rekening)
            throws ApplicationException, DBException {
        if (rekening == null) {
            throw new ApplicationException(ApplicationExceptionType.REK_NULL.getMessage());

        }

        // controleren of alle velden ingevuld zijn
        checkAlleVeldenIngevuld(rekening);

        // bestaat er al een rekening met dit rekeningnummer?
        if (zoekRekening(rekening.getRekeningnummer().getRekeningnummer()) != null) {
            throw new ApplicationException(ApplicationExceptionType.REK_BESTAAT_AL.getMessage());
        }

        // rekening toevoegen
        rekeningDAO.toevoegenRekening(rekening);
    }

    /**
     * Zoekt adhv van het rekeningnummer een rekening op. Wanneer geen rekening
     * werd gevonden wordt null teruggeven
     *
     * @param rekeningnummer rekeningnummer van de rekening die gezocht moet
     *                       worden.
     * @return rekening die gezocht wordt, null indien de rekening niet werd
     * gevonden.
     * @throws DBException          Exception die duidt op een verkeerde
     *                              installatie van de be.vives.DAO of een fout in de query.
     * @throws ApplicationException wordt gegooid wanneer het
     *                              rekeningnummer ongeldig is
     */
    public Rekening zoekRekening(String rekeningnummer) throws DBException, ApplicationException {
        // rekeningnummer opgegeven?
        if (StringUtils.isEmpty(rekeningnummer)) {
            throw new ApplicationException(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());
        }

        return rekeningDAO.zoekRekening(rekeningnummer);
    }

    /**
     * Geeft alle open rekeningen van een gegeven klant terug in een lijst
     *
     * @param eigenaar de eigenaar van de rekeningen die gezocht worden
     * @return lijst van rekeningen die open zijn
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de be.vives.DAO of een fout in de query.
     */
    public ArrayList<Rekening> zoekOpenRekeningen(int eigenaar) throws DBException {
        return rekeningDAO.zoekOpenRekeningen(eigenaar);
    }

    /**
     * Geeft het aantal open rekeningen van een gegeven klant
     *
     * @param eigenaar de eigenaar van de rekeningen die gezocht worden
     * @return aantal rekeningen die nog open staan
     * @throws DBException Exception die duidt op een verkeerde
     *                     installatie van de be.vives.DAO of een fout in de query.
     */
    public int zoekAantalOpenRekeningen(int eigenaar) throws DBException {
        return rekeningDAO.zoekAantalOpenRekeningen(eigenaar);
    }

    /**
     * Controleert of de rekening omschreven in het object r open is. Dit
     * gebeurt op basis van zijn id.
     * <p>
     * Gooit een be.vives.exception bij: - rekening niet open
     */
    private static void checkRekeningOpen(Rekening rekening) throws ApplicationException {
        if (RekeningStatus.GESLOTEN.equals(rekening.getStatus())) {
            throw new ApplicationException(ApplicationExceptionType.REK_IS_GESLOTEN.getMessage());
        }
    }

    /**
     * Controleert of bedrag > 0
     * <p>
     * Gooit een be.vives.exception bij: - bedrag negatief of nul, of niet opgegeven
     */
    private void checkBedragPositief(BigDecimal bedrag) throws
            ApplicationException {

        //is bedrag geldig?
        if (bedrag == null) {
            throw new ApplicationException(ApplicationExceptionType.REK_BEDRAG_LEEG.getMessage());
        }

        if (bedrag.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException(ApplicationExceptionType.REK_BEDRAG_MOET_POS_ZIJN.getMessage());
        }
    }

    /**
     * Controleert of saldo - bedrag >= 0
     * <p>
     * Gooit een be.vives.exception bij: - bedrag groter dan saldo, of niet opgegeven
     */
    private void checkBedragNietTeGroot(Rekening rekening, BigDecimal bedrag) throws
            ApplicationException {

        // is bedrag kleiner dan saldo?
        if (rekening.getSaldo().compareTo(bedrag) < 0) {
            throw new ApplicationException(ApplicationExceptionType.REK_BEDRAG_TE_GROOT.getMessage());
        }
    }

    /**
     * Controleer of het saldo = 0
     * Gooit een exception bij: saldo niet 0, of niet opgegeven
     */
    private void checkSaldoIsZero(Rekening rekening) throws ApplicationException {
        //is saldo 0?
        if (BigDecimal.ZERO.compareTo(rekening.getSaldo()) != 0) {
            throw new ApplicationException(ApplicationExceptionType.REK_SALDO_MOET_NUL_ZIJN.getMessage());
        }
    }

    /**
     * Controleert of alle velden in het object r ingevuld zijn
     * <p>
     * Gooit een be.vives.exception bij: - rekeningnummer niet ingevuld - saldo niet
     * ingevuld of saldo niet 0 - status niet ingevuld of status = GESLOTEN
     */
    private void checkAlleVeldenIngevuld(Rekening rekening) throws ApplicationException {
        //is er een rekeningnummer?
        if (rekening.getRekeningnummer() == null) {
            throw new ApplicationException(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());
        }

        checkSaldoIsZero(rekening);

        // heeft nieuwe rekening een geldige status
        if ((rekening.getStatus() != null)) {
            throw new ApplicationException(ApplicationExceptionType.REK_MOET_OPEN_ZIJN.getMessage());
        }
    }
}
