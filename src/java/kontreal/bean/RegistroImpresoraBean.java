package kontreal.bean;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import kontreal.services.ImpresoraService;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class RegistroImpresoraBean implements Serializable{
    
    private String nombre;
    private String service;
    private String characteristic;
    private boolean saveIsDisabled;
    private Integer tamanio;
    
    @Inject
    private ImpresoraService impresoraService;

    /**
     * Creates a new instance of RegistroImpresoraBean
     */
    public RegistroImpresoraBean() {
        saveIsDisabled = true;
    }
    
    public void guardar(){
        impresoraService.guardarImpresora(nombre,service,characteristic, tamanio);
    }
    
    public void enableSave(){
        saveIsDisabled = false;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }

    public boolean isSaveIsDisabled() {
        return saveIsDisabled;
    }

    public void setSaveIsDisabled(boolean saveIsDisabled) {
        this.saveIsDisabled = saveIsDisabled;
    }

    public Integer getTamanio() {
        return tamanio;
    }

    public void setTamanio(Integer tamanio) {
        this.tamanio = tamanio;
    }
    
}
