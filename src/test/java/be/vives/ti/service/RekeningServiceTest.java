package be.vives.ti.service;

import be.vives.ti.DAO.RekeningDAO;
import be.vives.ti.databag.Rekening;
import be.vives.ti.datatype.RekeningStatus;
import be.vives.ti.datatype.Rekeningnummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RekeningServiceTest {

    private RekeningService rekeningService;
    private RekeningDAO rekeningDAO;
    private final String rekeningNummer;

    public RekeningServiceTest() {
        // simulatieobject maken voor RekeningDAO
        this.rekeningDAO = mock(RekeningDAO.class);
        this.rekeningService = new RekeningService(rekeningDAO);

        this.rekeningNummer = "BE62 0016 6836 7361";
    }

    private Rekening maakRekening(BigDecimal saldo, RekeningStatus status, int klantid) throws ApplicationException {
        return maakRekening(new Rekeningnummer(this.rekeningNummer), saldo, status, klantid);
    }

    private Rekening maakRekening(Rekeningnummer rekeningnummer, BigDecimal saldo, RekeningStatus status, int klantid) {
        Rekening rek = new Rekening();

        rek.setRekeningnummer(rekeningnummer);
        rek.setSaldo(saldo);
        rek.setStatus(status);
        rek.setEigenaar(klantid);

        return rek;
    }

    @Test
    public void verwijderRekening_reknummer_null() throws Exception {
        assertThatThrownBy(() -> {
            rekeningService.verwijderRekening(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).verwijderRekening(null);

    }

    @Test
    public void verwijderRekening_reknummer_emptyString() throws Exception {
        assertThatThrownBy(() -> {
            rekeningService.verwijderRekening("");
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).verwijderRekening("");
    }

    @Test
    public void verwijderRekening_reknummer_nietGevonden() throws Exception {
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(null);

        assertThatThrownBy(() -> {
            rekeningService.verwijderRekening(rekeningNummer);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BESTAAT_NIET.getMessage());

        verify(rekeningDAO, never()).verwijderRekening(rekeningNummer);
    }

    @Test
    public void verwijderRekening_reedsGeslotenRekening() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, RekeningStatus.GESLOTEN, 123);

        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);

        assertThatThrownBy(() -> {
            rekeningService.verwijderRekening(rekeningNummer);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_IS_GESLOTEN.getMessage());

        verify(rekeningDAO, never()).verwijderRekening(rekeningNummer);
    }

    @Test
    public void verwijderRekening_saldoNietZero() throws Exception {
        Rekening rekening = maakRekening(new BigDecimal(421.10), RekeningStatus.OPEN, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);
        assertThatThrownBy(() -> {
            rekeningService.verwijderRekening(rekeningNummer);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_SALDO_MOET_NUL_ZIJN.getMessage());

        verify(rekeningDAO, never()).verwijderRekening(rekeningNummer);
    }

    //positieve test
    @Test
    public void verwijderRekening_succesvol() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, RekeningStatus.OPEN, 123);

        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);

        assertThatCode(() -> {
            rekeningService.verwijderRekening(rekeningNummer);
        }).doesNotThrowAnyException();

        verify(rekeningDAO).verwijderRekening(rekeningNummer);
    }

    @Test
    public void stortenRekening_reknummer_null() throws Exception {

        assertThatThrownBy(() -> {
            rekeningService.stortenRekening(null, BigDecimal.TEN);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(anyString(), any(BigDecimal.class));
    }

    @Test
    public void stortenRekening_reknummer_emptyString() throws Exception {
        assertThatThrownBy(() -> {
            rekeningService.stortenRekening("", BigDecimal.TEN);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(anyString(), any(BigDecimal.class));
    }

    @Test
    public void stortenRekening_reknummer_nietGevonden() throws Exception {
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(null);
        assertThatThrownBy(() -> {
            rekeningService.stortenRekening(rekeningNummer, BigDecimal.TEN);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BESTAAT_NIET.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(anyString(), any(BigDecimal.class));
    }

    @Test
    public void stortenRekening_reedsGeslotenRekening() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, RekeningStatus.GESLOTEN, 123);

        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);
        assertThatThrownBy(() -> {
            rekeningService.stortenRekening(rekeningNummer, BigDecimal.TEN);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_IS_GESLOTEN.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(rekeningNummer, BigDecimal.TEN);
    }

    @Test
    public void stortenRekening_bedragNull() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, RekeningStatus.OPEN, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);
        assertThatThrownBy(() -> {
            rekeningService.stortenRekening(rekeningNummer, null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BEDRAG_LEEG.getMessage());
        verify(rekeningDAO, never()).wijzigenSaldoRekening(rekeningNummer, BigDecimal.ZERO);
    }

    @Test
    public void stortenRekening_bedragNegatief() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, RekeningStatus.OPEN, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);
        assertThatThrownBy(() -> {
            rekeningService.stortenRekening(rekeningNummer, new BigDecimal(-412.45));
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BEDRAG_MOET_POS_ZIJN.getMessage());
        verify(rekeningDAO, never()).wijzigenSaldoRekening(rekeningNummer, new BigDecimal(-412.45));
    }

    //positieve test
    @Test
    public void stortenRekening_succesvol() throws Exception {
        Rekening rekening = maakRekening(new BigDecimal(1452.88), RekeningStatus.OPEN, 123);

        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);

        assertThatCode(() -> {
            rekeningService.stortenRekening(rekeningNummer, BigDecimal.valueOf(300.21));
        }).doesNotThrowAnyException();

        verify(rekeningDAO).wijzigenSaldoRekening(rekeningNummer, BigDecimal.valueOf(1753.09));
    }

    @Test
    public void opnemenRekening_reknummer_null() throws Exception {
        assertThatThrownBy(() -> {
            rekeningService.opnemenRekening(null, BigDecimal.TEN);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(anyString(), any(BigDecimal.class));
    }

    @Test
    public void opnemenRekening_reknummer_emptyString() throws Exception {
        assertThatThrownBy(() -> {
            rekeningService.opnemenRekening("", BigDecimal.TEN);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(anyString(), any(BigDecimal.class));
    }

    @Test
    public void opnemenRekening_reknummer_nietGevonden() throws Exception {
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(null);
        assertThatThrownBy(() -> {
            rekeningService.opnemenRekening(rekeningNummer, BigDecimal.TEN);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BESTAAT_NIET.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(anyString(), any(BigDecimal.class));
    }

    @Test
    public void opnemenRekening_reedsGeslotenRekening() throws Exception {
        Rekening rekening = maakRekening(new BigDecimal(100.00), RekeningStatus.GESLOTEN, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);
        assertThatThrownBy(() -> {
            rekeningService.opnemenRekening(rekeningNummer, BigDecimal.TEN);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_IS_GESLOTEN.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(rekeningNummer, new BigDecimal(90.00));
    }

    @Test
    public void opnemenRekening_bedragNull() throws Exception {
        Rekening rekening = maakRekening(new BigDecimal(100.00), RekeningStatus.OPEN, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);
        assertThatThrownBy(() -> {
            rekeningService.opnemenRekening(rekeningNummer, null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BEDRAG_LEEG.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(rekeningNummer, new BigDecimal(100.00));
    }

    @Test
    public void opnemenRekening_bedragNegatief() throws Exception {
        Rekening rekening = maakRekening(new BigDecimal(100.00), RekeningStatus.OPEN, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);
        assertThatThrownBy(() -> {
            rekeningService.opnemenRekening(rekeningNummer, new BigDecimal(-412.45));
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BEDRAG_MOET_POS_ZIJN.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(rekeningNummer, new BigDecimal(512.45));
    }

    @Test
    public void opnemenRekening_bedragTeGroot() throws Exception {
        Rekening rekening = maakRekening(new BigDecimal(3.10), RekeningStatus.OPEN, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);
        assertThatThrownBy(() -> {
            rekeningService.opnemenRekening(rekeningNummer, BigDecimal.valueOf(412.45));
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BEDRAG_TE_GROOT.getMessage());

        verify(rekeningDAO, never()).wijzigenSaldoRekening(rekeningNummer, new BigDecimal(-409.35));
    }

    //positieve test
    @Test
    public void opnemenRekening() throws Exception {
        Rekening rekening = maakRekening(new BigDecimal(1452.88), RekeningStatus.OPEN, 123);

        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);

        assertThatCode(() -> {
            rekeningService.opnemenRekening(rekeningNummer, BigDecimal.valueOf(300.21));
        }).doesNotThrowAnyException();

        verify(rekeningDAO).wijzigenSaldoRekening(rekeningNummer, BigDecimal.valueOf(1152.67));
    }

    @Test
    public void toevoegenRekening_null() throws Exception {
        assertThatThrownBy(() -> {
            rekeningService.toevoegenRekening(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_NULL.getMessage());

        verify(rekeningDAO, never()).toevoegenRekening(null);
    }

    @Test
    public void toevoegenRekening_zonderRekeningnummer() throws Exception {
        Rekening rekening = maakRekening(null, BigDecimal.ZERO, RekeningStatus.OPEN, 123);
        assertThatThrownBy(() -> {
            rekeningService.toevoegenRekening(rekening);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).toevoegenRekening(rekening);

    }

    @Test
    public void toevoegenRekening_saldoNietZero() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.TEN, RekeningStatus.OPEN, 123);

        assertThatThrownBy(() -> {
            rekeningService.toevoegenRekening(rekening);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_SALDO_MOET_NUL_ZIJN.getMessage());

        verify(rekeningDAO, never()).toevoegenRekening(rekening);
    }

    @Test
    public void toevoegenRekening_statusNietNull() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, RekeningStatus.OPEN, 123);

        assertThatThrownBy(() -> {
            rekeningService.toevoegenRekening(rekening);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_MOET_OPEN_ZIJN.getMessage());

        verify(rekeningDAO, never()).toevoegenRekening(rekening);

    }

    @Test
    public void toevoegenRekening_statusGesloten() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, RekeningStatus.GESLOTEN, 123);
        assertThatThrownBy(() -> {
            rekeningService.toevoegenRekening(rekening);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_MOET_OPEN_ZIJN.getMessage());

        verify(rekeningDAO, never()).toevoegenRekening(rekening);

    }

    @Test
    public void toevoegenRekening_rekeningBestaatAl() throws Exception {

        Rekening rekening = maakRekening(BigDecimal.ZERO, null, 123);
        Rekening rekeningUitDB = maakRekening(BigDecimal.ZERO, RekeningStatus.OPEN, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekeningUitDB);

        assertThatThrownBy(() -> {
            rekeningService.toevoegenRekening(rekening);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_BESTAAT_AL.getMessage());

        verify(rekeningDAO, never()).toevoegenRekening(rekening);

    }

    //positieve test
    @Test
    public void toevoegenRekening_succesvol() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, null, 123);
        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(null);

        assertThatCode(() -> {
            rekeningService.toevoegenRekening(rekening);
        }).doesNotThrowAnyException();

        verify(rekeningDAO).toevoegenRekening(rekening);
    }

    //positieve test
    @Test
    public void zoekRekening() throws Exception {
        Rekening rekening = maakRekening(BigDecimal.ZERO, RekeningStatus.OPEN, 123);

        when(rekeningDAO.zoekRekening(rekeningNummer)).thenReturn(rekening);

        assertThat(rekeningService.zoekRekening(rekeningNummer)).isEqualTo(rekening);
    }

    @Test
    public void zoekRekening_RekeningnummerNull() throws Exception {

        assertThatThrownBy(() -> {
            rekeningService.zoekRekening(null);
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).zoekRekening(null);
    }

    @Test
    public void zoekRekening_RekeningnummerLeeg() throws Exception {
        assertThatThrownBy(() -> {
            rekeningService.zoekRekening("");
        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());

        verify(rekeningDAO, never()).zoekRekening("");
    }

    //positieve test
    @Test
    public void zoekOpenRekeningen() throws Exception {
        int eigenaar = 123;
        Rekening rekening1 = maakRekening(BigDecimal.ZERO, RekeningStatus.OPEN, eigenaar);
        Rekening rekening2 = maakRekening(new Rekeningnummer("BE51 1231 2589 8962"), BigDecimal.ZERO, RekeningStatus.OPEN, eigenaar);

        ArrayList<Rekening> rekeningen = new ArrayList();
        rekeningen.add(rekening1);
        rekeningen.add(rekening2);

        when(rekeningDAO.zoekOpenRekeningen(eigenaar)).thenReturn(rekeningen);

        assertThat(rekeningService.zoekOpenRekeningen(eigenaar)).containsExactly(rekening1, rekening2);
    }

    //positieve test
    @Test
    public void zoekAantalOpenRekeningen() throws Exception {
        int eigenaar = 123;

        when(rekeningDAO.zoekAantalOpenRekeningen(eigenaar)).thenReturn(4);

        assertThat(rekeningService.zoekAantalOpenRekeningen(eigenaar)).isEqualTo(4);
    }
}