package com.abhimanyu.doda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DrawingAdapter drawingAdapter;
    private List<Drawing> drawingList;
    private DatabaseReference drawingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        drawingsRef = FirebaseDatabase.getInstance().getReference("drawings");

        // Initialize RecyclerView and its layout manager
        recyclerView = findViewById(R.id.recyclerview_drawings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Initialize the list and adapter
        drawingList = new ArrayList<>();
        drawingAdapter = new DrawingAdapter(drawingList);

        // Set click listener for drawing items
        drawingAdapter.setOnItemClickListener(new DrawingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click event, e.g., navigate to drawing details activity
                // or show a dialog with marker information
                Drawing drawing = drawingList.get(position);
                Toast.makeText(MainActivity.this, "Clicked on drawing: " + drawing.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(drawingAdapter);

        // Fetch the list of drawings from the database
        fetchDrawings();
    }

    private void fetchDrawings() {
        drawingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                drawingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Drawing drawing = snapshot.getValue(Drawing.class);
                    drawingList.add(drawing);
                }
                drawingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if necessary
            }
        });
    }
}
