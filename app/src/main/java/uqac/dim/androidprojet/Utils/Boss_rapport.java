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

public class Boss_rapport extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boss_rapport);

        TextView msg = findViewById(R.id.success_boss_text);

        if( (boolean)getIntent().getExtras().get("success") )
            msg.setText("Bravo, vous avez vaincu le boss !");
        else
            msg.setText("Dommage, vous avez abandonné sans vaincre le boss...");

    }
}
