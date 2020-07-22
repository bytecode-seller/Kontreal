package kontreal.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;
import kontreal.util.DateUtils;
import kontreal.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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

    private UploadedFile file;
    private Date fecha;
    private String empresa;
    private Integer numRegistros;
    private boolean fileUploaded;
    private ArrayList<Cuenta> registrosCuentas = new ArrayList<>();

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

    public void handleFileUpload(FileUploadEvent event) {

        setFile(event.getFile());
        FacesContext context = FacesContext.getCurrentInstance();
        String name = event.getFile().getFileName();
        System.out.println("File name: " + name);

        String type = file.getContentType();
        System.out.println("File type: " + type);

        long size = file.getSize();
        System.out.println("File size: " + size);

        String htmlString = "";
        //Se convierte el archivo de texto a un solo String para procesarlo con Jsoup
        try {
            htmlString = convertFileToString();
        } catch (IOException ex) {
            Logger.getLogger(ArchivoBean.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al convertir archivo a texto");
        }

        //Se  filtran y validan los datos
        try {
            registrosCuentas.addAll(filterCuentasFromHtml(htmlString));
            //Guardar datos
            RequestContext reqContext = RequestContext.getCurrentInstance();
            reqContext.execute("PF('confirmDialog').show();");
        } catch (DateTimeException dte) {
            context.addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la validacion de datos", dte.getMessage()));
        } catch (Exception ex) {
            context.addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la validacion de datos", ex.getMessage()));
        }
        file = null;
    }

    private String convertFileToString() throws IOException {
        String html = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        return html;
    }

    private List<Cuenta> filterCuentasFromHtml(String html) throws DateTimeException, Exception {

        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("tr");

        //El primer elemento <tr> es el nombre de la empresa
        String nombreEmpresa = elements.get(0).child(1).text();
        this.empresa = nombreEmpresa;

        Empresa e = EmpresaDao.searchByName(nombreEmpresa);
        System.out.println("nombre de la empresa: " + nombreEmpresa);

        if (e == null) {
            throw new Exception("El nombre de la empresa no es valido, no se pudo actualizar el catalogo de cuentas.");
        }

        //El segundo elemento <tr> contiene la fecha en la que se hizo la balanza
        String fechaCatalogo = elements.get(1).child(0).text();

        //Filtramos la cadena del tercer tag <td> para obtener la fecha
        fechaCatalogo = fechaCatalogo.substring(23);
        System.out.println("fecha catalogo: " + fechaCatalogo);
        //Cambiamos las iniciales del mes por su representacion numerica
        String inicialesMes = fechaCatalogo.substring(3, 6);

        fechaCatalogo = fechaCatalogo.replace(inicialesMes.subSequence(0, inicialesMes.length()), DateUtils.inicialesMesPorNumero(inicialesMes).subSequence(0, 2));
        Date date = null;
        date = new SimpleDateFormat("dd/MM/yyyy").parse(fechaCatalogo);
        System.out.println(fechaCatalogo + "\t" + date);
        System.out.println("fecha catalogo parsed: " + fechaCatalogo);

        Boolean fechaValida = DateUtils.validarUltimoDiaMes(date.getYear(), date.getMonth(), date.getDate());

        //Si la fecha de la balanza es invalida se lanza una Excepcion
        if (!fechaValida) {
            throw new DateTimeException("La fecha proporcionada es invalida, fecha de catalogo no coincide con fin de mes");
        } else {
            this.fecha = date;
        }

        ArrayList<Cuenta> catalogo = new ArrayList<>();
        Cuenta c;
        // Comenzamos a leer <tr> a partir del sexto que donde es empiezan los registros y hasta 6 antes del ultimo ya que no tienen datos de interes
        for (int i = 6; i < elements.size() - 6; i++) {
            c = CuentaDao.searchByCuentaAndEmpresa(elements.get(i).child(CUENTA).text(), nombreEmpresa);

            if (c == null) {
                c = new Cuenta();
            } else {
                System.out.println("Id de la cuenta: " + c.getIdCuenta());
                System.out.println("Cuenta encontrada: " + c.getNombre());
                System.out.println("Id empresa: " + c.getEmpresa().getIdEmpresa());
            }

            c.setNivel(Integer.parseInt(elements.get(i).child(NIVEL).text()));
            c.setCuenta(elements.get(i).child(CUENTA).text());
            c.setNombre(elements.get(i).child(NOMBRE).text().toUpperCase());
            c.setTipo(elements.get(i).child(TIPO).text().toUpperCase());
            c.setAfecta(elements.get(i).child(AFECTA).text().toUpperCase());
            c.setDig(elements.get(i).child(DIG).text().toUpperCase());
            c.setDivisa(elements.get(i).child(DIVISA).text().toUpperCase());
            c.setEdo(elements.get(i).child(EDO).text().toUpperCase());
            c.setUpdated(new Date());
            c.setFecha(date);
            c.setEmpresa(e);

            catalogo.add(c);
            c = null;
        }

        this.setNumRegistros(catalogo.size());

        return catalogo;
    }

    public void save() {
        for (Cuenta cuenta : registrosCuentas) {
            CuentaDao.insertOrUpdate(cuenta);
        }
        FacesContext.getCurrentInstance()
                .addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_INFO, "Carga exitosa!", "El catalogo de cuentas fue actualizado con exito"));
        this.setFileUploaded(true);
        System.out.println("Guardado");
    }
}
