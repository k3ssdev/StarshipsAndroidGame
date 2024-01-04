package io.github.k3ssdev.starshipsandroidgame;

import java.util.Random;

// Clase para representar las estrellas
public class Estrella {

    private final Random random_apr = new Random();
    private float x1_apr;
    private final float y1_apr;
    private float x2_apr;
    private final float y2_apr;

    // Constructor que inicializa las coordenadas de la estrella
    public Estrella(float x1, float y1, float x2, float y2) {
        this.x1_apr = x1;
        this.y1_apr = y1;
        this.x2_apr = x2;
        this.y2_apr = y2;
    }

    // Método para obtener la coordenada x1 de la estrella
    public float getX1_apr() {
        return x1_apr;
    }

    // Método para obtener la coordenada y1 de la estrella
    public float getY1_apr() {
        return y1_apr;
    }

    // Método para obtener la coordenada x2 de la estrella
    public float getX2_apr() {
        return x2_apr;
    }

    // Método para obtener la coordenada y2 de la estrella
    public float getY2_apr() {
        return y2_apr;
    }

    // Método para mover la estrella en el eje x
    public void mover() {
        x1_apr -= 2;  // Ajusta la velocidad de las estrellas
        x2_apr -= 2;

        // Las estrellas vuelvan a aparecer cuando se salgan de la pantalla
        if (x1_apr < 0) {
            x1_apr = Juego.ancho_apr;
            x2_apr = x1_apr + random_apr.nextInt(5) - 2;
        }
    }
}
