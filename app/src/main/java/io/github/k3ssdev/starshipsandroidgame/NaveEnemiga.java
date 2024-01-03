package io.github.k3ssdev.starshipsandroidgame;

import static io.github.k3ssdev.starshipsandroidgame.Juego.random;

import android.graphics.RectF;

// Clase para representar las naves enemigas
public class NaveEnemiga {
    private float posX, posY;
    private float velocidad;

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
        if (posX + Juego.radio < 0) {
            posY = random.nextInt(Juego.alto);
            posX = Juego.ancho;
        }
    }

    public RectF getRect() {
        return new RectF((posX - Juego.radio), (posY - Juego.radio), (posX + Juego.radio), (posY + Juego.radio));
    }
}
