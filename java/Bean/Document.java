/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bean;

/**
 *
 * @author Irving
 */
public class Document implements Comparable<Document> {
   
    private int numeroDeDocumento;
    private String title;
    private String creator;
    private String description;
    private String publisher;
    private String identifier;
    private float gradoDeSimilitud;
    
    public Document(int numeroDeDocumento, String title, String creator, String description, String publisher, String identifier, float gradoDeSimilitud)
    {
        this.numeroDeDocumento = numeroDeDocumento;
        this.title = title;
        this.creator = creator;
        this.description = description;
        this.publisher = publisher;
        this.identifier = identifier;
        this.gradoDeSimilitud = gradoDeSimilitud;
    }
   
    public int getNumeroDeDocumento(){return numeroDeDocumento;}
    public String getTitle(){return title;}
    public String getCreator(){return creator;}
    public String getDescription(){return description;}
    public String getPublisher(){return publisher;}
    public String getIdentifier(){return identifier;}
    public float getGradoDeSimilitud (){return gradoDeSimilitud;}
    
    @Override
    public int compareTo(Document doc) {
        if(this.gradoDeSimilitud<doc.gradoDeSimilitud)
            return 1;
        else if(this.gradoDeSimilitud>doc.gradoDeSimilitud)
            return -1;
        else
            return 0;
    }
}
