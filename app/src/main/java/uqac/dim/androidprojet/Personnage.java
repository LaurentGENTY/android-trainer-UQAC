package uqac.dim.androidprojet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Luis Palluel on 30/03/18.
 */

public class Personnage {

    private int niveau = 1;

    private int vie = 100;
    private int force = 15;
    private int vitesse = 30;
    private int precision = 20;
    private int experience = 0;

    private Context baseContext;
    private Context applicationContext;

    Personnage(Context baseContext, Context applicationContext){
        this.baseContext = baseContext;
        this.applicationContext = applicationContext;
    }

    public int getNiveau() { return niveau; }

    public int getVie() {
        return vie;
    }

    public int getForce() {
        return force;
    }

    public int getVitesse() {
        return vitesse;
    }

    public int getPourcentageExperience() {
        int exp = (this.experience * 100) / (this.niveau*100);
        return exp;
    }

    public void give_experience(int experience){
        experience += this.experience;

        while(experience > this.niveau * 100){
            experience-=this.niveau * 100;
            niveau++;
        }
        this.experience = experience;
    }

    public int getPrecision() {
        return precision;
    }

    public void get_stats(){

        if(this.baseContext.getFileStreamPath("stats.txt").exists()) {

            try {
                FileInputStream fis = this.applicationContext.openFileInput("stats.txt");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                calculate_stats(sb.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.applicationContext.openFileOutput("stats.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("1.0");
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

    }

    private void calculate_stats(String s) {
        String[] stats = s.split("\\.");
        this.niveau = Integer.parseInt(stats[0]);
        this.experience = Integer.parseInt(stats[1]);

        this.vie = this.niveau * 100;
        this.force = this.niveau * 15;
        this.vitesse = this.niveau * 30;
        this.precision = this.niveau * 5;
    }

    public void save_stats() {
        String save = Integer.toString(this.niveau) + "." + Integer.toString(this.experience);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.applicationContext.openFileOutput("stats.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(save);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
