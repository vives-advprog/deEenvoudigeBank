package be.vives.ti.DAO;

import be.vives.ti.databag.Klant;
import be.vives.ti.datatype.KlantStatus;
import be.vives.ti.exception.DBException;
import be.vives.ti.extra.ExtraQueries;
import be.vives.ti.extra.Removals;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

public class KlantDAOTest {

    private KlantDAO klantDAO = new KlantDAO();

    private Klant maakKlant(String voornaam, String familienaam, String adres, String postcode, String gemeente, KlantStatus klantStatus) {
        Klant klant = new Klant();
        klant.setVoornaam(voornaam);
        klant.setNaam(familienaam);
        klant.setAdres(adres);
        klant.setPostcode(postcode);
        klant.setGemeente(gemeente);
        klant.setStatus(klantStatus);
        return klant;
    }

    // voornaam, naam, adres, postcode, gemeente, status
    // geen id
    @Test
    public void testToevoegenKlant() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);
        try {
            // geldige klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant opnieuw ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // toegevoegde klant vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // voornaam, naam, adres, postcode, gemeente
    // geen id
    // status uitgeschreven -> wordt ingeschreven: nieuwe klant is steeds ingeschreven
    //
    @Test
    public void testToevoegenKlantUitgeschreven() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.UITGESCHREVEN);

        try {
            // klant toevoegen met status uitgeschreven
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant opnieuw ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // toegevoegde klant vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // negatieve test: klant toevoegen zonder voornaam
    @Test
    public void testToevoegenKlantZonderVoornaam() throws Exception {

        Klant klant = maakKlant(null, "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantDAO.toevoegenKlant(klant);
        }).isInstanceOf(DBException.class);
    }

    // negatieve test: klant toevoegen zonder naam
    @Test
    public void testToevoegenKlantZonderNaam() throws Exception {
        Klant klant = maakKlant("Mieke", null, "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantDAO.toevoegenKlant(klant);
        }).isInstanceOf(DBException.class);
    }

    // negatieve test: klant toevoegen zonder adres
    @Test
    public void testToevoegenKlantZonderAdres() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", null, "8000", "Brugge", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantDAO.toevoegenKlant(klant);
        }).isInstanceOf(DBException.class);
    }

    // negatieve test: klant toevoegen zonder postocde
    @Test
    public void testToevoegenKlantZonderPostcode() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", null, "Brugge", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantDAO.toevoegenKlant(klant);
        }).isInstanceOf(DBException.class);
    }

    // negatieve test: klant toevoegen zonder gemeente
    @Test
    public void testToevoegenKlantZonderGemeente() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", null, KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantDAO.toevoegenKlant(klant);
        }).isInstanceOf(DBException.class);
    }

    // voornaam, naam, adres, postcode, gemeente
    // geen id
    // geen status -> wordt ingeschreven: nieuwe klant is steeds ingeschreven
    //
    @Test
    public void testToevoegenKlantGeenStatus() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", null);

        try {
            // klant toevoegen met status null
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant opnieuw ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // toegevoegde klant vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // geen klant (geen effect)
    @Test
    public void testToevoegenKlantNull() throws Exception {

        // geen klant

        Integer klantID = klantDAO.toevoegenKlant(null);

        // is er niets toegevoegd (geen effect)
        assertThat(klantID).isNull();
    }

    // voornaam klant wijzigen
    @Test
    public void testWijzigenKlantVoornaam() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant wijzigen (nieuwe voornaam)
            klant.setVoornaam("Ann");
            klantDAO.wijzigenKlant(klant);
            //gewijzigde klant ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // wijzigingen vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Ann");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    //  naam klant wijzigen

    @Test
    public void testWijzigenKlantNaam() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant wijzigen (nieuwe naam)
            klant.setNaam("Depoorter");
            klantDAO.wijzigenKlant(klant);
            //gewijzigde klant ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // wijzigingen vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Depoorter");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // adres klant wijzigen

    @Test
    public void testWijzigenKlantAdres() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant wijzigen (nieuw adres)
            klant.setAdres("Kerkweg 1");
            klantDAO.wijzigenKlant(klant);
            //gewijzigde klant ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // wijzigingen vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkweg 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            Assertions.assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // postcode klant wijzigen

    @Test
    public void testWijzigenKlantPostcode() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant wijzigen (nieuwe postcode)
            klant.setPostcode("8800");
            klantDAO.wijzigenKlant(klant);
            //gewijzigde klant ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // wijzigingen vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8800");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            Assertions.assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // gemeente klant wijzigen

    @Test
    public void testWijzigenKlantGemeente() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {

            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant wijzigen (nieuwe gemeente)
            klant.setGemeente("Roeselare");
            klantDAO.wijzigenKlant(klant);
            //gewijzigde klant ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // wijzigingen vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Roeselare");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    //negative test: klant wijzigen, geen voornaam opgegeven
    @Test
    public void testWijzigenKlantZonderVoornaam() throws Exception {
        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // voornaam leeg maken
            klant.setVoornaam(null);

            assertThatThrownBy(() -> {
                klantDAO.wijzigenKlant(klant);
            }).isInstanceOf(DBException.class);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // negatieve test: klant wijzigen, maar geen naam opgegeven
    @Test
    public void testWijzigenKlantZonderNaam() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // naam leeg maken
            klant.setNaam(null);

            assertThatThrownBy(() -> {
                klantDAO.wijzigenKlant(klant);
            }).isInstanceOf(DBException.class);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // negatieve test: klant wijzigen, maar geen adres opgegeven
    @Test
    public void testWijzigenKlantZonderAdres() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // adres leeg maken
            klant.setAdres(null);

            assertThatThrownBy(() -> {
                klantDAO.wijzigenKlant(klant);
            }).isInstanceOf(DBException.class);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // negatieve test: klant wijzigen, maar geen postcode opgegeven
    @Test
    public void testWijzigenKlantZonderPostcode() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // postcode leeg maken
            klant.setPostcode(null);

            assertThatThrownBy(() -> {
                klantDAO.wijzigenKlant(klant);
            }).isInstanceOf(DBException.class);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // negatieve test: klant wijzigen, maar geen gemeente opgegeven
    @Test
    public void testWijzigenKlantZonderGemeente() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // gemeente leeg maken
            klant.setGemeente(null);

            assertThatThrownBy(() -> {
                klantDAO.wijzigenKlant(klant);
            }).isInstanceOf(DBException.class);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    // negatieve test: klant zonder id proberen te wijzigen
    @Test
    public void testWijzigenKlantZonderId() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        assertThatCode(() -> klantDAO.wijzigenKlant(klant)).doesNotThrowAnyException();
    }

    //klant verwijderen (status wijzigt)
    @Test
    public void testVerwijderenKlantIngeschrevenNaarUitgeschreven() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // klant verwijderen
            klantDAO.verwijderKlant(klant.getId());
            // verwijderde klant ophalen
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // verwijderde klant vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.UITGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }

    }

    // klant verwijderen met ongeldig id
    // geen effect, klant wordt niet vonden en is dus niet uitgeschreven
    @Test
    public void testVerwijderenKlantOngeldigId() throws Exception {

        Klant klant = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);

        try {
            // klant toevoegen
            klant.setId(klantDAO.toevoegenKlant(klant));
            // id zoeken dat nog niet in DB zit.
            // klant verwijderen met ongeldig id
            klantDAO.verwijderKlant(ExtraQueries.getOngebruiktKlantID());
            // klant ophalen met ongebruikt id
            Klant ophaalKlant = klantDAO.zoekKlant(klant.getId());

            // klant is niet uitgeschreven, want werd niet gevonden om uit te schrijven
            assertThat(ophaalKlant.getId()).isEqualTo(klant.getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            // klant weer verwijderen
            Removals.removeKlant(klant.getId());
        }
    }

    /**
     * Methode om be.vives.extra klanten toe te voegen (naast de setup)
     */
    private ArrayList<Klant> extraKlantenToevoegen() throws Exception {
        Klant klant1 = maakKlant("Mieke", "Defoort", "Kerkstraat 1", "8000", "Brugge", KlantStatus.INGESCHREVEN);
        Klant klant2 = maakKlant("Ann", "Depoorter", "Statiestraat 12", "8800", "Roeselare", KlantStatus.INGESCHREVEN);
        Klant klant3 = maakKlant("Peter", "Vandenbossche", "Tarwelaan 15", "9000", "Gent", KlantStatus.INGESCHREVEN);
        Klant klant4 = maakKlant("Jan", "Vandewalle", "Ieperstraat 120", "8000", "Brugge", KlantStatus.INGESCHREVEN);
        Klant klant5 = maakKlant("Bart", "Maddens", "Bruggestraat 189", "8900", "Ieper", KlantStatus.INGESCHREVEN);

        // klanten toevoegen
        klant1.setId(klantDAO.toevoegenKlant(klant1));
        klant2.setId(klantDAO.toevoegenKlant(klant2));
        klant3.setId(klantDAO.toevoegenKlant(klant3));
        klant4.setId(klantDAO.toevoegenKlant(klant4));
        klant5.setId(klantDAO.toevoegenKlant(klant5));

        // uitschrijven klant 3 en klant 5
        klantDAO.verwijderKlant(klant3.getId());
        klantDAO.verwijderKlant(klant5.getId());

        ArrayList<Klant> klanten = new ArrayList<>();
        klanten.add(klant1);
        klanten.add(klant2);
        klanten.add(klant3);
        klanten.add(klant4);
        klanten.add(klant5);

        return klanten;
    }

    @Test
    public void zoekAlleKlanten() throws Exception {
        // reeds aanwezige klanten tellen
        int aantalKlanten = klantDAO.zoekAlleKlanten().size();

        // extra klanten toevoegen
        ArrayList<Klant> klanten = extraKlantenToevoegen();

        try {
            // alle klanten zoeken
            ArrayList<Klant> gevondenKlanten = klantDAO.zoekAlleKlanten();

            assertThat(gevondenKlanten.size()).isEqualTo(aantalKlanten + 5);
        } finally {
            removeTestklanten(klanten);
        }
    }

    @Test
    public void zoekIngeschrevenKlanten() throws Exception {
        // reeds ingeschreven klanten tellen
        int aantalKlanten = klantDAO.zoekIngeschrevenKlanten().size();

        // extra klanten toevoegen
        ArrayList<Klant> klanten = extraKlantenToevoegen();

        try {
            // alle ingeschreven klanten zoeken
            ArrayList<Klant> gevondenKlanten = klantDAO.zoekIngeschrevenKlanten();

            assertThat(gevondenKlanten.size()).isEqualTo(aantalKlanten + 3);
        } finally {
            removeTestklanten(klanten);
        }
    }

    @Test
    public void zoekUitgeschrevenKlanten() throws Exception {
        // reeds uitgeschreven klanten tellen
        int aantalKlanten = klantDAO.zoekUitgeschrevenKlanten().size();

        // extra klanten toevoegen
        ArrayList<Klant> klanten = extraKlantenToevoegen();

        try {
            // alle uitgeschreven klanten zoeken
            ArrayList<Klant> gevondenKlanten = klantDAO.zoekUitgeschrevenKlanten();

            assertThat(gevondenKlanten.size()).isEqualTo(aantalKlanten + 2);
        } finally {
            removeTestklanten(klanten);
        }
    }

    // zoek een klant op basis van id
    @Test
    public void zoekKlant() throws Exception {

        // extra klanten toevoegen
        ArrayList<Klant> klanten = extraKlantenToevoegen();

        try {
            Klant ophaalKlant = klantDAO.zoekKlant(klanten.get(0).getId());

            // toegevoegde klant vergelijken met opgehaalde klant
            assertThat(ophaalKlant.getId()).isEqualTo(klanten.get(0).getId());
            assertThat(ophaalKlant.getVoornaam()).isEqualTo("Mieke");
            assertThat(ophaalKlant.getNaam()).isEqualTo("Defoort");
            assertThat(ophaalKlant.getAdres()).isEqualTo("Kerkstraat 1");
            assertThat(ophaalKlant.getPostcode()).isEqualTo("8000");
            assertThat(ophaalKlant.getGemeente()).isEqualTo("Brugge");
            assertThat(ophaalKlant.getStatus()).isEqualTo(KlantStatus.INGESCHREVEN);
        } finally {
            removeTestklanten(klanten);
        }
    }

    // zoek een klant op basis van id, id is null
    @Test
    public void zoekKlantIDNull() throws Exception {

        // extra klanten toevoegen
        ArrayList<Klant> klanten = extraKlantenToevoegen();

        try {
            // klant zoeken met id null
            Klant ophaalKlant = klantDAO.zoekKlant(null);

            // toegevoegde klant vergelijken met opgehaalde klant
            assertThat(ophaalKlant).isNull();
        } finally {
            removeTestklanten(klanten);
        }
    }

    // zoek een klant op basis van naam, voornaam en adres
    @Test
    public void bestaatKlant() throws Exception {

        // extra klanten toevoegen
        ArrayList<Klant> klanten = extraKlantenToevoegen();

        try {
            boolean bestaatKlant = klantDAO.bestaatKlant(klanten.get(0));

            // toegevoegde klant vergelijken met opgehaalde klant
            assertThat(bestaatKlant).isTrue();
        } finally {
            removeTestklanten(klanten);
        }
    }

    // zoek een klant op basis van naam, voornaam en adres. Klant is null
    @Test
    public void bestaatKlantNull() throws Exception {

        // extra klanten toevoegen
        ArrayList<Klant> klanten = extraKlantenToevoegen();

        try {
            // klant is null
            boolean bestaatKlant = klantDAO.bestaatKlant(null);

            // gezochte klant niet gevonden
            assertThat(bestaatKlant).isFalse();
        } finally {
            removeTestklanten(klanten);
        }
    }

    private void removeTestklanten(ArrayList<Klant> klanten) throws Exception {
        for (Klant k : klanten) {
            Removals.removeKlant(k.getId());
        }

    }

}
