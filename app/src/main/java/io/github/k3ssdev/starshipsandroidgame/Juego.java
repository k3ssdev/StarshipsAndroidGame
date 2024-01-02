package io.github.k3ssdev.starshipsandroidgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Juego extends View {
    public static int ancho;
    public static int alto;
    public int posX;
    public int posY;
    public static int radio;
    public int posNaveEnemigaY;
    private RectF rectNaveJugador;
    private Integer puntuacion = 0;
    public static Random random = new Random();

    private Paint fondo = new Paint();
    private Paint naveJugador = new Paint();
    private Paint naveEnemiga = new Paint();
    private Paint puntos = new Paint();

    private Timer timerNavesEnemigas;
    private Timer timerEstrellas;
    private Handler handler = new Handler();

    private List<Estrella> estrellas = new ArrayList<>();
    private List<NaveEnemiga> navesEnemigas = new ArrayList<>();

    private long navesEnemigasDelay = 4000; // Retraso inicial para generar naves enemigas
    private long aumentoFrecuencia = 120000; // 2 minutos en milisegundos
    private TimerTask increaseFrequencyTask;

    public Juego(Context context) {
        super(context);
        init();
    }

    public Juego(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Juego(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        fondo.setColor(Color.BLACK);
        fondo.setStyle(Paint.Style.FILL_AND_STROKE);
        naveJugador.setColor(Color.YELLOW);
        naveJugador.setStyle(Paint.Style.FILL_AND_STROKE);
        naveEnemiga.setColor(Color.RED);
        naveEnemiga.setStyle(Paint.Style.FILL_AND_STROKE);
        puntos.setTextAlign(Paint.Align.RIGHT);
        puntos.setTextSize(100);
        puntos.setColor(Color.WHITE);

        // Inicializa el temporizador para generar naves enemigas
        timerNavesEnemigas = new Timer();
        timerNavesEnemigas.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        generarNaveEnemiga();
                    }
                });
            }
        }, 0, navesEnemigasDelay);

        // Inicializa el temporizador para aumentar gradualmente la frecuencia
        increaseFrequencyTask = new TimerTask() {
            @Override
            public void run() {
                navesEnemigasDelay -= 200; // Reduce el retraso en 0.2 segundos
                // Reagenda el temporizador con el retraso actualizado
                timerNavesEnemigas.cancel();
                timerNavesEnemigas = new Timer();
                timerNavesEnemigas.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                generarNaveEnemiga();
                            }
                        });
                    }
                }, 0, navesEnemigasDelay);
            }
        };

        // Programa la tarea para que se ejecute cada 2 minutos (aumentoFrecuencia)
        timerNavesEnemigas.schedule(increaseFrequencyTask, aumentoFrecuencia, aumentoFrecuencia);

        // Inicializa el temporizador para generar estrellas
        timerEstrellas = new Timer();
        timerEstrellas.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        generarEstrella();
                    }
                });
            }
        }, 0, 1000); // Ajusta la frecuencia de generación de estrellas (1000 milisegundos en este ejemplo)
    }

    private void generarNaveEnemiga() {
        NaveEnemiga nuevaNave = new NaveEnemiga(ancho, alto);
        navesEnemigas.add(nuevaNave);
    }

    private void generarEstrella() {
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(ancho);
            int y1 = random.nextInt(alto);
            int x2 = x1 + random.nextInt(5) - 2;  // Pequeñas variaciones en la posición
            int y2 = y1 + random.nextInt(5) - 2;
            estrellas.add(new Estrella(x1, y1, x2, y2));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Pinta el fondo con estrellas
        canvas.drawColor(Color.BLACK);  // Fondo negro
        Paint estrella = new Paint();
        estrella.setColor(Color.WHITE);
        estrella.setStrokeWidth(2);

        for (Estrella s : estrellas) {
            canvas.drawLine(s.getX1(), s.getY1(), s.getX2(), s.getY2(), estrella);
        }

        // Pinta la nave del jugador con margen a la izquierda
        rectNaveJugador = new RectF(250, (posY - radio), (250 + 2 * radio), (posY + radio));
        canvas.drawOval(rectNaveJugador, naveJugador);

        // Pinta las naves enemigas
        for (NaveEnemiga nave : navesEnemigas) {
            RectF rectNaveEnemiga = nave.getRect();
            canvas.drawOval(rectNaveEnemiga, naveEnemiga);
        }

        // Pinta la puntuación
        canvas.drawText(puntuacion.toString(), 150, 150, puntos);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Movimiento vertical
                posY = (int) event.getY();
                // Limita el movimiento dentro de los límites de la pantalla
                posY = Math.max(radio, Math.min(alto - radio, posY));
                invalidate();
                break;
        }
        return true;
    }

    public void actualizarJuego() {
        moverNavesEnemigas();
        moverEstrellas();
        detectarColision();
        invalidate();
    }

    private void moverNavesEnemigas() {
        for (NaveEnemiga nave : navesEnemigas) {
            nave.mover();
        }
    }

    private void moverEstrellas() {
        for (Estrella estrella : estrellas) {
            estrella.mover();
        }
    }

    private void detectarColision() {
        List<NaveEnemiga> navesEliminadas = new ArrayList<>();

        for (NaveEnemiga nave : navesEnemigas) {
            if (RectF.intersects(rectNaveJugador, nave.getRect())) {
                puntuacion += 1;
                navesEliminadas.add(nave);
            }
        }

        // Elimina las naves enemigas que colisionaron
        navesEnemigas.removeAll(navesEliminadas);

        // Genera nuevas naves enemigas para reemplazar las eliminadas
        for (int i = 0; i < navesEliminadas.size(); i++) {
            generarNaveEnemiga();
        }
    }

}
