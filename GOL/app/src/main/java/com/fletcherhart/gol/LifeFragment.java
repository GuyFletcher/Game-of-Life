package com.fletcherhart.gol;


import android.content.Context;
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


public class LifeFragment extends Fragment {

    private int mGeneration;
    private Context context;

    private Cell [][] mCells;

    private Cell[] mCell = new Cell[400];

    private int mPulseDiameter;				//Diameter of Pulsating cells
    private boolean mPulseIncreasing;	//Are the circles getting bigger or smaller
    private boolean mTouchEnabled = true;

    private EditText mSizeField, mRowsField, mColumnsField;
    private TextView mDelayLabel, mAliveLabel, mDeadLabel;

    private RecyclerView mLifeRecycler;
    private RecyclerView.Adapter<GridHolder> mAdapter = new GridAdapter();

    private TextView mCurGen;

    private Handler mHandle;

    private static final int sDEFAULTDELAY = 1000;

    private Spinner mAliveBox, mDeadBox;

    private int mRows, mColumns;
    public int mColor, mColor2;
    private boolean mRunning;
    private Paint mAlive = new Paint(), mDead = new Paint();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_life, container, false);

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

        mLifeRecycler = (RecyclerView) v.findViewById(R.id.reycler_gol);
        mLifeRecycler.setLayoutManager(new GridLayoutManager(getActivity(), mColumns));
        mLifeRecycler.setAdapter(mAdapter);

        mAlive.setColor(Color.BLUE);
        mDead.setColor(Color.YELLOW);

        Button mNextButton = (Button) v.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int k = 0;
                for(int i = 0;i < mRows;i++)
                {
                    for(int j = 0; j < mRows;j++)
                    {
                        mCells[i][j] = mCell[k];
                        k++;
                    }
                }

                updateColony();
                mAdapter.notifyDataSetChanged();
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

                //every x milleseconds call updateColony()
                //stop on second press

            }
        });

        Button mCloneButton = (Button) v.findViewById(R.id.life_button);
        mCloneButton.setText("Clone");
        mCloneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    case 2:
                        mColor = 2;
                        break;
                    case 3:
                        mColor = 3;
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
                    case 2:
                        mColor2 = 2;
                        break;
                    case 3:
                        mColor2 = 3;
                        break;
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       final Spinner mDelayBox = (Spinner) v.findViewById(R.id.spin_delay);


        // EXTRA CREDIT
        // Shows the proper items as selected.
        mAliveBox.setSelection(0);
        mDeadBox.setSelection(8);
        mDelayBox.setSelection(3);


        //EXTRA CREDIT
        //Generation Stuff
/*        mCurGen.setText("Generation: 0");
        Button reset = (Button) v.findViewById(R.id.reset_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        //EXTRA CREDIT
        //Make the Save and Open buttons
        Button save = (Button) v.findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button open = (Button) v.findViewById(R.id.open_button);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //EXTRA
        //Reset button
        Button resetb = (Button) v.findViewById(R.id.resetb_button);
        resetb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // new ResetListener();
            }
        });



        TextView rows = (TextView)v.findViewById(R.id.life_text);
        TextView columns = (TextView)v.findViewById(R.id.life_text);
        TextView size = (TextView)v.findViewById(R.id.life_text);


        return v;
    }

    // Class to listen for the timer.
    private class ControllerListener implements View.OnClickListener{
        public void onClick(View v){
            if (mRunning){
                mHandle.removeCallbacks(r);
               // mAnimationController.setText("Start Animation");
                mRunning = false;
            }
            else {
               // mHandle.postDelayed(r, mDelayBox.getSelectedItemPosition());
                //mAnimationController.setText("Stop Animation");
                mRunning = true;
            }
        }
    }

    private final Runnable r = new Runnable()
    {
        public void run()
        {
            updateColony();
        }
    };

    //EXTRA CREDIT
    //Reset Generation
    private class GenResetListener implements View.OnClickListener{
        public void onClick(View v){
            setGeneration(0);
            //Make Sure gets displayed
            mCurGen.setText("Generation: " + Integer.toString(getGeneration()));
        }
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
        int k = 0;
        for(int i = 0; i < mRows; i++)
        {
            for(int j = 0; j < mColumns; j++)
            {
                mCell[k] = mCells[i][j];
                //System.out.println("mCell at : " + k + mCell[k].getStatus() + " mCells: " + mCells[i][j].getStatus());
                k++;
            }
        }
    }

    public void setGeneration(int gen){
        mGeneration = gen;
    }
    public int getGeneration(){
        return mGeneration;
    }

    public void setPreferredSize(int col, int row) {mColumns = col;
        mRows = row;
    }


    private class GridHolder extends RecyclerView.ViewHolder {

        private int mPosition;
        private Button mButton;


        public GridHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.square, container, false));


            mButton = (Button)itemView.findViewById(R.id.cell_button);
            Animation pulse = AnimationUtils.loadAnimation(mButton.getContext(), R.anim.animation);
            mButton.startAnimation(pulse);

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
        }

    }

    private class GridAdapter extends RecyclerView.Adapter<GridHolder> {
        public void onBindViewHolder(GridHolder holder, int position) {
            // tell holder which place on grid it is representing
            holder.bindPosition(position);
            // actually change image displayed
            if(mCell[position].getStatus() == true)
            {
                //holder.mButton.setBackgroundResource(R.drawable.red);
                changeButton(holder);
            }
            else
            {
               //holder.mButton.setBackgroundResource(R.drawable.green);
                changeButton2(holder);
            }
        }

        public void changeButton(GridHolder holder)
        {
            if(mColor == 0)
            {
                holder.mButton.setBackgroundResource(R.drawable.red);
            }
            else if (mColor == 1)
            {
                holder.mButton.setBackgroundResource(R.drawable.green);
            }
            else if (mColor == 2)
            {
                holder.mButton.setBackgroundResource(R.drawable.blue);
            }
            else if (mColor == 3)
            {
                holder.mButton.setBackgroundResource(R.drawable.purple);
            }
        }

        public void changeButton2(GridHolder holder)
        {
            if(mColor2 == 0)
            {
                holder.mButton.setBackgroundResource(R.drawable.red);
            }
            else if (mColor2 == 1)
            {
                holder.mButton.setBackgroundResource(R.drawable.green);
            }
            else if (mColor2 == 2)
            {
                holder.mButton.setBackgroundResource(R.drawable.blue);
            }
            else if (mColor2 == 3)
            {
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
