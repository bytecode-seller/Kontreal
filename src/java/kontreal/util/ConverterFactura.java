/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.util;

import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import kontreal.bean.GastosBean;
import kontreal.entities.Cuenta;
import kontreal.entities.Factura;

/**
 *
 * @author modima65
 */
@FacesConverter(value = "converterFactura")
public class ConverterFactura implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        }

        GastosBean bean = context.getApplication().evaluateExpressionGet(context, "#{gastosBean}", GastosBean.class);

        Factura returnedFactura = bean.getFacturasConverter().get(value);
        return returnedFactura;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (value instanceof Factura) ? ((Factura) value).getRfc() + " - " + ((Factura) value).getFolio() : "";
    }

}
