package Conexion;

import Consultas.DataHarverstingProject;
import java.sql.*;

public class Conexion {

    public Connection Conectar() {
        DataHarverstingProject lo = new DataHarverstingProject();//accede al archivo de propiedades
        Connection cn2 = null; 
        try {
            Class.forName(lo.getClassname());
            cn2 = DriverManager.getConnection(lo.getUrl(), lo.getUsername(), lo.getPassword()); //conexion a la bd en sqlserver
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cn2;
    }

}
