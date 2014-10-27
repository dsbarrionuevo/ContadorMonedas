package contadormonedas;

import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class HoughCircular {

    public int[][] acumuladora;
    private int[][] imagen;
    private int ANCHO = 200;
    private int ALTO = 200;
    private final int RADIO = 26;
    private final int colorBlue = Color.blue.getRGB();
    private BufferedImage bfi;

    public HoughCircular(BufferedImage bfi) {
        this.imagen = new int[ANCHO][ALTO];
        this.acumuladora = new int[ANCHO][ALTO];
        this.bfi = bfi;
    }

    public void addXY(int f, int c) {
        this.imagen[f][c] = 1;
    }

    public void mostrarValoresDistintosDeCero(BufferedImage img) {
        int max = 0;
        for (int i = 0; i < this.acumuladora.length; i++) {
            for (int j = 0; j < this.acumuladora[0].length; j++) {
                if (this.acumuladora[i][j] >= 5 && this.acumuladora[i][j] < 10) {
                    img.setRGB(j, i, Color.blue.getRGB());
                } else if (this.acumuladora[i][j] >= 10 && this.acumuladora[i][j] < 20) {
                    img.setRGB(j, i, Color.yellow.getRGB());
                } else if (this.acumuladora[i][j] >= 20 && this.acumuladora[i][j] < 50) {
                    img.setRGB(j, i, Color.orange.getRGB());
                } else if (this.acumuladora[i][j] >= 50) {
                    img.setRGB(j, i, Color.red.getRGB());
                }
                if (max < this.acumuladora[i][j]) {
                    max = this.acumuladora[i][j];
                }
            }
        }
        System.out.println(max);

    }

    public void dibujarCirculo(int a, int b, BufferedImage bf) {
        System.out.println("Dibujando círculo: " + a + ", " + b);
        for (int i = 0; i < 1000; i++) {
            int x = i;
            int radicando = RADIO * RADIO - (x - a) * (x - a);
            if (radicando >= 0) {
                int y = (int) (Math.sqrt(radicando) + b);
                if (x >= 0 && x < 200 && y >= 0 && y < 200 && y - (y - b) * 2 >= 0) {
                    bf.setRGB(x, y, colorBlue);
                    bf.setRGB(x, y - (y - b) * 2, colorBlue);
                }
            }
        }
    }

    public void llenarMatrizAcumuladora() {
        int radio = RADIO;
        Par fc = null;
        for (int f = 0; f < this.imagen.length; f++) {
            for (int c = 0; c < this.imagen[0].length; c++) {
                if (this.imagen[f][c] == 1) {
                    fc = new Par();
                    fc.primero = f;
                    fc.segundo = c;
                    //this.bfi.setRGB(f, c, Color.red.getRGB());
                    calcularA(fc.primero, fc.segundo, radio);
                }
            }
        }
    }

    private void calcularA(int f, int c, int radio) {
        for (int a = 0; a < 200; a++) {
            if (Math.abs(c - a) < radio) {
                int b = getB(f, c, radio, a);
                if (b >= 0 && b < 200) {
                    this.acumuladora[b + f][a]++;
                    if (-b + f >= 0) {
                        this.acumuladora[-b + f][a]++;
                    }
                }
            }
        }
    }

    private int getB(int f, int c, int radio, int a) {
        //System.out.println(x + ", " + y + ", " + radio + ", " + a);
        //System.out.println((int) (Math.sqrt(radio * radio - (x - a) * (x - a)) + y));
        return (int) (Math.sqrt(radio * radio - (c - a) * (c - a)));
    }

    public static void main(String args[]) {
        //String nombreImagen = JOptionPane.showInputDialog(null, "Ingrese el nombre de la imagen");
        try {
            BufferedImage img = ImageIO.read(new File("imagenes/circulo2.png"));
            HoughCircular hc = new HoughCircular(img);
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color color = new Color(img.getRGB(i, j));
                    if (color.equals(Color.black)) {
                        // Encontré un pixel negro.
                        hc.addXY(j, i);
                    }
                }
            }
            hc.llenarMatrizAcumuladora();
            hc.mostrarValoresDistintosDeCero(img);
            System.out.println(DetectorRegiones.getRegiones(hc.acumuladora).size());
            //hc.dibujarCirculo(40, 40, img);
            Ventana v = new Ventana(img);
            v.setVisible(true);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    static class Ventana extends JFrame {

        public Ventana(BufferedImage bfi) {
            super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            super.setBounds(0, 0, 500, 500);
            JLabel lblImagen = new JLabel();
            lblImagen.setIcon(new ImageIcon(bfi));
            super.add(lblImagen);
        }
    }
}

class Par {

    public int primero;
    public int segundo;
}

class Region {

    public ArrayList<Par> puntos;

    public Region() {
        this.puntos = new ArrayList<Par>();
    }
}

class DetectorRegiones {

    public static ArrayList<Region> getRegiones(int[][] matrizAcumuladora) {
        ArrayList<Region> regiones = new ArrayList<Region>();
        boolean vistos[][] = new boolean[matrizAcumuladora.length][matrizAcumuladora[0].length];
        for (int i = 0; i < matrizAcumuladora.length; i++) {
            for (int j = 0; j < matrizAcumuladora[0].length; j++) {
                if (matrizAcumuladora[i][j] >= 50 && !vistos[i][j]) {
                    Region region = new Region();
                    buscarRegion(i, j, region.puntos, matrizAcumuladora, vistos);
                    regiones.add(region);
                }
                vistos[i][j] = true;
            }
        }
        return regiones;
    }

    private static void buscarRegion(int f, int c, ArrayList<Par> puntos, int[][] matrizAcumuladora, boolean vistos[][]) {
        if (matrizAcumuladora[f][c] >= 50 && !vistos[f][c]) {
            vistos[f][c] = true;
            Par par = new Par();
            par.primero = f;
            par.segundo = c;
            puntos.add(par);
            buscarRegion(f, c + 1, puntos, matrizAcumuladora, vistos);
            buscarRegion(f + 1, c, puntos, matrizAcumuladora, vistos);
            buscarRegion(f, c - 1, puntos, matrizAcumuladora, vistos);
            buscarRegion(f - 1, c, puntos, matrizAcumuladora, vistos);
        } else {
            return;
        }
    }
}