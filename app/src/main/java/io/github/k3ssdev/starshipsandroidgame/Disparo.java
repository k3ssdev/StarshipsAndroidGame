package io.github.k3ssdev.starshipsandroidgame;

import android.graphics.RectF;

// Clase para representar los disparos realizados por la nave del jugador
public class Disparo {

    private float x_apr;
    private final float y_apr;
    private static final int VELOCIDAD_apr = 20; // Ajusta la velocidad del disparo según sea necesario
    private final RectF rect;

    // Constructor que inicializa la posición del disparo y el rectángulo asociado
    public Disparo(float x, float y) {
        this.x_apr = x;
        this.y_apr = y;
        this.rect = new RectF(x, y, x + 10, y + 10); // Ajusta el tamaño del rectángulo según sea necesario
    }

    // Método para obtener la coordenada x del disparo
    public float getX_apr() {
        return x_apr;
    }

    // Método para obtener la coordenada y del disparo
    public float getY_apr() {
        return y_apr;
    }

    // Método para obtener el rectángulo asociado al disparo
    public RectF getRect() {
        return rect;
    }

    // Método para mover el disparo en el eje x
    public void mover() {
        x_apr += VELOCIDAD_apr;  // Cambia el eje de movimiento a la derecha
        // Actualiza la posición del rectángulo después de mover el disparo
        rect.left = x_apr;
        rect.right = x_apr + 30; // Ajusta el tamaño del rectángulo del laser
    }
}
