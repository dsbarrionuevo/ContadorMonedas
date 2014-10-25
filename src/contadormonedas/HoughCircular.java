package contadormonedas;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
public class HoughCircular{
	private int[][] acumuladora;
	private int[][] imagen;
	private int ANCHO = 200;
	private int ALTO = 200;
	private final int RADIO = 26; // Dejo fijo el radio. Solo manejo dos parámetros a y b.
	private final int colorBlue = Color.blue.getRGB();

	public HoughCircular(){
		this.imagen = new int[ANCHO][ALTO];
		this.acumuladora = new int[ANCHO][ALTO];
	}
	
	public void addXY(int x, int y){
		this.imagen[x][y] = 1;
	}
	
	public void mostrarValoresDistintosDeCero(BufferedImage img){
		for(int i = 0; i < this.acumuladora.length; i++){
			for(int j = 0; j < this.acumuladora[0].length; j++){
				if(this.acumuladora[i][j] != 0){
					dibujarCirculo(i, j, img);
					System.out.println(i + ", " + j + ": " + this.acumuladora[i][j]);
				}
			}
		}
		
	}
	
	public void dibujarCirculo(int a, int b, BufferedImage bf){
		System.out.println("Dibujando círculo: " + a + ", " + b);
		for(int i = 0; i < 1000; i++){
			int x = i;
			int radicando = RADIO * RADIO - (x - a) * (x - a);
			if(radicando >= 0){
				int y = (int)(Math.sqrt(radicando) + b);
				if(x >= 0 && x < 200 && y >= 0 && y < 200 && y - (y - b) * 2 >= 0){
					bf.setRGB(x, y, colorBlue);
					bf.setRGB(x, y - (y - b) * 2, colorBlue);
				}
			}
		}
	}
	
	private Par proximoXY(int desdeX, int desdeY){
		Par xy = null;
		for(int x = desdeX + 1; x < this.imagen.length; x++){
			for(int y = desdeY + 1; y < this.imagen[0].length; y++){
				if(this.imagen[x][y] == 1){
					xy = new Par();
					xy.primero = x;
					xy.segundo = y;
					break;
				}
			}
		}
		return xy;
	}
	
	public void llenarMatrizAcumuladora(){
		Par xy = null;
		int radio = RADIO;
		do{
			if(xy == null){
				xy = proximoXY(0, 0);
			}else{
				xy = proximoXY(xy.primero, xy.segundo);
			}
			if(xy != null){
				calcularA(xy.primero, xy.segundo, radio);
			}
		}while(xy != null);
	}
	
	private void calcularA(int x, int y, int radio){
		for(int a = 0; a < 200; a++){
			if(x - a < radio){
				int b = getB(x, y, radio, a);
				if(b >= 0 && b < 200){
					this.acumuladora[a][b]++;
				}
			}			
		}
	}
	
	private int getB(int x, int y, int radio, int a){
		//System.out.println(x + ", " + y + ", " + radio + ", " + a);
		//System.out.println((int) (Math.sqrt(radio * radio - (x - a) * (x - a)) + y));
		return (int) (Math.sqrt(radio * radio - (x - a) * (x - a)) + y);
	}
	
	public static void main(String args[]){
		HoughCircular hc = new HoughCircular();
		try{
			BufferedImage img = ImageIO.read(new File("circulo.png"));
			for(int i = 0; i < img.getWidth(); i++){
				for(int j = 0; j < img.getHeight(); j++){
					Color color = new Color(img.getRGB(i, j));
					if(color.getRed() == 0){
						// Encontré un pixel negro.
						hc.addXY(i, j);
					}
				}
			}
			hc.llenarMatrizAcumuladora();
			hc.mostrarValoresDistintosDeCero(img);
			//hc.dibujarCirculo(40, 40, img);
			Ventana v = new Ventana(img);
			v.setVisible(true);
			
		}catch(IOException e){}
		
	}
	
	static class Ventana extends JFrame{
		public Ventana(BufferedImage bfi){
			super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			super.setBounds(0, 0, 500, 500);
			JLabel lblImagen = new JLabel();
			lblImagen.setIcon(new ImageIcon(bfi));
			super.add(lblImagen);
		}
	}
}
class Par{
	public int primero;
	public int segundo;
}