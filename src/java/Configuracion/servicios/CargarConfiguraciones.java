package Configuracion.servicios;

import Configuracion.clases.LeerParametrosDB;
import Configuracion.clases.ParametrosDB;
import Configuracion.modelo.Componente;
import Configuracion.modelo.Configuracion;
import Configuracion.modelo.Seccion;
import Configuracion.modelo.TipoPagina;
import Configuracion.modelo.Background_Structure;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que se encarga de la Carga de la información de Configuraciones
 * existente en la base de datos.
 *
 * @author dlopez
 */
public class CargarConfiguraciones {

    public List<Configuracion> cargarListaConfiguraciones() throws Exception {

        List<Configuracion> listaConfiguraciones = new ArrayList();
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // 2005 version
            Connection con = this.obtenerConexion();
            //Obtener las configuraciones
            listaConfiguraciones = this.obtenerConfiguraciones(con);
            //Obtener las páginas asociadas con cada configuración
            this.obtenerPaginasConfiguracion(con, listaConfiguraciones);
            //Obtener las secciones asociadas con cada configuración
            this.obtenerSeccionesConfiguracion(con, listaConfiguraciones);
            //Obtener los componentes asociados con cada configuración
            this.obtenerComponentesConfiguracion(con, listaConfiguraciones);
            //Cerrar la conexión con la base de datos
            con.close();

        } catch (Exception e) {
            throw e;
        }

