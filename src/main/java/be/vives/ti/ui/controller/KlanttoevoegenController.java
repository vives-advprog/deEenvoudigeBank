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

public class KlanttoevoegenController {

    private KlantService klantService;
    private FXDBBank mainApp;
    private Integer geselecteerdeKlant;

    public KlanttoevoegenController(KlantService klantService) {
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
        buActie.setText("Toevoegen");
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
     * Nieuwe klant bewaren
     *
     * @param evt Info over het event dat deze methode triggerde
     */
    @FXML
    private void opslaanKlant(ActionEvent evt) {
        try {
            // toevoegen in DB
            Klant klant = new Klant();
            klant.setNaam(tfNaam.getText());
            klant.setVoornaam(tfVoornaam.getText());
            klant.setAdres(tfAdres.getText());
            klant.setPostcode(tfPostcode.getText());
            klant.setGemeente(tfGemeente.getText());
            Integer id = klantService.toevoegenKlant(klant);

            // terugkeren naar het hoofdscherm en  klant selecteren
            mainApp.laadHoofdscherm(id);

        } catch (ApplicationException ae) {
            laErrorMessage.setText(ae.getMessage());
        } catch (DBException ae) {
            laErrorMessage.setText("onherstelbare fout: " + ae.getMessage());
        }
    }

    /**
     * Specifieke data voor het scherm instellen
     *
     * @param indexKlant id van de geselecteerde klant in het oproepende scherm
     */
    public void setData(Integer indexKlant) {
        geselecteerdeKlant = indexKlant;
    }
}
