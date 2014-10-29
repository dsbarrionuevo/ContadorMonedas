package contadormonedas;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import util.Imagen;

/**
 *
 * @author Diego Barrionuevo
 */
public class Controlador {

    private File archivo;
    private double exigencia = 0.95;
    private ArrayList<Integer> radios;
    private int umbralBinarizacion, tamanioVentana;
    private boolean mostrar;

    public Controlador() {
        this.radios = new ArrayList<>();
        VentanaPrincipal vp = new VentanaPrincipal(this);
        vp.setVisible(true);
    }

    public void addRadio(int radio) {
        radios.add(radio);
    }

    public boolean ejecutar() {
        if (archivo == null) {
            System.out.println("Archivo nulo.");
            return false;
        }
        if (this.radios.isEmpty()) {
            System.out.println("Radios vacíos.");
            return false;
        }
        if (this.exigencia < 0.5 || this.exigencia >= 1) {
            System.out.println("Exigencia incorrecta.");
            return false;
        }
        try {
            BufferedImage img = ImageIO.read(archivo);
            BufferedImage imgGrises, imgBinarizada, imgFinal;

            Imagen imagen = new Imagen(img);
            imagen.fotocopiar();
            imagen.escalaGrises(true);
            imgGrises = Imagen.copiar(imagen.getImagen());
            imagen.filtroMedia(tamanioVentana, true);
            imagen.binarizar(new Color(umbralBinarizacion, umbralBinarizacion, umbralBinarizacion), true);
            imgBinarizada = Imagen.copiar(imagen.getImagen());

            boolean aplicarContorno = true;
            HoughCircular hc;
            if (aplicarContorno) {
                ArrayList<util.Region> regiones = imagen.regionGrowing();
                int[][] matrizContornos = imagen.contornearRegion(imagen.getMatrizRegiones(), regiones.size());

//                for (int i = 0; i < matrizContornos.length; i++) {
//                    for (int j = 0; j < matrizContornos[i].length; j++) {
//                        if (matrizContornos[i][j] == 1) {
//                            imagen.pintarPixel(j, i, Color.red);
//                        }
//                    }
//                }

                imgFinal = imagen.getImagen();
                hc = new HoughCircular(imgFinal.getWidth(), imgFinal.getHeight(), exigencia);
                for (int i = 0; i < matrizContornos.length; i++) {
                    for (int j = 0; j < matrizContornos[i].length; j++) {
                        if (matrizContornos[i][j] == 1) {
                            hc.addXY(i, j);
                        }
                    }
                }
            } else {
                imgFinal = imagen.getImagen();
                hc = new HoughCircular(imgFinal.getWidth(), imgFinal.getHeight(), exigencia);
                for (int i = 0; i < imgFinal.getWidth(); i++) {
                    for (int j = 0; j < imgFinal.getHeight(); j++) {
                        Color color = new Color(imgFinal.getRGB(i, j));
                        if (color.equals(Color.black)) {
                            // Encontré un pixel negro.
                            hc.addXY(j, i);
                        }
                    }
                }
            }


            StringBuilder sb = new StringBuilder();
            for (Integer radio : radios) {
                hc.addRadio(radio);
                hc.llenarMatrizAcumuladora();
                hc.dibujarParametros(imgFinal);
                int max = hc.getMaxAcumulado();
                int umbral = (int) (max * exigencia);
                int umbral2 = (int) (max * 0.3);
                sb.append("La cantidad de círculos detectados cuyo radio aproximado es ");
                sb.append(radio);
                sb.append(" fueron ");
                sb.append(DetectorRegiones.getRegiones(hc.getMatrizAcumuladora(), umbral, umbral2, max).size());
                sb.append("\n");
            }
            System.out.println(sb.toString());

            ImageIO.write(img, "png", new File("imagenes/r1.png"));
            ImageIO.write(imgGrises, "png", new File("imagenes/r2.png"));
            ImageIO.write(imgBinarizada, "png", new File("imagenes/r3.png"));
            ImageIO.write(imgFinal, "png", new File("imagenes/r4.png"));

            if (this.mostrar) {
                Ventana v = new Ventana(img, imgGrises, imgBinarizada, imgFinal, sb.toString());
                v.setVisible(true);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public void limpiar() {
        this.exigencia = 0;
        this.radios.clear();
    }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
    }

    public void setExigencia(double exigencia) {
        this.exigencia = exigencia;
    }

    public static void main(String[] args) {
        new Controlador();
    }

    void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    public void setUmbralBinarizacion(int umbralBinarizacion) {
        this.umbralBinarizacion = umbralBinarizacion;
    }

    public void setTamanioVentana(int tamanioVentana) {
        this.tamanioVentana = tamanioVentana;
    }

    static class Ventana extends JFrame {

        public Ventana(BufferedImage original, BufferedImage gris, BufferedImage binaria, BufferedImage imagenFinal, String resultado) {
            super.setBounds(0, 0, 500, 500);
            super.setLayout(new FlowLayout());

            JLabel lblImagenOriginal = new JLabel();
            lblImagenOriginal.setIcon(new ImageIcon(original));
            JScrollPane scrollImagenOriginal = new JScrollPane(lblImagenOriginal);
            super.add(scrollImagenOriginal);

            JLabel lblImagenGris = new JLabel();
            lblImagenGris.setIcon(new ImageIcon(gris));
            JScrollPane scrollImagenGris = new JScrollPane(lblImagenGris);
            super.add(scrollImagenGris);

            JLabel lblImagenBinaria = new JLabel();
            lblImagenBinaria.setIcon(new ImageIcon(binaria));
            JScrollPane scrollImagenBinaria = new JScrollPane(lblImagenBinaria);
            super.add(scrollImagenBinaria);

            JLabel lblImagenFinal = new JLabel();
            lblImagenFinal.setIcon(new ImageIcon(imagenFinal));
            JScrollPane scrollImagenFinal = new JScrollPane(lblImagenFinal);
            super.add(scrollImagenFinal);

            JTextArea txtTitulo = new JTextArea(resultado);
            JScrollPane scroll = new JScrollPane(txtTitulo);
//            scroll.setPreferredSize(new Dimension(300, 400));
            super.add(scroll);
            pack();
        }
    }
}
