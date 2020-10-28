package kontreal.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Tepostillo
 */
public class ResultadosDTO implements Serializable{
    private String cuenta;
    private String tipo;
    private String nombre;
    private List<Double> resultadosAc;
    private List<Double> resultadosFin;
    
    public ResultadosDTO(){
        this.resultadosAc = new ArrayList<>();
        this.resultadosFin = new ArrayList<>();
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
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

    public List<Double> getResultadosAc() {
        return resultadosAc;
    }

    public void setResultadosAc(List<Double> resultadosAc) {
        this.resultadosAc = resultadosAc;
    }

    public List<Double> getResultadosFin() {
        return resultadosFin;
    }

    public void setResultadosFin(List<Double> resultadosFin) {
        this.resultadosFin = resultadosFin;
    }
    
    public void addResultadosAc(Double resultadoAc){
        this.resultadosAc.add(resultadoAc);
    }
    
    public void addResultadosFin(Double resultadofin){
        this.resultadosFin.add(resultadofin);
    }
}
