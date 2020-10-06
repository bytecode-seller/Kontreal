package kontreal.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import kontreal.entities.Cuenta;

/**
 *
 * @author Martin Tepostillo
 */
public class ArchivoCuentasDTO implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private String nombreEmpresa;
    private Date fechaCatalogo;
    private Date fechaDescarga;
    private int numeroRegistros;
    private boolean toUpdate;
    private boolean valid;
    private ArrayList<String> errores;
    private ArrayList<Cuenta> registrosCatalogo;
    private String nombreArchivo;
    private Integer actualizados;

    public ArchivoCuentasDTO() {
        this.valid = true;
        this.actualizados = 0;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public Date getFechaCatalogo() {
        return fechaCatalogo;
    }

    public void setFechaCatalogo(Date fechaCatalogo) {
        this.fechaCatalogo = fechaCatalogo;
    }

    public int getNumeroRegistros() {
        return numeroRegistros;
    }

    public void setNumeroRegistros(int numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

    public boolean isToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(boolean toUpdate) {
        this.toUpdate = toUpdate;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public ArrayList<String> getErrores() {
        return errores;
    }

    public void setErrores(ArrayList<String> errores) {
        this.errores = errores;
    }

    public ArrayList<Cuenta> getRegistrosCatalogo() {
        return registrosCatalogo;
    }

    public void setRegistrosCatalogo(ArrayList<Cuenta> registrosCatalogo) {
        this.registrosCatalogo = registrosCatalogo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public Date getFechaDescarga() {
        return fechaDescarga;
    }

    public void setFechaDescarga(Date fechaDescarga) {
        this.fechaDescarga = fechaDescarga;
    }

    public Integer getActualizados() {
        return actualizados;
    }

    public void setActualizados(Integer actualizados) {
        this.actualizados = actualizados;
    }
    
}
