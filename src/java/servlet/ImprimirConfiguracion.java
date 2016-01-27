/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import Configuracion.clases.ListaConfiguraciones;
import Configuracion.modelo.Componente;
import Configuracion.modelo.Configuracion;
import Configuracion.modelo.Seccion;
import Configuracion.modelo.TipoPagina;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class ImprimirConfiguracion {
    
    public void imprimirConfiguracion(List<Configuracion> listaConfiguraciones){
        
        for (int i=0; i<listaConfiguraciones.size();i++){
                    Configuracion miConf = listaConfiguraciones.get(i);
                    System.out.println("*******************************************");
                    System.out.println("Identificador: "+miConf.getIdentificador());
                    System.out.println("Nombre: "+miConf.getNombre());
                    System.out.println("Descripcion: "+miConf.getDescripcion());
                    System.out.println("@@@@@@@ PAGINAS @@@@@@@");
                    for (int j=0; j<miConf.getPaginasConfiguracion().size();j++){
                        TipoPagina tp = miConf.getPaginasConfiguracion().get(j);
                        System.out.println("@@@@@@@@@@@@@@@@@@");
                        System.out.println("Identificador: "+tp.getCodigo());
                        System.out.println("Nombre: "+tp.getNombre());
                        System.out.println("Plantilla: "+tp.getArchivo_plantilla());
                        System.out.println("!!!!!!SECCIONES!!!!!!!!");
                        for(int k=0; k<tp.getSeccionesPagina().size();k++){
                            Seccion sec = tp.getSeccionesPagina().get(k);
                            System.out.println("!!!!!!!!!!!");
                            System.out.println("Código: "+sec.getCodigo());
                            System.out.println("Nombre: "+sec.getNombre());
                            System.out.println("Tipo Sección: "+sec.getTipo_seccion());
                            System.out.println("Coord X: "+sec.getCoordenada_inicio_x());
                            System.out.println("Coord Y: "+sec.getCoordenada_inicio_x());
                            System.out.println("Alto: "+sec.getAlto());
                            System.out.println("Ancho: "+sec.getAncho());
                            System.out.println("Fondo Rojo: "+sec.getColor_fondo_red());
                            System.out.println("Fondo Verde: "+sec.getColor_fondo_green());
                            System.out.println("Fondo Azul: "+sec.getColor_fondo_blue());
                            System.out.println("??????COMPONENTES???????");
                            for (int w=0; w<sec.getComponentesSeccion().size(); w++){
                                Componente comp = sec.getComponentesSeccion().get(w);
                                System.out.println("Código: "+comp.getCodigo());
                                System.out.println("Nombre: "+comp.getNombre());
                                System.out.println("Alto: "+comp.getAlto());
                                System.out.println("Ancho: "+comp.getAncho());
                                System.out.println("Atributo: "+comp.getAtributo());
                                System.out.println("Objeto"+comp.getObjeto());
                                System.out.println("Descripcion: "+comp.getDescripcion());
                                System.out.println("Label: "+comp.getLabel());
                                System.out.println("Color Fondo Rojo: "+comp.getColor_fondo_red());
                                System.out.println("Color Fondo Azul: "+comp.getColor_fondo_blue());
                                System.out.println("Color Fondo Green: "+comp.getColor_fondo_green());
                                System.out.println("Color Fuente Rojo: "+comp.getColor_fuente_red());
                                System.out.println("Color Fuente Azul: "+comp.getColor_fuente_blue());
                                System.out.println("Color Fuente Green: "+comp.getColor_fuente_green());
                                System.out.println("Tipo Fuente: "+comp.getTipoFuente());
                                System.out.println("Tamaño Fuente: "+comp.getTamano_fuente());
                                System.out.println("Coordenada Escribir X: "+comp.getCoordenada_escribir_x());
                                System.out.println("Coordenada Escribir Y: "+comp.getCoordenada_escribir_y());
                                System.out.println("Coordenada Inicial X: "+comp.getCoordenada_inicio_x());
                                System.out.println("Coordenada Inicial Y: "+comp.getCoordenada_inicio_y());
                                System.out.println("Es Dinamico: "+comp.getEsDinamico());
                                System.out.println("??????COMPONENTES???????");
                            }
                            
                            System.out.println("!!!!!!SECCIONES!!!!!!!!");
                        }
                        System.out.println("@@@@@@@@@@@@@@@@@@");
                    }
                    System.out.println("*******************************************");
                }
    }

}
