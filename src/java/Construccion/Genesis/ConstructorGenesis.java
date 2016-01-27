package Construccion.Genesis;

import Configuracion.modelo.Componente;
import Configuracion.modelo.Configuracion;
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
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import nuevo_codigo_barras.AxentriaDigitizingService;
import nuevo_codigo_barras.AxentriaDigitizingService_Service;
import org.apache.commons.codec.binary.Base64;
import parametros_plan_salesforce.AdvertiseInfo;
import parametros_plan_salesforce.ContractInfo;
import web_service_genesis.ArrayOfPrintedBuiltReference;
import web_service_genesis.PrintedBuiltReference;
import web_service_genesis.TemporaryDesignRequestService;
import web_service_genesis.TemporaryDesignRequestService_Service;
import web_service_genesis2.CommercialReferenceRequest;
import web_service_genesis2.GenesisService;
import web_service_genesis2.GenesisService_Service;
import Configuracion.modelo.Background_Structure;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 *
 * @author Administrador
 */
public class ConstructorGenesis {
    private List<PrintedBuiltReference> listaReferencias;
    private List<web_service_genesis2.PrintedBuiltReference> listaReferencias2;
    byte[] archivoFinalArte;
    boolean aviso_no_disponible=true;
    List<Background_Structure> Background;
    private List<Float> background_Color = new ArrayList();
    
