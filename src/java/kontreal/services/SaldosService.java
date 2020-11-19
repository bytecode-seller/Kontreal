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
    public List<Balanza> getSaldos(Empresa empresa,int ejercicio, int primero, int pageSize);
    public List<Balanza> getSaldos(Empresa empresa, Cuenta cuenta, int ejercicio, int primero, int pageSize);
    public int numSaldosEmpresa(Empresa empresa, int ejercicio);
    public int numSaldosEmpresaCuenta(Empresa empresa,Cuenta cuenta, int ejercicio);
}
