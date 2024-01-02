package io.github.k3ssdev.starshipsandroidgame;

import android.graphics.RectF;

// Clase para representar las naves enemigas
public class NaveEnemiga {
    private float posX, posY;
    private float velocidad;

    public NaveEnemiga(int ancho, int alto) {
        posY = Juego.random.nextInt(alto);
        posX = ancho;
        velocidad = 10; // Ajusta la velocidad de las naves enemigas
    }

    public void mover() {
        posX -= velocidad;

        // Asegúrate de que la nave vuelva a aparecer cuando se salga de la pantalla
        if (posX + Juego.radio < 0) {
            posY = Juego.random.nextInt(Juego.alto);
            posX = Juego.ancho;
        }
    }

    public RectF getRect() {
        return new RectF((posX - Juego.radio), (posY - Juego.radio), (posX + Juego.radio), (posY + Juego.radio));
    }
}
