/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.NodeList;
import Servlets.XMLReader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Irving
 */
public class CosecharURLbase extends HttpServlet {
    XMLReader xmlReader = new XMLReader();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
         //Definimos el verbo para recuperar los metadatos en formato Dublin Core
         String listRecords = "?verb=ListRecords&metadataPrefix=oai_dc";
         //Conseguimos el URL base proporcionado por el cliente en (request)
         String urlBase = request.getParameter("urlBase");
         //Hacemos que el XML sea leido 
         NodeList nList = xmlReader.leerArchivoXML(urlBase+listRecords);
            
        try (PrintWriter out = response.getWriter()) {
            //Si la lista de nodos es mayor a 0
            if(nList!=null)
            {
                //Imprimimos la tabla con los resultados de la cosecha
                //Header
                out.println("<table >");
                out.println("<tr>");
                out.println("<th> <center> Title </center> </th>");
                out.println("<th> <center> Creator </center> </th>");
                out.println("<th> <center> Description </center> </th>");
                out.println("<th> <center> Publisher </center> </th>");
                out.println("<th> <center> Identifier </center> </th>");
                out.println("</tr>");
                //Contenido
                for (int i = 0; i < nList.getLength(); i++) {
                    Node nNode = nList.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            if(eElement.getElementsByTagName("dc:title").item(0)!=null)
                            {
                                out.println("<tr>");
                                out.println("<td>"+eElement.getElementsByTagName("dc:title").item(0).getTextContent()+"</td>");
                                out.println("<td>"+eElement.getElementsByTagName("dc:creator").item(0).getTextContent()+"</td>");
                                out.println("<td>"+eElement.getElementsByTagName("dc:description").item(0).getTextContent().substring(0, 20)+"...</td>");
                                out.println("<td>"+eElement.getElementsByTagName("dc:publisher").item(0).getTextContent()+"</td>");
                                out.println("<td>"+eElement.getElementsByTagName("dc:identifier").item(0).getTextContent()+"</td>");
                                out.println("</tr>");
                            }
                    }
                }
                //Cerramos la tabla
                out.println("</table>");
            }
            else
            {
                out.println("No pudimos cosechar nada de la URL: " + urlBase);
            }
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
