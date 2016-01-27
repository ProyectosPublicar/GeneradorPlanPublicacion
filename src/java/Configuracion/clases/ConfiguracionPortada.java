package Configuracion.clases;

import Configuracion.modelo.Configuracion;
import Configuracion.modelo.ConstantesTipoPaginas;
import Configuracion.modelo.TipoPagina;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class ConfiguracionPortada {

    public TipoPagina llevaPortadaPagina(List<Configuracion> configuraciones, String idConfiguracion) {
        System.out.println("---------------------------------CONFIG PORTADA INICIO-------------------------------------");
        //Recorrer la lista de configuraciones
        for (int i = 0; i < configuraciones.size(); i++) {
            System.out.println("->Configuracion-> " + configuraciones.get(i));
            Configuracion conf = configuraciones.get(i);
            System.out.println("->TipoConfig->" + conf.getIdentificador());
            String tipoConfiguracion = conf.getIdentificador();
            System.out.println("->TemplateCode->" + idConfiguracion);
            if (tipoConfiguracion.equalsIgnoreCase(idConfiguracion)) {
                List<TipoPagina> listaPaginas = conf.getPaginasConfiguracion();
                for (int j = 0; j < listaPaginas.size(); j++) {
                    System.out.println("->LstPage1->" + conf.getPaginasConfiguracion().get(j).getArchivo_plantilla());
                    System.out.println("->LstPage2->" + conf.getPaginasConfiguracion().get(j).getNombre());
                    TipoPagina tp = listaPaginas.get(j);
                    if (tp.getNombre().equalsIgnoreCase(ConstantesTipoPaginas.portadaPlan)) {
                        System.out.println("---------------------------------CONFIG PORTADA FIN-------------------------------------");
                        return tp;
                    }
                }
            }
        }
        return null;
    }

}
