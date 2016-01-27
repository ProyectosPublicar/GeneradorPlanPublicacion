/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Configuracion.modelo;

/**
 *
 * @author Administrador
 */
public class Componente {
    
    private int codigo;
    private String nombre;
    private int coordenada_inicio_x;
    private int coordenada_inicio_y;
    private int ancho;
    private int alto;
    private int color_fondo_red;
    private int color_fondo_green;
    private int color_fondo_blue;
    private String tipoFuente;
    private int tamano_fuente;
    private int color_fuente_red;
    private int color_fuente_blue;
    private int color_fuente_green;
    private String objeto;
    private String atributo;
    private String descripcion;
    private Boolean esDinamico;
    private String label;
    private int coordenada_escribir_x;
    private int coordenada_escribir_y;
    

    /**
     * @return Código del Componente
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * @param Código del Componente
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * @return Nombre del Componente
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param Nombre del Componente
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return Coordenada de Inicio en X
     */
    public int getCoordenada_inicio_x() {
        return coordenada_inicio_x;
    }

    /**
     * @param Coordenada de Inicio en X
     */
    public void setCoordenada_inicio_x(int coordenada_inicio_x) {
        this.coordenada_inicio_x = coordenada_inicio_x;
    }

    /**
     * @return Coordenada de Inicio en Y
     */
    public int getCoordenada_inicio_y() {
        return coordenada_inicio_y;
    }

    /**
     * @param Coordenada de Inicio en Y
     */
    public void setCoordenada_inicio_y(int coordenada_inicio_y) {
        this.coordenada_inicio_y = coordenada_inicio_y;
    }

    /**
     * @return Ancho del Componente
     */
    public int getAncho() {
        return ancho;
    }

    /**
     * @param Ancho del Componente
     */
    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    /**
     * @return Alto del Componente
     */
    public int getAlto() {
        return alto;
    }

    /**
     * @param Alto del Componente
     */
    public void setAlto(int alto) {
        this.alto = alto;
    }

    /**
     * @return Color de fondo componente rojo
     */
    public int getColor_fondo_red() {
        return color_fondo_red;
    }

    /**
     * @param Color de fondo componente rojo
     */
    public void setColor_fondo_red(int color_fondo_red) {
        this.color_fondo_red = color_fondo_red;
    }

    /**
     * @return Color de fondo componente verde
     */
    public int getColor_fondo_green() {
        return color_fondo_green;
    }

    /**
     * @param Color de fondo componente verde
     */
    public void setColor_fondo_green(int color_fondo_green) {
        this.color_fondo_green = color_fondo_green;
    }

    /**
     * @return Color de fondo componente azul
     */
    public int getColor_fondo_blue() {
        return color_fondo_blue;
    }

    /**
     * @param Color de fondo componente azul
     */
    public void setColor_fondo_blue(int color_fondo_blue) {
        this.color_fondo_blue = color_fondo_blue;
    }

    /**
     * @return Tipo de Fuente
     */
    public String getTipoFuente() {
        return tipoFuente;
    }

    /**
     * @param Tipo de Fuente
     */
    public void setTipoFuente(String tipoFuente) {
        this.tipoFuente = tipoFuente;
    }

   
    /**
     * @return Objeto en el que viene el valor del campo
     */
    public String getObjeto() {
        return objeto;
    }

    /**
     * @param Objeto en el que viene el valor del campo
     */
    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    /**
     * @return Atributo en el que viene el valor del campo
     */
    public String getAtributo() {
        return atributo;
    }

    /**
     * @param Atributo en el que viene el valor del campo
     */
    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    /**
     * @return Descripción del Componente
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param Descripción del Componente
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return Indica si el componente es dinámico o es estático
     */
    public Boolean getEsDinamico() {
        return esDinamico;
    }

    /**
     * @param es Indica si el componente es dinámico o es estático
     */
    public void setEsDinamico(Boolean esDinamico) {
        this.esDinamico = esDinamico;
    }

    /**
     * @return Indica el Label del Componente
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param Indica el Label del Componente
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return Coordenada X en la que se comienza a escribir
     */
    public int getCoordenada_escribir_x() {
        return coordenada_escribir_x;
    }

    /**
     * @param Coordenada X en la que se comienza a escribir
     */
    public void setCoordenada_escribir_x(int coordenada_escribir_x) {
        this.coordenada_escribir_x = coordenada_escribir_x;
    }

    /**
     * @return Coordenada Y en la que se comienza a escribir
     */
    public int getCoordenada_escribir_y() {
        return coordenada_escribir_y;
    }

    /**
     * @param Coordenada Y en la que se comienza a escribir
     */
    public void setCoordenada_escribir_y(int coordenada_escribir_y) {
        this.coordenada_escribir_y = coordenada_escribir_y;
    }

    /**
     * @return Tamaño de la Fuente
     */
    public int getTamano_fuente() {
        return tamano_fuente;
    }

    /**
     * @param Tamaño de la Fuente
     */
    public void setTamano_fuente(int tamano_fuente) {
        this.tamano_fuente = tamano_fuente;
    }

    /**
     * @return Componente rojo del color de la Fuente
     */
    public int getColor_fuente_red() {
        return color_fuente_red;
    }

    /**
     * @param Componente rojo del color de la Fuente
     */
    public void setColor_fuente_red(int color_fuente_red) {
        this.color_fuente_red = color_fuente_red;
    }

    /**
     * @return Componente azul del color de la Fuente
     */
    public int getColor_fuente_blue() {
        return color_fuente_blue;
    }

    /**
     * @param Componente azul del color de la Fuente
     */
    public void setColor_fuente_blue(int color_fuente_blue) {
        this.color_fuente_blue = color_fuente_blue;
    }

    /**
     * @return Componente verde del color de la Fuente
     */
    public int getColor_fuente_green() {
        return color_fuente_green;
    }

    /**
     * @param Componente verde del color de la Fuente
     */
    public void setColor_fuente_green(int color_fuente_green) {
        this.color_fuente_green = color_fuente_green;
    }

}
