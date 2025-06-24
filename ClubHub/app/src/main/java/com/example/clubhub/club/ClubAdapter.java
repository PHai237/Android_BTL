package com.example.clubhub.club;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clubhub.R;
import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnClubClickListener {
        void onClubClick(Club club);
    }

    private List<ClubListItem> items;
    private OnClubClickListener listener;

    public ClubAdapter(List<ClubListItem> items, OnClubClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ClubListItem.TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club, parent, false);
            return new ClubViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ClubListItem item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText(item.headerTitle);
        } else if (holder instanceof ClubViewHolder) {
            Club club = item.club;
            ClubViewHolder vh = (ClubViewHolder) holder;
            vh.tvName.setText(club.getName());
            vh.tvType.setText(club.getType());
            vh.tvDesc.setText(club.getDescription());
            vh.tvVisibility.setText(club.isPublic() ? "Public" : "Private");
            vh.btnDetails.setOnClickListener(v -> listener.onClubClick(club));
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvDesc, tvVisibility, btnDetails;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvClubName);
            tvType = itemView.findViewById(R.id.tvClubType);
            tvDesc = itemView.findViewById(R.id.tvClubDesc);
            tvVisibility = itemView.findViewById(R.id.tvClubVisibility);
            btnDetails = itemView.findViewById(R.id.btnClubDetails);
        }
    }
}
