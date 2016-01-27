package Configuracion.modelo;

import Configuracion.modelo.Seccion;
import java.util.List;

/**
 * Clase en la que se definen los diferentes tipos de Página que puede contener 
 * un documento.
 * @author dlopez
 */
public class TipoPagina {
    
    private int codigo;
    private String nombre;
    private String archivo_plantilla;
    private List<Seccion> seccionesPagina;

    /**
     * @return Código de la Página
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * @return Nombre de la página
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
     * @return Lista de Secciones incluidas en la página
     */
    public List<Seccion> getSeccionesPagina() {
        return seccionesPagina;
    }

    /**
     * @param Lista de Secciones incluidas en la página
     */
    public void setSeccionesPagina(List<Seccion> seccionesPagina) {
        this.seccionesPagina = seccionesPagina;
    }

    /**
     * @return Archivo pdf que va a ser utilizado como plantilla para generar el tipo de página
     */
    public String getArchivo_plantilla() {
        return archivo_plantilla;
    }

    /**
     * @param Archivo pdf que va a ser utilizado como plantilla para generar el tipo de página
     */
    public void setArchivo_plantilla(String archivo_plantilla) {
        this.archivo_plantilla = archivo_plantilla;
    }

}
