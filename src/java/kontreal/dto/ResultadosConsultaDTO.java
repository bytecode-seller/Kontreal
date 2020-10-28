package kontreal.dto;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Martin Tepostillo
 */
public class ResultadosConsultaDTO implements Serializable{
    private String numeroCuenta;
    private String tipo;
    private String nombre;
    private Date fecha;
    private double cargos;
    private double abonos;
    private double saldofin;
    
    public ResultadosConsultaDTO(){
    
    }

    public ResultadosConsultaDTO(String numeroCuenta, String tipo, String nombre, Date fecha, double cargos, double abonos, double saldofin) {
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.nombre = nombre;
        this.fecha = fecha;
        this.cargos = cargos;
        this.abonos = abonos;
        this.saldofin = saldofin;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getCargos() {
        return cargos;
    }

    public void setCargos(double cargos) {
        this.cargos = cargos;
    }

    public double getAbonos() {
        return abonos;
    }

    public void setAbonos(double abonos) {
        this.abonos = abonos;
    }

    public double getSaldofin() {
        return saldofin;
    }

    public void setSaldofin(double saldofin) {
        this.saldofin = saldofin;
    }
    
}
