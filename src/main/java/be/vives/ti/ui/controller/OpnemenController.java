package be.vives.ti.ui.controller;

import be.vives.ti.databag.Rekening;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.RekeningService;
import be.vives.ti.ui.FXDBBank;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class OpnemenController  {

    private RekeningService rekeningService;

    private FXDBBank mainApp;
    private int geselecteerdeKlant;
    private Rekening rekening;

    public OpnemenController(RekeningService rekeningService) {
        this.rekeningService = rekeningService;
    }

    @FXML
    private TextField tfRekeningnummer;
    @FXML
    private TextField tfBedrag;
    @FXML
    private TextField tfSaldo;
    @FXML
    private Label laErrorMessage;
    @FXML
    private Label laTransactie;
    @FXML
    private Button buActie;

    public void initialize() {
        laTransactie.setText("Op te nemen bedrag:");
        buActie.setText("Opnemen");
    }

    /**
     * Referentie naar mainApp (start) instellen
     *
     * @param mainApp referentie naar de runnable class die alle oproepen naar
     *                de schermen bestuurt
     */
    public void setMainApp(FXDBBank mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Terugkeren naar het hoofdscherm
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void goToHoofdscherm(ActionEvent event) {
        // terugkeren naar het hoofdscherm en de klant selecteren die geselecteerd was
        mainApp.laadHoofdscherm(geselecteerdeKlant);
    }

    /**
     * Aanpassingen aan saldo bewaren
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void voerTransactieUit(ActionEvent event) {

        try {
            this.checkBedragIngevuld();
            rekeningService.opnemenRekening(rekening.getRekeningnummer().getRekeningnummer(),
                    new BigDecimal(tfBedrag.getText()));

            // terugkeren naar het hoofdscherm en klant selecteren
            mainApp.laadHoofdscherm(geselecteerdeKlant);

        } catch (ApplicationException ae) {
            laErrorMessage.setText(ae.getMessage());
        } catch (DBException ae) {
            laErrorMessage.setText("onherstelbare fout: " + ae.getMessage());
        }

    }

    /**
     * Controleren of alle velden correct ingevuld zijn. Indien niet, dan wordt
     * er een overeenkomstige ApplicationException gegooid.
     */
    private void checkBedragIngevuld() throws ApplicationException {
        try {
            BigDecimal bedrag = new BigDecimal(tfBedrag.getText());
        } catch (NumberFormatException ne) {
            throw new ApplicationException(
                    ApplicationExceptionType.REK_ONGELDIG_BEDRAG.getMessage());
        }
    }

    /**
     * Specifieke data voor het scherm instellen
     *
     * @param klantId id van de klant die geselecteerd was in het oproepende
     *                scherm
     * @param rek     de rekening waarvan de gegevens getoond moeten worden
     */
    public void setData(Integer klantId, Rekening rek) {
        geselecteerdeKlant = klantId;
        rekening = rek;
        tfRekeningnummer.setText(rek.getRekeningnummer().getRekeningnummer());
        tfSaldo.setText(String.valueOf(rek.getSaldo()));
    }

}
