package be.vives.ti.ui;

import be.vives.ti.DAO.KlantDAO;
import be.vives.ti.DAO.RekeningDAO;
import be.vives.ti.databag.Klant;
import be.vives.ti.databag.Rekening;
import be.vives.ti.service.KlantService;
import be.vives.ti.service.RekeningService;
import be.vives.ti.ui.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FXDBBank extends Application {

    private Stage stage;

    private RekeningService rekeningService;
    private KlantService klantService;
    private RekeningDAO rekeningDAO;
    private KlantDAO klantDAO;

    private RekeningService createRekeningService() {
        if (rekeningService == null) {
            this.rekeningService = new RekeningService(createRekeningDao());
        }
        return rekeningService;
    }

    private RekeningDAO createRekeningDao() {
        if (rekeningDAO == null) {
            this.rekeningDAO = new RekeningDAO();
        }
        return rekeningDAO;
    }

    private KlantService createKlantService() {
        if (klantService == null) {
            this.klantService = new KlantService(createKlantDao(), createRekeningService());
        }
        return klantService;
    }

    private KlantDAO createKlantDao() {
        if (klantDAO == null) {
            this.klantDAO = new KlantDAO();
        }
        return klantDAO;
    }

    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;
        laadHoofdscherm(null);
        primaryStage.show();
    }

    /**
     * Laadt het hoofdscherm. De controller bewaart een referentie naar this en
     * krijgt de klant door die geselecteerd moet zijn.
     *
     * @param klantId Id van de klant die geselecteerd moet zijn.
     */
    public void laadHoofdscherm(Integer klantId) {

        try {
            String fxmlFile = "/fxml/hoofdscherm.fxml";
            FXMLLoader loader = new FXMLLoader();

            // controller instellen
            HoofdschermController controller = new HoofdschermController(createRekeningService(), createKlantService());
            loader.setController(controller);

            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));

            // referentie naar hier bewaren in de controller
            controller.setMainApp(this);
            controller.setData(klantId);

            Scene scene = new Scene(root);
            stage.setTitle("Bank");
            stage.setScene(scene);

        } catch (Exception e) {
            System.out.println("!!! laadHoofdscherm - " + e.getMessage());

        }

    }

    /**
     * Laadt het scherm om een klant toe te voegen. De controller bewaart een
     * referentie naar this en krijgt de klant door die geselecteerd was.
     *
     * @param klantId Id van de klant die geselecteerd was in het
     *                oproepend scherm.
     */
    public void laadKlanttoevoegen(Integer klantId) {
        try {
            String fxmlFile = "/fxml/klantscherm.fxml";
            FXMLLoader loader = new FXMLLoader();

            // controller instellen
            KlanttoevoegenController controller = new KlanttoevoegenController(createKlantService());
            loader.setController(controller);

            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
            // referentie naar hier bewaren in de controller
            controller.setMainApp(this);
            controller.setData(klantId);

            Scene scene = new Scene(root);
            stage.setTitle("Klant toevoegen");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("!!! laadKlanttoevoegen - " + e.getMessage());
        }

    }

    /**
     * Laadt het scherm om een klant te wijzigen. De controller bewaart een
     * referentie naar this en krijgt de klant door die geselecteerd was.
     *
     * @param klantId Id van de klant die geselecteerd is in het oproepend
     *                scherm
     * @param klant   De klant die gewijzigd moet worden.
     */
    public void laadKlantwijzigen(Integer klantId, Klant klant) {
        try {
            String fxmlFile = "/fxml/klantscherm.fxml";
            FXMLLoader loader = new FXMLLoader();

            // controller instellen
            KlantwijzigenController controller = new KlantwijzigenController(createKlantService());
            loader.setController(controller);

            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));

            // referentie naar hier bewaren in de controller
            controller.setMainApp(this);
            // doorgeven welke klant in het oproepend scherm geselecteerd was
            // en de klantgegevens die getoond moeten worden.
            controller.setData(klantId, klant);

            Scene scene = new Scene(root);
            stage.setTitle("Klant wijzigen");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("!!! laadKlantwijzigen - " + e.getMessage());
        }

    }

    /**
     * Laadt het scherm om geld te storten. De controller bewaart een referentie
     * naar this en krijgt de klant en rekening door die geselecteerd waren.
     *
     * @param klantId Id van de klant die geselecteerd is in het oproepend
     *                scherm
     * @param rek     De rekening waarop geld gestort moet worden
     */
    public void laadStorten(Integer klantId, Rekening rek) {
        try {
            String fxmlFile = "/fxml/transactie.fxml";
            FXMLLoader loader = new FXMLLoader();

            // controller instellen
            StortenController controller = new StortenController(createRekeningService());
            loader.setController(controller);

            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
            // referentie naar hier bewaren in de controller
            controller.setMainApp(this);
            // doorgeven welke klant en welke rekening geselecteerd waren
            controller.setData(klantId, rek);

            Scene scene = new Scene(root);
            stage.setTitle("Storten");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("!!! laadStorten - " + e.getMessage());
        }

    }

    /**
     * Laadt het scherm om geld op te nemen. De controller bewaart een
     * referentie naar this en krijgt de klant en rekening door die geselecteerd
     * waren.
     *
     * @param klantId Id van de klant die geselecteerd was in het
     *                oproepend scherm
     * @param rek     De rekening waarvan geld moet opgenomen worden
     */
    public void laadOpnemen(Integer klantId, Rekening rek) {
        try {
            String fxmlFile = "/fxml/transactie.fxml";
            FXMLLoader loader = new FXMLLoader();

            // controller instellen
            OpnemenController controller = new OpnemenController(createRekeningService());
            loader.setController(controller);
            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
            // referentie naar hier bewaren in de controller
            controller.setMainApp(this);
            // doorgeven welke klant en welke rekening geselecteerd waren
            controller.setData(klantId, rek);

            Scene scene = new Scene(root);
            stage.setTitle("Opnemen");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("!!! laadOpnemen - " + e.getMessage());
        }
    }

    /**
     * Laadt het scherm om een rekening toe te voegen. De controller bewaart een
     * referentie naar this en krijgt de klant door die geselecteerd was.
     *
     * @param klantId Index van de klant die geselecteerd is in het oproepend
     *                scherm
     * @param k       De eigenaar van de nieuwe rekening
     */
    public void laadRekeningtoevoegen(Integer klantId, Klant k) {
        try {
            String fxmlFile = "/fxml/rekeningtoevoegen.fxml";
            FXMLLoader loader = new FXMLLoader();
            // controller instellen
            RekeningtoevoegenController controller = new RekeningtoevoegenController(createRekeningService(), createKlantService());
            loader.setController(controller);
            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));

            // referentie naar hier bewaren in de controller
            controller.setMainApp(this);
            // doorgeven welke klant een rekening wil toevoegen
            controller.setData(klantId, k);

            Scene scene = new Scene(root);
            stage.setTitle("Rekening toevoegen");
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("!!! laadRekeningtoevoegen - " + e.getMessage());
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
