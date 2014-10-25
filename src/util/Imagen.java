package util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.geometry.Point2D;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Diego Barrionuevo
 * @version 1.0
 */
public class Imagen {

    private BufferedImage imagen;
    private Color[][] pixeles;
    private int ancho, alto;

    static int valorMaximo = 0;

    public Imagen(BufferedImage bufferImagen) {
        this.ancho = bufferImagen.getWidth();
        this.alto = bufferImagen.getHeight();
        this.pixeles = new Color[this.alto][this.ancho];
        for (int row = 0; row < this.alto; row++) {
            for (int col = 0; col < this.ancho; col++) {
                this.pixeles[row][col] = new Color(bufferImagen.getRGB(col, row));
            }
        }
        this.imagen = new BufferedImage(this.ancho, this.alto, bufferImagen.getType());
    }

    public void fotocopiar() {
        for (int i = 0; i < pixeles.length; i++) {
            for (int j = 0; j < pixeles[i].length; j++) {
                Color pixel = pixeles[i][j];
                imagen.setRGB(j, i, pixel.getRGB());
            }
        }
    }

    public void binarizar(Color umbral) {
        for (int i = 0; i < pixeles.length; i++) {
            for (int j = 0; j < pixeles[i].length; j++) {
                Color pixel = pixeles[i][j];
                if (pixel.getRed() < umbral.getRed() && pixel.getGreen() < umbral.getGreen() && pixel.getBlue() < umbral.getBlue()) {
                    imagen.setRGB(j, i, (Color.black).getRGB());
                } else {
                    imagen.setRGB(j, i, (Color.white).getRGB());
                }
            }
        }
    }

