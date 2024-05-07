package com.example.detectorenfermedadesdeltomateapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigarionActivity extends AppCompatActivity {

    ViewPager sliderViewPager;
    LinearLayout dotIndicator;
    ViewPagerAdapter viewPagerAdapter;
    Button backButton, skipButton, nextButton;
    TextView[] dots;

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setDotIndicator(position);

            if (position > 0){
                backButton.setVisibility(View.VISIBLE);
            } else{
                backButton.setVisibility(View.INVISIBLE);
            }

            if (position == 7){
                nextButton.setText("Terminar");
            } else{
                nextButton.setText("Siguiente");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigarion);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        skipButton = findViewById(R.id.skipButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(0)>0){
                    sliderViewPager.setCurrentItem(getItem(-1),true);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(0)<7){
                    sliderViewPager.setCurrentItem(getItem(1),true);
                }else{
                    Intent intent = new Intent(NavigarionActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigarionActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sliderViewPager = (ViewPager) findViewById(R.id.sliderViewPager);
        dotIndicator = (LinearLayout) findViewById(R.id.dotIndicator);

        viewPagerAdapter = new ViewPagerAdapter(this);
        sliderViewPager.setAdapter(viewPagerAdapter);

        setDotIndicator(0);
        sliderViewPager.addOnPageChangeListener(viewPagerListener);

    }

    public void setDotIndicator(int position){
        dots = new TextView[8];
        dotIndicator.removeAllViews();

        for(int i=0; i< dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226",Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.EXITO,getApplicationContext().getTheme()));
            dotIndicator.addView(dots[i]);
            
        }

        dots[position].setTextColor(getResources().getColor(R.color.TEXTO_PRINCIPAL,getApplicationContext().getTheme()));
    }

    private int getItem(int i){
        return sliderViewPager.getCurrentItem() + i;
    }

}