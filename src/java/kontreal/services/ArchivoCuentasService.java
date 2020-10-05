package kontreal.services;

import javax.ejb.Remote;
import kontreal.dto.ArchivoCuentasDTO;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Martin Tepostillo
 */
@Remote
public interface ArchivoCuentasService {
    
    public ArchivoCuentasDTO leerArchivo(String html);
}
