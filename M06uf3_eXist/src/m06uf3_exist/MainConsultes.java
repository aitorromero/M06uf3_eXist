package m06uf3_exist;

import java.util.List;
import org.w3c.dom.Node;

public class MainConsultes {

    public static void main(String[] args) {
        ConfigConnexio cc = new ConfigConnexio();
        Consultes cons = new Consultes(cc.getCon());
        
        //cons.traducirEtiqueta();
        //cons.quitarSimboloDollar();
        /*
        List<Node> plantes = cons.obtenirPlantes();
        for (Node planta : plantes) {
            System.out.println(planta.getTextContent());
        }
        */
        //System.out.println(cons.cercarPlantaPerNom("Bloodroot").getTextContent());
    }

}
