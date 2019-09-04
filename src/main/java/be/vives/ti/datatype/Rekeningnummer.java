package be.vives.ti.datatype;

import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import org.apache.commons.lang3.StringUtils;

/**
 * Van een rekeningnummer een type maken. Er kunnen enkel geldige
 * rekeningnummers gecreeerd worden volgens het IBAN formaat voor België:
 * BEaa bbbb cccc dddd
 * met a, b, c, d willekeurige cijfers
 * Wanneer het rekeningnummer niet klopt wordt een ApplicationException gegooid.
 * <p>
 * Om na te gaan of zo’n rekeningnummer geldig is moeten volgende stappen ondergaan worden:
 * Plak BEaa achteraan, dus na bbbbccccdddd
 * bbbbccccddddBEaa
 * Zet BE om naar een getal (A=10, B=11, C=12, …)
 * bbbbccccdddd1114aa
 * Bereken de rest na deling door 97
 * bbbbccccdddd1114aa mod 97
 * Als de rest gelijk is aan 1, gaat het om een geldig Belgisch rekeningnummer
 * volgens de IBAN-regels.
 */
public class Rekeningnummer {

    private String rekeningnummer;

    /**
     * analyseert een rekeningnummer in stringvorm op geldigheid.
     *
     * @param rekeningnr
     * @throws ApplicationException fout wanneer de het rekeningnummer niet voldoet
     *                              aan het formaat of wanneer het rekeningnummer niet geldig is.
     */
    public Rekeningnummer(String rekeningnr) throws ApplicationException {
        if (StringUtils.isEmpty(rekeningnr)) {
            throw new ApplicationException(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());
        }

        int spatie1 = rekeningnr.indexOf(" ");
        int spatie2 = rekeningnr.indexOf(" ", spatie1 + 1);
        int spatie3 = rekeningnr.indexOf(" ", spatie2 + 1);

        // controleren of het formaat BExx xxxx xxxx xxxx klopt
        if ((spatie1 == 4) && (spatie2 == 9) && (spatie3 == 14)) {
            // plaatsen spaties kloppen
            // testen het gaat om BE en getallen
            try {

                long deel2 = Long.parseLong(rekeningnr.substring(spatie1 + 1,
                        spatie2));
                long deel3 = Long.parseLong(rekeningnr.substring(spatie2 + 1,
                        spatie3));
                long deel4 = Long.parseLong(rekeningnr.substring(spatie3 + 1));

                char B = rekeningnr.toUpperCase().charAt(0);
                char E = rekeningnr.toUpperCase().charAt(1);

                if (B == 'B' && E == 'E') {
                    //B wordt opgezet naar 11
                    //E wordt omgezet naar 14
                    //de twee cijfers erachter moeten er nog aan vastgeplakt worden

                    long deel1 = 111400L + Long.parseLong(rekeningnr.substring(2, 4));

                    long geheel = deel2 * 100000000000000L + deel3 * 10000000000L + deel4 * 1000000L + deel1;

                    if (geheel % 97 == 1) {
                        rekeningnummer = rekeningnr;
                    } else {
                        throw new ApplicationException(ApplicationExceptionType.REK_REKNUMMER_ONGELDIG.getMessage());
                    }
                } else {
                    throw new ApplicationException(ApplicationExceptionType.REK_REKNUMMER_ONGELDIG_FORMAAT.getMessage());
                }
            } catch (NumberFormatException p) {
                throw new ApplicationException(ApplicationExceptionType.REK_REKNUMMER_ONGELDIG_FORMAAT.getMessage());
            }
        } else {
            throw new ApplicationException(ApplicationExceptionType.REK_REKNUMMER_ONGELDIG_FORMAAT.getMessage());
        }
    }

    public String getRekeningnummer() {
        return rekeningnummer;
    }

    // nodig om het rekeningnummer in Stringvorm te kunnen tonen in de tabel
    public String toString() {
        return rekeningnummer;
    }
}
