package be.vives.ti.databag;

import be.vives.ti.datatype.KlantStatus;

/**
 * Databag-class Transportmiddel voor een Klant
 * - private datamembers
 * - getters en setters
 * - defaultconstructor
 * - toString() (string-representatie van een Klant-object)
 */
public class Klant {

    private Integer id;  //kan null zijn (id wordt gegenereerd door DB)
    private String naam;
    private String voornaam;
    private String adres;
    private String postcode;
    private String gemeente;
    private KlantStatus status;

    // constructor standaard aanwezig

    // getters
    public Integer getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public String getAdres() {
        return adres;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getGemeente() {
        return gemeente;
    }

    public KlantStatus getStatus() {
        return status;
    }

    //setters

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setGemeente(String gemeente) {
        this.gemeente = gemeente;
    }

    public void setStatus(KlantStatus status) {
        this.status = status;
    }

    // toString wordt gebuikt door UI (bv. combobox opvullen)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(voornaam).append(" ").
                append(naam).append(" ");
        return sb.toString();
    }
}
