package com.example.hanabakery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView categoryBread,categoryDoughnut,categoryCupcake;
    private ImageView categoryPizza,categoryCake,categoryBun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);


        categoryBread=(ImageView)findViewById(R.id.categorybread);
        categoryDoughnut=(ImageView)findViewById(R.id.categorydoughnut);
        categoryCupcake=(ImageView)findViewById(R.id.categorycupcake);
        categoryPizza=(ImageView)findViewById(R.id.categorypizza);
        categoryCake=(ImageView)findViewById(R.id.categorycake);
        categoryBun=(ImageView)findViewById(R.id.categorybun);




        categoryBread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","Bread");
                startActivity(intent);
            }
        });
        categoryDoughnut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","Doughnut");
                startActivity(intent);
            }
        });
        categoryCupcake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","Cup cake");
                startActivity(intent);
            }
        });
        categoryPizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","Pizza");
                startActivity(intent);
            }
        });
        categoryCake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","Cake");
                startActivity(intent);
            }
        });
        categoryBun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","Bun");
                startActivity(intent);
            }
        });
    }
}
