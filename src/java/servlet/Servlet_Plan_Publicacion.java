/* Modificacion GitHub - Modificacion Neatbeans
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Version 1.1 enviada a produccion 09/08/2012 - Joe Ayala - Ricardo Parra
 * cambion ConstructorGenesis linea 499,ConstructorDiagramacion linea 250 IDSOL PROPUESTA no se pinta si es el mismo de ID COMPRA
 * Version 1.2 enviada a produccion 27/08/2012- Joe Ayala - Ricardo Parra
 * Version 1.3 version entregada a pruebas 02_09_2012
 * cambion ConstructorGenesis linea 639 (Manejo de fondo azul y recuadro sobre el  aviso)
 * Version 1.4 cambio linea 858 de ConstructorDiagramacion id Sol prob no se pintya si es igual al id de compra
 * Version 1.5 Se realizo la configuración de color parametrizable desde base de datos y por producto. Se agrego una estructura nueva llamada Background_Structure, los cambios fueron en las siguientes líneas
 •	ListaConfiguraciones  línea 19 y línea 37
 •	CargarConfiguraciones  se agregaron los métodos 
 o	Cargarbackground línea  57
 o	obtenerBackground línea 149
 •	ConstructorGenesis 
 o	línea 49 linea 50, 
 o	modifico método construirGenesis se agrego nuevo parámetro List<Background_Structure> BackgroundColor,
 o	método construirPaginaGenesis línea 271-288
 o	método agregarComponente línea 683-699; 705
 •	Servlet_Plan_Publicacion línea 104-109, línea 261
 * Version 1.6 se corrigio el error que en algunos casos las listas no iniciaban en la coordenada correcta en la linea 1128 del ConstructorDiagramador sec.setCoordenada_inicio_y(altoConfiguracionInicial);+
 * version 1.7 se coloco fechas en la Portada en las cuotas en genesis
 * version 1.8 se agrego una plantilla nueva plantillas_archivos/aviso_no_contenido_disponible.pdf el cual se va a mostrar cuando  los avisos de texto no temnga datos completos
 * Version 1.9 se corrigio el error para referencias de internet ya que algunas veces no iniciaban en la coordenada correcta en la linea 1128 del ConstructorInternet sec.setCoordenada_inicio_y(altoConfiguracionInicial);+
 Version 2.0 Joe Ayala Se Carga el nuevo servicio de diagramming version 1.7, se agrega la logica para visualizacion de TAGS              
 Version 3.0 Joe Ayala 20-11-2013 Se adcionan los campos Dirección de facturación y  Telefono de facturacion            

 */
package servlet;

import Configuracion.clases.ConfiguracionBasica;
import Configuracion.clases.ConfiguracionCuotas;
import Configuracion.clases.ConfiguracionDiagramacion;
import Configuracion.clases.ConfiguracionGenesis;
import Configuracion.clases.ConfiguracionInternet;
import Configuracion.clases.ConfiguracionPortada;
import Configuracion.clases.ConfiguracionReglamentos;
import Configuracion.clases.ListaConfiguraciones;
import Configuracion.modelo.Configuracion;
import Configuracion.modelo.TipoPagina;
import Construccion.Diagramacion.ConstructorDiagramacion;
import Construccion.Ensamblador.EnsambladorPaginas;
import Construccion.Genesis.ConstructorGenesis;
import Construccion.Internet.ConstructorInternet;
import Construccion.Portada.ConstructorPortada;
import Construccion.Reglamentos.ConstructorReglamentos;
import Construccion.cuotas.ConstructorCuotas;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import parametros_plan_salesforce.AdvertiseInfo;
import parametros_plan_salesforce.AgentInfo;
import parametros_plan_salesforce.ClientInfo;
import parametros_plan_salesforce.ContractInfo;
import parametros_plan_salesforce.InternetInfo;
import parametros_plan_salesforce.ListInfo;
import parametros_plan_salesforce.ProductInfo;
import parametros_plan_salesforce.ProductionInfo;
import parametros_plan_salesforce.Response;
import parametros_plan_salesforce.SessionHeader;
import parametros_plan_salesforce.WSDesignBridgePortType;
import parametros_plan_salesforce.WSDesignBridgeService;
import Configuracion.modelo.Background_Structure;
import com.sun.codemodel.JOp;
import static java.util.Collections.list;
import parametros_plan_salesforce.AdditionalLineInfo;
import parametros_plan_salesforce.AssemblageInfo;

/**
 * @author dlopez Servlet que se encarga del proceso de validación y de
 * orquestar la construcción del documento de plan de publicación a través de la
 * integración con los diferentes servicios Web expuestos por SalesForce y por
 * Publicar de forma dinámica.
 */
