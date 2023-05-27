package com.abhimanyu.doda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DrawingAdapter drawingAdapter;
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
        drawingAdapter = new DrawingAdapter();
        recyclerView.setAdapter(drawingAdapter);

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(drawingAdapter);

        // Fetch the list of drawings from the database
        fetchDrawings();
    }

    private void fetchDrawings() {
        drawingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Drawing> drawings = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Drawing drawing = snapshot.getValue(Drawing.class);
                    if (drawing != null) {
                        drawings.add(drawing);
                    }
                }

                // Reverse the list to show the latest drawings first
                Collections.reverse(drawings);

                drawingAdapter.setDrawings(drawings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to fetch drawings. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void showAddDrawingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Drawing");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_drawing, null);
        builder.setView(view);

        EditText drawingNameEditText = view.findViewById(R.id.edittext_drawing_name);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String drawingName = drawingNameEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(drawingName)) {
                    // Generate a unique key for the new drawing
                    String drawingId = drawingsRef.push().getKey();

                    // Create a new Drawing object
                    Drawing newDrawing = new Drawing(drawingId, drawingName, System.currentTimeMillis(), 0);

                    // Add the new drawing to the Firebase Database
                    drawingsRef.child(drawingId).setValue(newDrawing);
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
