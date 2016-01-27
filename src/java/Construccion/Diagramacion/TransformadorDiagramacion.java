package Construccion.Diagramacion;

import java.util.List;
import javax.xml.bind.JAXBElement;
import parametros_plan_salesforce.AdditionalLineInfo;
import parametros_plan_salesforce.AssemblageInfo;
import parametros_plan_salesforce.ListInfo;
import parametros_plan_salesforce.ProductionInfo;
import web_service_diagramacion.DiagramListRequest;
import web_service_diagramacion.DiagramProdAdvRequest;
import web_service_diagramacion.LineasTextoDTO;
import web_service_diagramacion.ProposalListDTO;
import web_service_diagramacion.ProposalProductionAdvertlineDTO;

/**
 *
 * @author Administrador
 */
public class TransformadorDiagramacion {

    public DiagramProdAdvRequest transformarObjetoProduccionSalesforceDiagramacion(ProductionInfo elementoLista){
        
        DiagramProdAdvRequest miRequest = new DiagramProdAdvRequest();
        ProposalProductionAdvertlineDTO proposal= new ProposalProductionAdvertlineDTO();

        try{
            
            //Clasificación
            JAXBElement<String> clasificacionJ = elementoLista.getClasification();
            if (clasificacionJ != null){
                String clasificacion = clasificacionJ.getValue();
                proposal.setClasificacion(clasificacion);
            }

            //ID Inserción
            JAXBElement<String> idInsercionJ = elementoLista.getInsercionName();
            if (idInsercionJ != null){
                String insercion = idInsercionJ.getValue();
                proposal.setIdInsercion(insercion);
            }

            //Código del Producto
            JAXBElement<String> cpnproductoJ = elementoLista.getProductCode();
            if (cpnproductoJ != null){
                String cpn_producto = cpnproductoJ.getValue();
                proposal.setCodigoCpnProducto(cpn_producto);
            }

            //Código de Parte de Producto
            JAXBElement<String> cpnparteproductoJ = elementoLista.getProductPartCode();
            if (cpnparteproductoJ != null){
               String cpn_parte_producto = cpnparteproductoJ.getValue();
               proposal.setCodigoCpnParteProducto(cpn_parte_producto);
            }

            //Referencia del Producto
            JAXBElement<String> referenciaJ = elementoLista.getReferenceCode();
            if (referenciaJ != null){
                String referencia = referenciaJ.getValue();
                proposal.setCodReferencia(referencia);
            }
            
            
            List<AdditionalLineInfo> additionalLines = elementoLista.getAdditionalLines();
            List<web_service_diagramacion.LineaAvisoDTO> listaAvisosNew = proposal.getLineaAvisoSet();

            
            //Copiar las líneas respectivas.
            for (int r=0; r<additionalLines.size(); r++){
                AdditionalLineInfo additionalLineInfo = additionalLines.get(r);
                web_service_diagramacion.LineaAvisoDTO lineaNew = new web_service_diagramacion.LineaAvisoDTO();

                if (additionalLineInfo != null){
                    JAXBElement<String> consecutivoJ = additionalLineInfo.getConsecutive();
                    if (consecutivoJ != null){
                    String consecutivo = consecutivoJ.getValue();
                    
                    try{
                        Integer intConsecutivo = new Integer(consecutivo);
                        lineaNew.setConsecutivo(intConsecutivo.intValue());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                    }
                    
                    
                    List<parametros_plan_salesforce.AssemblageInfo> listaMontajeOld = additionalLineInfo.getAssemblageList();
                    List<web_service_diagramacion.MontajeDTO>listaMontajeNew = lineaNew.getMontajeSet();
                    
                    for (int s=0; s< listaMontajeOld.size(); s++){
                        AssemblageInfo montajeOld = listaMontajeOld.get(s);
                        web_service_diagramacion.MontajeDTO montajeNew = new web_service_diagramacion.MontajeDTO();
                        
                        //Código de la Fuente.
                        JAXBElement<String> codFuenteJ = montajeOld.getFontCode();
                        if (codFuenteJ!= null){
                            String codFuente = codFuenteJ.getValue();
                            Integer codFuenteInt = new Integer (codFuente);
                            montajeNew.setCodigoFuente(codFuenteInt.intValue());
                        }
                        
                        //Consecutivo del Montaje
                        JAXBElement<String> consMontajeJ = montajeOld.getConsecutive();
                        if (consMontajeJ!= null){
                           String consMontaje = consMontajeJ.getValue(); 
                           Integer consMontajeInt = new Integer (consMontaje);
                           montajeNew.setConsMontaje(consMontajeInt.intValue());
                        }
                        
                        //Interlineado
                        JAXBElement<String> interlineadoJ = montajeOld.getInterline();
                        if (interlineadoJ != null){
                           String interlineado = interlineadoJ.getValue();
                           Integer interlineadoInt = new Integer (interlineado);
                           montajeNew.setInterlineado(interlineadoInt.intValue());
                        }
                        
                        //Márgen a la derecha
                        JAXBElement<String> derechaJ = montajeOld.getRightMargin();
                        if (derechaJ!= null){
                           String derecha = derechaJ.getValue();
                           Integer derechaInt = new Integer(derecha);
                           montajeNew.setMargenDerecha(derechaInt);
                        }
                        
                        //Márgen a la izquierda
                        JAXBElement<String> izquierdaJ = montajeOld.getLeftMargin();
                        if (izquierdaJ!= null){
                           String izquierda = izquierdaJ.getValue();
                           Integer izquierdaInt = new Integer(izquierda);
                           montajeNew.setMargenIzquierda(izquierdaInt);
                        }
                        
                        //Tamaño de la fuente
                        JAXBElement<String> tamanoJ = montajeOld.getSize();
                        if (tamanoJ!= null){
                           String tamano = tamanoJ.getValue();
                           Integer tamanoInt = new Integer(tamano);
                           montajeNew.setTamano(tamanoInt);
                        }
                        
                        //Condensación de la fuente
                        JAXBElement<String> condensacionJ = montajeOld.getCondensation();
                        if (condensacionJ!= null){
                           String condensacion = condensacionJ.getValue();
                           montajeNew.setCondensacion(condensacion);
                        }
                        
                        //Justificación
                        JAXBElement<String> justificacionJ = montajeOld.getJustification();
                        if (justificacionJ!= null){
                           String justificacion = justificacionJ.getValue();
                           montajeNew.setJustificacion(justificacion);
                        }
                           
                        //Texto
                        JAXBElement<String> textoJ = montajeOld.getText();
                        if (textoJ!= null){
                           String texto = textoJ.getValue();
                           montajeNew.setText(texto);
                        }
                        
                        //Añadirlo a la lista de Montajes
                        listaMontajeNew.add(montajeNew);
                    }
                    
                    listaAvisosNew.add(lineaNew);
                } 
                
            }
            miRequest.setAvisoProdDTO(proposal);
        
        
            
             
            
        }catch(Exception e){
            e.printStackTrace();
            
        }
        
        
        return miRequest;
    }
  
    
        public DiagramListRequest transformarObjetoProduccionSalesforceDiagramacionListas(ListInfo elementoLista){
        
       
        DiagramListRequest miRequest = new DiagramListRequest();
        ProposalListDTO proposal= new ProposalListDTO();

        try{
            
            //Clasificación
            JAXBElement<String> clasificacionJ = elementoLista.getClasification();
            if (clasificacionJ != null){
                String clasificacion = clasificacionJ.getValue();
                proposal.setClasificacion(clasificacion);
            }
            
            //Código Aviso Logo
            JAXBElement<String> codigoAvisoLogoJ = elementoLista.getSketchNumber();
            if (codigoAvisoLogoJ != null){
                String codigoAvisoLogo = codigoAvisoLogoJ.getValue();
                proposal.setCodigoAvisoLogo(codigoAvisoLogo);
            }
            
            //Código de Parte de Producto
            JAXBElement<String> cpnparteproductoJ = elementoLista.getProductPartCode();
            if (cpnparteproductoJ != null){
               String cpn_parte_producto = cpnparteproductoJ.getValue();
               proposal.setCodigoCpnParteProducto(cpn_parte_producto);
            }
            

            //ID Inserción
            JAXBElement<String> idInsercionJ = elementoLista.getInsercionName();
            if (idInsercionJ != null){
                String insercion = idInsercionJ.getValue();
                proposal.setIdInsercion(insercion);
            }

            //Código del Producto
            JAXBElement<String> cpnproductoJ = elementoLista.getProductCode();
            if (cpnproductoJ != null){
                String cpn_producto = cpnproductoJ.getValue();
                proposal.setCodigoCpnProducto(cpn_producto);
            }

            //Reference Type
            JAXBElement<String> referenceTypeJ = elementoLista.getReferenceType();
            if (referenceTypeJ != null){
                String referenciaTipo = referenceTypeJ.getValue();
                proposal.setTipoLista(referenciaTipo);
            }
            
            //Tipo de Solicitud
            JAXBElement<String> solicitudTypeJ = elementoLista.getSolicitudeType();
            if (solicitudTypeJ != null){
                String solicitudTipo = solicitudTypeJ.getValue();
                proposal.setTipoSolicitud(solicitudTipo);
            }
            
            //Profesión
            JAXBElement<String> profesionJ = elementoLista.getProfession();
            if(profesionJ != null){
                String profesion = profesionJ.getValue();
                if (profesion==null){
                    proposal.setNombreProfesion("");
                }
                else{
                    proposal.setNombreProfesion(profesion);
                }
                
            }
            else{
                proposal.setNombreProfesion("");
            }
            
            
            //Código Instancia de Producto
            JAXBElement<String> instanciaJ = elementoLista.getCodeInstance();
            if(instanciaJ != null){
                String instancia = instanciaJ.getValue();
                proposal.setCodigoCpnInstanciaProducto(instancia);
            }
            
            //Sigla
            JAXBElement<String> siglaJ = elementoLista.getInitials();
            if(siglaJ != null){
                String sigla = siglaJ.getValue();
                if (sigla==null){
                    proposal.setSigla("");
                }
                else{
                    proposal.setSigla(sigla);
                }
                
            }
            else{
                proposal.setSigla("");
            }
                
                 
            
            List<AdditionalLineInfo> additionalLines = elementoLista.getAdditionalLines();
            List<web_service_diagramacion.LineasTextoDTO> listaAvisosNew = proposal.getLineasTextoSet();
 
            
            //Copiar las líneas respectivas.
            for (int r=0; r<additionalLines.size(); r++){
                AdditionalLineInfo additionalLineInfo = additionalLines.get(r);
                web_service_diagramacion.LineasTextoDTO lineaNew = new web_service_diagramacion.LineasTextoDTO();

                if (additionalLineInfo != null){
                    
                    //Consecutivo
                    JAXBElement<String> consecutivoJ = additionalLineInfo.getConsecutive();
                    if (consecutivoJ != null){
                    String consecutivo = consecutivoJ.getValue();
                    //System.out.println("consecutivo = "+consecutivo);
                    //consecutivo = "0000091483";
                    try{
                        Integer intConsecutivo = new Integer(consecutivo);
                        lineaNew.setConsecutivo(intConsecutivo.intValue());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                    }
                    
                   
                    
                    //Dirección
                    JAXBElement<String> direccionJ = additionalLineInfo.getAddress();
                    if (direccionJ != null){
                       String direccion = direccionJ.getValue();
                       lineaNew.setDireccion(direccion);
                    }
                    
                    //Código de Referencia
                    JAXBElement<String> codReferenciaJ = additionalLineInfo.getReferenceCode();
                    if (codReferenciaJ != null){
                       String codReferencia = codReferenciaJ.getValue();
                       lineaNew.setCodigoReferencia(codReferencia);
                    }
                    
                    //Identado
                    JAXBElement<String> identadoJ = additionalLineInfo.getIndent();
                    if (identadoJ != null){
                       String identado = identadoJ.getValue();
                       try{
                           Integer identadoInt = new Integer(identado);
                           lineaNew.setIdentado(identadoInt);
                       }
                       catch(Exception e){
                           
                       }
                       
                    }
                    
                    //Prefijo
                    JAXBElement<String> prefijoJ = additionalLineInfo.getPrefix();
                    if (prefijoJ != null){
                       String prefijo = prefijoJ.getValue();
                       lineaNew.setPrefijo(prefijo);
                    }
                    
                    //Primer Apellido
                    JAXBElement<String> apellido1J = additionalLineInfo.getLastname1();
                    if (apellido1J != null){
                       String apellido1 = apellido1J.getValue();
                       lineaNew.setPrimerApellido(apellido1);
                    }
                    
                    //Segundo Apellido
                    JAXBElement<String> apellido2J = additionalLineInfo.getLastname2();
                    if (apellido2J != null){
                       String apellido2 = apellido2J.getValue();
                       lineaNew.setSegundoApellido(apellido2);
                    }
                    
                    //Primer Nombre
                    JAXBElement<String> nombre1J = additionalLineInfo.getFirstname();
                    if (nombre1J != null){
                       String nombre1 = nombre1J.getValue();
                       lineaNew.setPrimerNombre(nombre1);
                    }
                    
                    //Segundo Nombre
                    JAXBElement<String> nombre2J = additionalLineInfo.getSecondname();
                    if (nombre2J != null){
                       String nombre2 = nombre2J.getValue();
                       lineaNew.setSegundoNombre(nombre2);
                    }
                    
                    //Teléfono
                    JAXBElement<String> telefonoJ = additionalLineInfo.getPhone();
                    if (telefonoJ != null){
                       String telefono = telefonoJ.getValue();
                       lineaNew.setTelefono(telefono);
                    }
                    
                     //Tipo de Prefijo
                    JAXBElement<String> tipoPrefijoJ = additionalLineInfo.getPrefixType();
                    if (tipoPrefijoJ != null){
                       String tipoPrefijo = tipoPrefijoJ.getValue();
                       lineaNew.setTipoPrefijo(tipoPrefijo);
                    }
                           
                }
                listaAvisosNew.add(lineaNew);
                   
                } 
                
           
            miRequest.setListaDTO(proposal);
        
          
             
            
        }catch(Exception e){
            e.printStackTrace();
            
        }
        
       
        return miRequest;
    }
    
}
