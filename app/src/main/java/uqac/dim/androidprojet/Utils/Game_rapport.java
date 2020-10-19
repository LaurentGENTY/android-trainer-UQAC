package uqac.dim.androidprojet.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import uqac.dim.androidprojet.R;

/**
 * Created by Luis Palluel on 28/04/18.
 */

public class Game_rapport extends AppCompatActivity{

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_rapport);

        TextView msg = findViewById(R.id.game_rapport_text);

        if( getIntent().getExtras().get("exp") != null )
            msg.setText("Vous avez gagné : "+ getIntent().getExtras().get("exp") + " exp.");
        else
            msg.setText("Vous n'avez rien gagné.");

    }
}
