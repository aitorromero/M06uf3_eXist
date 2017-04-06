package Colleccions;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public class ConfigConnexio {
    private Database dbDriver;
    private Collection coll;
    
    public ConfigConnexio(){
        conexio();
    }
    
    public Collection conexio() {
        try {
            dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
            DatabaseManager.registerDatabase(dbDriver);
            coll = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db", "admin", "1234");
            

        } catch (ClassNotFoundException | XMLDBException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ConfigConnexio.class.getName()).log(Level.SEVERE, null, ex);
        }
        return coll;
    }
}

