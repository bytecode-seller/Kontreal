package kontreal.services;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import kontreal.dao.BalanzaDao;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;

/**
 *
 * @author Martin Tepostillo
 */
@ViewScoped
@Named
public class SaldosServiceImpl implements SaldosService, Serializable{

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

    @Override
    public List<Balanza> getSubCuentas(String startsWith, String nombreEmpresa, int ejercicio, int first, int pageSize) {
        List<Balanza> subcue = BalanzaDao.getSubCuentas(startsWith, nombreEmpresa, ejercicio, first, pageSize);
        System.out.println(" sub: "+subcue.get(0).getCuenta().getNombre());
        return subcue;
    }

    @Override
    public int getNumSubCuentas(String startsWith, String nombreEmpresa, int ejercicio) {
        return BalanzaDao.getNumSubCuentas(startsWith, nombreEmpresa, ejercicio).intValue();
    }

}
