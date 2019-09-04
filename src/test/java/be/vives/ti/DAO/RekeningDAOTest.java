package be.vives.ti.DAO;

import be.vives.ti.databag.Klant;
import be.vives.ti.databag.Rekening;
import be.vives.ti.datatype.KlantStatus;
import be.vives.ti.datatype.RekeningStatus;
import be.vives.ti.datatype.Rekeningnummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.ExtraQueries;
import be.vives.ti.extra.Removals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RekeningDAOTest {

    private RekeningDAO rekeningDAO = new RekeningDAO();
    private KlantDAO klantDAO = new KlantDAO();

    private Klant klant;
    private Klant klant2;
    private Klant klant3;

    @Before
    public void setUp() throws Exception {
        // een klant met een rekening maken
        klant = new Klant();

        klant.setVoornaam("Mieke");
        klant.setNaam("Defoort");
        klant.setAdres("Kerkstraat 1");
        klant.setPostcode("8000");
        klant.setGemeente("Brugge");
        klant.setStatus(KlantStatus.INGESCHREVEN);

        //klant toevoegen
        klant.setId(klantDAO.toevoegenKlant(klant));
    }

    private Rekening maakRekening(Rekeningnummer rekeningnummer, BigDecimal saldo, RekeningStatus status, int klantid) {

        Rekening rek = new Rekening();

        rek.setRekeningnummer(rekeningnummer);
        rek.setSaldo(saldo);
        rek.setStatus(status);
        rek.setEigenaar(klantid);

        return rek;
    }

    @After
    public void tearDown() throws Exception {
        // toegevoegde klant verwijderen uit DB
        Removals.removeKlant(klant.getId());
    }

    // rekening toevoegen
    @Test
    public void testToevoegenRekening() throws Exception {

        // toegevoegde klant (via setup)
        // rek maken voor klant
        Rekening rek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());

        try {
            // rek toevoegen
            rekeningDAO.toevoegenRekening(rek);
            // rek opnieuw ophalen
            Rekening ophaalRek = rekeningDAO.zoekRekening(
                rek.getRekeningnummer().toString());

            // toegevoegde rek vergelijken met opgehaalde rek
            assertThat(ophaalRek.getRekeningnummer().toString()).isEqualTo("BE24 1238 8888 8838");
            assertThat(ophaalRek.getSaldo()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            assertThat(ophaalRek.getEigenaar()).isEqualTo(klant.getId());
            assertThat(ophaalRek.getStatus()).isEqualTo(RekeningStatus.OPEN);
        } finally {
            // rekening weer verwijderen
            Removals.removeRekening(rek.getRekeningnummer().toString());
        }
    }

    // negatieve test: rekening toevoegen zonder rekeningnummer
    @Test
    public void testToevoegenRekeningZonderRekeningnummer() throws Exception {

        // toegevoegde klant (via setup)
        // rekening maken voor klant
        Rekening rek = maakRekening(null, BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());

        assertThatThrownBy(() -> {
            rekeningDAO.toevoegenRekening(rek);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());
    }

    // rekening toevoegen
    // geen status -> wordt status open (is voor elke nieuwe rekening zo)
    @Test
    public void testToevoegenRekeningZonderStatus() throws Exception {
        // toegevoegde klant (via setup)
        // rek maken voor klant
        Rekening rek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, null, klant.getId());

        try {
            // rek toevoegen
            rekeningDAO.toevoegenRekening(rek);
            // rek opnieuw ophalen
            Rekening ophaalRek = rekeningDAO.zoekRekening(rek.getRekeningnummer().toString());

            // toegevoegde rek vergelijken met opgehaalde rek
            assertThat(ophaalRek.getRekeningnummer().toString()).isEqualTo("BE24 1238 8888 8838");
            assertThat(ophaalRek.getSaldo()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            assertThat(ophaalRek.getEigenaar()).isEqualTo(klant.getId());
            assertThat(ophaalRek.getStatus()).isEqualTo(RekeningStatus.OPEN);
        } finally {
            // rekening weer verwijderen
            Removals.removeRekening(rek.getRekeningnummer().toString());
        }
    }

    // negatieve test: rekening toevoegen zonder eigenaar
    @Test
    public void testToevoegenRekeningOnbestaandeEigenaar() throws Exception {

        // toegevoegde klant (via setup)
        // rekening maken voor klant
        Rekening rek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.OPEN, ExtraQueries.getOngebruiktKlantID());

        assertThatThrownBy(() -> {
            rekeningDAO.toevoegenRekening(rek);
        }).isInstanceOf(DBException.class);
    }

    // rekening toevoegen
    // status gesloten -> wordt status open (is voor elke nieuwe rekening zo)
    @Test
    public void testToevoegenRekeningStatusGesloten() throws Exception {

        // toegevoegde klant (via setup)
        // rek maken voor klant
        Rekening rek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.GESLOTEN, klant.getId());

        try {
            // rek toevoegen
            rekeningDAO.toevoegenRekening(rek);
            // rek opnieuw ophalen
            Rekening ophaalRek = rekeningDAO.zoekRekening(rek.getRekeningnummer().toString());

            // toegevoegde rek vergelijken met opgehaalde rek
            assertThat(ophaalRek.getRekeningnummer().toString()).isEqualTo("BE24 1238 8888 8838");
            assertThat(ophaalRek.getSaldo()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            assertThat(ophaalRek.getEigenaar()).isEqualTo(klant.getId());
            assertThat(ophaalRek.getStatus()).isEqualTo(RekeningStatus.OPEN);
        } finally {
            // rekening weer verwijderen
            Removals.removeRekening(rek.getRekeningnummer().toString());
        }
    }

    // negatieve test: rekening toevoegen met zelfde rekeningnummer als reeds bestaade rekening
    @Test
    public void testToevoegenRekeningZelfdeRekNr() throws Exception {

        // toegevoegde klant (via setup)
        // rek maken voor klant
        Rekening rek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());

        try {
            // rek toevoegen
            rekeningDAO.toevoegenRekening(rek);
            // tweede rek maken, met zelfde rekeningnummer
            Rekening nieuweRek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());

            assertThatThrownBy(() -> {
                rekeningDAO.toevoegenRekening(rek);
            }).isInstanceOf(DBException.class);
        } finally {
            // rekening die wel toegevoegd werd, eerst verwijderen vooraleer
            // methode te stoppen
            Removals.removeRekening("BE24 1238 8888 8838");
        }
    }

    // saldo rekening wijzigen
    @Test
    public void testWijzigenRekeningSaldo() throws Exception {

        // toegevoegde klant (via setup)
        // rek maken voor klant
        String reknr = "BE24 1238 8888 8838";
        Rekening rek = maakRekening(new Rekeningnummer(reknr), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());

        try {
            // rek toevoegen
            rekeningDAO.toevoegenRekening(rek);
            // saldo wijzigen
            rekeningDAO.wijzigenSaldoRekening(reknr, new BigDecimal(20));

            // rek opnieuw ophalen
            Rekening ophaalRek = rekeningDAO.zoekRekening(rek.getRekeningnummer().toString());

            // toegevoegde rek vergelijken met opgehaalde rek
            assertThat(ophaalRek.getRekeningnummer().toString()).isEqualTo("BE24 1238 8888 8838");
            assertThat(ophaalRek.getSaldo()).isEqualTo(new BigDecimal(20).setScale(2, RoundingMode.HALF_UP));
            assertThat(ophaalRek.getEigenaar()).isEqualTo(klant.getId());
            assertThat(ophaalRek.getStatus()).isEqualTo(RekeningStatus.OPEN);
        } finally {
            // rekening weer verwijderen
            Removals.removeRekening("BE24 1238 8888 8838");
        }
    }

    // open rekening afsluiten
    @Test
    public void testVerwijderenRekeningOpenNaarGesloten() throws Exception {

        // toegevoegde klant (via setup)
        // rek maken voor klant
        Rekening rek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());

        try {
            // rek toevoegen
            rekeningDAO.toevoegenRekening(rek);
            // rek verwijderen
            rekeningDAO.verwijderRekening("BE24 1238 8888 8838");
            // rek opnieuw ophalen
            Rekening ophaalRek = rekeningDAO.zoekRekening("BE24 1238 8888 8838");

            // toegevoegde rek vergelijken met opgehaalde rek
            assertThat(ophaalRek.getRekeningnummer().toString()).isEqualTo("BE24 1238 8888 8838");
            assertThat(ophaalRek.getSaldo()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            assertThat(ophaalRek.getEigenaar()).isEqualTo(klant.getId());
            assertThat(ophaalRek.getStatus()).isEqualTo(RekeningStatus.GESLOTEN);
        } finally {
            Removals.removeRekening("BE24 1238 8888 8838");
        }
    }

    //rekening sluiten, maar ongeldig rekeningnummer opgegeven
    // geen effect, want rekening wordt niet gevonden
    @Test
    public void testVerwijderenRekeningOngeldigRekeningnummer() throws Exception {

        // toegevoegde klant (via setup)
        // rek maken voor klant
        Rekening rek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());

        try {
            // rek toevoegen
            rekeningDAO.toevoegenRekening(rek);
            // rek verwijderen met ongeldig rekeningnummer
            rekeningDAO.verwijderRekening("");
            // rek opnieuw ophalen
            Rekening ophaalRek = rekeningDAO.zoekRekening("BE24 1238 8888 8838");

            // rekening is niet gesloten, want niet gevonden
            assertThat(ophaalRek.getRekeningnummer().toString()).isEqualTo("BE24 1238 8888 8838");
            assertThat(ophaalRek.getSaldo()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            assertThat(ophaalRek.getEigenaar()).isEqualTo(klant.getId());
            assertThat(ophaalRek.getStatus()).isEqualTo(RekeningStatus.OPEN);
        } finally {
            Removals.removeRekening("BE24 1238 8888 8838");
        }
    }

    //rekening sluiten, maar geen rekeningnummer opgegeven
    // geen effect, want rekening wordt niet gevonden
    @Test
    public void testVerwijderenRekeningGeenRekeningnummer() throws Exception {

        // toegevoegde klant (via setup)
        // rek maken voor klant
        Rekening rek = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());

        try {
            // rek toevoegen
            rekeningDAO.toevoegenRekening(rek);
            // rek verwijderen met ongeldig rekeningnummer
            rekeningDAO.verwijderRekening(null);
            // rek opnieuw ophalen
            Rekening ophaalRek = rekeningDAO.zoekRekening("BE24 1238 8888 8838");

            // rekening is niet gesloten, want niet gevonden
            assertThat(ophaalRek.getRekeningnummer().toString()).isEqualTo("BE24 1238 8888 8838");
            assertThat(ophaalRek.getSaldo()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            assertThat(ophaalRek.getEigenaar()).isEqualTo(klant.getId());
            assertThat(ophaalRek.getStatus()).isEqualTo(RekeningStatus.OPEN);
        } finally {
            Removals.removeRekening("BE24 1238 8888 8838");
        }
    }

    /**
     * Methode om be.vives.extra klanten en rekeningen toe te voegen (naast de setup)
     */
    private ArrayList<Rekening> extraRekeningenToevoegen() throws Exception {
        Rekening rek1;
        Rekening rek2;
        Rekening rek3;
        Rekening rek4;
        Rekening rek5;

        klant2 = new Klant();
        klant3 = new Klant();

        // klant2 maken
        klant2.setVoornaam("Ann");
        klant2.setNaam("Depoorter");
        klant2.setAdres("Statiestraat 12");
        klant2.setPostcode("8800");
        klant2.setGemeente("Roeselare");
        klant2.setStatus(KlantStatus.INGESCHREVEN);

        // klant3 maken
        klant3.setVoornaam("Peter");
        klant3.setNaam("Vandenbossche");
        klant3.setAdres("Tarwelaan 15");
        klant3.setPostcode("9000");
        klant3.setGemeente("Gent");
        klant3.setStatus(KlantStatus.INGESCHREVEN);

        // extra klanten toevoegen
        klant2.setId(klantDAO.toevoegenKlant(klant2));
        klant3.setId(klantDAO.toevoegenKlant(klant3));

        // rek toevoegen
        rek1 = maakRekening(new Rekeningnummer("BE24 1238 8888 8838"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());
        rekeningDAO.toevoegenRekening(rek1);

        rek2 = maakRekening(new Rekeningnummer("BE74 9871 1111 1107"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());
        rekeningDAO.toevoegenRekening(rek2);

        rek3 = maakRekening(new Rekeningnummer("BE33 1112 2222 2246"), BigDecimal.ZERO, RekeningStatus.OPEN, klant.getId());
        rekeningDAO.toevoegenRekening(rek3);

        rek4 = maakRekening(new Rekeningnummer("BE84 5555 5555 5559"), BigDecimal.ZERO, RekeningStatus.OPEN, klant2.getId());
        rekeningDAO.toevoegenRekening(rek4);

        rek5 = maakRekening(new Rekeningnummer("BE92 9639 6396 3923"), BigDecimal.ZERO, RekeningStatus.OPEN, klant2.getId());
        rekeningDAO.toevoegenRekening(rek5);

        // rekeningen 3 en 5 afsluiten
        rekeningDAO.verwijderRekening(rek3.getRekeningnummer().toString());
        rekeningDAO.verwijderRekening(rek5.getRekeningnummer().toString());

        ArrayList<Rekening> rekeningen = new ArrayList<>();
        rekeningen.add(rek1);
        rekeningen.add(rek2);
        rekeningen.add(rek3);
        rekeningen.add(rek4);
        rekeningen.add(rek5);

        return rekeningen;

    }

    @Test
    public void zoekAantalOpenRekeningenKlant() throws Exception {
        // reeds open rekeningen tellen
        int aantalRek = rekeningDAO.zoekAantalOpenRekeningen(klant.getId());

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekAantalOpenRekeningen(klant.getId())).isEqualTo(aantalRek + 2);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekAantalOpenRekeningenKlantOngeldig() throws Exception {

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekAantalOpenRekeningen(ExtraQueries.getOngebruiktKlantID())).isEqualTo(0);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekAlleRekeningenKlant() throws Exception {
        // reeds alle rekeningen tellen
        int aantalRek = rekeningDAO.zoekAlleRekeningen(klant.getId()).size();

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekAlleRekeningen(klant.getId()).size()).isEqualTo(aantalRek + 3);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekAlleRekeningenKlantOngeldig() throws Exception {

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekAlleRekeningen(ExtraQueries.getOngebruiktKlantID()).size()).isEqualTo(0);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekOpenRekeningenKlant() throws Exception {
        // reeds alle open rekeningen tellen
        int aantalRek = rekeningDAO.zoekOpenRekeningen(klant.getId()).size();

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekOpenRekeningen(klant.getId()).size()).isEqualTo(aantalRek + 2);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekOpenRekeningenKlantOngeldig() throws Exception {

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekOpenRekeningen(ExtraQueries.getOngebruiktKlantID()).size()).isEqualTo(0);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekGeslotenRekeningenKlant() throws Exception {
        // reeds alle gesloten rekeningen tellen
        int aantalRek = rekeningDAO.zoekGeslotenRekeningen(klant.getId()).size();

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekGeslotenRekeningen(klant.getId()).size()).isEqualTo(aantalRek + 1);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekGeslotenRekeningenKlantOngeldig() throws Exception {

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekGeslotenRekeningen(ExtraQueries.getOngebruiktKlantID()).size()).isEqualTo(0);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekRekening() throws Exception {

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {
            Rekening ophaalRek = rekeningDAO.zoekRekening("BE74 9871 1111 1107");

            // rek vergelijken met gevonden rek
            assertThat(ophaalRek.getRekeningnummer().toString()).isEqualTo("BE74 9871 1111 1107");
            assertThat(ophaalRek.getSaldo()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            assertThat(ophaalRek.getEigenaar()).isEqualTo(klant.getId());
            assertThat(ophaalRek.getStatus()).isEqualTo(RekeningStatus.OPEN);
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    // werkt enkel als BE93872268696898 inderdaad niet in DB zit.
    @Test
    public void zoekRekeningBestaatNiet() throws Exception {

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekRekening("BE93 8722 6869 6898")).isNull();
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    @Test
    public void zoekRekeningNull() throws Exception {

        // be.vives.extra klanten en rekeningen toevoegen
        ArrayList<Rekening> rekeningen = extraRekeningenToevoegen();

        try {

            assertThat(rekeningDAO.zoekRekening(null)).isNull();
        } finally {
            removeAlleExtras(rekeningen);
        }
    }

    private void removeAlleExtras(ArrayList<Rekening> rekeningen) throws Exception {

        //alle rekeningen verwijderen
        for (Rekening r : rekeningen) {
            Removals.removeRekening(r.getRekeningnummer().getRekeningnummer());
        }

        //extra klanten verwijderen
        Removals.removeKlant(klant2.getId());
        Removals.removeKlant(klant3.getId());
    }
}
