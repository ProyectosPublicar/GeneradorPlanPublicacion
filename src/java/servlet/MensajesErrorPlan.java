/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import java.io.PrintWriter;

/**
 * Clase que se encarga de imprimir en pantalla los diferentes mensajes de error
 * que pueden ser generados durante el proceso de generación del documento de
 * plan de publicación. 
 * @author Administrador
 */
public class MensajesErrorPlan {
    
    
    /**
    * Imprime en pantalla el mensaje de error cuando se presenta algún tipo de error obteniendo
    * servicios desde el Web Service WSDesignBidgeService.  Ejemplos: INVALID SESSION ID o
    * problemas de comunicación accediendo el servicio.
    */
    public void informarErrorWSDesignBridgeService (PrintWriter out, Exception ex) {
        
        //1. Escribir en el archivo de logs.
        System.err.println("[ERROR PLAN PUBLICACION] [SALESFORCE]\n");
        System.err.println("Error obteniendo servicio WSDesignBridgeService.\n");
        System.err.println("Descripción del error: "+ex.getMessage());
        
        //2. Escribir la respuesta html
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1' />");
        out.println("<title>Error obteniendo servicio WSDesignBridgeService</title>");  
        out.println("<style type='text/css'>");
        out.println(".style1 {	font-family: Arial, Helvetica, sans-serif; color: #FFFF99;}");
        out.println(".style6 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 12px; }");
        out.println(".style8 {font-family: Arial, Helvetica, sans-serif; font-size: 12px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p></p>");
        out.println("<table width='90%' border='0' align='center'>");
        out.println("<tr>");
        out.println("<td width='25%'>&nbsp;</td>");
        out.println("<td width='75%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='2' bgcolor='#006699'><div align='center'><span class='style1'>");
        out.println("Se present&oacute; un error durante la generaci&oacute;n  del Plan de Publicación");
        out.println("</span> </div></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Tipo de Error: </span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>Bloqueante</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Descripci&oacute;n </span></td>");
        out.println("<td><span class='style8'>");
        out.println("Se presentó un error obteniendo el Servicio WSDesignBridgeService");
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Detalle de la excepción</span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>");
        out.println(ex.getMessage());
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Traza de la excepción</span></td>");
        out.println("<td><span class='style8'>");
        StackTraceElement [] error = ex.getStackTrace();
        for (int excepciones=0; excepciones <ex.getStackTrace().length; excepciones++)
        {
           String errorMensaje = (error[excepciones]).toString();
           out.println(errorMensaje);
        }
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>&nbsp;</td>");
        out.println("<td>&nbsp;</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<p></p>");
        out.println("</body>");
        out.println("</html>"); 
    }
    
    
    
    /**
    * Imprime en pantalla el mensaje de error cuando el Web Service WSDesignBridge devuelve
    * una respuesta false para el método IsSuccess
    */
    public void informarErrorIsSuccess(PrintWriter out) {
        
        //1. Escribir en el archivo de logs.
        System.err.println("[ERROR PLAN PUBLICACION] [SALESFORCE]\n");
        System.err.println("Se recibió una respuesta false para el método isSuccess");
        
        //2. Escribir la respuesta html
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1' />");
        out.println("<title>Error en respuesta IsSuccess</title>");  
        out.println("<style type='text/css'>");
        out.println(".style1 {	font-family: Arial, Helvetica, sans-serif; color: #FFFF99;}");
        out.println(".style6 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 12px; }");
        out.println(".style8 {font-family: Arial, Helvetica, sans-serif; font-size: 12px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p></p>");
        out.println("<table width='60%' border='0' align='center'>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='2' bgcolor='#006699'><div align='center'><span class='style1'>");
        out.println("Se present&oacute; un error durante la generaci&oacute;n  del Plan de Publicación");
        out.println("</span> </div></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Tipo de Error: </span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>Bloqueante</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Descripci&oacute;n </span></td>");
        out.println("<td><span class='style8'>");
        out.println("Se recibió una respuesta false para el método IsSuccess.  No es posible generar el plan de publicación con los parámetros recibidos");
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>&nbsp;</td>");
        out.println("<td>&nbsp;</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<p></p>");
        out.println("</body>");
        out.println("</html>"); 
    }

    
    
