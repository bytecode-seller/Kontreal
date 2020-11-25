package kontreal.services;

import javax.ejb.Local;
import kontreal.dto.ArchivoBalanzaDTO;

/**
 *
 * @author Martin Tepostillo
 */
public interface ArchivoBalanzaService {
    public ArchivoBalanzaDTO filterDataFromHtml(String html);
}
