package kontreal.entities;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Martin Tepostillo
 */
public class LogArchivoBalanza implements Serializable{
    
    private Integer idLogArchivoBalanza;
    private String nombre;
    private String mensaje;
    private Date fechaCarga;
    private boolean guardado;
    private Integer numRegistros;
    private Date fechaArchivo;
    private Date fechaDescarga;
    private Integer actualizado;

    public Integer getIdLogArchivoBalanza() {
        return idLogArchivoBalanza;
    }

    public void setIdLogArchivoBalanza(Integer idLogArchivoBalanza) {
        this.idLogArchivoBalanza = idLogArchivoBalanza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }

    public Integer getNumRegistros() {
        return numRegistros;
    }

    public void setNumRegistros(Integer numRegistros) {
        this.numRegistros = numRegistros;
    }
    
    public Date getFechaArchivo() {
        return fechaArchivo;
    }

    public void setFechaArchivo(Date fechaArchivo) {
        this.fechaArchivo = fechaArchivo;
    }

    public Date getFechaDescarga() {
        return fechaDescarga;
    }

    public void setFechaDescarga(Date fechaDescarga) {
        this.fechaDescarga = fechaDescarga;
    }

    public Integer getActualizado() {
        return actualizado;
    }

    public void setActualizado(Integer actualizado) {
        this.actualizado = actualizado;
    }
    
}
