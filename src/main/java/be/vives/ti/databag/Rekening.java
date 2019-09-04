package be.vives.ti.databag;

import be.vives.ti.datatype.RekeningStatus;
import be.vives.ti.datatype.Rekeningnummer;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Databag-class Transportmiddel voor een Rekening
 * - private datamembers
 * - getters en setters
 * - defaultconstructor
 * - toString() (string-representatie van een Rekening-object)
 */
public class Rekening {

    private Rekeningnummer rekeningnummer;
    //BigDecimal is ideaal voor bedragen in een bepaalde munteenheid
    //Het maakt afronden en tot op 2-decimalen afdrukken veel eenvoudiger en correcter
    private BigDecimal saldo = BigDecimal.ZERO;
    private RekeningStatus status;
    private int eigenaar;  //kan nooit null zijn, dus int

    // constructor standaard aanwezig

    // getters
    public Rekeningnummer getRekeningnummer() {
        return rekeningnummer;
    }

    public BigDecimal getSaldo() {
        if (saldo == null) {
            return BigDecimal.ZERO;
        } else {
            return saldo.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public RekeningStatus getStatus() {
        return status;
    }

    public int getEigenaar() {
        return eigenaar;
    }

    //setters

    public void setRekeningnummer(Rekeningnummer rekeningnummer) {
        this.rekeningnummer = rekeningnummer;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void setStatus(RekeningStatus status) {
        this.status = status;
    }

    public void setEigenaar(int eigenaar) {
        this.eigenaar = eigenaar;
    }

    @Override
    public String toString() {
        return "Rekening {" + "rekeningnummer=" + rekeningnummer
                + ", saldo=" + getSaldo()
                + ", status=" + getStatus().name()
                + ", eigenaar=" + eigenaar + '}';
    }

}
