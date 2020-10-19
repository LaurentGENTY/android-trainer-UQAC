package uqac.dim.androidprojet.menu_fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uqac.dim.androidprojet.R;

/**
 * Created by lpalluel on 28/03/18.
 */

public class Menu_mini_jeu extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mini_jeu_menu, container, false);
    }
}
