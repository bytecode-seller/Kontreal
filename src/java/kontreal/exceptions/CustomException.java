package kontreal.exceptions;

/**
 *
 * @author Martin Tepostillo
 */
public class CustomException extends Exception{
    
    String entityName;
    String mensaje;
    String value;
    
    public CustomException(String message){
        super(message);
        this.mensaje = message;
    }
    
    public CustomException entityName(String entityName){
        this.entityName = entityName;
        return this;
    }
    
    public CustomException value(String value){
        this.value = value;
        return this;
    }
    
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
