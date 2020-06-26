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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import kontreal.dao.ConceptoDao;
import kontreal.dao.EmpresaDao;
import kontreal.dao.FacturaDao;
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
public class FacturasBean implements Serializable {

    private final int TIPO;
    private final double IVA;
    private final double RET;
    private List<Factura> facturasData;
    private List<Factura> facturasFilter;
    private Factura selectedFactura;
    private Factura factura;
    private Date desde;
    private Date hasta;
    private Map<String, Concepto> conceptosConverter;
    private Map<String, Empresa> empresasConverter;
    private List<SelectItem> emisores;
    private int documentos;
    private double acumulado;
    private double utilizado;
    private double disponible;
    private boolean conRetencion;
    private boolean modifyMode;

    /**
     * Creates a new instance of FacturasBean
     */
    public FacturasBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
        TIPO = 0;
        IVA = 0.16;
        RET = 0.04;
        desde = DateTime.now().minusMonths(4).dayOfMonth().withMaximumValue().toDate();
        hasta = DateTime.now().hourOfDay().withMaximumValue().toDate();
    }

    @PostConstruct
    private void initData() {
        updateData();
        emisoresItems();
        empresasConverter();
    }

    private void updateData() {
        resetFactura();
        facturasData = FacturaDao.searchAll(desde, hasta);
    }

    private void resetFactura() {
        selectedFactura = null;
        factura = new Factura(null, null, new Date(), null, null, 0.0, IVA, 0.0, 0.0, 0.0, null, new Date(), new HashSet<Gasto>());
        modifyMode = false;
        documentos = 0;
        acumulado = 0.0;
        utilizado = 0.0;
        disponible = 0.0;
    }

    public void dropFilters() {
        desde = DateTime.now().minusMonths(4).dayOfMonth().withMaximumValue().toDate();
        hasta = DateTime.now().hourOfDay().withMaximumValue().toDate();
        updateData();
    }

    public void fechasListener() {
        updateData();
    }

    public void importeListener() {
        factura.setImporteiva(factura.getImportebruto() * (IVA - (conRetencion ? RET : 0)));
        factura.setImporte(factura.getImportebruto() + factura.getImporteiva());
    }

    public void cancelarListener() {
        resetFactura();
    }

    public void aceptarListener() {
        if (modifyMode) {
            if (factura.getImporte() < factura.getImporteutilizado()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El importe es menor al monto ya utilizado", "Ingresa un importe mayor"));
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                FacturaDao.edit(factura);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificación correcta", null));
                updateData();
            }
        } else {
            FacturaDao.insert(factura);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Creación exitosa", null));
            updateData();
        }
    }

    public void eliminarListener() {
        FacturaDao.refresh(selectedFactura);
        if (selectedFactura.getGastos().isEmpty()) {
            FacturaDao.delete(selectedFactura);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminación aplicada.", null));
            updateData();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "La factura no puede eliminarse.", "Mantiene dependencias con gastos."));
        }
    }

    public void chooseModify() {
        modifyMode = true;
        factura = selectedFactura;
        FacturaDao.refresh(factura);
        // A patch due to lack of hashcode.
        empresasConverter.put(factura.getEmpresa().getNombre(), factura.getEmpresa());
        empresaListener();
    }

    public void empresaListener() {
        Object[] totales = FacturaDao.sumAll(factura.getEmpresa());
        documentos = (int) totales[0];
        acumulado = (double) totales[1];
        utilizado = (double) totales[2];
        disponible = (double) totales[3];
        conceptosConverter();
    }

    private void empresasConverter() {
        empresasConverter = new TreeMap<>();
        empresasConverter.put("- Selecciona -", null);

        for (Empresa emp : EmpresaDao.searchAll()) {
            empresasConverter.put(emp.getNombre(), emp);
        }
    }

    private void conceptosConverter() {
        List<Concepto> conceptos = ConceptoDao.searchAll(factura.getEmpresa(), TIPO);
        conceptosConverter = new TreeMap<>();
        conceptosConverter.put("- Selecciona - ", new Concepto());

        for (Concepto con : conceptos) {
            conceptosConverter.put(con.getNombre(), con);
        }
    }

    private void emisoresItems() {
        emisores = new ArrayList<>();
        emisores.add(new SelectItem(null, "- Selecciona -"));

        for (String emisor : FacturaDao.searchEmisores()) {
            emisores.add(new SelectItem(emisor));
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

    public List<Factura> getFacturasData() {
        return facturasData;
    }

    public List<Factura> getFacturasFilter() {
        return facturasFilter;
    }

    public void setFacturasFilter(List<Factura> facturasFilter) {
        this.facturasFilter = facturasFilter;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Factura getSelectedFactura() {
        return selectedFactura;
    }

    public void setSelectedFactura(Factura selectedFactura) {
        this.selectedFactura = selectedFactura;
    }

    public Map<String, Concepto> getConceptosConverter() {
        return conceptosConverter;
    }

    public Map<String, Empresa> getEmpresasConverter() {
        return empresasConverter;
    }

    public int getDocumentos() {
        return documentos;
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

    public boolean isConRetencion() {
        return conRetencion;
    }

    public void setConRetencion(boolean conRetencion) {
        this.conRetencion = conRetencion;
    }

    public List<SelectItem> getEmisores() {
        return emisores;
    }

}
