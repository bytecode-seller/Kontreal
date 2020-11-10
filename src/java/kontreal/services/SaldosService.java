package kontreal.services;

import java.util.List;
import javax.ejb.Remote;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;

/**
 *
 * @author Martin Tepostillo
 */
@Remote
public interface SaldosService {
    public List<Balanza> getSaldos(Empresa empresa,int ejercicio);
    public List<Balanza> getSaldos(Empresa empresa, Cuenta cuenta, int ejercicio);
}
