package com.ufc.com.carona_ufc.views.fragments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ufc.com.carona_ufc.R;
import com.ufc.com.carona_ufc.views.OferecerCaronaActivity;
import com.ufc.com.carona_ufc.views.ProcurarCaronaActivity;

public class Pg_Inicial_Fragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pg_inicial, container, false);

        Button btnOferecerCarona = root.findViewById(R.id.btnOferecerCarona);
        Button btnProcurarCarona = root.findViewById(R.id.btnProcurarCarona);

        btnOferecerCarona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OferecerCaronaActivity.class);
                //abrir activity com animação
                //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.fade_in, R.anim.fade_out);
                //ActivityCompat.startActivity(getContext(), intent, activityOptionsCompat.toBundle());
                startActivity(intent);
            }
        });
        btnProcurarCarona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), ProcurarCaronaActivity.class);
                startActivity(intent1);
            }
        });


        //teste do shared preferences
        /*
        SharedPreferences prefs = getActivity().getSharedPreferences("preferencias",Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("nome", "Wemerson");
        ed.putString("email", "wemerson@gmail.com");
        ed.apply();
        SharedPreferences pref = getActivity().getSharedPreferences("preferencias",Context.MODE_PRIVATE);
        String nome = pref.getString("nome","Deu ruim nome");
        String email = pref.getString("email","Deu ruim email");
       */


        return root;
    }
}