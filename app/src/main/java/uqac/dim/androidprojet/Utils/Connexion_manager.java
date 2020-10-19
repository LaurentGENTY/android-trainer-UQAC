package uqac.dim.androidprojet.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lpalluel on 29/03/18.
 */

public class Connexion_manager {

    private Context baseContext;
    private Context applicationContext;

    private String date_lc;

    private int last_hours_elapsed = 0;
    private int last_minutes_elapsed = 0;
    private int last_seconds_elapsed = 0;

    public Connexion_manager(Context base, Context application){
        this.baseContext = base;
        this.applicationContext = application;
    }

    private void get_last_connexion(){

        if(this.baseContext.getFileStreamPath("last_connexion.txt").exists()) {

            try {
                FileInputStream fis = this.applicationContext.openFileInput("last_connexion.txt");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                if(sb.toString() != ""){
                    this.date_lc = sb.toString();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.applicationContext.openFileOutput("last_connexion.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("");
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

    }

    public String get_elapsed_time(){
        get_last_connexion();
        Log.i("Debugggggggggggg", "fsdfsdf");

        if(this.date_lc != null){
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String now = dateFormat.format(date);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(this.date_lc);
                d2 = format.parse(now);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000;
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);

            this.last_hours_elapsed = (int) diffHours;
            this.last_minutes_elapsed = (int) diffMinutes;
            this.last_seconds_elapsed = (int) diffSeconds;

            String et = Long.toString(diffHours)+"h "+Long.toString(diffMinutes%60)+"m "+Long.toString(diffSeconds%60)+"s";
            return et;
        }
        else{
            return "0s";
        }

    }

    public void save_last_connexion(){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.applicationContext.openFileOutput("last_connexion.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(dateFormat.format(date));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public int calcul_experience(){
        int experience = 0;
        experience += this.last_hours_elapsed*20;
        experience += this.last_minutes_elapsed*4;
        experience += this.last_seconds_elapsed*1;

        return experience;
    }
}