        return listaConfiguraciones;

    }

    /**
     * Clase que se encarga de la Carga del color del background existente en la
     * base de datos.
     *
     * @author rparra
     */
    public List<Background_Structure> cargarbackground() throws Exception {

        List<Background_Structure> background = new ArrayList<Background_Structure>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // 2005 version
            Connection con = this.obtenerConexion();
            //Obtener las configuraciones
            background = this.obtenerBackground(con);
            //Obtener las páginas asociadas con cada configuración

            //Cerrar la conexión con la base de datos
            con.close();

        } catch (Exception e) {
            throw e;
        }

        return background;

    }

    /**
     * Método que se encarga de obtener la conexión con la Base de Datos
     *
     * @author dlopez
     */
    private Connection obtenerConexion() throws Exception {

        Connection con = null;
        try {
            //Obtener la Conexión
            LeerParametrosDB leerParams = new LeerParametrosDB();
            ParametrosDB params = leerParams.cargarParametrosDB();
            String url = params.getUrl();
            String database = params.getDatabase();
            String user = params.getUser();
            String password = params.getPassword();
            con = DriverManager.getConnection("jdbc:sqlserver://" + url + ";databaseName=" + database + ";user=" + user + ";password=" + password + ";");
        } catch (Exception e) {
            throw e;
        }
        return con;
    }

    /**
     * Método que se encarga de cargar la lista de las diferentes
     * configuraciones existentes.
     */
    private List<Configuracion> obtenerConfiguraciones(Connection con) throws Exception {

        List<Configuracion> lista = new ArrayList<Configuracion>();
        try {
            //Obtener los diferentes objetos de tipo Configuración existentes en la Base de Datos
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Configuracion");
            while (rs.next()) {
                Configuracion config = new Configuracion();
                String identificador = rs.getString("identificador");
                /**
                 * @author Jonathan Pachón
                 */
                System.out.println("-------------------------->" + identificador);
                /**
                 *
                 */
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String paginacion = rs.getString("paginacion");
                String plantillaNoDisponible = rs.getString("plantilla_no_disponible");
                String plantillaEnProceso = rs.getString("plantilla_en_proceso");
                String plantillaErrorGeneracion = rs.getString("plantilla_error_generacion");
                config.setIdentificador(identificador);
                config.setNombre(nombre);
                config.setDescripcion(descripcion);
                List<TipoPagina> listaPaginas = new ArrayList<TipoPagina>();
                /**
                 * @author Jonathan Pachón
                 */
//                int i = 0;
//                System.out.println("////////////////////////////////////////////////");
//                System.out.println(listaPaginas.get(i).getCodigo());
//                System.out.println(listaPaginas.get(i).getNombre());
//                System.out.println(listaPaginas.get(i).getArchivo_plantilla());
//                System.out.println(listaPaginas.get(i).getSeccionesPagina());
//                System.out.println("////////////////////////////////////////////////");
//                i++;
                /**
                 *
                 */
                config.setPaginasConfiguracion(listaPaginas);
                config.setLlevaPaginacion(paginacion);
                config.setPlantillaAvisoGenesisEnProceso(plantillaEnProceso);
                config.setPlantillaAvisoGenesisNoDisponible(plantillaNoDisponible);
                config.setPlantillaErrorGeneracionAviso(plantillaErrorGeneracion);
                lista.add(config);
                /**
                 *
                 */
                /**
                 *
                 */
            }
            /**
             *
             */
            System.out.println("//////////////// Tamaño lista Config /////////////////");
            System.out.println(lista.size());
            System.out.println("//////////////////////////////////////////////////////");
            /**
             *
             */
            rs.close();
            stmt.close();

        } catch (Exception e) {
            throw e;
        }
        return lista;
    }

    /**
     * Método que se encarga de cargar la lista del Background
     */
    private List<Background_Structure> obtenerBackground(Connection con) throws Exception {
        List<Background_Structure> lista = new ArrayList<Background_Structure>();

        try {
            //Obtener los diferentes objetos de tipo Configuración existentes en la Base de Datos
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM background");
            while (rs.next()) {
                Background_Structure Background = new Background_Structure();
                List<Float> Color = new ArrayList();
                float red = rs.getFloat("red");
                Color.add(red);
                float green = rs.getFloat("green");
                Color.add(green);
                float blue = rs.getFloat("blue");
                Color.add(blue);
                int Code_Part_product = rs.getInt("Code_part_Product");
                String Name_Part_product = rs.getString("Name_Part_Product");
                Background.setBackground_Color(Color);
                Background.setCode_Part_Product(Code_Part_product);
                Background.setName_Part_Product(Name_Part_product);

                lista.add(Background);

            }
            /**
             *
             */
            System.out.println("//////////////// Tamaño lista Background /////////////////");
            System.out.println(lista.size());
            System.out.println("//////////////////////////////////////////////////////");
            /**
             *
             */
        } catch (Exception e) {
            throw e;
        }

        return lista;

    }

    /**
     * Método que se encarga de obtener las páginas asociadas con las diferentes
     * configuraciones cargadas en el Sistema.
     *
     * @author dlopez
     */
    private void obtenerPaginasConfiguracion(Connection con, List<Configuracion> lista) throws Exception {

        try {
            //Obtener los diferentes objetos de tipo Configuración existentes en la Base de Datos
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Paginas_Configuracion WHERE identificador_configuracion=?");
            for (int i = 0; i < lista.size(); i++) {
                Configuracion conf = lista.get(i);
                String idConfiguracion = conf.getIdentificador();
                stmt.setString(1, idConfiguracion);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    TipoPagina tipoPagina = new TipoPagina();
                    int identificador = rs.getInt("identificador");
                    String tipoPlantilla = rs.getString("tipo_pagina");
                    String plantilla = rs.getString("plantilla_usada");
                    tipoPagina.setCodigo(identificador);
                    tipoPagina.setNombre(tipoPlantilla);
                    tipoPagina.setArchivo_plantilla(plantilla);
                    conf.getPaginasConfiguracion().add(tipoPagina);
                    List<Seccion> listaSecciones = new ArrayList<Seccion>();
                    tipoPagina.setSeccionesPagina(listaSecciones);
                }
            }

        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Método que se encarga de obtener las secciones asociadas con las
     * diferentes páginas incluidas dentro de las configuraciones cargadas en el
     * Sistema.
     *
     * @author dlopez
     */
    private void obtenerSeccionesConfiguracion(Connection con, List<Configuracion> lista) throws Exception {

        try {
            //Obtener los diferentes objetos de tipo Configuración existentes en la Base de Datos
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Seccion WHERE identificador_pagina=?");
            for (int i = 0; i < lista.size(); i++) {
                Configuracion conf = lista.get(i);
                List<TipoPagina> listaTipoPaginas = conf.getPaginasConfiguracion();
                for (int j = 0; j < listaTipoPaginas.size(); j++) {
                    TipoPagina tipoP = listaTipoPaginas.get(j);
                    int codigoPagina = tipoP.getCodigo();
                    stmt.setInt(1, codigoPagina);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        Seccion seccion = new Seccion();
                        int codigo = rs.getInt("codigo");
                        String nombre = rs.getString("nombre");
                        int coordenada_inicio_x = rs.getInt("coordenada_inicio_x");
                        int coordenada_inicio_y = rs.getInt("coordenada_inicio_y");
                        int ancho = rs.getInt("ancho");
                        int alto = rs.getInt("alto");
                        int colorFondoRed = rs.getInt("color_fondo_red");
                        int colorFondoBlue = rs.getInt("color_fondo_blue");
                        int colorFondoGreen = rs.getInt("color_fondo_green");
                        String tipoSeccion = rs.getString("tipo_seccion");
                        seccion.setCodigo(codigo);
                        seccion.setNombre(nombre);
                        seccion.setCoordenada_inicio_x(coordenada_inicio_x);
                        seccion.setCoordenada_inicio_y(coordenada_inicio_y);
                        seccion.setAncho(ancho);
                        seccion.setAlto(alto);
                        seccion.setColor_fondo_red(colorFondoRed);
                        seccion.setColor_fondo_green(colorFondoGreen);
                        seccion.setColor_fondo_blue(colorFondoBlue);
                        seccion.setTipo_seccion(tipoSeccion);
                        List<Componente> listaComponentes = new ArrayList<Componente>();
                        seccion.setComponentesSeccion(listaComponentes);
                        tipoP.getSeccionesPagina().add(seccion);
                    }
                }

            }

        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Método que se encarga de obtener los componentes asociados con las
     * diferentes secciones incluidas dentro de una página en las
     * configuraciones cargadas en el Sistema.
     *
     * @author dlopez
     */
    private void obtenerComponentesConfiguracion(Connection con, List<Configuracion> lista) throws Exception {

        try {
            //Obtener los diferentes objetos de tipo Configuración existentes en la Base de Datos
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Componente WHERE codigo_seccion=?");
            for (int i = 0; i < lista.size(); i++) {
                Configuracion conf = lista.get(i);
                List<TipoPagina> listaTipoPaginas = conf.getPaginasConfiguracion();
                for (int j = 0; j < listaTipoPaginas.size(); j++) {
                    TipoPagina tipoP = listaTipoPaginas.get(j);
                    List<Seccion> listaSecciones = tipoP.getSeccionesPagina();
                    for (int k = 0; k < listaSecciones.size(); k++) {
                        Seccion sec = listaSecciones.get(k);
                        int codigoSeccion = sec.getCodigo();
                        stmt.setInt(1, codigoSeccion);
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            Componente comp = new Componente();
                            int codigo = rs.getInt("codigo");
                            String nombre = rs.getString("nombre");
                            int coordenada_inicio_x = rs.getInt("coordenada_inicio_x");
                            int coordenada_inicio_y = rs.getInt("coordenada_inicio_y");
                            int ancho = rs.getInt("ancho");
                            int alto = rs.getInt("alto");
                            int colorFondoRed = rs.getInt("color_fondo_red");
                            int colorFondoBlue = rs.getInt("color_fondo_blue");
                            int colorFondoGreen = rs.getInt("color_fondo_green");
                            String tipoFuente = rs.getString("tipo_fuente");
                            String atributo = rs.getString("atributo");
                            String descripcion = rs.getString("descripcion");
                            Boolean esDinamico = rs.getBoolean("esDinamico");
                            String label = rs.getString("label");
                            int coordenada_escribir_x = rs.getInt("coordenada_escribir_x");
                            int coordenada_escribir_y = rs.getInt("coordenada_escribir_y");
                            int tamano_fuente = rs.getInt("tamano_fuente");
                            int colorFuenteRed = rs.getInt("color_fuente_red");
                            int colorFuenteBlue = rs.getInt("color_fuente_blue");
                            int colorFuenteGreen = rs.getInt("color_fuente_green");

                            comp.setCodigo(codigo);
                            comp.setNombre(nombre);
                            comp.setCoordenada_inicio_x(coordenada_inicio_x);
                            comp.setCoordenada_inicio_y(coordenada_inicio_y);
                            comp.setAncho(ancho);
                            comp.setAlto(alto);
                            comp.setColor_fondo_blue(colorFondoBlue);
                            comp.setColor_fondo_green(colorFondoGreen);
                            comp.setColor_fondo_red(colorFondoRed);
                            comp.setTipoFuente(tipoFuente);
                            comp.setAtributo(atributo);
                            comp.setDescripcion(descripcion);
                            comp.setEsDinamico(esDinamico);
                            comp.setLabel(label);
                            comp.setCoordenada_escribir_x(coordenada_escribir_x);
                            comp.setCoordenada_escribir_y(coordenada_escribir_y);
                            comp.setTamano_fuente(tamano_fuente);
                            comp.setColor_fuente_blue(colorFuenteBlue);
                            comp.setColor_fuente_red(colorFuenteRed);
                            comp.setColor_fuente_green(colorFuenteGreen);
                            sec.getComponentesSeccion().add(comp);

                        }
                    }
                }
            }

        } catch (Exception e) {
            throw e;
        }

    }

}
