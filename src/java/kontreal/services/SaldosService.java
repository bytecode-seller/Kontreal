package kontreal.services;

import java.util.List;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;

/**
 *
 * @author Martin Tepostillo
 */
public interface SaldosService {
    public List<Balanza> getSaldos(Empresa empresa,int ejercicio, int primero, int pageSize);
    public List<Balanza> getSaldos(Empresa empresa, Cuenta cuenta, int ejercicio, int primero, int pageSize);
    public int numSaldosEmpresa(Empresa empresa, int ejercicio);
    public int numSaldosEmpresaCuenta(Empresa empresa,Cuenta cuenta, int ejercicio);
    public List<Balanza> getSubCuentas(String startsWith, String nombreEmpresa, int ejercicio, int first, int pageSize);
    public int getNumSubCuentas(String startsWith, String nombreEmpresa, int ejercicio);
}
