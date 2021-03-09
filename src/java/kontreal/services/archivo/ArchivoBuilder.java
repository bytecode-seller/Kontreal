package kontreal.services.archivo;

import java.util.ArrayList;
import kontreal.dto.Archivo;

/**
 *
 * @author Martin Tepostillo
 */
public class ArchivoBuilder {
    
    private Archivo archivo;
    private String html;
    
    public ArchivoBuilder(Class<?> tipo){
        this.archivo = new Archivo(tipo);
    }

}
