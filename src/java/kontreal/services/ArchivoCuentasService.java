package kontreal.services;

import javax.ejb.Local;
import kontreal.dto.ArchivoCuentasDTO;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Martin Tepostillo
 */
public interface ArchivoCuentasService {
    
    public ArchivoCuentasDTO leerArchivo(String html);
}
