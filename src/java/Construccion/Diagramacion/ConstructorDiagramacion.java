package Construccion.Diagramacion;

import Configuracion.modelo.Componente;
import Configuracion.modelo.Configuracion;
import Configuracion.modelo.ConstantesDiagramacion;
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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import nuevo_codigo_barras.AxentriaDigitizingService;
import nuevo_codigo_barras.AxentriaDigitizingService_Service;
import org.apache.commons.codec.binary.Base64;
import parametros_plan_salesforce.ContractInfo;
import parametros_plan_salesforce.ListInfo;
import parametros_plan_salesforce.ProductionInfo;
import web_service_diagramacion.*;


import web_service_diagramacion.DiagramInfoResponse;
import web_service_diagramacion.DiagramListRequest;
import web_service_diagramacion.DiagramProdAdvRequest;
//import web_service_diagramacion.DiagrammingService;
//import web_service_diconstruirAvisoProduccionagramacion.DiagrammingService_Service;
import web_service_diagramacion.LineasTextoDTO;
import web_service_diagramacion.ProposalListDTO;

//JAAR se reemplaza el servicio DiagrammingService por la version 1.7
import web_service_diagramacion.SvcProductionDiagrammingServices17PublicarServicesDiagrammingService_Service;
import web_service_diagramacion.SvcProductionDiagrammingServices17PublicarServicesDiagrammingService;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;



/**
 *
 * @author Administrador
 */
public class ConstructorDiagramacion {
    
    byte[] archivoFinalArte;
    ByteArrayOutputStream archivoFinalArte2;
       
