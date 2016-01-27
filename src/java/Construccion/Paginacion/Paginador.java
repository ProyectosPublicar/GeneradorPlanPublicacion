/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Construccion.Paginacion;

import Configuracion.modelo.Componente;
import Configuracion.modelo.ConstantesTipoPaginas;
import Configuracion.modelo.ConstantesTipoSeccion;
import Configuracion.modelo.Seccion;
import Configuracion.modelo.TipoPagina;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author Administrador
 */
public class Paginador {
    
     /**
     * Método que se encarga de numerar las páginas y de colocar fecha y hora de generación a las mismas.
     * @author dlopez
     * @param documentoSinPaginar documento antes de ser paginado
     * @return El documento una vez paginado y con la fecha y hora de generación en la parte superior.  
     */
    public ByteArrayOutputStream paginarDocumento(ByteArrayOutputStream documentoSinPaginar, int paginaInicial, String constantePagina, String plantilla, TipoPagina confPortada, int numeroTotalPaginas, String fechaGeneracion, TipoPagina confGenesis, TipoPagina confProduccion,TipoPagina confInternet,TipoPagina confDiagramacion,TipoPagina confReglamentos) throws Exception 
    {
         
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ByteArrayOutputStream bosTemporal = new ByteArrayOutputStream();
         
         
         try{
         
                                  
            PdfReader readerDocumento = new PdfReader(documentoSinPaginar.toByteArray());
            Rectangle pageSize = new Rectangle(readerDocumento.getPageSize(1));
            Document document = new Document(pageSize);
            PdfWriter writterFinal = PdfWriter.getInstance(document,bos);
            document.open();
            
           /********************************************************/
           /*        AGREGAR LA SECCION DE PAGINACION              */
           /********************************************************/
           Seccion seccionFinal = null;
           if (constantePagina.equals(ConstantesTipoPaginas.portadaPlan))
               
           {
               try {
            
                     for (int i=0; i<confPortada.getSeccionesPagina().size(); i++){
                         Seccion sec = confPortada.getSeccionesPagina().get(i);
                         if (sec.getTipo_seccion().equals(ConstantesTipoSeccion.paginacion)){
                             seccionFinal = sec;
                             
                         }           
                     }
               }
               catch(Exception e) {
                    e.printStackTrace();
                    throw e;
               }
               
           }
           //Génesis
           if (constantePagina.equals(ConstantesTipoPaginas.genesisPlan))
           {
               try {
            
                     for (int i=0; i<confGenesis.getSeccionesPagina().size(); i++){
                         Seccion sec = confGenesis.getSeccionesPagina().get(i);
                         if (sec.getTipo_seccion().equals(ConstantesTipoSeccion.paginacion)){
                             seccionFinal = sec;
                             
                         }           
                     }
               }
               catch(Exception e) {
                    e.printStackTrace();
                    throw e;
               }
           }
           //Avisos de Producción
           if (constantePagina.equals(ConstantesTipoPaginas.produccionPlan))
           {
               try {
            
                     for (int i=0; i<confProduccion.getSeccionesPagina().size(); i++){
                         Seccion sec = confProduccion.getSeccionesPagina().get(i);
                         if (sec.getTipo_seccion().equals(ConstantesTipoSeccion.paginacion)){
                             seccionFinal = sec;
                             
                         }           
                     }
               }
               catch(Exception e) {
                    e.printStackTrace();
                    throw e;
               }
           }
           //Avisos de Internet
           if (constantePagina.equals(ConstantesTipoPaginas.internetPlan))
           {
               try {
            
                     for (int i=0; i<confInternet.getSeccionesPagina().size(); i++){
                         Seccion sec = confInternet.getSeccionesPagina().get(i);
                         if (sec.getTipo_seccion().equals(ConstantesTipoSeccion.paginacion)){
                             seccionFinal = sec;
                             
                         }           
                     }
               }
               catch(Exception e) {
                    e.printStackTrace();
                    throw e;
               }
           }
           //Diagramación
           if (constantePagina.equals(ConstantesTipoPaginas.diagramacionPlan))
           {
               try {
            
                     for (int i=0; i<confDiagramacion.getSeccionesPagina().size(); i++){
                         Seccion sec = confDiagramacion.getSeccionesPagina().get(i);
                         if (sec.getTipo_seccion().equals(ConstantesTipoSeccion.paginacion)){
                             seccionFinal = sec;
                             
                         }           
                     }
               }
               catch(Exception e) {
                    e.printStackTrace();
                    throw e;
               }
           }
           //Reglamentos
           if (constantePagina.equals(ConstantesTipoPaginas.reglamentoPlan))
           {
               try {
            
                     for (int i=0; i<confReglamentos.getSeccionesPagina().size(); i++){
                         Seccion sec = confReglamentos.getSeccionesPagina().get(i);
                         if (sec.getTipo_seccion().equals(ConstantesTipoSeccion.paginacion)){
                             seccionFinal = sec;
                             
                         }           
                     }
               }
               catch(Exception e) {
                    e.printStackTrace();
                    throw e;
               }
           }
           
           
           
           for (int i=1; i<=readerDocumento.getNumberOfPages(); i++)
           {
            
               //Agregar la paginación.
               if (seccionFinal!=null){
                   
                   Rectangle pageSize2 = new Rectangle (seccionFinal.getAncho(),seccionFinal.getAlto());
                   //Color de Fondo de la Sección:
                   if (seccionFinal.getColor_fondo_red()>-1 && seccionFinal.getColor_fondo_green()>-1 && seccionFinal.getColor_fondo_blue()>-1){
                       pageSize2.setBackgroundColor(new BaseColor(seccionFinal.getColor_fondo_red(), seccionFinal.getColor_fondo_green(), seccionFinal.getColor_fondo_blue()));
                   }
                   
                   Document documentoFinal = new Document(pageSize2);
                   PdfWriter writter = PdfWriter.getInstance(documentoFinal, bosTemporal);
                   documentoFinal.open();
                   PdfContentByte cb = writter.getDirectContent();
                   BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
                   cb.setFontAndSize(bf1,20);
                   cb.showText(" ");
                   documentoFinal.close();
                   byte[] tempPDF2 = bosTemporal.toByteArray();
                   PdfReader reader2 = new PdfReader(tempPDF2);
                   //Agregar la Sección al Documento.
                   
                   //Agregar el contenido a la plantilla de la página
                   PdfTemplate plantillaFinal =  writterFinal.getImportedPage(readerDocumento, i);
                   PdfContentByte content = writterFinal.getDirectContent();
                   PdfTemplate membrete1 = writterFinal.getImportedPage(reader2, 1);
                   content.addTemplate(membrete1, seccionFinal.getCoordenada_inicio_x(),seccionFinal.getCoordenada_inicio_y());
                   content.addTemplate(plantillaFinal, 0,0);
                   
                   
                   //Obtener los componentes de la paginación:
                   for (int j=0; j<seccionFinal.getComponentesSeccion().size();j++){
                       
                       Componente comp = seccionFinal.getComponentesSeccion().get(j);
                       ByteArrayOutputStream bosComponente = this.agregarComponente(comp,(paginaInicial+i-1),numeroTotalPaginas,fechaGeneracion,seccionFinal.getCoordenada_inicio_x(), seccionFinal.getCoordenada_inicio_y());
                       byte[] tempPDF3 = bosComponente.toByteArray();
                       PdfReader reader3 = new PdfReader(tempPDF3); 
                       PdfTemplate membrete2 = writterFinal.getImportedPage(reader3, 1);
                       int valorCoordenadaX = seccionFinal.getCoordenada_inicio_x()+comp.getCoordenada_inicio_x();
                       int valorCoordenadaY = seccionFinal.getCoordenada_inicio_y()+comp.getCoordenada_inicio_y();
                       content.addTemplate(membrete2, valorCoordenadaX, valorCoordenadaY);
                       
                       
                   }
                   document.newPage();
                   
               }
               else{
                   return documentoSinPaginar;
                   
               }
           }     
           document.close();     
           
        
                
        
        }
        catch(Exception e){
             
             e.printStackTrace();
             throw e;
             
        }
                
         return bos;
    }


    
    public ByteArrayOutputStream agregarComponente(Componente comp,int numeroPagina, int numeroTotalPaginas, String fechaGeneracion, int x_inicial, int y_inicial) throws Exception{
        
          //Obtener los atributos del Componente
          int coordenada_inicial_x = comp.getCoordenada_inicio_x()+x_inicial;
          int coordenada_inicial_y = comp.getCoordenada_inicio_y()+y_inicial;
          int alto = comp.getAlto();
          int ancho = comp.getAncho();
          String atributo = comp.getAtributo();
          String label = comp.getLabel();
          int colorFondoRed = comp.getColor_fondo_red();
          int colorFondoGreen = comp.getColor_fondo_green();
          int colorFondoBlue = comp.getColor_fondo_blue();
          int colorFuenteRed = comp.getColor_fuente_red();
          int colorFuenteGreen = comp.getColor_fuente_green();
          int colorFuenteBlue = comp.getColor_fuente_blue();   
          Boolean esDinamico = comp.getEsDinamico();
          int coordenada_escribir_x = comp.getCoordenada_escribir_x();
          int coordenada_escribir_y = comp.getCoordenada_escribir_y();
          String tipoFuente = comp.getTipoFuente();
          int tamanoFuente = comp.getTamano_fuente();
          String textoEscribir = "";
          ByteArrayOutputStream bosTemporal = new ByteArrayOutputStream();
          
          if (esDinamico.booleanValue() ==false){
              textoEscribir = label;
          }
          
          else{
              
              if (atributo.equalsIgnoreCase(ConstantesPaginacion.NUMERO_PAGINA)){
                  textoEscribir = numeroPagina+"";
              }
              if (atributo.equalsIgnoreCase(ConstantesPaginacion.TOTAL_PAGINAS)){
                  textoEscribir = numeroTotalPaginas+"";
              }
              if (atributo.equalsIgnoreCase(ConstantesPaginacion.FECHA_GENERACION)){
                  textoEscribir = fechaGeneracion;
              }
          }
          
          
          try{
          
              //Crear el Documento.
              Rectangle pageSize = new Rectangle (ancho,alto);

              //Color de Fondo del Componente
              if (colorFondoRed>-1 && colorFondoGreen>-1 && colorFondoBlue>-1){
                    pageSize.setBackgroundColor(new BaseColor(colorFondoRed,colorFondoGreen,colorFondoBlue));
              }

              Document documentoFinal = new Document(pageSize);
              PdfWriter writter = PdfWriter.getInstance(documentoFinal, bosTemporal);
              documentoFinal.open();
              PdfContentByte cb = writter.getDirectContent();
              cb.beginText();
              BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
              if (tipoFuente!=null){
                   bf1 = BaseFont.createFont("fuentes_archivos/"+tipoFuente, BaseFont.CP1252, BaseFont.EMBEDDED);
              }
              cb.setRGBColorStroke(colorFuenteRed, colorFuenteGreen, colorFuenteBlue);
              cb.setColorFill(new BaseColor(colorFuenteRed, colorFuenteGreen, colorFuenteBlue));
              cb.setFontAndSize(bf1,tamanoFuente);
              cb.moveText(coordenada_escribir_x, coordenada_escribir_y);
              cb.showText(textoEscribir);
              cb.endText();
              documentoFinal.close();
          }
          catch(Exception e){
              e.printStackTrace();
              throw e;
          }
        
        return bosTemporal;
    }

}
  
    
