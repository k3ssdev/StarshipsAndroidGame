package io.github.k3ssdev.starshipsandroidgame;

import android.graphics.RectF;

public class NaveEnemiga {
    private float x, y;
    private float velocidad;

    public NaveEnemiga(int anchoPantalla, int altoPantalla) {
        x = anchoPantalla;
        y = (float) Math.floor(Math.random() * (altoPantalla - 200));
        velocidad = 10;
    }

    public void mover() {
        x -= velocidad;
    }

    public RectF getRect() {
        return new RectF(x, y, x + 100, y + 100);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
