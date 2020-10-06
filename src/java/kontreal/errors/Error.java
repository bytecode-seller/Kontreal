package kontreal.errors;

/**
 *
 * @author Martin Tepostillo
 */
public class Error {
    public final static String INVALID_DATE_ERROR = "Fecha proporcionada es invalida";
    public final static String EMPRESA_NOT_FOUND_ERROR = "La empresa no fue encontrada";
    public final static String FECHA_NOT_FOUND_ERROR = "Fecha no encontrada";
    public final static String PARSE_DATE_ERROR = "Error al leer los datos de fecha, asegurese que el archivo es una balanza de comprobacion";
    public final static String PARSE_DATE_ERROR_CUENTAS = "Error al leer los datos de fecha, asegurese que el archivo es un catalogo de cuentas";
    public final static String CUENTA_NOT_FOUND_ERROR = "La cuenta no fue encontrada";
    public final static String BALANZA_ALREADY_EXISTS = "La balanza ya ha sido cargada anteriormente";
    
    public Error(){
        
    }
}
