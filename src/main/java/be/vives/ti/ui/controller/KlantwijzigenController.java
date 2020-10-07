package be.vives.ti.ui.controller;

import be.vives.ti.databag.Klant;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.ApplicationExceptionType;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.KlantService;
import be.vives.ti.ui.FXDBBank;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

public class KlantwijzigenController {

    private KlantService klantService;

    private FXDBBank mainApp;
    private Klant klant;
    private int geselecteerdeKlant;

    public KlantwijzigenController(KlantService klantService) {
        this.klantService = klantService;
    }

    @FXML
    private TextField tfNaam;
    @FXML
    private TextField tfVoornaam;
    @FXML
    private TextField tfAdres;
    @FXML
    private TextField tfPostcode;
    @FXML
    private TextField tfGemeente;
    @FXML
    private Label laErrorMessage;
    @FXML
    private Button buActie;

    public void initialize() {
        buActie.setText("Wijzigen");
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
     * Wijzigingen klant bewaren
     *
     * @param evt Info over het event dat deze methode triggerde
     */
    @FXML
    private void opslaanKlant(ActionEvent evt) {

        try {
            // klant wijzigen in DB
            Klant gewijzigdeKlant = new Klant();
            gewijzigdeKlant.setNaam(tfNaam.getText());
            gewijzigdeKlant.setVoornaam(tfVoornaam.getText());
            gewijzigdeKlant.setAdres(tfAdres.getText());
            gewijzigdeKlant.setPostcode(tfPostcode.getText());
            gewijzigdeKlant.setGemeente(tfGemeente.getText());
            gewijzigdeKlant.setId(klant.getId());

            klantService.wijzigenKlant(gewijzigdeKlant);

            // terugkeren naar het hoofdscherm en gewijzigde klant selecteren
            mainApp.laadHoofdscherm(geselecteerdeKlant);

        } catch (ApplicationException ae) {
            laErrorMessage.setText(ae.getMessage());
        } catch (DBException ae) {
            laErrorMessage.setText("onherstelbare fout: " + ae.getMessage());
        }
    }

    /**
     * Specifieke data voor het scherm instellen
     *
     * @param klandId id van de klant die geselecteerd was in het oproepende
     *                scherm
     * @param k       de klant waarvan de gegevens getoond moeten worden
     */
    public void setData(Integer klandId, Klant k) {
        geselecteerdeKlant = klandId;
        klant = k;

        tfNaam.setText(k.getNaam());
        tfVoornaam.setText(k.getVoornaam());
        tfAdres.setText(k.getAdres());
        tfPostcode.setText(k.getPostcode());
        tfGemeente.setText(k.getGemeente());
    }

}
