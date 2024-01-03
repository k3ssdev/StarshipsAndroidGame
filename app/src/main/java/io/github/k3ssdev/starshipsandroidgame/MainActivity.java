package io.github.k3ssdev.starshipsandroidgame;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private Juego juego;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Oculta la barra de acción (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Oculta la barra de estado (barra superior)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        juego = findViewById(R.id.Pantalla);

        ViewTreeObserver obs = juego.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(() -> {
            // Se calcula el ancho y alto una vez ya se ha pintado el layout
            Juego.ancho = juego.getWidth();
            Juego.alto = juego.getHeight();
            juego.posX = 250;  // Establece la posición inicial de la nave del jugador
            juego.posY = Juego.alto / 2;  // Centra la nave del jugador verticalmente
            Juego.radio = 50;
            juego.posNaveEnemigaY = 50;
        });

        // Ejecutamos la actualización del juego cada 20 milisegundos
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> juego.actualizarJuego());
            }
        }, 0, 30);
    }
}