    public ByteArrayOutputStream construirAvisoProduccion(ProductionInfo elementoLista, TipoPagina produccionPage, ContractInfo contract, Configuracion confBasica)
    throws Exception{
        
        
        
        ByteArrayOutputStream paginaGenerada=new ByteArrayOutputStream();
        DiagramProdAdvRequest requestDiagramacion = new DiagramProdAdvRequest();
        //DiagrammingService_Service diagramacion_service = new DiagrammingService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/DiagrammingService.wsdl"));
        //DiagrammingService diagrama = diagramacion_service.getBasicHttpBindingDiagrammingService();
        SvcProductionDiagrammingServices17PublicarServicesDiagrammingService_Service diagramacion_service = new SvcProductionDiagrammingServices17PublicarServicesDiagrammingService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/publicar.services.DiagrammingService.wsdl"));
        SvcProductionDiagrammingServices17PublicarServicesDiagrammingService diagrama = diagramacion_service.getBasicHttpBindingSvcProductionDiagrammingServices17PublicarServicesDiagrammingService();
        DiagramInfoResponse processAp = null;
              
        try{
        //Transformar parÃ¡metros para generar.
        TransformadorDiagramacion transformador = new TransformadorDiagramacion();
        try{
            //System.out.println("elementoLista ="+elementoLista);
             requestDiagramacion = transformador.transformarObjetoProduccionSalesforceDiagramacion(elementoLista);
        }
        catch(Exception e){
            //System.out.println("generarAvisoErrorGeneracion4");
            e.printStackTrace();
            archivoFinalArte = this.generarAvisoErrorGeneracion(confBasica);                    
        }
                             
        //Invocar el web service para recibir el pdf generado por el diagramador.
        try{
                processAp = diagrama.processAp(requestDiagramacion);
                String mensaje = processAp.getMensajeError();
                archivoFinalArte = processAp.getPdf();
                
                
                if (archivoFinalArte==null){
                    System.out.println("generarAvisoErrorGeneracion5");
                    archivoFinalArte = this.generarAvisoErrorGeneracion(confBasica);
                }
        }
        catch(Exception e){
           e.printStackTrace();
           System.out.println("generarAvisoErrorGeneracion6");
           archivoFinalArte = this.generarAvisoErrorGeneracion(confBasica);
        }
        
       
        //Armar la pÃ¡gina.
       
        PdfReader readerPlantilla;
            
        String rutaPlantilla="plantillas_archivos/"+produccionPage.getArchivo_plantilla();
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°RUTA PLANTILLA°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
            System.out.println("---------------> " + rutaPlantilla);
        readerPlantilla = new PdfReader(rutaPlantilla);
                     
        //En esta variable se guarda el tamaÃ±o de la Plantilla.
        Rectangle pageSize = new Rectangle(readerPlantilla.getPageSize(1));
        Document documentoFinal = new Document(pageSize);
        PdfWriter writterGeneral = PdfWriter.getInstance(documentoFinal,paginaGenerada);
        documentoFinal.open();
        PdfTemplate plantilla = writterGeneral.getImportedPage(readerPlantilla, 1);
        PdfContentByte content = writterGeneral.getDirectContent();
        content.addTemplate(plantilla, 0,0);
            
        /********************************************************/
        /*       AGREGAR CADA UNA DE LAS SECCIONES              */
        /********************************************************/
        try{
            for (int i=0; i<produccionPage.getSeccionesPagina().size(); i++){
            
                     Seccion sec = produccionPage.getSeccionesPagina().get(i);
                     this.agregarSeccion(content, sec, writterGeneral, elementoLista,contract);
                                    
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
        }
        
        return paginaGenerada;
        
    }
    
    
    
    
    /**
    * Genera un pdf en el que se informa que hubo algÃºn error en la generaciÃ³n del aviso.
    */
    public byte[] generarAvisoErrorGeneracion(Configuracion configBasica){
        //Crear un Pdf que diga Aviso No Disponible
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
    
    
    public void agregarSeccion (PdfContentByte content, Seccion sec, PdfWriter writterGeneral, ProductionInfo advertise, ContractInfo contract) throws Exception{
         
        try{
            int coordenada_inicio_x = sec.getCoordenada_inicio_x();
            int coordenada_inicio_y = sec.getCoordenada_inicio_y();
            int alto = sec.getAlto();
            int ancho = sec.getAncho();
            int blue = sec.getColor_fondo_blue();
            int green = sec.getColor_fondo_green();
            int red = sec.getColor_fondo_red();
            String tipoSeccion = sec.getTipo_seccion();

            //Crear un objeto para la secciÃ³n
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
                agregarComponente(content, componente, writterGeneral, coordenada_inicio_x, coordenada_inicio_y, tipoSeccion, advertise,contract);
                        
            }
        
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
       
    }
    
    public void agregarComponente(PdfContentByte content, Componente comp, PdfWriter writterGeneral, int x_inicial, int y_inicial, String tipoSeccion, ProductionInfo advertise, ContractInfo contract)throws Exception{
        
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
          System.out.println("TIPO SECCION DIAGRAMACION +++++= "+tipoSeccion);
          
          
          //Caso Texto DinÃ¡mico
          if (esDinamico.booleanValue() ==true){
              
              String nombreClase = "";
              
              //SecciÃ³n de Datos de GestiÃ³n de Avisos
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.gestionAvisos)){
                  Class miClase = Class.forName("parametros_plan_salesforce.ProductionInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(advertise);
                      textoEscribir = textoI.getValue();
                      
                     
                      //System.out.println("atributo agregarComponente "+atributo);
                      //System.out.println("textoEscribir agregarComponente "+textoEscribir);
                      if("sketchNumber".equals(atributo)){// solo imprime el ID SOL PROPUESTA si es distinto al ID COMPRA
                          //System.out.println("if(atributo==sketchNumber) ");
                          JAXBElement<String> tipoSketchJ = advertise.getIdPurchase();
                          String SketchNumber = tipoSketchJ.getValue();
                          //System.out.println("sketchNumber= "+SketchNumber);
                          //System.out.println("textoEscribir= "+textoEscribir);
                          if(textoEscribir.equals(SketchNumber)){
                              textoEscribir="";
                          }
                      
                      }
                     
                  }
                  catch (Exception e){
                      throw e;
                  }
              }
              
              //SecciÃ³n datos del Contrato
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
              
                           
              //SecciÃ³n CÃ³digo de BarrasCODIGO_BARRAS
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
                      System.err.println("Error obteniendo servicios web para la generaciÃ³n del cÃ³digo de barras.\n");
                      System.err.println("Traza de la ExcepciÃ³n: "+e);
                     
                  }
              }
            if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.Fecha_de_impresion)){
                  Date hoy =new Date();
                  
                  SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                  Date resultdate = new Date();
                  System.out.println(date_format.format(resultdate));
                  
                  System.out.println("FECHA DIAGRAMACION ES IGUAL A = "+date_format.format(resultdate).toString());
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
              }
             
          }
                  
           /**********************************************************/
           /*          CASO MOSTRAR EL AVISO                         */
           /**********************************************************/
          else if(tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.avisosProduccion)){
              
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
                  
                  //Determinar la posiciÃ³n en la cual se coloca el aviso
                  //1. Determinar el Centro
                  int centroAlto = (alto/2)+coordenada_inicial_y;
                  int centroAncho = (ancho/2)+coordenada_inicial_x;
                  
                  //2. Con base en el centro determinar coordenada escribir x y y.
                  int coordenadaEscribirX = (int) (centroAncho-(anchoImagen/2));
                  int coordenadaEscribirY = (int) (centroAlto-(altoImagen/2));
                  
                  content.addTemplate(plantilla, coordenadaEscribirX,coordenadaEscribirY);
                                
                  
              }
              else{
                  
              }
              
             
          }
          //Si la secciÃ³n es de paginaciÃ³n no pintar los componentes.
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
    
   //JAAR 12-06-2013 se agrega el parametro contenido para cambio TAG
    public ByteArrayOutputStream construirAvisoDiagramacion(ListInfo elementoLista, TipoPagina produccionPage, ContractInfo contract, Configuracion confBasica,int z, boolean contenido)   
    throws Exception{
        System.out.println("entro_a_ConstruirAvisoDiagramacionz -->>"+ z + "contenido==>>>"+ contenido + "elemento lista: " + elementoLista);

        try{
            //JAAR cambio servicio diagramming 1.7
            archivoFinalArte2 = new ByteArrayOutputStream();
            ByteArrayOutputStream paginaGenerada=new ByteArrayOutputStream();
            SvcProductionDiagrammingServices17PublicarServicesDiagrammingService_Service diagramacion_service = new SvcProductionDiagrammingServices17PublicarServicesDiagrammingService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/publicar.services.DiagrammingService.wsdl"));
            SvcProductionDiagrammingServices17PublicarServicesDiagrammingService diagrama = diagramacion_service.getBasicHttpBindingSvcProductionDiagrammingServices17PublicarServicesDiagrammingService();
        
       
        DiagramListRequest requestDiagramacion = new DiagramListRequest();
        
        ProposalListDTO proposal= new ProposalListDTO();
        
           //Realizar la copia del Objeto.
           //Transformar parÃ¡metros para generar.
           TransformadorDiagramacion transformador = new TransformadorDiagramacion(); 
           try{
               System.out.println("elementoLista 3 ="+elementoLista.getProductCode());
                requestDiagramacion = transformador.transformarObjetoProduccionSalesforceDiagramacionListas(elementoLista);
                  
                  ProposalListDTO listaDTO = requestDiagramacion.getListaDTO();
                  
            List<LineasTextoDTO> lineasTextoSet = requestDiagramacion.getListaDTO().getLineasTextoSet();
            
            for (int ia=0; ia<lineasTextoSet.size(); ia++){
                LineasTextoDTO get = lineasTextoSet.get(ia);
                
                
                
                 
            }
            
           }
           catch(Exception e){
                System.out.println("generarAvisoErrorGeneracion1 ");
               e.printStackTrace();
               archivoFinalArte = this.generarAvisoErrorGeneracion(confBasica);                    
           }
           
           //Invocar el Web Service de DiagramaciÃ³n
           try{
                DiagramInfoResponse processAp  = diagrama.processList(requestDiagramacion);
                String mensaje = processAp.getMensajeError();
                System.out.println("mensaje de error" + mensaje);
                archivoFinalArte = processAp.getPdf();
                
               //****************************************************************************
                //JAAR 12-06-2013 cambio tag = si la variable contenido esta en true se genera una espacio en el plan no muestra contenido
                
                if (archivoFinalArte==null && contenido == false){
                     System.out.println("==***===GenerarAvisoErrorGeneracion8==**== ");
                     archivoFinalArte = this.generarAvisoNoDisponible(confBasica);
                     //JAAR muestra la plantilla en blanco
                } else if(archivoFinalArte==null && contenido == true)
                {
                    System.out.println("==***===No_debe_Llevar_Aviso==**== ");
                    // quitar
                    archivoFinalArte = this.generarAvisoSinContenido(confBasica);
                }
               //**************************************************************************** 
           }
           catch(Exception e){
               System.out.println("generarAvisoErrorGeneracion2 ");
               e.printStackTrace();
               archivoFinalArte = this.generarAvisoErrorGeneracion(confBasica);
              
           }
            
        }
        catch(Exception e){
            System.out.println("generarAvisoErrorGeneracion3 ");
            e.printStackTrace();
            archivoFinalArte = this.generarAvisoErrorGeneracion(confBasica);
        }
        
        //Transformar el PDF En un byteArrayOutputStream
        archivoFinalArte2 = new ByteArrayOutputStream();
        archivoFinalArte2.write(archivoFinalArte);
        
        return archivoFinalArte2;
    }
    
    
    public List<ByteArrayOutputStream> construirListaPaginasFinal(List<ListInfo> elementoLista, TipoPagina diagramacionListasPage,ContractInfo contract, Configuracion configBasica,List<ByteArrayOutputStream>listaArchivosTemporal){
        
        List<ByteArrayOutputStream> listaRetorno = new ArrayList<ByteArrayOutputStream>();
        ByteArrayOutputStream paginaGenerada=null;
        
        int altoSeccionBasica=0;
        int altoSeccionAviso=0;
                
        //Determinar el tamaÃ±o de la secciÃ³n en la que se pintan los datos del aviso.
         for (int i=0; i<diagramacionListasPage.getSeccionesPagina().size(); i++){
            
              Seccion sec = diagramacionListasPage.getSeccionesPagina().get(i);
               
              if (sec.getTipo_seccion().equals(ConstantesDiagramacion.informacion_basica_listas)){
                  altoSeccionBasica = sec.getAlto();
              }
              
              if (sec.getTipo_seccion().equals(ConstantesDiagramacion.archivo_lista_logo)){
                  altoSeccionAviso = sec.getAlto();
              }
        }
         
        
        //Ir recorriendo la lista de avisos generados para ver si caben o se requiere
        //generar una nueva pÃ¡gina.
        try{
        
        
        for (int k=0; k<listaArchivosTemporal.size();k++){
          
            //Insertar el primer aviso
            int multipagina=0;
            float altoTemporal= 0;
            List<ByteArrayOutputStream> listaTemporalPdfs = new ArrayList<ByteArrayOutputStream>();
            List<ListInfo> listaTemporalDatosBasicos = new ArrayList<ListInfo>();
            ByteArrayOutputStream pdfTemporal1 = new ByteArrayOutputStream();
            PdfReader readerComponente = null;
            byte[] pdfTemporal2 = null;
            pdfTemporal1 = listaArchivosTemporal.get(k);
            pdfTemporal2 = pdfTemporal1.toByteArray();
            readerComponente = new PdfReader(pdfTemporal2);
            Rectangle tamanioRecibido = readerComponente.getPageSize(1);
            float alto  = tamanioRecibido.getHeight();
            float ancho = tamanioRecibido.getWidth();
            altoTemporal+=altoSeccionBasica;
            altoTemporal+=alto;
            altoTemporal+=5;
            listaTemporalPdfs.add(pdfTemporal1);
            listaTemporalDatosBasicos.add(elementoLista.get(k));
            int numeroPaginas= readerComponente.getNumberOfPages();
            
            if(numeroPaginas >1){
                
                
                paginaGenerada = generarPaginaParcial(listaTemporalPdfs,listaTemporalDatosBasicos,diagramacionListasPage,contract, configBasica);
                
                listaRetorno.add(paginaGenerada);
                
                multipagina++;
                
                altoTemporal = 1000;
                
                
                for (int i=1; i<numeroPaginas; i++){
                    
                    ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                    Document document = new Document(readerComponente.getPageSizeWithRotation(i+1));
                    PdfWriter writter = PdfWriter.getInstance(document, pdfOutputStream);
                    document.open();
                    PdfTemplate topOfPage = writter.getImportedPage( readerComponente, i+1 );
                    PdfContentByte content = writter.getDirectContent();
		    content.addTemplate( topOfPage, 0, 0 );
                    document.close();
                    listaTemporalPdfs = new ArrayList<ByteArrayOutputStream>();
                    listaTemporalPdfs.add(pdfOutputStream);
                    paginaGenerada = generarPaginaParcialContinuacion(listaTemporalPdfs,listaTemporalDatosBasicos,diagramacionListasPage,contract, configBasica);
                    listaRetorno.add(paginaGenerada);
                    
                    
                }
                
                
            }
            
            
            //Mirar si cabe el segundo aviso
            if (k+1 < listaArchivosTemporal.size()){
                pdfTemporal1 = listaArchivosTemporal.get(k+1);
                pdfTemporal2 = pdfTemporal1.toByteArray();
                readerComponente = new PdfReader(pdfTemporal2);
                tamanioRecibido = readerComponente.getPageSize(1);
                alto  = tamanioRecibido.getHeight();
                ancho = tamanioRecibido.getWidth();
                
                float altoTemporal2=altoTemporal+altoSeccionBasica;
                altoTemporal2+=alto;
                
                if(altoTemporal2<=altoSeccionAviso){
                   listaTemporalPdfs.add(pdfTemporal1);
                   listaTemporalDatosBasicos.add(elementoLista.get(k+1)); 
                   k++;
                   altoTemporal+=altoSeccionBasica;
                   altoTemporal+=alto;
                   altoTemporal+=5;
                }
            }
            
            //Mirar si cabe el tercer aviso
            if (k+1 < listaArchivosTemporal.size()){
                pdfTemporal1 = listaArchivosTemporal.get(k+1);
                pdfTemporal2 = pdfTemporal1.toByteArray();
                readerComponente = new PdfReader(pdfTemporal2);
                tamanioRecibido = readerComponente.getPageSize(1);
                alto  = tamanioRecibido.getHeight();
                ancho = tamanioRecibido.getWidth();
                
                float altoTemporal3=altoTemporal+altoSeccionBasica;
                altoTemporal3+=alto;
                
                if(altoTemporal3<=altoSeccionAviso){
                   listaTemporalPdfs.add(pdfTemporal1);
                   listaTemporalDatosBasicos.add(elementoLista.get(k+1)); 
                   k++;
                }
            }
            
            //Mandara a generar la pÃ¡gina y agregarla a la lista de pÃ¡ginas
            if (multipagina==0){
                paginaGenerada = generarPaginaParcial(listaTemporalPdfs,listaTemporalDatosBasicos,diagramacionListasPage,contract, configBasica);
                listaRetorno.add(paginaGenerada);
            }
            
         }
            
            
 
        }
        catch(Exception e){
            e.printStackTrace();
            
        }
        
        
        return listaRetorno;
    }
   
    
    public ByteArrayOutputStream generarPaginaParcial(List<ByteArrayOutputStream>listaTemporalPdfs,List<ListInfo> listaTemporalDatosBasicos,TipoPagina diagramacionListasPage,ContractInfo contract, Configuracion configBasica){
        
       ByteArrayOutputStream paginaGenerada = new ByteArrayOutputStream(); 
       try{
            
            PdfReader readerPlantilla;
            String rutaPlantilla="plantillas_archivos/"+diagramacionListasPage.getArchivo_plantilla();
            
                    
            readerPlantilla = new PdfReader(rutaPlantilla);
                     
            //En esta variable se guarda el tamaÃ±o de la Plantilla.
            Rectangle pageSize = new Rectangle(readerPlantilla.getPageSize(1));
            Document documentoFinal = new Document(pageSize);
            PdfWriter writterGeneral = PdfWriter.getInstance(documentoFinal,paginaGenerada);
            documentoFinal.open();
            PdfTemplate plantilla = writterGeneral.getImportedPage(readerPlantilla, 1);
            PdfContentByte content = writterGeneral.getDirectContent();
            content.addTemplate(plantilla, 0,0);
            Seccion sec = new Seccion();
            int altoConfiguracionInicial = 0;
            
            /********************************************************/
            /*       AGREGAR CADA UNA DE LAS SECCIONES              */
            /********************************************************/
            try{
                 for (int i=0; i<diagramacionListasPage.getSeccionesPagina().size(); i++){
            
                     sec = diagramacionListasPage.getSeccionesPagina().get(i);
                     //altoConfiguracionInicial = sec.getCoordenada_inicio_y();
                     
                     int nuevoAlto=sec.getCoordenada_inicio_y();
                     int altoOriginal=0;
                    //Caso en que la secciÃ³n es de tipo AVISOS_INTERNET
                    if (sec.getTipo_seccion().equals(ConstantesDiagramacion.informacion_basica_listas)) {
                       for (int paginas=0; paginas <listaTemporalPdfs.size(); paginas++){
                          if(paginas==0){
                              //Agregar la SecciÃ³n de datos bÃ¡sicos
                           
                              altoOriginal=sec.getCoordenada_inicio_y();                             
                              nuevoAlto = sec.getCoordenada_inicio_y();
                              this.agregarSeccionListas(content, sec, writterGeneral, contract, listaTemporalPdfs, listaTemporalDatosBasicos,paginas,nuevoAlto); 
                              
                              nuevoAlto -=5;
                          
                              //Determinar el tamaÃ±o del primer pdf
                              ByteArrayOutputStream avisoInternet = listaTemporalPdfs.get(paginas);
                              byte[] pdfTemporal2 = null;
                              pdfTemporal2 = avisoInternet.toByteArray();
                              PdfReader readerComponente = new PdfReader(pdfTemporal2);
                              Rectangle tamanioRecibido = readerComponente.getPageSize(1);
                              float alto  = tamanioRecibido.getHeight();
                              nuevoAlto-=alto;
                              agregarAvisoDiagramacion(content,writterGeneral,sec.getCoordenada_inicio_x(),nuevoAlto,avisoInternet);
                              
                            
                            
                          }
                          else{
                              //Agregar la SecciÃ³n de datos bÃ¡sicos
                              nuevoAlto-=sec.getAlto();  
                              nuevoAlto -=5;
                              this.agregarSeccionListas(content, sec, writterGeneral, contract, listaTemporalPdfs, listaTemporalDatosBasicos,paginas,nuevoAlto); 
                              
                              ByteArrayOutputStream avisoInternet = listaTemporalPdfs.get(paginas);
                              byte[] pdfTemporal2 = null;
                              pdfTemporal2 = avisoInternet.toByteArray();
                              PdfReader readerComponente = new PdfReader(pdfTemporal2);
                              Rectangle tamanioRecibido = readerComponente.getPageSize(1);
                              float alto  = tamanioRecibido.getHeight();
                              nuevoAlto-=alto;
                              agregarAvisoDiagramacion(content,writterGeneral,sec.getCoordenada_inicio_x(),nuevoAlto,avisoInternet);
                              
                                        
                          }
                       }
                                             
                    }
                    
                    else if (sec.getTipo_seccion().equals(ConstantesDiagramacion.archivo_lista_logo)) {
                    }
            
                    else{
                       
                       this.agregarSeccionListas(content, sec, writterGeneral, contract, listaTemporalPdfs, listaTemporalDatosBasicos,0,nuevoAlto);
                       
                    }
                    
                                    
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
            
        } 
        
       return paginaGenerada;
    }
    
    
        public void agregarSeccionListas (PdfContentByte content, Seccion sec, PdfWriter writterGeneral, ContractInfo contract, List<ByteArrayOutputStream>listaTemporalPdfs, List<ListInfo> listaTemporalDatosBasicos, int pagina, int nuevoAlto) throws Exception{
        
        try{
            int coordenada_inicio_x = sec.getCoordenada_inicio_x();
            //int coordenada_inicio_y = sec.getCoordenada_inicio_y();
            int coordenada_inicio_y = nuevoAlto;
            int alto = sec.getAlto();
            int ancho = sec.getAncho();
            int blue = sec.getColor_fondo_blue();
            int green = sec.getColor_fondo_green();
            int red = sec.getColor_fondo_red();
            String tipoSeccion = sec.getTipo_seccion();

            //Crear un objeto para la secciÃ³n
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
            
            //Determinar el componente
            ListInfo advertise = listaTemporalDatosBasicos.get(pagina);
        
            for (int j=0; j< sec.getComponentesSeccion().size();j++){
                
                Componente componente = sec.getComponentesSeccion().get(j);
                
                agregarComponenteListas(content, componente, writterGeneral, coordenada_inicio_x, coordenada_inicio_y, tipoSeccion, advertise,contract);
                
            }
          
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
       
    }
        
        
   public void agregarComponenteListas(PdfContentByte content, Componente comp, PdfWriter writterGeneral, int x_inicial, int y_inicial, String tipoSeccion, ListInfo advertise, ContractInfo contract)throws Exception{
        
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
          
          
          //Caso Texto DinÃ¡mico
          if (esDinamico.booleanValue() ==true){
              
              String nombreClase = "";
              
              //SecciÃ³n de Datos de GestiÃ³n de Avisos
              if (tipoSeccion.equalsIgnoreCase(ConstantesDiagramacion.informacion_basica_listas)){
                  Class miClase = Class.forName("parametros_plan_salesforce.ListInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(advertise);
                      textoEscribir = textoI.getValue();
                      //System.out.println("atributo agregarComponente2 "+atributo);
                      //System.out.println("textoEscribir agregarComponente2 "+textoEscribir);
                      if("purchase".equals(atributo)){// solo imprime el ID SOL PROPUESTA si es distinto al ID COMPRA
                          //System.out.println("if(atributo==sketchNumber) ");
                          JAXBElement<String> tipoSketchJ = advertise.getSketchNumber();
                          String SketchNumber = tipoSketchJ.getValue();
                          //System.out.println("sketchNumber= "+SketchNumber);
                          //System.out.println("textoEscribir= "+textoEscribir);
                          if(SketchNumber != null){
                            if(textoEscribir.equals(SketchNumber))
                            {

                                textoEscribir="";
                            }
                          }
                      
                      }
                      
                     
                  }
                  catch (Exception e){
                      throw e;
                  }
              }
              
              //SecciÃ³n datos del Contrato
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
              
                           
              //SecciÃ³n CÃ³digo de BarrasCODIGO_BARRAS
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
                      System.err.println("Error obteniendo servicios web para la generaciÃ³n del cÃ³digo de barras.\n");
                      System.err.println("Traza de la ExcepciÃ³n: "+e);
                     
                  }
              }
             if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.Fecha_de_impresion)){
                  Date hoy =new Date();
                  
                  SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                  Date resultdate = new Date();
                  System.out.println(date_format.format(resultdate));
                  
                  System.out.println("FECHA agregarComponenteListas ES IGUAL A = "+date_format.format(resultdate).toString());
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
              }
             
          }
          //Si la secciÃ³n es de paginaciÃ³n no pintar los componentes.
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
 
 
   public void agregarAvisoDiagramacion(PdfContentByte content, PdfWriter writterGeneral, int coordx, int coordy, ByteArrayOutputStream avisoInternet){
        
        try{
            PdfReader readerRecibido;
        System.out.println(" &&&&&&&==COORDENADA_ENVIADA_agregarAvisoDiagramacion nuevoAltoImpreso=="+ coordy);     
        byte[] archivoGenerado = avisoInternet.toByteArray();
        readerRecibido = new PdfReader(archivoGenerado);
        Rectangle tamanioRecibido = readerRecibido.getPageSize(1);
        float altoImagen  = tamanioRecibido.getHeight();
        float anchoImagen = tamanioRecibido.getWidth();
        PdfTemplate plantilla = writterGeneral.getImportedPage(readerRecibido, 1);
        content.addTemplate(plantilla, coordx,coordy);
        
        }
        
        catch(Exception e){
            e.printStackTrace();
        }
        
        
        
    }
   
   
    public ByteArrayOutputStream generarPaginaParcialContinuacion(List<ByteArrayOutputStream>listaTemporalPdfs,List<ListInfo> listaTemporalDatosBasicos,TipoPagina diagramacionListasPage,ContractInfo contract, Configuracion configBasica){
        
       ByteArrayOutputStream paginaGenerada = new ByteArrayOutputStream(); 
       try{
            
            PdfReader readerPlantilla;
            String rutaPlantilla="plantillas_archivos/"+diagramacionListasPage.getArchivo_plantilla();
            
                    
            readerPlantilla = new PdfReader(rutaPlantilla);
                     
            //En esta variable se guarda el tamaÃ±o de la Plantilla.
            Rectangle pageSize = new Rectangle(readerPlantilla.getPageSize(1));
            Document documentoFinal = new Document(pageSize);
            PdfWriter writterGeneral = PdfWriter.getInstance(documentoFinal,paginaGenerada);
            documentoFinal.open();
            PdfTemplate plantilla = writterGeneral.getImportedPage(readerPlantilla, 1);
            PdfContentByte content = writterGeneral.getDirectContent();
            content.addTemplate(plantilla, 0,0);
            Seccion sec = new Seccion();
            int altoConfiguracionInicial = 0;
            
            /********************************************************/
            /*       AGREGAR CADA UNA DE LAS SECCIONES              */
            /********************************************************/
            try{
                 for (int i=0; i<diagramacionListasPage.getSeccionesPagina().size(); i++){
            
                     sec = diagramacionListasPage.getSeccionesPagina().get(i);
                     int nuevoAlto = sec.getCoordenada_inicio_y();
                     
                    //Caso en que la secciÃ³n es de tipo AVISOS_INTERNET
                    if (sec.getTipo_seccion().equals(ConstantesDiagramacion.informacion_basica_listas)) {
                       for (int paginas=0; paginas <listaTemporalPdfs.size(); paginas++){
                          if(paginas==0){
                              //Agregar la SecciÃ³n de datos bÃ¡sicos
                              
                              /*
                              System.out.println("sec.getCoordenada_inicio_y2()++++++++;"+sec.getCoordenada_inicio_y()); 
                              System.out.println("sec.getAlto2()++++++++;"+sec.getAlto()); 
                             
                              nuevoAlto = sec.getCoordenada_inicio_y();
                              this.agregarSeccionListasContinuacion(content, sec, writterGeneral, contract, listaTemporalPdfs, listaTemporalDatosBasicos,paginas,nuevoAlto); 
                              nuevoAlto -=5;
                              
                              
                              
                              //Determinar el tamaÃ±o del primer pdf
                              ByteArrayOutputStream avisoInternet = listaTemporalPdfs.get(paginas);
                              byte[] pdfTemporal2 = null;
                              pdfTemporal2 = avisoInternet.toByteArray();
                              PdfReader readerComponente = new PdfReader(pdfTemporal2);
                              Rectangle tamanioRecibido = readerComponente.getPageSize(1);
                              float alto  = tamanioRecibido.getHeight();
                              nuevoAlto-=alto;
                              agregarAvisoDiagramacionContinuacion(content,writterGeneral,sec.getCoordenada_inicio_x(),nuevoAlto,avisoInternet);
                              */   
                              
                              nuevoAlto = sec.getCoordenada_inicio_y();
                              nuevoAlto +=sec.getAlto();
                              System.out.println("sec.getCoordenada_inicio_y()++++++++;"+sec.getCoordenada_inicio_y()); 
                              System.out.println("sec.getAlto()++++++++;"+sec.getAlto()); 
                              //Determinar el tamaÃ±o del siguiente pdf
                              ByteArrayOutputStream avisoInternet = listaTemporalPdfs.get(paginas);
                              byte[] pdfTemporal2 = null;
                              pdfTemporal2 = avisoInternet.toByteArray();
                              PdfReader readerComponente = new PdfReader(pdfTemporal2);
                              Rectangle tamanioRecibido = readerComponente.getPageSize(1);
                              float alto  = tamanioRecibido.getHeight();
                              nuevoAlto-=alto;
                              agregarAvisoDiagramacion(content,writterGeneral,sec.getCoordenada_inicio_x(),nuevoAlto,avisoInternet);
                              
                              
                          }
                          else{
                              //Agregar la SecciÃ³n de datos bÃ¡sicos
                              nuevoAlto = sec.getCoordenada_inicio_y();
                              nuevoAlto +=sec.getAlto();
                              System.out.println("sec.getCoordenada_inicio_y()++++++++;"+sec.getCoordenada_inicio_y()); 
                              System.out.println("sec.getAlto()++++++++;"+sec.getAlto()); 
                              //Determinar el tamaÃ±o del siguiente pdf
                              ByteArrayOutputStream avisoInternet = listaTemporalPdfs.get(paginas);
                              byte[] pdfTemporal2 = null;
                              pdfTemporal2 = avisoInternet.toByteArray();
                              PdfReader readerComponente = new PdfReader(pdfTemporal2);
                              Rectangle tamanioRecibido = readerComponente.getPageSize(1);
                              float alto  = tamanioRecibido.getHeight();
                              nuevoAlto-=alto;
                              agregarAvisoDiagramacion(content,writterGeneral,sec.getCoordenada_inicio_x(),nuevoAlto,avisoInternet);
                              
                              
                          }
                       }
                       
                    }
                    
                    else if (sec.getTipo_seccion().equals(ConstantesDiagramacion.archivo_lista_logo)) {
                    }
            
                    else{
                       
                       this.agregarSeccionListasContinuacion(content, sec, writterGeneral, contract, listaTemporalPdfs, listaTemporalDatosBasicos,0,nuevoAlto);
                       
                    }
                    
                                    
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
            
        } 
        
       return paginaGenerada;
    }
    
    
     public void agregarSeccionListasContinuacion (PdfContentByte content, Seccion sec, PdfWriter writterGeneral, ContractInfo contract, List<ByteArrayOutputStream>listaTemporalPdfs, List<ListInfo> listaTemporalDatosBasicos, int pagina, int nuevoAlto) throws Exception{
        
        try{
             System.out.println(" &&&&&&&==COORDENADA_ENVIADA_agregarAvisoInternet nuevoAltoImpreso=="+ nuevoAlto); 
            int coordenada_inicio_x = sec.getCoordenada_inicio_x();
            int coordenada_inicio_y = nuevoAlto;
            int alto = sec.getAlto();
            int ancho = sec.getAncho();
            int blue = sec.getColor_fondo_blue();
            int green = sec.getColor_fondo_green();
            int red = sec.getColor_fondo_red();
            String tipoSeccion = sec.getTipo_seccion();

            //Crear un objeto para la secciÃ³n
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
            
            //Determinar el componente
            ListInfo advertise = listaTemporalDatosBasicos.get(pagina);
        
            for (int j=0; j< sec.getComponentesSeccion().size();j++){
                
                Componente componente = sec.getComponentesSeccion().get(j);
                
                agregarComponenteListasContinuacion(content, componente, writterGeneral, coordenada_inicio_x, coordenada_inicio_y, tipoSeccion, advertise,contract);
                
            }
          
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
       
    }
 
     
     
 public void agregarComponenteListasContinuacion(PdfContentByte content, Componente comp, PdfWriter writterGeneral, int x_inicial, int y_inicial, String tipoSeccion, ListInfo advertise, ContractInfo contract)throws Exception{
        
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
          
          
          //Caso Texto DinÃ¡mico
          if (esDinamico.booleanValue() ==true){
              
              String nombreClase = "";
              
             
              
              //SecciÃ³n datos del Contrato
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosContrato)){
                  Class miClase = Class.forName("parametros_plan_salesforce.ContractInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(contract);
                      textoEscribir = textoI.getValue();
                      //System.out.println("atributo agregarComponente3 "+atributo);
                      //System.out.println("textoEscribir agregarComponente3 "+textoEscribir);
                  }
                  catch (Exception e){
                      throw e;
                      
                  }
              }
              
                           
              //SecciÃ³n CÃ³digo de BarrasCODIGO_BARRAS
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
                      System.err.println("Error obteniendo servicios web para la generaciÃ³n del cÃ³digo de barras.\n");
                      System.err.println("Traza de la ExcepciÃ³n: "+e);
                     
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
                  float escalarY = (float) 0.8;
                  float escalarX = (float) 1.0;
                  content.addTemplate(plantilla, escalarX, 0,0, escalarY, coordenada_inicial_x,coordenada_inicial_y);
              }
             
          }
          //Si la secciÃ³n es de paginaciÃ³n no pintar los componentes.
          else if(tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.paginacion)){
              
          }
                  
                   
          else if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosContrato)){
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
 
 
 public void agregarAvisoDiagramacionContinuacion(PdfContentByte content, PdfWriter writterGeneral, int coordx, int coordy, ByteArrayOutputStream avisoInternet){
        
        try{
            PdfReader readerRecibido;
            
        byte[] archivoGenerado = avisoInternet.toByteArray();
        readerRecibido = new PdfReader(archivoGenerado);
        Rectangle tamanioRecibido = readerRecibido.getPageSize(1);
        tamanioRecibido.setBackgroundColor(new BaseColor(120,189,201));
        float altoImagen  = tamanioRecibido.getHeight();
        float anchoImagen = tamanioRecibido.getWidth();
        
        PdfTemplate plantilla = writterGeneral.getImportedPage(readerRecibido, 1);
        //float reducir = 534 - altoImagen;
        //coordy+=reducir;
        content.addTemplate(plantilla, coordx,coordy);
       
        }
        
        catch(Exception e){
            e.printStackTrace();
        }
 }
 /**
    * Genera un pdf en el que se informa que hubo algÃºn error en la generaciÃ³n del aviso.
    */
    public byte[] generarAvisoNoDisponible(Configuracion configBasica){
        //Crear un Pdf que diga Aviso No Disponible
        byte[] archivoErrorGeneracion=null;
        try{
            String rutaPlantilla = configBasica.getPlantillaAvisoGenesisNoDisponible();
             System.out.println("rutaPlantilla)++++++++"+rutaPlantilla);
             
             
            PdfReader readerImagen = new PdfReader("plantillas_archivos/aviso_no_contenido_disponible.pdf");
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
    
    
     /**
    *JAAR 13-006-2013 cambio Tags Genera un pdf en blanco.
    */
    public byte[] generarAvisoSinContenido(Configuracion configBasica){
        //Crear un Pdf en blanco
        byte[] archivoErrorGeneracion=null;
        try{
            String rutaPlantilla = configBasica.getPlantillaAvisoGenesisNoDisponible();
             System.out.println("rutaPlantilla)++++++++"+rutaPlantilla);
             
             
            PdfReader readerImagen = new PdfReader("plantillas_archivos/aviso_sin_contenido.pdf");
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
    
}
