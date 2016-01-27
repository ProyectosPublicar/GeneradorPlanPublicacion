package Configuracion.modelo;

import java.util.List;

public class Configuracion {
    
    private String identificador;
    private String nombre;
    private String descripcion;
    private List<TipoPagina> paginasConfiguracion;
    private String llevaPaginacion;
    private String plantillaAvisoGenesisNoDisponible;
    private String plantillaAvisoGenesisEnProceso;
    private String plantillaErrorGeneracionAviso;

    public Configuracion(){
        
    }
    
    /**
     * @return the identificador
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * @param identificador the identificador to set
     */
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return Listado de páginas asociado con la configuración
     */
    public List<TipoPagina> getPaginasConfiguracion() {
        return paginasConfiguracion;
    }

    /**
     * @param Listado de páginas asociado con la configuración
     */
    public void setPaginasConfiguracion(List<TipoPagina> paginasConfiguracion) {
        this.paginasConfiguracion = paginasConfiguracion;
    }

    /**
     * @return the llevaPaginacion
     */
    public String getLlevaPaginacion() {
        return llevaPaginacion;
    }

    /**
     * @param llevaPaginacion the llevaPaginacion to set
     */
    public void setLlevaPaginacion(String llevaPaginacion) {
        this.llevaPaginacion = llevaPaginacion;
    }

    /**
     * @return the plantillaAvisoGenesisNoDisponible
     */
    public String getPlantillaAvisoGenesisNoDisponible() {
        return plantillaAvisoGenesisNoDisponible;
    }

    /**
     * @param plantillaAvisoGenesisNoDisponible the plantillaAvisoGenesisNoDisponible to set
     */
    public void setPlantillaAvisoGenesisNoDisponible(String plantillaAvisoGenesisNoDisponible) {
        this.plantillaAvisoGenesisNoDisponible = plantillaAvisoGenesisNoDisponible;
    }

    /**
     * @return the plantillaAvisoGenesisEnProceso
     */
    public String getPlantillaAvisoGenesisEnProceso() {
        return plantillaAvisoGenesisEnProceso;
    }

    /**
     * @param plantillaAvisoGenesisEnProceso the plantillaAvisoGenesisEnProceso to set
     */
    public void setPlantillaAvisoGenesisEnProceso(String plantillaAvisoGenesisEnProceso) {
        this.plantillaAvisoGenesisEnProceso = plantillaAvisoGenesisEnProceso;
    }
    
      /**
     * @return the plantillaErrorGeneracionAviso
     */
    public String getPlantillaErrorGeneracionAviso() {
        return this.plantillaErrorGeneracionAviso;
    }

    /**
     * @param plantillaErrorGeneracionAviso the plantillaAvisoGenesisEnProceso to set
     */
    public void setPlantillaErrorGeneracionAviso(String plantillaErrorGeneracionAviso) {
        this.plantillaErrorGeneracionAviso = plantillaErrorGeneracionAviso;
    }


}
