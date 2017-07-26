package com.cimcitech.forkliftdispatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class FrequencyActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView1, imageView2, imageView3, imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequency);
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getXmlView();
    }

    private void getXmlView() {
        this.findViewById(R.id.layout_view_1).setOnClickListener(this);
        this.findViewById(R.id.layout_view_2).setOnClickListener(this);
        this.findViewById(R.id.layout_view_3).setOnClickListener(this);
        this.findViewById(R.id.layout_view_4).setOnClickListener(this);
        imageView1 = (ImageView) this.findViewById(R.id.image_view_1);
        imageView2 = (ImageView) this.findViewById(R.id.image_view_2);
        imageView3 = (ImageView) this.findViewById(R.id.image_view_3);
        imageView4 = (ImageView) this.findViewById(R.id.image_view_4);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_view_1:
                showImageState(imageView1);
                break;
            case R.id.layout_view_2:
                showImageState(imageView2);
                break;
            case R.id.layout_view_3:
                showImageState(imageView3);
                break;
            case R.id.layout_view_4:
                showImageState(imageView4);
                break;
        }
    }

    public void showImageState(ImageView view) {
        imageView1.setVisibility(View.GONE);
        imageView2.setVisibility(View.GONE);
        imageView3.setVisibility(View.GONE);
        imageView4.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }
}
