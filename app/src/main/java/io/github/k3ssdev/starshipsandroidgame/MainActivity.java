package io.github.k3ssdev.starshipsandroidgame;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Juego juego_apr;
    private final Handler handler_apr = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Oculta la barra de acción (ActionBar)
        ActionBar actionBar_apr = getSupportActionBar();
        if (actionBar_apr != null) {
            actionBar_apr.hide();
        }

        // Oculta la barra de estado (barra superior)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Oculta la barra de navegación (barra de botones)
        View decorView_apr = getWindow().getDecorView();
        int uiOptions_apr = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView_apr.setSystemUiVisibility(uiOptions_apr);

        setContentView(R.layout.activity_main);

        // Obtiene la referencia al componente de juego en el layout
        juego_apr = findViewById(R.id.Pantalla);

        // Calcula el ancho y alto una vez que se ha pintado el layout
        ViewTreeObserver obs = juego_apr.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(() -> {
            Juego.ancho_apr = juego_apr.getWidth();
            Juego.alto_apr = juego_apr.getHeight();
            juego_apr.posX_apr = 250;  // Establece la posición inicial de la nave del jugador
            juego_apr.posY_apr = Juego.alto_apr / 2;  // Centra la nave del jugador verticalmente
            Juego.radio_apr = 50;
            juego_apr.posNaveEnemigaY_apr = 50;
        });

        // Ejecuta la actualización del juego cada 30 milisegundos
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler_apr.post(() -> juego_apr.actualizarJuego());
            }
        }, 0, 30);
    }
}