    /**
    * Imprime en pantalla el mensaje de error cuando se presenta algún tipo de error 
    * durante la carga de la Configuración de las plantillas.
    */
    public void informarErrorCargandoConfiguracion (PrintWriter out, Exception ex) {
        
        //1. Escribir en el archivo de logs.
        System.err.println("[ERROR PLAN PUBLICACION] [SALESFORCE]\n");
        System.err.println("Error obteniendo configuración de Base de Datos.\n");
        System.err.println("Descripción del error: "+ex.getMessage());
        
        //2. Escribir la respuesta html
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1' />");
        out.println("<title>Error obteniendo configuración desde Base de Datos</title>");  
        out.println("<style type='text/css'>");
        out.println(".style1 {	font-family: Arial, Helvetica, sans-serif; color: #FFFF99;}");
        out.println(".style6 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 12px; }");
        out.println(".style8 {font-family: Arial, Helvetica, sans-serif; font-size: 12px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p></p>");
        out.println("<table width='90%' border='0' align='center'>");
        out.println("<tr>");
        out.println("<td width='25%'>&nbsp;</td>");
        out.println("<td width='75%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='2' bgcolor='#006699'><div align='center'><span class='style1'>");
        out.println("Se present&oacute; un error durante la generaci&oacute;n  del Plan de Publicación");
        out.println("</span> </div></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Tipo de Error: </span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>Bloqueante</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Descripci&oacute;n </span></td>");
        out.println("<td><span class='style8'>");
        out.println("Se presentó un error obteniendo la configuración desde la Base de Datos");
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Detalle de la excepción</span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>");
        out.println(ex.getMessage());
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Traza de la excepción</span></td>");
        out.println("<td><span class='style8'>");
        StackTraceElement [] error = ex.getStackTrace();
        for (int excepciones=0; excepciones <ex.getStackTrace().length; excepciones++)
        {
           String errorMensaje = (error[excepciones]).toString();
           out.println(errorMensaje);
        }
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>&nbsp;</td>");
        out.println("<td>&nbsp;</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<p></p>");
        out.println("</body>");
        out.println("</html>"); 
    }
    
    
    /**
    * Imprime en pantalla el mensaje de error cuando se presenta algún tipo de error 
    * durante la carga de la Configuración de las plantillas.
    */
    public void informarErrorPortada (PrintWriter out, Exception ex) {
        
        //1. Escribir en el archivo de logs.
        System.err.println("[ERROR PLAN PUBLICACION] [SALESFORCE]\n");
        System.err.println("Error construyendo página de la portada.\n");
        System.err.println("Descripción del error: "+ex.getMessage());
        
        //2. Escribir la respuesta html
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1' />");
        out.println("<title>Error obteniendo configuración desde Base de Datos</title>");  
        out.println("<style type='text/css'>");
        out.println(".style1 {	font-family: Arial, Helvetica, sans-serif; color: #FFFF99;}");
        out.println(".style6 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 12px; }");
        out.println(".style8 {font-family: Arial, Helvetica, sans-serif; font-size: 12px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p></p>");
        out.println("<table width='90%' border='0' align='center'>");
        out.println("<tr>");
        out.println("<td width='25%'>&nbsp;</td>");
        out.println("<td width='75%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='2' bgcolor='#006699'><div align='center'><span class='style1'>");
        out.println("Se present&oacute; un error durante la generaci&oacute;n  del Plan de Publicación");
        out.println("</span> </div></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Tipo de Error: </span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>Bloqueante</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Descripci&oacute;n </span></td>");
        out.println("<td><span class='style8'>");
        out.println("Se presentó un error generando la portada del Plan de Publicación");
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Detalle de la excepción</span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>");
        out.println(ex.getMessage());
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Traza de la excepción</span></td>");
        out.println("<td><span class='style8'>");
        StackTraceElement [] error = ex.getStackTrace();
        for (int excepciones=0; excepciones <ex.getStackTrace().length; excepciones++)
        {
           String errorMensaje = (error[excepciones]).toString();
           out.println(errorMensaje);
        }
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>&nbsp;</td>");
        out.println("<td>&nbsp;</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<p></p>");
        out.println("</body>");
        out.println("</html>"); 
    }
    
    
    /**
    * Imprime en pantalla el mensaje de error cuando se presenta algún tipo de error 
    * durante el llamado a Diagramación.
    */
    public void informarErrorDiagramacion (PrintWriter out, Exception ex) {
        
        //1. Escribir en el archivo de logs.
        System.err.println("[ERROR PLAN PUBLICACION] [DIAGRAMACION]\n");
        System.err.println("Error construyendo páginas de diagramación.\n");
        System.err.println("Descripción del error: "+ex.getMessage());
        
        //2. Escribir la respuesta html
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1' />");
        out.println("<title>Error consumiendo servicios de diagramación.</title>");  
        out.println("<style type='text/css'>");
        out.println(".style1 {	font-family: Arial, Helvetica, sans-serif; color: #FFFF99;}");
        out.println(".style6 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 12px; }");
        out.println(".style8 {font-family: Arial, Helvetica, sans-serif; font-size: 12px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p></p>");
        out.println("<table width='90%' border='0' align='center'>");
        out.println("<tr>");
        out.println("<td width='25%'>&nbsp;</td>");
        out.println("<td width='75%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='2' bgcolor='#006699'><div align='center'><span class='style1'>");
        out.println("Se present&oacute; un error durante la generaci&oacute;n  del Plan de Publicación");
        out.println("</span> </div></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Tipo de Error: </span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>Bloqueante</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Descripci&oacute;n </span></td>");
        out.println("<td><span class='style8'>");
        out.println("Se presentó un error consumiendo los servicios de diagramación.");
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Detalle de la excepción</span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>");
        out.println(ex.getMessage());
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Traza de la excepción</span></td>");
        out.println("<td><span class='style8'>");
        StackTraceElement [] error = ex.getStackTrace();
        for (int excepciones=0; excepciones <ex.getStackTrace().length; excepciones++)
        {
           String errorMensaje = (error[excepciones]).toString();
           out.println(errorMensaje);
        }
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>&nbsp;</td>");
        out.println("<td>&nbsp;</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<p></p>");
        out.println("</body>");
        out.println("</html>"); 
    }
    

   
    /**
    * Imprime en pantalla el mensaje de error cuando se presenta algún tipo de error 
    * durante el llamado a Génesis.
    */
    public void informarErrorGenesis (PrintWriter out, Exception ex) {
        
        //1. Escribir en el archivo de logs.
        System.err.println("[ERROR PLAN PUBLICACION] [GENESIS]\n");
        System.err.println("Error construyendo páginas de Génesis.\n");
        System.err.println("Descripción del error: "+ex.getMessage());
        
        //2. Escribir la respuesta html
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1' />");
        out.println("<title>Error consumiendo servicios de Génesis.</title>");  
        out.println("<style type='text/css'>");
        out.println(".style1 {	font-family: Arial, Helvetica, sans-serif; color: #FFFF99;}");
        out.println(".style6 {font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 12px; }");
        out.println(".style8 {font-family: Arial, Helvetica, sans-serif; font-size: 12px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p></p>");
        out.println("<table width='90%' border='0' align='center'>");
        out.println("<tr>");
        out.println("<td width='25%'>&nbsp;</td>");
        out.println("<td width='75%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='2' bgcolor='#006699'><div align='center'><span class='style1'>");
        out.println("Se present&oacute; un error durante la generaci&oacute;n  del Plan de Publicación");
        out.println("</span> </div></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td width='20%'>&nbsp;</td>");
        out.println("<td width='80%'>&nbsp;</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Tipo de Error: </span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>Bloqueante</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Descripci&oacute;n </span></td>");
        out.println("<td><span class='style8'>");
        out.println("Se presentó un error consumiendo los servicios de Génesis.");
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td bgcolor='#D7EAFD'><span class='style6'>Detalle de la excepción</span></td>");
        out.println("<td bgcolor='#D7EAFD'><span class='style8'>");
        out.println(ex.getMessage());
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><span class='style6'>Traza de la excepción</span></td>");
        out.println("<td><span class='style8'>");
        StackTraceElement [] error = ex.getStackTrace();
        for (int excepciones=0; excepciones <ex.getStackTrace().length; excepciones++)
        {
           String errorMensaje = (error[excepciones]).toString();
           out.println(errorMensaje);
        }
        out.println("</span></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>&nbsp;</td>");
        out.println("<td>&nbsp;</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("<p></p>");
        out.println("</body>");
        out.println("</html>"); 
    }
    

}
