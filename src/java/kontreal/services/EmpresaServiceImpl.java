package kontreal.services;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Empresa;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class EmpresaServiceImpl implements Serializable{
    
    public boolean guardarEmpresa(Empresa empresa){
        
        if(empresa.getNombre().trim() == "" || empresa.getNombre()==null)
            return false;
        
        if(EmpresaDao.exists(empresa.getNombre()))
            return false;
        
        EmpresaDao.save(empresa);
        return true;
    }
}
