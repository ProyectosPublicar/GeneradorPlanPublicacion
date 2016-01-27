package Configuracion.clases;

import Configuracion.modelo.Configuracion;
import Configuracion.servicios.CargarConfiguraciones;
import java.util.ArrayList;
import java.util.List;
import Configuracion.modelo.Background_Structure;

/**
 * Singleton que instancia en memoria la configuración para la Generación de los
 * pdfs del plan de publicación
 *
 * @author dlopez
 */
public class ListaConfiguraciones {

    private static ListaConfiguraciones listaConfiguraciones;
    private static List<Configuracion> listaDeConfiguraciones;
    private static boolean yaCreado;
    private static List<Background_Structure> background; // estructura que contiene la informacion del color del fondo de los avisos

    private ListaConfiguraciones() {
    }

    //MÉTODO PARA OBTENER LA INSTANCIA
    public static ListaConfiguraciones getInstance() throws Exception {

        if (yaCreado == false) {
            listaConfiguraciones = new ListaConfiguraciones();

            try {
                listaDeConfiguraciones = new ArrayList();
                background = new ArrayList();
                CargarConfiguraciones cargar = new CargarConfiguraciones();
                listaDeConfiguraciones = cargar.cargarListaConfiguraciones();
                background = cargar.cargarbackground();// llama el metodo que carga los datos necesarios para armar el color del fondo 

            } catch (Exception ex) {
                throw ex;
            }
            //yaCreado = true; 
        }
        return listaConfiguraciones;
    }

    //METODO PARA OBTENER LA LISTA DE CONFIGURACIONES
    public List<Configuracion> getListaDeConfiguraciones() {
        return this.listaDeConfiguraciones;
    }

    public List<Background_Structure> getbackground() {
        return this.background;
    }

}
