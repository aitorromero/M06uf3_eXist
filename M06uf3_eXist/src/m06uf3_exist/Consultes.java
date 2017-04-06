package m06uf3_exist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import org.w3c.dom.Node;

/**
 *
 * @author Jorge
 */
public class Consultes {

    private final XQConnection con;
    private XQExpression xqe;
    private XQPreparedExpression xqpe;

    public Consultes(XQConnection con) {
        this.con = con;
    }

    public List<Node> obtenirLlibres() {
        List<Node> libros = new ArrayList<>();
        try {
            xqe = con.createExpression();
            String xq = "for $b in doc ('/m06_uf3/libros.xml')//libro return $b/titulo";

            XQResultSequence rs = xqe.executeQuery(xq);
            while (rs.next()) {
                libros.add(rs.getItem().getNode());
            }
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
        return libros;
    }

    public Node cercarNom(String nom) {
        Node libro = null;
        try {
            xqe = con.createExpression();
            String xq = "for $b in doc('/m06_uf3/libros.xml')"
                    + "//libro where every $a in $b/titulo satisfies ($a = '" + nom + "') return $b";

            XQResultSequence rs = xqe.executeQuery(xq);
            rs.next();
            libro = rs.getItem().getNode();
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
        return libro;
    }

    public void afegirLlibre(String codigo, String categoria, String fecha_pub, String titulo, String ventas) {
        try {
            xqe = con.createExpression();
            String xq = "update insert "
                    + "    <libro codigo='" + codigo + "'>"
                    + "        <categoria>" + categoria + "</categoria>"
                    + "        <fecha_pub>" + fecha_pub + "</fecha_pub>"
                    + "        <titulo>" + titulo + "</titulo>"
                    + "        <ventas>" + ventas + "</ventas>"
                    + "    </libro>\n"
                    + "into doc('/m06_uf3/libros.xml')/listadelibros";

            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void afegirAtribut(String atributo, String valor) {
        try {
            xqe = con.createExpression();
            String xq = "update insert attribute " + atributo + " {'" + valor + "'} into doc('/m06_uf3/libros.xml')//libro";
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void afegirEtiqueta(String etiqueta, String valor) {
        try {
            xqe = con.createExpression();
            String xq = "update insert <" + etiqueta + ">'" + valor + "'</" + etiqueta + "> into doc('/m06_uf3/libros.xml')//libro";
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void modificarPreuNode(String codigo, String precio) {
        try {
            xqe = con.createExpression();
            String xq = "update value doc('/m06_uf3/libros.xml')//libro[@codigo='" + codigo + "']/preu with '" + precio + "'";
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void eliminarLlibre(String codigo) {

        try {
            xqe = con.createExpression();
            String xq = "update delete doc('/m06_uf3/libros.xml')//libro[@codigo='" + codigo + "']";
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void eliminarEtiqueta(String etiqueta) {
        try {
            xqe = con.createExpression();
            String xq = "update delete doc('/m06_uf3/libros.xml')//libro/" + etiqueta;
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    void eliminarAtribut(String atributo) {
        try {
            xqe = con.createExpression();
            String xq = "update delete doc('/m06_uf3/libros.xml')//libro/@" + atributo;
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Declaramos un nuevo String con la traduccion de la etiquetas. Dentro
     * de un try cambiamos cada una de estas.
     */
    void traducirEtiqueta() {
        String[] traduccionEtiqueta = {"NOMBRE_COMUN", "NOMBRE_CIENTIFICO", "ZONA",
            "LUMINOSIDAD", "PRECIO", "DISPONIBILIDAD"};
        String[] etiqueta = {"COMMON", "BOTANICAL", "ZONE", "LIGHT", "PRICE",
            "AVAILABILITY"};
        try {
            xqe = con.createExpression();
            for (int i = 0; i < etiqueta.length; i++) {
                String xq = "update rename doc('/m06_uf3/plantes.xml')//PLANT/" + etiqueta[i] + " as '" + traduccionEtiqueta[i] + "'";
                xqe.executeCommand(xq);
            }
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Accedemos a cada uno de los precios y eliminamos el simbolo de dollar.
     */
    void quitarSimboloDollar() {
        try {
            xqe = con.createExpression();
            /*
                for $b in doc('/m06_uf3/plantes.xml')//PRICE 
                return update value $b with substring($b, 2)
             */
            String xq = "for $b in doc('/m06_uf3/plantes.xml')//PRICE return update value $b with substring($b, 2)";
            xqe.executeCommand(xq);

        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Obtenemos todas la plantas y las almacenamos en un arrayList de nodos, que
     * despues recorreremos para mostrar.
     * @return 
     */
    public List<Node> obtenirPlantes() {
        List<Node> plantes = new ArrayList<>();
        try {
            xqe = con.createExpression();
            String xq = "for $b in doc ('/m06_uf3/plantes.xml')//PLANT  return $b";

            XQResultSequence rs = xqe.executeQuery(xq);
            while (rs.next()) {
                plantes.add(rs.getItem().getNode());
            }
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
        return plantes;
    }

    /**
     * Usando satisfies en la consulta buscamos el valor de la etiqueta COMMON
     * con el pasado por parametro.
     * @param nom
     * @return 
     */
    public Node cercarPlantaPerNom(String nom) {
        Node libro = null;
        try {
            xqe = con.createExpression();
            String xq = "for $b in doc('/m06_uf3/plantes.xml')"
                    + "//PLANT where every $a in $b/COMMON satisfies ($a = '" + nom + "') return $b";

            XQResultSequence rs = xqe.executeQuery(xq);
            rs.next();
            libro = rs.getItem().getNode();
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
        return libro;
    }

    /**
     * Añadimos una nueva planta haciendo un insert de toda la estructura
     * con los vslores que deseamos.
     * @param nombreComun
     * @param nombreCientifico
     * @param zona
     * @param luminosidad
     * @param precio
     * @param disponibilidad 
     */
    public void afegirPlanta(String nombreComun, String nombreCientifico, String zona, String luminosidad, String precio, String disponibilidad) {
        try {
            xqe = con.createExpression();
            String xq = "update insert "
                    + "    <PLANT>"
                    + "        <COMMON>" + nombreComun + "</COMMON>"
                    + "        <BOTANICAL>" + nombreCientifico + "</BOTANICAL>"
                    + "        <ZONE>" + zona + "</ZONE>"
                    + "        <LIGHT>" + luminosidad + "</LIGHT>"
                    + "        <PRICE>" + precio + "</PRICE>"
                    + "        <AVAILABILITY>" + disponibilidad + "</AVAILABILITY>"
                    + "    </PLANT>\n"
                    + "preceding doc('/m06_uf3/plantes.xml')//PLANT[1]";

            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Añadimos un atributo con un valor a la primera planta.
     * @param atributo
     * @param valor 
     */
    public void afegirAtributPlanta(String atributo, String valor) {
        try {
            xqe = con.createExpression();
            String xq = "update insert attribute " + atributo + " {'" + valor + "'} into doc('/m06_uf3/plantes.xml')//PLANT";
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * En la planta que coincida con el nombre que recibimos por parametro
     * añadimos un valor, tambien recibido por parametro. Mediante la estructura
     * que podemos ver abajo.
     * @param etiqueta
     * @param valor
     * @param zona 
     */
    public void afegirEtiquetaPlanta(String etiqueta, String valor, String zona) {
        try {
            xqe = con.createExpression();
            String xq = "for $b in doc ('/m06_uf3/plantes.xml')//PLANT where every $a in $b/ZONE satisfies ($a='" + zona + "')"
                    + "return update insert <" + etiqueta.toUpperCase() + "> {'" + valor + "'} </" + etiqueta.toUpperCase() + "> into $b";
            //System.out.println(xq);
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Obtenemos todas las plantas con un precio entre los dos que hemos introducido
     * por parametro.
     * @param preuMin
     * @param preuMas
     * @return 
     */
    public List<Node> obtenirPlantesPerPreus(String preuMin, String preuMas) {
        List<Node> plantes = new ArrayList<>();
        try {
            xqe = con.createExpression();
            /*String xq = "for $b in doc ('/m06_uf3/plantes.xml')//PLANT  return $b";*/
            String xq = "for $b in doc ('/plantas/plantes.xml')//PLANT "
                    + "where every $a in $b/PRICE satisfies($a >= '" + preuMin + "' and $a <= '" + preuMas + "') return $b";

            XQResultSequence rs = xqe.executeQuery(xq);
            while (rs.next()) {
                plantes.add(rs.getItem().getNode());
            }
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
        return plantes;
    }

    /**
     * Obtenemos todas las plantas que su zona coincida con la que recibimos por
     * parametro
     * @param zona
     * @return 
     */
    public List<Node> obtenirPlantesPerZona(String zona) {
        List<Node> plantes = new ArrayList<>();
        try {
            xqe = con.createExpression();
            /*String xq = "for $b in doc ('/m06_uf3/plantes.xml')//PLANT  return $b";*/
            String xq = "for $b in doc ('/plantas/plantes.xml')//PLANT "
                    + "where every $a in $b/ZONE satisfies($a = '" + zona + "') return $b";

            XQResultSequence rs = xqe.executeQuery(xq);
            while (rs.next()) {
                plantes.add(rs.getItem().getNode());
            }
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
        return plantes;
    }

    /**
     * El elemento que coincida con el nombre recibido por parametro es modificado
     * con el precio tambien recibido por parametro.
     * @param nom
     * @param precio 
     */
    public void modificarPreuDunNom(String nom, String precio) {
        try {
            xqe = con.createExpression();
            String xq = "for $b in doc ('/m06_uf3/plantes.xml')//PLANT "
                    + "where every $a in $b/COMMON satisfies($a = '" + nom + "') return update value $b/PRICE with '" + precio + "'";
            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Eliminamos la planta que tenga el mismo nombre que el que recibimos por
     * parametro.
     * @param nom 
     */
    public void eliminarPlanta(String nom) {

        try {
            xqe = con.createExpression();
            /*String xq = "update delete doc('/m06_uf3/plantes.xml')//PLANT/COMMON[@codigo='" + codigo + "']";*/
            String xq = "for $b in doc('/plantas/plantes.xml')//PLANT where every $a in $b/COMMON satisfies($a = '" + nom + "') return update delete $b";

            xqe.executeCommand(xq);
        } catch (XQException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
