package roombooking.fyp.fyproomviewfinal;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.exampleViewHolder>{
    private ArrayList<exampleItem> ExampleList;

    public static class exampleViewHolder extends  RecyclerView.ViewHolder{
        public TextView name;
        public TextView location;
        public  TextView available;
        public  TextView maxPeople;
        public TextView live;


        public exampleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            location = itemView.findViewById(R.id.location);
            available = itemView.findViewById(R.id.available);
            maxPeople = itemView.findViewById(R.id.maxPeople);
live = itemView.findViewById(R.id.live);
        }
    }

    public recyclerAdapter(ArrayList<exampleItem> exampleList) {
        ExampleList = exampleList;
    }

    @NonNull
    @Override
    public exampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exampleitem, parent, false);
        exampleViewHolder evh = new exampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull exampleViewHolder exampleViewHolder, int position) {
            exampleItem current = ExampleList.get(position);

        exampleViewHolder.name.setText(current.getName());
        exampleViewHolder.location.setText(current.getLocation());
        exampleViewHolder.available.setText(current.getAvailable());
        exampleViewHolder.maxPeople.setText(current.getMaxOccupancy());
        exampleViewHolder.live.setText(current.getLiveAvail());
    }

    @Override
    public int getItemCount() {
        return ExampleList.size();
    }
}
