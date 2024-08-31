package com.example.artbookf.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.artbookf.Model.Art;
import com.example.artbookf.R;
import com.example.artbookf.Roomdb.ArtDao;
import com.example.artbookf.Roomdb.ArtDatabase;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddFragment extends Fragment {

    private EditText textName;
    private EditText textDesc;
    private ImageView imgView;
    private Button butonSave;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Bitmap selectedImage;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();

    ArtDatabase db;
    ArtDao artDao;


    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        textName = view.findViewById(R.id.txtName);
        textDesc = view.findViewById(R.id.txtDesc);
        imgView = view.findViewById(R.id.imageView);
        butonSave = view.findViewById(R.id.btnSave);
        db= Room.databaseBuilder(getContext(),ArtDatabase.class,"Art").build();
        artDao=db.artDao();

        imgView.setOnClickListener(v -> selectImage());

        butonSave.setOnClickListener(v -> save());

        return view;
    }

    private void save() {
        String name=textName.getText().toString().trim();
        String desc=textDesc.getText().toString().trim();
        if(name.isEmpty() || desc.isEmpty()){
            Toast.makeText(getContext(),"Enter name and description",Toast.LENGTH_LONG).show();
        }
        else{

            byte[] imageInBytes = null;
            if (selectedImage != null) {
                imageInBytes = convertBitmapToByteArray(selectedImage);
            }

           try {
               Art art=new Art(textName.getText().toString(),textDesc.getText().toString(),imageInBytes);
               compositeDisposable.add(artDao.insert(art)
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(this::handleResponse)
               );
           }
           catch (Exception e){
               e.printStackTrace();
               Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();

           }
        }


    }

    private void handleResponse(){
        Toast.makeText(getContext(),"Art saved",Toast.LENGTH_SHORT).show();
        textName.setText("");
        textDesc.setText("");
        selectedImage = null;
        imgView.setImageResource(R.drawable.image_search24);


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void selectImage() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(getView(), "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", v -> permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE))
                        .show();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentGallery);
        }
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        Uri imageData = intentFromResult.getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                imgView.setImageBitmap(selectedImage);
                            } else {
                                selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageData);
                                imgView.setImageBitmap(selectedImage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentGallery);
                } else {
                    Toast.makeText(getContext(), "Permission needed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

}
