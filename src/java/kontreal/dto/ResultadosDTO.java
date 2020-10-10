package kontreal.dto;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Martin Tepostillo
 */
public class ResultadosDTO implements Serializable{
    private String cuentaR;
    private String tipoR;
    private String nombreR;
    private Date fechaR;
    private double cargosR;
    private double abonosR;
    private double saldofinR;
    
    public ResultadosDTO(){
    
    }
    
    public ResultadosDTO(String cuentaR, String tipoR, String nombreR, Date fechaR, double cargosR, double abonosR, double saldofinR){
        this.cuentaR = cuentaR;
        this.tipoR = tipoR;
        this.nombreR = nombreR;
        this.fechaR = fechaR;
        this.cargosR = cargosR;
        this.abonosR = abonosR;
        this.saldofinR = saldofinR;
    }

    public String getCuentaR() {
        return cuentaR;
    }

    public void setCuentaR(String cuentaR) {
        this.cuentaR = cuentaR;
    }

    public String getTipoR() {
        return tipoR;
    }

    public void setTipoR(String tipoR) {
        this.tipoR = tipoR;
    }

    public String getNombreR() {
        return nombreR;
    }

    public void setNombreR(String nombreR) {
        this.nombreR = nombreR;
    }

    public Date getFechaR() {
        return fechaR;
    }

    public void setFechaR(Date fechaR) {
        this.fechaR = fechaR;
    }

    public double getCargosR() {
        return cargosR;
    }

    public void setCargosR(double cargosR) {
        this.cargosR = cargosR;
    }

    public double getAbonosR() {
        return abonosR;
    }

    public void setAbonosR(double abonosR) {
        this.abonosR = abonosR;
    }

    public double getSaldofinR() {
        return saldofinR;
    }

    public void setSaldofinR(double saldofinR) {
        this.saldofinR = saldofinR;
    }
    
}
