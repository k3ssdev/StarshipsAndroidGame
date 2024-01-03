package io.github.k3ssdev.starshipsandroidgame;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Juego juego;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        juego = findViewById(R.id.Pantalla);

        ViewTreeObserver obs = juego.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Se calcula el ancho y alto una vez ya se ha pintado el layout
                juego.ancho = juego.getWidth();
                juego.alto = juego.getHeight();
                juego.posX = 250;  // Establece la posición inicial de la nave del jugador
                juego.posY = juego.alto / 2;  // Centra la nave del jugador verticalmente
                juego.radio = 50;
                juego.posNaveEnemigaY = 50;
            }
        });

        // Ejecutamos la actualización del juego cada 20 milisegundos
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        juego.actualizarJuego();
                    }
                });
            }
        }, 0, 30);
    }
}