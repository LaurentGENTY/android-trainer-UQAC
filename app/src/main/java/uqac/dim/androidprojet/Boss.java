package uqac.dim.androidprojet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * Created by Luis Palluel on 28/04/18.
 */

public class Boss extends AppCompatActivity {

    int level;
    int life_points_max;
    int life_points;
    int player_force;
    int player_vitesse;
    int player_precision;

    double dps;

    NumberFormat format1;
    NumberFormat format2;

    boolean success = false;
    boolean fight_in_progress = false;

    ProgressBar progress;
    TextView life_view;
    TextView time_left;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);

        this.level = Integer.parseInt(getIntent().getExtras().getString("level"));
        this.player_force = Integer.parseInt(getIntent().getExtras().getString("force"));
        this.player_vitesse = Integer.parseInt(getIntent().getExtras().getString("vitesse"));
        this.player_precision = Integer.parseInt(getIntent().getExtras().getString("precision"));
        this.life_points_max = level * 200;
        this.life_points = this.life_points_max;
        calculate_dps();

        format1 = NumberFormat.getInstance();
        format1.setMaximumFractionDigits(1);

        format2 = NumberFormat.getInstance();
        format2.setMaximumFractionDigits(2);

        String dps_s = format1.format(this.dps);
        TextView dps_view = findViewById(R.id.dps_view);
        dps_view.setText(dps_s);
        this.life_view = findViewById(R.id.life_boss_text);
        this.life_view.setText(Integer.toString(this.life_points));

        this.time_left = findViewById(R.id.time_left);
        this.time_left.setText("60");

        this.progress = findViewById(R.id.life_boss_bar);
        this.progress.setProgress(100);
    }

    private int getPourcentageLife() {

        return 0;
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("success", this.success);
        setResult(RESULT_OK, intent);
        super.finish();

    }

    private void calculate_dps(){
        this.dps = ( this.player_force * this.player_vitesse )*1.0 / (100 - this.player_precision);
    }

    public void start_fight(View view) {
        if(!fight_in_progress){
            mHandler.post(Combat);
            fight_in_progress =true;
        }
    }

    public void close_boss(View view) {
        finish();
    }

    private Runnable Combat = new Runnable() {
        private int div = 10;
        private double actual_time = 30.0;

        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            life_points -= dps/div;
            progress.setProgress((life_points*100) / life_points_max);
            life_view.setText(Integer.toString(life_points));
            actual_time -= (1000/div)/1000.0;
            time_left.setText(format2.format(actual_time));
            if(life_points <= 0) {
                success = true;
                finish();
            }
            else
                mHandler.postDelayed(Combat, 1000/div);
        }
    };
}