    public void escalaGrises() {
        for (int i = 0; i < pixeles.length; i++) {
            for (int j = 0; j < pixeles[i].length; j++) {
                Color pixel = pixeles[i][j];
                int nivelGris = (int) ((pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3);
                Color color = new Color(nivelGris, nivelGris, nivelGris);
                imagen.setRGB(j, i, color.getRGB());
            }
        }
    }

    public void escalaGrises(int cantidadNiveles) {
        int intervalo = (int) (256 / cantidadNiveles);
        for (int i = 0; i < pixeles.length; i++) {
            for (int j = 0; j < pixeles[i].length; j++) {
                Color pixel = pixeles[i][j];
                int media = (int) ((pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3);
                int valorA = 0, valorN;
                int nivelGris = 0;
                for (int k = 0; k < cantidadNiveles; k++) {
                    valorN = valorA + intervalo;
                    if (media >= valorA && media < valorN) {
                        nivelGris = intervalo * k;
                    }
                    valorA += intervalo;
                }
                Color color = new Color(nivelGris, nivelGris, nivelGris);
                imagen.setRGB(j, i, color.getRGB());
            }
        }
    }

    public void filtroMedia(int tamanioVecindad) {
        for (int i = tamanioVecindad; i < pixeles.length - tamanioVecindad; i++) {
            for (int j = tamanioVecindad; j < pixeles[i].length - tamanioVecindad; j++) {
                //Color pixel = pixeles[i][j];
                Color vecindad[][] = this.obtenerVecindad(i, j, tamanioVecindad);
                int rojos = 0, verdes = 0, azules = 0;
                for (int k = 0; k < vecindad.length; k++) {
                    for (int l = 0; l < vecindad[k].length; l++) {
                        rojos += vecindad[k][l].getRed();
                        verdes += vecindad[k][l].getGreen();
                        azules += vecindad[k][l].getBlue();
                    }
                }
                int cuadradoTamanioTotal = (int) Math.pow(tamanioVecindad * 2 + 1, 2);
                int promedioRojo = rojos / cuadradoTamanioTotal;
                int promedioVerde = verdes / cuadradoTamanioTotal;
                int promedioAzul = azules / cuadradoTamanioTotal;
                Color colorPromedio = new Color(promedioRojo, promedioVerde, promedioAzul);
                imagen.setRGB(j, i, colorPromedio.getRGB());
            }
        }
    }

    public void filtroMediana(int tamanioVecindad) {
        for (int i = tamanioVecindad; i < pixeles.length - tamanioVecindad; i++) {
            for (int j = tamanioVecindad; j < pixeles[i].length - tamanioVecindad; j++) {
                Color vecindad[][] = this.obtenerVecindad(i, j, tamanioVecindad);
                int indColumnaColorido = i, indFilaColorido = j, masColorida = 0;
                for (int k = 0; k < vecindad.length; k++) {
                    for (int l = 0; l < vecindad[k].length; l++) {
                        int colorTotal = vecindad[k][l].getRed() + vecindad[k][l].getGreen() + vecindad[k][l].getBlue();
                        if (colorTotal > masColorida) {
                            masColorida = colorTotal;
                            indFilaColorido = k;
                            indColumnaColorido = l;
                        }
                    }
                }
                Color colorPromedio = vecindad[indFilaColorido][indColumnaColorido];
                imagen.setRGB(j, i, colorPromedio.getRGB());
            }
        }
    }

    public int[][] convolucion(int[][] mascara) {
        int[][] convolucion = new int[alto][ancho];
        int tamanioVecindad = (int) Math.floor((double) (((double) mascara.length) / ((double) 2)));
        for (int i = tamanioVecindad; i < pixeles.length - tamanioVecindad; i++) {
            for (int j = tamanioVecindad; j < pixeles[i].length - tamanioVecindad; j++) {
                Color vecindad[][] = this.obtenerVecindad(i, j, tamanioVecindad);
                int nuevoValor = 0;
                for (int k = 0; k < vecindad.length; k++) {
                    for (int l = 0; l < vecindad[k].length; l++) {
                        int intesidadTotal = (int) (vecindad[k][l].getRed() + vecindad[k][l].getGreen() + vecindad[k][l].getBlue()) / 3;
                        nuevoValor += intesidadTotal * mascara[k][l];
                    }
                }
                convolucion[i][j] = nuevoValor;
            }
        }
        return convolucion;
    }

    public Color[][] convolucionColor(Color[][] mascara) {
        Color[][] convolucion = new Color[alto][ancho];
        int tamanioVecindad = (int) Math.floor((double) (((double) mascara.length) / ((double) 2)));
        for (int i = tamanioVecindad; i < pixeles.length - tamanioVecindad; i++) {
            for (int j = tamanioVecindad; j < pixeles[i].length - tamanioVecindad; j++) {
                Color vecindad[][] = this.obtenerVecindad(i, j, tamanioVecindad);
                int nuevoRojo = 0, nuevoVerde = 0, nuevoAzul = 0;
                for (int k = 0; k < vecindad.length; k++) {
                    for (int l = 0; l < vecindad[k].length; l++) {
                        nuevoRojo += vecindad[k][l].getRed() * mascara[k][l].getRed();
                        nuevoVerde += vecindad[k][l].getGreen() * mascara[k][l].getGreen();
                        nuevoAzul += vecindad[k][l].getBlue() * mascara[k][l].getBlue();
                    }
                }
                Color nuevoColor = new Color(nuevoRojo, nuevoVerde, nuevoAzul);
                convolucion[i][j] = nuevoColor;
            }
        }
        return convolucion;
    }

    //para una vecindad de tamanio 3x3, el parametro tamanioVecindad sera de valor 1
    private Color[][] obtenerVecindad(int fila, int columna, int tamanioVecindad) {
        Color vecindad[][] = new Color[tamanioVecindad * 2 + 1][tamanioVecindad * 2 + 1];
        for (int i = -tamanioVecindad, filaVecindad = 0; i < tamanioVecindad + 1; i++, filaVecindad++) {
            for (int j = -tamanioVecindad, columnaVecindad = 0; j < tamanioVecindad + 1; j++, columnaVecindad++) {
                vecindad[columnaVecindad][filaVecindad] = pixeles[fila + i][columna + j];
            }
        }
        return vecindad;
    }

    public void histograma() {
        int rojos[] = new int[256];
        int verdes[] = new int[256];
        int azules[] = new int[256];
        for (int i = 0; i < pixeles.length; i++) {
            for (int j = 0; j < pixeles[i].length; j++) {
                Color pixel = pixeles[i][j];
                rojos[pixel.getRed()]++;
                verdes[pixel.getGreen()]++;
                azules[pixel.getBlue()]++;
            }
        }
    }

    //algoritmo dew segmentacion no terminado
    public void splitMerge() {
        ArrayList<int[]> regiones = new ArrayList();
        //divido
        split(0, 0, ancho, alto, regiones);
        System.out.println("cantidad: " + regiones.size());
        //fusiono
        //consiste en unir las regiones homogeneas
        Iterator<int[]> it = regiones.iterator();
        while (it.hasNext()) {
            int[] region = it.next();
            for (int i = region[0]; i < region[2]; i++) {
                for (int j = region[1]; j < region[3]; j++) {
                    imagen.setRGB(j, i, (Color.blue).getRGB());
                }
            }
        }
    }

    //fx y fy son los puntos finales, y no el ancho ni el alto!
    private void split(int ix, int iy, int fx, int fy, ArrayList<int[]> regiones) {
        //comprobar homogeneidad
        int intensidadMedia = 0;
        int anchoRegion = (fx - ix);
        int altoRegion = (fy - iy);
        int cantPixeles = anchoRegion * altoRegion;
        if (cantPixeles == 0) {
            return;
        }
        for (int i = ix; i < fx; i++) {
            for (int j = iy; j < fy; j++) {
                Color pixel = pixeles[j][i];
                intensidadMedia += (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
            }
        }
        intensidadMedia = intensidadMedia / cantPixeles;
        if (Math.abs(intensidadMedia) > 1) {
            //no es homogeneo
            int anchoSubregion = anchoRegion / 2;
            int altoSubregion = altoRegion / 2;
            //.:
            split(ix,
                    iy,
                    ix + anchoSubregion,
                    iy + altoSubregion,
                    regiones);
            //:.
            split(ix + anchoSubregion,
                    iy,
                    ix + anchoSubregion + anchoSubregion,
                    iy + altoSubregion,
                    regiones);
            //':
            split(ix,
                    iy + altoSubregion,
                    ix + anchoSubregion,
                    iy + altoSubregion + altoSubregion,
                    regiones);
            //:'
            split(ix + anchoSubregion,
                    iy + altoSubregion,
                    ix + anchoSubregion + anchoSubregion,
                    iy + altoSubregion + altoSubregion,
                    regiones);
        } else {
            //es homogeneo
            int[] region = new int[]{ix, iy, fx, fy};
            regiones.add(region);
        }
    }

    //algoritmo dew segmentacion no terminado
    public void regionGrowing() {
        ArrayList<Point2D> puntos = new ArrayList();
        /*
         int semillaX = (int) (Math.random() * (ancho - 2)) + 1;
         int semillaY = (int) (Math.random() * (alto - 2)) + 1;
         */
        int semillaX = (int) (ancho / 2);
        int semillaY = (int) (alto / 2);
        tirarPunto(semillaX, semillaY, puntos);
        Iterator<Point2D> it = puntos.iterator();
        while (it.hasNext()) {
            Point2D posicion = it.next();
            imagen.setRGB((int) posicion.getY(), (int) posicion.getX(), (Color.blue).getRGB());
        }
    }

    private void tirarPunto(int puntoX, int puntoY, ArrayList<Point2D> puntos) {
        if (puntoX < 0 || puntoX > ancho - 1 || puntoY < 0 || puntoY > alto - 1) {
            return;
        }
        Color pixel = pixeles[puntoY][puntoX];
        Point2D punto = new Point2D(puntoX, puntoY);
        if (puntos.contains(punto)) {
            return;
        }
        int intensidadMedia = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
        if (intensidadMedia < 128) {
            puntos.add(punto);
            for (int i = puntoY - 1; i <= puntoY + 1; i++) {
                for (int j = puntoX - 1; j <= puntoX + 1; j++) {
                    tirarPunto(i, j, puntos);
                }
            }
        }
    }

    public void guardar(String nombreArchivo) throws IOException {
        ImageIO.write(imagen, "png", new File(nombreArchivo));
    }
}
