package kontreal.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import kontreal.dao.BalanzaDao;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;
import kontreal.services.SaldosServiceImpl;
import org.joda.time.DateTime;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author modima65
 */
@ViewScoped
@Named
public class ShowSaldosBean implements Serializable {

    private List<Balanza> saldosData;
    private List<Balanza> saldosFilter;
    private final List<Integer> ejercicios;
    private Map<String, Cuenta> cuentasConverter;
    private Cuenta cuenta;
    private int ejercicio;
    private final String[] meses;
    private List<Empresa> empresas;
    private List<Cuenta> cuentas;
    private Empresa selectedEmpresa;
    boolean changeEmpresa;
    private LazyDataModel<Balanza> saldosBalanza;
    @Inject
    private SessionBean sessionBean;
    @Inject
    private SaldosServiceImpl saldosService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ShowBalanza
     */
    public ShowSaldosBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
        meses = new String[] {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre", };
        ejercicios = BalanzaDao.getEjerciciosBalanza();
        ejercicio = ejercicios.get(ejercicios.size() - 1);
        this.saldosData = new ArrayList<>();
        this.empresas = new ArrayList<>();
        this.cuentas = new ArrayList<>();
    }

    @PostConstruct
    private void initData() {
        System.out.println("Init data");
        this.empresas.addAll( EmpresaDao.searchAll());
        this.cuentas.addAll(CuentaDao.findAll());
        
    }

    private void updateData() {
        int nSaldos = saldosService.numSaldosEmpresa(selectedEmpresa, ejercicio);
        
        if(this.selectedEmpresa != null){
            cuentas.addAll(CuentaDao.searchAll(selectedEmpresa));
            saldosData.clear();
            
            saldosBalanza = new LazyDataModel<Balanza>(){
                @Override
                public List<Balanza> load(int first ,int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters){
                    return saldosService.getSaldos(selectedEmpresa, ejercicio, first, pageSize);
                }
            };
            saldosBalanza.setRowCount(nSaldos);
        }else{
            FacesMessage f = new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin empresa", "Seleccione una empresa para mostrar datos");
            FacesContext.getCurrentInstance().addMessage("dataTableSaldos", f);
        }
        
    }
    
    public void searchByCuenta(){
        if(this.cuenta == null){
            this.listenerParams();
        }else {
            int nSaldos = saldosService.numSaldosEmpresaCuenta(selectedEmpresa, cuenta,ejercicio);
            saldosBalanza = new LazyDataModel<Balanza>(){
                @Override
                public List<Balanza> load(int first ,int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters){
                    return saldosService.getSaldos(selectedEmpresa,cuenta, ejercicio, first, pageSize);
                }
            };
            saldosBalanza.setRowCount(nSaldos);
        }
    }
    
    public void subCuentas(String numeroCuenta){
        
        final String startsWith;
        if(numeroCuenta.substring(4,4).equals("0000")){
            startsWith = numeroCuenta.substring(0, 3);
            System.out.println("Primera opcion");
        }else{
            startsWith = numeroCuenta.substring(0, 3);
            System.out.println("Segunda opcion");
        }
        System.out.println("numero cuenta comienza con: " + startsWith);
        
        int nSaldos = saldosService.getNumSubCuentas(startsWith, selectedEmpresa.getNombre(), ejercicio);
        saldosBalanza.setRowCount(nSaldos);
        System.out.println("Numero de conincidencias: " + nSaldos);
        saldosBalanza = new LazyDataModel<Balanza>(){
            @Override
            public List<Balanza> load(int first ,int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters){
                return saldosService.getSubCuentas(startsWith, selectedEmpresa.getNombre(), ejercicio, first, pageSize);
            }
        };
    }
    
    public void listenerChangeEmpresa(){
        this.cuentas.clear();
    }

    public void listenerParams() {
        updateData();
    }

    public String convertMonth(Date fecha) {
        DateTime date = new DateTime(fecha);
        return meses[date.getMonthOfYear() - 1].substring(0, 3) + "-" + date.getYear();
    }

    public List<Balanza> getSaldosData() {
        return saldosData;
    }

    public List<Balanza> getSaldosFilter() {
        return saldosFilter;
    }

    public void setSaldosFilter(List<Balanza> saldosFilter) {
        this.saldosFilter = saldosFilter;
    }

    public List<Integer> getEjercicios() {
        return ejercicios;
    }

    public int getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(int ejercicio) {
        this.ejercicio = ejercicio;
    }

    public Map<String, Cuenta> getCuentasConverter() {
        return cuentasConverter;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public List<Empresa> getEmpresas(){
        return this.empresas;
    }
    
    public void setEmpresas( List<Empresa> empresas){
        this.empresas = empresas;
    }

    public Empresa getSelectedEmpresa() {
        return selectedEmpresa;
    }

    public void setSelectedEmpresa(Empresa selectedEmpresa) {
        this.selectedEmpresa = selectedEmpresa;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public LazyDataModel<Balanza> getSaldosBalanza() {
        return saldosBalanza;
    }

    public void setSaldosBalanza(LazyDataModel<Balanza> saldosBalanza) {
        this.saldosBalanza = saldosBalanza;
    }
    
}
