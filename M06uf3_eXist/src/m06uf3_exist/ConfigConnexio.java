package m06uf3_exist;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import net.xqj.exist.ExistXQDataSource;

public class ConfigConnexio {

    private XQDataSource xqs;
    private XQConnection con;

    public ConfigConnexio() {
        configConnexio();
    }

    private void configConnexio() {
        try {
            xqs = new ExistXQDataSource();
            xqs.setProperty("serverName", "localhost");
            xqs.setProperty("port", "8080");
            con = xqs.getConnection("admin", "1234");
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public XQConnection getCon() {
        return con;
    }

    public void propietatsBD() {
        String propietats[] = xqs.getSupportedPropertyNames();
        System.out.println("Propietats de XQData" + Arrays.toString(propietats));
        try {
            System.out.println("IsClosed: " + con.isClosed());
            for (String propietat : propietats) {
                System.out.println(propietat + "=\t" + xqs.getProperty(propietat));
            }
        } catch (XQException ex) {
            Logger.getLogger(ConfigConnexio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
