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
    public List<Balanza> getSaldos(Empresa empresa, int ejercicio, int primero, int pageSize) {
        return BalanzaDao.searchSaldos(empresa, ejercicio, primero, pageSize);
    }

    @Override
    public List<Balanza> getSaldos(Empresa empresa, Cuenta cuenta, int ejercicio, int primero, int pageSize) {
        return BalanzaDao.searchSaldos(empresa, cuenta, ejercicio, primero, pageSize);
    }

    @Override
    public int numSaldosEmpresa(Empresa empresa, int ejercicio) {
        return BalanzaDao.searchNumSaldos(empresa, ejercicio);
    }
    
    @Override
    public int numSaldosEmpresaCuenta(Empresa empresa,Cuenta cuenta, int ejercicio) {
        return BalanzaDao.numSaldosEmpresaCuenta(empresa, cuenta, ejercicio);
    }

}
