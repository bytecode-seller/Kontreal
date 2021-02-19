package kontreal.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import kontreal.entities.LogArchivoBalanza;
import kontreal.services.LogServiceImpl;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class LogArchivoBalanzaBean implements Serializable{
    List<LogArchivoBalanza> logs;
    List<LogArchivoBalanza> filteredLogs;
    Date desdeCarga;
    Date hastaCarga;
    Date desdeConsulta;
    Date hastaConsulta;
    @Inject
    LogServiceImpl logService;

    public LogArchivoBalanzaBean() {
        
    }

    @PostConstruct
    public void initData(){
        logs = new ArrayList<>();
        boolean addAll = logs.addAll( logService.getTwoMontsAgo() );
        if(!addAll)
            System.out.println("No se cargaron los logs");
    }

    public List<LogArchivoBalanza> getLogs() {
        return logs;
    }

    public List<LogArchivoBalanza> getFilteredLogs() {
        return filteredLogs;
    }

    public void setFilteredLogs(List<LogArchivoBalanza> filteredLogs) {
        this.filteredLogs = filteredLogs;
    }

    public void chooseFecha() {
        logs = logService.getByDateWithCritera(desdeCarga,hastaCarga,desdeConsulta, hastaConsulta);
    }
    
    public Date getDesdeCarga() {
        return desdeCarga;
    }

    public void setDesdeCarga(Date desdeCarga) {
        this.desdeCarga = desdeCarga;
    }

    public Date getHastaCarga() {
        return hastaCarga;
    }

    public void setHastaCarga(Date hastaCarga) {
        this.hastaCarga = hastaCarga;
    }

    public Date getDesdeConsulta() {
        return desdeConsulta;
    }

    public void setDesdeConsulta(Date desdeConsulta) {
        this.desdeConsulta = desdeConsulta;
    }

    public Date getHastaConsulta() {
        return hastaConsulta;
    }

    public void setHastaConsulta(Date hastaConsulta) {
        this.hastaConsulta = hastaConsulta;
    }
    
    public void clearDates(){
        this.desdeCarga = null;
        this.desdeConsulta = null;
        this.hastaCarga = null;
        this.hastaConsulta = null;
        this.logs = logService.getTwoMontsAgo();
    }
}
