package uqac.dim.androidprojet.mj_falling;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import uqac.dim.androidprojet.R;
import uqac.dim.androidprojet.mj_boule.Ball;

public class Falling extends AppCompatActivity {

    //ma vue
    private FallingView fW;

    //moteur physique
    private FallingEngine fE;

    //éléments
    private Catcher catcher = null;
    private ArrayList<Ring> fallingRings = new ArrayList<Ring>();

    //vies du joueur
    private int lives;

    //socre du joueur
    private int score = 0;
    //musique
    private MediaPlayer lecture = null;
    private int currentPositionMusic = 0;

    //lock
    Object mutex;
    private int scoreFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mutex = new Object();

        fW = new FallingView(this,mutex);
        setContentView(fW);

        //et les données physiques des objets
        fE = new FallingEngine(this,fW,mutex);

        //init la balle
        catcher = new Catcher();

        //je la met dans les deux moteurs
        fW.setCatcher(catcher);
        fE.setCatcher(catcher);

        initCatcherInitial();

        initRingsCatcher();

        fallingRings = initRings(fallingRings);

        fE.setFallingRings(fallingRings);
        fW.setFallingRings(fallingRings);

        lecture = new MediaPlayer();
        try {
            lecture.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/raw/notification_sound"));
            lecture.prepare();
            lecture.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Ring> initRings(ArrayList<Ring> fallingRings) {
        this.fallingRings = fallingRings;

        fallingRings.add(new Ring(50));

        return fallingRings;
    }

    private void initRingsCatcher() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //initialisation des variables statiques des objets
        Log.i("TAILLE",Integer.toString(width));
        Ring.setSIZE(width);
        Catcher.setSIZE(width,height);
    }


    @Override
    public void finish(){
        Log.i("Falling", "destroyed");
        Intent intent = new Intent();
        intent.putExtra("score", scoreFinal);
        Log.i("ACTI", Integer.toString(scoreFinal));
        setResult(RESULT_OK, intent);

        super.finish();


    }

    private void initCatcherInitial() {

        RectF initial = new RectF(0,0,300,300);
        catcher.setRectInitial(initial);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        fE.resume();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        fE.pause();

        currentPositionMusic=lecture.getCurrentPosition();
        lecture.pause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        lecture.stop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(lecture != null)
        {
            lecture.release();
            lecture = null;
        }
    }

    public void addRing(Ring ring) {
        fallingRings.add(ring);
        fW.setFallingRings(fallingRings);
    }

    public void showRings() {
        for(Ring r : fallingRings)
        {
            Log.i("RINGS ACTIVITY",Float.toString(r.getX())+"        Y : "+Float.toString(r.getY()));
        }

        ArrayList<Ring> fallingsRingsView = new ArrayList<>();
        fallingsRingsView = fW.getFallingRings();
        for(Ring r : fallingsRingsView)
        {
            Log.i("RINGS VIEW",Float.toString(r.getX())+"        Y : "+Float.toString(r.getY()));
        }

        ArrayList<Ring> fallingsRingsEngine = new ArrayList<>();
        fallingsRingsEngine = fE.getFallingRings();
        for(Ring r : fallingsRingsEngine)
        {
            Log.i("RINGS ENGINE",Float.toString(r.getX())+"        Y : "+Float.toString(r.getY()));
        }

    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getCurrentPositionMusic() {
        return currentPositionMusic;
    }

    public void setCurrentPositionMusic(int currentPositionMusic) {
        this.currentPositionMusic = currentPositionMusic;
    }


    public void setScore(int score) {
        this.score = score;
    }

    public int getScore()
    {
        return this.score;
    }

    public void end() {
        fW.stop();
        fE.stop();

        this.finish();
    }

    public void getScoreFinal(int score) {
        this.scoreFinal = score;
    }
}
