package Colleccions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQPreparedExpression;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

public class Consultes {

    Collection col;
    ConfigConnexio cc = new ConfigConnexio();
    Service[] serveis;
    CollectionManagementService cms;

    public Consultes() {
        this.col = cc.conexio();
        buscarCollectionManagement();
    }

    public void nomColeccioActual() {

        try {
            System.out.println(col.getName());
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nomColeccioPare() {
        try {
            System.out.println(col.getParentCollection().getName());
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] llistatColeccionsFilles() {
        try {
            return col.listChildCollections();
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void crearColeccio(String nom) {
        try {
            cms.createCollection(nom);
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eliminarColeccio(String nom) {
        try {
            cms.removeCollection(nom);
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean cercarEnColeccio(String coleccio, String recurso) {
        boolean cierto;
        try {
            col = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db/" + coleccio, "admin", "admin");

            Resource r = col.getResource(recurso);

            cierto = r != null;
        } catch (XMLDBException ex) {
            cierto = false;
        }
        return cierto;
    }

    public void buscarCollectionManagement() {
        try {
            serveis = col.getServices();
            for (Service s : serveis) {
                if (s.getName().equals("CollectionManagementService")) {
                    cms = (CollectionManagementService) s;
                }
            }
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Document generarXML(File file) {
        Document doc = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(file);

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }

    public void subirDoc(String ruta) {
        try {
            XMLResource xmlres = (XMLResource) col.createResource(ruta, XMLResource.RESOURCE_TYPE);//Genera la conexion al recurso
            xmlres.setContentAsDOM(generarXML(new File(ruta)));//Llenamos el recurso con el doc que hemos creado en generarXML al que le pasamos un file gracias a la ruta 
            col.storeResource(xmlres);
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void obtenerRecurso(String nombre) {
        try {
            XMLResource xmlres = (XMLResource) col.getResource(nombre);
            Document document = (Document) xmlres.getContentAsDOM();
            System.out.println(document.getFirstChild().getTextContent());
            
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
