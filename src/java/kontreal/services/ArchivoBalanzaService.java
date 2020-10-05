package kontreal.services;

import javax.ejb.Remote;
import kontreal.dto.ArchivoBalanzaDTO;

/**
 *
 * @author Martin Tepostillo
 */
@Remote
public interface ArchivoBalanzaService {
    public ArchivoBalanzaDTO filterDataFromHtml(String html);
}
