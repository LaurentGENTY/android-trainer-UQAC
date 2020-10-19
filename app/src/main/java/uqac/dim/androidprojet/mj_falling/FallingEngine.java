package uqac.dim.androidprojet.mj_falling;

import android.app.Service;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by laure on 29/03/2018.
 */

class FallingEngine {

    //liste de pieces
    private ArrayList<Ring> fallingRings = new ArrayList<Ring>();

    //mon personnage
    private Catcher catcher = null;

    //l'activité link
    private Falling fallingActivity = null;

    //vue associée
    private FallingView fW;

    //sensors
    private SensorManager sM;
    private Sensor mAccelerometre;

    //score du joueur (option) et vies
    private int score;
    private int lives;

    //thread pour faire descendre les pieces
    private FallingThread thread;

    //variables de taille écran
    public static int WIDTH;
    public static int HEIGHT;

    //appli en pause
    private Object pauseLock = new Object();
    private static boolean paused = false;

    //determiner la direction pour le sprite
    private float _lastY = 0;
    private float _newY = 0;

    //mutex
    private Object mutex;

    public FallingEngine(Falling fA, FallingView fW, Object mutex) {
        init(fA);
        this.fW = fW;
        this.mutex = mutex;
    }

    private void init(Falling fA) {

        //l'activité c'est celle du paramètre
        setFallingActivity(fA);

        //classe permettant d'accéder aux capteurs : instancie
        setsM((SensorManager) fA.getBaseContext().getSystemService(Service.SENSOR_SERVICE));

        //on link les sensors etc
        setmAccelerometre(sM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

        setScore(0);
        setLives(3);
        fallingActivity.setLives(3);

        thread = new FallingThread();
        thread.start();
    }

    public ArrayList<Ring> getFallingRings() {
        return fallingRings;
    }

    public void setFallingRings(ArrayList<Ring> fallingRings) {
        this.fallingRings = fallingRings;
    }

    public Catcher getCatcher() {
        return catcher;
    }

    public void setCatcher(Catcher catcher) {
        this.catcher = catcher;
    }

    public void setFallingActivity(Falling fallingActivity) {
        this.fallingActivity = fallingActivity;
    }

    //FONCTIONS SENSORS

    //event listener
    SensorEventListener mSEL = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float newY = 0;

            changementDirectionSprite(newY);

            //nouvelles valeurs dans values
            newY = sensorEvent.values[0];
            if (catcher != null) {
                //la hitbox correspond aux coordonnées de la boule actuellement

                RectF catcherHitbox = catcher.setNewCoordinates(-newY);

                if (fallingRings != null) {
                    ArrayList<Integer> temp = new ArrayList<>();
                        /*for (Ring r : temp) {
                            //si un wall touche la balle
                            RectF rect = new RectF(r.getLeft(), r.getTop(), r.getRight(), r.getBottom());
                            verifierIntersection(rect,catcherHitbox,r);
                        }*/
                    for(int i=0; i < fallingRings.size();i++)
                    {
                        RectF rect = new RectF(fallingRings.get(i).getLeft(), fallingRings.get(i).getTop(), fallingRings.get(i).getRight(), fallingRings.get(i).getBottom());
                        verifierIntersection(rect,catcherHitbox,fallingRings.get(i),temp,i);
                    }
                    for(int j=0;j<temp.size();j++)
                    {
                        fallingRings.remove(fallingRings.get(j));
                    }
                }

            }
            _lastY = newY;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    private void changementDirectionSprite(float newY) {
        if(_lastY > newY)
        {
            Catcher.setDIRECTION(DIRECTION.DROITE);
        }
        else if(_lastY < newY)
        {
            Catcher.setDIRECTION(DIRECTION.GAUCHE);
        }
        else
        {
            Catcher.setDIRECTION(DIRECTION.NORMALE);
        }
        _newY = newY;
    }

    private void verifierIntersection(RectF ring, RectF catcherHitbox,Ring r,ArrayList<Integer> delete,int i) {
        if ((ring.left >= catcherHitbox.left &&
                ring.left < catcherHitbox.right &&
                ring.bottom >= catcherHitbox.top &&
                ring.bottom < catcherHitbox.bottom) ||
                (ring.top > catcherHitbox.top && ring.top < catcherHitbox.bottom &&
                        ring.right >= catcherHitbox.left && ring.right < catcherHitbox.right
                ))
        {
            Log.i("GETAAA","get");
            score = score + 1;
            Log.i("SCORECOUNT", Integer.toString(score));
            fallingActivity.setScore(score);
            delete.add(i);
        }
    }

    public Sensor getmAccelerometre() {
        return mAccelerometre;
    }

    public void setmAccelerometre(Sensor mAccelerometre) {
        this.mAccelerometre = mAccelerometre;
    }

    public void setsM(SensorManager sM) {
        this.sM = sM;
    }

    public void pause() {
        sM.unregisterListener(mSEL, mAccelerometre);
        paused = true;
    }

    public void resume() {
        sM.registerListener(mSEL, mAccelerometre, sM.SENSOR_DELAY_GAME);
        paused = false;

    }

    public int getScore() {
        return score;
    }

    public Falling getFallingActivity() {
        return fallingActivity;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void addRing(Ring ring) {
        fallingRings.add(ring);
    }

    public float get_lastY() {
        return _lastY;
    }

    public float get_newY() {
        return _newY;
    }

    public void stop() {
        thread.setFalling(false);
        boolean fin = true;
        while (fin) {
            //on termine le thread
            try {
                thread.join();
                fin = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class FallingThread extends Thread {

        private boolean falling = true;

        private int time = 0;
        private int accelerationTime;
        private int newRingTime = 100;

        @Override
        public void run() {
            while (falling) {
                synchronized (mutex) {
                    sortieEcran();
                    testLives();
                    initRandomRing();
                    descenteRings();
                    accelerationRings();

                    mutex.notifyAll();

                    try {
                        //on met en pause le thread
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }

                synchronized (mutex) {
                    // Wait until being notified
                    try {
                        mutex.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /*sortieEcran();
                testLives();
                initRandomRing();
                descenteRings();
                accelerationRings();
                try {
                    //on met en pause le thread
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }*/
            }
        }

        private void accelerationRings() {
            accelerationTime++;
            if (accelerationTime >= 300) {
                Log.i("ACCELERATION", "Augmentation vitesse + diminution temps nouveau ring");
                Ring.setySpeed(Ring.getySpeed() * 1.1f);
                accelerationTime = 0;
                newRingTime /= 1.2;
            }
        }

        private void testLives() {
            if (lives <= 0) {
                fallingActivity.getScoreFinal(getScore());
                Log.i("ACTO", Integer.toString(getScore()));
                fallingActivity.end();
            }
        }

        private void descenteRings() {
            for (Ring r : fallingRings) {
                r.setY(r.getY() + Ring.getySpeed());
            }
        }

        private void sortieEcran() {
            if (fallingRings != null) {
                if (!fallingRings.isEmpty()) {
                    /*for (Ring r : fallingRings) {
                        if (r.getY() + Ring.RADIUS > HEIGHT) {
                            fallingRings.remove(r);
                            lives--;
                            fallingActivity.setLives(lives);
                            Log.i("Vies", Integer.toString(lives));
                        }
                    }*/
                    ArrayList<Integer>delete = new ArrayList<>();

                    Ring temp;
                    for(int i=0; i<fallingRings.size();i++)
                    {
                        temp = fallingRings.get(i);
                        if (temp.getY() + Ring.RADIUS > HEIGHT) {
                            delete.add(i);
                            lives--;
                            fallingActivity.setLives(lives);
                            Log.i("Vies", Integer.toString(lives));
                        }
                    }
                    for(int j=0;j<delete.size();j++)
                    {
                        fallingRings.remove(fallingRings.get(j));
                    }
                }
            }

        }

        //fonction permettant d'ajouter une nouvelle piece toutes les X secondes
        private void initRandomRing() {
            time++;
            if (time > newRingTime) {
                float r = rand();
                Ring ring = new Ring(r);
                fallingRings.add(ring);
                fW.setFallingRings(fallingRings);
                fallingActivity.showRings();
                time = 0;
            }
        }

        //fonction permettant de positionner une nouvelle piece aléatoirement sur la vue
        private float rand() {
            Random r = new Random();
            return 0 + r.nextFloat() * (WIDTH - 0);
        }

        public void setFalling(boolean falling) {
            this.falling = falling;
        }

        public boolean getFalling() {
            return this.falling;
        }


        private void engine() {
            sortieEcran();
            testLives();
            initRandomRing();
            descenteRings();
            accelerationRings();
        }
    }


    public static int getWIDTH() {
        return WIDTH;
    }

    public static void setWIDTH(int WIDTH) {
        FallingEngine.WIDTH = WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static void setHEIGHT(int HEIGHT) {
        FallingEngine.HEIGHT = HEIGHT;
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean paused) {
        FallingEngine.paused = paused;
    }
}
