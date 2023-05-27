package com.abhimanyu.doda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawingAdapter drawingAdapter;
    private DatabaseReference drawingsRef;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        drawingsRef = FirebaseDatabase.getInstance().getReference("drawings");

        // Initialize RecyclerView and its layout manager
        RecyclerView recyclerView = findViewById(R.id.recyclerview_drawings);
        drawingAdapter = new DrawingAdapter(MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(drawingAdapter);

        // Fetch the list of drawings from the database
        fetchDrawings();

        // Initialize the addDrawingButton
        FloatingActionButton addDrawingButton = findViewById(R.id.fab_add_drawing);
        addDrawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDrawingDialog();
            }
        });


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
        builder.setMessage("Enter the drawing name:");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_drawing, null);
        builder.setView(dialogView);

        EditText editTextDrawingName = dialogView.findViewById(R.id.edittext_drawing_name);
//        TextView textViewSelectedImage = dialogView.findViewById(R.id.textView_selected_image);
        Button buttonSelectImage = dialogView.findViewById(R.id.button_select_image);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String drawingName = editTextDrawingName.getText().toString().trim();

                if (selectedImageUri != null) {
                    addDrawing(drawingName, selectedImageUri);
                } else {
                    Toast.makeText(MainActivity.this, "Please select an image for the drawing.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData(); // Assign selected image URI
        }
    }

    private void addDrawing(String drawingName, Uri imageUri) {
        // Generate a unique ID for the drawing
        String drawingId = drawingsRef.push().getKey();
        // Upload the image to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("drawings/" + drawingId);
        UploadTask uploadTask = fileRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        String currentTime = String.valueOf(System.currentTimeMillis());

                        // Create a new Drawing object
                        Drawing newDrawing = new Drawing(drawingId, drawingName, currentTime, 0, downloadUri.toString());

                        // Save the newDrawing to the database or perform any other required operations
                        drawingsRef.child(drawingId).setValue(newDrawing);
                        Toast.makeText(MainActivity.this, "Drawing added successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle failure to get download URL
                        Toast.makeText(MainActivity.this, "Failed to add drawing: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failure to upload image
                Toast.makeText(MainActivity.this, Uri.fromFile(new File(imageUri.getPath())).toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Failed to add drawing: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
