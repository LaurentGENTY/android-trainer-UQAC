package uqac.dim.androidprojet;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import uqac.dim.androidprojet.Utils.Bienvenue_dialog;
import uqac.dim.androidprojet.Utils.Connexion_manager;
import uqac.dim.androidprojet.Utils.StepsService;
import uqac.dim.androidprojet.Utils.Game_rapport;
import uqac.dim.androidprojet.mj_boule.MazeGame;
import uqac.dim.androidprojet.mj_falling.Falling;
import uqac.dim.androidprojet.Utils.Boss_rapport;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    private Fragment main_menu;
    private Fragment statistiques;
    private Fragment mini_jeu;
    private Fragment labyrinthe;
    private Fragment last_connexion;
    private Fragment choixPersonnage;
    private Fragment a_propos;
    private Fragment menu_boss;
    private Fragment main_reglages;

    private Connexion_manager cm;
    private Personnage personnage;

    private static final int CST_MAZE = 11;
    private static final int CST_FALLING = 20;
    private static final int CST_BOSS = 1;

    private String time_elapsed;

    private myRenderer renderer;
    private SurfaceView rendererSurface;

    private StepsService myService;
    private ServiceConnection myServiceConnexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.main_menu = getFragmentManager().findFragmentById(R.id.frag_main_menu);
        this.statistiques = getFragmentManager().findFragmentById(R.id.frag_stats);
        this.mini_jeu = getFragmentManager().findFragmentById(R.id.frag_menu_mini_jeu);
        this.labyrinthe = getFragmentManager().findFragmentById(R.id.frag_menu_labyrinthe);
        this.choixPersonnage = getFragmentManager().findFragmentById(R.id.frag_choix_personnage);
        this.a_propos = getFragmentManager().findFragmentById(R.id.frag_a_propos);
        this.menu_boss = getFragmentManager().findFragmentById(R.id.frag_boss);
        this.main_reglages = getFragmentManager().findFragmentById(R.id.main_reglages);


        getFragmentManager().beginTransaction()
                .hide(this.statistiques)
                .hide(this.mini_jeu)
                .hide(this.labyrinthe)
                .hide(this.choixPersonnage)
                .hide(this.a_propos)
                .hide(this.menu_boss)
                .hide(this.main_reglages)
                .commit();

        this.cm = new Connexion_manager(getBaseContext(), getApplicationContext());
        //show_last_connexion();
        this.time_elapsed = this.cm.get_elapsed_time();

        ((Button)findViewById(R.id.but_labyrinthe_1)).setOnClickListener(this);
        ((Button)findViewById(R.id.but_labyrinthe_2)).setOnClickListener(this);
        ((Button)findViewById(R.id.but_labyrinthe_3)).setOnClickListener(this);
        ((Button)findViewById(R.id.but_labyrinthe_4)).setOnClickListener(this);
        ((Button)findViewById(R.id.but_mini_jeu_menu_2)).setOnClickListener(this);
        ((Button)findViewById(R.id.but_boss_menu_1)).setOnClickListener(this);

        this.personnage = new Personnage(getBaseContext(), getApplicationContext());
        this.personnage.get_stats();
        this.personnage.give_experience(cm.calcul_experience());


        ProgressBar progress = findViewById(R.id.experience_barre);
        progress.setProgress(this.personnage.getPourcentageExperience());

        TextView niveau = findViewById(R.id.niveau);
        niveau.setText(Integer.toString(this.personnage.getNiveau()));

        int steps = 0;
        if( getBaseContext().getFileStreamPath("steps.txt").exists()){
            try {
                FileInputStream fis = getApplicationContext().openFileInput("steps.txt");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                steps = Integer.parseInt(sb.toString());
                personnage.give_experience(steps*100);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(cm.calcul_experience() != 0){
            Intent intent = new Intent(this, Bienvenue_dialog.class);
            intent.putExtra("last_connexion", this.time_elapsed);
            intent.putExtra("exp_gagnee", Integer.toString(cm.calcul_experience()));
            intent.putExtra("steps", steps);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, Bienvenue_dialog.class);
            intent.putExtra("first_co", "true");
            startActivity(intent);
        }

        rendererSurface = findViewById(R.id.surfaceModel3D);
        rendererSurface.setFrameRate(60);
        rendererSurface.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);
        renderer = new myRenderer(this, rendererSurface, R.raw.android_obj);
        rendererSurface.setSurfaceRenderer(renderer);

        Intent stepService = new Intent(this, StepsService.class);
        setServiceConnection();
        bindService(stepService, myServiceConnexion, Context.BIND_AUTO_CREATE);



    }

    private void setServiceConnection(){
        myServiceConnexion = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                myService = ((StepsService.myBinderActivity) iBinder).getService();
                Log.i("NOUVEAUPAS", "BLEUBLEU" + Integer.toString(myService.getSteps()));
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                myService = null;
            }
        };
    }

    @Override
    protected void onDestroy() {
        this.cm.save_last_connexion();
        this.personnage.save_stats();
        super.onDestroy();
    }

    private void show_last_connexion(){
        this.time_elapsed = this.cm.get_elapsed_time();
        TextView last_connexion = findViewById(R.id.last_connexion);
        last_connexion.setText(this.time_elapsed);
    }


    public void show_statistiques(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.main_menu)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.statistiques)
                .commit();

        TextView vie = findViewById(R.id.text_pv);
        vie.setText("Vie : "+Integer.toString(this.personnage.getVie()));
        TextView force = findViewById(R.id.text_force);
        force.setText("Force : "+Integer.toString(this.personnage.getForce()));
        TextView vitesse = findViewById(R.id.text_vitesse);
        vitesse.setText("Vitesse : "+Integer.toString(this.personnage.getVitesse()));
        TextView precision = findViewById(R.id.text_precision);
        precision.setText("Précision : "+Integer.toString(this.personnage.getPrecision())+"%");
    }

    public void close_statistiques(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.statistiques)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.main_menu)
                .commit();
    }

    public void show_mini_jeu(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.main_menu)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.mini_jeu)
                .commit();
    }

    public void close_mini_jeu(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.mini_jeu)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.main_menu)
                .commit();
    }

    public void show_labyrinthe(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.mini_jeu)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.labyrinthe)
                .commit();
    }

    public void close_labyrinthe(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.labyrinthe)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.mini_jeu)
                .commit();
    }

    public void show_choixPersonnage(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.main_menu)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.choixPersonnage)
                .commit();
    }

    public void close_choixPersonnage(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.choixPersonnage)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.main_menu)
                .commit();
    }

    public void show_Apropos(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.main_menu)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.a_propos)
                .commit();
    }

    public void close_Apropos(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.a_propos)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.main_menu)
                .commit();
    }

    public void show_menu_boss(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.main_menu)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.menu_boss)
                .commit();
    }

    public void close_menu_boss(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.menu_boss)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.main_menu)
                .commit();
    }

    public void show_reglages(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.main_menu)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.main_reglages)
                .commit();
    }

    public void close_reglages(View view) {

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .hide(this.main_reglages)
                .commit();

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(this.main_menu)
                .commit();
    }

    @Override
    public void onClick(View view) {
        Button button = (Button)view;

        Intent intent;
        switch (button.getText().toString()){
            case "1":
                intent = new Intent(this, MazeGame.class);
                intent.putExtra("Niveau", "1");
                startActivityForResult(intent, CST_MAZE);
                break;
            case "2":
                intent = new Intent(this, MazeGame.class);
                intent.putExtra("Niveau", "2");
                startActivityForResult(intent, CST_MAZE);
                break;
            case "3":
                intent = new Intent(this, MazeGame.class);
                intent.putExtra("Niveau", "3");
                startActivityForResult(intent, CST_MAZE);
                break;
            case "4":
                intent = new Intent(this, MazeGame.class);
                intent.putExtra("Niveau", "4");
                startActivityForResult(intent, CST_MAZE);
                break;
            case "Falling":
                intent = new Intent(this, Falling.class);
                intent.putExtra("Niveau", "8");
                startActivityForResult(intent, CST_FALLING);
                break;
            case "Boss 1":
                intent = new Intent(this, Boss.class);
                intent.putExtra("level", "1");
                intent.putExtra("force", Integer.toString(this.personnage.getForce()));
                intent.putExtra("vitesse", Integer.toString(this.personnage.getVitesse()));
                intent.putExtra("precision", Integer.toString(this.personnage.getPrecision()));
                startActivityForResult(intent, CST_BOSS);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == CST_MAZE){
                try{
                    Log.i("Result Maze", "Result catched");
                    int lives = (int)data.getExtras().get("lives");
                    this.personnage.give_experience(lives*50);
                    Intent intent = new Intent(this, Game_rapport.class);
                    intent.putExtra("exp", lives*50);
                    startActivity(intent);
                }
                catch(Exception e){
                    Log.i("error", "Parse error : intent return");
                }
            }
            else if(requestCode == CST_FALLING){
                Log.i("Result Falling", "Result catched");
                int score = (int)data.getExtras().get("score");
                Log.i("ACT", Integer.toString(score));
                this.personnage.give_experience(score*50);
                Intent intent = new Intent(this, Game_rapport.class);
                intent.putExtra("exp", score*50);
                startActivity(intent);
            }
            else if(requestCode == CST_BOSS){
                boolean success = (boolean)data.getExtras().get("success");
                Intent intent = new Intent(this, Boss_rapport.class);
                intent.putExtra("success", success);
                startActivity(intent);
            }
        }
    }

    public void changerModel(View view){
        switch(view.getId()){
            case R.id.but_choixperso_basique:
                renderer.changeModel(R.raw.android_obj);
                break;

            case R.id.but_choixperso_chapvert:
                renderer.changeModel(R.raw.greenhatedandroid_obj);
                break;

            case R.id.but_choixperso_chapbleu:
                renderer.changeModel(R.raw.bluehatedandroid_obj);
                break;

            case R.id.but_choixperso_chaprouge:
                renderer.changeModel(R.raw.redhatedandroid_obj);
                break;

            case R.id.but_choixperso_chapviolet:
                renderer.changeModel(R.raw.purplehatedandroid_obj);
                break;

            case R.id.but_choixperso_golden:
                renderer.changeModel(R.raw.goldenandroid_obj);
                break;

            default: break;


        }
    }
}
