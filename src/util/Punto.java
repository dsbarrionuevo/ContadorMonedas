package util;

/**
 *
 * @author Diego Barrionuevo
 * @version 1.0
 */
public class Punto {

    private int x, y;

    public Punto(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object punto) {
        Punto puntoComparacion = (Punto) punto;
        return (this.x == puntoComparacion.getX()) && (this.y == puntoComparacion.getY());
    }

}
