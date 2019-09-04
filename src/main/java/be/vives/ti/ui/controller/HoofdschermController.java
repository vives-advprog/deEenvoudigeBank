package be.vives.ti.ui.controller;

import be.vives.ti.comparator.KlantComparator;
import be.vives.ti.databag.Klant;
import be.vives.ti.databag.Rekening;
import be.vives.ti.exception.ApplicationException;
import be.vives.ti.exception.DBException;
import be.vives.ti.service.KlantService;
import be.vives.ti.service.RekeningService;
import be.vives.ti.ui.FXDBBank;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class HoofdschermController {

    private RekeningService rekTrans;
    private KlantService klantService;

    // referentie naar mainapp (start)
    private FXDBBank mainApp;

    // klant die geselecteerd is/moet zijn.
    private Integer geselecteerdeKlant;

    public HoofdschermController(RekeningService rekTrans, KlantService klantService) {
        this.rekTrans = rekTrans;
        this.klantService = klantService;
    }

    @FXML
    private ComboBox<Klant> cbNaam;
    @FXML
    private Label laErrorMessage;
    @FXML
    private TableView<Rekening> taRekeningen;
    @FXML
    private TextField tfAdres;
    @FXML
    private TextField tfPostcode;
    @FXML
    private  TextField tfGemeente;
    @FXML
    private TableColumn tcReknr;
    @FXML
    private TableColumn tcSaldo;

    public void initialize() {
        // kolommen van de tabel initialiseren (koppelen met velden uit bag)
        tcReknr.setCellValueFactory(
                new PropertyValueFactory<>("rekeningnummer"));

        tcSaldo.setCellValueFactory(
                new PropertyValueFactory<>("saldo"));
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
     * Data uit de DB op het scherm zetten. klant is het id van de klant die
     * geselecteerd moet worden.
     *
     * @param klant Het id van de klant die geselecteerd moet worden in de
     *              combobox.
     */
    public void setData(Integer klant) {
        geselecteerdeKlant = klant;
        // combox opvullen met klanten uit de DB
        initialiseerCombobox();
        // tabel opvullen met rekeninginfo uit de DB
        initialiseerTabel();
    }

    /**
     * Toevoegen van een nieuwe rekening.
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    public void rekeningNieuw(ActionEvent event) {
        resetErrorMessage();
        // klant wijzigen kan maar als er een klant geselecteerd is.
        Klant activeKlant = cbNaam.getSelectionModel().
                getSelectedItem();
        if (activeKlant == null) {
            laErrorMessage.setText("Geen klant geselecteerd");
        } else {
            mainApp.laadRekeningtoevoegen(geselecteerdeKlant, activeKlant);
        }
    }

    /**
     * Storten van een bedrag op een rekening.
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void rekeningStorten(ActionEvent event) {
        resetErrorMessage();

        // geld storten op een rekening kan maar als er een rekening geselecteerd is.
        Rekening r = taRekeningen.getSelectionModel().getSelectedItem();
        if (r == null) {
            laErrorMessage.setText("Geen rekening geselecteerd");
        } else {
            mainApp.laadStorten(geselecteerdeKlant, r);
        }
    }

    /**
     * Opnemen van een bedrag van een rekening.
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void rekeningOpnemen(ActionEvent event) {
        resetErrorMessage();

        // geld ophalen van een rekening kan maar als er een rekening geselecteerd is.
        Rekening r = taRekeningen.getSelectionModel().getSelectedItem();
        if (r == null) {
            laErrorMessage.setText("Geen rekening geselecteerd");
        } else {
            mainApp.laadOpnemen(geselecteerdeKlant, r);
        }
    }

    /**
     * Rekening schrappen.
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void rekeningSchrappen(ActionEvent event) {
        resetErrorMessage();
        // een rekening kan maar geschrapt worden als er een rekening geselecteerd is.
        Rekening r = taRekeningen.getSelectionModel().getSelectedItem();
        if (r != null) {
            try {
                // rekening verwijderen in de DB
                rekTrans.verwijderRekening(r.getRekeningnummer().toString());
                // rekening verwijderen in tabel (niet alle data moet opnieuw uit de DB gehaald worden!)
                taRekeningen.getItems().remove(r);
            } catch (ApplicationException ae) {
                laErrorMessage.setText(ae.getMessage());
            } catch (DBException ae) {
                laErrorMessage.setText("onherstelbare fout: " + ae.getMessage());
            }
        } else {
            laErrorMessage.setText("Er werd geen rekening geselecteerd");
        }
    }

    /**
     * Klant schrappen.
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void klantSchrappen(ActionEvent event) {
        resetErrorMessage();

        // klant schrappen kan maar als er een klant geselecteerd is.
        Klant activeKlant = cbNaam.getSelectionModel().
                getSelectedItem();
        if (activeKlant != null) {
            try {
                // klant verwijderen in DB
                klantService.verwijderKlant(activeKlant.getId());
                // klant verwijderen in combobox
                // --> klant verwijderen uit combobox
                // --> ook tabel aanpassen (rekeningen van geselecteerde klant)      
                cbNaam.getItems().remove(activeKlant);
                initialiseerTabel();
            } catch (ApplicationException ae) {
                laErrorMessage.setText(ae.getMessage());
            } catch (DBException ae) {
                laErrorMessage.setText("onherstelbare fout: " + ae.getMessage());
            }
        }
    }

    /**
     * Klant wijzigen.
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void klantWijzigen(ActionEvent event) {
        resetErrorMessage();

        // klant wijzigen kan maar als er een klant geselecteerd is.
        Klant activeKlant = cbNaam.getSelectionModel().
                getSelectedItem();
        if (activeKlant == null) {
            laErrorMessage.setText("Geen klant geselecteerd");
        } else {
            mainApp.laadKlantwijzigen(geselecteerdeKlant, activeKlant);
        }
    }

    /**
     * Klant toevoegen, hiervoor wordt een nieuw scherm opgestart.
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void klantNieuw(ActionEvent event) {

        resetErrorMessage();
        // klant toevoegen
        mainApp.laadKlanttoevoegen(geselecteerdeKlant);
    }

    /**
     * Bij het selecteren van een klant worden de details in de overeenkomstige
     * velden geladen.
     *
     * @param event Info over het event dat deze methode triggerde
     */
    @FXML
    private void selecteerKlant(ActionEvent event) {

        resetErrorMessage();
        // geselecteerde klant ophalen en bewaren in het hoofdscherm
        Klant k = cbNaam.getSelectionModel().getSelectedItem();
        if (k != null) {
            geselecteerdeKlant = k.getId();
        } else {
            geselecteerdeKlant = null;
            //gegevens geselecteerde klant ophalen
        }
        selecteer(k);

    }

    /**
     * gegevens van geselecteerde klant aanpassing in het scherm
     */
    private void selecteer(Klant k) {
        if (k != null) {
            // van geselecteerde klant overeenkomstig adres, postcode en gemeente ophalen
            // en tonen
            tfAdres.setText(k.getAdres());
            tfPostcode.setText(k.getPostcode());
            tfGemeente.setText(k.getGemeente());

            // nieuw geselecteerde klant, dus nieuwe rekeningen opvragen en tonen in tabel
            initialiseerTabel();
        } else // als er geen klant geselecteerd is, dan overige gegevens op blanco zetten
        {
            tfAdres.setText("");
            tfPostcode.setText("");
            tfGemeente.setText("");
        }
    }

    /**
     * De foutboodschap op het scherm verwijderen
     */
    private void resetErrorMessage() {
        laErrorMessage.setText("");
    }

    /**
     * De tabel opvullen met data over rekeningen uit de DB
     */
    private void initialiseerTabel() {
        resetErrorMessage();
        // geselecteerde klant ophalen
        Klant k = cbNaam.getSelectionModel().getSelectedItem();
        if (k != null) {
            try {
                // alle rekeningen van de geselecteerde klant ophalen
                ArrayList<Rekening> reklijst = rekTrans.zoekOpenRekeningen(k.getId());

                ObservableList<Rekening> rekeningen = FXCollections.
                        observableArrayList(reklijst);
                taRekeningen.setItems(rekeningen);
            } catch (DBException ae) {
                laErrorMessage.setText("onherstelbare fout: " + ae.
                        getMessage());
            }
        }
    }

    /**
     * De combobox opvullen met data over klanten uit de DB
     */
    private void initialiseerCombobox() {
        // alle ingeschreven klanten ophalen en in de combobox steken
        Klant selectie = null;
        try {
            ArrayList<Klant> klantlijst = klantService.zoekIngeschrevenKlanten();
            for (Klant klant : klantlijst) {
                //klant die geselecteerd moet worden zoeken
                if (klant.getId().equals(geselecteerdeKlant)) {
                    selectie = klant;
                }
                cbNaam.getItems().add(klant);
            }
            //klanten sorteren
            cbNaam.getItems().sort(new KlantComparator());
            //klant die geselecteerd moet worden, selecteren
            if (selectie != null) {

                cbNaam.getSelectionModel().select(selectie);
                //bijhorende velden aanvullen
                selecteer(selectie);
            }
        } catch (DBException ae) {
            laErrorMessage.setText("onherstelbare fout: " + ae.
                    getMessage());
        }
    }
}
