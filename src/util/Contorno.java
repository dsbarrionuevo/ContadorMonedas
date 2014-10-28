package util;

import java.util.ArrayList;

/**
 *
 * @author Diego Barrionuevo
 * @version 1.0
 */
public class Contorno {

    private ArrayList<Punto> puntos;

    public Contorno(ArrayList<Punto> puntos) {
        this.puntos = puntos;
    }

    public ArrayList<Punto> getPuntos() {
        return puntos;
    }

    public void setPuntos(ArrayList<Punto> puntos) {
        this.puntos = puntos;
    }

}
