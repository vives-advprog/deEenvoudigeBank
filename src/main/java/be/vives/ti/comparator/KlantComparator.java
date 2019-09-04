package be.vives.ti.comparator;

import be.vives.ti.databag.Klant;

import java.util.Comparator;

public class KlantComparator implements Comparator<Klant> {

    /**
     * Vergelijkt twee klanten om die te kunnen sorteren. Klanten worden
     * gesorteerd op basis van naam, voornaam
     *
     * @param k1 eerste klant
     * @param k2 tweede klant
     * @return een getal < 0 als k1 voor k2 komt
     * een getal > 0 als k1 na k2 komt 0 als k1 = k2
     */
    @Override
    public int compare(Klant k1, Klant k2) {
        int vergelijk = k1.getNaam().compareTo(k2.getNaam());
        if (vergelijk == 0) {
            return k1.getVoornaam().compareTo(k2.getVoornaam());
        } else {
            return vergelijk;
        }
    }

}
