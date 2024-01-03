package io.github.k3ssdev.starshipsandroidgame;

// Music: https://opengameart.org/content/space-dimensions-8bitretro-version
// Laser sound: https://opengameart.org/content/laser-fire
// Sprites: https://opengameart.org/content/space-ship-construction-kit

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Juego extends View {

    // Constantes para el juego
    private static final float RADIO_JUGADOR = 65;
    private static final int VELOCIDAD_MOVIMIENTO_SUAVE = 50;

    // Variables para el juego
    public static int ancho;
    public static int alto;
    public static int radio;
    public int posX;
    public int posY;
    public int posNaveEnemigaY;
    private RectF rectNaveJugador;
    private Bitmap bitmapNaveJugador;
    private Bitmap bitmapNaveEnemiga;
    private MediaPlayer musicaFondo;
    private MediaPlayer mediaPlayerDisparo;
    private final Paint fondo = new Paint();
    private final Paint naveJugador = new Paint();
    private final Paint naveEnemiga = new Paint();
    private final Paint puntos = new Paint();
    private Timer timerNavesEnemigas;
    private Timer timerEstrellas;
    private Timer timerDisparo;
    private TimerTask increaseFrequencyTask;
    private final Handler handler = new Handler();
    private final List<Estrella> estrellas = new ArrayList<>();
    private final List<NaveEnemiga> navesEnemigas = new ArrayList<>();
    private final List<Disparo> disparos = new ArrayList<>();
    private String nombreJugador = "Jugador";
    private String dificultad = "Normal";
    private boolean juegoEnPausa = true;
    private boolean moviendose = false;
    private long navesEnemigasDelay = 4000;
    private Integer puntuacion = 0;

    private final Random random = new Random();

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

        // Carga las imágenes de jugador y enemigo
        bitmapNaveJugador = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.player);
        bitmapNaveEnemiga = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.enemy);

        if (juegoEnPausa) {
            // Muestra un cuadro de diálogo solo si el juego está en pausa
            mostrarDialogoNombreYDificultad();
        } else {
            // Inicializa el juego solo si no está en pausa
            iniciarJuego();

        }
    }

    private void mostrarDialogoNombreYDificultad() {
        // Creamos un layout personalizado para el diálogo
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nombre_dificultad, null);

        // Referenciamos los elementos del layout
        final EditText input = viewInflated.findViewById(R.id.editTextNombre);
        final Spinner dificultadSpinner = viewInflated.findViewById(R.id.spinnerDificultad);

        // Configuramos el Spinner con las opciones de dificultad
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.opciones_dificultad,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dificultadSpinner.setAdapter(adapter);

        // Creamos el diálogo y establecemos su contenido personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ingrese su nombre y seleccione la dificultad");
        builder.setView(viewInflated);

        // Botón "Aceptar" en el cuadro de diálogo
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Obtenemos el nombre ingresado y la dificultad seleccionada
            nombreJugador = input.getText().toString();
            dificultad = dificultadSpinner.getSelectedItem().toString();

            // Mostramos un mensaje de bienvenida
            Toast.makeText(getContext(), "¡Bienvenido, " + nombreJugador + "!", Toast.LENGTH_SHORT).show();

            // Inicializamos el juego después de hacer clic en Aceptar
            juegoEnPausa = false;
            iniciarJuego();
        });

        // Botón "Cancelar" en el cuadro de diálogo
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            // Si el usuario cancela, cierra la aplicación
            ((Activity) getContext()).finish();
        });

        builder.setCancelable(false); // Evitamos que se cierre el cuadro de diálogo al tocar fuera de él
        builder.show();
    }

    private void iniciarJuego() {
        fondo.setColor(Color.BLACK);
        fondo.setStyle(Paint.Style.FILL_AND_STROKE);
        naveJugador.setColor(Color.YELLOW);
        naveJugador.setStyle(Paint.Style.FILL_AND_STROKE);
        naveEnemiga.setColor(Color.RED);
        naveEnemiga.setStyle(Paint.Style.FILL_AND_STROKE);
        puntos.setTextAlign(Paint.Align.RIGHT);
        puntos.setTextSize(100);
        puntos.setColor(Color.WHITE);




        // Temporizador para disparos
        timerDisparo = new Timer();

        // Inicializa el MediaPlayer para la música de fondo
        musicaFondo = MediaPlayer.create(getContext(), R.raw.music);
        musicaFondo.setLooping(true); // Repetir la música de fondo
        musicaFondo.start(); // Comienza la reproducción

        // Inicializa el MediaPlayer para el sonido de disparo
        mediaPlayerDisparo = MediaPlayer.create(getContext(), R.raw.laser1);

        // Inicializa el temporizador para generar naves enemigas
        timerNavesEnemigas = new Timer();
        timerNavesEnemigas.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (!juegoEnPausa) {
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
                        handler.post(() -> {
                            if (!juegoEnPausa) {
                                generarNaveEnemiga();
                            }
                        });
                    }
                }, 0, navesEnemigasDelay);
            }
        };

        // Programa la tarea para que se ejecute cada 2 minutos (aumentoFrecuencia)
        long aumentoFrecuencia = 120000;
        timerNavesEnemigas.schedule(increaseFrequencyTask, aumentoFrecuencia, aumentoFrecuencia);

        // Inicializa el temporizador para generar estrellas
        timerEstrellas = new Timer();
        timerEstrellas.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (!juegoEnPausa) {
                        generarEstrella();
                    }
                });
            }
        }, 0, 1000); // Ajusta la frecuencia de generación de estrellas (1000 milisegundos en este ejemplo)
    }


    // Método para dibujar la nave del jugador
    private void dibujarNaveJugador(Canvas canvas) {
        float left = 450 - RADIO_JUGADOR;
        float top = posY - RADIO_JUGADOR;
        float right = 450 + RADIO_JUGADOR;
        float bottom = posY + RADIO_JUGADOR;

        RectF rectNaveJugador = new RectF(left, top, right, bottom);
        canvas.drawBitmap(bitmapNaveJugador, null, rectNaveJugador, null);
    }





    // Método para dibujar las naves enemigas
    private void dibujarNavesEnemigas(Canvas canvas) {
        for (NaveEnemiga nave : navesEnemigas) {
            RectF rectNaveEnemiga = nave.getRect();
            canvas.drawBitmap(bitmapNaveEnemiga, null, rectNaveEnemiga, null);
        }
    }


    private void generarNaveEnemiga() {
        NaveEnemiga nuevaNave = new NaveEnemiga(ancho, alto, dificultad);
        navesEnemigas.add(nuevaNave);
    }

    private void generarEstrella() {
        int x1 = random.nextInt(ancho);
        int y1 = random.nextInt(alto);
        int x2 = x1 + random.nextInt(5) - 2;  // Pequeñas variaciones en la posición
        int y2 = y1 + random.nextInt(5) - 2;
        estrellas.add(new Estrella(x1, y1, x2, y2));
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Pinta el fondo con estrellas
        canvas.drawColor(Color.BLACK);  // Fondo negro
        @SuppressLint("DrawAllocation") Paint estrella = new Paint();
        estrella.setColor(Color.WHITE);
        estrella.setStrokeWidth(2);

        // Genera estrellas si hay menos de 5 en la pantalla
        while (estrellas.size() < 5) {
            generarEstrella();
        }

        // Mueve y pinta las estrellas
        for (Estrella s : estrellas) {
            canvas.drawLine(s.getX1(), s.getY1(), s.getX2(), s.getY2(), estrella);
        }


        // Pinta la nave del jugador
        dibujarNaveJugador(canvas);

        // Pinta las naves enemigas
        dibujarNavesEnemigas(canvas);

        // Pinta la puntuación
        canvas.drawText(puntuacion.toString(), 150, 150, puntos);

        // Pinta los disparos
        @SuppressLint("DrawAllocation") Paint disparoPaint = new Paint();
        disparoPaint.setColor(Color.GREEN);  // Ajusta el color del disparo según sea necesario
        for (Disparo disparo : disparos) {
            float longitudLaser = 50;  // Ajusta la longitud del láser según sea necesario
            canvas.drawRect(disparo.getX(), disparo.getY(), disparo.getX() + longitudLaser, disparo.getY() + 5, disparoPaint);
        }
    }


    // Método para disparar
    private void disparar() {
        // Reproducir sonido de disparo
        if (mediaPlayerDisparo != null) {
            mediaPlayerDisparo.start();
        }

        // Lógica de disparo
        Disparo disparo = new Disparo(450 + radio, posY);
        disparos.add(disparo);
    }

    // Detener la reproducción del sonido al finalizar el juego
    private void detenerSonidoDisparo() {
        if (mediaPlayerDisparo != null) {
            mediaPlayerDisparo.release();
            mediaPlayerDisparo = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Verifica si el juego está en pausa antes de procesar el evento táctil
        if (juegoEnPausa) {
            return true;  // Ignora los eventos táctiles mientras el juego está en pausa
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moviendose = true;
                actualizarPosicionNaveSuavemente((int) event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (moviendose) {
                    actualizarPosicionNaveSuavemente((int) event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                moviendose = false;
                disparar();
                break;
        }
        return true;
    }


    // Método para actualizar la posición de la nave del jugador suavemente
    private void actualizarPosicionNaveSuavemente(int nuevaPosY) {
        float left = 450 - RADIO_JUGADOR;
        float top = posY - RADIO_JUGADOR;
        float right = 450 + RADIO_JUGADOR;
        float bottom = posY + RADIO_JUGADOR;

        if (posY < nuevaPosY) {
            posY += VELOCIDAD_MOVIMIENTO_SUAVE;
            if (posY > nuevaPosY) {
                posY = nuevaPosY;
            }
        } else if (posY > nuevaPosY) {
            posY -= VELOCIDAD_MOVIMIENTO_SUAVE;
            if (posY < nuevaPosY) {
                posY = nuevaPosY;
            }
        }

        rectNaveJugador = new RectF(left, top, right, bottom);
        invalidate();
    }


    public void actualizarJuego() {
        moverNavesEnemigas();
        moverEstrellas();
        moverDisparos(); // Agregado para mover los disparos
        detectarColision();
        invalidate();
    }

    private void moverDisparos() {
        Iterator<Disparo> iterator = disparos.iterator();
        while (iterator.hasNext()) {
            Disparo disparo = iterator.next();
            disparo.mover();

            // Eliminar disparos que salen de la pantalla
            if (disparo.getX() > ancho) {
                iterator.remove();
            }
        }
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
            //if (RectF.intersects(rectNaveJugador, nave.getRect())) {
            if (rectNaveJugador != null && RectF.intersects(rectNaveJugador, nave.getRect())) {

                // Colisión con nave enemiga, muestra "Game Over" y la puntuación
                mostrarGameOver();
                return;  // Sale del método para evitar la concurrencia
            }

            // Verifica colisiones con disparos
            Iterator<Disparo> disparosIterator = disparos.iterator();
            while (disparosIterator.hasNext()) {
                Disparo disparo = disparosIterator.next();
                if (RectF.intersects(disparo.getRect(), nave.getRect())) {
                    // Colisión con disparo, incrementa la puntuación y elimina la nave y el disparo
                    puntuacion += 1;
                    disparosIterator.remove();
                    navesEliminadas.add(nave);
                }
            }
        }

        // Elimina las naves enemigas que colisionaron
        navesEnemigas.removeAll(navesEliminadas);

        // Genera nuevas naves enemigas para reemplazar las eliminadas
        for (int i = 0; i < navesEliminadas.size(); i++) {
            generarNaveEnemiga();
        }
    }

    private void mostrarGameOver() {
        // Detener la música de fondo y sonidos
        detenerMusicaFondo();
        detenerSonidoDisparo();

        // Muestra "Game Over" y la puntuación
        ((Activity) getContext()).runOnUiThread(() -> {
            Toast.makeText(getContext(), "Game Over - Puntuación: " + puntuacion, Toast.LENGTH_LONG).show();

            // Muestra un cuadro de diálogo para reiniciar o cerrar el juego
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("¿Desea reiniciar el juego?");
            builder.setPositiveButton("Sí", (dialog, which) -> reiniciarJuego());

            // Agrega botón negativo para cerrar la aplicación
            builder.setNegativeButton("No", (dialog, which) -> {
                // Cierra la actividad actual (la aplicación)
                ((Activity) getContext()).finish();
            });

            // Muestra realmente el cuadro de diálogo
            builder.show();
        });

        // Pausa el juego y realiza otras acciones según sea necesario
        pausarJuego();
    }



    private void pausarJuego() {
        // Pausa el juego y realiza otras acciones según sea necesario
        juegoEnPausa = true;
        timerNavesEnemigas.cancel();
        timerEstrellas.cancel();
        increaseFrequencyTask.cancel();
        navesEnemigasDelay = 4000; // Restaura el retraso inicial
        navesEnemigas.clear();
        estrellas.clear();
        disparos.clear();
    }


    private void reiniciarJuego() {
        init();  // Reinicia el juego llamando al método init
    }

    private void detenerMusicaFondo() {
        if (musicaFondo != null) {
            musicaFondo.pause();
            // Detener el temporizador de disparo
            if (timerDisparo != null) {
                timerDisparo.cancel();
                timerDisparo.purge();
            }
            // Asegúrate de liberar los recursos del MediaPlayer cuando ya no se necesiten
            musicaFondo.release();
            musicaFondo = null;
        }
    }

}

