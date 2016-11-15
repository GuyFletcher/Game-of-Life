package com.fletcherhart.gol;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public class LifeFragment extends Fragment {

    private int mGeneration;
    private Context context;

    private Cell [][] mCells;

    private int mPulseDiameter;				//Diameter of Pulsating cells
    private boolean mPulseIncreasing;	//Are the circles getting bigger or smaller
    private boolean mTouchEnabled = true;

    private Timer mTimer;
    private Colony mColony;
    private Button mAnimationController;
    private EditText mSizeField, mRowsField, mColumnsField;
    private Spinner mDelayBox, mAliveBox, mDeadBox;
    private TextView mDelayLabel, mAliveLabel, mDeadLabel;

    private RecyclerView mLifeRecycler;
    private RecyclerView.Adapter<ArrayHolder> mAdapter = new ArrayAdapter();

    private TextView mCurGen;

    private Handler mHandle;

    private static final int sDEFAULTDELAY = 1000;

    Button mCloneButton, mNextButton;

    private int mRows, mColumns, mSideLength;
    private boolean mRunning;
    private Paint mAlive, mDead;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_life_colony, container, false);

        mLifeRecycler = (RecyclerView) v.findViewById(R.id.life_recycler_view);
        mLifeRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 20));
        mLifeRecycler.setAdapter(mAdapter);



        mRows = 25;
        mColumns = 39;
        mSideLength = 20;

        mAlive.setColor(Color.BLUE);
        mDead.setColor(Color.YELLOW);

        mColony = new Colony(mRows, mColumns,  mSideLength, mAlive, mDead);
        //colonyPanel.setPreferredSize(new Dimension(columns * sideLength, mRows * sideLength));

        mNextButton = (Button) v.findViewById(R.id.life_button);
        mNextButton.setText("Next");
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mColony.updateColony();

                //Update Generation Label
                mCurGen.setText("Generation: " + Integer.toString(mColony.getGeneration()));
            }
        });

        mAnimationController.setText("Start Animation");
        mAnimationController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControllerListener control;
            }
        });

        mCloneButton = (Button) v.findViewById(R.id.life_button);
        mCloneButton.setText("Clone");
        mCloneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // EXTRA CREDIT:
        // Creating strings for the Spinneres
        mAliveBox = (Spinner) v.findViewById(R.id.spin_color);
        mAliveBox.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                switch (mAliveBox.getSelectedItemPosition()){
                    case 0:
                        mAlive.setColor(Color.BLUE);
                        break;
                    case 1:
                        mAlive.setColor(Color.CYAN);
                        break;
                    case 2:
                        mAlive.setColor(Color.GREEN);
                        break;
                    case 3:
                        mAlive.setColor(Color.MAGENTA);
                        break;
                    case 4:
                        mAlive.setColor(Color.GRAY);
                        break;
                    case 5:
                        mAlive.setColor(Color.TRANSPARENT);
                        break;
                    case 6:
                        mAlive.setColor(Color.RED);
                        break;
                    case 7:
                        mAlive.setColor(Color.WHITE);
                        break;
                    case 8:
                        mAlive.setColor(Color.YELLOW);
                        break;
                }

                //mColony.setAlive(mAlive);
            }
        });

        mDeadBox = (Spinner) v.findViewById(R.id.spin_color);
        mDeadBox.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
               switch (mDeadBox.getSelectedItemPosition()) {
                   case 0:
                       mDead.setColor(Color.BLUE);
                       break;
                   case 1:
                       mDead.setColor(Color.CYAN);
                       break;
                   case 2:
                       mDead.setColor(Color.GREEN);
                       break;
                   case 3:
                       mDead.setColor(Color.MAGENTA);
                       break;
                   case 4:
                       mDead.setColor(Color.GRAY);
                       break;
                   case 5:
                       mDead.setColor(Color.TRANSPARENT);
                       break;
                   case 6:
                       mDead.setColor(Color.RED);
                       break;
                   case 7:
                       mDead.setColor(Color.WHITE);
                       break;
                   case 8:
                       mDead.setColor(Color.YELLOW);
                       break;
               }

                //colonyPanel.setDead(mDead);
            }
        });

        mDelayBox = (Spinner) v.findViewById(R.id.spin_delay);
        mDelayBox.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                new DelayListener();
            }
        });


        // EXTRA CREDIT
        // Shows the proper items as selected.
        mAliveBox.setSelection(0);
        mDeadBox.setSelection(8);
        mDelayBox.setSelection(3);

        // EXTRA CREDIT
        // Labels for the Spinneres.
        mAliveLabel.setText("Alive Cell:");
        mDeadLabel.setText("Dead Cell:");
        mDelayLabel.setText("Delay:");

        //EXTRA CREDIT
        //Generation Stuff
        mCurGen.setText("Generation: 0");
        Button reset = (Button) v.findViewById(R.id.reset_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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

        // EXTRA CREDIT
        // Adds the GUI components to a panel, buttonPanel.
       /* bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(firstPanel, BorderLayout.NORTH);
        bottomPanel.add(secondPanel, BorderLayout.CENTER);
        bottomPanel.add(thirdPanel, BorderLayout.SOUTH);*/


        TextView rows = (TextView)v.findViewById(R.id.life_text);
        TextView columns = (TextView)v.findViewById(R.id.life_text);
        TextView size = (TextView)v.findViewById(R.id.life_text);



        // Groups the buttonPanel and colonyPanel
       /* Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(colonyPanel, BorderLayout.NORTH);
        contentPane.add(bottomPanel, BorderLayout.CENTER);*/

        return v;
    }

    public int getSideLength(){
        return mSideLength;
    }



    // Class to listen for the timer.
    private class ControllerListener implements View.OnClickListener{
        public void onClick(View v){
            if (mRunning){
                mHandle.removeCallbacks(r);
                mAnimationController.setText("Start Animation");
                mRunning = false;
            }
            else {
                mHandle.postDelayed(r, mDelayBox.getSelectedItemPosition());
                mAnimationController.setText("Stop Animation");
                mRunning = true;
            }
        }
    }

    private final Runnable r = new Runnable()
    {
        public void run()
        {
            mColony.updateColony();
        }
    };

    // EXTRA CREDIT
    private class DelayListener implements View.OnClickListener {
        public void onClick(View v){
            switch(mDelayBox.getSelectedItemPosition()){
                case 0:
                    mHandle.postDelayed(r, 250);
                    break;
                case 1:
                    mHandle.postDelayed(r, 500);
                    break;
                case 2:
                    mHandle.postDelayed(r, 750);
                    break;
                case 3:
                    mHandle.postDelayed(r, 1000);
                    break;
                case 4:
                    mHandle.postDelayed(r, 1250);
                    break;
                case 5:
                    mHandle.postDelayed(r, 1500);
                    break;
            }
        }
    }

    //EXTRA CREDIT
    //Reset Generation
    private class GenResetListener implements View.OnClickListener{
        public void onClick(View v){
            mColony.setGeneration(0);
            //Make Sure gets displayed
            mCurGen.setText("Generation: " + Integer.toString(mColony.getGeneration()));
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

    //EXTRA CREDIT
    //set the size of each cell
    private class SizeListener implements View.OnClickListener{
        public void onClick(View v){
            mSideLength = Integer.parseInt(mSizeField.getText().toString());
            mColony.setSideLength(mSideLength);
            mColony.setPreferredSize(mColumns * mSideLength, mRows * mSideLength);
           // pack();
           // repaint();
        }
    }

    // Draws the squares with the grid.
    public void paintComponent(Canvas page){


        //EXTRA CREDIT
        //	Draw all cells with circle for pulse
        if (page!= null){
            for(int i = 0; i < mRows; i++){
                for(int j = 0; j < mColumns; j++)
                {
                    mCells[i][j].drawCellAsCircle(page, mPulseDiameter);
                }
            }
        }

        if(mPulseDiameter >= mSideLength){
            mPulseIncreasing = false;
            //Do this incase The size of cells has been changed
            mPulseDiameter = mSideLength;
        }

        if(mPulseDiameter <= mSideLength/4){
            mPulseIncreasing = true;
        }

        //Change pulseDiameter size
        if(mPulseIncreasing){

            mPulseDiameter++;
        }
        else{
            mPulseDiameter--;
        }

        //EXTRA CREDIT
        //Taken out in favor of pulsing
        //drawColony(page);

        page.drawColor(Color.GRAY, PorterDuff.Mode.CLEAR);

        Paint p = new Paint();
        p.setColor(Color.BLACK);

        // Draw the grid.
        for (int i = 0; i < mColumns; i++)
            page.drawLine(i * mSideLength, 0, i * mSideLength, mSideLength * mRows, p);
        for (int i = 0; i < mRows; i++)
            page.drawLine(0, i * mSideLength, mSideLength * mColumns, i * mSideLength, p);
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

                // Checks to see if the cells are alive or dead. If they are alive
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

        // repaint();
    }

    public void setGeneration(int gen){
        mGeneration = gen;
    }
    public int getGeneration(){
        return mGeneration;
    }

    public void setSideLength(int side) {mSideLength = side;}
    public void setPreferredSize(int col, int row) {mColumns = col;
        mRows = row;
    }


    private class ArrayHolder extends RecyclerView.ViewHolder {

        private int mPosition, mPosition2;
        private Button mButton;
        private ImageView imageView;

        public ArrayHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.square, container, false));

            imageView = (ImageView) itemView.findViewById(R.id.cell_image);
            imageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation));

            mButton = (Button)itemView.findViewById(R.id.cell_button);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View view) {
                    if (mTouchEnabled && (mCells[mPosition][mPosition2].getStatus() == false)) {
                        mCells[mPosition][mPosition2].setStatus(true);
                        mAdapter.notifyItemChanged(mPosition); // reload ViewHolder
                    }
                }
            });
        }

        public void bindPosition(int p) {
            mPosition = p;
        }
    }

    private class ArrayAdapter extends RecyclerView.Adapter<ArrayHolder> {
        @Override
        public void onBindViewHolder(ArrayHolder holder, int position) {
            // tell holder which place on grid it is representing
            holder.bindPosition(position);
            // actually change image displayed
            if (mCells[position][position].getStatus() == true) {
                //change color to alive
            } else {
                //holder.mButton.setBackgroundResource(R.drawable.empty);
                //change color to dead
            }
        }

        @Override
        public ArrayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ArrayHolder(inflater, parent);
        }

        @Override
        public int getItemCount() {
            return mCells.length;
        }
    }

}
