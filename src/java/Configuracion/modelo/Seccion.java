package Configuracion.modelo;

import Configuracion.modelo.Componente;
import java.util.List;

/**
 * Clase en la que se definen los diferentes tipos de Secciones que pueden ser
 * contenidos dentro de una página.
 * @author dlopez
 */
public class Seccion {
    
    private int codigo;
    private String nombre;
    private int coordenada_inicio_x;
    private int coordenada_inicio_y;
    private int ancho;
    private int alto;
    private int color_fondo_red;
    private int color_fondo_green;
    private int color_fondo_blue;
    private String tipo_seccion;
    private List<Componente> componentesSeccion;

    /**
     * @return Código Numérico definido para la sección
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * @param Código Numérico definido para la sección
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * @return Nombre definido para la sección
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param Nombre definido para la sección
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return Coordenada de inicio X definida para la sección
     */
    public int getCoordenada_inicio_x() {
        return coordenada_inicio_x;
    }

    /**
     * @param Coordenada de inicio X definida para la sección
     */
    public void setCoordenada_inicio_x(int coordenada_inicio_x) {
        this.coordenada_inicio_x = coordenada_inicio_x;
    }

    /**
     * @return Coordenada de inicio Y definida para la sección
     */
    public int getCoordenada_inicio_y() {
        return coordenada_inicio_y;
    }

    /**
     * @param Coordenada de inicio Y definida para la sección
     */
    public void setCoordenada_inicio_y(int coordenada_inicio_y) {
        this.coordenada_inicio_y = coordenada_inicio_y;
    }

    /**
     * @return Ancho en puntos definido para la sección
     */
    public int getAncho() {
        return ancho;
    }

    /**
     * @param Ancho en puntos definido para la sección
     */
    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    /**
     * @return Alto en puntos definido para la sección
     */
    public int getAlto() {
        return alto;
    }

    /**
     * @param Alto en puntos definido para la sección
     */
    public void setAlto(int alto) {
        this.alto = alto;
    }

    /**
     * @return Componente red para el color de fondo
     */
    public int getColor_fondo_red() {
        return color_fondo_red;
    }

    /**
     * @param Componente red para el color de fondo
     */
    public void setColor_fondo_red(int color_fondo_red) {
        this.color_fondo_red = color_fondo_red;
    }

    /**
     * @return Componente green para el color de fondo
     */
    public int getColor_fondo_green() {
        return color_fondo_green;
    }

    /**
     * @param Componente green para el color de fondo
     */
    public void setColor_fondo_green(int color_fondo_green) {
        this.color_fondo_green = color_fondo_green;
    }

    /**
     * @return Componente blue para el color de fondo
     */
    public int getColor_fondo_blue() {
        return color_fondo_blue;
    }

    /**
     * @param Componente blue para el color de fondo
     */
    public void setColor_fondo_blue(int color_fondo_blue) {
        this.color_fondo_blue = color_fondo_blue;
    }

    /**
     * @return Lista de componentes que están incluidos dentro de la plantilla
     */
    public List<Componente> getComponentesSeccion() {
        return componentesSeccion;
    }

    /**
     * @param Lista de componentes que están incluidos dentro de la plantilla
     */
    public void setComponentesSeccion(List<Componente> componentesSeccion) {
        this.componentesSeccion = componentesSeccion;
    }

    /**
     * @return Tipo de Sección definido para la sección
     */
    public String getTipo_seccion() {
        return tipo_seccion;
    }

    /**
     * @param Tipo de Sección definido para la sección
     */
    public void setTipo_seccion(String tipo_seccion) {
        this.tipo_seccion = tipo_seccion;
    }

}
