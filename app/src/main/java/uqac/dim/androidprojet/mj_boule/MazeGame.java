package uqac.dim.androidprojet.mj_boule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uqac.dim.androidprojet.R;
import uqac.dim.androidprojet.mj_falling.Ring;

public class MazeGame extends AppCompatActivity {

    //ma vue : éléments sur l'écran
    private MazeView view;

    //données
    private MazeData mData;

    //éléments du labyrinthe
    private Ball ball;
    private ArrayList<Wall> walls = null;

    //gap
    private static int GAP;

    //widgets graphiques
    private LinearLayout all;

    private LinearLayout game;

    private LinearLayout buttonsLayout;
    private LinearLayout otherButtons;
    private LinearLayout gameButtons;
    private Button optionsButton;
    private Button parametersButton;
    private Button quitButton;
    private Button pauseButton;

    private ImageView imageLives;
    private TextView textLives;

    //vies du joueur
    private int lives = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //on créér tous les éléments pour jouer : les éléments graphiques

        //je crée ma vue
        view = new MazeView(this);

        //faire en sorte de faire le labyrinthe selon la taille de l'écran + ball + autres constantes
        initSizes();

        //init des boutons etc
        setGraphics();

        setContentView(all);

        //et les données physiques des objets
        mData = new MazeData(this, getBaseContext(), getApplicationContext());

        //init la balle
        ball = new Ball();

        //je la met dans les deux moteurs
        view.setBall(ball);
        mData.setBall(ball);

        //je créé le labyrinthe
        walls = new ArrayList<Wall>();

        //init decalage des walls et de la balle
        this.setGAP(Ball.RADIUS * 2);
        Ball.setGAP(Ball.RADIUS * 2);
        Wall.setGAP(Ball.RADIUS * 2);

        try {
            walls = mData.createMaze(Integer.parseInt(getIntent().getStringExtra("Niveau")));
        } catch (WrongWallType wrongWallType) {
            wrongWallType.printStackTrace();
        }
        Log.i("MazeGame", "J'ai build mon labyrinthe");
        Log.i("BALLXY", ball.getX() + "          " + ball.getY());

