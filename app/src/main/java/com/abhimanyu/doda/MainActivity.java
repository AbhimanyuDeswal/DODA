package com.abhimanyu.doda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    private FloatingActionButton addDrawingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        drawingsRef = FirebaseDatabase.getInstance().getReference("drawings");

        // Initialize the addDrawingButton
        addDrawingButton = findViewById(R.id.fab_add_drawing);
        addDrawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDrawingDialog();
            }
        });

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

    private void showAddDrawingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Drawing");

        // Create a custom layout for the dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_drawing, null);
        final EditText nameEditText = view.findViewById(R.id.edittext_drawing_name);
        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String drawingName = nameEditText.getText().toString().trim();
                if (!drawingName.isEmpty()) {
                    addDrawingToDatabase(drawingName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void addDrawingToDatabase(String drawingName) {
        // Generate a unique key for the new drawing
        String drawingId = drawingsRef.push().getKey();

        // Create a new Drawing object
        Drawing drawing = new Drawing(drawingId, drawingName, System.currentTimeMillis(), 0);

        // Save the drawing to the Firebase Database
        drawingsRef.child(drawingId).setValue(drawing)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Drawing added successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to add drawing. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
