package com.example.clubhub.event;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clubhub.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;

    // Constructor nhận vào Context và danh sách Event
    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Cập nhật thông tin sự kiện vào các view
        holder.tvEventName.setText(event.getEventName());
        holder.tvEventDate.setText(event.getEventDate());
        holder.tvEventTime.setText(event.getEventTime());
        holder.tvEventPlace.setText(event.getEventPlace());

        // Kiểm tra nếu URL ảnh sự kiện hợp lệ trước khi tải
        if (event.getEventImageUrl() != null && !event.getEventImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(event.getEventImageUrl())
                    .into(holder.imgEvent);
        } else {
            holder.imgEvent.setImageResource(R.drawable.sample_img_default); // Default image
        }

        // Xử lý sự kiện click vào nút "Details"
        holder.btnEventDetails.setOnClickListener(v -> {
            // Chuyển tới EventDetailActivity với eventId
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("eventId", event.getEventId());  // Truyền eventId để hiển thị chi tiết sự kiện
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDate, tvEventTime, tvEventPlace;
        ImageView imgEvent;
        Button btnEventDetails;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tv_event_name);
            tvEventDate = itemView.findViewById(R.id.tv_event_date);
            tvEventTime = itemView.findViewById(R.id.tv_event_time);
            tvEventPlace = itemView.findViewById(R.id.tv_event_place);
            imgEvent = itemView.findViewById(R.id.img_event);
            btnEventDetails = itemView.findViewById(R.id.btn_event_details);
        }
    }

    // Method to update event data in the adapter
    public void updateEvents(List<Event> newEventList) {
        this.eventList = newEventList;
        notifyDataSetChanged();
    }
}
