package kontreal.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Tepostillo
 */
public class ResultadosTotalesDTO implements Serializable{
    private String tipo;
    private List<Double> resultadosAcreedora;
    private List<Double> resultadosDeudora;

    public ResultadosTotalesDTO() {
        this.resultadosAcreedora = new ArrayList<>();
        this.resultadosDeudora = new ArrayList<>();
    }

    public ResultadosTotalesDTO(String tipo, List<Double> resultadosAcreedora, List<Double> resultadosDeudora) {
        this.tipo = tipo;
        this.resultadosAcreedora = resultadosAcreedora;
        this.resultadosDeudora = resultadosDeudora;
    }

    public List<Double> getResultadosDeudora() {
        return resultadosDeudora;
    }

    public void setResultadosDeudora(List<Double> resultadosDeudora) {
        this.resultadosDeudora = resultadosDeudora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Double> getResultadosAcreedora() {
        return resultadosAcreedora;
    }

    public void setResultadosAcreedora(List<Double> resultadosAcreedora) {
        this.resultadosAcreedora = resultadosAcreedora;
    }
    
    public void addResultadosTotalesAcreedora(Double resultadoAcreedora){
        this.resultadosAcreedora.add(resultadoAcreedora);
    }
    
    public void addResultadosTotalesDeudora(Double resultadoDeudora){
        this.resultadosDeudora.add(resultadoDeudora);
    }
    
}
