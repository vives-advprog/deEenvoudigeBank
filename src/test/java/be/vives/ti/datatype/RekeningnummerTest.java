package be.vives.ti.datatype;

import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RekeningnummerTest {

    private String rekeningNummer;

    @Before
    public void setUp() throws Exception {
        this.rekeningNummer = "BE62 0016 6836 7361";
    }

    @Test
    public void createGeldigRekeningnummer() {
        assertThatCode(() -> {
                    new Rekeningnummer(rekeningNummer);
                }
        ).doesNotThrowAnyException();
    }

    @Test
    public void createRekeningNummer_null() {
        assertThatThrownBy(() -> {
            new Rekeningnummer(null);

        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());
    }

    @Test
    public void createRekeningNummer_emptyString() {
        assertThatThrownBy(() -> {
            new Rekeningnummer("");

        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_LEEG.getMessage());
    }

    @Test
    public void createRekeningNummer_ongeldigFormaat() {
        assertThatThrownBy(() -> {
            new Rekeningnummer("bla");

        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_ONGELDIG_FORMAAT.getMessage());
    }

    @Test
    public void createRekeningNummer_ongeldigFormaat_nietBE() {
        assertThatThrownBy(() -> {
            new Rekeningnummer("NL00 0000 0000 0000");

        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_ONGELDIG_FORMAAT.getMessage());
    }

    @Test
    public void createRekeningNummer_ongeldigFormaat_module97() {
        assertThatThrownBy(() -> {
            new Rekeningnummer("BE00 0000 0000 0000");

        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_ONGELDIG.getMessage());
    }

    @Test
    public void createRekeningNummer_ongeldigFormaat_nfe() {
        assertThatThrownBy(() -> {
            new Rekeningnummer("BE00 BE00 BE00 00BE");

        }).isInstanceOf(ApplicationException.class).hasMessage(ApplicationExceptionType.REK_REKNUMMER_ONGELDIG_FORMAAT.getMessage());
    }

}