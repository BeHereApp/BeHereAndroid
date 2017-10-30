package br.ufrn.imd.behere;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class LinkActivity extends CustomActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        setup();
    }

    private void setup() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ArrayList<UserLink> userLinks = new ArrayList<>();
        userLinks.add(new UserLink(UserLink.LinkType.PROFESSOR, "Digital Metropolis Institute"));
        userLinks.add(new UserLink(UserLink.LinkType.STUDENT, "Digital Metropolis Institute"));
        adapter = new RecyclerAdapter(userLinks);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    //Atribui os valores e titulos de cada item
    /*adapter = new RecycleAdapter(arraylist);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView;setAdapter(adapter);*/
}

