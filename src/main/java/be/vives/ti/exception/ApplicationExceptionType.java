package be.vives.ti.exception;

/**
 * Enum die een gestandaardiseerde foutboodschap voor een ApplicationException voorstelt.
 */
public enum ApplicationExceptionType {

    // ongeldige eigenschappen klant
    KLANT_NULL("Er werd geen klant opgegeven."),
    KLANT_ID("Er werd geen klantid opgegeven."),
    KLANT_VOORNAAM_LEEG("Er werd geen voornaam opgegeven."),
    KLANT_NAAM_LEEG("Er werd geen naam opgegeven."),
    KLANT_ADRES_LEEG("Er werd geen straat opgegeven."),
    KLANT_POSTCODE_LEEG("Er werd geen postcode opgegeven."),
    KLANT_GEMEENTE_LEEG("Er werd geen gemeente opgegeven."),

    // ongeldige operaties klant
    KLANT_ID_WORDT_GEGENEREERD("De klant krijgt automatisch een id en mag dus niet opgegeven worden."),
    KLANT_BESTAAT_AL("Er bestaat al een klant met deze naam, voornaam en adres."),
    KLANT_BESTAAT_NIET("De klant werd niet gevonden."),
    KLANT_UITGESCHREVEN("De klant is uitgeschreven."),
    KLANT_MOET_INGESCHREVEN_ZIJN("De klant krijgt automatisch de status INGESCHREVEN."),
    KLANT_HEEFT_NOG_REKENINGEN("De klant heeft nog openstaande rekeningen."),

    // ongeldige eigenschappen rekening
    REK_NULL("Er werd geen rekening opgegeven."),
    REK_REKNUMMER_LEEG("Er werd geen rekeningnummer opgegeven."),
    REK_BEDRAG_LEEG("Er werd geen bedrag opgegeven."),

    // ongeldige operaties rekening
    REK_BESTAAT_AL("Het rekeningnummer is al in gebruik."),
    REK_BESTAAT_NIET("Er werd geen rekening gevonden."),
    REK_BEDRAG_TE_GROOT("Het bedrag overschrijdt de limiet."),
    REK_BEDRAG_MOET_POS_ZIJN("Het bedrag moet positief zijn."),
    REK_SALDO_MOET_NUL_ZIJN("Het saldo moet nul zijn."),
    REK_IS_GESLOTEN("De rekening werd al afgesloten."),
    REK_MOET_OPEN_ZIJN("De rekening krijgt automatisch de status OPEN."),
    REK_ONGELDIG_BEDRAG("Het bedrag moet getal zijn, eventueel met een decimale punt."),
    REK_REKNUMMER_ONGELDIG("Er werd geen geldig rekeningnummer opgegeven."),
    REK_REKNUMMER_ONGELDIG_FORMAAT("Het formaat van de het rekeningnummer is ongeldig."),;

    private final String message;

    ApplicationExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
