package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.RecyclerAdapter;
import br.ufrn.imd.behere.model.UserLink;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;

public class LinkActivity extends CustomActivity implements RecyclerViewClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<UserLink> userLinks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        setup();
    }

    private void setup() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        userLinks = new ArrayList<>();
        userLinks.add(new UserLink(UserLink.LinkType.PROFESSOR, "Digital Metropolis Institute"));
        userLinks.add(new UserLink(UserLink.LinkType.STUDENT, "Digital Metropolis Institute"));
        adapter = new RecyclerAdapter(userLinks, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void recyclerViewListClicked(View v, int position) {
        if (userLinks.get(position).getType() == UserLink.LinkType.STUDENT) {
            Intent intent = new Intent(this, StudentChooseActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "ProfessorChooseActivity", Toast.LENGTH_SHORT).show();
        }
    }
}