    public List<ByteArrayOutputStream> construirGenesis(AdvertiseInfo advertiseElement, TipoPagina genesisPage, Configuracion confBasica, ContractInfo contract,List<Background_Structure> BackgroundColor)
    throws Exception{
        
        List<ByteArrayOutputStream> listaRetorno = new ArrayList<ByteArrayOutputStream>();
        ByteArrayOutputStream paginaGenerada=null;
        Background=new ArrayList<Background_Structure>();
        Background=BackgroundColor;
        
        //Obtener los parámetros para el llamado al Servicio:
        JAXBElement<String> sketchNumberJ = advertiseElement.getSketchNumber();
        String sketchNumber = sketchNumberJ.getValue(); 
        JAXBElement<String> productCodeJ = advertiseElement.getProductCode();
        String productCode = productCodeJ.getValue();
        JAXBElement<String> tipoSketchJ = advertiseElement.getSketchType();
        String tipoSketch = tipoSketchJ.getValue();
        JAXBElement<Boolean> highQualityJ = advertiseElement.getHighQuality();
        Boolean highQuality = highQualityJ.getValue();
                
        /***************************************************************/
        /*                   CASO TIPO SKETCH B                        */
        /*          Llama al Servicio getBuiltReference                */
        /***************************************************************/ 
        if (tipoSketch.compareTo(ConstantesGenesis.GENESIS_BUILT_REFERENCE)==0){
            Long longSketch=null;
            int generacionCorrecta=1;
          
               
               try{
                  longSketch = new Long(sketchNumber);
                  System.out.println("longSketch "+longSketch); 
               }
               catch(NumberFormatException nfe){
                   System.err.println("[ERROR PLAN PUBLICACION] [SALESFORCE]\n");
                   System.err.println("Error transformando en número el SketchNumber.\n");
                   System.err.println("Valor Recibido: "+sketchNumber);
                   archivoFinalArte = generarAvisoErrorGeneracion(confBasica);
                   paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                   listaRetorno.add(paginaGenerada);
                   return listaRetorno;
               }
               
               
               try{
                       TemporaryDesignRequestService_Service genesis_service = new TemporaryDesignRequestService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/Genesis.wsdl"));
                       TemporaryDesignRequestService diagrama = genesis_service.getBasicHttpBindingTemporaryDesignRequestService();
                       ArrayOfPrintedBuiltReference processAp = diagrama.getBuiltReference(longSketch);
                       listaReferencias = processAp.getPrintedBuiltReference();
               }
               catch(Exception e){
                       System.err.println("[ERROR PLAN PUBLICACION] [GENESIS]\n");
                       System.err.println("Error Obteniendo servicio getPrintedBuiltReference.\n");
                       System.err.println("Excepción: "+e);
                       archivoFinalArte = generarAvisoErrorGeneracion(confBasica);
                       paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                       listaRetorno.add(paginaGenerada);
                       return listaRetorno;
               }
              
           
               //Caso en el que se recibe una Lista de Referencias no vacía
               if (listaReferencias != null && listaReferencias.size()>0){
               
                  for (int iteradorReferencias=0; iteradorReferencias<listaReferencias.size(); iteradorReferencias++){
                      PrintedBuiltReference myReferencia = listaReferencias.get(iteradorReferencias);
                      JAXBElement<byte[]> pdfRecibido = myReferencia.getFile();
                      archivoFinalArte = pdfRecibido.getValue();
                      paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                      listaRetorno.add(paginaGenerada);
                      
                      return listaRetorno;
                  }
               }
               else{
               
                  
                  archivoFinalArte = this.generarAvisoNoDisponible(confBasica);
                  paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                  listaRetorno.add(paginaGenerada);
                  return listaRetorno;
            }
                            
                      
        }
        
        /***************************************************************/
        /*                   CASO TIPO SKETCH D                        */
        /*          Llama al Servicio getBuiltReference                */
        /***************************************************************/ 
        else if (tipoSketch.compareTo(ConstantesGenesis.GENESIS_BY_LEGACY_CODE)==0){
            
              Integer int_code=null;
              CommercialReferenceRequest processAp2 = null;
        
               try{
                  int_code = new Integer(productCode);
                  
               }
               catch(NumberFormatException nfe){
                   System.err.println("[ERROR PLAN PUBLICACION] [SALESFORCE]\n");
                   System.err.println("Error transformando en número el Código de Producto.\n");
                   System.err.println("Valor Recibido: "+sketchNumber);
                   archivoFinalArte = generarAvisoErrorGeneracion(confBasica);
                   paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                   listaRetorno.add(paginaGenerada);
                   return listaRetorno;
               }
                
               try{ 
                GenesisService_Service genesis_service2 = new GenesisService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/Genesis2.wsdl"));
                GenesisService diagrama2 = genesis_service2.getBasicHttpBindingGenesisService();
                processAp2 = diagrama2.getCommercialReferenceRequestBylegacyId(sketchNumber, int_code, highQuality);
                
               }
               catch(Exception e){
                   System.err.println("[ERROR PLAN PUBLICACION] [GENESIS]\n");
                   System.err.println("Error Obteniendo servicio getCommercialReferenceRequestBylegacyId.\n");
                   System.err.println("Excepción: "+e);
                   archivoFinalArte = generarAvisoErrorGeneracion(confBasica);
                   
                   paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                   listaRetorno.add(paginaGenerada);
                   return listaRetorno;               
               }
            
               if (processAp2!=null){
                      
                  System.out.println("Ingresa a recuperar la respuesta de gènesis");
                  //1. Obtener el Status de la respuesta:
                  JAXBElement<String> statusJ = processAp2.getStatus();
                  String status = statusJ.getValue();
                                        
                  //CASO EN EL QUE GENESIS DEVUELVE UN AVISO
                  JAXBElement<web_service_genesis2.ArrayOfPrintedBuiltReference> files = processAp2.getFiles();
                  if (files != null){
                     web_service_genesis2.ArrayOfPrintedBuiltReference processAp = files.getValue();
                     if (processAp !=null){
                         System.out.println("Gènesis generò aviso");                         
                           listaReferencias2 = processAp.getPrintedBuiltReference();
                           System.out.println("El tamaño de la lista generada es:"+listaReferencias2.size());
                           for (int iteradorG =0; iteradorG<listaReferencias2.size(); iteradorG++){
                               System.out.println("Inserta el elemento:"+iteradorG);
                               web_service_genesis2.PrintedBuiltReference archivoMostrar = listaReferencias2.get(iteradorG);
                               JAXBElement<byte[]> fileJ = archivoMostrar.getFile();
                               archivoFinalArte = fileJ.getValue();
                               System.out.println("@@@@@@La pagina generada es: "+archivoFinalArte.toString());
                               paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                               listaRetorno.add(paginaGenerada);
                           }                   
                     }
                     else {
                      
                         System.out.println("Gènesis NO generò aviso");
                         //CASO CANCELADO
                         if (status.compareTo(ConstantesGenesis.GENESIS_CANCELADO)==0){      
                             
                             archivoFinalArte = this.generarAvisoNoDisponible(confBasica);
                             paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                             listaRetorno.add(paginaGenerada);
                             
                         }
                                    
                         //CASO ESTADOS DIFERENTES A CANCELADO
                         else {                           
                             
                            
                             archivoFinalArte = this.generarAvisoNoProducido(confBasica);
                             paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                             
                             listaRetorno.add(paginaGenerada);
                            
                         }
                     }
                  } 
                  //Génesis no devolvió ningún aviso
                  else
                  {
                      
                      archivoFinalArte = this.generarAvisoNoDisponible(confBasica);
                      paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                      listaRetorno.add(paginaGenerada);
                  }
              }
               else{
                   
                   archivoFinalArte = this.generarAvisoNoDisponible(confBasica);
                   paginaGenerada = construirPaginaGenesis(advertiseElement,genesisPage,archivoFinalArte,contract);
                   listaRetorno.add(paginaGenerada);
               }
            
            
        
        }
        return listaRetorno;
    }
    
    
    
