package io.github.k3ssdev.starshipsandroidgame;

import android.graphics.RectF;

import java.util.Random;

// Clase para representar las naves enemigas
public class NaveEnemiga {
    private Random random = new Random();
    private float posX, posY;
    private float velocidad;
    private static final float RADIO_NAVE = 65; // Ajusta el tamaño del radio según sea necesario

    public NaveEnemiga(int ancho, int alto) {
        posY = random.nextInt(alto);
        posX = ancho;
        velocidad = 10; // Ajusta la velocidad de las naves enemigas
    }

    // Añade un nuevo constructor a la clase NaveEnemiga
    public NaveEnemiga(int ancho, int alto, String dificultad) {
        posY = random.nextInt(alto);
        posX = ancho;
        // Ajusta la velocidad de las naves en función de la dificultad
        ajustarVelocidad(dificultad);
    }

    // Añade el método ajustarVelocidad a la clase NaveEnemiga
    public void ajustarVelocidad(String dificultad) {
        // Ajusta la velocidad de las naves en función de la dificultad
        switch (dificultad) {
            case "Fácil":
                velocidad = 5; // Velocidad más baja para Fácil
                break;
            case "Normal":
                velocidad = 10; // Velocidad normal para Normal
                break;
            case "Difícil":
                velocidad = 15; // Velocidad más alta para Difícil
                break;
            default:
                velocidad = 10; // Por defecto, velocidad normal
                break;
        }
    }

    public void mover() {
        posX -= velocidad;

        // Asegúrate de que la nave vuelva a aparecer cuando se salga de la pantalla
        if (posX + RADIO_NAVE < 0) {
            posY = random.nextInt(Juego.alto);
            posX = Juego.ancho;
        }
    }

    public RectF getRect() {
        return new RectF((posX - RADIO_NAVE), (posY - RADIO_NAVE), (posX + RADIO_NAVE), (posY + RADIO_NAVE));
    }
}
