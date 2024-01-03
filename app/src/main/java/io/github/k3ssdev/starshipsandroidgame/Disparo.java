package io.github.k3ssdev.starshipsandroidgame;

import android.graphics.RectF;

public class Disparo {
    private float x;
    private final float y;
    private static final int VELOCIDAD = 20; // Ajusta la velocidad del disparo según sea necesario
    private final RectF rect;

    public Disparo(float x, float y) {
        this.x = x;
        this.y = y;
        this.rect = new RectF(x, y, x + 10, y + 10); // Ajusta el tamaño del rectángulo según sea necesario
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public RectF getRect() {
        return rect;
    }

    public void mover() {
        x += VELOCIDAD;  // Cambia el eje de movimiento a la derecha
        // Actualiza la posición del rectángulo después de mover el disparo
        rect.left = x;
        rect.right = x + 30; // Ajusta el tamaño del rectángulo según sea necesario
    }
}
