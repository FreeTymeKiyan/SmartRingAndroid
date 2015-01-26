package me.freetymekiyan.smartring;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kiyan on 1/12/15.
 */
public class DrawListAdapter extends RecyclerView.Adapter<DrawListAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;

    private static final int TYPE_ITEM = 1;

    String[] titles;

    int[] icons;

    private String name;

    private int profile;

    private String email;

    class ViewHolder extends RecyclerView.ViewHolder {

        int holderId;

        TextView tvTitle;

        ImageView ivIcon;

        ImageView ivProfile;

        TextView tvName;

        TextView tvEmail;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_ITEM) {
                tvTitle = (TextView) itemView.findViewById(R.id.rowText);
                ivIcon = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderId = TYPE_ITEM;
            } else {
                tvName = (TextView) itemView.findViewById(R.id.name);
                tvEmail = (TextView) itemView.findViewById(R.id.email);
                ivProfile = (ImageView) itemView.findViewById(R.id.circleView);
                holderId = TYPE_HEADER;
            }
        }
    }

    public DrawListAdapter(String[] titles, int[] icons, String name, int profile, String email) {
        this.titles = titles;
        this.icons = icons;
        this.name = name;
        this.profile = profile;
        this.email = email;
    }

    @Override
    public DrawListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_drawer, parent, false);
            ViewHolder vh = new ViewHolder(v, viewType);
            return vh;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_header_drawer, parent, false);
            ViewHolder header = new ViewHolder(v, viewType);
            return header;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(DrawListAdapter.ViewHolder holder, int position) {
        if (holder.holderId == TYPE_ITEM) {
            holder.tvTitle.setText(titles[position - 1]);
            holder.ivIcon.setImageResource(icons[position - 1]);
        } else {
            holder.ivProfile.setImageResource(profile);
            holder.tvName.setText(name);
            holder.tvEmail.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }
}
