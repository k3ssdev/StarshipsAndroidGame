package io.github.k3ssdev.starshipsandroidgame;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Iterator;
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

    private String nombreJugador = "Jugador"; // Nombre predeterminado
    private String dificultad = "Normal"; // Dificultad predeterminada

    private static final int VELOCIDAD_NAVE = 40; // Ajusta la velocidad según sea necesario

    private boolean juegoEnPausa = true; // Bandera para controlar si el juego está en pausa

    private static final int VELOCIDAD_DISPARO = 20; // Ajusta la velocidad del disparo según sea necesario
    private List<Disparo> disparos = new ArrayList<>();


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
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtenemos el nombre ingresado y la dificultad seleccionada
                nombreJugador = input.getText().toString();
                dificultad = dificultadSpinner.getSelectedItem().toString();

                // Mostramos un mensaje de bienvenida
                Toast.makeText(getContext(), "¡Bienvenido, " + nombreJugador + "!", Toast.LENGTH_SHORT).show();

                // Inicializamos el juego después de hacer clic en Aceptar
                juegoEnPausa = false;
                iniciarJuego();
            }
        });

        // Botón "Cancelar" en el cuadro de diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario cancela, cierra la aplicación
                ((Activity) getContext()).finish();
            }
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

        // Inicializa el temporizador para generar naves enemigas
        timerNavesEnemigas = new Timer();
        timerNavesEnemigas.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (!juegoEnPausa) {
                            generarNaveEnemiga();
                        }
                    }
                });
            }
        }, 0, navesEnemigasDelay);

        // ... (resto del código)

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
                                if (!juegoEnPausa) {
                                    generarNaveEnemiga();
                                }
                            }
                        });
                    }
                }, 0, navesEnemigasDelay);
            }
        };

        // Programa la tarea para que se ejecute cada 2 minutos (aumentoFrecuencia)
        timerNavesEnemigas.schedule(increaseFrequencyTask, aumentoFrecuencia, aumentoFrecuencia);

        // ... (resto del código)

        // Inicializa el temporizador para generar estrellas
        timerEstrellas = new Timer();
        timerEstrellas.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (!juegoEnPausa) {
                            generarEstrella();
                        }
                    }
                });
            }
        }, 0, 1000); // Ajusta la frecuencia de generación de estrellas (1000 milisegundos en este ejemplo)
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

    private void ajustarVelocidadNavesEnemigas() {
        for (NaveEnemiga nave : navesEnemigas) {
            nave.ajustarVelocidad(dificultad);
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

        // Genera estrellas si hay menos de 5 en la pantalla
        while (estrellas.size() < 5) {
            generarEstrella();
        }

        // Mueve y pinta las estrellas
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

        // Pinta los disparos
        Paint disparoPaint = new Paint();
        disparoPaint.setColor(Color.BLUE);
        for (Disparo disparo : disparos) {
            canvas.drawCircle(disparo.getX(), disparo.getY(), 10, disparoPaint);
        }
    }

    private void disparar() {
        Disparo disparo = new Disparo(250 + radio, posY);
        disparos.add(disparo);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Verifica si el juego está en pausa antes de procesar el evento táctil
        if (juegoEnPausa) {
            return true;  // Ignora los eventos táctiles mientras el juego está en pausa
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Movimiento vertical
                posY = (int) event.getY();
                // Limita el movimiento dentro de los límites de la pantalla
                posY = Math.max(radio, Math.min(alto - radio, posY));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                disparar();
                break;
        }
        return true;
    }


    private void moverNaveSuavemente(int posYActual, int nuevaPosY) {
        if (posYActual < nuevaPosY) {
            posY += VELOCIDAD_NAVE;
            if (posY > nuevaPosY) {
                posY = nuevaPosY;
            }
        } else if (posYActual > nuevaPosY) {
            posY -= VELOCIDAD_NAVE;
            if (posY < nuevaPosY) {
                posY = nuevaPosY;
            }
        }
    }

    public void actualizarJuego() {
        moverNavesEnemigas();
        moverEstrellas();
        detectarColision();
        moverDisparos();
        detectarColisionDisparos();
        detectarColision();

        invalidate();
    }

    // Agrega estos métodos a la clase Juego
    private void moverDisparos() {
        for (Disparo disparo : disparos) {
            disparo.mover();
        }
    }

    private void detectarColisionDisparos() {
        List<Disparo> disparosEliminados = new ArrayList<>();

        for (Disparo disparo : disparos) {
            List<NaveEnemiga> navesEliminadas = new ArrayList<>();

            for (NaveEnemiga nave : navesEnemigas) {
                if (RectF.intersects(new RectF(disparo.getX(), disparo.getY(), disparo.getX(), disparo.getY()), nave.getRect())) {
                    navesEliminadas.add(nave);
                    disparosEliminados.add(disparo);
                }
            }

            navesEnemigas.removeAll(navesEliminadas);
        }

        disparos.removeAll(disparosEliminados);
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

        Iterator<NaveEnemiga> iterator = navesEnemigas.iterator();
        while (iterator.hasNext()) {
            NaveEnemiga nave = iterator.next();

            if (RectF.intersects(rectNaveJugador, nave.getRect())) {
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
        // Muestra el mensaje de "Game Over" y la puntuación
        Toast.makeText(getContext(), "Game Over. Puntuación: " + puntuacion, Toast.LENGTH_SHORT).show();
        // Reinicia el juego o realiza alguna acción adicional según tus necesidades
        reiniciarJuego();
    }

    private void reiniciarJuego() {
        // Aquí puedes reiniciar las variables del juego, reiniciar timers, etc.
        // Por ejemplo, podrías reiniciar la actividad o reiniciar el temporizador de generación de naves enemigas.
        // También puedes agregar lógica adicional según tus necesidades.
        // Este método debe realizar todas las acciones necesarias para reiniciar el juego.
        // Puedes personalizarlo según tus necesidades específicas.
        init();  // Reinicia el juego llamando al método init
    }
}

