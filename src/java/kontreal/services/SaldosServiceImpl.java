package kontreal.services;

import java.util.List;
import javax.ejb.Stateless;
import kontreal.dao.BalanzaDao;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;

/**
 *
 * @author Martin Tepostillo
 */
@Stateless
public class SaldosServiceImpl implements SaldosService{

    @Override
    public List<Balanza> getSaldos(Empresa empresa, int ejercicio) {
        return BalanzaDao.searchSaldos(empresa, ejercicio);
    }

    @Override
    public List<Balanza> getSaldos(Empresa empresa, Cuenta cuenta, int ejercicio) {
        return BalanzaDao.searchSaldos(empresa, cuenta, ejercicio);
    }

}
