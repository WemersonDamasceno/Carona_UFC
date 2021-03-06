package com.ufc.com.carona_ufc.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.ufc.com.carona_ufc.R;
import com.ufc.com.carona_ufc.model.Usuario;
import com.ufc.com.carona_ufc.views.fragments.ui.HistoricoCaronasFragment;
import com.ufc.com.carona_ufc.views.fragments.ui.PerfilUsuarioFragment;
import com.ufc.com.carona_ufc.views.fragments.ui.Pg_Inicial_Fragment;
import com.ufc.com.carona_ufc.views.fragments.ui.ToolsFragment;

import java.util.List;

public class PaginaInicialActivity extends AppCompatActivity {
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    NavController navController;
    private ActionBarDrawerToggle drawerToggle;
    private AppBarConfiguration mAppBarConfiguration;
    //perfil drawer
    ImageView imgPerfilDrawer;
    TextView tvNomePerfilDrawer;
    TextView tvEmailPerfilDrawer;

    //imagem BD
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage fireBaseStorage;
    StorageReference storageReference;
    LinearLayout llMenuFoto;
    //
    GoogleSignInClient mGoogleSingInCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_inicial);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pagina Inicial");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        drawer.addDrawerListener(drawerToggle);
        navigationView = findViewById(R.id.nav_view);

        setupDrawerContent(navigationView);
        imgPerfilDrawer = navigationView.getHeaderView(0).findViewById(R.id.imagePerfilMenu);
        tvEmailPerfilDrawer = navigationView.getHeaderView(0).findViewById(R.id.tvEmailPerfilDrawer);
        tvNomePerfilDrawer = navigationView.getHeaderView(0).findViewById(R.id.tvNomePerfilDrawer);
        llMenuFoto = navigationView.getHeaderView(0).findViewById(R.id.llMenuFoto);

        //primeira pagina a ser exibida
        Fragment fragment = new Pg_Inicial_Fragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();


        final ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7F1FAC")));

        //Verificar se o usuario já esta autenticado
        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        checarPermissaoClient();
        getUser(FirebaseAuth.getInstance().getUid());


        //configurar o login google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInCliente = GoogleSignIn.getClient(this, gso);


        //abrir o perfil do usuario
        llMenuFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("/users")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot doc : docs) {
                                    Usuario user = doc.toObject(Usuario.class);
                                    if (user.getIdUser().equals(FirebaseAuth.getInstance().getUid())) {
                                        setTitle("Perfil");
                                        Fragment fragment = new PerfilUsuarioFragment(user, bar);
                                        // Inserir o fragment no local dele na main activity
                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                                        // Fechar o navigation drawer
                                        drawer.closeDrawers();
                                    }
                                }
                            }
                        });
            }
        });


    }

    private void getUser(final String uid) {
        FirebaseFirestore.getInstance().collection("/users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.i("teste", "erro em buscar no bd: " + e.getMessage());
                        } else {
                            List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot doc : docs) {
                                Usuario user = doc.toObject(Usuario.class);
                                if (user.getIdUser().equals(uid)) {
                                    Picasso.get().load(user.getUrlFotoUser()).into(imgPerfilDrawer);
                                    tvNomePerfilDrawer.setText(user.getNomeUser());
                                    tvEmailPerfilDrawer.setText(user.getEmailUser());
                                }
                            }
                        }
                    }
                });
    }
    private void setupDrawerContent(NavigationView navigationView) {
        //setar o verdim na pagina inicial
        navigationView.setCheckedItem(R.id.nav_pagInicial);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Class fragmentClass = Pg_Inicial_Fragment.class;
        switch (menuItem.getItemId()) {
            case R.id.nav_pagInicial:
                ActionBar bar2 = getSupportActionBar();
                bar2.setTitle("Pagina Inicial");
                fragmentClass = Pg_Inicial_Fragment.class;
                break;
            case R.id.nav_MinhasCaronas:
                final Intent intent = new Intent(getBaseContext(), CaronaQuePegareiActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_PesquisarCarona:
                Intent intent1 = new Intent(this, ProcurarCaronaActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_historicocarona:
                ActionBar bar = getSupportActionBar();
                bar.setTitle("Historico de Caronas");
                fragmentClass = HistoricoCaronasFragment.class;
                break;
            case R.id.nav_tools:
                ActionBar bar3 = getSupportActionBar();
                bar3.setTitle("Configurações");
                fragmentClass = ToolsFragment.class;
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "E ai, estou usando o App Caronas para pegar caronas grátis e/ou baratas, baixe também: " +
                        "https://drive.google.com/drive/folders/1ZPKojnhW1kYc_CdTByaR-TmoGJGL_DhX?usp=sharing");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                break;
            case R.id.nav_sair:
                FirebaseAuth.getInstance().signOut();
                mGoogleSingInCliente.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("teste", "Deslogado do google");
                    }
                });
                Intent intent2 = new Intent(this, LoginActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.nav_send:
                String url = "https://api.whatsapp.com/send?phone=+55 88 99709-1133";
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                intent3.setData(Uri.parse(url));
                startActivity(intent3);
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Inserir o fragment no local dele na main activity
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        //menuItem.setChecked(true);
        // Fechar o navigation drawer
        drawer.closeDrawers();
    }
    //Faz a pergunta para o usuario da PERMISSAO
    private void checarPermissaoClient() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }
        }
    }
    //O código abaixo faz o tratamento da resposta do usuário sobre a PERMISSAO
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Sucess | Permissão concedida", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Fail | Aceite a permissão para usar sua localização!", Toast.LENGTH_LONG).show();
                // A permissão foi negada. Precisa ver o que deve ser desabilitado
            }
            return;
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

}
