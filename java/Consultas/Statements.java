/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consultas;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author Irving
 */
public class Statements {
    static Conexion cn;
    static Connection con;
    //Declaramos los preparedStatements
    static PreparedStatement ps;

    String agregarTerminoSQL = "INSERT INTO terms\n" +
                                "VALUES (?, NULL)"; //Creamos la consulta

    String insertNewSectionSQL = "INSERT INTO sections\n" +
                                 "VALUES (?, NULL, NULL, NULL, NULL)";//Creamos la consulta

    String agregarRelacionTerminoSeccionSQL = "INSERT INTO frequencyTable\n" +
                                              "VALUES (?, ?, 1)"; //Creamos la consulta

    String buscarIDTerminoSQL = "select termID\n" +
                                "from terms\n" +
                                "where termName = ?";//Creamos la consulta


    String buscarRelacionTerminoSeccionSQL = "select *\n" +
                                             "from frequencyTable\n" +
                                             "where termID = ? AND docID = ?"; //Creamos la consulta

    String sumarContenidoARelacionSQL = "UPDATE frequencyTable \n" +
                                     "SET noTimesContained = noTimesContained + 1 \n" +
                                     "WHERE termID = ? AND docID = ?"; //Creamos la consulta


    String getTFSQL = "SELECT noTimesContained\n" +
                      "FROM frequencyTable\n" +
                      "WHERE termID=? AND docID = ?";//Creamos la consulta


    String documentosQueContienenSQL = "select docID\n" +
                                        "from frequencytable\n" +
                                        "where termID = ?"; //Creamos la consulta

    String getIDFSQL = "SELECT IDF \n " +
                        "FROM terms \n " +
                        "WHERE termID = ?"; //Creamos la consulta


    String getDocumentoSQL = "SELECT *\n" +
                             "FROM documents\n" +
                             "WHERE docID = ?";

    String getCalcularIDFSQL = "DELETE FROM terms\n" +
                                "WHERE termID not in (SELECT DISTINCT termID\n" +
                                "FROM frequencyTable)"
                                + "UPDATE terms\n" +
                                "SET idf = LOG10((SELECT COUNT (termID) FROM frequencyTable)/ \n" +
                                "(SELECT COUNT ( DISTINCT docID) FROM frequencyTable WHERE frequencyTable.termID = terms.termID))";

    String getTermnosDeDocSQL = "SELECT termID \n" +
                                "FROM frequencyTable\n" +
                                "WHERE docID = ?";

    String getTerminosConIDSQL = "select termName\n" +
                                 "from terms\n" +
                                 "where termID = ?";//Creamos la consulta

    String getInsertNewDocSQL = "INSERT INTO documents\n" +
                                "OUTPUT Inserted.docID\n" +
                                 "VALUES (?, ?, ?, ?, ?);";

    public Statements() {
        cn = new Conexion();
        try{
            con = cn.Conectar();//realizamos la conexion
        }
        catch (Exception e)
        {
            System.out.println("Error de conexion: " + e);
        }
    }

    public PreparedStatement getAgregarTermino(){
        try {ps = con.prepareStatement(agregarTerminoSQL);}
        catch (Exception e){};
        return ps;
    }
    public PreparedStatement getAgregarRelacionTerminoSeccion () {
        try {ps = con.prepareStatement(agregarRelacionTerminoSeccionSQL);}
        catch (Exception e){};
        return ps;
    }

    public PreparedStatement getBuscarIDTermino () {
        try {ps = con.prepareStatement(buscarIDTerminoSQL);}
        catch (Exception e){};
        return ps;
    }
    
    public PreparedStatement getBuscarRelacionTerminoSeccion () {
        try {ps = con.prepareStatement(buscarRelacionTerminoSeccionSQL);}
        catch (Exception e){};
        return ps;
    }
    public PreparedStatement getSumarContenidoARelacion () {
        try {ps = con.prepareStatement(sumarContenidoARelacionSQL);}
        catch (Exception e){};
        return ps;
    }
    
    public PreparedStatement getGetTF () {
        try {ps = con.prepareStatement(getTFSQL);}
        catch (Exception e){};
        return ps;
    }
    public PreparedStatement getDocumentosQueContienen(){
        try{ps = con.prepareStatement(documentosQueContienenSQL);}
        catch (Exception e){};
        return ps;
    }
    public PreparedStatement getGetIDF(){
        try{ ps = con.prepareStatement(getIDFSQL);}
        catch (Exception e){};
        return  ps;
    }
    public PreparedStatement getGetDocumento(){
        try{ps = con.prepareStatement(getDocumentoSQL);}
        catch (Exception e){};
        return ps;
    }
    public PreparedStatement getCalcularIDF(){
        try{ps = con.prepareStatement(getCalcularIDFSQL);}
        catch (Exception e){};
        return ps;
    }
    public PreparedStatement getGetTermnosDeDoc(){
        try{ps = con.prepareStatement(getTermnosDeDocSQL);}
        catch (Exception e){};
        return ps;
    }
    public PreparedStatement getGetTerminosConID(){
        try{ps = con.prepareStatement(getTerminosConIDSQL);}
        catch (Exception e){};
        return ps;
    }
    public PreparedStatement getInsertNewDoc()
    {
        try{ps = con.prepareStatement(getInsertNewDocSQL);}
        catch (Exception e){};
        return ps;
    }
}
