package kontreal.services;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import kontreal.dao.ImpresoraDao;
import kontreal.entities.Impresora;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class ImpresoraService implements Serializable {
    public void guardarImpresora(String nombre, String printService, String printCharacteristic){
        Impresora impresora = new Impresora();
        impresora.setNombre(nombre);
        impresora.setPrintService(printService);
        impresora.setPrintCharacteristic(printCharacteristic);
        ImpresoraDao.guardarImpresora(impresora);
    }
    
    public boolean exists(Impresora impresora){
        return ImpresoraDao.exists(impresora);
    }
    
    public List<Impresora> getImpresoras(){
        return ImpresoraDao.getImpresoras();
    }
}
