/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Construccion.Reglamentos;

import Configuracion.modelo.Componente;
import Configuracion.modelo.ConstantesTipoSeccion;
import Configuracion.modelo.Seccion;
import Configuracion.modelo.TipoPagina;
import codigo_barras.BarCode;
import codigo_barras.ProductionDigitizingService;
import codigo_barras.ProductionDigitizingService_Service;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import javax.xml.bind.JAXBElement;
import org.apache.commons.codec.binary.Base64;
import parametros_plan_salesforce.AgentInfo;
import parametros_plan_salesforce.ClientInfo;
import parametros_plan_salesforce.ContractInfo;

/**
 *
 * @author Administrador
 */
public class ConstructorReglamentos {

    public ByteArrayOutputStream construirReglamento(ClientInfo clientInfo, ContractInfo contract, AgentInfo agent, String templateCode, TipoPagina reglamento)
    throws Exception{
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader readerPlantilla;
        String rutaPlantilla="plantillas_archivos/"+reglamento.getArchivo_plantilla();
        //Cargar la Plantilla
         try {
                readerPlantilla = new PdfReader(rutaPlantilla);
         } 
         catch (Exception ex) {
                throw ex;
        }
        
        //En esta variable se guarda el tamaño de la Plantilla.
        Rectangle pageSize = new Rectangle(readerPlantilla.getPageSize(1));
        Document documentoFinal = new Document(pageSize);
        PdfWriter writterGeneral = PdfWriter.getInstance(documentoFinal,bos);
        documentoFinal.open();
        PdfTemplate plantilla = writterGeneral.getImportedPage(readerPlantilla, 1);
        PdfContentByte content = writterGeneral.getDirectContent();
        content.addTemplate(plantilla, 0,0);
        
        
        /********************************************************/
        /*       AGREGAR CADA UNA DE LAS SECCIONES              */
        /********************************************************/
        try{
            for (int i=0; i<reglamento.getSeccionesPagina().size(); i++){
            
                Seccion sec = reglamento.getSeccionesPagina().get(i);
                
                this.agregarSeccion(content, sec, writterGeneral, clientInfo, contract, agent);
               
            
            }
        
        }
                
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        documentoFinal.close();
        
        return bos;
    }
    
    public void agregarSeccion (PdfContentByte content, Seccion sec, PdfWriter writterGeneral, ClientInfo clientInfo, ContractInfo contract, AgentInfo agent) throws Exception{
        
        try{
            int coordenada_inicio_x = sec.getCoordenada_inicio_x();
            int coordenada_inicio_y = sec.getCoordenada_inicio_y();
            int alto = sec.getAlto();
            int ancho = sec.getAncho();
            int blue = sec.getColor_fondo_blue();
            int green = sec.getColor_fondo_green();
            int red = sec.getColor_fondo_red();
            String tipoSeccion = sec.getTipo_seccion();

            //Crear un objeto para la sección
            Rectangle recSeccion = new Rectangle(ancho, alto);
            Document documento = new Document(recSeccion);
            //Si el color es mayor que cero se asigna el fondo.  Si se quiere transparente, configurarlo con -1 en la base de datos.
            if (blue>=0 && red >=0 && green>=0){
                recSeccion.setBackgroundColor(new BaseColor(red,green,blue));
            }
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
            documento.open();
            PdfContentByte cb = writter.getDirectContent();
            BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
            cb.setFontAndSize(bf1,20);
            cb.showText(" ");
            documento.close();
            byte[] tempPDF = pdfOutputStream.toByteArray();
            PdfReader readerComponente = new PdfReader(tempPDF);
            PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
            content.addTemplate(plantilla, coordenada_inicio_x,coordenada_inicio_y);
        
            for (int j=0; j< sec.getComponentesSeccion().size();j++){
                
                Componente componente = sec.getComponentesSeccion().get(j);
                
                agregarComponente(content, componente, writterGeneral, clientInfo, contract, agent, coordenada_inicio_x, coordenada_inicio_y, tipoSeccion);
                            
            }
        
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
       
    }
    
    public void agregarComponente(PdfContentByte content, Componente comp, PdfWriter writterGeneral, ClientInfo clientInfo, ContractInfo contract, AgentInfo agent, int x_inicial, int y_inicial, String tipoSeccion)throws Exception{
        
        try{
            
          //Obtener los atributos del Componente
          String datosBarras="";
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
          
          
          //Caso Texto Dinámico
          if (esDinamico.booleanValue() ==true){
              
              String nombreClase = "";
              
              //Sección de Clientes
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosCliente)){
                  Class miClase = Class.forName("parametros_plan_salesforce.ClientInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(clientInfo);
                      textoEscribir = textoI.getValue();
                  }
                  catch (Exception e){
                      throw e;
                  }
              }
              
              //Sección datos del Contrato
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosContrato)){
                  Class miClase = Class.forName("parametros_plan_salesforce.ContractInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(contract);
                      textoEscribir = textoI.getValue();
                  }
                  catch (Exception e){
                      throw e;
                      
                  }
              }
              
