/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import kontreal.dao.BalanzaDao;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Balanza;
import kontreal.entities.Empresa;
import kontreal.entities.Lminicatalogo;
import kontreal.entities.Minicatalogo;
import org.joda.time.DateTime;
import org.primefaces.event.data.SortEvent;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class ShowBalanzaBean implements Serializable {

    private List<SelectItem> empresasItems;

    private List<Balanza> balanzasData;
    private List<Balanza> balanzasFilter;

    private String selectedMiniCatalogo;
    private Map<String, Minicatalogo> miniCatalogos;
    private List<String> selectedCuentas;

    private int selectedNivel;
    private Map<String, Integer> niveles;

    private List<String> grupo;
    private Map<String, String> grupos;

    private double[] subTotales;
    private double[] totales;
    private int sortColumn;
    private Date fecha;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ShowBalanza
     */
    public ShowBalanzaBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
    }

    @PostConstruct
    private void initData() {
        minicatalogoConverter();
        gruposConverter();
        nivelesConverter();
        empresasItems();
        dropFilters();
    }

    private void updateData() {
        balanzasData = BalanzaDao.searchBalanza(null, fecha, selectedCuentas, selectedNivel);
        balanzasFilter = balanzasData;
        totales = new double[] {0.00, 0.00, 0.00, 0.00};
    }

    public void updateGroups() {
        if (grupo.size() < 6) {
            Iterator iter = balanzasData.listIterator();
            while (iter.hasNext()) {
                Balanza bal = (Balanza) iter.next();
                if (!grupo.contains("1") && (bal.getCuenta().getTipo().equals("A ACTIVO DEUDOR") || bal.getCuenta().getTipo().equals("B ACTIVO ACREED"))) {
                    if (!grupo.contains("5") && bal.getCuenta().getCuenta().startsWith("103")) {
                        iter.remove();
                        continue;
                    } else if (!bal.getCuenta().getCuenta().startsWith("103")) {
                        iter.remove();
                        continue;
                    }
                }
                if (!grupo.contains("2") && bal.getCuenta().getTipo().equals("D PASIVO ACREED")) {
                    if (!grupo.contains("6") && bal.getCuenta().getCuenta().startsWith("200")) {
                        iter.remove();
                        continue;
                    } else if (!bal.getCuenta().getCuenta().startsWith("200")) {
                        iter.remove();
                        continue;
                    }
                }
                if (!grupo.contains("3") && bal.getCuenta().getTipo().equals("F CAPITAL ACREE")) {
                    iter.remove();
                    continue;
                }
                if (!grupo.contains("4") && (bal.getCuenta().getTipo().equals("H RESULTADOS AC") || bal.getCuenta().getTipo().equals("G RESULTADOS DE"))) {
                    iter.remove();
                    continue;
                }
                if (!grupo.contains("5") && bal.getCuenta().getCuenta().startsWith("103")) {
                    iter.remove();
                    continue;
                }
                if (!grupo.contains("6") && bal.getCuenta().getCuenta().startsWith("200")) {
                    iter.remove();
                }
            }
        }
    }

    public void chooseFecha() {
        fecha = new DateTime(fecha).dayOfMonth().withMaximumValue().toDate();
        miniCatalogoListener();
    }

    public void dropFilters() {
        fecha = DateTime.now().minusMonths(2).dayOfMonth().withMaximumValue().toDate();
        grupo = new ArrayList<>();
        grupo.addAll(grupos.values());
        selectedMiniCatalogo = null;
        selectedNivel = 3;

        miniCatalogoListener();
    }

    public void miniCatalogoListener() {
        selectedCuentas = new ArrayList<>();
        if (selectedMiniCatalogo != null) {
            for (Lminicatalogo lmini : miniCatalogos.get(selectedMiniCatalogo).getLminicatalogos()) {
                selectedCuentas.add(lmini.getRegexpstr());
            }
        }

        updateData();
        updateGroups();
    }
    
    public void nivelListener() {
        updateData();
        updateGroups();
    }

    private void gruposConverter() {
        grupos = new LinkedHashMap<>();
        grupos.put("Activo", "1");
        grupos.put("Pasivo", "2");
        grupos.put("Capital", "3");
        grupos.put("Resultados", "4");
        grupos.put("Clientes", "5");
        grupos.put("Proveedores", "6");
    }

    private void minicatalogoConverter() {
        miniCatalogos = new LinkedHashMap<>();
        miniCatalogos.put("- Selecciona -", null);

        for (Minicatalogo minicat : CuentaDao.searchMiniCatalogs()) {
            miniCatalogos.put(minicat.getNombre(), minicat);
        }
    }

    private void nivelesConverter() {
        niveles = new LinkedHashMap<>();
        niveles.put("- Todo -", 0);
        niveles.put("Mayor", 3);
        niveles.put("SubCuenta", 4);
        niveles.put("SubSubCuenta", 5);
        niveles.put("Grupo 1", 1);
        niveles.put("Grupo 2", 2);
    }

    private void empresasItems() {
        empresasItems = new ArrayList<>();
        empresasItems.add(new SelectItem("", "- Todo -"));

        for (Empresa emp : EmpresaDao.searchAll()) {
            empresasItems.add(new SelectItem(emp.getNombre()));
        }
    }

    public void onSort(SortEvent event) {
        sortColumn = event.getSortColumn().getColumnKey().contains("tipo") ? 2 : event.getSortColumn().getColumnKey().contains("empresa") ? 0 : 1;
        totales = new double[]{0.00, 0.00, 0.00, 0.00};
    }

    public void onFilter() {
        totales = new double[]{0.00, 0.00, 0.00, 0.00};
    }

    public void calculateTotals(Balanza balanza) {
        subTotales = new double[]{0.00, 0.00, 0.00, 0.00};

        String sortValue = (sortColumn == 0 ? balanza.getCuenta().getEmpresa().getNombre()
                : sortColumn == 1 ? balanza.getCuenta().getCuenta() : balanza.getCuenta().getTipo());

        String sortKey;
        boolean previuslyFound = false;
        for (Balanza balFilter : balanzasFilter) {
            sortKey = (sortColumn == 0 ? balFilter.getCuenta().getEmpresa().getNombre()
                    : sortColumn == 1 ? balFilter.getCuenta().getCuenta() : balFilter.getCuenta().getTipo());
            if (sortKey.equals(sortValue)) {
                subTotales[0] += balFilter.getSaldoini();
                subTotales[1] += balFilter.getCargos();
                subTotales[2] += balFilter.getAbonos();
                subTotales[3] += balFilter.getSaldofin();
                previuslyFound = true;
            } else if (previuslyFound) {
                break;
            }
        }

        totales[0] += subTotales[0];
        totales[1] += subTotales[1];
        totales[2] += subTotales[2];
        totales[3] += subTotales[3];
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

    public List<Balanza> getBalanzasData() {
        return balanzasData;
    }

    public List<Balanza> getBalanzasFilter() {
        return balanzasFilter;
    }

    public void setBalanzasFilter(List<Balanza> balanzasFilter) {
        this.balanzasFilter = balanzasFilter;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<String> getGrupo() {
        return grupo;
    }

    public void setGrupo(List<String> grupo) {
        this.grupo = grupo;
    }

    public Map<String, String> getGrupos() {
        return grupos;
    }

    public List<SelectItem> getEmpresasItems() {
        return empresasItems;
    }

    public double[] getSubTotales() {
        return subTotales;
    }

    public int getSortColumn() {
        return sortColumn;
    }

    public double[] getTotales() {
        return totales;
    }

    public String getSelectedMiniCatalogo() {
        return selectedMiniCatalogo;
    }

    public void setSelectedMiniCatalogo(String selectedMiniCatalogo) {
        this.selectedMiniCatalogo = selectedMiniCatalogo;
    }

    public Map<String, Minicatalogo> getMiniCatalogos() {
        return miniCatalogos;
    }

    public Map<String, Integer> getNiveles() {
        return niveles;
    }

    public int getSelectedNivel() {
        return selectedNivel;
    }

    public void setSelectedNivel(int selectedNivel) {
        this.selectedNivel = selectedNivel;
    }
}
