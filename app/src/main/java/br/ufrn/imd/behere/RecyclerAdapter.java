package br.ufrn.imd.behere;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by claudio on 26/10/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<UserLink> userLinks = new ArrayList<>();

    public RecyclerAdapter(ArrayList<UserLink> userLinks) {
        this.userLinks = userLinks;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent,
                false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        UserLink userLink = userLinks.get(position);
        holder.title.setText(userLink.getName());
        holder.description.setText(userLink.getDescription());
    }

    @Override
    public int getItemCount() {
        return userLinks.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public RecyclerViewHolder(View view) {

            super(view);
            title = (TextView) view.findViewById(R.id.item_title);
            description = (TextView) view.findViewById(R.id.item_description);
        }

    }
}
