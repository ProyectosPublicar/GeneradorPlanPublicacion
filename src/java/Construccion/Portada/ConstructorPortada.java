/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Construccion.Portada;

import Configuracion.modelo.Componente;
import Configuracion.modelo.ConstantesFacturacion;
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
import parametros_plan_salesforce.AgentInfo;
import parametros_plan_salesforce.BillingInfo;
import parametros_plan_salesforce.ClientInfo;
import parametros_plan_salesforce.ContractInfo;
import parametros_plan_salesforce.QuoteInfo;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Administrador
 */
public class ConstructorPortada {

    public ByteArrayOutputStream construirPortada(ClientInfo clientInfo, ContractInfo contract, AgentInfo agent, String templateCode, TipoPagina portada, String mostrar_valor)
            throws Exception {
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°ENTRO A CONTRUIR PORTADA°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("ClientInfo1->" + clientInfo.getAddress().getValue());
        System.out.println("ClientInfo2->" + clientInfo.getAuthorizer1ID().getValue());
        System.out.println("ClientInfo3->" + clientInfo.getAuthorizer1Name().getValue());
        System.out.println("ClientInfo4->" + clientInfo.getAuthorizer1Phone().getValue());
        System.out.println("ClientInfo5->" + clientInfo.getAuthorizer2ID().getValue());
        System.out.println("ClientInfo6->" + clientInfo.getAuthorizer2Name().getValue());
        System.out.println("ClientInfo7->" + clientInfo.getAuthorizer2Phone().getValue());
        System.out.println("ClientInfo8->" + clientInfo.getCity().getValue());
        System.out.println("ClientInfo9->" + clientInfo.getEmail().getValue());
        System.out.println("ClientInfo10->" + clientInfo.getFacturatoradress().getValue());
        System.out.println("ClientInfo11->" + clientInfo.getFacturatorphone().getValue());
        System.out.println("ClientInfo12->" + clientInfo.getFax().getValue());
        System.out.println("ClientInfo13->" + clientInfo.getIdNumber().getValue());
        System.out.println("ClientInfo14->" + clientInfo.getIdAccount().getValue());
        System.out.println("ClientInfo15->" + clientInfo.getIdType().getValue());
        System.out.println("ClientInfo16->" + clientInfo.getLegalRepID().getValue());
        System.out.println("ClientInfo17->" + clientInfo.getLegalRepName().getValue());
        System.out.println("ClientInfo18->" + clientInfo.getWeb().getValue());
        System.out.println("ClientInfo19->" + clientInfo.getName().getValue());
        System.out.println("ClientInfo20->" + clientInfo.getPartyId().getValue());
        System.out.println("ClientInfo21->" + clientInfo.getPhone().getValue());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader readerPlantilla;
        String rutaPlantilla = "plantillas_archivos/" + portada.getArchivo_plantilla();
        //Cargar la Plantilla
        try {
            readerPlantilla = new PdfReader(rutaPlantilla);
        } catch (Exception ex) {
            throw ex;
        }

        //En esta variable se guarda el tamaño de la Plantilla.
        Rectangle pageSize = new Rectangle(readerPlantilla.getPageSize(1));
        Document documentoFinal = new Document(pageSize);
        PdfWriter writterGeneral = PdfWriter.getInstance(documentoFinal, bos);
        documentoFinal.open();
        PdfTemplate plantilla = writterGeneral.getImportedPage(readerPlantilla, 1);
        PdfContentByte content = writterGeneral.getDirectContent();
        content.addTemplate(plantilla, 0, 0);

        /**
         * *****************************************************
         */
        /*       AGREGAR CADA UNA DE LAS SECCIONES              */
        /**
         * *****************************************************
         */
        try {
            for (int i = 0; i < portada.getSeccionesPagina().size(); i++) {

                Seccion sec = portada.getSeccionesPagina().get(i);
                //System.out.println("SECCIONES PORTADA+++++= "+sec.getNombre());

                this.agregarSeccion(content, sec, writterGeneral, clientInfo, contract, agent, mostrar_valor);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        documentoFinal.close();
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°FIN DE CONTRUIR PORTADA°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        return bos;
    }

    public void agregarSeccion(PdfContentByte content, Seccion sec, PdfWriter writterGeneral, ClientInfo clientInfo, ContractInfo contract, AgentInfo agent, String mostrar_valor) throws Exception {
        System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A AGREGAR SECCION°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°Nombre Seccion->" + sec.getNombre() + "°°°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°Tipo Seccion->" + sec.getTipo_seccion() + "°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        try {
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
            if (blue >= 0 && red >= 0 && green >= 0) {
                recSeccion.setBackgroundColor(new BaseColor(red, green, blue));
            }
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
            documento.open();
            PdfContentByte cb = writter.getDirectContent();
            BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
            cb.setFontAndSize(bf1, 20);
            cb.showText(" ");
            documento.close();
            byte[] tempPDF = pdfOutputStream.toByteArray();
            PdfReader readerComponente = new PdfReader(tempPDF);
            PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
            content.addTemplate(plantilla, coordenada_inicio_x, coordenada_inicio_y);

            for (int j = 0; j < sec.getComponentesSeccion().size(); j++) {

                Componente componente = sec.getComponentesSeccion().get(j);

                //System.out.println("TIPO SECCION+++++= "+tipoSeccion);
                System.out.println("componente+++++= " + componente + "clientInfo" + clientInfo);
                agregarComponente(content, componente, writterGeneral, clientInfo, contract, agent, coordenada_inicio_x, coordenada_inicio_y, tipoSeccion, mostrar_valor, alto);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°°°°°°°°°°FIN DE AGREGAR SECCION°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
    }

    public void agregarComponente(PdfContentByte content, Componente comp, PdfWriter writterGeneral, ClientInfo clientInfo, ContractInfo contract, AgentInfo agent, int x_inicial, int y_inicial, String tipoSeccion, String mostrar_valor, int altoSeccion) throws Exception {
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°ENTRO A AGREGAR COMPONENTE°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°Comp Codigo->" + comp.getCodigo() + "°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°Comp Nombre->" + comp.getNombre() + "°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°Comp Label ->" + comp.getLabel() + "°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°Comp Atrib ->" + comp.getAtributo() + "°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°Comp Obj   ->" + comp.getObjeto() + "°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°Comp Desc  ->" + comp.getDescripcion() + "°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        try {

            //Obtener los atributos del Componente
            String datosBarras = "";
            int coordenada_inicial_x = comp.getCoordenada_inicio_x() + x_inicial;
            int coordenada_inicial_y = comp.getCoordenada_inicio_y() + y_inicial;
            int alto = comp.getAlto();
            int ancho = comp.getAncho();
            String atributo = comp.getAtributo();
            String label = comp.getLabel();
            System.out.println("\t\t°°°°°°°°°°°°°°°°°°°Componente Atributo-> " + atributo + "°°°°°°°°°°°°°°°°°°°°°°");
            System.out.println("\t\t°°°°°°°°°°°°°°°°°°°Componente Label   -> " + label + "°°°°°°°°°°°°°°°°°°°°°°");
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
            String tipoFacturacion = "";

            JAXBElement<String> tipoFacturacionJ = contract.getTypeBilling();
            tipoFacturacion = tipoFacturacionJ.getValue();
            System.err.println("tipo de facturacion: " + tipoFacturacion);

            //Caso Texto Dinámico
            if (esDinamico.booleanValue() == true) {

                String nombreClase = "";

                //Sección de Clientes
                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosCliente)) {
                    Class miClase = Class.forName("parametros_plan_salesforce.ClientInfo");
                    try {
                        Field f = miClase.getDeclaredField(atributo);
                        f.setAccessible(true);
                        JAXBElement<String> textoI = (JAXBElement<String>) f.get(clientInfo);
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t----------------->" + clientInfo.getIdAccount().getValue());
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Label->" + label + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Atribu->" + atributo + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Comp->" + textoI.getValue() + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
                        textoEscribir = textoI.getValue();
                    } catch (Exception e) {
                        throw e;
                    }
                }

                //Sección datos del Contrato
                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosContrato)) {
                    Class miClase = Class.forName("parametros_plan_salesforce.ContractInfo");
                    try {
                        Field f = miClase.getDeclaredField(atributo);
                        f.setAccessible(true);
                        JAXBElement<String> textoI = (JAXBElement<String>) f.get(contract);
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t----------------->" + contract.getConsultorID().getValue());
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Label->" + label + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Atribu->" + atributo + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Comp->" + textoI.getValue() + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
                        textoEscribir = textoI.getValue();
                    } catch (Exception e) {
                        throw e;

                    }
                }
                
                //Sección datos del Agente 
              if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosAgente)){
                  Class miClase = Class.forName("parametros_plan_salesforce.AgentInfo");
                  try{
                      Field f = miClase.getDeclaredField(atributo);
                      f.setAccessible(true);
                      /**
                       * 
                       */
                      JAXBElement<String> aid = null;
                      JAXBElement<String> anm = null;
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°DATOS SALESFORCE°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°AgenteId-> " + agent.getAgentID() + "°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°AgenteNm-> " + agent.getAgentName()+ "°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      
                      aid.setValue("1019086916");
                      anm.setValue("JONATHAN PACHON");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°DATOS QUEMADOS°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°AgenteId-> " + aid.getValue() + "°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°AgenteNm-> " + anm.getValue()+ "°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      
                      agent.setAgentID(aid);                      
                      agent.setAgentName(anm);
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°DATOS NUEVOS°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°AgenteId-> " + aid.getValue() + "°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°AgenteNm-> " + anm.getValue()+ "°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      
                      JAXBElement<String> textoI = (JAXBElement<String>) f.get(agent);
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°TEXTO I°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°TextoI-> " + textoI.getValue() + "°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                      
                      textoEscribir = textoI.getValue();
                  }
                  catch (Exception e){
                      throw e;
                      
                  }
              }
//                //Sección datos del Agente (Jonathan Pachon)
//                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosAgente)) {
//                    Class miClase = Class.forName("parametros_plan_salesforce.AgentInfo");
//                    try {
//                        Field f = miClase.getDeclaredField(atributo);
//                        f.setAccessible(true);
//                        AgentInfo pruebaAgent = new AgentInfo();
////                      String ids = "1019086916";
////                      JAXBElement<String> id = (JAXBElement<String>)ids;
//                        JAXBElement<String> agentId = null;
//                        JAXBElement<String> agentName = null;
//                        agentId.setValue("1019086916");
//                        agentName.setValue("JONATHAN PACHON");
//                        pruebaAgent.setAgentID(agentId);
//                        pruebaAgent.setAgentName(agentName);
////                        JAXBElement<String> textoI = (JAXBElement<String>) f.get(agent);
//                        System.out.println("°");
//                        System.out.println("°°");
//                        System.out.println("°°°");
//                        System.out.println("°°°°");
//                        System.out.println("°°°°°");
//                        System.out.println("°°°°°°");
//                        System.out.println("°°°°°°°");
//                        System.out.println("°°°°°°°°");
//                        System.out.println("°°°°°°°°°");
//                        System.out.println("Agente->" + agentId.getValue());
//                        System.out.println("Agente->" + agentName.getValue());
//                        JAXBElement<String> textoI = (JAXBElement<String>) f.get(pruebaAgent);
//                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
//                        System.out.println("\t\t\t----------------->" + pruebaAgent.getAgentID().getValue());
//                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
//                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
//                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Label->" + label + "°°°°°°");
//                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Atribu->" + atributo + "°°°°°°");
//                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Comp->" + textoI.getValue() + "°°°°°°");
//                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
//                        System.out.println("2||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
//                        System.out.println("Id Agente" + pruebaAgent.getAgentID().getValue());
//                        System.out.println("Nombre Agente" + pruebaAgent.getAgentName().getValue());
//                        System.out.println("TextoI->" + textoI.getValue());
//                        System.out.println("2||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
//
//                        textoEscribir = textoI.getValue();
//                    } catch (Exception e) {
//                        throw e;
//
//                    }
//                }

                //Sección Facturación
                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.facturacion)) {
                    Class miClase = Class.forName("parametros_plan_salesforce.ContractInfo");
                    try {
                        Field f = miClase.getDeclaredField(atributo);
                        f.setAccessible(true);
                        JAXBElement<String> textoI = (JAXBElement<String>) f.get(contract);
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t----------------->" + contract.getConsultorID().getValue());
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Label->" + label + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Atribu->" + atributo + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°Valor Comp->" + textoI.getValue() + "°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°\n");
                        textoEscribir = textoI.getValue();

                    } catch (Exception e) {
                        throw e;

                    }
                }

                //Sección Código de BarrasCODIGO_BARRAS
                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.codigoBarras)) {
                    System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                    System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°ENTRO A CREACION COD_BARRAS°°°°°°°°°°°°°°°°°°°");
                    System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                    try {
                        JAXBElement<String> cadenaCodigoI = contract.getContractBarCode();
                        String cadenaCodigo = cadenaCodigoI.getValue();

                        AxentriaDigitizingService_Service barras_service = new AxentriaDigitizingService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/DigitizingServiceNuevo.wsdl"));
                        AxentriaDigitizingService digitazing_service = barras_service.getBasicHttpBindingAxentriaDigitizingService();
                        nuevo_codigo_barras.BarCode dataPdfBarCode = digitazing_service.getDataPngCode128(cadenaCodigo);

                        JAXBElement<String> dataBarras = dataPdfBarCode.getBarCodeString();
                        datosBarras = dataBarras.getValue();
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°SALIÓ DE CREACION COD_BARRAS°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

                    } catch (Exception e) {
                        System.out.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t\t°°°°°°°°°°°°°°°ENTRO AL CATCH DE CREACION COD_BARRAS°°°°°°°°°°°°°");
                        System.out.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.err.println("[ERROR PLAN PUBLICACION] [CODIGO DE BARRAS]\n");
                        System.err.println("Error obteniendo servicios web para la generación del código de barras.\n");
                        System.err.println("Traza de la Excepción: " + e);
                        System.out.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("\t\t\t\t°°°°°°°°°°°°°°°SALIO DEL CATCH DE CREACION COD_BARRAS°°°°°°°°°°°°°");
                        System.out.println("\t\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        
                    }
                }

                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.Fecha_de_impresion)) {
                    Date hoy = new Date();

                    SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date resultdate = new Date();
                    System.out.println(date_format.format(resultdate));

                    System.out.println("FECHA PORTADA ES IGUAL A = " + date_format.format(resultdate).toString());
                    textoEscribir = date_format.format(resultdate).toString();

                }

            } //Caso Label
            else {
                textoEscribir = label;

                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.cuotas_pago)) {

                    //Determinar el tipo de facturación y el atributo mostrar valores:
                    List<BillingInfo> listaCuotas = contract.getBillList();
                    tipoFacturacionJ = contract.getTypeBilling();
                    tipoFacturacion = tipoFacturacionJ.getValue();

                    //1. CASO NO MOSTRAR VALOR
                    if (mostrar_valor.equalsIgnoreCase("0")) {

                        if (tipoFacturacion.equals(ConstantesFacturacion.facturacion_independiente)) {

                            if (comp.getAtributo().equals(ConstantesFacturacion.texto_cobro_impuesto)) {
                                textoEscribir = "";
                            }
                            if (comp.getAtributo().equals(ConstantesFacturacion.asterisco_iva)) {
                                textoEscribir = "";
                            }

                            if (comp.getNombre().equalsIgnoreCase(ConstantesFacturacion.texto_telefono_cuotas)) {
                                textoEscribir = "";
                            }
                        }

                        if (comp.getAtributo().equals(ConstantesFacturacion.titulo_tabla_cuotas)) {
                            textoEscribir = "";
                        }

                    } //2. CASO MOSTRAR VALOR
                    else {

                        //2.1 FACTURACION DEPENDIENTE
                        if (tipoFacturacion.equals(ConstantesFacturacion.facturacion_independiente)) {

                            if (comp.getAtributo().equals(ConstantesFacturacion.texto_cobro_impuesto)) {
                                textoEscribir = "";
                            }
                            if (comp.getAtributo().equals(ConstantesFacturacion.asterisco_iva)) {
                                textoEscribir = "";
                            }

                        }

                        //No mostrar el texto de Forma de Pago
                        if (comp.getAtributo().equals(ConstantesFacturacion.texto_forma_pago)) {
                            textoEscribir = "";
                        }
                        if (comp.getAtributo().equals(ConstantesFacturacion.texto_cobro_cuotas)) {
                            textoEscribir = "";
                        }

                    }

                }

            }

            /**
             * *******************************************************
             */
            /*          CASO CODIGO DE BARRAS                         */
            /**
             * *******************************************************
             */
            if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.codigoBarras)) {
                Rectangle recComponente = new Rectangle(ancho, alto);
                Document documento = new Document(recComponente);

                if (datosBarras != null && !(datosBarras.equals(""))) {

                    byte[] tempPDF = Base64.decodeBase64(datosBarras);
                    PdfReader readerComponente = new PdfReader(tempPDF);
                    PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
                    //content.addTemplate(plantilla, coordenada_inicial_x,coordenada_inicial_y);
                    float escalarY = (float) 0.8;
                    float escalarX = (float) 1.0;
                    content.addTemplate(plantilla, escalarX, 0, 0, escalarY, coordenada_inicial_x, coordenada_inicial_y);
                }

            } //Si la sección es de paginación no pintar los componentes.
            else if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.paginacion)) {

            } /**
             * *******************************************************
             */
            /*            CASO CUOTAS DE PAGO                         */ /**
             * *******************************************************
             */
            else if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.listas_cuotas_pago) && mostrar_valor.equalsIgnoreCase("1")) {

                //Mirar cuántas cuotas caben en la sección:
                int numeroCuotas = 0;
                double numeroPosiblesCuotas = altoSeccion / alto;
                numeroPosiblesCuotas = Math.floor(numeroPosiblesCuotas);

                //Mirar cuantas cuotas trae el contrato.
                List<BillingInfo> billList = contract.getBillList();
                if (billList != null && !(billList.isEmpty())) {

                    for (int cuotas = 0; cuotas < billList.size(); cuotas++) {

                        BillingInfo primeraLista = billList.get(cuotas);
                        List<QuoteInfo> listaCuotas = primeraLista.getQuoteList();
                        if (listaCuotas != null && !(listaCuotas.isEmpty())) {
                            numeroCuotas += listaCuotas.size();
                            QuoteInfo quote = listaCuotas.get(0);
                            quote.getDateBilling();
                        }
                        if (cuotas > 0) {
                            numeroCuotas += 2;
                        }

                    }
                }
                //RP cuando hay mas de un dato de facturacion en la portada solo se muestra el primero  

                if (billList.size() > 1) {
                    numeroCuotas = 13;
                }

                if (numeroCuotas <= numeroPosiblesCuotas) {

                    //Pintar que si caben en la primera página
                    int numeroCuotasPintadas = 0;
                    float coordenadaPintarY = 0;

                    //Pintar el elemento en la lista de cuotas de pago.
                    for (int ii = 0; ii < billList.size(); ii++) {

                        BillingInfo primeraLista = billList.get(ii);
                        List<QuoteInfo> listaCuotas = primeraLista.getQuoteList();

                        for (int jj = 0; jj < listaCuotas.size(); jj++) {

                            QuoteInfo miCuota = listaCuotas.get(jj);
                            numeroCuotasPintadas++;
                            coordenadaPintarY = coordenada_inicial_y - (numeroCuotasPintadas * alto);
                            //Determinar el Texto a Escribir
                            Class miClase = Class.forName("parametros_plan_salesforce.QuoteInfo");
                            try {
                                System.out.println("ANTES QuoteInfoJ+++++= " + atributo);
                                Field f = miClase.getDeclaredField(atributo);
                                System.out.println("ATRIBUTO QuoteInfoJ+++++= " + atributo + "cuota==>>" + jj);
                                f.setAccessible(true);
                                JAXBElement<String> textoI = (JAXBElement<String>) f.get(miCuota);

                                //JAAR Se agrega el valor del Nie a plan de publicacion
                                if (atributo.equalsIgnoreCase("nie") && jj != 0) {
                                    System.out.println("ENTRO QuoteInfo NO ESCRIBIR+++++= " + atributo + "cuota" + jj + f);
                                    textoEscribir = "";
                                } else {
                                    textoEscribir = textoI.getValue();
                                }

                                if ((comp.getNombre().equalsIgnoreCase(ConstantesFacturacion.valor_telefono_cuotas)) && (mostrar_valor.equalsIgnoreCase("1")) && (tipoFacturacion.equals(ConstantesFacturacion.facturacion_independiente))) {
                                    textoEscribir = "";

                                }
                            } catch (Exception e) {
                                throw e;
                            }

                            //Crear un objeto para el componente
                            Rectangle recComponente = new Rectangle(ancho, alto);
                            Document documento = new Document(recComponente);
                            //Si el color es mayor que cero se usa como background. Si se requiere transparente colocar -1 en la base de datos.
                            if (colorFondoRed >= 0 && colorFondoGreen >= 0 && colorFondoBlue >= 0) {
                                recComponente.setBackgroundColor(new BaseColor(colorFondoRed, colorFondoGreen, colorFondoBlue));
                            }
                            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
                            documento.open();
                            PdfContentByte cb = writter.getDirectContent();
                            cb.beginText();
                            BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
                            if (tipoFuente != null) {
                                bf1 = BaseFont.createFont("fuentes_archivos/" + tipoFuente, BaseFont.CP1252, BaseFont.EMBEDDED);
                            }
                            cb.setRGBColorStroke(colorFuenteRed, colorFuenteGreen, colorFuenteBlue);
                            cb.setColorFill(new BaseColor(colorFuenteRed, colorFuenteGreen, colorFuenteBlue));
                            cb.setFontAndSize(bf1, tamanoFuente);
                            cb.moveText(coordenada_escribir_x, coordenada_escribir_y);
                            cb.showText(textoEscribir);
                            cb.endText();
                            documento.close();
                            byte[] tempPDF = pdfOutputStream.toByteArray();
                            PdfReader readerComponente = new PdfReader(tempPDF);
                            PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
                            content.addTemplate(plantilla, coordenada_inicial_x, coordenadaPintarY);
                        }
                    }
                } else {

                    //Pintar las que quepan.
                    int numeroCuotasPintadas = 0;
                    float coordenadaPintarY = 0;

                    if (billList.size() > 0) {

                        BillingInfo primeraLista = billList.get(0);
                        List<QuoteInfo> listaCuotas = primeraLista.getQuoteList();

                        for (int jj = 0; jj < listaCuotas.size(); jj++) {

                            QuoteInfo miCuota = listaCuotas.get(jj);
                            numeroCuotasPintadas++;
                            coordenadaPintarY = coordenada_inicial_y - (numeroCuotasPintadas * alto);
                            //Determinar el Texto a Escribir
                            Class miClase = Class.forName("parametros_plan_salesforce.QuoteInfo");
                            try {

                                Field f = miClase.getDeclaredField(atributo);
                                System.out.println("ATRIBUTO__2 QuoteInfo+++++= " + atributo + "cuota" + jj + "f" + f);
                                f.setAccessible(true);
                                JAXBElement<String> textoI = (JAXBElement<String>) f.get(miCuota);

                                //JAAR Se agrega el valor del Nie a plan de publicacion
                                if (atributo.equalsIgnoreCase("nie") && jj != 0) {
                                    System.out.println("ENTRO QuoteInfo NO ESCRIBIR+++++= " + atributo + "cuota" + jj + f);
                                    textoEscribir = "";
                                } else {
                                    textoEscribir = textoI.getValue();
                                }

                                if ((comp.getNombre().equalsIgnoreCase(ConstantesFacturacion.valor_telefono_cuotas)) && (mostrar_valor.equalsIgnoreCase("1")) && (tipoFacturacion.equals(ConstantesFacturacion.facturacion_independiente))) {
                                    textoEscribir = "";

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw e;

                            }

                            //Crear un objeto para el componente
                            Rectangle recComponente = new Rectangle(ancho, alto);
                            Document documento = new Document(recComponente);
                            //Si el color es mayor que cero se usa como background. Si se requiere transparente colocar -1 en la base de datos.
                            if (colorFondoRed >= 0 && colorFondoGreen >= 0 && colorFondoBlue >= 0) {
                                recComponente.setBackgroundColor(new BaseColor(colorFondoRed, colorFondoGreen, colorFondoBlue));
                            }
                            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
                            documento.open();
                            PdfContentByte cb = writter.getDirectContent();
                            cb.beginText();
                            BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
                            if (tipoFuente != null) {
                                bf1 = BaseFont.createFont("fuentes_archivos/" + tipoFuente, BaseFont.CP1252, BaseFont.EMBEDDED);
                            }
                            cb.setRGBColorStroke(colorFuenteRed, colorFuenteGreen, colorFuenteBlue);
                            cb.setColorFill(new BaseColor(colorFuenteRed, colorFuenteGreen, colorFuenteBlue));
                            cb.setFontAndSize(bf1, tamanoFuente);
                            cb.moveText(coordenada_escribir_x, coordenada_escribir_y);
                            cb.showText(textoEscribir);
                            cb.endText();
                            documento.close();
                            byte[] tempPDF = pdfOutputStream.toByteArray();
                            PdfReader readerComponente = new PdfReader(tempPDF);
                            PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
                            content.addTemplate(plantilla, coordenada_inicial_x, coordenadaPintarY);

                            if (numeroCuotasPintadas >= numeroPosiblesCuotas) {
                                jj = listaCuotas.size() + 1;
                            }
                        }
                        coordenada_inicial_y -= 2 * alto;

                    }

                }
            } else {
                //Crear un objeto para el componente
                Rectangle recComponente = new Rectangle(ancho, alto);
                Document documento = new Document(recComponente);
                //Si el color es mayor que cero se usa como background. Si se requiere transparente colocar -1 en la base de datos.
                if (colorFondoRed >= 0 && colorFondoGreen >= 0 && colorFondoBlue >= 0) {
                    recComponente.setBackgroundColor(new BaseColor(colorFondoRed, colorFondoGreen, colorFondoBlue));
                }
                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
                documento.open();
                PdfContentByte cb = writter.getDirectContent();
                cb.beginText();
                BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
                if (tipoFuente != null) {
                    bf1 = BaseFont.createFont("fuentes_archivos/" + tipoFuente, BaseFont.CP1252, BaseFont.EMBEDDED);
                }
                cb.setRGBColorStroke(colorFuenteRed, colorFuenteGreen, colorFuenteBlue);
                cb.setColorFill(new BaseColor(colorFuenteRed, colorFuenteGreen, colorFuenteBlue));
                cb.setFontAndSize(bf1, tamanoFuente);
                cb.moveText(coordenada_escribir_x, coordenada_escribir_y);
                cb.showText(textoEscribir);
                cb.endText();
                documento.close();
                byte[] tempPDF = pdfOutputStream.toByteArray();
                PdfReader readerComponente = new PdfReader(tempPDF);
                PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);
                content.addTemplate(plantilla, coordenada_inicial_x, coordenada_inicial_y);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°FIN DE AGREGAR COMPONENTE°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
    }

}
