/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import Consultas.Statements;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Irving
 */
public class MostrarDocumento extends HttpServlet {
    
    Statements statements = new Statements();
    XMLReader xmlReader = new XMLReader();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Obtenemos el id del documento elegido por el usuario 
        int docID = Integer.parseInt(request.getParameter("docID"));
        //Hacemos un statement a la BD
        PreparedStatement ps = statements.getGetDocumento();
        //Conseguimos las partes del documentos
        String[] docPartes = xmlReader.getDocumento(docID, ps);
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<table class=tablaMetadatos>");
            out.println("<tr>"); out.println("<td>Title: "+docPartes[0]+"</td>"); out.println("</tr>");
            out.println("<tr>"); out.println("<td>Creator: "+docPartes[1]+"</td>"); out.println("</tr>");
            out.println("<tr>"); out.println("<td>Description: "+docPartes[2]+"</td>"); out.println("</tr>");
            out.println("<tr>"); out.println("<td>Publisher: "+docPartes[3]+"</td>"); out.println("</tr>");
            out.println("<tr>"); out.println("<td>Identifier: "+docPartes[4]+"</td>"); out.println("</tr>");
            out.println("</table>");
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
