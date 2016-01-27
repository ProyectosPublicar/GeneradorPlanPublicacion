package Construccion.cuotas;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import Configuracion.modelo.Componente;
import Configuracion.modelo.Configuracion;
import Configuracion.modelo.ConstantesCuotas;
import Configuracion.modelo.ConstantesFacturacion;
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
import parametros_plan_salesforce.AgentInfo;
import parametros_plan_salesforce.BillingInfo;
import parametros_plan_salesforce.ClientInfo;
import parametros_plan_salesforce.QuoteInfo;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Administrador
 */
public class ConstructorCuotas {

    public List<ByteArrayOutputStream> construirListaCuotasFinal(ClientInfo clientInfo, ContractInfo contract, AgentInfo agent, String templateCode, TipoPagina cuotasPage, String mostrarValor) {
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°ENTRO A CONTRUIR CUOTAS FINAL°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        List<ByteArrayOutputStream> listaRetorno = new ArrayList<ByteArrayOutputStream>();
        ByteArrayOutputStream paginaGenerada = null;
        List<BillingInfo> listaTemporalCuotas = new ArrayList<BillingInfo>();

        int altoSeccionBasica = 0;
        int altoSeccionDetalle = 0;

        //Determinar el tamaño de la sección en la que se pintan las cuotas.
        for (int i = 0; i < cuotasPage.getSeccionesPagina().size(); i++) {

            Seccion sec = cuotasPage.getSeccionesPagina().get(i);

            if (sec.getTipo_seccion().equals(ConstantesCuotas.area_cuotas)) {
                altoSeccionBasica = sec.getAlto();
            }

            if (sec.getTipo_seccion().equals(ConstantesCuotas.detalle_cuotas)) {
                altoSeccionDetalle = sec.getAlto();
            }
        }

        //Ir recorriendo la lista de cuotas generadas para ver si caben o se requiere
        //generar una nueva página.
        try {

            List<BillingInfo> listaCuotas = contract.getBillList();
            if (listaCuotas.size() < 2 || mostrarValor.equalsIgnoreCase("0")) {
                return listaRetorno;
            }

            for (int k = 1; k < listaCuotas.size(); k++) {
                //Insertar la primera facturación
                BillingInfo cuota = listaCuotas.get(k);
                float altoTemporal = 0;
                float alto = altoSeccionDetalle;
                altoTemporal += altoSeccionDetalle;
                altoTemporal += 20;
                listaTemporalCuotas.add(cuota);

                //Mirar si cabe la segunda facturaciòn
                if (k + 1 < listaCuotas.size()) {
                    cuota = listaCuotas.get(k + 1);
                    alto = altoSeccionDetalle;

                    float altoTemporal2 = altoTemporal + altoSeccionDetalle;

                    if (altoTemporal2 <= altoSeccionBasica) {
                        listaTemporalCuotas.add(cuota);
                        k++;
                        altoTemporal += altoSeccionDetalle;
                        altoTemporal += 20;
                    }
                }

                //Mirar si cabe la tercera facturación
                if (k + 1 < listaCuotas.size()) {
                    cuota = listaCuotas.get(k + 1);
                    alto = altoSeccionDetalle;

                    float altoTemporal3 = altoTemporal + altoSeccionDetalle;

                    if (altoTemporal3 <= altoSeccionBasica) {
                        listaTemporalCuotas.add(cuota);
                        k++;
                        altoTemporal += altoSeccionDetalle;
                        altoTemporal += 20;
                    }
                }

                //Mirar si cabe la cuarta facturaciòn
                if (k + 1 < listaCuotas.size()) {
                    cuota = listaCuotas.get(k + 1);
                    alto = altoSeccionDetalle;

                    float altoTemporal4 = altoTemporal + altoSeccionDetalle;

                    if (altoTemporal4 <= altoSeccionBasica) {
                        listaTemporalCuotas.add(cuota);
                        k++;
                        altoTemporal += altoSeccionDetalle;
                        altoTemporal += 20;
                    }
                }

                //Mirar si cabe la quinta facturaciòn
                if (k + 1 < listaCuotas.size()) {
                    cuota = listaCuotas.get(k + 1);
                    alto = altoSeccionDetalle;

                    float altoTemporal5 = altoTemporal + altoSeccionDetalle;

                    if (altoTemporal5 <= altoSeccionBasica) {
                        listaTemporalCuotas.add(cuota);
                        k++;
                        altoTemporal += altoSeccionDetalle;
                        altoTemporal += 20;
                    }
                }

                //Mirar si cabe la sexta facturaciòn
                if (k + 1 < listaCuotas.size()) {
                    cuota = listaCuotas.get(k + 1);
                    alto = altoSeccionDetalle;

                    float altoTemporal6 = altoTemporal + altoSeccionDetalle;

                    if (altoTemporal6 <= altoSeccionBasica) {
                        listaTemporalCuotas.add(cuota);
                        k++;
                    }
                }

                //Mandara a generar la página y agregarla a la lista de páginas
                paginaGenerada = generarPaginaParcial(listaTemporalCuotas, cuotasPage, contract, mostrarValor);
                listaRetorno.add(paginaGenerada);
                listaTemporalCuotas = new ArrayList<BillingInfo>();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°SALIO DE CONTRUIR CUOTAS FINAL°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        return listaRetorno;
    }

    public ByteArrayOutputStream generarPaginaParcial(List<BillingInfo> listaTemporalCuotas, TipoPagina cuotasPage, ContractInfo contract, String mostrarValor) {
        System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A GENERAR PAGINA PARCIAL°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        ByteArrayOutputStream paginaGenerada = new ByteArrayOutputStream();
        try {

            PdfReader readerPlantilla;
            String rutaPlantilla = "plantillas_archivos/" + cuotasPage.getArchivo_plantilla();

            readerPlantilla = new PdfReader(rutaPlantilla);

            //En esta variable se guarda el tamaño de la Plantilla.
            Rectangle pageSize = new Rectangle(readerPlantilla.getPageSize(1));
            Document documentoFinal = new Document(pageSize);
            PdfWriter writterGeneral = PdfWriter.getInstance(documentoFinal, paginaGenerada);
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
            int altoContenedor = 0;
            try {
                for (int i = 0; i < cuotasPage.getSeccionesPagina().size(); i++) {

                    Seccion sec = cuotasPage.getSeccionesPagina().get(i);
                    int altoSeccion = sec.getAlto();
                    int coordenadaYInicial = sec.getCoordenada_inicio_y();
                    int coordenadaYInicialSinModificar = sec.getCoordenada_inicio_y();

                    if (sec.getTipo_seccion().equals(ConstantesCuotas.detalle_cuotas)) {

                        altoContenedor = altoSeccion;

                        //Recorrer la lista de Cuotas e ir agregando:
                        for (int jj = 0; jj < listaTemporalCuotas.size(); jj++) {
                            BillingInfo detalleCuota = listaTemporalCuotas.get(jj);

                            if (jj == 0) {
                                this.agregarSeccion(content, sec, writterGeneral, contract, listaTemporalCuotas, 0, detalleCuota, mostrarValor);
                                coordenadaYInicial -= altoSeccion;
                                coordenadaYInicial -= 20;
                            } else {

                                sec.setCoordenada_inicio_y(coordenadaYInicial);
                                this.agregarSeccion(content, sec, writterGeneral, contract, listaTemporalCuotas, 0, detalleCuota, mostrarValor);
                                coordenadaYInicial -= altoSeccion;
                                coordenadaYInicial -= 20;
                            }

                        }
                        sec.setCoordenada_inicio_y(coordenadaYInicialSinModificar);

                    } else if (sec.getTipo_seccion().equals(ConstantesTipoSeccion.listas_cuotas_pago_adicionales)) {

                        //Recorrer la lista de Cuotas e ir agregando:
                        for (int jj = 0; jj < listaTemporalCuotas.size(); jj++) {
                            BillingInfo detalleCuota = listaTemporalCuotas.get(jj);

                            if (jj == 0) {
                                this.agregarSeccion(content, sec, writterGeneral, contract, listaTemporalCuotas, 0, detalleCuota, mostrarValor);
                                coordenadaYInicial -= altoContenedor;
                                coordenadaYInicial -= 20;
                            } else {

                                sec.setCoordenada_inicio_y(coordenadaYInicial);
                                this.agregarSeccion(content, sec, writterGeneral, contract, listaTemporalCuotas, 0, detalleCuota, mostrarValor);
                                coordenadaYInicial -= altoContenedor;
                                coordenadaYInicial -= 20;
                            }

                        }
                        sec.setCoordenada_inicio_y(coordenadaYInicialSinModificar);

                    } else {

                        this.agregarSeccion(content, sec, writterGeneral, contract, listaTemporalCuotas, 0, null, mostrarValor);

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            documentoFinal.close();
        } catch (Exception e) {

            e.printStackTrace();

        }
        System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°°°°°°°°°°SALIO DE GENERAR PAGINA PARCIAL°°°°°°°°°°°°°°°°");
        System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        return paginaGenerada;
    }

    public void agregarSeccion(PdfContentByte content, Seccion sec, PdfWriter writterGeneral, ContractInfo contract, List<BillingInfo> listaTemporalPdfs, int pagina, BillingInfo detalleCuota, String mostrarValor) throws Exception {
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°ENTRO A AGREGAR SECCION******°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

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

            //Pintar el Rectàngulo
            if (sec.getTipo_seccion().equalsIgnoreCase(ConstantesCuotas.detalle_cuotas)) {
                recSeccion.setBorderWidth(1);
                recSeccion.setBorder(1);
                recSeccion.setBorderColor(new BaseColor(0, 0, 0));

            }

            Document documento = new Document(recSeccion);
            //Si el color es mayor que cero se asigna el fondo.  Si se quiere transparente, configurarlo con -1 en la base de datos.
            if (blue >= 0 && red >= 0 && green >= 0) {
                recSeccion.setBackgroundColor(new BaseColor(red, green, blue));
            }
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writter = PdfWriter.getInstance(documento, pdfOutputStream);
            documento.open();
            PdfContentByte cb = writter.getDirectContent();
            cb.beginText();
            BaseFont bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
            cb.setFontAndSize(bf1, 20);
            cb.showText(" ");

            cb.endText();
            documento.close();
            byte[] tempPDF = pdfOutputStream.toByteArray();
            PdfReader readerComponente = new PdfReader(tempPDF);
            PdfTemplate plantilla = writterGeneral.getImportedPage(readerComponente, 1);

            content.addTemplate(plantilla, coordenada_inicio_x, coordenada_inicio_y);

            if (sec.getTipo_seccion().equalsIgnoreCase(ConstantesTipoSeccion.listas_cuotas_pago_adicionales)) {
                List<QuoteInfo> quoteList = detalleCuota.getQuoteList();

                //Iterar sobre cada uno de los componentes
                List<Componente> componentesSeccion = sec.getComponentesSeccion();
                if (componentesSeccion != null && !componentesSeccion.isEmpty()) {
                    for (int w = 0; w < componentesSeccion.size(); w++) {
                        Componente comp = componentesSeccion.get(w);

                        //Obtener todos los atributos del componente:
                        //Iterar sobre la lista de las cuotas.
                        if (quoteList != null) {

                            int numeroCuotasPintadas = 1;
                            float coordenadaPintarY = 0;

                            for (int ii = 0; ii < quoteList.size(); ii++) {
                                QuoteInfo get = quoteList.get(ii);

                                //Agregar cada uno de los componentes:
                                int coordenada_inicial_x = comp.getCoordenada_inicio_x() + coordenada_inicio_x;
                                int coordenada_inicial_y = comp.getCoordenada_inicio_y() + coordenada_inicio_y;

                                int altoComponente = comp.getAlto();
                                int anchoComponente = comp.getAncho();

                                coordenadaPintarY = coordenada_inicial_y - (numeroCuotasPintadas * altoComponente);
                                String atributo = comp.getAtributo();
                                String label = comp.getLabel();
                                int colorFondoRed = comp.getColor_fondo_red();
                                int colorFondoGreen = comp.getColor_fondo_green();
                                int colorFondoBlue = comp.getColor_fondo_blue();
                                int colorFuenteRed = comp.getColor_fuente_red();
                                int colorFuenteGreen = comp.getColor_fuente_green();
                                int colorFuenteBlue = comp.getColor_fuente_blue();
                                Boolean esDinamico = comp.getEsDinamico();
                                String nombreComponente = comp.getNombre();
                                int coordenada_escribir_x = comp.getCoordenada_escribir_x();
                                int coordenada_escribir_y = comp.getCoordenada_escribir_y();
                                String tipoFuente = comp.getTipoFuente();
                                int tamanoFuente = comp.getTamano_fuente();
                                String textoEscribir = "";

                                JAXBElement<String> tipoFacturacionJ = contract.getTypeBilling();
                                String tipoFacturacion = tipoFacturacionJ.getValue();

                                if (esDinamico.booleanValue() == true) {
                                    Class miClase = Class.forName("parametros_plan_salesforce.QuoteInfo");
                                    try {
                                        Field f = miClase.getDeclaredField(atributo);
                                        System.out.println("CONSTRUCTOR CUOTAS QuoteInfo+++++= " + atributo);
                                        f.setAccessible(true);
                                        JAXBElement<String> textoI = (JAXBElement<String>) f.get(get);

                                        //JAAR Se agrega el valor del Nie a plan de publicacion
                                        if (atributo.equalsIgnoreCase("nie") && ii != 0) {
                                            System.out.println("ENTRO QuoteInfo NO ESCRIBIR ADICIONAL+++++= " + atributo + "cuota" + ii + f);
                                            textoEscribir = "";
                                        } else {
                                            textoEscribir = textoI.getValue();
                                        }

                                        if ((nombreComponente.equalsIgnoreCase(ConstantesFacturacion.valor_telefono_cuotas_adicionales)) && (mostrarValor.equalsIgnoreCase("1")) && (tipoFacturacion.equals(ConstantesFacturacion.facturacion_independiente))) {
                                            textoEscribir = "";
                                        }
                                    } catch (Exception e) {
                                        throw e;

                                    }
                                }

                                //Pintar el componente
                                Rectangle recComponente = new Rectangle(anchoComponente, altoComponente);
                                documento = new Document(recComponente);

                                //Si el color es mayor que cero se usa como background. Si se requiere transparente colocar -1 en la base de datos.
                                if (colorFondoRed >= 0 && colorFondoGreen >= 0 && colorFondoBlue >= 0) {
                                    recComponente.setBackgroundColor(new BaseColor(colorFondoRed, colorFondoGreen, colorFondoBlue));
                                }

                                pdfOutputStream = new ByteArrayOutputStream();
                                writter = PdfWriter.getInstance(documento, pdfOutputStream);
                                documento.open();
                                cb = writter.getDirectContent();
                                cb.beginText();
                                bf1 = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
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
                                tempPDF = pdfOutputStream.toByteArray();
                                readerComponente = new PdfReader(tempPDF);
                                plantilla = writterGeneral.getImportedPage(readerComponente, 1);
                                content.addTemplate(plantilla, coordenada_inicial_x, coordenadaPintarY);

                                numeroCuotasPintadas++;

                            }
                        }

                    }
                }
            } else {

                for (int j = 0; j < sec.getComponentesSeccion().size(); j++) {

                    Componente componente = sec.getComponentesSeccion().get(j);

                    agregarComponente(content, componente, writterGeneral, coordenada_inicio_x, coordenada_inicio_y, tipoSeccion, contract, detalleCuota, mostrarValor);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°SALIO DE AGREGAR SECCION******°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

    }

    public void agregarComponente(PdfContentByte content, Componente comp, PdfWriter writterGeneral, int x_inicial, int y_inicial, String tipoSeccion, ContractInfo contract, BillingInfo billing, String mostrar_valor) throws Exception {
        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°ENTRO A AGREGAR COMPONENTE°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        try {

            //Obtener los atributos del Componente
            String datosBarras = "";
            int coordenada_inicial_x = comp.getCoordenada_inicio_x() + x_inicial;
            int coordenada_inicial_y = comp.getCoordenada_inicio_y() + y_inicial;
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
            if (esDinamico.booleanValue() == true) {

                String nombreClase = "";

                //Sección datos del Contrato
                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.datosContrato)) {
                    Class miClase = Class.forName("parametros_plan_salesforce.ContractInfo");
                    try {
                        Field f = miClase.getDeclaredField(atributo);
                        f.setAccessible(true);
                        JAXBElement<String> textoI = (JAXBElement<String>) f.get(contract);
                        textoEscribir = textoI.getValue();
                    } catch (Exception e) {
                        throw e;

                    }
                }

                //Sección datos del Contrato
                if (tipoSeccion.equalsIgnoreCase(ConstantesCuotas.detalle_cuotas)) {
                    Class miClase = Class.forName("parametros_plan_salesforce.BillingInfo");
                    try {
                        Field f = miClase.getDeclaredField(atributo);
                        f.setAccessible(true);
                        JAXBElement<String> textoI = (JAXBElement<String>) f.get(billing);
                        textoEscribir = textoI.getValue();

                        if ((comp.getNombre().equalsIgnoreCase(ConstantesFacturacion.valor_telefono_cuotas)) && (mostrar_valor.equalsIgnoreCase("1"))) {
                            textoEscribir = "";
                        }
                    } catch (Exception e) {
                        throw e;

                    }
                }

                //Sección Código de BarrasCODIGO_BARRAS
                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.codigoBarras)) {

                    try {

                        JAXBElement<String> cadenaCodigoI = contract.getContractBarCode();
                        String cadenaCodigo = cadenaCodigoI.getValue();

                        AxentriaDigitizingService_Service barras_service = new AxentriaDigitizingService_Service(new java.net.URL("http://localhost:8080/GeneradorPlanPublicacion/wsdl/DigitizingServiceNuevo.wsdl"));
                        AxentriaDigitizingService digitazing_service = barras_service.getBasicHttpBindingAxentriaDigitizingService();
                        nuevo_codigo_barras.BarCode dataPdfBarCode = digitazing_service.getDataPngCode128(cadenaCodigo);

                        JAXBElement<String> dataBarras = dataPdfBarCode.getBarCodeString();
                        datosBarras = dataBarras.getValue();

                    } catch (Exception e) {
                        System.err.println("[ERROR PLAN PUBLICACION] [CODIGO DE BARRAS]\n");
                        System.err.println("Error obteniendo servicios web para la generación del código de barras.\n");
                        System.err.println("Traza de la Excepción: " + e);

                    }
                }

                if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.Fecha_de_impresion)) {
                    Date hoy = new Date();

                    SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date resultdate = new Date();
                    System.out.println(date_format.format(resultdate));

                    System.out.println("FECHA CUOTAS ADICIONALES ES IGUAL A = " + date_format.format(resultdate).toString());
                    textoEscribir = date_format.format(resultdate).toString();

                }

            } //Caso Label
            else {
                textoEscribir = label;
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
                    content.addTemplate(plantilla, coordenada_inicial_x, coordenada_inicial_y);
                }

            } //Si la sección es de paginación no pintar los componentes.
            else if (tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.paginacion)) {

            } //Si la sección es de cuotas adicionales segunda pagina, pintar las cuotas
            //else if(tipoSeccion.equalsIgnoreCase(ConstantesTipoSeccion.listas_cuotas_pago_adicionales)){
            //}
            else {
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
        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°SALIO DE AGREGAR COMPONENTE°°°°°°°°°°°°°°°°°");
        System.out.println("\t\t\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
    }

}
