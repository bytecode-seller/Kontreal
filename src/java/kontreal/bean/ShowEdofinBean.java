/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import kontreal.dao.BalanzaDao;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Balanza;
import kontreal.entities.Empresa;
import org.joda.time.DateTime;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class ShowEdofinBean implements Serializable {

    private Empresa selectedEmpresa;
    private Map<String, Empresa> empresasConverter;

    private List<Balanza> balanzasData;
    private List<Balanza> activoData;
    private List<Balanza> pasivoData;
    private List<Balanza> capitalData;
    private double totActivos;
    private double totPasivos;
    private double totCapital;
    private final List<Date> fechasBalanza;
    private Date fecha;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ShowBalanza
     */
    public ShowEdofinBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
        fechasBalanza = BalanzaDao.getFechasBalanza();
        fecha = fechasBalanza.get(fechasBalanza.size() - 1);
    }

    @PostConstruct
    private void initData() {
        empresasConverter();
        updateData();
    }

    private void updateData() {
        // reporte 1 = Estados Financieros
        activoData = new ArrayList<>();
        pasivoData = new ArrayList<>();
        capitalData = new ArrayList<>();

        totActivos = 0;
        totPasivos = 0;
        totCapital = 0;

        balanzasData = BalanzaDao.searchSaldos2(selectedEmpresa, 1, fecha);
        for (Balanza balanza : balanzasData) {
            if (balanza.getCuenta().getTipo().startsWith("A ACTIVO")) {
                activoData.add(balanza);
                totActivos += balanza.getSaldofin();
            } else if (balanza.getCuenta().getTipo().startsWith("D PASIVO")) {
                pasivoData.add(balanza);
                totPasivos += balanza.getSaldofin();
            } else {
                capitalData.add(balanza);
                totCapital += balanza.getSaldofin();
            }
        }
    }

    public void dropFilters() {
        fecha = fechasBalanza.get(fechasBalanza.size() - 1);
        selectedEmpresa = null;
        updateData();
    }

    public void fechaListener() {
        fecha = new DateTime(fecha).dayOfMonth().withMaximumValue().toDate();
        updateData();
    }

    public void empresaListener() {
        System.out.println("----------- empresa " + (selectedEmpresa == null ? "null" : selectedEmpresa));


        updateData();
    }

    private void empresasConverter() {
        empresasConverter = new TreeMap<>();
        empresasConverter.put("- Todas -", null);

        for (Empresa emp : EmpresaDao.searchAll()) {
            empresasConverter.put(emp.getNombre(), emp);
        }
    }

    public Map<String, Empresa> getEmpresasConverter() {
        return empresasConverter;
    }

    public List<Date> getFechasBalanza() {
        return fechasBalanza;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<Balanza> getActivoData() {
        return activoData;
    }

    public List<Balanza> getPasivoData() {
        return pasivoData;
    }

    public List<Balanza> getCapitalData() {
        return capitalData;
    }

    public double getTotActivos() {
        return totActivos;
    }

    public double getTotPasivos() {
        return totPasivos;
    }

    public double getTotCapital() {
        return totCapital;
    }

    public Empresa getSelectedEmpresa() {
        return selectedEmpresa;
    }

    public void setSelectedEmpresa(Empresa selectedEmpresa) {
        this.selectedEmpresa = selectedEmpresa;
    }

}
