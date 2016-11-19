package com.fletcherhart.gol;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;

import android.support.v7.widget.RecyclerView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LifeFragment extends Fragment {

    private int mGeneration;
    private Context mContext;

    private static final String EXTRA = "com.fletcher.gol";

    private Cell [][] mCells;

    private Cell[] mCell = new Cell[400];

    private boolean mTouchEnabled = true;

    private EditText mSizeField, mRowsField, mColumnsField;

    private RecyclerView mLifeRecycler;
    private RecyclerView.Adapter<GridHolder> mAdapter = new GridAdapter();

    private int mDelay = 1000;

    private Spinner mAliveBox, mDeadBox, mDelayBox;

    private int mRows, mColumns;
    public int mColor, mColor2;
    private boolean mRunning;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if(mRunning)
            {
                updateColony();
               // mAdapter.notifyDataSetChanged();
                handler.postDelayed(runnable, mDelay);
            }
            else
            {
                handler.removeCallbacks(runnable);
            }

            //handler.postDelayed(this, 1000);
        }
    };



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_life, container, false);

        Intent intent = getActivity().getIntent();



        mRows = 20;
        mColumns = 20;


        mCells = new Cell[mRows][mColumns];

        for(int i = 0; i < mRows; i++)
        {
            for(int j = 0; j < mColumns; j++)
            {
                mCells[i][j] = new Cell();
            }
        }


        for(int i = 0; i < mRows*mColumns; i++)
        {
                mCell[i] = new Cell();
        }

        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            mCell = (Cell[]) bundle.getSerializable(EXTRA);
        }

        //mCell = (Cell[]) getActivity().getIntent().getSerializableExtra(EXTRA);

        mLifeRecycler = (RecyclerView) v.findViewById(R.id.reycler_gol);
        mLifeRecycler.setLayoutManager(new GridLayoutManager(getActivity(), mColumns));
        mLifeRecycler.setAdapter(mAdapter);

        Button mNextButton = (Button) v.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateColony();
                //mAdapter.notifyDataSetChanged();
                //System.out.println("Changed");

                //Update Generation Label
              //  mCurGen.setText("Generation: " + Integer.toString(mColony.getGeneration()));
            }
        });

        Button mAnimationController = (Button) v.findViewById(R.id.animation);
        mAnimationController.setText("Start Animation");
        mAnimationController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mRunning)
                {
                    mRunning = false;
                }
                else
                {
                    mRunning = true;
                    handler.postDelayed(runnable, mDelay);
                }
            }
        });

        Button mCloneButton = (Button) v.findViewById(R.id.life_button);
        mCloneButton.setText("Clone");
        mCloneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(getActivity(), LifeActivity.class);
               i.putExtra(EXTRA, mCell);
               getActivity().startActivity(i);
            }
        });

        // EXTRA CREDIT:
        // Creating strings for the Spinneres
        mAliveBox = (Spinner) v.findViewById(R.id.spin_alive);
        mAliveBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mColor = 0;
                        break;
                    case 1:
                        mColor = 1;
                        break;
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDeadBox = (Spinner) v.findViewById(R.id.spin_dead);
        mDeadBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mColor2 = 0;
                        break;
                    case 1:
                        mColor2 = 1;
                        break;
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       mDelayBox = (Spinner) v.findViewById(R.id.spin_delay);
       mDelayBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               switch (position) {
                   case 0:
                       mDelay = 250;
                       break;
                   case 1:
                       mDelay = 500;
                       break;
                   case 2:
                       mDelay = 750;
                       break;
                   case 3:
                       mDelay = 1000;
                       break;
                   case 4:
                       mDelay = 1250;
                       break;
                   case 5:
                       mDelay = 1500;
                       break;
               }

           }
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });


        // EXTRA CREDIT
        // Shows the proper items as selected.
        mAliveBox.setSelection(0);
        mDeadBox.setSelection(0);
        mDelayBox.setSelection(3);

        //EXTRA CREDIT
        //Make the Save and Open buttons
        Button save = (Button) v.findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                                getActivity().getApplicationContext(),
                                "us-west-2:81639732-a618-4d90-a474-a9809fe1c72e", // Identity Pool ID
                                Regions.US_WEST_2 // Region
                        );



                        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                        ddbClient.setEndpoint("https://dynamodb.us-west-2.amazonaws.com");

                        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                        Grid grid = new Grid();
                        Random rand = new Random();
                        int value = rand.nextInt(50000);
                        grid.setId(value);
                        grid.setGrid(mCell);
                        mapper.save(grid);


                        }
                    }).start();
            }
        });
        Button open = (Button) v.findViewById(R.id.open_button);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), OpenScoreActivity.class);
                i.putExtra(EXTRA, mCell);
                getActivity().startActivity(i);
            }
        });

        return v;
    }

    //EXTRA CREDIT
    //Open Save dialog and call method of colonyPanel to store it
    private class SaveListener implements View.OnClickListener {
        public void onClick(View v){
           /* JFileChooser daChooser = new JFileChooser();
            //open save dialog
            daChooser.showSaveDialog(LifeFrame.this);
            //get which file was chosen and save it to there
            File f = daChooser.getSelectedFile();
            colonyPanel.saveState(f);*/
        }
    }

    //EXTRA CREDIT
    //Open Open dialog and call method of colonyPanel to open it
    private class OpenListener implements View.OnClickListener {
        public void onClick(View v){
          /*  JFileChooser daChooser = new JFileChooser();
            //open open dialog
            daChooser.showOpenDialog(LifeFrame.this);
            //get which file was chosen and open it
            File f = daChooser.getSelectedFile();
            colonyPanel.openState(f);*/
        }
    }

    //  A method that updates the entire colony to the next generation.
    public void updateColony(){

        int k = 0;
        for(int i = 0; i < mRows; i++)
        {
            for(int j = 0; j < mColumns; j++)
            {
                mCells[i][j] = mCell[k];
                k++;
            }
        }

        // Creates an array of bytes that keep track of the number of cells living
        byte [][] livingNeighborsCount = new byte[mRows][mColumns];

        // Counts the number of neighbors a cell has and stores it in the array
        for(int i = 0; i < mRows; i++){
            for(int j = 0; j < mColumns; j++){

                // Variables to save positions left and right of mRow and mColumn
                int leftOfmRow = i + mRows - 1;
                int rightOfmRow = i + 1;
                int leftOfmColumn = j + mColumns - 1;
                int rightOfmColumn = j + 1;

                // Checks to see if the cells are alive or blue. If they are alive
                // it increments the count for living neighbors.
                if ( mCells[i][j].getStatus() ){
                    livingNeighborsCount[leftOfmRow % mRows][leftOfmColumn % mColumns]++;
                    livingNeighborsCount[leftOfmRow % mRows][j % mColumns]++;
                    livingNeighborsCount[(i + mRows - 1) % mRows][rightOfmColumn % mColumns]++;
                    livingNeighborsCount[i % mRows][leftOfmColumn % mColumns]++;
                    livingNeighborsCount[i % mRows][rightOfmColumn % mColumns]++;
                    livingNeighborsCount[rightOfmRow % mRows][leftOfmColumn % mColumns]++;
                    livingNeighborsCount[rightOfmRow % mRows][j % mColumns]++;
                    livingNeighborsCount[rightOfmRow % mRows][rightOfmColumn % mColumns]++;
                }
            }
        }

        // Changes the status of the cell based on the number of living
        // neighbors it has.
        for (int i = 0; i < mRows; i++){
            for (int j = 0; j < mColumns; j++){
                // If the cell has 4 or more living neighbors, it dies
                // by overcmRowding.
                if (livingNeighborsCount[i][j] >= 4){
                    mCells[i][j].setStatus(false);
                }

                // A cell dies by exposure if it has 0 or 1 living neighbors.
                if (livingNeighborsCount[i][j] < 2){
                    mCells[i][j].setStatus(false);
                }

                // A cell is born if it has 3 living neighbors.
                if (livingNeighborsCount[i][j] == 3){
                    mCells[i][j].setStatus(true);
                    // EXTRA CREDIT
                    mCells[i][j].resetAge();
                }
            }
        }

        // EXTRA CREDIT
        // Age all the cells.
        for (int i = 0; i < mRows; i++){
            for (int j = 0; j < mColumns; j++){
                mCells[i][j].age();
            }
        }

        //EXTRA CREDIT
        //Increase generation
        mGeneration++;
        k = 0;
        for(int i = 0; i < mRows; i++)
        {
            for(int j = 0; j < mColumns; j++)
            {
                mCell[k] = mCells[i][j];
                k++;
            }
        }
       mAdapter.notifyDataSetChanged();
    }

    private class GridHolder extends RecyclerView.ViewHolder {

        private int mPosition;
        private Button mButton;


        public GridHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.cell, container, false));


            mButton = (Button)itemView.findViewById(R.id.cell_button);


            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View view) {
                    if (mTouchEnabled && (mCell[mPosition].getStatus() == false)) {
                        mCell[mPosition].setStatus(true);
                        mAdapter.notifyItemChanged(mPosition); // reload ViewHolder
                    }
                    else
                    {
                        mCell[mPosition].setStatus(false);
                        mAdapter.notifyItemChanged(mPosition); // reload ViewHolder
                    }
                }
            });
        }

        public void bindPosition(int p) {
            mPosition = p;
            Animation pulse = AnimationUtils.loadAnimation(mButton.getContext(), R.anim.animation);
            mButton.startAnimation(pulse);
        }

    }

    private class GridAdapter extends RecyclerView.Adapter<GridHolder> {
        public void onBindViewHolder(GridHolder holder, int position) {
            // tell holder which place on grid it is representing
            holder.bindPosition(position);
            // actually change image displayed
            if (mCell[position].getStatus() == true) {
                // holder.mButton.setBackgroundResource(R.drawable.blue);
                changeButton(holder);
            } else {
                //holder.mButton.setBackgroundResource(R.drawable.red);
                changeButton2(holder);
            }
        }

        public void changeButton(GridHolder holder) {
            if (mColor == 0) {
                holder.mButton.setBackgroundResource(R.drawable.green);
            } else if (mColor == 1) {
                holder.mButton.setBackgroundResource(R.drawable.blue);
            }
        }

        public void changeButton2(GridHolder holder) {
            if (mColor2 == 0) {
                holder.mButton.setBackgroundResource(R.drawable.red);
            } else if (mColor2 == 1) {
                holder.mButton.setBackgroundResource(R.drawable.purple);
            }
        }

        @Override
        public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new GridHolder(inflater, parent);
        }

        @Override
        public int getItemCount() {
            return mCell.length;
        }
    }
}
