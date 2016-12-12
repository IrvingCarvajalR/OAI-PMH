package Servlets;

import Bean.Document;
import Consultas.Consulta;
import Consultas.Statements;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Irving on 05/09/2016.
 */
public class ModeloVectorial{
    //Ocuparemos la clase LecturaArchivo para recuperar los datos de los archivos reelevantes
    XMLReader lecturaArchivo = new XMLReader();
    //Ocuapermos la clase de Statements
    Statements statements = new Statements();
    PreparedStatement ps;
    //Ocuparemos la clase Consulta
    Consulta consulta = new Consulta();
    //Lista de los documentos reelevantes encontrados
    List<Document> rankedResultSet;
    //Arreglo de los ID's de los terminos de la consulta
    int [] terminosIDs;
    //Documentos que por lo menos tienen una palabra de la consulta
    List<Integer> documentosAevaluar = new ArrayList<>();
    //Lista de todos los terminos diferentes que tiene la consulta
    List<Integer> vectorConsulta = new ArrayList<>();
    //Recibimos el vector de la consulta Q y llevamos a cabo el modelo
    public List<Document> obtenerDocumentosReelevantes(String [] vectorInicialTerminos)
    {
        //Incializamos la lista para guardar los resultados
        rankedResultSet = new ArrayList<>();
        System.out.println("Vector de Terminos: ");
        for(int i=0; i<vectorInicialTerminos.length; i++)
            System.out.print("[" + vectorInicialTerminos[i] + "],");
        /*
        En primer lugar, obtenemos todos los documentos que por lo menos
        contienen una palabra de la consulta
         */

        //Buscamos los ID's de los terminos
        terminosIDs = new int[vectorInicialTerminos.length];
        
        //Eliminamos
        for (int i = 0; i<vectorInicialTerminos.length; i++)
            terminosIDs[i]=consulta.buscarIDTermino(vectorInicialTerminos[i]);

        //Obtenemos los documentos a evaluar (que por lo menos tienen 1 palabra de la consulta)
        documentosAevaluar = obtenerDocumentosAEvaluar(terminosIDs);
        Collections.sort(documentosAevaluar);
        System.out.println("Lista de docs a evaluar: ");
        System.out.println(documentosAevaluar);
        if(documentosAevaluar.size()>100)
            documentosAevaluar = documentosAevaluar.subList(0, 100);
        else if(documentosAevaluar.size()==0)
            return null;
        //Conseguimos el Vector Consulta = Vector de todos los elementos no repetidos
        vectorConsulta = eliminarDuplicados(terminosIDs);


        /*Ya podemos trabajar con TF e IDF pero solo lo harémos con
        los terminos que nos solicitan en la consulta ya que si realizamos
        el proceso para todos los terminos al final la multiplicacion  TF*IDF = 0
        para todos aquellos terminos que no estén incluidos en la consulta y por lo tanto
        no suman nada en el producto punto.
         */

        //TERM FREQUENCY TABLE  (TF)
        int[][] tablaTFs = new int[documentosAevaluar.size()+1][vectorConsulta.size()];
        tablaTFs = llenarTablaTFs (tablaTFs);
        System.out.println("Tabla TF");
        imprimirTabla(tablaTFs);

        //INVERSE DOCUMENT FREQUENCY (IDF)
        float[] tablaIDF = new float[vectorConsulta.size()];
        PreparedStatement ps = statements.getGetIDF();
        for(int i = 0; i<vectorConsulta.size(); i++) {
            tablaIDF[i] = consulta.getIDF(vectorConsulta.get(i), ps);
        }
        try{ps.close();}
        catch (Exception e){}


        //TF * IDF o Asignación de pesos alternativo
        float[][] tablaTFIDF = multiplicarTFIDF(tablaTFs, tablaIDF);
        System.out.println("Tabla de pesos para cada termino: ");
        imprimirTablaFloat(tablaTFIDF);

        //Vector de similitud  con Producto Punto o Coseno del Ángulo 
        float[] productoPunto = cosenoDelAngulo(tablaTFIDF);

        System.out.println("Vector de similitud: ");
        for(int i=0; i<productoPunto.length; i++)
        {
            System.out.print("[" + productoPunto[i] + "], ");
        }

        /*Por ultimo generamos los documentos que necesitamos mostrar
        ya con un ranking
         */
        for(int i=0; i<productoPunto.length; i++)
        {
            ps = statements.getGetDocumento();
            //Conseguimos el titulo, autor y contenido del documento
            String[] partes = lecturaArchivo.getDocumento(documentosAevaluar.get(i), ps);
            try{ps.close();}
            catch (Exception e){};
            //Creamos un objeto de tipo documento
            Document nuevoDocumento = new Document(documentosAevaluar.get(i), partes[0], partes[1], partes[2], partes[3], partes[4], productoPunto[i]);
            //Y por ultimo lo agregamos a la lista de documentos reelevantes
            rankedResultSet.add(nuevoDocumento);
        }

        //Por ultimo ordenamos la lista y la regresamos
        Collections.sort(rankedResultSet);
        return rankedResultSet;
    }


