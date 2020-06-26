/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Empresa;

/**
 *
 * @author modima65
 */
@ManagedBean
@SessionScoped
public class SessionBean implements Serializable {

    private String empresa;
    private Empresa selectedEmpresa;
    private Map<String, Empresa> empresasConverter;

    /**
     * Creates a new instance of sessionBean
     */
    public SessionBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
    }

    @PostConstruct
    private void initData() {
        empresasConverter();
    }
    
    public void reset() {
        empresa = null;
        selectedEmpresa = null;
    }

    public void empresaListener() {
        selectedEmpresa = empresasConverter.get(empresa);
    }

    private void empresasConverter() {
        empresasConverter = new TreeMap<>();
        empresasConverter.put("- Todas -", null);

        for (Empresa emp : EmpresaDao.searchAll()) {
            empresasConverter.put(emp.getNombre(), emp);
        }
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Map<String, Empresa> getEmpresasConverter() {
        return empresasConverter;
    }

    public Empresa getSelectedEmpresa() {
        return selectedEmpresa;
    }
}
