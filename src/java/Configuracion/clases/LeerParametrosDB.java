package Configuracion.clases;

import java.util.ResourceBundle;

/**
 * Clase que se encarga de leer los parámetros de configuración de 
 * la Base de Datos desde un archivo de propiedades (.properties)
 * @author Administrador
 */
public class LeerParametrosDB {
    
    public ParametrosDB cargarParametrosDB() throws Exception {
        
        ParametrosDB parametros = new ParametrosDB();
        String archivo_configuracion ="Configuracion.propiedades.properties";
        ResourceBundle rb = ResourceBundle.getBundle(archivo_configuracion);
        try{
            String url = rb.getString("url_base_de_datos");
            String database = rb.getString("databaseName");
            String user = rb.getString("user");
            String password = rb.getString("password");
            parametros.setUrl(url);
            parametros.setDatabase(database);
            parametros.setUser(user);
            parametros.setPassword(password);
            
        }
        catch(Exception e){
            throw e;
        }
        
        return parametros;
        
        
    }

}
