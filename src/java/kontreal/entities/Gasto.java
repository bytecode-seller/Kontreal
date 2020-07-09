package kontreal.entities;
// Generated 16-may-2015 17:28:00 by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Gasto generated by hbm2java
 */
public class Gasto  implements java.io.Serializable {


     private Integer idGasto;
     private Concepto concepto;
     private Empresa empresa;
     private Factura factura;
     private double importe;
     private Date fecha;
     private String comentario;
     private Date updated;

    public Gasto() {
    }

	
    public Gasto(Concepto concepto, Empresa empresa, Factura factura, double importe, Date fecha, Date updated) {
        this.concepto = concepto;
        this.empresa = empresa;
        this.factura = factura;
        this.importe = importe;
        this.fecha = fecha;
        this.updated = updated;
    }
    public Gasto(Concepto concepto, Empresa empresa, Factura factura, double importe, Date fecha, String comentario, Date updated) {
       this.concepto = concepto;
       this.empresa = empresa;
       this.factura = factura;
       this.importe = importe;
       this.fecha = fecha;
       this.comentario = comentario;
       this.updated = updated;
    }
   
    public Integer getIdGasto() {
        return this.idGasto;
    }
    
    public void setIdGasto(Integer idGasto) {
        this.idGasto = idGasto;
    }
    public Concepto getConcepto() {
        return this.concepto;
    }
    
    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }
    public Empresa getEmpresa() {
        return this.empresa;
    }
    
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
    public Factura getFactura() {
        return this.factura;
    }
    
    public void setFactura(Factura factura) {
        this.factura = factura;
    }
    public double getImporte() {
        return this.importe;
    }
    
    public void setImporte(double importe) {
        this.importe = importe;
    }
    public Date getFecha() {
        return this.fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public String getComentario() {
        return this.comentario;
    }
    
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public Date getUpdated() {
        return this.updated;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }




}

