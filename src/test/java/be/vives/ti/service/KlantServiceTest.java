package be.vives.ti.service;

import be.vives.ti.DAO.KlantDAO;
import be.vives.ti.databag.Klant;
import be.vives.ti.datatype.KlantStatus;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KlantServiceTest {

    private KlantService klantService;
    private KlantDAO klantDAO;
    private RekeningService rekeningService;


    public KlantServiceTest() {
        // simulatieobject maken voor KlantDAO en RekeningService.
        this.klantDAO = mock(KlantDAO.class);
        this.rekeningService = mock(RekeningService.class);

        this.klantService = new KlantService(klantDAO, rekeningService);
    }

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

    @Test
    public void toevoegenKlantNull() throws Exception {
        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_NULL.getMessage());

        verify(klantDAO, never()).toevoegenKlant(null);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_GeenVoornaam() throws Exception {
        Klant klant = maakKlant(null, "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_VOORNAAM_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_GeenNaam() throws Exception {
        Klant klant = maakKlant("Jan", null, "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_NAAM_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_GeenAdres() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", null, "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_ADRES_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_GeenPostcode() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", null, "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_POSTCODE_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_GeenGemeente() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", null, KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_GEMEENTE_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_WelStatus() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_MOET_INGESCHREVEN_ZIJN.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_StatusUitgeschreven() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.UITGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_MOET_INGESCHREVEN_ZIJN.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_VoornaamLeeg() throws Exception {
        Klant klant = maakKlant(" ", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_VOORNAAM_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_NaamLeeg() throws Exception {
        Klant klant = maakKlant("Jan", "", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_NAAM_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_AdresLeeg() throws Exception {
        Klant klant = maakKlant("Jan", "Jansenss", "", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_ADRES_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_PostcodeLeeg() throws Exception {
        Klant klant = maakKlant("Jan", "Jansenss", "Dorpsplein 120", "", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_POSTCODE_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_GemeenteLeeg() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_GEMEENTE_LEEG.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_AlleVeldenIngevuld_KlantMetId() throws Exception {

        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", null);
        klant.setId(123);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_ID_WORDT_GEGENEREERD.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    @Test
    public void toevoegenKlant_bestaatAl() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", null);

        when(klantDAO.bestaatKlant(klant)).thenReturn(true);

        assertThatThrownBy(() -> {
            klantService.toevoegenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_BESTAAT_AL.getMessage());

        verify(klantDAO, never()).toevoegenKlant(klant);
    }

    //positieve test
    @Test
    public void toevoegenKlant_succesvol() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", null);

        int klantId = 8;
        when(klantDAO.bestaatKlant(klant)).thenReturn(false);
        when(klantDAO.toevoegenKlant(klant)).thenReturn(klantId);

        Integer returnedKlantId = klantService.toevoegenKlant(klant);

        assertThat(returnedKlantId).isEqualTo(klantId);
    }

    //positieve test
    @Test
    public void verwijderKlant_succesvol() throws Exception {
        int klantId = 123;

        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);
        klant.setId(klantId);

        when(klantDAO.zoekKlant(klantId)).thenReturn(klant);
        when(rekeningService.zoekAantalOpenRekeningen(klantId)).thenReturn(0);

        assertThatCode(() -> {
            klantService.verwijderKlant(klantId);
        }).doesNotThrowAnyException();

        verify(klantDAO).verwijderKlant(klantId);
    }

    @Test
    public void verwijderKlant_BestaatNiet() throws Exception {
        int klantId = 123;

        when(klantDAO.zoekKlant(klantId)).thenReturn(null);

        assertThatThrownBy(() -> {
            klantService.verwijderKlant(klantId);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_BESTAAT_NIET.getMessage());

        verify(klantDAO, never()).verwijderKlant(klantId);

    }

    @Test
    public void verwijderKlant_isUitgeschreven() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.UITGESCHREVEN);
        int klantId = 123;
        klant.setId(klantId);
        when(klantDAO.zoekKlant(klantId)).thenReturn(klant);

        assertThatThrownBy(() -> {
            klantService.verwijderKlant(klantId);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_UITGESCHREVEN.getMessage());

        verify(klantDAO, never()).verwijderKlant(klantId);
    }

    @Test
    public void verwijderKlant_heeftNogOpenstaandeRekeningen() throws Exception {
        int klantId = 123;
        //wanneer er gezocht wordt naar klant met id 123, return dan een nieuwe klant
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);
        klant.setId(klantId);

        when(klantDAO.zoekKlant(klantId)).thenReturn(klant);
        when(rekeningService.zoekAantalOpenRekeningen(klantId)).thenReturn(4);

        assertThatThrownBy(() -> {
            klantService.verwijderKlant(klantId);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_HEEFT_NOG_REKENINGEN.getMessage());

        verify(klantDAO, never()).verwijderKlant(klantId);
    }

    @Test
    public void wijzigKlant_klantIsNull() throws Exception {
        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_NULL.getMessage());

        verify(klantDAO, never()).wijzigenKlant(null);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_VoornaamLeeg() throws Exception {
        Klant klant = maakKlant(" ", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_VOORNAAM_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_NaamLeeg() throws Exception {
        Klant klant = maakKlant("Jan", "", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_NAAM_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_AdresLeeg() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", " ", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_ADRES_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_PostcodeLeeg() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", " ", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_POSTCODE_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_GemeenteLeeg() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", " ", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_GEMEENTE_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_StatusUitgeschreven() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.UITGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_MOET_INGESCHREVEN_ZIJN.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_GeenVoornaam() throws Exception {
        Klant klant = maakKlant(null, "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_VOORNAAM_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_GeenNaam() throws Exception {
        Klant klant = maakKlant("Jan", null, "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_NAAM_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_GeenAdres() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", null, "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_ADRES_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_GeenPostcode() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", null, "Kortrijk", KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_POSTCODE_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_GeenGemeente() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", null, KlantStatus.INGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_GEMEENTE_LEEG.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_AlleVeldenIngevuld_WelStatus() throws Exception {
        Klant klant = maakKlant("Jan", "Janssens", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.UITGESCHREVEN);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_MOET_INGESCHREVEN_ZIJN.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigUitgeschrevenKlant() throws Exception {
        int klantId = 123;
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", null);
        klant.setId(klantId);

        Klant klantUitDb = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.UITGESCHREVEN);

        when(klantDAO.zoekKlant(klantId)).thenReturn(klantUitDb);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_UITGESCHREVEN.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_klantBestaatNogNiet() throws Exception {
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", null);
        klant.setId(123);

        when(klantDAO.zoekKlant(123)).thenReturn(null);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_BESTAAT_NIET.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    @Test
    public void wijzigKlant_erBestaatReedsKlantMetNieuweGegegevens() throws Exception {
        int klantId = 123;
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", null);
        klant.setId(klantId);

        Klant klantUitDB = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);


        when(klantDAO.zoekKlant(klantId)).thenReturn(klantUitDB);

        when(klantDAO.bestaatKlant(klant)).thenReturn(true);

        assertThatThrownBy(() -> {
            klantService.wijzigenKlant(klant);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_BESTAAT_AL.getMessage());

        verify(klantDAO, never()).wijzigenKlant(klant);
    }

    //positieve test
    @Test
    public void wijzigKlant_successvol() throws Exception {
        Klant klantInDb = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);
        int id = 123;
        klantInDb.setId(id);

        Klant klantWijzigen = maakKlant("Jantje", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", null);
        klantWijzigen.setId(id);
        when(klantDAO.zoekKlant(id)).thenReturn(klantInDb);
        when(klantDAO.bestaatKlant(klantWijzigen)).thenReturn(false);

        assertThatCode(() -> {
            klantService.wijzigenKlant(klantWijzigen);
        }).doesNotThrowAnyException();

        verify(klantDAO).wijzigenKlant(klantWijzigen);
    }

    //positieve test
    @Test
    public void bestaatKlant() throws Exception {
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);
        klant.setId(123);

        when(klantDAO.bestaatKlant(klant)).thenReturn(true);

        assertThat(klantService.bestaatKlant(klant)).isTrue();
    }

    //positieve test
    @Test
    public void bestaatKlantNietGevonden() throws Exception {
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);
        klant.setId(123);

        when(klantDAO.bestaatKlant(klant)).thenReturn(false);

        assertThat(klantService.bestaatKlant(klant)).isFalse();
    }

    @Test
    public void bestaatKlant_null() throws Exception {
        assertThatThrownBy(() -> {
            klantService.bestaatKlant(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_NULL.getMessage());

        verify(klantDAO, never()).bestaatKlant(null);
    }

    @Test
    public void zoekKlant_null() throws Exception {
        assertThatThrownBy(() -> {
            klantService.zoekKlant(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_ID.getMessage());

        verify(klantDAO, never()).zoekKlant(null);
    }

    //positieve test
    @Test
    public void zoekKlant() throws Exception {
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);
        int klantId = 123;
        klant.setId(klantId);

        when(klantDAO.zoekKlant(klantId)).thenReturn(klant);

        assertThat(klantService.zoekKlant(klantId)).isEqualTo(klant);

    }

    @Test
    public void valideerKlant_klantBestaatNiet() throws Exception {
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);

        int id = 123;
        // klant.setId(id);

        when(klantDAO.zoekKlant(id)).thenReturn(null);

        assertThatThrownBy(() -> {
            klantService.valideerKlant(id);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_BESTAAT_NIET.getMessage());
    }

    @Test
    public void valideerKlant_klantAlUitgeschreven() throws Exception {
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.UITGESCHREVEN);
        int id = 123;

        when(klantDAO.zoekKlant(id)).thenReturn(klant);

        assertThatThrownBy(() -> {
            klantService.valideerKlant(id);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.KLANT_UITGESCHREVEN.getMessage());
    }

    //positieve test
    @Test
    public void valideerKlant_succesvol() throws Exception {
        Klant klant = maakKlant("Jan", "Janssen", "Dorpsplein 120", "8500", "Kortrijk", KlantStatus.INGESCHREVEN);
        int klantId = 123;
        klant.setId(klantId);

        when(klantDAO.zoekKlant(klantId)).thenReturn(klant);

        assertThatCode(() -> {
            klantService.valideerKlant(klantId);
        }).doesNotThrowAnyException();
    }

    //positieve test
    @Test
    public void zoekIngeschrevenKlanten() throws Exception {
        // 3 klanten maken
        ArrayList<Klant> klanten = new ArrayList<>();
        Klant kl1 = new Klant();
        klanten.add(kl1);
        Klant kl2 = new Klant();
        klanten.add(kl2);
        Klant kl3 = new Klant();
        // 3 klanten in lijst
        klanten.add(kl3);

        when(klantDAO.zoekIngeschrevenKlanten()).thenReturn(klanten);

        assertThat(klantService.zoekIngeschrevenKlanten()).containsExactly(kl1, kl2, kl3);
    }

}