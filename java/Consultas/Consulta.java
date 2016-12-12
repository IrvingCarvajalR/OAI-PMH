package Consultas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Consulta {
    Statements statements = new Statements();
    
    //Consulta para ingresar una nueva seccion a la BD
     public int insertNewDocument(String title, String creator, String description, String publisher, String identifier, PreparedStatement ps) {
         Integer recordID = null;
         try {
             ps.setString(1, title);
             ps.setString(2, creator); 
             ps.setString(3, description); 
             ps.setString(4, publisher);
             ps.setString(5, identifier); 
             ResultSet rs = ps.executeQuery();//Ejecutamos la consulta
             while(rs.next())
             {
                 recordID = rs.getInt("docID");
             }
         } catch (Exception e) {
             System.out.println("Excepcion al insertar sección:" + e);
         }
         return recordID;
     }

     //Consulta para ingresar un nuevo termino a la base de datos
     public  boolean agregarTermino(String newTerm, int numeroDeDocumento, PreparedStatement statement) {
         boolean resultado = false;
         //Primero nos aseguramos de que el termino todavia no está  incluido en la BD
         boolean terminoRepetido = false;
         int terminoID = buscarIDTermino(newTerm);
         //System.out.println("ID de termino: " + newTerm + " = " + terminoID);
             if(terminoID>0)
                 terminoRepetido = true;

         //Si aún no se encuentra en la BD lo agregamos
         if(!terminoRepetido)
         {
             try {
                 statement.setString(1, newTerm); //agregamos el nombre del nuevo termino a la consulta
                 int resul = statement.executeUpdate();//Ejecutamos la consulta
                 //System.out.println("Resultado de agregar nuevo termino: " + resul);
                 if (resul > 0) {
                     resultado = true;
                     terminoID = buscarIDTermino(newTerm);
                     //Agregamos la relacion entre el termino y la sección
                     agregarRelacionTerminoSeccion(terminoID, numeroDeDocumento);
                 }
                 return resultado;
             } catch (Exception e) {
                 System.out.println("Excepcion al agregar termino: " + e);
             }
         }
         else
         {
             /*Si el termino ya existe pero no existe la relación
             * entre ese termino y  el documento se genera una nueva relación*/
             boolean existeRelacion = buscarRelacionTerminoSeccion(terminoID, numeroDeDocumento);
             //System.out.println("Existe relación para este termino? " + existeRelacion);
             if (!existeRelacion)
                 agregarRelacionTerminoSeccion(terminoID, numeroDeDocumento);
             else //Sumamos uno a la relación existente entre un termino y una sección
                 sumarContenidoARelacion(terminoID, numeroDeDocumento);
         }
         return resultado;
     }//End insertNewTerm

     //Consulta que regresa el ID de un termino ya contenido en la BD
     public  int buscarIDTermino(String termino)
     {
         PreparedStatement ps;
         int IDTermino = 0;
         ResultSet rs;
         String sql = "select termID\n" +
                      "from terms\n" +
                      "where termName = ?";//Creamos la consulta
         try {
             ps = statements.getBuscarIDTermino();
             ps.setString(1, termino); //agregamos el termino a la consulta
             rs = ps.executeQuery();//Ejecutamos la consulta
              while (rs.next()) {
                  IDTermino = Integer.parseInt(rs.getString("termID"));
              }
             try{ps.close();}
             catch (Exception e){}
         } catch (Exception e) {
             System.out.println("Excepcion al buscar ID de termino: " + e);
         }
         return IDTermino;
     }

     //Consulta para agregar una nueva relacion a la BD entre un termino y una seccion
     public  boolean agregarRelacionTerminoSeccion(int terminoID, int numeroDeDocumento)
     {
         boolean resultado = false;
         PreparedStatement ps;
         //System.out.println("termID: " + terminoID + ", pageID: " + numeroDeDocumento);

         try {
             ps = statements.getAgregarRelacionTerminoSeccion();
             ps.setInt(1, terminoID); //agregamos el id del  termino a la consulta
             ps.setInt(2, numeroDeDocumento); //agregamos el id de la sección a la consulta
             int resul = ps.executeUpdate();//Ejecutamos la consulta
             if (resul > 0)
                 resultado = true;
             return resultado; //devolvemos el nombre del area
         } catch (Exception e) {
             System.out.println("Excepcion al agregar relación: " + e);
         }
         return resultado;
     }
 
    //Consulta para localizar el numero de veces que existe una relación
    public boolean buscarRelacionTerminoSeccion(int terminoID, int numeroDeDocumento)
    {
        boolean resultado = false;
        int numeroDeRelaciones = 0;
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = statements.getBuscarRelacionTerminoSeccion();
            ps.setInt(1, terminoID); //agregamos el id del  termino a la consulta
            ps.setInt(2, numeroDeDocumento); //agregamos el id de la sección a la consulta
            rs = ps.executeQuery();//Ejecutamos la consulta
            while (rs.next()) {
                numeroDeRelaciones++;
            }
            //System.out.println("Num de relaciones: " + numeroDeRelaciones);
            if(numeroDeRelaciones>0)
            {
                resultado  = true;
            }
            return resultado; //devolvemos el nombre del area
        } catch (Exception e) {
            System.out.println("Excepcion al buscar relación entre termino y sección: " + e);
        }
        return resultado;
    }
    
    //Consulta para sumar uno a las veces que está contenido un termino en una relación
    public  boolean sumarContenidoARelacion(int terminoID, int numeroDeDocumento)
    {
        boolean resultado = false;
        PreparedStatement ps;

        try {
            ps = statements.getSumarContenidoARelacion();
            ps.setInt(1, terminoID); //agregamos el id del  termino a la consulta
            ps.setInt(2, numeroDeDocumento); //agregamos el id de la sección a la consulta
            int resul = ps.executeUpdate();//Ejecutamos la consulta
            //System.out.println("Resultado de sumar a relacion " + resul);
            if (resul > 0)
                resultado = true;
            return resultado; //devolvemos el nombre del area
        } catch (Exception e) {
            System.out.println("Excepcion al sumar contenido a relación: " + e);
        }
        return resultado;
    }
    
    
    //Consulta para obtener el titulo, autor y abstract de un documento en la BD
    public String [] getPartesDeDocumento(int numeroDeDocumento, PreparedStatement ps)
    {
        String[] partes = new String[5];
        ResultSet rs;
        try {
            ps.setInt(1, numeroDeDocumento); //agregamos el  termino a la consulta
            rs = ps.executeQuery();//Ejecutamos la consulta
            while (rs.next()){
                try{
                    partes[0] = rs.getString("title");
                    partes[1] = rs.getString("creator");
                    partes[2] = rs.getString("descript");
                    partes[3] = rs.getString("publisher");
                    partes[4] = rs.getString("identifier");
                }
                catch (Exception e)
                {
                }
            }
        } catch (Exception e) {
            System.out.println("Excepcion al obtener partes del documento : " + e);
        }
        return partes;
    }
    
    //Consulta para calcular el IDF de todos los terminos
    public boolean calcularIDF ()
    {
        boolean resultado = false;
        PreparedStatement ps = statements.getCalcularIDF();
        try {
            int rs = ps.executeUpdate();//Ejecutamos la consulta
            if (rs>0)
                resultado = true;
        } catch (Exception e) {
            System.out.println("Excepcion al calcular IDF: " + e);
        }
        return resultado;
    }
    
    //Consulta para obtener el IDF de un termino dado su ID
    public float getIDF (int termID, PreparedStatement ps)
    {
        float IDF = 0;
        ResultSet rs;
        try {
            ps.setInt(1, termID); //agregamos el  termino a la consulta
            rs = ps.executeQuery();//Ejecutamos la consulta
            while (rs.next()){
                try{
                    IDF = (Float.parseFloat(rs.getString("IDF")));
                    //System.out.println("Se consigue IDF = " +  IDF);
                }
                catch (Exception e)
                {
                }
            }
        } catch (Exception e) {
            System.out.println("Excepcion al obtener IDF: " + e);
        }
        return IDF;
    }
    
    //Consulta para recuperar el id de los documentos que contiene cierto termino
    public List<Integer> documentosQueContienen (int termID, PreparedStatement ps)
    {
        List<Integer> documentosQueContienenTermino = new ArrayList<>();
        ResultSet rs;
        try {
            ps.setInt(1, termID); //agregamos el  termino a la consulta
            rs = ps.executeQuery();//Ejecutamos la consulta
            while (rs.next()){
                try{
                    documentosQueContienenTermino.add(Integer.parseInt(rs.getString("docID")));
                }
                catch (Exception e)
                {
                }
            }
        } catch (Exception e) {
            System.out.println("Excepcion al obtener documentosQueContienen un termino: " + e);
        }
        return documentosQueContienenTermino;
    }
    
    //Consulta para obtener el Term Frequency de un termino en un documento
    public static int getTF(int terminoID, int documento, PreparedStatement ps)
    {
        Integer TF = 0;
        ResultSet rs;
        try {
            ps.setInt(1, terminoID); //agregamos el  termino a la consulta
            ps.setInt(2, documento); //agregamos el numero de documento a la consulta
            rs = ps.executeQuery();//Ejecutamos la consulta
            while (rs.next()){
                try{
                    TF = Integer.parseInt(rs.getString("noTimesContained"));
                }
                catch (Exception e)
                {
                }
            }
        } catch (Exception e) {
            System.out.println("Excepcion al buscar obtener TF: " + e);
        }
        //System.out.println("...");
        return TF;
    }
} 

