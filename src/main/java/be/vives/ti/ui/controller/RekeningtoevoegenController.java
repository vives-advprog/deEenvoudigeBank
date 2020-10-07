package be.vives.ti.ui.controller;

import be.vives.ti.databag.Klant;
import be.vives.ti.databag.Rekening;
import be.vives.ti.datatype.Rekeningnummer;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.KlantService;
import be.vives.ti.service.RekeningService;
import be.vives.ti.ui.FXDBBank;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RekeningtoevoegenController {

    private RekeningService rekeningService;
    private KlantService klantService;
    private int geselecteerdeKlant;
    private Klant klant;
    private FXDBBank mainApp;

    public RekeningtoevoegenController(RekeningService rekeningService, KlantService klantService) {
        this.rekeningService = rekeningService;
        this.klantService = klantService;
    }

    @FXML
    private TextField tfRekeningnummer;
    @FXML
    private TextField tfEigenaar;
    @FXML
    private Label laErrorMessage;

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
     * Wijzigen rekening bewaren
     *
     * @param evt Info over het event dat deze methode triggerde
     */
    @FXML
    private void opslaanRekening(ActionEvent evt) {

        try {
            // rekening toevoegen in DB
            Rekening rekening = new Rekening();
            rekening.setRekeningnummer(new Rekeningnummer(tfRekeningnummer.getText()));
            rekening.setEigenaar(klant.getId());

            klantService.valideerKlant(klant.getId());
            rekeningService.toevoegenRekening(rekening);

            // terugkeren naar het hoofdscherm en klant selecteren
            mainApp.laadHoofdscherm(geselecteerdeKlant);
        } catch (ApplicationException ae) {
            laErrorMessage.setText(ae.getMessage());
        } catch (DBException ae) {
            laErrorMessage.setText("onherstelbare fout: " + ae.getMessage());

        }
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
     * Specifieke data voor het scherm instellen
     *
     * @param indexId id van de klant die geselecteerd was in het oproepende
     *                scherm
     * @param klant   de klant waarvan de gegevens getoond moeten worden
     */
    public void setData(Integer indexId, Klant klant) {
        this.geselecteerdeKlant = indexId;
        this.klant = klant;
        tfEigenaar.setText(klant.getVoornaam() + " " + klant.getNaam());
    }
}
