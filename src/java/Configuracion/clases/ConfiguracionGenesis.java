package Configuracion.clases;

import Configuracion.modelo.Configuracion;
import Configuracion.modelo.ConstantesTipoPaginas;
import Configuracion.modelo.TipoPagina;
import java.util.List;

/**
 * 
 * @author Administrador
 */
public class ConfiguracionGenesis {
    
    public TipoPagina llevaGenesis (List<Configuracion> configuraciones, String idConfiguracion){
        
        //Recorrer la lista de configuraciones
        for (int i=0; i<configuraciones.size();i++){
            
            Configuracion conf = configuraciones.get(i);
            String tipoConfiguracion = conf.getIdentificador();
            if (tipoConfiguracion.equalsIgnoreCase(idConfiguracion)){
            
            List<TipoPagina> listaPaginas = conf.getPaginasConfiguracion();
            for (int j=0; j<listaPaginas.size();j++){
                TipoPagina tp = listaPaginas.get(j);
                if (tp.getNombre().equalsIgnoreCase(ConstantesTipoPaginas.genesisPlan))
                {
                    return tp;
                }
            }
          }  
        }
        return null;
    }
    
    

}