              //Sección datos del Agente
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosAgente)){
                  Class miClase = Class.forName("parametros_plan_salesforce.AgentInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(agent);
                      textoEscribir = textoI.getValue();
                  }
                  catch (Exception e){
                      throw e;
                      
                  }
              }
              
              //Sección Facturación
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.facturacion)){
                  Class miClase = Class.forName("parametros_plan_salesforce.ContractInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(contract);
                      textoEscribir = textoI.getValue();
                      
                  }
                  catch (Exception e){
                      throw e;
                     
                  }
              }
              
              //Sección Código de BarrasCODIGO_BARRAS
                            
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.codigoBarras)){
                  
                  try{
                        
                        JAXBElement<String> cadenaCodigoI = contract.getContractBarCode();
                        String cadenaCodigo = cadenaCodigoI.getValue();
                        
                        ProductionDigitizingService_Service barras_service = new ProductionDigitizingService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/DigitizingService.wsdl"));
                        ProductionDigitizingService product = barras_service.getBasicHttpBindingProductionDigitizingService();
                        BarCode dataPdfBarCode = product.getDataPdfBarCode(cadenaCodigo);
                        
                        JAXBElement<String> dataBarras = dataPdfBarCode.getBarCodeString();
                        datosBarras = dataBarras.getValue();
                       
                      
                  }
                  catch (Exception e){
                      System.err.println("[ERROR PLAN PUBLICACION] [CODIGO DE BARRAS]\n");
                      System.err.println("Error obteniendo servicios web para la generación del código de barras.\n");
                      System.err.println("Traza de la Excepción: "+e);
                     
                  }
              }
                  
           }
           
           //Caso Label
           else {
               textoEscribir=label;
           }
          
          
          /**********************************************************/
          /*          CASO CODIGO DE BARRAS                         */
          /**********************************************************/
          if(tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.codigoBarras)){
              Rectangle recComponente = new Rectangle(ancho, alto);
              Document documento = new Document(recComponente);
              
              
              if(datosBarras!=null && !(datosBarras.equals(""))){
                  
                  byte[] tempPDF = Base64.decodeBase64(datosBarras);
                  PdfReader readerComponente = new PdfReader(tempPDF);
                  PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
                  content.addTemplate(plantilla, coordenada_inicial_x,coordenada_inicial_y);
              }
             
          }
          
          else {
            //Crear un objeto para el componente
            Rectangle recComponente = new Rectangle(ancho, alto);
            Document documento = new Document(recComponente);
            //Si el color es mayor que cero se usa como background. Si se requiere transparente colocar -1 en la base de datos.
            if (colorFondoRed >=0 && colorFondoGreen >=0 && colorFondoBlue >=0){
                recComponente.setBackgroundColor(new BaseColor(colorFondoRed,colorFondoGreen,colorFondoBlue));
            }           
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
            documento.open();
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
            documento.close();
            byte[] tempPDF = pdfOutputStream.toByteArray();
            PdfReader readerComponente = new PdfReader(tempPDF);
            PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
            content.addTemplate(plantilla, coordenada_inicial_x,coordenada_inicial_y);
          }
            
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    
    
    
}
