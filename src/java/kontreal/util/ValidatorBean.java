/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.util;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author modima65
 */
@ManagedBean
@RequestScoped
public class ValidatorBean implements Serializable {

    /**
     * Creates a new instance of ValidatorBean
     */
    public ValidatorBean() {
    }

    public void validateRFC(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().length() < 12 || !value.toString().matches("[A-Z,Ñ,&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z,0-9]?[A-Z,0-9]?[0-9,A-Z]?")) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "El R.F.C. no es válido", null);
            throw new ValidatorException(message);
        }
    }

    public void validateImporte(FacesContext context, UIComponent component, Object value) throws ValidatorException {
//        double importe = Double.valueOf(value.toString().replace(",", ""));
        double importe = value instanceof Double ? (double) value : (long) value;
        double disponible = (double) component.getAttributes().get("disponible");

        if (importe < 0 || importe > disponible) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "El importe rebasa el límite permitido", null);
            throw new ValidatorException(message);
        }
    }

}
