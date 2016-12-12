/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import Bean.Document;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author Irving
 */
public class BuscarDocumentos extends HttpServlet {
    XMLReader xmlReader = new XMLReader();
    ModeloVectorial modeloVectorial = new ModeloVectorial();
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
       
        System.out.println("Consiguiendo archivos...");
        //Obtenemos los terminos de la consulta:
        String [] terminosDeConsulta = request.getParameter("consulta").split(" ");
         //Eliminamos los signos de los terminos de la consulta
        for(int i=0; i<terminosDeConsulta.length; i++){terminosDeConsulta[i] = xmlReader.eliminarSignos(terminosDeConsulta[i]);}
        //Eliminamos los Stop Words
        terminosDeConsulta = xmlReader.eliminarPreposiciones(terminosDeConsulta);
        //Conseguimos los documentos con TFIDF/Coseno del Ángulo
        List<Document> listaLigadaResultados = modeloVectorial.obtenerDocumentosReelevantes(terminosDeConsulta);
         
        //Desplegamos los resultados en una tabla 
        try (PrintWriter out = response.getWriter()) {
             out.println("<table class=tablaMetadatos>");
             if(listaLigadaResultados!=null)
            {
                for(int i=0; i<listaLigadaResultados.size(); i++)
                {
                    out.println("<tr id="+listaLigadaResultados.get(i).getNumeroDeDocumento()+" onClick=mostrarDocumento(this.id)>");
                    out.println("<td>"+listaLigadaResultados.get(i).getTitle()+"</td>");
                    out.println("</tr>");
                }
            }
             out.println("</table>");
            //Ocultamos el modal de esperar 
            out.println("<script> waitingDialog.hide(); </script>");
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