    public ByteArrayOutputStream construirPaginaGenesis(AdvertiseInfo advertiseElement, TipoPagina genesisPage, byte[] archivoFinalArte, ContractInfo contract)
    throws Exception{
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        try{
            
            
            PdfReader readerPlantilla;
            //System.out.println("genesisPage.getArchivo_plantilla()" + genesisPage.getArchivo_plantilla());
            String rutaPlantilla="plantillas_archivos/"+genesisPage.getArchivo_plantilla();
            
            readerPlantilla = new PdfReader(rutaPlantilla);
                     
            //En esta variable se guarda el tamaño de la Plantilla.
            Rectangle pageSize = new Rectangle(readerPlantilla.getPageSize(1));
            Document documentoFinal = new Document(pageSize);
            PdfWriter writterGeneral = PdfWriter.getInstance(documentoFinal,bos);
            documentoFinal.open();
            PdfTemplate plantilla = writterGeneral.getImportedPage(readerPlantilla, 1);
            PdfContentByte content = writterGeneral.getDirectContent();
            content.addTemplate(plantilla, 0,0);
            
             JAXBElement<String> sketchNumberJ = advertiseElement.getProductPartName();
              String ProductPartName = sketchNumberJ.getValue(); 
              System.out.println("ProductPartName ************************ ="+ProductPartName);
              
               JAXBElement<String> sketchNumberp = advertiseElement.getProductPartCode();
              String ProductPartCode = sketchNumberp.getValue(); 
              System.out.println("ProductPartCode ************************ ="+ProductPartCode);
              
              for(int j=0;j<Background.size();j++){
                  int ProductPartCodeInt;
                  ProductPartCodeInt = Integer.parseInt(ProductPartCode);
                  if(Background.get(j).getCode_Part_Product()== ProductPartCodeInt){
                      background_Color = new ArrayList();
                      for(int k=0;k<Background.get(j).getBackground_Color().size();k++){
                          background_Color.add(Background.get(j).getBackground_Color().get(k));                      
                      }                  
                  }
              }
              
              
            
              
            
            
            /********************************************************/
            /*       AGREGAR CADA UNA DE LAS SECCIONES              */
            /********************************************************/
            try{
                 for (int i=0; i<genesisPage.getSeccionesPagina().size(); i++){
            
                     Seccion sec = genesisPage.getSeccionesPagina().get(i);
                     
                     this.agregarSeccion(content, sec, writterGeneral, advertiseElement,contract);
                                   
                 }
            }
            catch(Exception e) {
                e.printStackTrace();
                throw e;
            }
            documentoFinal.close();
         }
        catch(Exception e){
            
            e.printStackTrace();
            throw e;
        }
        
        
        return bos;
        
    }
    
    
    
    /**
    * Imprime en pantalla los datos básicos para llamar los web services
    * de Génesis
    * @param advertiseElement 
    */
    public void imprimirOportunidad (AdvertiseInfo advertiseElement){
        
        JAXBElement<String> sketchNumberJ = advertiseElement.getSketchNumber();
        String sketchNumber = sketchNumberJ.getValue(); 
        
        JAXBElement<String> productCodeJ = advertiseElement.getProductCode();
        String productCode = productCodeJ.getValue();
        
        JAXBElement<String> tipoSketchJ = advertiseElement.getSketchType();
        String tipoSketch = tipoSketchJ.getValue();
        
        JAXBElement<Boolean> highQualityJ = advertiseElement.getHighQuality();
        Boolean highQuality = highQualityJ.getValue();
        
    }
    
