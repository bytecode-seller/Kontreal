/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import kontreal.dao.ConceptoDao;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Concepto;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class ConceptosBean implements Serializable {

    private List<Concepto> conceptosData;
    private List<Concepto> conceptosFilter;
    private Concepto concepto;
    private Concepto selectedConcepto;
    private Map<String, Cuenta> cuentasConverter;
    private Empresa selectedEmpresa;
    private Map<String, Empresa> empresasConverter;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ConceptosBean
     */
    public ConceptosBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
    }

    @PostConstruct
    private void initData() {
        conceptosData = ConceptoDao.searchAll();
    }

    public void chooseCreate() {
        concepto = new Concepto();
        selectedEmpresa = null;
        empresasConverter();
    }

    public void chooseModify() {
        concepto = selectedConcepto;
        ConceptoDao.refresh(concepto);
        selectedEmpresa = concepto.getCuenta().getEmpresa();
        cuentasConverter();
    }

    public void empresaListener() {
        cuentasConverter();
    }

    public void applyInsert() {
        CuentaDao.refresh(concepto.getCuenta());
        ConceptoDao.insert(concepto);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Creación exitosa.", null));
        initData();
    }

    public void applyDelete() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (ConceptoDao.delete(selectedConcepto)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminación exitosa.", null));
            selectedConcepto = null;
            initData();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Eliminación cancelada.", "El concepto mantiene dependencias con facturas."));
        }
    }

    public void applyEdit() {
        ConceptoDao.edit(selectedConcepto);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificación exitosa.", null));
        selectedConcepto = null;
    }

    private void empresasConverter() {
        empresasConverter = new TreeMap<>();
        empresasConverter.put("- Selecciona -", null);

        for (Empresa emp : EmpresaDao.searchAll()) {
            empresasConverter.put(emp.getNombre(), emp);
        }
    }

    private void cuentasConverter() {
        cuentasConverter = new TreeMap<>();
        cuentasConverter.put("- Selecciona -", null);

        for (Cuenta cue : CuentaDao.searchGastos(selectedEmpresa)) {
            cuentasConverter.put(cue.getNumeroCuenta() + " - " + cue.getNombre(), cue);
        }
    }

    public List<Concepto> getConceptosData() {
        return conceptosData;
    }

    public List<Concepto> getConceptosFilter() {
        return conceptosFilter;
    }

    public void setConceptosFilter(List<Concepto> conceptosFilter) {
        this.conceptosFilter = conceptosFilter;
    }

    public Concepto getSelectedConcepto() {
        return selectedConcepto;
    }

    public void setSelectedConcepto(Concepto selectedConcepto) {
        this.selectedConcepto = selectedConcepto;
    }

    public Map<String, Cuenta> getCuentasConverter() {
        return cuentasConverter;
    }

    public Map<String, Empresa> getEmpresasConverter() {
        return empresasConverter;
    }

    public Empresa getSelectedEmpresa() {
        return selectedEmpresa;
    }

    public void setSelectedEmpresa(Empresa selectedEmpresa) {
        this.selectedEmpresa = selectedEmpresa;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

}
