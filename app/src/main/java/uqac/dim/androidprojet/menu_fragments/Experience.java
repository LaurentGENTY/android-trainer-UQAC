package uqac.dim.androidprojet.menu_fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uqac.dim.androidprojet.R;

/**
 * Created by Luis Palluel on 30/03/18.
 */

public class Experience extends Fragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.barre_experience, container, false);
    }
}