        view.setWalls(walls);

    }

    protected void save_levels() {
        String level1 = "VVVVVVVVVVVVVVVVVVVV" +
                "V                  V" +
                "V S                V" +
                "V                  V" +
                "V                  V" +
                "V                  V" +
                "V                  V" +
                "V                  V" +
                "V                  V" +
                "V                  V" +
                "V                E V" +
                "V                  V" +
                "VVVVVVVVVVVVVVVVVVVV";

        String level2 = "VVVVVVVVVVVVVVVVVVVV" +
                "V         V        V" +
                "V S       V        V" +
                "V         V        V" +
                "V                  V" +
                "V                  V" +
                "V                  V" +
                "V                  V" +
                "V         V        V" +
                "V         V      E V" +
                "V         V        V" +
                "VVVVVVVVVVVVVVVVVVVV";

        String level3 = "VVVVVVVVVVVVVVVVVVVV" +
                "VVVVV    S     VVVVV" +
                "VVVV            VVVV" +
                "VVV              VVV" +
                "VV                VV" +
                "VVV              VVV" +
                "VVVV            VVVV" +
                "VVVVV          VVVVV" +
                "VVVVVV        VVVVVV" +
                "VVVVVVV      VVVVVVV" +
                "VVVVVVVVV   VVVVVVVV" +
                "VVVVVVVVVVEVVVVVVVVV" +
                "VVVVVVVVVVVVVVVVVVVV";

        String level4 = "VVVVVVVVVVVVVVVVVVVV" +
                "VE                EV" +
                "VVVVVV        VVVVVV" +
                "VE V            V EV" +
                "V  V      V     V  V" +
                "V  V            V  V" +
                "V  VVV    S   VVV  V" +
                "V                  V" +
                "V V      VVV     V V" +
                "V V              V V" +
                "V V VV VVVVVV VV V V" +
                "VEV       E      VEV" +
                "VVVVVVVVVVVVVVVVVVVV";


        try {
            OutputStreamWriter outputStreamWriter1 = new OutputStreamWriter(getApplicationContext().openFileOutput("maze1.txt", Context.MODE_PRIVATE));
            outputStreamWriter1.write(level1);
            outputStreamWriter1.close();

            OutputStreamWriter outputStreamWriter2 = new OutputStreamWriter(getApplicationContext().openFileOutput("maze2.txt", Context.MODE_PRIVATE));
            outputStreamWriter2.write(level2);
            outputStreamWriter2.close();

            OutputStreamWriter outputStreamWriter3 = new OutputStreamWriter(getApplicationContext().openFileOutput("maze3.txt", Context.MODE_PRIVATE));
            outputStreamWriter3.write(level3);
            outputStreamWriter3.close();

            OutputStreamWriter outputStreamWriter4 = new OutputStreamWriter(getApplicationContext().openFileOutput("maze4.txt", Context.MODE_PRIVATE));
            outputStreamWriter4.write(level4);
            outputStreamWriter4.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void setGraphics() {

        all = new LinearLayout(this);
        all.setOrientation(LinearLayout.VERTICAL);

        //linear layout qui contiendra le jeu
        game = new LinearLayout(this);

        //layout qui contiendra tous les boutons
        buttonsLayout = new LinearLayout(this);
        buttonsLayout.setOrientation(LinearLayout.VERTICAL);

        gameButtons = new LinearLayout(this);
        gameButtons.setOrientation(LinearLayout.HORIZONTAL);

        otherButtons = new LinearLayout(this);
        otherButtons.setOrientation(LinearLayout.HORIZONTAL);

        parametersButton = new Button(this);
        optionsButton = new Button(this);
        quitButton = new Button(this);
        pauseButton = new Button(this);

        imageLives = new ImageView(this);
        textLives = new TextView(this);

        setImage();
        setText();

        //premiere ligne
        parametersButton.setWidth(MazeView.getWIDTH()/2);
        parametersButton.setHeight(MazeView.getHEIGHT()/10);
        parametersButton.setText("PARAMETRES");
        gameButtons.addView(parametersButton);

        pauseButton.setWidth(MazeView.getWIDTH()/2);
        pauseButton.setHeight(MazeView.getHEIGHT()/10);
        pauseButton.setText("PAUSE");
        gameButtons.addView(pauseButton);

        buttonsLayout.addView(gameButtons);

        //deuxieme ligne
        optionsButton.setWidth(MazeView.getWIDTH()/2);
        optionsButton.setHeight(MazeView.getHEIGHT()/10);
        optionsButton.setText("OPTIONS");
        otherButtons.addView(optionsButton);

        quitButton.setWidth(MazeView.getWIDTH()/2);
        quitButton.setHeight(MazeView.getHEIGHT()/10);
        quitButton.setText("QUITTER");
        otherButtons.addView(quitButton);

        buttonsLayout.addView(otherButtons);

        //on ajoute le tout

        //le layout du jeu contient notre surfaceview custom
        game.addView(view);

        //ensuite on ajoute tous les éléments dans le layout final
        all.addView(game);
        all.addView(imageLives);
        all.addView(textLives);
        all.addView(buttonsLayout);



    }

    private void setText() {
        textLives.setText(": "+Integer.toString(this.lives));
        textLives.setX(-MazeView.getWIDTH()/2 +  Ball.RADIUS*3 + 150);
        textLives.setY(0);
    }

    private void setImage() {
        imageLives.setY(0);
        imageLives.setX(-MazeView.getWIDTH()/2 +  Ball.RADIUS*3);
        Bitmap _scratch = BitmapFactory.decodeResource(getResources(),
                R.drawable.pokeball);
        int outWidth = 0;
        int outHeight = 0;

        final int maxSize = Ball.RADIUS * 6;

        int inWidth = _scratch.getWidth();
        int inHeight = _scratch.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(_scratch, outWidth, outHeight, false);
        imageLives.setImageBitmap(resizedBitmap);
    }

    private void initSizes() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Ball.setRADIUS(width / 44);
        MazeView.setWIDTH(width);
        MazeView.setHEIGHT(height);

        view.getHolder().setFixedSize(MazeView.getWIDTH(), MazeView.getHEIGHT()/2);
    }

    @Override
    public void finish() {
        Log.i("COLLISION","end");
        Intent intent = new Intent();
        intent.putExtra("lives", lives);
        setResult(RESULT_OK, intent);
        super.finish();

    }

    @Override
    protected void onResume() {

        super.onResume();
        mData.resume();
    }

    @Override
    protected void onPause() {

        super.onPause();
        mData.pause();
    }

    public static int getGAP() {
        return GAP;
    }

    public static void setGAP(int GAP) {
        MazeGame.GAP = GAP;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void end() {
        this.finish();
    }
}
