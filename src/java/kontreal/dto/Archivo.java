package kontreal.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import kontreal.entities.Empresa;

/**
 *
 * @author Martin Tepostillo
 */
public class Archivo<T> implements Serializable{
    
    private String nombre;
    private Empresa empresa;
    private Date descarga;
    private Date consulta;
    private Boolean valido;
    private List<T> registros;
    private Boolean actualizable;
    private List<String> errores;
    private Class<T> tipo;
    private Integer actualizados;

    public Archivo(Class<T> tipo) {
        this.tipo = tipo;
        this.registros = new ArrayList<>();
        this.errores = new ArrayList<>();
    }

    public Archivo(String nombre, Empresa empresa, Date descarga, Date consulta, Boolean valido, List<T> registros, Boolean actualizable, List<String> errores, Integer actualizados) {
        this.nombre = nombre;
        this.empresa = empresa;
        this.descarga = descarga;
        this.consulta = consulta;
        this.valido = valido;
        this.registros = registros;
        this.actualizable = actualizable;
        this.errores = errores;
        this.actualizados = actualizados;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Date getDescarga() {
        return descarga;
    }

    public void setDescarga(Date descarga) {
        this.descarga = descarga;
    }

    public Date getConsulta() {
        return consulta;
    }

    public void setConsulta(Date consulta) {
        this.consulta = consulta;
    }

    public Boolean getValido() {
        return valido;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public List<T> getRegistros() {
        return registros;
    }

    public void setRegistros(List<T> registros) {
        this.registros = registros;
    }

    public Boolean getActualizable() {
        return actualizable;
    }

    public void setActualizable(Boolean actualizable) {
        this.actualizable = actualizable;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }
    
    public void setTipo(Class<T> tipo){
        this.tipo = tipo;
    }
    
    public Class<T> getTipo(){
        return this.tipo;
    }

    public Integer getActualizados() {
        return actualizados;
    }

    public void setActualizados(Integer actualizados) {
        this.actualizados = actualizados;
    }
    
    
}
