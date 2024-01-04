package io.github.k3ssdev.starshipsandroidgame;

import android.graphics.RectF;

import java.util.Random;

// Clase para representar las naves enemigas
public class NaveEnemiga {

    private final Random random_apr = new Random();
    private float posX_apr, posY_apr;
    private float velocidad_apr;
    private static final float RADIO_NAVE_apr = 65; // Ajusta el tamaño del radio según sea necesario

    // Constructor que inicializa la posición y velocidad de la nave enemiga
    public NaveEnemiga(int ancho, int alto, String dificultad) {
        posY_apr = random_apr.nextInt(alto);
        posX_apr = ancho;
        ajustarVelocidad(dificultad); // Ajusta la velocidad según la dificultad
    }

    // Método para ajustar la velocidad de las naves en función de la dificultad
    public void ajustarVelocidad(String dificultad) {
        float multiplicador_apr = 1.0f; // Multiplicador por defecto para la dificultad Normal

        switch (dificultad) {
            case "Fácil":
                velocidad_apr = 5 * multiplicador_apr; // Velocidad más baja para Fácil
                break;
            case "Difícil":
                multiplicador_apr = 1.5f; // Multiplicador para Difícil
                velocidad_apr = 15 * multiplicador_apr; // Velocidad más alta para Difícil
                break;
            default:
                velocidad_apr = 10 * multiplicador_apr; // Por defecto, velocidad normal
                break;
        }
    }

    // Método para mover la nave enemiga
    public void mover() {
        posX_apr -= velocidad_apr;

        // La nave vuelve a aparecer cuando se sale de la pantalla
        if (posX_apr + RADIO_NAVE_apr < 0) {
            posY_apr = random_apr.nextInt(Juego.alto_apr);
            posX_apr = Juego.ancho_apr;
        }
    }

    // Método para obtener el rectángulo asociado a la nave enemiga
    public RectF getRect() {
        return new RectF((posX_apr - RADIO_NAVE_apr), (posY_apr - RADIO_NAVE_apr), (posX_apr + RADIO_NAVE_apr), (posY_apr + RADIO_NAVE_apr));
    }

    public int getX() {
        return (int) posX_apr;
    }
}
