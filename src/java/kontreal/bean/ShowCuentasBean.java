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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class ShowCuentasBean implements Serializable {
    private List<Cuenta> cuentasData;
    private List<Cuenta> cuentasFilter;
    private String empresa;
    private Empresa selectedEmpresa;
    private Map<String, Empresa> empresasConverter;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ShowCuenta
     */
    public ShowCuentasBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
    }

    @PostConstruct
    private void initData() {
        empresasConverter();
        updateData();
    }

    private void updateData() {
        cuentasData = CuentaDao.searchAll(selectedEmpresa);
        cuentasFilter = null;
    }

    public void dropFilter() {
        selectedEmpresa = null;
        empresa = null;
        updateData();
    }
    
    public void empresaListener() {
        selectedEmpresa = empresasConverter.get(empresa);
        updateData();
    }

    public void chooseCuenta() {
    }

    private void empresasConverter() {
        empresasConverter = new TreeMap<>();
        empresasConverter.put("- Selecciona -", null);

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

    public List<Cuenta> getCuentasData() {
        return cuentasData;
    }

    public List<Cuenta> getCuentasFilter() {
        return cuentasFilter;
    }

    public void setCuentasFilter(List<Cuenta> cuentasFilter) {
        this.cuentasFilter = cuentasFilter;
    }

}
