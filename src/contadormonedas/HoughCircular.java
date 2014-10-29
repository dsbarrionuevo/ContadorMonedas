package contadormonedas;

import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class HoughCircular {

    private int[][] acumuladora;
    private int[][] imagen;
    private final int ANCHO;
    private final int ALTO;
    private ArrayList<Integer> radios;
    private int radioActual;
    private int maxAcumulado;
    private double exigencia = 0.95;
    private static String archivo = "imagenes/circulo18.png";

    public HoughCircular() {
        this.ANCHO = 200;
        this.ALTO = 200;
        this.imagen = new int[ANCHO][ALTO];
        this.acumuladora = new int[ANCHO][ALTO];
        this.radios = new ArrayList<>();
    }

    public HoughCircular(int ancho, int alto, double exigencia) {
        this.ANCHO = ancho;
        this.ALTO = alto;
        this.imagen = new int[alto][ancho];
        this.acumuladora = new int[alto][ancho];
        this.exigencia = exigencia;
        this.radios = new ArrayList<>();
    }

    public void addXY(int f, int c) {
        this.imagen[f][c] = 1;
    }

    public void addRadio(int radio) {
        this.radioActual = radio;
    }

    public int getMaxAcumulado() {
        return this.maxAcumulado;
    }

    public int[][] getMatrizAcumuladora() {
        return this.acumuladora;
    }

    public void dibujarParametros(BufferedImage img) {
        int max = 0;
        for (int i = 0; i < this.acumuladora.length; i++) {
            for (int j = 0; j < this.acumuladora[0].length; j++) {
                if (max < this.acumuladora[i][j]) {
                    max = this.acumuladora[i][j];
                }
            }
        }
        int lim1 = (int) (max * exigencia);
        int lim2 = (int) (max * 0.5);
        int lim3 = (int) (max * 0.3);
        int lim4 = (int) (max * 0.2);
        for (int i = 0; i < this.acumuladora.length; i++) {
            for (int j = 0; j < this.acumuladora[0].length; j++) {
                if (this.acumuladora[i][j] >= lim4 && this.acumuladora[i][j] < lim3) {
                    img.setRGB(j, i, Color.blue.getRGB());
                } else if (this.acumuladora[i][j] >= lim3 && this.acumuladora[i][j] < lim2) {
                    img.setRGB(j, i, Color.yellow.getRGB());
                } else if (this.acumuladora[i][j] >= lim2 && this.acumuladora[i][j] < lim1) {
                    img.setRGB(j, i, Color.orange.getRGB());
                } else if (this.acumuladora[i][j] >= lim1) {
                    img.setRGB(j, i, Color.red.getRGB());
                    dibujarCirculo(j, i, img);
                }
                if (max < this.acumuladora[i][j]) {
                    max = this.acumuladora[i][j];
                }
            }
        }
        this.maxAcumulado = max;
    }

    public void dibujarCirculo(int a, int b, BufferedImage bf) {
        //      System.out.println("Dibujando círculo: " + a + ", " + b);
        for (int i = 0; i < 1000; i++) {
            int x = i;
            int radicando = radioActual * radioActual - (x - a) * (x - a);
            if (radicando >= 0) {
                int y = (int) (Math.sqrt(radicando) + b);
                if (x >= 0 && x < ANCHO && y >= 0 && y < ALTO && y - (y - b) * 2 >= 0) {
                    bf.setRGB(x, y, Color.green.getRGB());
                    bf.setRGB(x, y - (y - b) * 2, Color.green.getRGB());
//                    bf.setRGB(x - 1, y - 1, Color.green.getRGB());
//                    bf.setRGB(x + 1, (y - (y - b) * 2) + 1, Color.green.getRGB());
                }
            }
        }
    }

    private void limpiarMatrizAcumuladora() {
        for (int i = 0; i < this.acumuladora.length; i++) {
            for (int j = 0; j < this.acumuladora[0].length; j++) {
                this.acumuladora[i][j] = 0;
            }
        }
    }

    public void llenarMatrizAcumuladora() {
        limpiarMatrizAcumuladora();
        int radio = radioActual;
        Par fc;
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
        for (int a = 0; a < ANCHO; a++) {
            if (Math.abs(c - a) < radio) {
                int b = getB(f, c, radio, a);
                if (b >= 0 && b < ALTO) {
                    if (b + f >= 0 && b + f < ALTO) {
                        this.acumuladora[b + f][a]++;
                    }
                    int aux = f - b;
                    if (aux >= 0 && aux < ALTO) {
                        this.acumuladora[aux][a]++;
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
//        try {
//            BufferedImage img = ImageIO.read(new File(archivo));
//            HoughCircular hc = new HoughCircular(img.getWidth(), img.getHeight());
//
//            for (int i = 0; i < img.getWidth(); i++) {
//                for (int j = 0; j < img.getHeight(); j++) {
//                    Color color = new Color(img.getRGB(i, j));
//                    if (color.equals(Color.black)) {
//                        // Encontré un pixel negro.
//                        hc.addXY(j, i);
//                    }
//                }
//            }
//            ArrayList<Integer> radios = new ArrayList<>();
////            radios.add(17);
////            radios.add(26);
////            radios.add(41);
//
////            radios.add(35);
//            radios.add(48);
//            StringBuilder sb = new StringBuilder();
//            for (Integer radio : radios) {
//                hc.addRadio(radio);
//                hc.llenarMatrizAcumuladora();
//                hc.dibujarParametros(img);
//                int max = hc.maxAcumulado;
//                int umbral = (int) (max * exigencia);
//                //int umbral2 = (int) (max * 0.5);
//                sb.append("La cantidad de círculos detectados cuyo radio aproximado es ");
//                sb.append(radio);
//                sb.append(" fueron ");
//                sb.append(DetectorRegiones.getRegiones(hc.acumuladora, umbral, max).size());
//                sb.append("\n");
//            }
//            System.out.println(sb.toString());
//            Ventana v = new Ventana(img, sb.toString());
//            v.setVisible(true);
//
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }

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

    public static ArrayList<Region> getRegiones(int[][] matrizAcumuladora, int umbral, int umbral2, int max) {
        ArrayList<Region> regiones = new ArrayList<Region>();
        boolean vistos[][] = new boolean[matrizAcumuladora.length][matrizAcumuladora[0].length];
        for (int i = 0; i < matrizAcumuladora.length; i++) {
            for (int j = 0; j < matrizAcumuladora[0].length; j++) {
                if (matrizAcumuladora[i][j] >= umbral && !vistos[i][j] && matrizAcumuladora[i][j] <= max) {
                    Region region = new Region();
                    buscarRegionRec(i, j, region.puntos, matrizAcumuladora, umbral2, vistos);
                    regiones.add(region);
                }
                vistos[i][j] = true;
            }
        }
        return regiones;
    }

    private static void buscarRegionRec(int f, int c, ArrayList<Par> puntos, int[][] matrizAcumuladora, int umbral, boolean vistos[][]) {
        if (matrizAcumuladora[f][c] >= umbral && !vistos[f][c]) {
            vistos[f][c] = true;
            Par par = new Par();
            par.primero = f;
            par.segundo = c;
            puntos.add(par);

            buscarRegionRec(f, c + 1, puntos, matrizAcumuladora, umbral, vistos);
            buscarRegionRec(f + 1, c, puntos, matrizAcumuladora, umbral, vistos);
            buscarRegionRec(f, c - 1, puntos, matrizAcumuladora, umbral, vistos);
            buscarRegionRec(f - 1, c, puntos, matrizAcumuladora, umbral, vistos);
        }
    }

    private static void buscarRegion(int f, int c, ArrayList<Par> puntos, int[][] matrizAcumuladora, int umbral, boolean vistos[][]) {
        Stack<Par> pila = new Stack<>();
        if (matrizAcumuladora[f][c] >= umbral && !vistos[f][c]) {
            vistos[f][c] = true;
            Par par = new Par();
            par.primero = f;
            par.segundo = c;
            puntos.add(par);
            pila.add(par);
            while (!pila.isEmpty()) {
                Par top = pila.peek();
                f = top.primero;
                c = top.segundo;
                c++;
                while (matrizAcumuladora[f][c] >= umbral && !vistos[f][c]) {
                    vistos[f][c] = true;
                    par = new Par();
                    par.primero = f;
                    par.segundo = c;
                    puntos.add(par);
                    pila.add(par);
                    top = par;
                    c = c + 1;
                }
                while (matrizAcumuladora[f][c] >= umbral && !vistos[f][c]) {
                    vistos[f][c] = true;
                    par = new Par();
                    par.primero = f;
                    par.segundo = c;
                    puntos.add(par);
                    pila.add(par);
                    top = par;
                    f = f + 1;
                }
                while (matrizAcumuladora[f][c] >= umbral && !vistos[f][c]) {
                    vistos[f][c] = true;
                    par = new Par();
                    par.primero = f;
                    par.segundo = c;
                    puntos.add(par);
                    pila.add(par);
                    top = par;
                    c = c - 1;
                }
                while (matrizAcumuladora[f][c] >= umbral && !vistos[f][c]) {
                    vistos[f][c] = true;
                    par = new Par();
                    par.primero = f;
                    par.segundo = c;
                    puntos.add(par);
                    pila.add(par);
                    top = par;
                    f = f - 1;
                }
                pila.pop();
            }
        }
    }

}
