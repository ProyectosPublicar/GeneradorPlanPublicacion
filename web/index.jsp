<%-- 
    Document   : index
    Created on : 7/10/2011, 10:21:48 AM
    Author     : Administrador
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Generador Plan de Publicación</title>
    </head>
    <body>
        <center>
            <h1>PDF11 PLAN DE PUBLICACIÓN</h1>
            <h2>Prueba sout en CargarConfiguracion</h2>
        </center>
        <form action="Servlet_Plan_Publicacion" method="post">
            Identificador cot:
            <input type="text" name="identificador">
            Session ID:
            <input type="text" name="sessionid">
            Mostrar Valores:
            <input type="text" name="mostrar_valor">
            Tipo de Documento
            <input type="text" name="DocumentType">
            <input type="submit" name="enviar">
            
        </form>
   
    </body>
</html>
