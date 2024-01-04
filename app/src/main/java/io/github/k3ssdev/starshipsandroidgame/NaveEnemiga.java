package io.github.k3ssdev.starshipsandroidgame;

import android.graphics.RectF;

import java.util.Random;

// Clase para representar las naves enemigas
public class NaveEnemiga {

    private final Random random = new Random();
    private float posX, posY;
    private float velocidad;
    private static final float RADIO_NAVE = 65; // Ajusta el tamaño del radio según sea necesario

    // Constructor que inicializa la posición y velocidad de la nave enemiga
    public NaveEnemiga(int ancho, int alto, String dificultad) {
        posY = random.nextInt(alto);
        posX = ancho;
        ajustarVelocidad(dificultad); // Ajusta la velocidad según la dificultad
    }

    // Método para ajustar la velocidad de las naves en función de la dificultad
    public void ajustarVelocidad(String dificultad) {
        switch (dificultad) {
            case "Fácil":
                velocidad = 5; // Velocidad más baja para Fácil
                break;
            case "Difícil":
                velocidad = 15; // Velocidad más alta para Difícil
                break;
            default:
                velocidad = 10; // Por defecto, velocidad normal
                break;
        }
    }

    // Método para mover la nave enemiga
    public void mover() {
        posX -= velocidad;

        // La nave vuelve a aparecer cuando se salga de la pantalla
        if (posX + RADIO_NAVE < 0) {
            posY = random.nextInt(Juego.alto);
            posX = Juego.ancho;
        }
    }

    // Método para obtener el rectángulo asociado a la nave enemiga
    public RectF getRect() {
        return new RectF((posX - RADIO_NAVE), (posY - RADIO_NAVE), (posX + RADIO_NAVE), (posY + RADIO_NAVE));
    }

    public int getX() {
        return (int) posX;
    }
}
