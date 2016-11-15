package com.fletcherhart.gol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.fletcherhart.gol.Cell;
import com.fletcherhart.gol.R;

import java.util.ArrayList;
import java.util.List;

public class LifeActivity extends AppCompatActivity {

    private GridLayoutManager mGrid;
    private Context mContext;

    RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life);

        Canvas page = new Canvas();

        // Get the application context
        mContext = getApplicationContext();

        // Change the action bar color
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#FF677589"))
        );

        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.cell_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);



        // Define a layout for RecyclerView
        mLayoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);




        // Set the adapter for RecyclerView
        mRecyclerView.setAdapter(mAdapter);



    }


}
