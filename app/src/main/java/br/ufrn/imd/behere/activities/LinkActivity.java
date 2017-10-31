package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.LinkRecyclerAdapter;
import br.ufrn.imd.behere.model.UserLink;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;

public class LinkActivity extends CustomActivity implements RecyclerViewClickListener {

    private ArrayList<UserLink> userLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        setup();
    }

    private void setup() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_links);
        userLinks = new ArrayList<>();

        // Add example links
        userLinks.add(new UserLink(UserLink.LinkType.PROFESSOR, "Digital Metropolis Institute"));
        userLinks.add(new UserLink(UserLink.LinkType.STUDENT, "Digital Metropolis Institute"));

        RecyclerView.Adapter adapter = new LinkRecyclerAdapter(userLinks, this);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {

        Intent intent;

        if (userLinks.get(position).getType() == UserLink.LinkType.STUDENT) {
            intent = new Intent(this, StudentChooseActivity.class);
        } else {
            intent = new Intent(this, ProfessorSubjectActivity.class);
        }

        startActivity(intent);
    }
}
