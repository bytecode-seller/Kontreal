package kontreal.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import kontreal.entities.Balanza;

/**
 *
 * @author Martin Tepostillo
 */
public class ArchivoBalanzaDto implements Serializable{

    private static final long serialVersionUID = 1L;
    private String nombreEmpresa;
    private Date fechaBalanza;
    private int numeroRegistros;
    private boolean toUpdate;
    private boolean valid;
    private ArrayList<String> errores;
    private ArrayList<Balanza> registrosBalanza;
    private String nombreArchivo;
    
    public ArchivoBalanzaDto(){
        this.valid = true;
    }
    
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public Date getFechaBalanza() {
        return fechaBalanza;
    }

    public void setFechaBalanza(Date fechaBalanza) {
        this.fechaBalanza = fechaBalanza;
    }

    public int getNumeroRegistros() {
        return numeroRegistros;
    }

    public void setNumeroRegistros(int numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

    public ArrayList<Balanza> getRegistrosBalanza() {
        return registrosBalanza;
    }

    public void setRegistrosBalanza(ArrayList<Balanza> registrosBalanza) {
        this.registrosBalanza = registrosBalanza;
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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
}
