package com.example.pedidosya2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final String USER_PREFS = "userPrefs";
    private static final String USER_ID_KEY = "userID";
    private static final String NAME_KEY = "name";
    private static final String EMAIL_KEY = "email";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    EditText editName, editEmail, editUsername, editPassword;
    Button saveButton, captureButton;
    ImageView profileImageView;
    DatabaseReference usersRef;
    String userID;
    Uri photoURI;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);
        captureButton = findViewById(R.id.captureButton);
        profileImageView = findViewById(R.id.profileImageView);

        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void saveChanges() {
        userID = getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(USER_ID_KEY, "");

        if (userID != null && !userID.isEmpty()) {
            boolean nameChanged = isNameChanged();
            boolean emailChanged = isEmailChanged();
            boolean usernameChanged = isUsernameChanged();
            boolean passwordChanged = isPasswordChanged();

            if (nameChanged || emailChanged || usernameChanged || passwordChanged) {
                Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No changes found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User ID is null or empty", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNameChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String currentName = sharedPreferences.getString(NAME_KEY, "");
        String newName = Objects.requireNonNull(editName.getText()).toString().trim();

        if (!newName.isEmpty() && !newName.equals(currentName)) {
            usersRef.child(userID).child(NAME_KEY).setValue(newName).addOnSuccessListener(aVoid -> {
                updateSharedPreferences(NAME_KEY, newName); // Actualizar en SharedPreferences
                Toast.makeText(EditProfileActivity.this, "Name updated", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show());
            return true;
        }
        return false;
    }

    private boolean isEmailChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String currentEmail = sharedPreferences.getString(EMAIL_KEY, "");
        String newEmail = Objects.requireNonNull(editEmail.getText()).toString().trim();

        if (!newEmail.isEmpty() && !newEmail.equals(currentEmail)) {
            usersRef.child(userID).child(EMAIL_KEY).setValue(newEmail).addOnSuccessListener(aVoid -> {
                updateSharedPreferences(EMAIL_KEY, newEmail); // Actualizar en SharedPreferences
                Toast.makeText(EditProfileActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show());
            return true;
        }
        return false;
    }

    private boolean isUsernameChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String currentUsername = sharedPreferences.getString(USERNAME_KEY, "");
        String newUsername = Objects.requireNonNull(editUsername.getText()).toString().trim();

        if (!newUsername.isEmpty() && !newUsername.equals(currentUsername)) {
            usersRef.child(userID).child(USERNAME_KEY).setValue(newUsername).addOnSuccessListener(aVoid -> {
                updateSharedPreferences(USERNAME_KEY, newUsername); // Actualizar en SharedPreferences
                Toast.makeText(EditProfileActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show());
            return true;
        }
        return false;
    }

    private boolean isPasswordChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String currentPassword = sharedPreferences.getString(PASSWORD_KEY, "");
        String newPassword = Objects.requireNonNull(editPassword.getText()).toString().trim();

        if (!newPassword.isEmpty() && !newPassword.equals(currentPassword)) {
            usersRef.child(userID).child(PASSWORD_KEY).setValue(newPassword).addOnSuccessListener(aVoid -> {
                updateSharedPreferences(PASSWORD_KEY, newPassword); // Actualizar en SharedPreferences
                Toast.makeText(EditProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show());
            return true;
        }
        return false;
    }

    private void showData() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        userID = sharedPreferences.getString(USER_ID_KEY, "");
        String nameUser = sharedPreferences.getString(NAME_KEY, "");
        String emailUser = sharedPreferences.getString(EMAIL_KEY, "");
        String usernameUser = sharedPreferences.getString(USERNAME_KEY, "");
        String passwordUser = sharedPreferences.getString(PASSWORD_KEY, "");

        editName.setText(nameUser);
        editEmail.setText(emailUser);
        editUsername.setText(usernameUser);
        editPassword.setText(passwordUser);
    }

    private void updateSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("EditProfileActivity", "Error occurred while creating the file", ex);
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.pedidosya2.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            profileImageView.setImageURI(photoURI);
            uploadImageToFirebase(photoURI);
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profileImageView.setImageURI(selectedImage);
            uploadImageToFirebase(selectedImage);
        }
    }

    private void dispatchGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child("users/" + userID + "/profile.jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    usersRef.child(userID).child("profileImageUrl").setValue(imageUrl).addOnSuccessListener(aVoid -> {
                                        updateSharedPreferences("profileImageUrl", imageUrl);
                                        Toast.makeText(EditProfileActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}