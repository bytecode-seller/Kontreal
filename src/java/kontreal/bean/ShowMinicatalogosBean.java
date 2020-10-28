/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import kontreal.dao.CuentaDao;
import kontreal.entities.Cuenta;
import kontreal.entities.Lminicatalogo;
import kontreal.entities.Minicatalogo;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class ShowMinicatalogosBean implements Serializable {

    private List<Minicatalogo> minicatalogosData;
    private List<Cuenta> innerCuentasData;
    private List<Cuenta> cuentasData;
    private List<Cuenta> filterData;
    private List<Cuenta> selectedCuentas;
    private Minicatalogo selectedMinicatalogo;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ShowCuenta
     */
    public ShowMinicatalogosBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
    }

    @PostConstruct
    private void initData() {
        innerCuentasData = CuentaDao.searchDistinct(null);
        updateData();
    }

    private void updateData() {
        minicatalogosData = CuentaDao.searchMiniCatalogs();
    }

    public void minicatListener() {
        System.out.println("kontreal.bean.ShowMinicatalogosBean.minicatListener()-------------------: ");
        cuentasData = null;
        selectedCuentas = new ArrayList<>();
        if (selectedMinicatalogo != null) {
            cuentasData = new ArrayList<>(innerCuentasData);
            for (Lminicatalogo lmini : selectedMinicatalogo.getLminicatalogos()) {
                for (Cuenta cuenta : cuentasData) {
                    if (lmini.getCuenta().trim().equals(cuenta.getNumeroCuenta().trim())) {
                        selectedCuentas.add(cuenta);
                        break;
                    }
                }
            }
        }
    }

    public void createListener() {
        selectedMinicatalogo = new Minicatalogo("Nuevo Mini catalogo", "", new Date(), new HashSet<Lminicatalogo>(0));
        minicatalogosData.add(selectedMinicatalogo);
        CuentaDao.insert(selectedMinicatalogo);
        minicatListener();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Mini catálogo creado exitósamente.", ""));
    }

    public void modifyListener() {
        CuentaDao.update(selectedMinicatalogo);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Mini catálogo actualizado exitósamente.", ""));
    }

    public void eliminateListener() {
        CuentaDao.delete(selectedMinicatalogo);
        selectedMinicatalogo = null;
        selectedCuentas = null;
        cuentasData = null;

        updateData();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Mini catálogo eliminado exitósamente.", ""));
    }

    public void restoreListener() {
        selectedCuentas = new ArrayList<>();
        for (Lminicatalogo lmini : selectedMinicatalogo.getLminicatalogos()) {
            for (Cuenta cuenta : cuentasData) {
                if (lmini.getCuenta().trim().equals(cuenta.getNumeroCuenta().trim())) {
                    selectedCuentas.add(cuenta);
                    break;
                }
            }
        }
    }

    public void applyListener() {
        final Date TODAY = new Date();

        for (Cuenta cuenta : selectedCuentas) {
            boolean found = false;
            for (Lminicatalogo lmini : selectedMinicatalogo.getLminicatalogos()) {
                if (lmini.getCuenta().trim().equals(cuenta.getNumeroCuenta().trim())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                StringBuilder regExp = new StringBuilder();
                String[] subLevel = cuenta.getNumeroCuenta().split("-");
                for (int indx = 0; indx < subLevel.length; ++indx) { //It should be indx++ ??
                    if (subLevel[indx].matches("^0*$")) {
                        regExp.append(indx == 0 ? "^" + subLevel[indx] : "-.*");
                    } else {
                        regExp.append(indx == 0 ? "^" : "-").append(subLevel[indx]);
                    }
                }

                selectedMinicatalogo.getLminicatalogos()
                        .add(new Lminicatalogo(selectedMinicatalogo, cuenta.getNumeroCuenta(), cuenta.getNombre(), cuenta.getTipo().substring(0, 1), regExp.toString(),
                                TODAY));
            }
        }

        Iterator iter = selectedMinicatalogo.getLminicatalogos().iterator();
        while (iter.hasNext()) {
            Lminicatalogo lmini = (Lminicatalogo) iter.next();
            boolean found = false;
            for (Cuenta cuenta : selectedCuentas) {
                if (lmini.getCuenta().trim().equals(cuenta.getNumeroCuenta().trim())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                iter.remove();
            }
        }

        CuentaDao.update(selectedMinicatalogo);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Mini catálogo actualizado exitósamente.", ""));
    }

    public int sortBySelection(Cuenta cuenta) {
        return selectedCuentas.contains(cuenta) ? 1 : -1;
    }

    public String fetchTipoGrupo(String tipo) {
        String label = "?";
        if (tipo.startsWith("A") || tipo.startsWith("B")) {
            label = "Activo";
        } else if (tipo.startsWith("D")) {
            label = "Pasivo";
        }
        if (tipo.startsWith("F")) {
            label = "Capital";
        }
        if (tipo.startsWith("G") || tipo.startsWith("H")) {
            label = "Resultados";
        }
        return label;
    }

    public List<Minicatalogo> getMinicatalogosData() {
        return minicatalogosData;
    }

    public Minicatalogo getSelectedMinicatalogo() {
        return selectedMinicatalogo;
    }

    public void setSelectedMinicatalogo(Minicatalogo selectedMinicatalogo) {
        this.selectedMinicatalogo = selectedMinicatalogo;
    }

    public List<Cuenta> getCuentasData() {
        return cuentasData;
    }

    public List<Cuenta> getSelectedCuentas() {
        return selectedCuentas;
    }

    public void setSelectedCuentas(List<Cuenta> selectedCuentas) {
        this.selectedCuentas = selectedCuentas;
    }

    public List<Cuenta> getFilterData() {
        return filterData;
    }

    public void setFilterData(List<Cuenta> filterData) {
        this.filterData = filterData;
    }
}
