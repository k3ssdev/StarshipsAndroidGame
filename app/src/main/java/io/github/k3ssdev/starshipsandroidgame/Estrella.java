package io.github.k3ssdev.starshipsandroidgame;

import java.util.Random;

// Clase para representar las estrellas
public class Estrella {

    private final Random random = new Random();
    private float x1;
    private final float y1;
    private float x2;
    private final float y2;

    // Constructor que inicializa las coordenadas de la estrella
    public Estrella(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    // Método para obtener la coordenada x1 de la estrella
    public float getX1() {
        return x1;
    }

    // Método para obtener la coordenada y1 de la estrella
    public float getY1() {
        return y1;
    }

    // Método para obtener la coordenada x2 de la estrella
    public float getX2() {
        return x2;
    }

    // Método para obtener la coordenada y2 de la estrella
    public float getY2() {
        return y2;
    }

    // Método para mover la estrella en el eje x
    public void mover() {
        x1 -= 2;  // Ajusta la velocidad de las estrellas
        x2 -= 2;

        // Las estrellas vuelvan a aparecer cuando se salgan de la pantalla
        if (x1 < 0) {
            x1 = Juego.ancho;
            x2 = x1 + random.nextInt(5) - 2;
        }
    }
}
