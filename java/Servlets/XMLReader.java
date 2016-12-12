package Servlets;
/**
 *
 * @author Irving
 */
import Consultas.Consulta;
import Consultas.Statements;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/*Clase para obtener el archivo XML del URL proporcionado 
e ingresar su records a la BD*/
public class XMLReader {
    Consulta consulta = new Consulta();
    Statements statements = new Statements();
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    Document doc;
    NodeList nList;
    
    public XMLReader(){
        try{
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
        }
        catch(Exception e)
        {
            System.out.println("Excepción al iniciar XMLReader: " + e);
        }
    }
    
    //Método para leer un XML desde una pagina y vaciar los elementos a una BD
public NodeList leerArchivoXML(String url)
    {
        try {
            Document doc = dBuilder.parse(new URL(url).openStream());
            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            //Lista de nodos de tipo "record" 
            nList = doc.getElementsByTagName("record");
            //Un entero para manejar el ID del record que se está procesando con la BD
            int recordID;
            //Statements para conexion con BD
            PreparedStatement insertPS = statements.getInsertNewDoc();
            
            //Para cada record en el XML obtenemos los metadatos y los ingresamos a la BD
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if(eElement.getElementsByTagName("dc:title").item(0)!=null)
                    {
                        String title = eElement.getElementsByTagName("dc:title").item(0).getTextContent();
                        String creator = eElement.getElementsByTagName("dc:creator").item(0).getTextContent();
                        String description = eElement.getElementsByTagName("dc:description").item(0).getTextContent();
                        String publisher = eElement.getElementsByTagName("dc:publisher").item(0).getTextContent();
                        String identifier = eElement.getElementsByTagName("dc:identifier").item(0).getTextContent();

                        //Ingresamos el nuevo record a la BD obteniendo como respuesta su ID
                        recordID = consulta.insertNewDocument(title, creator, description, publisher, identifier, insertPS);
                        //Ingresamos los terminos del documento y alimentamos la tabla "FrequencyTable" 
                        agregarTerminos(title, recordID);
                        agregarTerminos(creator, recordID);
                        agregarTerminos(description, recordID);
                        agregarTerminos(publisher, recordID);
                        agregarTerminos(identifier, recordID);
                    }
                }
            }
            //Cuando acabamos de leer los documents, Recalculamos IDF de los terminos en la BD
            consulta.calcularIDF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
           return nList;
        }
    }

    //Método para agregar los terminos contenidos en una linea de texto
    public void agregarTerminos (String linea, int numeroDeDocumento)
    {
        //Dividimos la linea en palabras para poder ingresar termino por termino a la BD
        String[] terminosCrudos = linea.split("-|\\ ");
        //Eliminamos los signos no reelevantes que pueda contener los terminos
        for(int i=0; i<terminosCrudos.length; i++)
        {
            terminosCrudos[i] = eliminarSignos(terminosCrudos[i]);
        }
        //Eliminamos los terminos que son preposiciones o espacios en blanco
        String[] arregloDeTerminos = eliminarPreposiciones(terminosCrudos);

        //Ciclo para agregar todos los terminos a la BD
        PreparedStatement ps = statements.getAgregarTermino();
        for(int i = 0; i<arregloDeTerminos.length; i++)
        {
            if(arregloDeTerminos[i]!=" ")
                consulta.agregarTermino(arregloDeTerminos[i], numeroDeDocumento, ps);
        }
    }

    //Método para eliminar todos los puntos y comas
    public String eliminarSignos (String termino)
    {
        termino = termino.replace(".","");
        termino = termino.replace(",","");
        termino = termino.replace("-","");
        termino = termino.replace("(","");
        termino = termino.replace(")","");
        termino = termino.replace("[","");
        termino = termino.replace("]","");
        termino = termino.replace(":","");
        termino = termino.replace("'","");
        termino = termino.replace(";","");
        termino = termino.replace("?","");
        return termino;
    }

    //Método para eliminar todas las preposiciones de la linea
    public String[] eliminarPreposiciones(String [] terminosCrudos)
    {
        String [] preposiciones = {"", "The", "the", "in", "on", "at", "for", "of", "as", "what", "are", "there", "between", "and", "that", "is", "an", "from", "or", "to", "it"};
        for(int i=0; i<terminosCrudos.length; i++)
        {
            for(int j =0; j<preposiciones.length; j++)
            {
                if(terminosCrudos[i].equals(preposiciones[j]))
                    terminosCrudos[i]=" ";
            }
        }
        ArrayList <String> terminosCrudosLista = new ArrayList<String>();
        for(int i=0; i<terminosCrudos.length;i++)
        {
            if(!(terminosCrudos[i]==" "));
                terminosCrudosLista.add(terminosCrudos[i]);
        }
        String[] terminosCrudosFinal = new String[terminosCrudosLista.size()];
        for(int i=0; i<terminosCrudosLista.size(); i++)
            terminosCrudosFinal[i]=terminosCrudosLista.get(i);
        return eliminarEspacios(terminosCrudosFinal);
    }

    //Método para leer un documento especifico de la colección
    public String[] getDocumento(int documentoID, PreparedStatement ps)
    {
        //Arreglo para las partes del doc 0.- url 1.-titulo 2.-contenido
        String [] datos = consulta.getPartesDeDocumento(documentoID, ps);
        return datos;
    }

    //Método para eliminar los espacios blancos de un arreglo
    public String[] eliminarEspacios(String[] arreglo)
    {
        ArrayList<String> lista = new ArrayList<>();
        for(int i=0; i<arreglo.length; i++)
        {
            if(!arreglo[i].equals(" "))
                lista.add(arreglo[i]);
        }
        String[] terminos = new String[lista.size()];
        for(int i=0; i<lista.size(); i++)
            terminos[i]=lista.get(i);

        return terminos;
    }
}