package modelo.pojo;

public class Paquete {

    private Integer idPaquete;
    private Integer idEnvio;
    private String descripcion;
    private float peso;
    private float dimensionesAlto;
    private float dimensionesAncho;
    private float dimensionesProfundidad;

    public Paquete() {
    }

    public Paquete(Integer idPaquete, Integer idEnvio, String descripcion, float peso, float dimensionesAlto, float dimensionesAncho, float dimensionesProfundidad) {
        this.idPaquete = idPaquete;
        this.idEnvio = idEnvio;
        this.descripcion = descripcion;
        this.peso = peso;
        this.dimensionesAlto = dimensionesAlto;
        this.dimensionesAncho = dimensionesAncho;
        this.dimensionesProfundidad = dimensionesProfundidad;
    }

    public Integer getIdPaquete() {
        return idPaquete;
    }

    public void setIdPaquete(Integer idPaquete) {
        this.idPaquete = idPaquete;
    }

    public Integer getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(Integer idEnvio) {
        this.idEnvio = idEnvio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public float getDimensionesAlto() {
        return dimensionesAlto;
    }

    public void setDimensionesAlto(float dimensionesAlto) {
        this.dimensionesAlto = dimensionesAlto;
    }

    public float getDimensionesAncho() {
        return dimensionesAncho;
    }

    public void setDimensionesAncho(float dimensionesAncho) {
        this.dimensionesAncho = dimensionesAncho;
    }

    public float getDimensionesProfundidad() {
        return dimensionesProfundidad;
    }

    public void setDimensionesProfundidad(float dimensionesProfundidad) {
        this.dimensionesProfundidad = dimensionesProfundidad;
    }
    
    

}
