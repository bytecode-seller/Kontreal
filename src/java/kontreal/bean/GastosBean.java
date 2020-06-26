/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.bean;

import java.io.Serializable;
import java.util.Date;
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
import kontreal.dao.EmpresaDao;
import kontreal.dao.FacturaDao;
import kontreal.dao.GastoDao;
import kontreal.entities.Concepto;
import kontreal.entities.Empresa;
import kontreal.entities.Factura;
import kontreal.entities.Gasto;
import org.joda.time.DateTime;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class GastosBean implements Serializable {

    private final int TIPO;
    private List<Gasto> gastosData;
    private List<Gasto> gastosFilter;
    private Gasto selectedGasto;
    private Gasto gasto;
    private Date desde;
    private Date hasta;
    private Map<String, Concepto> conceptosConverter;
    private Empresa empresa;
    private Map<String, Empresa> empresasConverter;
    private Factura factura;
    private Map<String, Factura> facturasConverter;
    private int gastos;
    private double acumulado;
    private double utilizado;
    private double disponible;
    private boolean modifyMode;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of GastosBean
     */
    public GastosBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
        TIPO = 1;
        desde = DateTime.now().minusMonths(4).dayOfMonth().withMaximumValue().toDate();
        hasta = DateTime.now().hourOfDay().withMaximumValue().toDate();
    }

    @PostConstruct
    private void initData() {
        updateData();
        empresasConverter();
    }

    private void updateData() {
        resetGasto();
        gastosData = GastoDao.searchAll(desde, hasta);
    }

    private void resetGasto() {
        selectedGasto = null;
        gasto = new Gasto(null, null, null, 0.0, new Date(), null, new Date());
        gastos = 0;
        acumulado = 0.0;
        utilizado = 0.0;
        disponible = 0.0;
        modifyMode = false;
        if (empresa != null) {
            empresaOrigenListener();
            facturaListener();
        }
    }

    public void dropFilters() {
        desde = DateTime.now().minusMonths(4).dayOfMonth().withMaximumValue().toDate();
        hasta = DateTime.now().hourOfDay().withMaximumValue().toDate();
        updateData();
    }

    public void fechasListener() {
        updateData();
    }

    public void cancelarListener() {
        empresa = null;
        factura = null;
        resetGasto();
    }

    public void aceptarListener() {
        factura.setImporteutilizado(utilizado + gasto.getImporte());
        gasto.setFactura(factura);
        factura.getGastos().add(gasto);
        FacturaDao.edit(factura);

        if (modifyMode) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificación correcta", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Creación exitosa", null));
        }
        updateData();
    }

    public void eliminarListener() {
        gasto = selectedGasto;
        GastoDao.refresh(gasto);
        factura = gasto.getFactura();
        utilizado = factura.getImporteutilizado();

        factura.setImporteutilizado(utilizado - selectedGasto.getImporte());
        factura.getGastos().remove(selectedGasto);
        FacturaDao.edit(factura);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminación aplicada.", null));
        updateData();
    }

    public void chooseModify() {
        modifyMode = true;
        gasto = selectedGasto;
        GastoDao.refresh(gasto);
        empresa = empresasConverter.get(gasto.getFactura().getEmpresa().getNombre());
        facturasConverter();
        factura = gasto.getFactura();
        gastos = factura.getGastos().size() - 1;
        acumulado = factura.getImporte();
        utilizado = factura.getImporteutilizado() - gasto.getImporte();
        disponible = acumulado - utilizado;

        // A patch due to lack of hashcode.
        empresasConverter.put(gasto.getEmpresa().getNombre(), gasto.getEmpresa());
        conceptosConverter();
    }

    public void empresaOrigenListener() {
        facturasConverter();
    }

    public void facturaListener() {
        FacturaDao.refresh(factura);
        gastos = factura.getGastos().size();
        acumulado = factura.getImporte();
        utilizado = factura.getImporteutilizado();
        disponible = acumulado - utilizado;

        gasto.setImporte(disponible);
    }

    public void empresaDestinoListener() {
        conceptosConverter();
    }

    private void facturasConverter() {
        facturasConverter = new TreeMap<>();
        facturasConverter.put("- Selecciona -", null);

        for (Factura fac : FacturaDao.searchAll(empresa)) {
            facturasConverter.put(fac.getRfc() + " - " + fac.getFolio(), fac);
        }
    }

    private void empresasConverter() {
        empresasConverter = new TreeMap<>();
        empresasConverter.put("- Selecciona -", null);

        for (Empresa emp : EmpresaDao.searchAll()) {
            empresasConverter.put(emp.getNombre(), emp);
        }
    }

    private void conceptosConverter() {
        conceptosConverter = new TreeMap<>();
        conceptosConverter.put("- Selecciona - ", new Concepto());

        if (gasto.getEmpresa() != null) {
            List<Concepto> conceptos = ConceptoDao.searchAll(gasto.getEmpresa(), TIPO);
            for (Concepto con : conceptos) {
                conceptosConverter.put(con.getNombre(), con);
            }
        }
    }

    // Getters & Setters
    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public List<Gasto> getGastosData() {
        return gastosData;
    }

    public List<Gasto> getGastosFilter() {
        return gastosFilter;
    }

    public void setGastosFilter(List<Gasto> gastosFilter) {
        this.gastosFilter = gastosFilter;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public Gasto getSelectedGasto() {
        return selectedGasto;
    }

    public void setSelectedGasto(Gasto selectedGasto) {
        this.selectedGasto = selectedGasto;
    }

    public Map<String, Concepto> getConceptosConverter() {
        return conceptosConverter;
    }

    public Map<String, Empresa> getEmpresasConverter() {
        return empresasConverter;
    }

    public double getAcumulado() {
        return acumulado;
    }

    public double getUtilizado() {
        return utilizado;
    }

    public double getDisponible() {
        return disponible;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Map<String, Factura> getFacturasConverter() {
        return facturasConverter;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public boolean isModifyMode() {
        return modifyMode;
    }

    public int getGastos() {
        return gastos;
    }

}
