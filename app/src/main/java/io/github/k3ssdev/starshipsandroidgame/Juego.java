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
    private static final float RADIO_JUGADOR_apr = 65;
    private static final int VELOCIDAD_MOVIMIENTO_SUAVE_apr = 50;

    // Variables para el juego
    public static int ancho_apr;
    public static int alto_apr;
    public static int radio_apr;
    public int posX_apr;
    public int posY_apr;
    public int posNaveEnemigaY_apr;
    private RectF rectNaveJugador_apr;
    private Bitmap bitmapNaveJugador_apr;
    private Bitmap bitmapNaveEnemiga_apr;
    private MediaPlayer musicaFondo_apr;
    private MediaPlayer mediaPlayerDisparo_apr;
    private final Paint fondo_apr = new Paint();
    private final Paint naveJugador_apr = new Paint();
    private final Paint naveEnemiga_apr = new Paint();
    private final Paint puntos_apr = new Paint();
    private Timer timerNavesEnemigas_apr;
    private Timer timerEstrellas_apr;
    private Timer timerDisparo_apr;
    private TimerTask increaseFrequencyTask_apr;
    private final Handler handler_apr = new Handler();
    private final List<Estrella> estrellas_apr = new ArrayList<>();
    private final List<NaveEnemiga> navesEnemigas_apr = new ArrayList<>();
    private final List<Disparo> disparos_apr = new ArrayList<>();
    private String nombreJugador_apr = "Jugador";
    private String dificultad_apr = "Normal";
    private boolean juegoEnPausa_apr = true;
    private boolean moviendose_apr = false;
    private long navesEnemigasDelay_apr = 4000;
    private Integer puntuacion_apr = 0;

    private final Random random_apr = new Random();

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
        bitmapNaveJugador_apr = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.player);
        bitmapNaveEnemiga_apr = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.enemy);

        if (juegoEnPausa_apr) {
            // Muestra un cuadro de diálogo solo si el juego está en pausa
            mostrarDialogoNombreYDificultad();
        } else {
            // Inicializa el juego solo si no está en pausa
            iniciarJuego();
        }
    }

    private void mostrarDialogoNombreYDificultad() {
        // Creamos un layout personalizado para el diálogo
        View viewInflated_apr = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nombre_dificultad, null);

        // Referenciamos los elementos del layout
        final EditText input_apr = viewInflated_apr.findViewById(R.id.editTextNombre);
        final Spinner dificultadSpinner = viewInflated_apr.findViewById(R.id.spinnerDificultad);

        // Configuramos el Spinner con las opciones de dificultad
        ArrayAdapter<CharSequence> adapter_apr = ArrayAdapter.createFromResource(
                getContext(),
                R.array.opciones_dificultad,
                android.R.layout.simple_spinner_item
        );
        adapter_apr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dificultadSpinner.setAdapter(adapter_apr);

        // Creamos el diálogo y establecemos su contenido personalizado
        AlertDialog.Builder builder_apr = new AlertDialog.Builder(getContext());
        builder_apr.setTitle("Ingrese su nombre y seleccione la dificultad");
        builder_apr.setView(viewInflated_apr);

        // Botón "Aceptar" en el cuadro de diálogo
        builder_apr.setPositiveButton("Aceptar", (dialog, which) -> {

            
            // Obtenemos el nombre ingresado y la dificultad seleccionada
            nombreJugador_apr = input_apr.getText().toString();
            dificultad_apr = dificultadSpinner.getSelectedItem().toString();

            // Mostramos un mensaje de bienvenida
            if (nombreJugador_apr == null || nombreJugador_apr.trim().isEmpty()) {
                nombreJugador_apr = "Jugador";
            }
            Toast.makeText(getContext(), "¡Bienvenido, " + nombreJugador_apr + "!", Toast.LENGTH_SHORT).show();

            // Inicializamos el juego después de hacer clic en Aceptar
            juegoEnPausa_apr = false;
            iniciarJuego();
        });

        // Botón "Cancelar" en el cuadro de diálogo
        builder_apr.setNegativeButton("Cancelar", (dialog, which) -> {
            // Si el usuario cancela, cierra la aplicación
            ((Activity) getContext()).finish();
        });

        builder_apr.setCancelable(false); // Evitamos que se cierre el cuadro de diálogo al tocar fuera de él
        builder_apr.show();
    }

    private void iniciarJuego() {
        fondo_apr.setColor(Color.BLACK);
        fondo_apr.setStyle(Paint.Style.FILL_AND_STROKE);
        naveJugador_apr.setColor(Color.YELLOW);
        naveJugador_apr.setStyle(Paint.Style.FILL_AND_STROKE);
        naveEnemiga_apr.setColor(Color.RED);
        naveEnemiga_apr.setStyle(Paint.Style.FILL_AND_STROKE);
        puntos_apr.setTextAlign(Paint.Align.RIGHT);
        puntos_apr.setTextSize(100);
        puntos_apr.setColor(Color.WHITE);

        // Temporizador para disparos
        timerDisparo_apr = new Timer();

        // Inicializa el MediaPlayer para la música de fondo
        musicaFondo_apr = MediaPlayer.create(getContext(), R.raw.music);
        musicaFondo_apr.setLooping(true); // Repetir la música de fondo
        musicaFondo_apr.start(); // Comienza la reproducción

        // Inicializa el MediaPlayer para el sonido de disparo
        mediaPlayerDisparo_apr = MediaPlayer.create(getContext(), R.raw.laser1);

        // Inicializa el temporizador para generar naves enemigas
        timerNavesEnemigas_apr = new Timer();
        timerNavesEnemigas_apr.schedule(new TimerTask() {
            @Override
            public void run() {
                handler_apr.post(() -> {
                    if (!juegoEnPausa_apr) {
                        generarNaveEnemiga();
                    }
                });
            }
        }, 0, navesEnemigasDelay_apr);

        // Inicializa el temporizador para aumentar gradualmente la frecuencia
        increaseFrequencyTask_apr = new TimerTask() {
            @Override
            public void run() {
                navesEnemigasDelay_apr -= 200; // Reduce el retraso en 0.2 segundos
                // Reagenda el temporizador con el retraso actualizado
                timerNavesEnemigas_apr.cancel();
                timerNavesEnemigas_apr = new Timer();
                timerNavesEnemigas_apr.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler_apr.post(() -> {
                            if (!juegoEnPausa_apr) {
                                generarNaveEnemiga();
                            }
                        });
                    }
                }, 0, navesEnemigasDelay_apr);
            }
        };

        // Programa la tarea para que se ejecute cada 2 minutos (aumentoFrecuencia)
        long aumentoFrecuencia = 120000;
        timerNavesEnemigas_apr.schedule(increaseFrequencyTask_apr, aumentoFrecuencia, aumentoFrecuencia);

        // Inicializa el temporizador para generar estrellas
        timerEstrellas_apr = new Timer();
        timerEstrellas_apr.schedule(new TimerTask() {
            @Override
            public void run() {
                handler_apr.post(() -> {
                    if (!juegoEnPausa_apr) {
                        generarEstrella();
                    }
                });
            }
        }, 0, 1000); // Ajusta la frecuencia de generación de estrellas (1000 milisegundos en este ejemplo)
    }

    // Método para dibujar la nave del jugador
    private void dibujarNaveJugador(Canvas canvas) {
        float left_apr = 450 - RADIO_JUGADOR_apr;
        float top_apr = posY_apr - RADIO_JUGADOR_apr;
        float right_apr = 450 + RADIO_JUGADOR_apr;
        float bottom_apr = posY_apr + RADIO_JUGADOR_apr;

        RectF rectNaveJugador_apr = new RectF(left_apr, top_apr, right_apr, bottom_apr);
        canvas.drawBitmap(bitmapNaveJugador_apr, null, rectNaveJugador_apr, null);
    }

    // Método para dibujar las naves enemigas
    private void dibujarNavesEnemigas(Canvas canvas) {
        for (NaveEnemiga nave : navesEnemigas_apr) {
            RectF rectNaveEnemiga_apr = nave.getRect();
            canvas.drawBitmap(bitmapNaveEnemiga_apr, null, rectNaveEnemiga_apr, null);
        }
    }

    private void generarNaveEnemiga() {
        NaveEnemiga nuevaNave = new NaveEnemiga(ancho_apr, alto_apr, dificultad_apr);
        navesEnemigas_apr.add(nuevaNave);
    }

    private void generarEstrella() {
        int x1_apr = random_apr.nextInt(ancho_apr);
        int y1_apr = random_apr.nextInt(alto_apr);
        int x2_apr = x1_apr + random_apr.nextInt(5) - 2;  // Pequeñas variaciones en la posición
        int y2_apr = y1_apr + random_apr.nextInt(5) - 2;
        estrellas_apr.add(new Estrella(x1_apr, y1_apr, x2_apr, y2_apr));
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
        while (estrellas_apr.size() < 5) {
            generarEstrella();
        }

        // Mueve y pinta las estrellas
        for (Estrella s : estrellas_apr) {
            canvas.drawLine(s.getX1_apr(), s.getY1_apr(), s.getX2_apr(), s.getY2_apr(), estrella);
        }

        // Pinta la nave del jugador
        dibujarNaveJugador(canvas);

        // Pinta las naves enemigas
        dibujarNavesEnemigas(canvas);

        // Pinta la puntuación
        canvas.drawText(puntuacion_apr.toString(), 150, 150, puntos_apr);

        // Pinta los disparos
        @SuppressLint("DrawAllocation") Paint disparoPaint = new Paint();
        disparoPaint.setColor(Color.GREEN);  // Ajusta el color del disparo según sea necesario
        for (Disparo disparo : disparos_apr) {
            float longitudLaser = 50;  // Ajusta la longitud del láser según sea necesario
            canvas.drawRect(disparo.getX_apr(), disparo.getY_apr(), disparo.getX_apr() + longitudLaser, disparo.getY_apr() + 5, disparoPaint);
        }
    }

    // Método para disparar
    private void disparar() {
        // Reproducir sonido de disparo
        if (mediaPlayerDisparo_apr != null) {
            mediaPlayerDisparo_apr.start();
        }

        // Lógica de disparo
        Disparo disparo_apr = new Disparo(450 + radio_apr, posY_apr);
        disparos_apr.add(disparo_apr);
    }

    // Detener la reproducción del sonido al finalizar el juego
    private void detenerSonidoDisparo() {
        if (mediaPlayerDisparo_apr != null) {
            mediaPlayerDisparo_apr.release();
            mediaPlayerDisparo_apr = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Verifica si el juego está en pausa antes de procesar el evento táctil
        if (juegoEnPausa_apr) {
            return true;  // Ignora los eventos táctiles mientras el juego está en pausa
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moviendose_apr = true;
                actualizarPosicionNaveSuavemente((int) event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (moviendose_apr) {
                    actualizarPosicionNaveSuavemente((int) event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                moviendose_apr = false;
                disparar();
                break;
        }
        return true;
    }

    // Método para actualizar la posición de la nave del jugador suavemente
    private void actualizarPosicionNaveSuavemente(int nuevaPosY) {
        float left_apr = 450 - RADIO_JUGADOR_apr;
        float top_apr = posY_apr - RADIO_JUGADOR_apr;
        float right_apr = 450 + RADIO_JUGADOR_apr;
        float bottom_apr = posY_apr + RADIO_JUGADOR_apr;

        if (posY_apr < nuevaPosY) {
            posY_apr += VELOCIDAD_MOVIMIENTO_SUAVE_apr;
            if (posY_apr > nuevaPosY) {
                posY_apr = nuevaPosY;
            }
        } else if (posY_apr > nuevaPosY) {
            posY_apr -= VELOCIDAD_MOVIMIENTO_SUAVE_apr;
            if (posY_apr < nuevaPosY) {
                posY_apr = nuevaPosY;
            }
        }

        rectNaveJugador_apr = new RectF(left_apr, top_apr, right_apr, bottom_apr);
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
        Iterator<Disparo> iterator = disparos_apr.iterator();
        while (iterator.hasNext()) {
            Disparo disparo_apr = iterator.next();
            disparo_apr.mover();

            // Eliminar disparos que salen de la pantalla
                       // Eliminar disparos que salen de la pantalla
            if (disparo_apr.getX_apr() > ancho_apr) {
                iterator.remove();
            }
        }
    }

    private void detectarColision() {
        List<NaveEnemiga> navesEliminadas_apr = new ArrayList<>();

        for (NaveEnemiga nave : navesEnemigas_apr) {
            //if (RectF.intersects(rectNaveJugador, nave.getRect())) {
            if (rectNaveJugador_apr != null && RectF.intersects(rectNaveJugador_apr, nave.getRect())) {

                // Colisión con nave enemiga, muestra "Game Over" y la puntuación
                mostrarGameOver();
                return;  // Sale del método para evitar la concurrencia
            }

            // Verifica colisiones con disparos
            Iterator<Disparo> disparosIterator_apr = disparos_apr.iterator();
            while (disparosIterator_apr.hasNext()) {
                Disparo disparo_apr = disparosIterator_apr.next();
                if (RectF.intersects(disparo_apr.getRect(), nave.getRect())) {
                    // Colisión con disparo, incrementa la puntuación y elimina la nave y el disparo
                    puntuacion_apr += 1;
                    disparosIterator_apr.remove();
                    navesEliminadas_apr.add(nave);
                }
            }
        }

        // Elimina las naves enemigas que colisionaron
        navesEnemigas_apr.removeAll(navesEliminadas_apr);

        // Genera nuevas naves enemigas para reemplazar las eliminadas
        for (int i = 0; i < navesEliminadas_apr.size(); i++) {
            generarNaveEnemiga();
        }
    }

    private void mostrarGameOver() {
        // Detener la música de fondo y sonidos
        detenerMusicaFondo();
        detenerSonidoDisparo();

        // Muestra "Game Over" y la puntuación
        ((Activity) getContext()).runOnUiThread(() -> {
            Toast.makeText(getContext(), "Game Over - Puntuación: " + puntuacion_apr, Toast.LENGTH_LONG).show();

            // Muestra un cuadro de diálogo para reiniciar o cerrar el juego
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("GAME OVER");
            builder.setMessage("¿Desea reiniciar el juego?");
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
        juegoEnPausa_apr = true;
        timerNavesEnemigas_apr.cancel();
        timerEstrellas_apr.cancel();
        increaseFrequencyTask_apr.cancel();
        navesEnemigasDelay_apr = 4000; // Restaura el retraso inicial
        navesEnemigas_apr.clear();
        estrellas_apr.clear();
        disparos_apr.clear();
    }

    private void reiniciarJuego() {
        // Detener la música de fondo y sonidos
        detenerMusicaFondo();
        detenerSonidoDisparo();

        // Restablece las variables del juego
        juegoEnPausa_apr = true;
        navesEnemigasDelay_apr = 4000; // Restaura el retraso inicial
        puntuacion_apr = 0;
        navesEnemigas_apr.clear();
        estrellas_apr.clear();
        disparos_apr.clear();

        // Muestra un cuadro de diálogo solo si el juego está en pausa
        mostrarDialogoNombreYDificultad();
    }


    private void detenerMusicaFondo() {
        if (musicaFondo_apr != null) {
            musicaFondo_apr.pause();
            // Detener el temporizador de disparo
            if (timerDisparo_apr != null) {
                timerDisparo_apr.cancel();
                timerDisparo_apr.purge();
            }
            // Asegúrate de liberar los recursos del MediaPlayer cuando ya no se necesiten
            musicaFondo_apr.release();
            musicaFondo_apr = null;
        }
    }


    private void moverNavesEnemigas() {
        Iterator<NaveEnemiga> iterator_apr = navesEnemigas_apr.iterator();
        while (iterator_apr.hasNext()) {
            NaveEnemiga naveEnemiga = iterator_apr.next();
            naveEnemiga.mover();
            if (naveEnemiga.getX() < 0) {
                // Elimina la nave enemiga si sale de la pantalla
                iterator_apr.remove();
            }
        }
    }

    private void moverEstrellas() {
        Iterator<Estrella> iterator = estrellas_apr.iterator();
        while (iterator.hasNext()) {
            Estrella estrella_apr = iterator.next();
            estrella_apr.mover();
            if (estrella_apr.getY1_apr() > alto_apr || estrella_apr.getY2_apr() > alto_apr) {
                // Elimina la estrella si sale de la pantalla
                iterator.remove();
            }
        }
    }

}