    //Metodo para obtener los documentos psoibles para evaluar
    public List<Integer> obtenerDocumentosAEvaluar (int [] terminosIDs)
    {
        List<Integer> documentosAevaluar = new ArrayList<>();
        ps = statements.getDocumentosQueContienen();
        //Guardamos en la lista los ID's de los documentos a evaluar
        for(int i = 0; i<terminosIDs.length; i++)
        {
            List<Integer> docs = consulta.documentosQueContienen(terminosIDs[i],ps);
            for (int j = 0; j < docs.size(); j++)
            {
                if(!documentosAevaluar.contains(docs.get(j)))
                    documentosAevaluar.add(docs.get(j));
            }
        }
        try{ps.close();}
        catch (Exception e){}

        return documentosAevaluar;
    }

    //Método para obtener el vectorConsulta (todos los terminos sin duplicar)
    public List<Integer> eliminarDuplicados(int [] terminosIDs)
    {
        List<Integer> vectorConsulta = new ArrayList<>();

        for(int i = 0; i<terminosIDs.length; i++)
        {
            if(!vectorConsulta.contains(terminosIDs[i]))
                vectorConsulta.add(terminosIDs[i]);
        }
        return vectorConsulta;
    }

    //Método para llenar la tabla de TFs para cada documento y en la ultima fila para la QUERY
    public int[][] llenarTablaTFs (int [][] tablaTFs)
    {
        PreparedStatement ps = statements.getGetTF();
        //Llenamos la tabla hasta la columna n-1
        for(int i=0; i<documentosAevaluar.size(); i++)
        {
            for(int j=0; j<vectorConsulta.size(); j++)
            {
                tablaTFs[i][j]= consulta.getTF(vectorConsulta.get(j), documentosAevaluar.get(i), ps);
            }
        }
        try{ps.close();}
        catch (Exception e){}
        //La ultima columna de la tabla de TFs es para la consulta
        for(int l=0; l<vectorConsulta.size(); l++)
        {
            int x=0;
            for(int i=0; i<terminosIDs.length; i++)
            {
                if(terminosIDs[i]==vectorConsulta.get(l))
                    x++;
            }
            tablaTFs[documentosAevaluar.size()][l]=x;
        }
        return tablaTFs;
    }

    //Método para multiplicar las tablas de TF e IDF
    public float[][] multiplicarTFIDF(int[][] TFs, float[] IDF)
    {
        float[][] TFIDF = new float[documentosAevaluar.size()+1][vectorConsulta.size()];
        for(int i=0; i<documentosAevaluar.size(); i++)
        {
            for(int j=0; j<vectorConsulta.size();j++)
            {
                TFIDF[i][j] = ((TFs[i][j])*IDF[j]);
            }
        }
        //La ultima columna se multiplica derecho
        for(int l=0; l<vectorConsulta.size(); l++)
        {
            TFIDF[documentosAevaluar.size()][l] =  ((TFs[documentosAevaluar.size()][l])*IDF[l]);;
        }
        return TFIDF;
    }

