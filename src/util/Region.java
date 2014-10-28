package util;

import java.util.ArrayList;

/**
 *
 * @author Diego Barrionuevo
 */
public class Region {

    private ArrayList<Punto> puntos;
    private int etiqueta;

    public Region(int etiqueta, ArrayList<Punto> puntos) {
        puntos = new ArrayList();
    }

    @Override
    public boolean equals(Object obj) {
        Region regionComparacion = (Region) obj;
        return (this.etiqueta == regionComparacion.getEtiqueta());
    }

    public ArrayList<Punto> getPuntos() {
        return puntos;
    }

    public void setPuntos(ArrayList<Punto> puntos) {
        this.puntos = puntos;
    }

    public int getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(int etiqueta) {
        this.etiqueta = etiqueta;
    }

}
