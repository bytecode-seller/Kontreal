package kontreal.dto;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Martin Tepostillo
 */
public class UtilidadPerdidaDTO implements Serializable{

    private Date fecha;
    private Double cargosAbonos;
    private Double saldofin;

    public UtilidadPerdidaDTO() {
    }

    public UtilidadPerdidaDTO(Date fecha, Double cargosAbonos, Double saldofin) {
        this.fecha = fecha;
        this.cargosAbonos = cargosAbonos;
        this.saldofin = saldofin;
    }

    public Double getSaldofin() {
        return saldofin;
    }

    public void setSaldofin(Double saldofin) {
        this.saldofin = saldofin;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getCargosAbonos() {
        return cargosAbonos;
    }

    public void setCargosAbonos(Double cargosAbonos) {
        this.cargosAbonos = cargosAbonos;
    }

}
