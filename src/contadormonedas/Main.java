package contadormonedas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import util.Imagen;

/**
 *
 * @author Diego Barrionuevo
 */
public class Main {

    public static void main(String[] args) {
        try {
            BufferedImage original = ImageIO.read(new File("./imagenes/img1.png"));
            Imagen imagen = new Imagen(original);
            imagen.escalaGrises(10);
            imagen.guardar("./imagenes/_img1.png");
        } catch (IOException ex) {
            Logger.getLogger(Imagen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
