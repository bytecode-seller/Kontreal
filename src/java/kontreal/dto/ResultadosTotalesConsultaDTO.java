package kontreal.dto;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Martin Tepostillo
 */
public class ResultadosTotalesConsultaDTO implements Serializable{
    private String tipo;
    private Date fecha;
    private Double sumatoriaCargos;
    private Double sumatoriaAbonos;
    private Double sumatoriaSaldofin;

    public ResultadosTotalesConsultaDTO(String tipo, Date fecha, Double sumatoriaCargos, Double sumatoriaAbonos, Double sumatoriaSaldofin) {
        this.tipo = tipo;
        this.fecha = fecha;
        this.sumatoriaCargos = sumatoriaCargos;
        this.sumatoriaAbonos = sumatoriaAbonos;
        this.sumatoriaSaldofin = sumatoriaSaldofin;
    }

    public ResultadosTotalesConsultaDTO() {
    }
    

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getSumatoriaCargos() {
        return sumatoriaCargos;
    }

    public void setSumatoriaCargos(Double sumatoriaCargos) {
        this.sumatoriaCargos = sumatoriaCargos;
    }

    public Double getSumatoriaAbonos() {
        return sumatoriaAbonos;
    }

    public void setSumatoriaAbonos(Double sumatoriaAbonos) {
        this.sumatoriaAbonos = sumatoriaAbonos;
    }

    public Double getSumatoriaSaldofin() {
        return sumatoriaSaldofin;
    }

    public void setSumatoriaSaldofin(Double sumatoriaSaldofin) {
        this.sumatoriaSaldofin = sumatoriaSaldofin;
    }
    
}
