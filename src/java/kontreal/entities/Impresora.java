package kontreal.entities;

import java.io.Serializable;

/**
 *
 * @author Martin Tepostillo
 */
public class Impresora implements Serializable{
    
    private Integer idImpresora;
    private String nombre;
    private String printService;
    private String printCharacteristic;

    public Impresora() {
    }

    public Impresora(Integer idImpresora, String nombre, String printService, String printCharacteristic) {
        this.idImpresora = idImpresora;
        this.nombre = nombre;
        this.printService = printService;
        this.printCharacteristic = printCharacteristic;
    }

    public Integer getIdImpresora() {
        return idImpresora;
    }

    public void setIdImpresora(Integer idImpresora) {
        this.idImpresora = idImpresora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrintService() {
        return printService;
    }

    public void setPrintService(String printService) {
        this.printService = printService;
    }

    public String getPrintCharacteristic() {
        return printCharacteristic;
    }

    public void setPrintCharacteristic(String printCharacteristic) {
        this.printCharacteristic = printCharacteristic;
    }
    
}