package com.example.hanabakery;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, Pname ,saveCurrentDate,saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName,InputProductDescription,InputProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey,downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        CategoryName = getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
//        Toast.makeText(this, CategoryName, Toast.LENGTH_SHORT).show();
        AddNewProductButton=findViewById(R.id.add_new_product);
        InputProductImage=findViewById(R.id.select_product_image);
        InputProductName=findViewById(R.id.product_name);
        InputProductDescription=findViewById(R.id.product_description);
        InputProductPrice=findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        //Gọi mở Gallery
        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenGallery();
            }
        });
        //Gọi hàm ValidateProductData
        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateProductData();
            }
        });

    }


    private void OpenGallery() {

        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    //Hàm trả về kết quả
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }

    private void  ValidateProductData()
    {
        Description =InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Please write product price...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductImageInformation();
        }
    }

    private void StoreProductImageInformation() {

        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, please wait while  we are adding the new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("yyyy-MM-dd");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask= filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message= e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();

                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "got the Product image save to Database Successfully...", Toast.LENGTH_SHORT).show();
                            SaveProductInforToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductInforToDatabase() {
        HashMap<String,Object> productMap= new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",CategoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
