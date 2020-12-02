package kontreal.services;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.dto.ArchivoCuentasDTO;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;
import kontreal.exceptions.CustomException;
import kontreal.util.DateUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class ArchivoCuentasServiceImpl implements ArchivoCuentasService, Serializable{
    
    final static int NIVEL = 0;
    final static int CUENTA = 1;
    final static int NOMBRE = 2;
    final static int TIPO = 3;
    final static int AFECTA = 4;
    final static int DIG = 5;
    final static int EDO = 6;
    final static int DIVISA = 7;

    @Override
    public ArchivoCuentasDTO leerArchivo(String html) {
        
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("tr");
        
        ArchivoCuentasDTO archivoDTO = new ArchivoCuentasDTO();
        archivoDTO.setErrores(new ArrayList<String>());
        
        archivoDTO.setNombreArchivo(doc.getElementById("nombreArchivo").text());
        String nombreEmpresa = getNombreEmpresa(html);
        
        if(!empresaExists(nombreEmpresa)){
            archivoDTO.getErrores().add(kontreal.errors.Error.EMPRESA_NOT_FOUND_ERROR);
            archivoDTO.setValid(false);
            return archivoDTO;
        }else
            archivoDTO.setNombreEmpresa(nombreEmpresa);
        
        String fechaCatalogo = getElement(html,1,0).text();
        
        Date fechaParseada;
        
        try {
            fechaParseada = parseFecha(fechaCatalogo);
        } catch (CustomException ex) {
            archivoDTO.getErrores().add(ex.getValue() + " " + ex.getMensaje());
            archivoDTO.setValid(false);
            return archivoDTO;
        }
        
        DateTime dTemp = new DateTime(fechaParseada);
        Boolean fechaValida = DateUtils.validarUltimoDiaMes(dTemp.getYear(), dTemp.getMonthOfYear(), dTemp.getDayOfMonth());
        
        //Si la fecha de la balanza es invalida se lanza una Excepcion
        if (!fechaValida){
            archivoDTO.getErrores().add(kontreal.errors.Error.INVALID_DATE_ERROR);
            archivoDTO.setValid(false);
            return archivoDTO;
        }
        
        archivoDTO.setFechaCatalogo(fechaParseada);
        
        String fechaDescarga = getElement(html,1,1).text();
        
        Date fechaParseadaDescarga;
        
        try {
            fechaParseadaDescarga = parseFechaDescarga(fechaDescarga);
        } catch (CustomException ex) {
            archivoDTO.getErrores().add(ex.getValue() + " " + ex.getMensaje());
            archivoDTO.setValid(false);
            return archivoDTO;
        }
        
        archivoDTO.setFechaDescarga(fechaParseadaDescarga);
        
        Cuenta c;
        Map<String, String> cuentasNoRegistradas = new HashMap<>();
        ArrayList<Cuenta> registrosCatalogo = new ArrayList<>();
        Empresa e = EmpresaDao.searchByName(nombreEmpresa);
        
        // Comenzamos a leer <tr> a partir del sexto que donde es empiezan los registros y hasta 6 antes del ultimo ya que no tienen datos de interes
        for (int i = 6; i < elements.size() - 6; i++) {
            c = CuentaDao.searchByCuentaAndEmpresa(elements.get(i).child(CUENTA).text(), nombreEmpresa);

            if (c == null) {
                c = new Cuenta();
            } else {
                System.out.println("Id de la cuenta: " + c.getIdCuenta());
                System.out.println("Cuenta encontrada: " + c.getNombre());
                System.out.println("Id empresa: " + c.getEmpresa().getIdEmpresa());
                archivoDTO.setActualizados(archivoDTO.getActualizados()+1); 
            }

            c.setNivel(Integer.parseInt(elements.get(i).child(NIVEL).text()));
            c.setNumeroCuenta(elements.get(i).child(CUENTA).text());
            c.setNombre(elements.get(i).child(NOMBRE).text().toUpperCase());
            c.setTipo(elements.get(i).child(TIPO).text().toUpperCase());
            c.setAfecta(elements.get(i).child(AFECTA).text().toUpperCase());
            c.setDig(elements.get(i).child(DIG).text().toUpperCase());
            c.setDivisa(elements.get(i).child(DIVISA).text().toUpperCase());
            c.setEdo(elements.get(i).child(EDO).text().toUpperCase());
            c.setUpdated(new Date());
            c.setFecha(fechaParseada);
            c.setEmpresa(e);

            registrosCatalogo.add(c);
            c = null;
        }

        archivoDTO.setNumeroRegistros(registrosCatalogo.size());
        archivoDTO.setRegistrosCatalogo(registrosCatalogo);
        
        return archivoDTO;
    }

    private String getNombreEmpresa(String html){
        
        String nombreEmpresa = getElement(html,0, 1).text();
        System.out.println("nombre de la empresa: " + nombreEmpresa);
        
        return nombreEmpresa;
    }
    
    public boolean empresaExists(String nombreEmpresa){
        return EmpresaDao.exists(nombreEmpresa);
    }
    
    public Element getElement(String html, int index, int child){
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("tr");
        
        return elements.get(index).child(child);
    }
    
    public Date parseFecha(String fecha) throws CustomException{
         //Filtramos la cadena del tercer tag <td> para obtener la fecha
        String fechaCatalogo = fecha.substring(23);
        
        //Cambiamos las iniciales del mes por su representacion numerica
        String inicialesMes = fechaCatalogo.substring(3, 6);
        
        fechaCatalogo = fechaCatalogo.replace(inicialesMes.subSequence(0, inicialesMes.length()), DateUtils.inicialesMesPorNumero(inicialesMes).subSequence(0, 2));
        
        //Se hace parse de tipo String a tipo Date
        Date date = null;  
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(fechaCatalogo);
        } catch (ParseException ex) {
            System.out.println("Parsing failed");
            throw new CustomException(kontreal.errors.Error.PARSE_DATE_ERROR_CUENTAS)
                    .entityName(Date.class.getSimpleName())
                    .value(fechaCatalogo);
        }
        System.out.println(fechaCatalogo+"\t"+date);  
        System.out.println("fecha catalogo: " + fechaCatalogo);
        
        return date;
    }
    
    public Date parseFechaDescarga(String fecha) throws CustomException{
         //Filtramos la cadena del tercer tag <td> para obtener la fecha
        String fechaDescarga = fecha.substring(7);
        
        //Cambiamos las iniciales del mes por su representacion numerica
        String inicialesMes = fechaDescarga.substring(3, 6);
        
        fechaDescarga = fechaDescarga.replace(inicialesMes.subSequence(0, inicialesMes.length()), DateUtils.inicialesMesPorNumero(inicialesMes).subSequence(0, 2));
        
        //Se hace parse de tipo String a tipo Date
        Date date = null;  
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(fechaDescarga);
        } catch (ParseException ex) {
            System.out.println("Parsing failed");
            throw new CustomException(kontreal.errors.Error.PARSE_DATE_ERROR_CUENTAS)
                    .entityName(Date.class.getSimpleName())
                    .value(fechaDescarga);
        }
        System.out.println(fechaDescarga+"\t"+date);  
        System.out.println("fecha de descarga del catalogo de cuentas de contpaq: " + fechaDescarga);
        
        return date;
    }
}
