package Configuracion.clases;

import Configuracion.modelo.Configuracion;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class ConfiguracionBasica {
    
    public Configuracion obtenerConfiguracionBasica (List<Configuracion> configuraciones, String idConfiguracion){
        
        //Recorrer la lista de configuraciones
        for (int i=0; i<configuraciones.size();i++){
            
            Configuracion conf = configuraciones.get(i);
            String tipoConfiguracion = conf.getIdentificador();
            
            if (tipoConfiguracion.equalsIgnoreCase(idConfiguracion)){
            Configuracion miConfiguracionBasica = new Configuracion();
            miConfiguracionBasica.setDescripcion(conf.getDescripcion());
            miConfiguracionBasica.setIdentificador(conf.getIdentificador());
            miConfiguracionBasica.setLlevaPaginacion(conf.getLlevaPaginacion());
            miConfiguracionBasica.setNombre(conf.getNombre());
            miConfiguracionBasica.setPlantillaAvisoGenesisEnProceso(conf.getPlantillaAvisoGenesisEnProceso());
            miConfiguracionBasica.setPlantillaAvisoGenesisNoDisponible(conf.getPlantillaAvisoGenesisNoDisponible());
            miConfiguracionBasica.setPlantillaErrorGeneracionAviso(conf.getPlantillaErrorGeneracionAviso());
            return miConfiguracionBasica;
          }  
        }
        
        return null;
    }

}
