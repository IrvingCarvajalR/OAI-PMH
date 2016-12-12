/*
 * Clase para obtener la informacion del archivo de propiedades en donde se encuentra la informacion 
 * para accesar a la base de datos y la direccion ip del active directory
 */
package Consultas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataHarverstingProject {
    //ubicacion del archivo de propiedades
    //public static String PROPERTIES = "C:\\CLocaliza-T\\LocalizaT.properties";
    public static String PROPERTIES = "C:\\Users\\Irving\\Documents\\NetBeansProjects\\DataHarvestingProject\\DataHarvestingProject.properties";
    //public static String PROPERTIES = "F:\\CLocaliza-T\\LocalizaT.properties";

    public static String USERNAME = null; //usuario para realizar la conexion con la base de datos 
    public static String PASSWORD = null; //password para realizar la conexion  con la bse de datos
    public static String CLASSNAME = null; // clase para ser la conexion a la base de 
    
    public static String URL = null; // url para realizar la conexion a la base de datos
    public static String IP = null; //ip para la conexion con active directory
    
    public String getUsername() throws FileNotFoundException, IOException {
        // Creamos un Objeto de tipo Properties
        Properties propiedades = new Properties();
        //Cargamos el archivo desde la ruta especificada
        propiedades.load(new FileInputStream(PROPERTIES));
        //Obtenemos los parametros definidos en el archivo
        USERNAME = propiedades.getProperty("USERNAME");
        return USERNAME;
    }
    
    public String getPassword() throws FileNotFoundException, IOException {
        // Creamos un Objeto de tipo Properties
        Properties propiedades = new Properties();
        //Cargamos el archivo desde la ruta especificada
        propiedades.load(new FileInputStream(PROPERTIES));
        //Obtenemos los parametros definidos en el archivo
        PASSWORD = propiedades.getProperty("PASSWORD");
        return PASSWORD;
    }

    public String getUrl() throws FileNotFoundException, IOException {
        // Creamos un Objeto de tipo Properties
        Properties propiedades = new Properties();
        //Cargamos el archivo desde la ruta especificada
        propiedades.load(new FileInputStream(PROPERTIES));
        //Obtenemos los parametros definidos en el archivo
        URL = propiedades.getProperty("URL");
        return URL;
    }

    public String getClassname() throws FileNotFoundException, IOException {
        // Creamos un Objeto de tipo Properties
        Properties propiedades = new Properties();
        //Cargamos el archivo desde la ruta especificada
        propiedades.load(new FileInputStream(PROPERTIES));
        //Obtenemos los parametros definidos en el archivo
        CLASSNAME = propiedades.getProperty("CLASSNAME");
        return CLASSNAME;
    }

    public String getIp() throws FileNotFoundException, IOException {
        // Creamos un Objeto de tipo Properties
        Properties propiedades = new Properties();
        //Cargamos el archivo desde la ruta especificada
        propiedades.load(new FileInputStream(PROPERTIES));
        //Obtenemos los parametros definidos en el archivo
        IP = propiedades.getProperty("IP");
        return IP;
    }  
}