    /**
    * Genera un pdf en el que se informa que el aviso No está disponible
    */
    public byte[] generarAvisoNoDisponible(Configuracion configBasica){
        //Crear un Pdf que diga Aviso No Disponible
        aviso_no_disponible = false;
        byte[] archivoNoDisponible=null;
        try{
                        
            String rutaPlantilla=configBasica.getPlantillaAvisoGenesisNoDisponible();
            
            PdfReader readerImagen = new PdfReader(rutaPlantilla);
            Rectangle pageSize = new Rectangle(readerImagen.getPageSize(1));
            Document documento = new Document(pageSize);
            
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
            documento.open();
            PdfTemplate plantillaImagen = writter.getImportedPage(readerImagen, 1);
            PdfContentByte contentImagen = writter.getDirectContent();
            contentImagen.addTemplate(plantillaImagen, 0,0);
            documento.close();
            byte[] tempPDF = pdfOutputStream.toByteArray();
            archivoNoDisponible = tempPDF;
        }
        catch(Exception e){
            e.printStackTrace();
        }        
        return archivoNoDisponible;
    }
    
    
    /**
    * Genera un pdf en el que se informa que el aviso No ha sido producido
    */
    public byte[] generarAvisoNoProducido(Configuracion configBasica){
        //Crear un Pdf que diga Aviso No Disponible
        aviso_no_disponible = false;
        byte[] archivoEnProceso=null;
        try{
            String rutaPlantilla = configBasica.getPlantillaAvisoGenesisEnProceso();
            PdfReader readerImagen = new PdfReader(rutaPlantilla);
            Rectangle pageSize = new Rectangle(readerImagen.getPageSize(1));
            Document documento = new Document(pageSize);
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
            documento.open();
            PdfTemplate plantillaImagen = writter.getImportedPage(readerImagen, 1);
            PdfContentByte contentImagen = writter.getDirectContent();
            contentImagen.addTemplate(plantillaImagen, 0,0);
            documento.close();
            byte[] tempPDF = pdfOutputStream.toByteArray();
            archivoEnProceso = tempPDF;
        }
        catch(Exception e){
           e.printStackTrace(); 
        }                    
        return archivoEnProceso;
    }
    
    
     /**
    * Genera un pdf en el que se informa que hubo algún error en la generación del aviso.
    */
    public byte[] generarAvisoErrorGeneracion(Configuracion configBasica){
        //Crear un Pdf que diga Aviso No Disponible
        aviso_no_disponible = false;
        byte[] archivoErrorGeneracion=null;
        try{
            String rutaPlantilla = configBasica.getPlantillaErrorGeneracionAviso();
            
            PdfReader readerImagen = new PdfReader(rutaPlantilla);
            Rectangle pageSize = new Rectangle(readerImagen.getPageSize(1));
            Document documento = new Document(pageSize);
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
            documento.open();
            PdfTemplate plantillaImagen = writter.getImportedPage(readerImagen, 1);
            PdfContentByte contentImagen = writter.getDirectContent();
            contentImagen.addTemplate(plantillaImagen, 0,0);
            documento.close();
            byte[] tempPDF = pdfOutputStream.toByteArray();
            archivoErrorGeneracion = tempPDF;
            
        }
        catch(Exception e){
            e.printStackTrace();
        }                    
        return archivoErrorGeneracion;
    }
    
    
    public void agregarSeccion (PdfContentByte content, Seccion sec, PdfWriter writterGeneral, AdvertiseInfo advertise, ContractInfo contract) throws Exception{
        
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
                //System.out.println("componente seccion = "+ componente.getAtributo() );
                //System.out.println("tipo seccion = "+tipoSeccion );
                agregarComponente(content, componente, writterGeneral, coordenada_inicio_x, coordenada_inicio_y, tipoSeccion, advertise,contract);
                
            }
        
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
       
    }
    
    public void agregarComponente(PdfContentByte content, Componente comp, PdfWriter writterGeneral, int x_inicial, int y_inicial, String tipoSeccion, AdvertiseInfo advertise, ContractInfo contract)throws Exception{
        
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
              
              //Sección de Datos de Gestión de Avisos
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.gestionAvisos)){
                  Class miClase = Class.forName("parametros_plan_salesforce.AdvertiseInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      //System.out.println("atributo = "+ f.get(advertise) );
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(advertise);
                      JAXBElement<String> tipoSketchJ = advertise.getSketchType();
                      String tipoSketch = tipoSketchJ.getValue();
                      System.out.println("componente = "+ atributo );
                      System.out.println("tipoSketch = "+ tipoSketch );
                      // si el sketchNumber es decir el IDSOL PROPUESTA es igual al ID COMPRA el tipoSketch es D no se escribe IDSOL PROPUESTA
                      if("sketchNumber".equals(atributo) && tipoSketch.compareTo(ConstantesGenesis.GENESIS_BY_LEGACY_CODE)==0 ){
                       textoEscribir="";  
                      
                      }
                      else{                          
                      textoEscribir = textoI.getValue();
                      }
                      System.out.println("textoEscribir = "+ textoEscribir );
                     
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
              
                           
              //Sección Código de BarrasCODIGO_BARRAS
             if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.codigoBarras)){
                  
                  try{
                        
                        JAXBElement<String> cadenaCodigoI = advertise.getProductBarCode();
                        String cadenaCodigo = cadenaCodigoI.getValue();
                        
                        AxentriaDigitizingService_Service barras_service = new AxentriaDigitizingService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/DigitizingServiceNuevo.wsdl"));
                        AxentriaDigitizingService digitazing_service = barras_service.getBasicHttpBindingAxentriaDigitizingService();
                        nuevo_codigo_barras.BarCode dataPdfBarCode = digitazing_service.getDataPngCode128(cadenaCodigo);
                        
                        
                        JAXBElement<String> dataBarras = dataPdfBarCode.getBarCodeString();
                        datosBarras = dataBarras.getValue();
                        
                  }
                  catch (Exception e){
                      System.err.println("[ERROR PLAN PUBLICACION] [CODIGO DE BARRAS]\n");
                      System.err.println("Error obteniendo servicios web para la generación del código de barras.\n");
                      System.err.println("Traza de la Excepción: "+e);
                     
                  }
              }
             if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.Fecha_de_impresion)){
                  Date hoy =new Date();
                  
                  SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                  Date resultdate = new Date();
                  System.out.println(date_format.format(resultdate));
                  
                  System.out.println("FECHA GENESIS ES IGUAL A = "+date_format.format(resultdate).toString());
                  textoEscribir=date_format.format(resultdate).toString();
                  
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
                  float escalarY = (float) 0.8;
                  float escalarX = (float) 1.0;
                  content.addTemplate(plantilla, escalarX, 0,0, escalarY, coordenada_inicial_x,coordenada_inicial_y);
                  //content.addTemplate(plantilla, coordenada_inicial_x,coordenada_inicial_y);
              }
             
          }
                  
           /**********************************************************/
           /*          CASO MOSTRAR EL AVISO                         */
           /**********************************************************/
          else if(tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.avisosGraficos)){
              
              if (archivoFinalArte!=null){
                  
                 
                  
                  //Determinar el punto de centrado del Aviso:
                  PdfReader readerRecibido;
                  readerRecibido = new PdfReader(archivoFinalArte);
                  Rectangle tamanioRecibido = readerRecibido.getPageSize(1);
                  float altoImagen  = tamanioRecibido.getHeight();
                  float anchoImagen = tamanioRecibido.getWidth();

                  
                  
                  //Pintar el aviso
                  PdfReader readerComponente = new PdfReader(archivoFinalArte);
                  PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
                  
                  //Determinar la posición en la cual se coloca el aviso
                  //1. Determinar el Centro
                  int centroAlto = (alto/2)+coordenada_inicial_y;
                  int centroAncho = (ancho/2)+coordenada_inicial_x;
                  int imagenEscalada =0;
                  
                  
                  //2. Determinar si hay que escalar el aviso.
                  //Caso en el que hay que escalar la imagen
                  float escalar=(float)1.0;
                  if (altoImagen > alto || anchoImagen > ancho)
                  {
                       //Determinar cuanto se debe escalar
                       float escalarX = (ancho-30) / anchoImagen;
                       float escalarY = (alto-30) / altoImagen;
                                
                       if(escalarX < escalarY)
                       {
                          escalar = escalarX;
                       }
                       else
                       {
                          escalar = escalarY;
                       }
                       imagenEscalada++;
                  }
                  anchoImagen = anchoImagen*escalar;
                  altoImagen = altoImagen*escalar;
                  
                  //3. Con base en el centro determinar coordenada escribir x y y.
                  int coordenadaEscribirX = (int) (centroAncho-(anchoImagen/2));
                  int coordenadaEscribirY = (int) (centroAlto-(altoImagen/2));
                  
                  float altoImagen2  = tamanioRecibido.getHeight();
                  float anchoImagen2 = tamanioRecibido.getWidth();                 
                  /********************************************************/
                  /*                CONTROL DE CAMBIOS                    */
                  /*     Manejo de fondo azul y recuadro sobre el     */
                  /*     aviso.                                           */
                  /********************************************************/
                  System.out.println("aviso_no_disponible =" + aviso_no_disponible); 
                   int red;
                  int green;
                  int blue;
                  if(background_Color.size()>0){
                  red = background_Color.get(0).intValue();
                  green = background_Color.get(1).intValue();
                  blue = background_Color.get(2).intValue();
                  
                  }
                  else{
                  red = 255;
                  green = 255;
                  blue = 255;
                  
                  }
               
                  System.out.println("red = "+red+" green= "+green+" blue="+blue);
    
                  if(aviso_no_disponible== true){// solo pinta el fondo azul en los avisos con diseño no el los de aviso no disponible
                        BaseFont bf2 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                        Rectangle rectanguloMarco = new Rectangle ((anchoImagen2)+30/escalar, (altoImagen2)+30/escalar);
                        //rectanguloMarco.setBackgroundColor(new BaseColor(181, 216, 240));
                        rectanguloMarco.setBackgroundColor(new BaseColor(red, green, blue));
                        System.out.println("se creo el rectangulo");
                        Document documento = new Document(rectanguloMarco);
                        ByteArrayOutputStream pdfOutputStream2 = new ByteArrayOutputStream();
                        PdfWriter writter2 = PdfWriter.getInstance(documento, pdfOutputStream2);
                        System.out.println("se creo el writter2");
                        documento.open();
                        PdfContentByte cb2 = writter2.getDirectContent();

                        cb2 = writter2.getDirectContent();
                        cb2.beginText();
                        cb2.setFontAndSize(bf2,7);
                        cb2.moveText(0, 2);
                        cb2.showText(" ");
                        cb2.endText();
                        documento.close();
                        System.out.println("se creo el cb2");
                        byte[] tempPDF2 = pdfOutputStream2.toByteArray();
                        PdfReader readerComponente2 = new PdfReader(tempPDF2);                  
                        PdfTemplate plantilla2 = writterGeneral.getImportedPage(readerComponente2, 1);
                        // pinta el fondo azul
                        content.addTemplate(plantilla2, escalar, 0,0, escalar, coordenadaEscribirX-15,coordenadaEscribirY-15 );
                  }      
                  // pinta el aviso
                  content.addTemplate(plantilla, escalar, 0,0, escalar, coordenadaEscribirX,coordenadaEscribirY);
                 
                  //Pintar el aviso de escalamiento:
                  if(imagenEscalada >0){
                      
                       //Colocar un mensaje indicando que hubo escalamiento
                       Rectangle recEscalar = new Rectangle (140,12);
                       Document documentoEscalar = new Document(recEscalar);
                       ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                       PdfWriter writter = PdfWriter.getInstance(documentoEscalar, pdfOutputStream);
                       documentoEscalar.open();
                       PdfContentByte cb = writter.getDirectContent();
                       cb.beginText();
                       BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
                       cb.setFontAndSize(bf1,7);
                       escalar = escalar * 100;
                       String textoEscala = escalar+"";
                         if (textoEscala.length()>5){
                               textoEscala = textoEscala.substring(0,4);
                         }
                       cb.showText("*** Imagen escalada al "+textoEscala+" %");
                       cb.endText();
                       documentoEscalar.close();
                       byte[] tempPDF = pdfOutputStream.toByteArray();
                       readerComponente = new PdfReader(tempPDF);
                       plantilla = writterGeneral.getImportedPage(readerComponente, 1);
                       content.addTemplate(plantilla, coordenada_inicial_x,(coordenada_inicial_y+alto));
                      
                  }
                  
              }
              else{
                  
              }
              
             
          }
          //Si la sección es de paginación no pintar los componentes.
          else if(tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.paginacion)){
              
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
