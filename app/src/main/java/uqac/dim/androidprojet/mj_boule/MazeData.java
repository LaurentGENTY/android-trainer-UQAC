package uqac.dim.androidprojet.mj_boule;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * Created by laure on 27/03/2018.
 */

public class MazeData {

    private int WIDTH_LEVEL = 20;

    //ma boule
    private Ball ball = null;
    //ma liste de murs
    private ArrayList<Wall> walls = null;

    //sensors
    private SensorManager sM;
    private Sensor mAccelerometre;
    //private Sensor mMagnetometre;

    //SIZE des wall et ball
    private static int SIZE;

    private Context baseContext;
    private Context applicationContext;

    private static int lives = 5;

    //l'activité link
    private MazeGame mazeActivity = null;

    //bool descente vies
    private boolean livesBlock = false;

    //on fait un listerner perso privée afin de gérer les evenements de l'accelerometre
    //EDIT : test avec une classe externe cependant nous ne pouvons pas
    //passer de paramètre telle que la boule à la classe externer dans les events
    //tels que onSensorChanged etc
    //EDIT2 : classe privée buggy donc listener direct

    //event listener
    SensorEventListener mSEL = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.i("MySensor","onSensorChanged");

            float newX = 0, newY = 0;

            //test
            //sM.getOrientation(R,sensorEvent.values);
            //sM.remapCoordinateSystem(R,sM.AXIS_X);
            //int screenRotation = getWindowManager().getDefaultDisplay().getRotation();

            //nouvelles valeurs dans values
            newX = sensorEvent.values[1];
            Log.i("AccelerometreX",Float.toString(newX));
            newY = sensorEvent.values[0];
            Log.i("AccelerometreYa",Float.toString(newY));

            Log.i("AccelerometreYb",Float.toString(newY));

            Log.i("Vies",Integer.toString(lives));

            if (ball != null) {
                Log.i("MazeData","New Hitbox");
                //la hitbox correspond aux coordonnées de la boule actuellement
                RectF ballHitbox = ball.setNewCoordinates(newX, -newY);


                for (Wall w : walls) {
                    //si un wall touche la balle
                    RectF temp = new RectF(w.getRectangle());
                    if (temp.intersect(ballHitbox)) {
                        switch (w.getType()) {
                            case VOID:
                                toaster(WALL_TYPE.VOID);
                                break;
                            case ENDING:
                                toaster(WALL_TYPE.ENDING);
                                break;
                        }

                    }
                }
            }
            else
            {
                Log.i("MazeData","GIGA BUG");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public MazeData(MazeGame mG, Context baseContext, Context applicationContext) {
        //l'activité c'est celle du paramètre
        mazeActivity = mG;
        this.baseContext = baseContext;
        this.applicationContext = applicationContext;

        //classe permettant d'accéder aux capteurs : instancie
        sM = (SensorManager) mG.getBaseContext().getSystemService(Service.SENSOR_SERVICE);

        //on link les sensors etc
        mAccelerometre = sM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mMagnetometre = sM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        //sM.getRotationMatrix(R, null, accelerometreValues, magnetometreValues);

        lives = 5;
        mazeActivity.setLives(lives);
    }

    public static int getSIZE() {
        return SIZE;
    }

    public static void setSIZE(int SIZE) {
        MazeData.SIZE = SIZE;
    }

    public static int getLives() {
        return lives;
    }

    //fonctions de la boule

    public Ball getBall() {
        return this.ball;
    }

    public void setBall(Ball ball) {
        if (ball != null) {
            this.ball = ball;
        }
    }

    public void pause() {
        sM.unregisterListener(mSEL, mAccelerometre);
        //sM.unregisterListener(mSEL, mMagnetometre);

    }

    public void resume() {
        sM.registerListener(mSEL, mAccelerometre, sM.SENSOR_DELAY_GAME);
        //sM.registerListener(mSEL, mMagnetometre, sM.SENSOR_DELAY_GAME);

    }

    public void restart()
    {
        ball.lose();
    }

    private void toaster(WALL_TYPE type) {

        if (type == WALL_TYPE.VOID) {
            if (lives > 0) {
                if(!livesBlock)
                {
                    lives--;
                    Log.i("test","TEST");
                    livesBlock = true;
                }
            } else {
                mazeActivity.end();
            }
        } else if (type == WALL_TYPE.ENDING) {
            mazeActivity.end();
        }

        reset();
    }

    private void reset() {
        ball.lose();
        livesBlock = false;
    }

    public ArrayList<Wall> createMaze(int num_level) throws WrongWallType {

        walls = new ArrayList<Wall>();

        String file = "maze"+num_level+".txt";
        String level = "";
        if(!this.baseContext.getFileStreamPath(file).exists()) {
            this.mazeActivity.save_levels();
        }

        try {
            FileInputStream fis = this.applicationContext.openFileInput(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            level = bufferedReader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        int iteratorX = 0;
        int iteratorY = 0;

        for(char elem: level.toCharArray()){
            switch (elem){
                case 'V':
                    walls.add(new Wall("VOID", iteratorX, iteratorY));
                    break;
                case 'E':
                    walls.add(new Wall("ENDING", iteratorX, iteratorY));
                    break;
                case 'S':
                    Wall s = new Wall("STARTING", iteratorX, iteratorY);
                    walls.add(s);
                    ball.setRectInitial(s.getRectangle());
                    break;
                default:
                    break;
            }
            iteratorX++;
            iteratorX = iteratorX%WIDTH_LEVEL;
            if(iteratorX == 0)
                iteratorY++;

        }

        return this.walls;
    }

}
