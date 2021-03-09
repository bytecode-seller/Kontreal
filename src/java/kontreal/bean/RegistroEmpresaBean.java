package kontreal.bean;

import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import kontreal.entities.Empresa;
import kontreal.services.EmpresaServiceImpl;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class RegistroEmpresaBean implements Serializable{
    
    @Inject
    private EmpresaServiceImpl empresaService;
    private String nombreEmpresa;
    private String descripcion;
    private Date updated;

    public RegistroEmpresaBean() {
    }

    public RegistroEmpresaBean(String nombreEmpresa, String descripcion, Date updated) {
        this.nombreEmpresa = nombreEmpresa;
        this.descripcion = descripcion;
        this.updated = updated;
    }
    
    @PostConstruct
    public void init(){
        
    }
    
    public void registrar(){
        Empresa empresa = new Empresa();
        FacesContext ctx = FacesContext.getCurrentInstance();
        FacesMessage message = null;
        empresa.setNombre(nombreEmpresa);
        empresa.setDescripcion(descripcion);
        empresa.setUpdated(new Date());
        if(empresaService.guardarEmpresa(empresa)){
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa guardada", "La empresa: " + nombreEmpresa + " se ha guardado con éxito.");
        }else{
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Empresa no guardada", "La empresa " + nombreEmpresa + " ya se ha registrado anteriormente.");
        }
        
        ctx.addMessage(null, message);
        nombreEmpresa = "";
        descripcion = "";
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
}
