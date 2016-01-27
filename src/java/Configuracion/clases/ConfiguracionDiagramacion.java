package Configuracion.clases;

import Configuracion.modelo.Configuracion;
import Configuracion.modelo.ConstantesTipoPaginas;
import Configuracion.modelo.TipoPagina;
import java.util.List;

/** 
 * 
 * @author Administrador
 */
public class ConfiguracionDiagramacion {
    
    public TipoPagina llevaDiagramacion (List<Configuracion> configuraciones, String idConfiguracion){
        System.out.println("configuraciones-> " + configuraciones.size());
        System.out.println("idConfiguracion-> " + idConfiguracion);
        //Recorrer la lista de configuraciones
        for (int i=0; i<configuraciones.size();i++){
            
            Configuracion conf = configuraciones.get(i);
            String tipoConfiguracion = conf.getIdentificador();
            if (tipoConfiguracion.equalsIgnoreCase(idConfiguracion)){
            
            List<TipoPagina> listaPaginas = conf.getPaginasConfiguracion();
            for (int j=0; j<listaPaginas.size();j++){
                TipoPagina tp = listaPaginas.get(j);
                if (tp.getNombre().equalsIgnoreCase(ConstantesTipoPaginas.diagramacionPlan))
                {
                    return tp;
                }
            }
          }  
        }
        return null;
    }
    
    public TipoPagina llevaProduccion (List<Configuracion> configuraciones, String idConfiguracion){
        
        //Recorrer la lista de configuraciones
        for (int i=0; i<configuraciones.size();i++){
            
            Configuracion conf = configuraciones.get(i);
            String tipoConfiguracion = conf.getIdentificador();
            if (tipoConfiguracion.equalsIgnoreCase(idConfiguracion)){
            
            List<TipoPagina> listaPaginas = conf.getPaginasConfiguracion();
            for (int j=0; j<listaPaginas.size();j++){
                TipoPagina tp = listaPaginas.get(j);
                if (tp.getNombre().equalsIgnoreCase(ConstantesTipoPaginas.produccionPlan))
                {
                    return tp;
                }
            }
          }  
        }
        return null;
    }

}