@WebServlet(name = "Servlet_Plan_Publicacion", urlPatterns = {"/Servlet_Plan_Publicacion"})
public class Servlet_Plan_Publicacion extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        int generacion_correcta = 1;
        List<Configuracion> listaConfiguraciones = null;
        ListaConfiguraciones listaConf = null;
        ByteArrayOutputStream paginaPortada = null;
        ByteArrayOutputStream documentoFinalSinPaginar = null;
        List<ByteArrayOutputStream> listaPaginasGeneradasCiclo = null;
        List<ByteArrayOutputStream> listaPaginasGeneradasCicloFinal = null;
        List<ByteArrayOutputStream> listaAvisosProduccion = new ArrayList<ByteArrayOutputStream>();
        List<ByteArrayOutputStream> listaAvisosInternetTemporal = new ArrayList<ByteArrayOutputStream>();
        List<ByteArrayOutputStream> listaAvisosInternetFinal = new ArrayList<ByteArrayOutputStream>();
        List<ByteArrayOutputStream> listaAvisosDiagramacionTemporal = new ArrayList<ByteArrayOutputStream>();
        List<ByteArrayOutputStream> listaAvisosDiagramacionFinal = new ArrayList<ByteArrayOutputStream>();
        List<ByteArrayOutputStream> listaCuotasFinal = new ArrayList<ByteArrayOutputStream>();
        ByteArrayOutputStream paginaReglamento = null;
        List<Float> BackgroundColor = new ArrayList();
        List<Background_Structure> background = new ArrayList<Background_Structure>();

        try {

            try {
                /**
                 * Carga la configuracion inicial que se encuentra en la base de
                 * datos, tales como Lista Config Pag Config Secc Config Compo
                 * Config
                 */
                listaConf = ListaConfiguraciones.getInstance();
//              //Guarda la lista de configuraciones
                listaConfiguraciones = listaConf.getListaDeConfiguraciones();
                //Guarda los tipos de background para los fondos
                background = listaConf.getbackground();
                for (int i = 0; i < background.size(); i++) {
                    System.out.println("background color1 ++++++++= " + background.get(i).getBackground_Color());
                    System.out.println("background Name_Part_Product ++++++++= " + background.get(i).getName_Part_Product());
                    System.out.println("background Code_Part_Product ++++++++= " + background.get(i).getCode_Part_Product());
                }
                //ImprimirConfiguracion imprimir = new ImprimirConfiguracion();
                //imprimir.imprimirConfiguracion(listaConfiguraciones);
            } catch (Exception e) {
                generacion_correcta = -1;
                PrintWriter out = response.getWriter();

                MensajesErrorPlan mensaje = new MensajesErrorPlan();
                mensaje.informarErrorCargandoConfiguracion(null, e);
            }

            //Si la Carga de la configuración es Correcta se continua con el proceso
            if (generacion_correcta > 0) {
//-----------------------------------------------------------------------------------------------------------------------
//              //Captura los datos ingresados en index.jsp
                String contractNumber = request.getParameter("identificador");
                String sessionId = request.getParameter("sessionid");
                //<editor-fold defaultstate="collapsed" desc="comment">
                System.err.println("********************************************");
                System.err.println("-------->" + sessionId + "<----------");
                System.out.println("-------->" + contractNumber + "<----------");
                System.err.println("********************************************");
                //</editor-fold>
                String mostrar_valor = request.getParameter("mostrar_valor");
                //String tipoDocumento = request.getParameter ("tipoDocumento");
                String tipoDocumento = request.getParameter("DocumentType");
                //tipoDocumento = "propuesta";
                //Almacena el id sesion de SalesForce para poder consumir el servicio WSDesignBridge
                SessionHeader sessionHeader = new SessionHeader();
                sessionHeader.setSessionId(sessionId);
//                sessionHeader.setSessionId("00Dj0000000H3SJ!ARIAQKu24fa3XM2s14k395xVc4K3hRsiRKowroIrSZc_.7yoDF.URL8mA3BdDLHy8GwO8clL9NDLPqUmxi8U2Z1lDJpNkwpw");

                //1. Intentar leer los parámetros del Servicio Web de Salesforce
                WSDesignBridgeService service = new WSDesignBridgeService(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/WSDesignBridge.wsdl"));
                WSDesignBridgePortType port = service.getWSDesignBridge();

                /*-
                 Envia a WSDesignBrigde los valores capturados, tales como <Id Cotizacion, Id Sesion,
                 TipoDocumento = 'propuesta', mostrar_valor = 1>
                 */
                /**
                 * **************************************
                 */
                //Captura los datos devueltos por WSDesignBridge
                Response result = port.getInformationProducts(contractNumber, mostrar_valor, tipoDocumento, sessionHeader, null, null, null);
//              Response result = port.getInformationProducts(contractNumber,mostrar_valor, tipoDocumento);

                //Armar el archivo de parámetros de acuerdo con la respuesta:
                JAXBElement<Boolean> resultadoParametros = result.getIsSuccess();
                Boolean resultadoParametrosBoolean = resultadoParametros.getValue();
                //Obtener el objeto Información del Cliente:
                JAXBElement<ClientInfo> clientI = result.getClientI();
                ClientInfo clientInfo = clientI.getValue();
                //Obtener el objeto Información del Contrato:
                JAXBElement<ContractInfo> contractI = result.getContractI();
                ContractInfo contract = contractI.getValue();
                //Obtener el objeto Agente
                JAXBElement<AgentInfo> agentI = result.getAgent();
                AgentInfo agent = agentI.getValue();
                // Obtener el objeto Código de la Plantilla.
                JAXBElement<String> templateCodeI = result.getTemplateCode();
                String templateCode = templateCodeI.getValue();

                //Verificacion de datos enviados de Salesforce
                /*System.out.println("°°°°°°°°°°DATOS DE SALESFORCE°°°°°°°°°°");
                System.out.println("isSuccess-> " + resultadoParametrosBoolean);
                System.out.println("templateCode-> " + templateCode);
                System.out.println("clientInfo.getAddress()->  " + clientInfo.getAddress().getValue());
                System.out.println("clientInfo.getAuthorizer1ID()->  " + clientInfo.getAuthorizer1ID().getValue());
                System.out.println("clientInfo.getAuthorizer1Name()->  " + clientInfo.getAuthorizer1Name().getValue());
                System.out.println("clientInfo.getAuthorizer1Phone()->  " + clientInfo.getAuthorizer1Phone().getValue());
                System.out.println("clientInfo.getAuthorizer2ID()->  " + clientInfo.getAuthorizer2ID().getValue());
                System.out.println("clientInfo.getAuthorizer2Name()->  " + clientInfo.getAuthorizer2Name().getValue());
                System.out.println("clientInfo.getAuthorizer2Phone()->  " + clientInfo.getAuthorizer2Phone().getValue());
                System.out.println("clientInfo.getCity()->  " + clientInfo.getCity().getValue());
                System.out.println("clientInfo.getEmail()->  " + clientInfo.getEmail().getValue());
                System.out.println("clientInfo.getFacturatoradress()->  " + clientInfo.getFacturatoradress().getValue());
                System.out.println("clientInfo.getFacturatorphone()->  " + clientInfo.getFacturatorphone().getValue());
                System.out.println("clientInfo.getFax()->  " + clientInfo.getFax().getValue());
                System.out.println("clientInfo.getIdAccount()->  " + clientInfo.getIdAccount().getValue());
                System.out.println("clientInfo.getIdNumber()->  " + clientInfo.getIdNumber().getValue());
                System.out.println("clientInfo.getIdType()->  " + clientInfo.getIdType().getValue());
                System.out.println("clientInfo.getLegalRepID()->  " + clientInfo.getLegalRepID().getValue());
                System.out.println("clientInfo.getLegalRepName()->  " + clientInfo.getLegalRepName().getValue());
                System.out.println("clientInfo.getName()->  " + clientInfo.getName().getValue());
                System.out.println("clientInfo.getPartyId()->  " + clientInfo.getPartyId().getValue());
                System.out.println("clientInfo.getPhone()->  " + clientInfo.getPhone().getValue());
                System.out.println("clientInfo.getWeb()->  " + clientInfo.getWeb().getValue());
                System.out.println("°°°°°°°°°°-ClientInfo°°°°°°°°°°");
                System.out.println("°°°°°°°°°°ContractInfo°°°°°°°°°°");
                System.out.println("contract.getBillList()->  " + contract.getBillList());
                System.out.println("contract.getBillingAgent()->  " + contract.getBillingAgent().getValue());
                System.out.println("contract.getBillingAgentID()->  " + contract.getBillingAgentID().getValue());
                System.out.println("contract.getConsultor()->  " + contract.getConsultor().getValue());
                System.out.println("contract.getConsultorID()->  " + contract.getConsultorID().getValue());
                System.out.println("contract.getContractBarCode()->  " + contract.getContractBarCode().getValue());
                System.out.println("contract.getContractNumber()->  " + contract.getContractNumber().getValue());
                System.out.println("contract.getCurrencyIsoCode()->  " + contract.getCurrencyIsoCode());
                System.out.println("contract.getDiscountValue()->  " + contract.getDiscountValue());
                System.out.println("contract.getMovementType()->  " + contract.getMovementType());
                System.out.println("contract.getNetValue()->  " + contract.getNetValue());
                System.out.println("contract.getSaleDate()->  " + contract.getSaleDate());
                System.out.println("contract.getSaleValue()->  " + contract.getSaleValue());
                System.out.println("contract.getTax1Value()->  " + contract.getTax1Value());
                System.out.println("contract.getTax2Value()->  " + contract.getTax2Value());
                System.out.println("contract.getTextValue()->  " + contract.getTextValue());
                System.out.println("contract.getTotalValue()->  " + contract.getTotalValue());
                System.out.println("contract.getTypeBilling()->  " + contract.getTypeBilling());
                System.out.println("°°°°°°°°°°-ContractInfo°°°°°°°°°°");
                System.out.println("°°°°°°°°°°AgentInfo°°°°°°°°°°");
                System.out.println("agent.getAgentID()-> " + agent.getAgentID());
                System.out.println("agent.getAgentName()-> " + agent.getAgentName());
                System.out.println("°°°°°°°°°°-AgentInfo°°°°°°°°°°");
                System.out.println("°°°°°°°°°°productList°°°°°°°°°°");
                List<ProductInfo> prInf = result.getProductList();
                List<ListInfo> lstLsInfo = null;
                List<InternetInfo> lstIntInfo = null;
                List<AdvertiseInfo> lstAdInfo = null;
                List<ProductionInfo> lstProInfo = null;

                for (int i = 0; i < prInf.size(); i++) {
                    for (int j = 0; j < prInf.get(i).getListType().size(); j++) {
                        ListInfo lsInfo = prInf.get(i).getListType().get(j);
                        System.out.println("°°°°°°°°°°ListType°°°°°°°°°°");
                        System.out.println("lsInfo.getClasification()->  " + lsInfo.getClasification());
                        System.out.println("lsInfo.getCodeinstance()->  " + lsInfo.getCodeInstance());
                        System.out.println("lsInfo.getFigurationcity()->  " + lsInfo.getFigurationCity());
                        System.out.println("lsInfo.getFigurationname()->  " + lsInfo.getFigurationName());
                        System.out.println("lsInfo.getInitials()->  " + lsInfo.getInitials());
                        System.out.println("lsInfo.getInsercionname()->  " + lsInfo.getInsercionName());
                        System.out.println("lsInfo.getMacrosection()->  " + lsInfo.getMacrosection());
                        System.out.println("lsInfo.getNetvalue()->  " + lsInfo.getNetValue());
                        System.out.println("lsInfo.getNocontent()->  " + lsInfo.getNocontent());
                        System.out.println("lsInfo.getProductbarcode()->  " + lsInfo.getProductBarCode());
                        System.out.println("lsInfo.getProductcode()->  " + lsInfo.getProductCode());
                        System.out.println("lsInfo.getProducteditioncode()->  " + lsInfo.getProductEditionCode());
                        System.out.println("lsInfo.getProducteditionname()->  " + lsInfo.getProductEditionName());
                        System.out.println("lsInfo.getProductname()->  " + lsInfo.getProductName());
                        System.out.println("lsInfo.getProductpartcode()->  " + lsInfo.getProductPartCode());
                        System.out.println("lsInfo.getProductpartname()->  " + lsInfo.getProductPartName());
                        System.out.println("lsInfo.getProfession()->  " + lsInfo.getProfession());
                        for (int k = 0; k < lsInfo.getAdditionalLines().size(); k++) {
                            AdditionalLineInfo adLinInfo = lsInfo.getAdditionalLines().get(k);
                            System.out.println("adLinInfo.getAddress()-> " + adLinInfo.getAddress());
                            System.out.println("adLinInfo.getConsecutive()-> " + adLinInfo.getConsecutive());
                            System.out.println("adLinInfo.getPrefixType()->  " + adLinInfo.getPrefixType());
                            System.out.println("adLinInfo.getAddress()->  " + adLinInfo.getAddress());
                            System.out.println("adLinInfo.getAssemblageList()->  " + adLinInfo.getAssemblageList());
                            System.out.println("adLinInfo.getConsecutive()->  " + adLinInfo.getConsecutive());
                            System.out.println("adLinInfo.getFirstname()->  " + adLinInfo.getFirstname());
                            System.out.println("adLinInfo.getIndent()->  " + adLinInfo.getIndent());
                            System.out.println("adLinInfo.getLastname1()->  " + adLinInfo.getLastname1());
                            System.out.println("adLinInfo.getLastname2()->  " + adLinInfo.getLastname2());
                            System.out.println("adLinInfo.getPhone()->  " + adLinInfo.getPhone());
                            System.out.println("adLinInfo.getPrefix()->  " + adLinInfo.getPrefix());
                            System.out.println("adLinInfo.getPrefixType()->  " + adLinInfo.getPrefixType());
                            System.out.println("adLinInfo.getReferenceCode()->  " + adLinInfo.getReferenceCode());
                            System.out.println("adLinInfo.getSecondname()->  " + adLinInfo.getSecondname());
                            for (int l = 0; l < lsInfo.getAdditionalLines().get(k).getAssemblageList().size(); l++) {
                                AssemblageInfo assemList = lsInfo.getAdditionalLines().get(k).getAssemblageList().get(l);
                                System.out.println("°°°°°°°°°°AssemList°°°°°°°°°°");
                                System.out.println("assemList.getCondensation()->  " + assemList.getCondensation());
                                System.out.println("assemList.getConsecutive()->  " + assemList.getConsecutive());
                                System.out.println("assemList.getFontcode()->  " + assemList.getFontCode());
                                System.out.println("assemList.getInterline()->  " + assemList.getInterline());
                                System.out.println("assemList.getJustification()->  " + assemList.getJustification());
                                System.out.println("assemList.getLeftmargin()->  " + assemList.getLeftMargin());
                                System.out.println("assemList.getRightmargin()->  " + assemList.getRightMargin());
                                System.out.println("assemList.getSize()->  " + assemList.getSize());
                                System.out.println("assemList.getText()->  " + assemList.getText());
                            }
                        }
                        lstLsInfo.add(lsInfo);
                    }
                    for (int j = 0; j < prInf.get(i).getInternetType().size(); j++) {
                        InternetInfo intInfo = prInf.get(i).getInternetType().get(j);
                        System.out.println("intInfo.getAdditionalSections()->  " + intInfo.getAdditionalSections());
                        System.out.println("intInfo.getCallFreePhone()->  " + intInfo.getCallFreePhone());
                        System.out.println("intInfo.getClientName()->  " + intInfo.getClientName());
                        System.out.println("intInfo.getEndDate()->  " + intInfo.getEndDate());
                        System.out.println("intInfo.getFigurationCity()->  " + intInfo.getFigurationCity());
                        System.out.println("intInfo.getFigurationName()->  " + intInfo.getFigurationName());
                        System.out.println("intInfo.getInsercionName()->  " + intInfo.getInsercionName());
                        System.out.println("intInfo.getLabel1()->  " + intInfo.getLabel1());
                        System.out.println("intInfo.getLabel2()->  " + intInfo.getLabel2());
                        System.out.println("intInfo.getMacrosection()->  " + intInfo.getMacrosection());
                        System.out.println("intInfo.getMainSection()->  " + intInfo.getMainSection());
                        System.out.println("intInfo.getNetValue()->  " + intInfo.getNetValue());
                        System.out.println("intInfo.getProductBarCode()->  " + intInfo.getProductBarCode());
                        System.out.println("intInfo.getProductCode()->  " + intInfo.getProductCode());
                        System.out.println("intInfo.getProductEditionCode()->  " + intInfo.getProductEditionCode());
                        System.out.println("intInfo.getProductEditionName()->  " + intInfo.getProductEditionName());
                        System.out.println("intInfo.getProductName()->  " + intInfo.getProductName());
                        System.out.println("intInfo.getProductPartCode()->  " + intInfo.getProductPartCode());
                        System.out.println("intInfo.getProductPartName()->  " + intInfo.getProductPartName());
                        System.out.println("intInfo.getReferenceCode()->  " + intInfo.getReferenceCode());
                        System.out.println("intInfo.getReferenceName()->  " + intInfo.getReferenceName());
                        System.out.println("intInfo.getSponsorSection()->  " + intInfo.getSponsorSection());
                        System.out.println("intInfo.getText1()->  " + intInfo.getText1());
                        System.out.println("intInfo.getText2()->  " + intInfo.getText2());
                        System.out.println("intInfo.getUrl()->  " + intInfo.getURL());
                        lstIntInfo.add(intInfo);
                    }
                    for (int j = 0; j < prInf.get(i).getAdvertiseType().size(); j++) {
                        AdvertiseInfo adInfo = prInf.get(i).getAdvertiseType().get(j);
                        System.out.println("adInfo.getClasification()->  " + adInfo.getClasification());
                        System.out.println("adInfo.getClientName()->  " + adInfo.getClientName());
                        System.out.println("adInfo.getFigurationCity()->  " + adInfo.getFigurationCity());
                        System.out.println("adInfo.getFigurationName()->  " + adInfo.getFigurationName());
                        System.out.println("adInfo.getHighQuality()->  " + adInfo.getHighQuality());
                        System.out.println("adInfo.getInsercionName()->  " + adInfo.getInsercionName());
                        System.out.println("adInfo.getMacrosection()->  " + adInfo.getMacrosection());
                        System.out.println("adInfo.getNetValue()->  " + adInfo.getNetValue());
                        System.out.println("adInfo.getProductBarCode()->  " + adInfo.getProductBarCode());
                        System.out.println("adInfo.getProductCode()->  " + adInfo.getProductCode());
                        System.out.println("adInfo.getProductEditionCode()->  " + adInfo.getProductEditionCode());
                        System.out.println("adInfo.getProductEditionName()->  " + adInfo.getProductEditionName());
                        System.out.println("adInfo.getProductName()->  " + adInfo.getProductName());
                        System.out.println("adInfo.getProductPartCode()->  " + adInfo.getProductPartCode());
                        System.out.println("adInfo.getProductPartName()->  " + adInfo.getProductPartName());
                        System.out.println("adInfo.getPurchase()->  " + adInfo.getPurchase());
                        System.out.println("adInfo.getReferenceCode()->  " + adInfo.getReferenceCode());
                        System.out.println("adInfo.getReferenceName()->  " + adInfo.getReferenceName());
                        System.out.println("adInfo.getSaleTypeDesignNumerRequest()->  " + adInfo.getSaleTypeDesignNumerRequest());
                        System.out.println("adInfo.getSectionName()->  " + adInfo.getSectionName());
                        System.out.println("adInfo.getSketchNumber()->  " + adInfo.getSketchNumber());
                        System.out.println("adInfo.getSketchType()->  " + adInfo.getSketchType());
                        System.out.println("adInfo.getSponsorSection()->  " + adInfo.getSponsorSection());
                        lstAdInfo.add(adInfo);
                    }
                    for (int j = 0; j < prInf.get(i).getProductionType().size(); j++) {
                        ProductionInfo proInfo = prInf.get(i).getProductionType().get(j);
                        System.out.println("proInfo.getAdditionalLines()->  " + proInfo.getAdditionalLines());
                        System.out.println("proInfo.getClasification()->  " + proInfo.getClasification());
                        System.out.println("proInfo.getFigurationCity()->  " + proInfo.getFigurationCity());
                        System.out.println("proInfo.getFigurationName()->  " + proInfo.getFigurationName());
                        System.out.println("proInfo.getIdPurchase()->  " + proInfo.getIdPurchase());
                        System.out.println("proInfo.getIdSaleRequest()->  " + proInfo.getIdSaleRequest());
                        System.out.println("proInfo.getInsercionName()->  " + proInfo.getInsercionName());
                        System.out.println("proInfo.getMacrosection()->  " + proInfo.getMacrosection());
                        System.out.println("proInfo.getName()->  " + proInfo.getName());
                        System.out.println("proInfo.getNetValue()->  " + proInfo.getNetValue());
                        System.out.println("proInfo.getProductBarCode()->  " + proInfo.getProductBarCode());
                        System.out.println("proInfo.getProductCode()->  " + proInfo.getProductCode());
                        System.out.println("proInfo.getProductEditionCode()->  " + proInfo.getProductEditionCode());
                        System.out.println("proInfo.getProductEditionName()->  " + proInfo.getProductEditionName());
                        System.out.println("proInfo.getProductName()->  " + proInfo.getProductName());
                        System.out.println("proInfo.getProductPartCode()->  " + proInfo.getProductPartCode());
                        System.out.println("proInfo.getProductPartName()->  " + proInfo.getProductPartName());
                        System.out.println("proInfo.getReferenceCode()->  " + proInfo.getReferenceCode());
                        System.out.println("proInfo.getReferenceName()->  " + proInfo.getReferenceName());
                        System.out.println("proInfo.getReferenceType()->  " + proInfo.getReferenceType());
                        System.out.println("proInfo.getSection()->  " + proInfo.getSection());
                        System.out.println("proInfo.getShowLogoInfo()->  " + proInfo.getShowLogoInfo());
                        System.out.println("proInfo.getShowSectionInfo()->  " + proInfo.getShowSectionInfo());
                        lstProInfo.add(proInfo);
                    }
                }
                //System.out.println("-> " + );
                System.out.println("°°°°°°°°°°-productList°°°°°°°°°°");
                */
                //Si la respuesta obtenida en la variable isSucces es true se continua con el proceso
                if (resultadoParametrosBoolean == true) {

                    /**
                     * ******************************************
                     */
                    /*     DATOS BASICOS DE CONFIGURACION        */
                    /**
                     * ******************************************
                     */
                    //Carga la configuracion correspondiente a partir de templateCode (1Juridico,1Calidad)
                    ConfiguracionBasica confB = new ConfiguracionBasica();
                    Configuracion configBasica = confB.obtenerConfiguracionBasica(listaConfiguraciones, templateCode);

                    /**
                     * ******************************************
                     */
                    /*                  PORTADA                  */
                    /**
                     * ******************************************
                     */
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                 INICIO PAG                  */");
                    System.out.println("/*                  PORTADA                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    ConfiguracionPortada confPortada = new ConfiguracionPortada();
                    TipoPagina portada = null;
                    TipoPagina genesisPage = null;
                    TipoPagina produccionPage = null;
                    TipoPagina internetPage = null;
                    TipoPagina diagramacionListasPage = null;
                    TipoPagina reglamentos = null;
                    TipoPagina cuotasPage = null;

                    try {
                        //Cargar el id de TipoPagina (PORTADA_PLAN_PUBLICACION)
                        portada = confPortada.llevaPortadaPagina(listaConfiguraciones, templateCode);
                        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("°°°°°°°°°°°°°°°°°°°DATOS PORTADA (TIPO PAGINA)°°°°°°°°°°°°°°°°°°°");
                        System.out.println("°°°°°°°°°°°°°°°°°°°" + portada.getCodigo() + "°°°°°°°°°°°°°°°°°°°");
                        System.out.println("°°°°°°°°°°°°°°°°°°°" + portada.getNombre() + "°°°°°°°°°°°°°°°°°°°°");
                        System.out.println("°°°°°°°°°°°°°°°°°°°" + portada.getArchivo_plantilla() + "°°°°°°°°°");
                        System.out.println("°°°°°°°°°°°°°°°°°°°" + portada.getSeccionesPagina().get(0) + "°°°°°°°°°°°");
                        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                        if (portada != null) {
                            System.out.println("--------------------------------------------------------------------------------");
                            System.out.println("Informacion para la portada: Cliente " + clientInfo + " contract: " + contract + " Agente: " + agent + " template code: " + templateCode + " portada:  " + portada + " Mostrar Valor: " + mostrar_valor);
                            System.out.println("--------------------------------------------------------------------------------");
                            ConstructorPortada constructorPortada = new ConstructorPortada();
                            //Construye la página pdf portada del documento
                            paginaPortada = constructorPortada.construirPortada(clientInfo, contract, agent, templateCode, portada, mostrar_valor);
                            System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                            System.out.println("Id Agente" + agent.getAgentID().getValue());
                            System.out.println("Nombre Agente" + agent.getAgentName().getValue());
                            System.out.println("1||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

                        }

                    } catch (Exception e) {
                        generacion_correcta = -1;
                        PrintWriter out = response.getWriter();
                        MensajesErrorPlan mensaje = new MensajesErrorPlan();
                        mensaje.informarErrorPortada(out, e);
                        out.close();
                    }
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  FIN PAG                  */");
                    System.out.println("/*                  PORTADA                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");

                    /**
                     * ******************************************
                     */
                    /*     PAGINA DE CUOTAS ADICIONALES          */
                    /**
                     * ******************************************
                     */
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                 INICIO PAG                  */");
                    System.out.println("/*                 CUOTAS ADI                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");

                    ConfiguracionCuotas confCuotas = new ConfiguracionCuotas();

                    try {
                        cuotasPage = confCuotas.llevaCuotasPagina(listaConfiguraciones, templateCode);
                        if (cuotasPage != null) {

                            ConstructorCuotas constructorCuotas = new ConstructorCuotas();
                            listaCuotasFinal = constructorCuotas.construirListaCuotasFinal(clientInfo, contract, agent, templateCode, cuotasPage, mostrar_valor);

                        } else {

                        }

                    } catch (Exception e) {
                        generacion_correcta = -1;
                        PrintWriter out = response.getWriter();
                        MensajesErrorPlan mensaje = new MensajesErrorPlan();
                        mensaje.informarErrorPortada(out, e);
                        out.close();
                    }
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  FIN PAG                  */");
                    System.out.println("/*                 CUOTAS ADI                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    /**
                     * ******************************************
                     */
                    /*       AVISOS DE PRODUCCION                */
                    /**
                     * ******************************************
                     */
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  INICIO PAG                  */");
                    System.out.println("/*                  AVISO PROD                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    if (generacion_correcta > 0) {

                        ConfiguracionDiagramacion confDiagramacion = new ConfiguracionDiagramacion();
                        produccionPage = confDiagramacion.llevaProduccion(listaConfiguraciones, templateCode);

                        if (produccionPage != null) {

                            try {

                                //Obtener las páginas que se deben generar.
                                List<ProductInfo> productList = result.getProductList();
                                System.out.println("productList.size()-> " + productList.size());
                                for (int k = 0; k < productList.size(); k++) {
                                    System.out.println("Ingresa a generar pagina de produccion");
                                    ProductInfo producto = productList.get(k);

                                    List<ProductionInfo> listType = producto.getProductionType();
                                    System.out.println("listType.size()-> " + listType.size());
                                    for (int z = 0; z < listType.size(); z++) {
                                        ProductionInfo elementoLista = listType.get(z);

                                        //Construir la página respectiva y agregarla a la lista
                                        try {

                                            ConstructorDiagramacion constructorDiagramacion = new ConstructorDiagramacion();
                                            ByteArrayOutputStream pdfGenerado = constructorDiagramacion.construirAvisoProduccion(elementoLista, produccionPage, contract, configBasica);
                                            if (pdfGenerado != null) {
                                                listaAvisosProduccion.add(pdfGenerado);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }

                            } catch (Exception e) {
                                generacion_correcta = -1;
                                PrintWriter out = response.getWriter();
                                MensajesErrorPlan mensaje = new MensajesErrorPlan();
                                mensaje.informarErrorDiagramacion(out, e);
                                out.close();
                            }
                        }
                    }
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  FIN PAG                  */");
                    System.out.println("/*                 AVISO PROD                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    /**
                     * ******************************************
                     */
                    /*         AVISOS DE INTERNET                */
 /*  PASO 1 Generar todos los avisos y almace-*/
 /*         narlos en una lista.              */
                    /**
                     * ******************************************
                     */
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                 INICIO PAG                  */");
                    System.out.println("/*                  AVISO INT                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    if (generacion_correcta > 0) {

                        ConfiguracionInternet confInternet = new ConfiguracionInternet();
                        internetPage = confInternet.llevaInternet(listaConfiguraciones, templateCode);

                        if (internetPage != null) {
                            List<InternetInfo> listType = new ArrayList<InternetInfo>();
                            ConstructorInternet constructorInternet = new ConstructorInternet();
                            try {

                                //Obtener las páginas que se deben generar.
                                List<ProductInfo> productList = result.getProductList();
                                for (int k = 0; k < productList.size(); k++) {

                                    ProductInfo producto = productList.get(k);

                                    listType = producto.getInternetType();

                                    for (int z = 0; z < listType.size(); z++) {
                                        System.out.println("Ingresa a generar pagina de internet");
                                        InternetInfo elementoLista = listType.get(z);

                                        //Construir la página respectiva y agregarla a la lista
                                        ByteArrayOutputStream pdfGenerado = constructorInternet.construirAvisoInternet(elementoLista, internetPage, contract, configBasica, z);
                                        //constructorInternet.construirAvisoInternet(elementoLista,internetPage,contract,configBasica);
                                        if (pdfGenerado != null) {
                                            listaAvisosInternetTemporal.add(pdfGenerado);
                                            System.out.println("Ingrese a generar el temporal de internet ");
                                        }

                                    }

                                }
                                //Con la lista de archivos construida se procede a armar las páginas del Plan.

                                listaAvisosInternetFinal = constructorInternet.construirListaPaginasFinal(listType, internetPage, contract, configBasica, listaAvisosInternetTemporal);

                            } catch (Exception e) {
                                generacion_correcta = -1;
                                System.out.println("se va por el catch ");
                                PrintWriter out = response.getWriter();
                                MensajesErrorPlan mensaje = new MensajesErrorPlan();
                                mensaje.informarErrorDiagramacion(out, e);
                                out.close();
                            }
                        }
                    }
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  FIN PAG                  */");
                    System.out.println("/*                 AVISO INT                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    /**
                     * ********************************************
                     */
                    /*         AVISOS DE DIAGRAMACION            */
 /*  PASO 1 Generar todos los avisos y almace-*/
 /*         narlos en una lista.              */
                    /**
                     * ******************************************
                     */
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  INICIO PAG                  */");
                    System.out.println("/*                 DIAGRAMACION                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    if (generacion_correcta > 0) {

                        ConfiguracionDiagramacion confDiagramacionListas = new ConfiguracionDiagramacion();
                        diagramacionListasPage = confDiagramacionListas.llevaDiagramacion(listaConfiguraciones, templateCode);

                        if (diagramacionListasPage != null) {
                            List<ListInfo> listType = new ArrayList<ListInfo>();
                            ConstructorDiagramacion constructorDiagramacionListas = new ConstructorDiagramacion();
                            try {
                                //Obtener las páginas que se deben generar.
                                List<ProductInfo> productList = result.getProductList();
                                for (int k = 0; k < productList.size(); k++) {
                                    System.out.println("Ingresa a generar pagina de Diagramacion");
                                    ProductInfo producto = productList.get(k);

                                    listType = producto.getListType();
                                    Boolean contenido = false;// 12-06-2013 JAAR cambio tag
                                    String novisible = "true";
                                    for (int z = 0; z < listType.size(); z++) {
                                        ListInfo elementoLista = listType.get(z);

                                        //Cambio para no mostrar aviso para productos tipo TAG
                                        //**********************************************************************************************
                                        //if(Mostrar.equalsIgnoreCase(String.valueOf(elementoLista.getNocontent().getValue())))
                                        if (novisible.equalsIgnoreCase(elementoLista.getNocontent().getValue())) {
                                            System.out.println("NO_DEBE_CONTENER_AVISO===>>> ");//JAAR TAG
                                            contenido = true;
                                        } else {
                                            contenido = false;
                                            System.out.println("No esssss igual" + elementoLista.getNocontent());
                                        }

                                        //********************************************************************************************** 
                                        //Construir la página respectiva y agregarla a la lista
                                        ByteArrayOutputStream pdfGenerado = constructorDiagramacionListas.construirAvisoDiagramacion(elementoLista, diagramacionListasPage, contract, configBasica, z, contenido);
                                        if (pdfGenerado != null) {
                                            listaAvisosDiagramacionTemporal.add(pdfGenerado);
                                        }

                                    }

                                }
                                //Con la lista de archivos construida se procede a armar las páginas del Plan.
                                listaAvisosDiagramacionFinal = constructorDiagramacionListas.construirListaPaginasFinal(listType, diagramacionListasPage, contract, configBasica, listaAvisosDiagramacionTemporal);

                            } catch (Exception e) {
                                generacion_correcta = -1;
                                PrintWriter out = response.getWriter();
                                MensajesErrorPlan mensaje = new MensajesErrorPlan();
                                mensaje.informarErrorDiagramacion(out, e);
                                out.close();
                            }
                        }
                    }
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  FIN PAG                  */");
                    System.out.println("/*                 DIAGRAMACION                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    /**
                     * ******************************************
                     */
                    /*                 GENESIS                   */
                    /**
                     * ******************************************
                     */
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                INICIO PAG                  */");
                    System.out.println("/*                 GENESIS                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    if (generacion_correcta > 0) {

                        ConfiguracionGenesis confGenesis = new ConfiguracionGenesis();

                        try {

                            genesisPage = confGenesis.llevaGenesis(listaConfiguraciones, templateCode);

                            //Obtener las páginas que se deben generar.
                            List<ProductInfo> productList = result.getProductList();
                            listaPaginasGeneradasCiclo = new ArrayList<ByteArrayOutputStream>();
                            listaPaginasGeneradasCicloFinal = new ArrayList<ByteArrayOutputStream>();
                            for (int k = 0; k < productList.size(); k++) {
                                ProductInfo producto = productList.get(k);
                                List<AdvertiseInfo> advertiseTypeList = producto.getAdvertiseType();
                                System.out.println("El tamaño de la lista de Advertise Type es: " + advertiseTypeList.size());
                                System.out.println("templateCode= " + templateCode);
                                for (int m = 0; m < advertiseTypeList.size(); m++) {
                                    AdvertiseInfo advertiseElement = advertiseTypeList.get(m);
                                    System.out.println("Ingresa a generar pagina de Genesis.");

                                    ConstructorGenesis miConstructorGenesis = new ConstructorGenesis();
                                    //BackgroundColor = listaConf.getbackground();
                                    listaPaginasGeneradasCiclo = miConstructorGenesis.construirGenesis(advertiseElement, genesisPage, configBasica, contract, background);
                                    //Copiar las páginas Generadas en Ciclo
                                    if (listaPaginasGeneradasCiclo != null) {
                                        System.out.println("El tamaño de la lista generada es: " + listaPaginasGeneradasCiclo.size());
                                        for (int w = 0; w < listaPaginasGeneradasCiclo.size(); w++) {
                                            ByteArrayOutputStream paginaGeneradoGenesis = listaPaginasGeneradasCiclo.get(w);
                                            listaPaginasGeneradasCicloFinal.add(paginaGeneradoGenesis);

                                        }
                                    }

                                }
                            }

                        } catch (Exception e) {
                            generacion_correcta = -1;
                            PrintWriter out = response.getWriter();
                            MensajesErrorPlan mensaje = new MensajesErrorPlan();
                            mensaje.informarErrorGenesis(out, e);
                            out.close();
                        }

                    }
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  FIN PAG                  */");
                    System.out.println("/*                 GENESIS                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    /**
                     * ******************************************
                     */
                    /*            REGLAMENTOS                    */
                    /**
                     * ******************************************
                     */
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  INICIO PAG                  */");
                    System.out.println("/*                  REGLAMENTO                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    if (generacion_correcta > 0) {

                        ConfiguracionReglamentos confReglamentos = new ConfiguracionReglamentos();

                        try {
                            reglamentos = confReglamentos.llevaReglamentoPagina(listaConfiguraciones, templateCode);
                            if (reglamentos != null) {

                                ConstructorReglamentos constructorReglamentos = new ConstructorReglamentos();
                                paginaReglamento = new ByteArrayOutputStream();
                                paginaReglamento = constructorReglamentos.construirReglamento(clientInfo, contract, agent, templateCode, reglamentos);

                            }

                        } catch (Exception e) {
                            generacion_correcta = -1;
                            PrintWriter out = response.getWriter();
                            MensajesErrorPlan mensaje = new MensajesErrorPlan();
                            mensaje.informarErrorPortada(out, e);
                            out.close();
                        }

                    }
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  FIN PAG                  */");
                    System.out.println("/*                 REGLAMENTO                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    /**
                     * ****************************************************
                     */
                    /*          ENSAMBLAR UN UNICO DOCUMENTO               */
 /* Ensamblar un único documento con las páginas gene-  */
 /* radas.                                              */
                    /**
                     * ****************************************************
                     */
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  INICIO PAG                  */");
                    System.out.println("/*                  ENSAMBLAR                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    if (generacion_correcta > 0) {
                        try {

                            EnsambladorPaginas ensamblador = new EnsambladorPaginas();
                            documentoFinalSinPaginar = ensamblador.ensamblarDocumento(paginaPortada, listaPaginasGeneradasCicloFinal, paginaReglamento, listaAvisosProduccion, listaAvisosInternetFinal, listaAvisosDiagramacionFinal, portada, templateCode, genesisPage, produccionPage, internetPage, diagramacionListasPage, reglamentos, listaCuotasFinal);
                            System.out.println("°°°°°°°°°°°°Tamaño doc->\t" + documentoFinalSinPaginar.size() + "");
//                            System.out.println("°°°°°°°°°°°°ToString doc->\t" + documentoFinalSinPaginar.toString()+ "");
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                    System.out.println("/*                  FIN PAG                  */");
                    System.out.println("/*                 ENSAMBLAR                  */");
                    System.out.println("/**");
                    System.out.println(" * ******************************************");
                    System.out.println(" */");
                } //Si se obtuvo un valor false para isSuccess se informa el error y se interrumpe el proceso.
                else {
                    generacion_correcta = -1;
                    PrintWriter out = response.getWriter();
                    MensajesErrorPlan mensaje = new MensajesErrorPlan();
                    mensaje.informarErrorIsSuccess(out);
                    out.close();
                }
            }

            if (documentoFinalSinPaginar != null) {
                System.out.println("/**");
                System.out.println(" * ******************************************");
                System.out.println(" */");
                System.out.println("/*                  INICIO                  */");
                System.out.println("/*               CREACION PDF                  */");
                System.out.println("/**");
                System.out.println(" * ******************************************");
                System.out.println(" */");
                byte[] bytes = documentoFinalSinPaginar.toByteArray();
                ServletOutputStream ouputStream = response.getOutputStream();
                response.setContentType("application/pdf");
                response.setContentLength(bytes.length);
                response.addHeader("content-disposition", "attachment; filename=plan_publicacion.pdf");
                ouputStream.write(bytes, 0, bytes.length);
                ouputStream.flush();
                ouputStream.close();
                System.out.println("/**");
                System.out.println(" * ******************************************");
                System.out.println(" */");
                System.out.println("/*                   FIN                    */");
                System.out.println("/*                 CREACION PDF             */");
                System.out.println("/**");
                System.out.println(" * ******************************************");
                System.out.println(" */");
            }

        } //Excepción accediendo al Servicio Salesforce
        catch (Exception e) {
            e.printStackTrace();
            generacion_correcta = -1;
            PrintWriter out = response.getWriter();
            MensajesErrorPlan mensaje = new MensajesErrorPlan();
            mensaje.informarErrorWSDesignBridgeService(out, e);
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