    //Método alternativo para obtener el peso de los terminos (TFIDF)
    public float[][] asignarPesosATerminos(int[][] TFs, float[] IDF)
    {
        float[][] TFIDF = new float[documentosAevaluar.size()+1][vectorConsulta.size()];
        for(int i=0; i<documentosAevaluar.size(); i++) //---Para cada documento
        {
            DecimalFormat decimalFormat = new DecimalFormat();
            float peso;
            double numerador = 0, denominador = 0;

            for(int j=0; j<vectorConsulta.size();j++) //Para cada termino
            {
                //Calculamos el numerador
                numerador = Math.log(TFs[i][j]+1)*IDF[j];
                //Calculamos el denominador con sumatoria
                for(int k=0; k<vectorConsulta.size(); k++)
                {
                    denominador = denominador + Math.pow((Math.log(TFs[i][k]+1) * IDF[k]),2);
                }
                //Calculamos el "TFIDF" alternativo y lo guardamos en la tabla TFIDF
                peso = Float.parseFloat(decimalFormat.format(numerador/denominador));
                TFIDF[i][j]=peso;
            }
        }
        //La ultima columna se multiplica derecho
        for(int l=0; l<vectorConsulta.size(); l++)
        {
            DecimalFormat decimalFormat = new DecimalFormat();
            float peso;
            double numerador = 0, denominador = 0;


                //Calculamos el numerador
                numerador = Math.log(TFs[documentosAevaluar.size()][l]+1)*IDF[l];
                //Calculamos el denominador con sumatoria
                for(int k=0; k<vectorConsulta.size(); k++)
                {
                    denominador = denominador + Math.pow((Math.log(TFs[documentosAevaluar.size()][k]+1) * IDF[k]),2);
                }
                //Calculamos el "TFIDF" alternativo y lo guardamos en la tabla TFIDF
                peso = Float.parseFloat(decimalFormat.format(numerador/denominador));
                TFIDF[documentosAevaluar.size()][l]=peso;
        }
        return TFIDF;
    }

    //Método para obtener el producto punto de cada documento con la consulta
    public float[] productoPunto(float [][] matrizTFIDF)
    {
        float[] productosPunto = new float[documentosAevaluar.size()];

        for(int i=0; i<documentosAevaluar.size(); i++)
        {
            float productoPunto = 0;
            for(int j=0; j<vectorConsulta.size(); j++)
            {
                productoPunto = productoPunto + ((matrizTFIDF[i][j])*(matrizTFIDF[documentosAevaluar.size()][j]));
            }
            productosPunto[i]=productoPunto;
            productoPunto=0;
        }
        return productosPunto;
    }

    /*Método para obtener la similitud de cada documento
    con la consulta medante el coseno del angulo*/
    public float[] cosenoDelAngulo(float [][] matrizTFIDF)
    {
        DecimalFormat decimalFormat = new DecimalFormat();
        float[] cosenosDelAngulo = new float[documentosAevaluar.size()];

        for(int i=0; i<documentosAevaluar.size(); i++) //----para cada documento
        {
            float cosenoDelAngulo = 0;
            double numerador = 0, denominadorA = 0, denominadorB = 0;
            //Calculamos el numerador
            for(int j=0; j<vectorConsulta.size(); j++) //----para cada termino
            {
                numerador = numerador + ((matrizTFIDF[i][j])*(matrizTFIDF[documentosAevaluar.size()][j]));
            }
            //Calculamos la sumatoria en deniminadorA
            for(int j=0; j<vectorConsulta.size(); j++) //----para cada termino
            {
                denominadorA = denominadorA + ((matrizTFIDF[i][j])*(matrizTFIDF[i][j]));
            }
            //Calculamos la sumtaoria en denominadorB
            for(int j=0; j<vectorConsulta.size(); j++) //----para cada termino
            {
                denominadorB = denominadorB + ((matrizTFIDF[documentosAevaluar.size()][j])*(matrizTFIDF[documentosAevaluar.size()][j]));
            }
            //Y recreamos la formula con la raiz cuadrada en los denominadores A Y B
            cosenosDelAngulo[i]=Float.parseFloat(decimalFormat.format(numerador/(Math.sqrt(denominadorA)*Math.sqrt(denominadorB))));
        }
        return cosenosDelAngulo;
    }


    //Imprimir tabla de TFs
    public void imprimirTabla(int [][] matriz)
    {
        for (int x=0; x < matriz.length; x++) {
            System.out.print("|");
            for (int y=0; y < matriz[x].length; y++) {
                System.out.print (matriz[x][y]);
                if (y!=matriz[x].length-1) System.out.print("\t");
            }
            System.out.println("|");
        }
    }

    //Imprimir tabla de TFs
    public void imprimirTablaFloat(float [][] matriz)
    {
        for (int x=0; x < matriz.length; x++) {
            System.out.print("|");
            for (int y=0; y < matriz[x].length; y++) {
                System.out.print (matriz[x][y]);
                if (y!=matriz[x].length-1) System.out.print("\t");
            }
            System.out.println("|");
        }
    }
}
