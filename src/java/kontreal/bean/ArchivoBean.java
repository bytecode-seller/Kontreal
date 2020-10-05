package kontreal.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import kontreal.dao.BalanzaDao;
import kontreal.dao.LogArchivoBalanzaDao;
import kontreal.dto.ArchivoBalanzaDTO;
import kontreal.entities.Balanza;
import kontreal.entities.LogArchivoBalanza;
import kontreal.services.ArchivoBalanzaService;
import kontreal.util.FileUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Martin Tepostillo
 */
// Comment test
@Named
@ViewScoped
public class ArchivoBean implements Serializable{
    
    final static int CUENTA = 0;
    final static int NOMBRE_CUENTA = 1;
    final static int SALDO_INI_DEUDOR = 2;
    final static int SALDO_INI_ACREEDOR = 3;
    final static int CARGOS = 4;
    final static int ABONOS = 5;
    final static int SALDO_FIN_DEUDOR = 6;
    final static int SALDO_FIN_ACREEDOR = 7;

    public ArchivoBean(UploadedFile file) {
        this.file = file;
    }
    
    @EJB
    private ArchivoBalanzaService archivoBalanzaService;
    private UploadedFile file;
    private Date fechaBalanza;
    private String empresa;
    private Integer numRegistros;
    private boolean fileUploaded;
    private CopyOnWriteArrayList<ArchivoBalanzaDTO> archivosBalanza = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<FacesMessage> errores = new CopyOnWriteArrayList<>();
    private AtomicInteger fileCounter = new AtomicInteger();

    public boolean isFileUploaded() {
        return fileUploaded;
    }

    public void setFileUploaded(boolean fileUploaded) {
        this.fileUploaded = fileUploaded;
    }
    
    public ArchivoBean() {
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Date getFechaBalanza() {
        return fechaBalanza;
    }

    public String getEmpresa() {
        return empresa;
    }

    public Integer getNumRegistros() {
        return numRegistros;
    }
    
    public void setNumRegistros(Integer numRegistros) {
        this.numRegistros = numRegistros;
    }

    public CopyOnWriteArrayList<ArchivoBalanzaDTO> getArchivosBalanza() {
        return archivosBalanza;
    }

    public void setArchivosBalanza(CopyOnWriteArrayList<ArchivoBalanzaDTO> archivosBalanza) {
        this.archivosBalanza = archivosBalanza;
    }
    
    
    
    public void handleFileUpload(FileUploadEvent event){
        
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
        archivosBalanza.add(archivoBalanzaService.filterDataFromHtml(htmlString));
        
        if(fileCounter.decrementAndGet() == 0) {
            int totalArchivos = archivosBalanza.size() -1 ;
            for (int i = totalArchivos; i >=0 ;i--) {
                
                if(!archivosBalanza.get(i).isValid()){
                    LogArchivoBalanza log = new LogArchivoBalanza();
                    for (String error : archivosBalanza.get(i).getErrores()) {
                        FacesContext.getCurrentInstance()
                                .addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error al leer documento",archivosBalanza.get(i).getNombreArchivo() + "\n" + error));
                    }
                    
                    if(archivosBalanza.get(i).getErrores().get(0).equals(kontreal.errors.Error.EMPRESA_NOT_FOUND_ERROR)){
                        log.setMensaje(kontreal.errors.Error.EMPRESA_NOT_FOUND_ERROR);
                    }
                    if(archivosBalanza.get(i).getErrores().get(0).startsWith(kontreal.errors.Error.CUENTA_NOT_FOUND_ERROR)){
                        log.setMensaje("Se cancela por no existir cuenta(s) contables.");
                    }else{
                        log.setMensaje("Error al leer el documento");
                    }
                    
                    log.setFechaCarga(new Date());
                    log.setNombre(archivosBalanza.get(i).getNombreArchivo());
                    log.setFechaArchivo(null);
                    log.setFechaDescarga(null);
                    log.setNumRegistros(0);
                    LogArchivoBalanzaDao.insert(log);
                    System.out.println("Archivo NO valido" + archivosBalanza.get(i).getNombreArchivo() +" " + archivosBalanza.get(i).isValid());
                    archivosBalanza.remove(i);
                }
            }
            if(!archivosIsEmpty()){
                reqContext.execute("PF('confirmDialog').show();");
                FacesMessage msg = new FacesMessage("Successful",  " uploaded");
                FacesContext.getCurrentInstance().addMessage("documento", msg);
                for(int i=0; i<archivosBalanza.size();i++)
                    System.out.println("Archivo VALIDO: "+archivosBalanza.get(i).getNombreArchivo() + " " + archivosBalanza.get(i).isValid()); 
            }
        }else{
            System.out.println("Numero de request: " + fileCounter);
        }
        System.out.println("fin archivo.");
        file = null;
    }
    
    public void save(int index){
        if(archivosBalanza.get(index).isToUpdate())
                BalanzaDao.deleteByDate(archivosBalanza.get(index).getFechaBalanza());
        
        for (Balanza balanza : archivosBalanza.get(index).getRegistrosBalanza()) {
                BalanzaDao.insertOrUpdate(balanza);
        }
        FacesContext.getCurrentInstance().addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_INFO, "Carga exitosa!", "Tu archivo fue cargado con exito") );
        this.setFileUploaded(true);
        LogArchivoBalanza log = new LogArchivoBalanza();
        if(archivosBalanza.get(index).isToUpdate())
            log.setMensaje("Archivo cargado anteriormente y fue reemplazado.");
        else
            log.setMensaje("Tu archivo fue cargado con exito");
        log.setNombre(archivosBalanza.get(index).getNombreArchivo());
        log.setFechaArchivo(archivosBalanza.get(index).getFechaBalanza());
        log.setFechaDescarga(archivosBalanza.get(index).getFechaDescarga());
        log.setFechaCarga(new Date());
        log.setGuardado(true);
        log.setNumRegistros(archivosBalanza.get(index).getNumeroRegistros());
        LogArchivoBalanzaDao.insert(log);
        archivosBalanza.remove(index);
        archivosBalanzaIsEmpty();
    }
    
    public void cancel(){
        RequestContext.getCurrentInstance().execute("PF('confirmDialog').hide();");
        this.archivosBalanza.clear();
    }
    
    public void disableUpload(int index){
        LogArchivoBalanza log = new LogArchivoBalanza();
        if(archivosBalanza.get(index).isToUpdate())
            log.setMensaje("Archivo cargado anteriormente y no fue reemplazado.");
        else
            log.setMensaje("Guardado de archivo cancelado");
        log.setNombre(archivosBalanza.get(index).getNombreArchivo());
        log.setFechaCarga(new Date());
        log.setNumRegistros(0);
        LogArchivoBalanzaDao.insert(log);
        this.archivosBalanza.remove(index);
        archivosBalanzaIsEmpty();
    }
    
    public boolean archivosIsEmpty(){    
        return this.archivosBalanza.isEmpty();
    }
    
    public void setFileCounter(ActionEvent e){
        String length  = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("size");
        System.out.println("Numero de archivos a subir: " + length );
        if(length != null) {
            fileCounter.set(Integer.parseInt(length));
        }
    }
    
    private void archivosBalanzaIsEmpty(){
        if(this.archivosBalanza.isEmpty()){
            RequestContext.getCurrentInstance().execute("PF('confirmDialog').hide();");
        }
    }
}
