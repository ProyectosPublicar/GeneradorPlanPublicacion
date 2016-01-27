/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Construccion.Internet;

import Configuracion.modelo.Componente;
import Configuracion.modelo.Configuracion;
import Configuracion.modelo.ConstantesInternet;
import Configuracion.modelo.ConstantesTipoSeccion;
import Configuracion.modelo.Seccion;
import Configuracion.modelo.TipoPagina;
import codigo_barras.BarCode;
import codigo_barras.ProductionDigitizingService;
import codigo_barras.ProductionDigitizingService_Service;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import nuevo_codigo_barras.AxentriaDigitizingService;
import nuevo_codigo_barras.AxentriaDigitizingService_Service;
import org.apache.commons.codec.binary.Base64;
import parametros_plan_salesforce.ContractInfo;
import parametros_plan_salesforce.InternetInfo;
import parametros_plan_salesforce.AdditionalSectionInfo;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Administrador
 */
public class ConstructorInternet {
    
    public ByteArrayOutputStream construirAvisoInternet(InternetInfo elementoLista, TipoPagina internetPage, ContractInfo contract, Configuracion configBasica, int z)
    {
        
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
        List<Seccion> seccionesPagina = internetPage.getSeccionesPagina();
        if (seccionesPagina != null){
            
            //Obtener la sección relacionada con configuración de avisos de internet.
            for (int i=0; i< seccionesPagina.size(); i++){
                Seccion seccion = seccionesPagina.get(i);
                 
                //Obtener la configuración para generación del aviso.
                if (seccion.getTipo_seccion().equals(ConstantesTipoSeccion.avisosProduccionConfiguracion)){
                    
                  int alto = determinarTamanoAvisoInternet(elementoLista, seccion);
                  bos = construirArchivoAvisoInternet(elementoLista, seccion, alto,z);
                    
                 
                }
                
            }
            
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        return bos;
    }

    
  private int determinarTamanoAvisoInternet(InternetInfo elementoLista, Seccion seccion) {
        
        
     int tamanoFinal=0;
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
     int alto = seccion.getAlto();
     int ancho = seccion.getAncho();
     float verticalPosition=0; 
     
        
     //1. Obtener los componentes de la sección
     //Se deben configurar en este orden en la base de datos. 
     try{
         
        Rectangle rect = new Rectangle (ancho, alto);
        Document document = new Document(rect,5,5,5,5);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        writer.setInitialLeading(2);
        Rectangle rct = new Rectangle(ancho, alto);
        writer.setBoxSize("art", rct);
        
        document.open();
        
        for (int r=0; r<seccion.getComponentesSeccion().size();r++){
            
            Componente comp = seccion.getComponentesSeccion().get(r);
            
            //Obtener los atributos.
            Boolean esDinamico = comp.getEsDinamico();
            String atributo = comp.getAtributo();
            String label = comp.getLabel();
            String textoEscribir="";
            String tipoFuente = comp.getTipoFuente();
            int tamanoFuente = comp.getTamano_fuente();
            //El estilo de decoración de la Fuente se coloca en el atributo alto.
            int tipoDecoracion = comp.getAlto();
            int colorFuenteRed = comp.getColor_fuente_red();
            int colorFuenteGreen = comp.getColor_fuente_green();
            int colorFuenteBlue = comp.getColor_fuente_blue(); 
            int identacion = comp.getCoordenada_inicio_x();
           
            if (esDinamico.booleanValue() ==true){
              
              //Caso en el que se requieren recuperar las secciones:
              if (atributo.equals(ConstantesInternet.Lista_Otras_Secciones)){
                  textoEscribir=ConstantesInternet.Lista_Otras_Secciones;
              }
               
              else{
              Class miClase = Class.forName("parametros_plan_salesforce.InternetInfo");
              try{
                  Field f = miClase.getDeclaredField(atributo);
                  f.setAccessible(true);
                  JAXBElement<String> textoI = (JAXBElement<String>) f.get(elementoLista);
                  textoEscribir = textoI.getValue();
              }
              catch (Exception e){
                  e.printStackTrace();
              }
              }
            }
            else {
               textoEscribir=label;
            }
            BaseColor baseColor = new BaseColor(colorFuenteRed,colorFuenteGreen,colorFuenteBlue);
            Font font = FontFactory.getFont("fuentes_archivos/"+tipoFuente, tamanoFuente, tipoDecoracion, baseColor);
            if (r ==0){
                document.add(new Chunk(Chunk.NEWLINE));
            }
            
            if (textoEscribir!=null){
                
                //Caso Lista de las Secciones
                if (textoEscribir.equals(ConstantesInternet.Lista_Otras_Secciones)){
                    
                    List<AdditionalSectionInfo> listaSeccionesAdicionales = elementoLista.getAdditionalSections(); 
                    for (int w=0; w<listaSeccionesAdicionales.size(); w++){
                        
                        AdditionalSectionInfo additionalSection = listaSeccionesAdicionales.get(w);
                        if (additionalSection != null){
                           JAXBElement<String> nombreSeccionJ = additionalSection.getSectionName();
                           if (nombreSeccionJ!= null){
                               String nombreSeccion = nombreSeccionJ.getValue();
                               //Imprimirlo en la página
                               Phrase titulo = new Phrase(textoEscribir, font);
                               Chunk myChunk = this.construirSeparador(identacion);
                               document.add(new Chunk(Chunk.NEWLINE));
                               document.add(myChunk);
                               document.add(titulo);
                               
                           }
                        }
                        
                    }
                    document.add(new Chunk(Chunk.NEWLINE));
                }
                        
               else if (textoEscribir.equals(ConstantesInternet.Otras_Secciones)){
                    
                    //Imprimir el label Otras secciones y generar salto de linea
                    Phrase titulo = new Phrase(textoEscribir, font);
                    Chunk myChunk = this.construirSeparador(identacion);
                
                    document.add(myChunk);
                    document.add(titulo);
                    document.add(new Chunk(Chunk.NEWLINE));
                    
                }
                
                else{
                //Chunk titulo = new Chunk(textoEscribir, font);
                Phrase titulo = new Phrase(textoEscribir, font);
                Chunk myChunk = this.construirSeparador(identacion);
                
                document.add(myChunk);
                document.add(titulo);
                }
                
            }
            if (esDinamico.booleanValue() ==true){
                document.add(new Chunk(Chunk.NEWLINE));
            }
            
            verticalPosition = writer.getVerticalPosition(false);
            
 
        } 
        document.close();    
     }
     catch(Exception e){
        e.printStackTrace();
     }
     
     tamanoFinal = (int) (alto-verticalPosition+15);
     
     return tamanoFinal;
  }
                                  
    private ByteArrayOutputStream construirArchivoAvisoInternet(InternetInfo elementoLista, Seccion seccion, int altoFinal, int z) {
        
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
     int alto = altoFinal;
     int ancho = seccion.getAncho();
     float verticalPosition=0; 
     
        
     //1. Obtener los componentes de la sección
     //Se deben configurar en este orden en la base de datos. 
     try{
         
        
        Rectangle rect = new Rectangle (ancho, alto);
        Document document = new Document(rect,5,5,5,5);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        writer.setInitialLeading(2);
        Rectangle rct = new Rectangle(ancho, alto);
        writer.setBoxSize("art", rct);
        
        document.open();
        
        for (int r=0; r<seccion.getComponentesSeccion().size();r++){
            
            Componente comp = seccion.getComponentesSeccion().get(r);
            
            //Obtener los atributos.
            Boolean esDinamico = comp.getEsDinamico();
            String atributo = comp.getAtributo();
            String label = comp.getLabel();
            String textoEscribir="";
            String tipoFuente = comp.getTipoFuente();
            int tamanoFuente = comp.getTamano_fuente();
            //El estilo de decoración de la Fuente se coloca en el atributo alto.
            int tipoDecoracion = comp.getAlto();
            int colorFuenteRed = comp.getColor_fuente_red();
            int colorFuenteGreen = comp.getColor_fuente_green();
            int colorFuenteBlue = comp.getColor_fuente_blue(); 
            int identacion = comp.getCoordenada_inicio_x();
            
            
            if (esDinamico.booleanValue() ==true){
              
              //Caso en el que se requieren recuperar las secciones:
              if (atributo.equals(ConstantesInternet.Lista_Otras_Secciones)){
                  textoEscribir=ConstantesInternet.Lista_Otras_Secciones;
              }
               
              else{
              Class miClase = Class.forName("parametros_plan_salesforce.InternetInfo");
              try{
                  Field f = miClase.getDeclaredField(atributo);
                  f.setAccessible(true);
                  JAXBElement<String> textoI = (JAXBElement<String>) f.get(elementoLista);
                  textoEscribir = textoI.getValue();
              }
              catch (Exception e){
                  e.printStackTrace();
              }
              }
            }
            else {
               textoEscribir=label;
            }
            BaseColor baseColor = new BaseColor(colorFuenteRed,colorFuenteGreen,colorFuenteBlue);
            Font font = FontFactory.getFont("fuentes_archivos/"+tipoFuente, tamanoFuente, tipoDecoracion, baseColor);
           
            if (r ==0){
                document.add(new Chunk(Chunk.NEWLINE));
            }
            if (textoEscribir!=null){
                
                
                //Caso Lista de las Secciones
                if (textoEscribir.equals(ConstantesInternet.Lista_Otras_Secciones)){
                    
                    List<AdditionalSectionInfo> listaSeccionesAdicionales = elementoLista.getAdditionalSections(); 
                    for (int w=0; w<listaSeccionesAdicionales.size(); w++){
                        
                        AdditionalSectionInfo additionalSection = listaSeccionesAdicionales.get(w);
                        if (additionalSection!= null){
                           JAXBElement<String> nombreSeccionJ = additionalSection.getSectionName();
                           if (nombreSeccionJ!= null){
                               String nombreSeccion = nombreSeccionJ.getValue();
                               //Imprimirlo en la página
                               Phrase titulo = new Phrase(nombreSeccion, font);
                               Chunk myChunk = this.construirSeparador(identacion);
                               document.add(new Chunk(Chunk.NEWLINE));
                               document.add(myChunk);
                               document.add(titulo);
                               
                           }
                        }
                    }
                    
                }
                        
                else if (textoEscribir.equals(ConstantesInternet.Otras_Secciones)){
                    
                    //Imprimir el label Otras secciones y generar salto de linea
                    Phrase titulo = new Phrase(textoEscribir, font);
                    Chunk myChunk = this.construirSeparador(identacion);
                
                    document.add(myChunk);
                    document.add(titulo);
                    document.add(new Chunk(Chunk.NEWLINE));
                    
                }
                
                else{
                //Chunk titulo = new Chunk(textoEscribir, font);
                Phrase titulo = new Phrase(textoEscribir, font);
                Chunk myChunk = this.construirSeparador(identacion);
                
                document.add(myChunk);
                document.add(titulo);
                }
                
            }
            if (esDinamico.booleanValue() ==true){
                document.add(new Chunk(Chunk.NEWLINE));
            }
 
        } 
        document.close();    
     }
     catch(Exception e){
        e.printStackTrace();
     }
      
      return bos;
    }

    
    public Chunk construirSeparador(int size){
       
        Chunk myChunk = new Chunk(" ");
        myChunk.setCharacterSpacing(size);
        return myChunk;        
        
    }
    
    public List<ByteArrayOutputStream> construirListaPaginasFinal(List<InternetInfo> elementoLista, TipoPagina internetPage,ContractInfo contract, Configuracion configBasica,List<ByteArrayOutputStream>listaArchivosTemporal){
        
        List<ByteArrayOutputStream> listaRetorno = new ArrayList<ByteArrayOutputStream>();
        ByteArrayOutputStream paginaGenerada=null;
        
        int altoSeccionBasica=0;
        int altoSeccionAviso=0;
                
        //Determinar el tamaño de la sección en la que se pintan los datos del aviso.
         for (int i=0; i<internetPage.getSeccionesPagina().size(); i++){
            
              Seccion sec = internetPage.getSeccionesPagina().get(i);
              
                     
              if (sec.getTipo_seccion().equals(ConstantesInternet.informacion_Basica)){
                  altoSeccionBasica = sec.getAlto();
              }
              
              if (sec.getTipo_seccion().equals(ConstantesInternet.informacion_Aviso)){
                  altoSeccionAviso = sec.getAlto();
              }
        }
         
        
        //Ir recorriendo la lista de avisos generados para ver si caben o se requiere
        //generar una nueva página.
        try{
        
        
        for (int k=0; k<listaArchivosTemporal.size();k++){
          
            //Insertar el primer aviso
            float altoTemporal= 0;
            List<ByteArrayOutputStream> listaTemporalPdfs = new ArrayList<ByteArrayOutputStream>();
            List<InternetInfo> listaTemporalDatosBasicos = new ArrayList<InternetInfo>();
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
            
            //Mandara a generar la página y agregarla a la lista de páginas
            paginaGenerada = generarPaginaParcial(listaTemporalPdfs,listaTemporalDatosBasicos,internetPage,contract, configBasica);
            listaRetorno.add(paginaGenerada);
         }
            
            
 
        }
        catch(Exception e){
            e.printStackTrace();
            
        }
        
        
        return listaRetorno;
    }
    
    public ByteArrayOutputStream generarPaginaParcial(List<ByteArrayOutputStream>listaTemporalPdfs,List<InternetInfo> listaTemporalDatosBasicos,TipoPagina internetPage,ContractInfo contract, Configuracion configBasica){
       System.out.println("Entre a generar la pagina parcial internet"); 
       ByteArrayOutputStream paginaGenerada = new ByteArrayOutputStream(); 
       try{
            System.out.println("Entre a generar la pagina parcial internet");
            PdfReader readerPlantilla;
            String rutaPlantilla="plantillas_archivos/"+internetPage.getArchivo_plantilla();
                                
            readerPlantilla = new PdfReader(rutaPlantilla);
                     
            //En esta variable se guarda el tamaño de la Plantilla.
            Rectangle pageSize = new Rectangle(readerPlantilla.getPageSize(1));
            Document documentoFinal = new Document(pageSize);
            PdfWriter writterGeneral = PdfWriter.getInstance(documentoFinal,paginaGenerada);
            documentoFinal.open();
            PdfTemplate plantilla = writterGeneral.getImportedPage(readerPlantilla, 1);
            PdfContentByte content = writterGeneral.getDirectContent();
            content.addTemplate(plantilla, 0,0);
            System.out.println("numero de secciones internet: " +internetPage.getSeccionesPagina().size());
            
            /********************************************************/
            /*       AGREGAR CADA UNA DE LAS SECCIONES              */
            /********************************************************/
            try{
                 for (int i=0; i<internetPage.getSeccionesPagina().size(); i++){
                      
                     Seccion sec = internetPage.getSeccionesPagina().get(i);
                     
                     int nuevoAlto=sec.getCoordenada_inicio_y();
                     //int nuevoAlto=0; 28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.
                     int altoOriginal=0;
                     System.out.println("tipo de seccion internet: " +sec.getTipo_seccion());
                    //Caso en que la sección es de tipo AVISOS_INTERNET
                    if (sec.getTipo_seccion().equals(ConstantesInternet.informacion_Basica)) {
                        System.out.println("entre por aviso internet " );
                        for (int paginas=0; paginas <listaTemporalPdfs.size(); paginas++){
                          if(paginas==0){
                              System.out.println("entre pagina==0 de aviso internet " );
                              //Agregar la Sección de datos básicos
                              altoOriginal=sec.getCoordenada_inicio_y();
                              nuevoAlto = sec.getCoordenada_inicio_y();
                              this.agregarSeccion(content, sec, writterGeneral, contract, listaTemporalPdfs, listaTemporalDatosBasicos,paginas,nuevoAlto); 
                              //nuevoAlto = sec.getCoordenada_inicio_y(); 28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.
                              nuevoAlto -=5;
                              
                              
                              //Determinar el tamaño del primer pdf
                              ByteArrayOutputStream avisoInternet = listaTemporalPdfs.get(paginas);
                              byte[] pdfTemporal2 = null;
                              pdfTemporal2 = avisoInternet.toByteArray();
                              PdfReader readerComponente = new PdfReader(pdfTemporal2);
                              Rectangle tamanioRecibido = readerComponente.getPageSize(1);
                              float alto  = tamanioRecibido.getHeight();
                              nuevoAlto-=alto;
                              System.out.println("******COORDENADA DE INICIO ANTES DE AGREGAR AVISO JA=== "+nuevoAlto );
                              agregarAvisoInternet(content,writterGeneral,sec.getCoordenada_inicio_x(),nuevoAlto,avisoInternet);
                              
                              //****28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.***
                              //nuevoAlto-=sec.getAlto();  
                              //nuevoAlto -=5;
                              //sec.setCoordenada_inicio_y(nuevoAlto);
                              
                              
                              
                          }
                          else{
                               nuevoAlto-=sec.getAlto(); 
                               nuevoAlto -=5;
                               //Agregar la Sección de datos básicos
                               this.agregarSeccion(content, sec, writterGeneral, contract, listaTemporalPdfs, listaTemporalDatosBasicos,paginas,nuevoAlto); 
                              //****28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.***
                              //nuevoAlto-=sec.getAlto(); 
                              //nuevoAlto = sec.getCoordenada_inicio_y();
                              //nuevoAlto -=5;
                              
                              //Determinar el tamaño del siguiente pdf
                              ByteArrayOutputStream avisoInternet = listaTemporalPdfs.get(paginas);
                              byte[] pdfTemporal2 = null;
                              pdfTemporal2 = avisoInternet.toByteArray();
                              PdfReader readerComponente = new PdfReader(pdfTemporal2);
                              Rectangle tamanioRecibido = readerComponente.getPageSize(1);
                              float alto  = tamanioRecibido.getHeight();
                              nuevoAlto-=alto;
                              agregarAvisoInternet(content,writterGeneral,sec.getCoordenada_inicio_x(),nuevoAlto,avisoInternet);
                              //****28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.***
                              //nuevoAlto-=sec.getAlto(); 
                              //nuevoAlto -=5;
                              //sec.setCoordenada_inicio_y(nuevoAlto);
                              
                          }
                       }
                  
                      // sec.setCoordenada_inicio_y(altoOriginal); ****28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.***

                    }
                    
                    else if (sec.getTipo_seccion().equals(ConstantesInternet.informacion_Aviso)) {
                    }
            
                    else{
                       // JAAR agrega el alto al metodo ****28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.***
                       this.agregarSeccion(content, sec, writterGeneral, contract, listaTemporalPdfs, listaTemporalDatosBasicos,0,nuevoAlto);
                    
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
     // JAAR recibe la variable nuevoAlto ****28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.***
     public void agregarSeccion (PdfContentByte content, Seccion sec, PdfWriter writterGeneral, ContractInfo contract, List<ByteArrayOutputStream>listaTemporalPdfs, List<InternetInfo> listaTemporalDatosBasicos, int pagina,int nuevoAlto) throws Exception{
        
        try{
            int coordenada_inicio_x = sec.getCoordenada_inicio_x();
            //int coordenada_inicio_y = sec.getCoordenada_inicio_y(); ****28-05-2013 Cambio realizado para evitar que se corra la coordenada de inicio donde se pinta la seccion.***
            int coordenada_inicio_y = nuevoAlto;
            // JAAR hace igual coord inicio a nuevo alto
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
            
            //Determinar el componente
            InternetInfo advertise = listaTemporalDatosBasicos.get(pagina);
        
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
     
     
    
    public void agregarComponente(PdfContentByte content, Componente comp, PdfWriter writterGeneral, int x_inicial, int y_inicial, String tipoSeccion, InternetInfo advertise, ContractInfo contract)throws Exception{
        
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
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.avisosInternet)){
                  Class miClase = Class.forName("parametros_plan_salesforce.InternetInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(advertise);
                      textoEscribir = textoI.getValue();
                       System.out.println("atributo agregarComponente "+atributo);
                      System.out.println("textoEscribir agregarComponente "+textoEscribir);
                     
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
                  
                  System.out.println("FECHA INTERNET ES IGUAL A = "+date_format.format(resultdate).toString());
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
                  content.addTemplate(plantilla, coordenada_inicial_x,coordenada_inicial_y);
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
    
    public void agregarAvisoInternet(PdfContentByte content, PdfWriter writterGeneral, int coordx, int coordy, ByteArrayOutputStream avisoInternet){
        
        try{
            PdfReader readerRecibido;
        System.out.println(" &&&&&&&==COORDENADA_ENVIADA_agregarAvisoInternet coordy=="+coordy +"coordx==>>" +coordx);   
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
}
