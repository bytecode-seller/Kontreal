package kontreal.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ejb.EJB;
import javax.enterprise.inject.New;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import kontreal.dao.CuentaDao;
import kontreal.dao.LogArchivoBalanzaDao;
import kontreal.dto.ArchivoCuentasDTO;
import kontreal.entities.Cuenta;
import kontreal.entities.LogArchivoBalanza;
import kontreal.services.ArchivoCuentasService;
import kontreal.services.ArchivoCuentasServiceImpl;
import kontreal.util.FileUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class CuentasArchivoBean implements Serializable {

    final static int NIVEL = 0;
    final static int CUENTA = 1;
    final static int NOMBRE = 2;
    final static int TIPO = 3;
    final static int AFECTA = 4;
    final static int DIG = 5;
    final static int EDO = 6;
    final static int DIVISA = 7;
    @Inject @New(ArchivoCuentasServiceImpl.class)
    private ArchivoCuentasService archivoCuentasService;
    private UploadedFile file;
    private Date fecha;
    private String empresa;
    private Integer numRegistros;
    private boolean fileUploaded;
    private CopyOnWriteArrayList<Cuenta> registrosCuentas = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ArchivoCuentasDTO> archivosCuentas = new CopyOnWriteArrayList<>();
    private AtomicInteger fileCounter = new AtomicInteger();
    
    public CuentasArchivoBean(){
        
    }
    

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Integer getNumRegistros() {
        return numRegistros;
    }

    public void setNumRegistros(Integer numRegistros) {
        this.numRegistros = numRegistros;
    }

    public boolean isFileUploaded() {
        return fileUploaded;
    }

    public void setFileUploaded(boolean fileUploaded) {
        this.fileUploaded = fileUploaded;
    }

    public CopyOnWriteArrayList<Cuenta> getRegistrosCuentas() {
        return registrosCuentas;
    }

    public void setRegistrosCuentas(CopyOnWriteArrayList<Cuenta> registrosCuentas) {
        this.registrosCuentas = registrosCuentas;
    }

    public CopyOnWriteArrayList<ArchivoCuentasDTO> getArchivosCuentas() {
        return archivosCuentas;
    }

    public void setArchivosCuentas(CopyOnWriteArrayList<ArchivoCuentasDTO> archivosCuentas) {
        this.archivosCuentas = archivosCuentas;
    }

    public AtomicInteger getFileCounter() {
        return fileCounter;
    }

    public void setFileCounter(AtomicInteger fileCounter) {
        this.fileCounter = fileCounter;
    }
    

    public void handle(FileUploadEvent event) {

        file = event.getFile();
        String name = file.getFileName();
        System.out.println("File name: " + name);

        String type = file.getContentType();
        System.out.println("File type: " + type);

        long size = file.getSize();
        System.out.println("File size: " + size);
        
        RequestContext reqContext = RequestContext.getCurrentInstance();
        String htmlString = "<h1 id=\"nombreArchivo\">" + name + "</h1>";
        //Se convierte el archivo de texto a un solo String para procesarlo con Jsoup
        try {
            htmlString += FileUtils.convertFileToString(file);
        } catch (IOException ex) {
            System.out.println("Error al convertir archivo a texto");
        }
        //Se  filtran y validan los datos
        archivosCuentas.add(archivoCuentasService.leerArchivo(htmlString));
        
        if(fileCounter.decrementAndGet() == 0) {
            int totalArchivos = archivosCuentas.size() -1 ;
            for (int i = totalArchivos; i >=0 ;i--) {
                
                if(!archivosCuentas.get(i).isValid()){
                    LogArchivoBalanza log = new LogArchivoBalanza();
                    for (String error : archivosCuentas.get(i).getErrores()) {
                        FacesContext.getCurrentInstance()
                                .addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error al leer documento",archivosCuentas.get(i).getNombreArchivo() + "\n" + error));
                    }
                    if(archivosCuentas.get(i).getErrores().get(0).equals(kontreal.errors.Error.EMPRESA_NOT_FOUND_ERROR))
                        log.setMensaje(kontreal.errors.Error.EMPRESA_NOT_FOUND_ERROR);
                    else
                    if(archivosCuentas.get(i).getErrores().get(0).startsWith(kontreal.errors.Error.CUENTA_NOT_FOUND_ERROR))
                        log.setMensaje("Se cancela por no existir cuenta(s) contables.");
                    else
                        log.setMensaje("Error al leer el documento");
                    log.setFechaCarga(new Date());
                    log.setNombre(archivosCuentas.get(i).getNombreArchivo());
                    log.setNumRegistros(0);
                    LogArchivoBalanzaDao.insert(log);
                    System.out.println("Archivo NO valido" + archivosCuentas.get(i).getNombreArchivo() +" " + archivosCuentas.get(i).isValid());
                    archivosCuentas.remove(i);
                }
            }
            if(!archivosIsEmpty()){
                reqContext.execute("PF('confirmDialog').show();");
                FacesMessage msg = new FacesMessage("Successful",  " uploaded");
                FacesContext.getCurrentInstance().addMessage("documento", msg);
            }
        }else{
            System.out.println("Numero de request: " + fileCounter);
        }
        System.out.println("fin archivo.");
        file = null;
    }


    public void save(int index) {

        for (Cuenta cuenta : archivosCuentas.get(index).getRegistrosCatalogo()) {
            CuentaDao.insertOrUpdate(cuenta);
        }
        FacesContext
                .getCurrentInstance()
                .addMessage("documento", 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, 
                                "Carga exitosa!", 
                                "Tu archivo fue cargado con exito" + "\nCargados/Actualizados: " +archivosCuentas.get(index).getNumeroRegistros() +"/"+ archivosCuentas.get(index).getActualizados()));
        this.setFileUploaded(true);
        LogArchivoBalanza log = new LogArchivoBalanza();
        log.setMensaje("Tu archivo fue cargado con exito");
        log.setNombre(archivosCuentas.get(index).getNombreArchivo());
        log.setFechaCarga(new Date());
        log.setFechaArchivo(archivosCuentas.get(index).getFechaCatalogo());
        log.setFechaDescarga(archivosCuentas.get(index).getFechaDescarga());
        log.setGuardado(true);
        log.setNumRegistros(archivosCuentas.get(index).getNumeroRegistros());
        log.setActualizado(archivosCuentas.get(index).getActualizados());
        LogArchivoBalanzaDao.insert(log);
        archivosCuentas.remove(index);
        archivosCuentasIsEmpty();
    }

    public void cancel(){
        RequestContext.getCurrentInstance().execute("PF('confirmDialog').hide();");
        this.archivosCuentas.clear();
    }
    
    public void disableUpload(int index){
        LogArchivoBalanza log = new LogArchivoBalanza();
        log.setMensaje("Guardado de archivo cancelado");
        log.setNombre(archivosCuentas.get(index).getNombreArchivo());
        log.setFechaCarga(new Date());
        log.setNumRegistros(0);
        LogArchivoBalanzaDao.insert(log);
        this.archivosCuentas.remove(index);
        archivosCuentasIsEmpty();
    }
    
    public boolean archivosIsEmpty(){    
        return this.archivosCuentas.isEmpty();
    }
    
    private void archivosCuentasIsEmpty(){
        if(archivosIsEmpty()){
            RequestContext.getCurrentInstance().execute("PF('confirmDialog').hide();");
        }
    }
    
    public void setFileCounter(ActionEvent e){
        String length  = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("size");
        System.out.println("Numero de archivos a subir: " + length );
        if(length != null) {
            fileCounter.set(Integer.parseInt(length));
        }
    }
    
    //    private String convertFileToString() throws IOException {
