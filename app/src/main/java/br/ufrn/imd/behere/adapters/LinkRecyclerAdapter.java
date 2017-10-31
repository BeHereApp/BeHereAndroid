package br.ufrn.imd.behere.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.model.UserLink;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;


public class LinkRecyclerAdapter extends RecyclerView.Adapter<LinkRecyclerAdapter.RecyclerViewHolder> {

    private static RecyclerViewClickListener itemListener;
    private ArrayList<UserLink> userLinks = new ArrayList<>();

    public LinkRecyclerAdapter(ArrayList<UserLink> userLinks, RecyclerViewClickListener itemListener) {
        this.userLinks = userLinks;
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_layout, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        UserLink userLink = userLinks.get(position);
        String linkTypeName =
                userLink.getType() == UserLink.LinkType.STUDENT ? "Student" : "Professor";
        holder.title.setText(linkTypeName);
        holder.description.setText(userLink.getDescription());
    }

    @Override
    public int getItemCount() {
        return userLinks.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, description;

        public RecyclerViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.subject_name);
            description = view.findViewById(R.id.link_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view, this.getLayoutPosition());
        }
    }
}

