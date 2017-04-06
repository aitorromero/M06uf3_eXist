package Colleccions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.xmldb.api.modules.BinaryResource;
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
    /**
     * Obtenemos mediante .getName el nombre de la coleccion.
     */
    public void nomColeccioActual() {

        try {
            System.out.println(col.getName());
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Mediante .getParentCFolection().getName() obtenemos el nombre del padre de la coleccion
     */
    public void nomColeccioPare() {
        try {
            System.out.println(col.getParentCollection().getName());
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtenemos una lista de todas las coleciones de la coleccion en la que estamos
     * @return 
     */
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

    /**
     * Eliminamos la coleccion con el nombre que le pasamos por parametro
     * @param nom 
     */
    public void eliminarColeccio(String nom) {
        try {
            cms.removeCollection(nom);
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Buscamos un recurso dentro de una coleccion mediante DatabaseManager.getCollection(ruta, usuario, contraseña)
     * @param coleccio
     * @param recurso
     * @return 
     */
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

    /**
     * Buscamos CollectionManagementService recuperando los servicios de la conexion. 
     * Y gracias a un for each, si es igual a CollectionManagementService lo
     * almacenamos en cms.
     */
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

    /**
     * Generamos una nueva instancia de DocumentBuilderFactory, despues creamos un 
     * DocumentBuilder y generamos el fichero gracias a docBuilder.parse(file).
     * @param file
     * @return 
     */
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

    /**
     * Generamos la conexion al recurso mediante XMLResource, llenamos el
     * recurso con el doc que hemos creado y le pasamos un file con la ruta.
     * Finalmente gracias a col.storeResource subimos el Doc.
     * @param ruta 
     */
    public void subirDoc(String ruta) {
        try {
            XMLResource xmlres = (XMLResource) col.createResource(ruta, XMLResource.RESOURCE_TYPE);//Genera la conexion al recurso
            xmlres.setContentAsDOM(generarXML(new File(ruta)));//Llenamos el recurso con el doc que hemos creado en generarXML al que le pasamos un file gracias a la ruta 
            col.storeResource(xmlres);
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Generamos la conexion mediante XMLResource. Creamos el documento mediante
     * la conexion creada donde añadiremos el recurso que hemos obtenido. Y de
     * aqui obtenemos el contenido.
     * @param nombre 
     */
    public void obtenerRecurso(String nombre) {
        try {
            XMLResource xmlres = (XMLResource) col.getResource(nombre);
            Document document = (Document) xmlres.getContentAsDOM();
            System.out.println(document.getFirstChild().getTextContent());

        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Encontramos el recurso con el nombre por parametro y lo eliminamos.
     * @param nombre 
     */
    public void eliminarRecurso(String nombre) {
        XMLResource xml = null;
        try {
            xml = (XMLResource) col.getResource(nombre);

            col.removeResource(xml);
        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creamos un nuevo BinaryResource con createResource(
     * nombre, BinaryResource.RESOURCE_TYPE), ademas creamos un nuevo file con
     * la ruta que recibimos por parametro y añadimos este file a el recurso 
     * binario que despues subiremos mediante storeResource.
     * @param ruta
     * @param nombre 
     */
    public void generarBinario(String ruta, String nombre) {

        BinaryResource bin = null;
        try {

            bin = (BinaryResource) col.createResource(nombre, BinaryResource.RESOURCE_TYPE);

            File f = new File(ruta);

            bin.setContent(f);

            col.storeResource(bin);

        } catch (XMLDBException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Generamos un nuevo BinaryResource con la ruta que obtenemos por parametro
     * acedemos a la ruta y accedemos al fichero donde obtendremos el contenido.
     * @param ruta 
     */
    public void obtenerBinario(String nombre, String ruta) {

        BinaryResource br;
        try {
            br = (BinaryResource) col.getResource(nombre);

            Files.write(Paths.get(ruta), (byte[]) br.getContent());

            col.storeResource(br);

        } catch (XMLDBException | IOException ex) {
            Logger.getLogger(Consultes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