//        String html = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8))
//                .lines()
//                .collect(Collectors.joining("\n"));
//
//        return html;
//    }
//
//    private List<Cuenta> filterCuentasFromHtml(String html) throws DateTimeException, Exception {
//
//        Document doc = Jsoup.parse(html);
//        Elements elements = doc.getElementsByTag("tr");
//
//        //El primer elemento <tr> es el nombre de la empresa
//        String nombreEmpresa = elements.get(0).child(1).text();
//        this.empresa = nombreEmpresa;
//
//        Empresa e = EmpresaDao.searchByName(nombreEmpresa);
//        System.out.println("nombre de la empresa: " + nombreEmpresa);
//
//        if (e == null) {
//            throw new Exception("El nombre de la empresa no es valido, no se pudo actualizar el catalogo de cuentas.");
//        }
//
//        //El segundo elemento <tr> contiene la fecha en la que se hizo la balanza
//        String fechaCatalogo = elements.get(1).child(0).text();
//
//        //Filtramos la cadena del tercer tag <td> para obtener la fecha
//        fechaCatalogo = fechaCatalogo.substring(23);
//        System.out.println("fecha catalogo: " + fechaCatalogo);
//        //Cambiamos las iniciales del mes por su representacion numerica
//        String inicialesMes = fechaCatalogo.substring(3, 6);
//
//        fechaCatalogo = fechaCatalogo.replace(inicialesMes.subSequence(0, inicialesMes.length()), DateUtils.inicialesMesPorNumero(inicialesMes).subSequence(0, 2));
//        Date date = null;
//        date = new SimpleDateFormat("dd/MM/yyyy").parse(fechaCatalogo);
//        System.out.println(fechaCatalogo + "\t" + date);
//        System.out.println("fecha catalogo parsed: " + fechaCatalogo);
//
//        Boolean fechaValida = DateUtils.validarUltimoDiaMes(date.getYear(), date.getMonth(), date.getDate());
//
//        //Si la fecha de la balanza es invalida se lanza una Excepcion
//        if (!fechaValida) {
//            throw new DateTimeException("La fecha proporcionada es invalida, fecha de catalogo no coincide con fin de mes");
//        } else {
//            this.fecha = date;
//        }
//
//        ArrayList<Cuenta> catalogo = new ArrayList<>();
//        Cuenta c;
//        // Comenzamos a leer <tr> a partir del sexto que donde es empiezan los registros y hasta 6 antes del ultimo ya que no tienen datos de interes
//        for (int i = 6; i < elements.size() - 6; i++) {
//            c = CuentaDao.searchByCuentaAndEmpresa(elements.get(i).child(CUENTA).text(), nombreEmpresa);
//
//            if (c == null) {
//                c = new Cuenta();
//            } else {
//                System.out.println("Id de la cuenta: " + c.getIdCuenta());
//                System.out.println("Cuenta encontrada: " + c.getNombre());
//                System.out.println("Id empresa: " + c.getEmpresa().getIdEmpresa());
//            }
//
//            c.setNivel(Integer.parseInt(elements.get(i).child(NIVEL).text()));
//            c.setCuenta(elements.get(i).child(CUENTA).text());
//            c.setNombre(elements.get(i).child(NOMBRE).text().toUpperCase());
//            c.setTipo(elements.get(i).child(TIPO).text().toUpperCase());
//            c.setAfecta(elements.get(i).child(AFECTA).text().toUpperCase());
//            c.setDig(elements.get(i).child(DIG).text().toUpperCase());
//            c.setDivisa(elements.get(i).child(DIVISA).text().toUpperCase());
//            c.setEdo(elements.get(i).child(EDO).text().toUpperCase());
//            c.setUpdated(new Date());
//            c.setFecha(date);
//            c.setEmpresa(e);
//
//            catalogo.add(c);
//            c = null;
//        }
//
//        this.setNumRegistros(catalogo.size());
//
//        return catalogo;
//    }

//    public void save() {
//        for (Cuenta cuenta : registrosCuentas) {
//            CuentaDao.insertOrUpdate(cuenta);
//        }
//        FacesContext.getCurrentInstance()
//                .addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_INFO, "Carga exitosa!", "El catalogo de cuentas fue actualizado con exito"));
//        this.setFileUploaded(true);
//        System.out.println("Guardado");
//    }
}
