/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Construccion.Ensamblador;

import Construccion.Paginacion.Paginador;
import Configuracion.modelo.ConstantesTipoPaginas;
import Configuracion.modelo.TipoPagina;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class EnsambladorPaginas {

    public ByteArrayOutputStream ensamblarDocumento(ByteArrayOutputStream portada, List<ByteArrayOutputStream> listaPaginasGeneradasCiclo, ByteArrayOutputStream reglamentos, List<ByteArrayOutputStream> listaPaginasProduccion, List<ByteArrayOutputStream> listaPaginasInternet, List<ByteArrayOutputStream> listaPaginasDiagramacion, TipoPagina confPortada, String plantilla, TipoPagina confGenesis, TipoPagina confProduccion, TipoPagina confInternet, TipoPagina confDiagramacion, TipoPagina confReglamentos, List<ByteArrayOutputStream> listaCuotasFinal) throws Exception {
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°ENTRO A ENSAMBLAR DOC°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("-> " + listaCuotasFinal);
//        System.out.println("-> " + confReglamentos);
//        System.out.println("-> " + confDiagramacion);
//        System.out.println("-> " + confInternet);
//        System.out.println("-> " + confProduccion);
//        System.out.println("-> " + confGenesis);
//        System.out.println("-> " + plantilla);
//        System.out.println("-> " + confPortada);
//        System.out.println("-> " + listaPaginasDiagramacion);
//        System.out.println("-> " + listaPaginasInternet);
//        System.out.println("-> " + listaPaginasProduccion);
//        System.out.println("-> " + reglamentos);
//        System.out.println("-> " + listaPaginasGeneradasCiclo);
//        System.out.println("-> " + portada);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfCopy copy = null;
        Document document1 = null;
        Paginador paginador = new Paginador();
        int numeroPagina = 1;
        int numeroTotalPaginas = 0;
        String fechaGeneracion = "";
        fechaGeneracion = this.obtenerFechaHora();

        try {

            //Determinar el número total de páginas
            System.out.println("°°°°°°°°°°°°°°°°°°ENTRO A DETERMINAR N. DE PAGINAS°°°°°°°°°°°°°°°°°°°");
            if (portada != null) {
                PdfReader readerAux = new PdfReader(portada.toByteArray());
                numeroTotalPaginas += readerAux.getNumberOfPages();
                readerAux.close();
            }
            if (listaPaginasGeneradasCiclo != null) {
                numeroTotalPaginas += listaPaginasGeneradasCiclo.size();
            }
            if (reglamentos != null) {
                PdfReader readerAux = new PdfReader(reglamentos.toByteArray());
                numeroTotalPaginas += readerAux.getNumberOfPages();
                readerAux.close();
            }
            if (listaPaginasProduccion != null) {
                numeroTotalPaginas += listaPaginasProduccion.size();
            }
            if (listaPaginasInternet != null) {
                numeroTotalPaginas += listaPaginasInternet.size();
            }
            if (listaPaginasDiagramacion != null) {
                numeroTotalPaginas += listaPaginasDiagramacion.size();
            }
            if (listaCuotasFinal != null) {
                numeroTotalPaginas += listaCuotasFinal.size();
            }

            //Agregar la portada
            if (portada != null) {
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A AGREGRAR PORTADA°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                //Paginar la Portada
                portada = paginador.paginarDocumento(portada, numeroPagina, ConstantesTipoPaginas.portadaPlan, plantilla, confPortada, numeroTotalPaginas, fechaGeneracion, confGenesis, confProduccion, confInternet, confDiagramacion, confReglamentos);
                PdfReader reader1 = new PdfReader(portada.toByteArray());
                numeroPagina += reader1.getNumberOfPages();
                document1 = new Document(reader1.getPageSizeWithRotation(1));
                copy = new PdfCopy(document1, bos);
                document1.open();
                copy.addPage(copy.getImportedPage(reader1, 1));
            }

            //Agregar las lista de Cuotas
            for (int i = 0; i < listaCuotasFinal.size(); i++) {
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A LISTA CUOTAS°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

                ByteArrayOutputStream bos1 = (ByteArrayOutputStream) listaCuotasFinal.get(i);
                bos1 = paginador.paginarDocumento(bos1, numeroPagina + i, ConstantesTipoPaginas.genesisPlan, plantilla, confPortada, numeroTotalPaginas, fechaGeneracion, confGenesis, confProduccion, confInternet, confDiagramacion, confReglamentos);
                PdfReader reader2 = new PdfReader(bos1.toByteArray());
                copy.addPage(copy.getImportedPage(reader2, 1));

            }
            numeroPagina += listaCuotasFinal.size();

            //Agregar las páginas de tipo 1
            for (int i = 0; i < listaPaginasGeneradasCiclo.size(); i++) {
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A PAGIANS TIPO 1°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

                ByteArrayOutputStream bos1 = (ByteArrayOutputStream) listaPaginasGeneradasCiclo.get(i);
                bos1 = paginador.paginarDocumento(bos1, numeroPagina + i, ConstantesTipoPaginas.genesisPlan, plantilla, confPortada, numeroTotalPaginas, fechaGeneracion, confGenesis, confProduccion, confInternet, confDiagramacion, confReglamentos);
                PdfReader reader2 = new PdfReader(bos1.toByteArray());
                copy.addPage(copy.getImportedPage(reader2, 1));

            }
            numeroPagina += listaPaginasGeneradasCiclo.size();

            //Agregar las páginas de avisos de producción
            for (int i = 0; i < listaPaginasProduccion.size(); i++) {
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A AVISOS DE PROD°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

                ByteArrayOutputStream bos1 = (ByteArrayOutputStream) listaPaginasProduccion.get(i);
                bos1 = paginador.paginarDocumento(bos1, numeroPagina + i, ConstantesTipoPaginas.produccionPlan, plantilla, confPortada, numeroTotalPaginas, fechaGeneracion, confGenesis, confProduccion, confInternet, confDiagramacion, confReglamentos);
                PdfReader reader2 = new PdfReader(bos1.toByteArray());
                copy.addPage(copy.getImportedPage(reader2, 1));

            }
            numeroPagina += listaPaginasProduccion.size();

            //Agregar las páginas de avisos de Internet
            for (int i = 0; i < listaPaginasInternet.size(); i++) {
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A AVISOS INT°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

                ByteArrayOutputStream bos1 = (ByteArrayOutputStream) listaPaginasInternet.get(i);
                bos1 = paginador.paginarDocumento(bos1, numeroPagina + i, ConstantesTipoPaginas.internetPlan, plantilla, confPortada, numeroTotalPaginas, fechaGeneracion, confGenesis, confProduccion, confInternet, confDiagramacion, confReglamentos);
                PdfReader reader2 = new PdfReader(bos1.toByteArray());
                copy.addPage(copy.getImportedPage(reader2, 1));

            }
            numeroPagina += listaPaginasInternet.size();

            //Agregar las páginas de avisos de Diagramación
            for (int i = 0; i < listaPaginasDiagramacion.size(); i++) {
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A AVISOS DIAGRAMACION°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                ByteArrayOutputStream bos1 = (ByteArrayOutputStream) listaPaginasDiagramacion.get(i);
                bos1 = paginador.paginarDocumento(bos1, numeroPagina + i, ConstantesTipoPaginas.diagramacionPlan, plantilla, confPortada, numeroTotalPaginas, fechaGeneracion, confGenesis, confProduccion, confInternet, confDiagramacion, confReglamentos);
                PdfReader reader2 = new PdfReader(bos1.toByteArray());
                copy.addPage(copy.getImportedPage(reader2, 1));
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°SALIO DE AVISOS DIAGRAMACION°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
            }
            numeroPagina += listaPaginasDiagramacion.size();

            //Agregar las páginas de Reglamentos
            if (reglamentos != null) {
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°ENTRO A PAG REGLAMENT°°°°°°°°°°°°°°°°°°°°°°°°°");
                System.out.println("\t°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

                reglamentos = paginador.paginarDocumento(reglamentos, numeroPagina, ConstantesTipoPaginas.reglamentoPlan, plantilla, confPortada, numeroTotalPaginas, fechaGeneracion, confGenesis, confProduccion, confInternet, confDiagramacion, confReglamentos);
                PdfReader reader1 = new PdfReader(reglamentos.toByteArray());
                copy.addPage(copy.getImportedPage(reader1, 1));
                numeroPagina += reader1.getNumberOfPages();

            }
            System.out.println("°°°°°°°°°°°°°°°°°°VA A SALIR DEL TRY DE ENSAMBLADOR DOC°°°°°°°°°°°°°°°°°°°°");
            document1.close();
        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°SALIO DE ENSAMBLAR DOC********°°°°°°°°°°°°°°°°°");
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        return bos;
    }

    /**
     * Método que se encarga de generar un String de la fecha y hora del Sistema
     * de acuerdo al formato requerido en el pdf.
     *
     * @return Cadena con la fecha y hora del sistema
     */
    private String obtenerFechaHora() {

        //1. Obtener Calendario actual
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DATE);
        int hour = currentDate.get(Calendar.HOUR);
        int minutes = currentDate.get(Calendar.MINUTE);
        int am_pm = currentDate.get(Calendar.AM_PM);

        //Formatear la fecha:
        String anio = year + "";
        String mes = "";
        if (month == 0) {
            mes = "ene";
        }
        if (month == 1) {
            mes = "feb";
        }
        if (month == 2) {
            mes = "mar";
        }
        if (month == 3) {
            mes = "abr";
        }
        if (month == 4) {
            mes = "may";
        }
        if (month == 5) {
            mes = "jun";
        }
        if (month == 6) {
            mes = "jul";
        }
        if (month == 7) {
            mes = "ago";
        }
        if (month == 8) {
            mes = "sep";
        }
        if (month == 9) {
            mes = "oct";
        }
        if (month == 10) {
            mes = "nov";
        }
        if (month == 11) {
            mes = "dic";
        }

        String dia = day + "";
        if (dia.length() == 1) {
            dia = "0" + dia;
        }

        String hora = hour + "";
        if (hour == 0) {
            hora = "12";
        }
        if (hora.length() == 1) {
            hora = "0" + hora;
        }

        String minutos = minutes + "";
        if (minutos.length() == 1) {
            minutos = "0" + minutos;
        }

        String am_pm2 = am_pm + "";
        if (am_pm2.compareTo("1") == 0) {
            am_pm2 = "PM";
        } else {
            am_pm2 = "AM";
        }

        String fecha_generacion = dia + "-" + mes + "-" + anio + " " + hora + ":" + minutos + " " + am_pm2;
        return fecha_generacion;

    }

}
