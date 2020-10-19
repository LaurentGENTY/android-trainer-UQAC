package uqac.dim.androidprojet.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import uqac.dim.androidprojet.R;

/**
 * Created by Luis Palluel on 31/03/18.
 */

public class Bienvenue_dialog extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra("last_connexion")){
            setContentView(R.layout.notif_last_connexion);
            TextView last_c = findViewById(R.id.last_connexion);
            last_c.setText(getIntent().getStringExtra("last_connexion"));
            TextView exp_gagnee = findViewById(R.id.exp_gagne);
            exp_gagnee.setText(getIntent().getStringExtra("exp_gagnee"));
            TextView nb_pas = findViewById(R.id.nb_pas);
            nb_pas.setText(Integer.toString((int)getIntent().getExtras().get("steps")));
        }
        else if(getIntent().hasExtra("first_co")){
            setContentView(R.layout.bienvenue);
        }

    }
}
