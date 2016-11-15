package com.fletcherhart.gol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.Dimension;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Fletcher on 11/2/2016.
 */

public class Colony extends Fragment {

    private int mGeneration;

    private int mRows, mColumns;				//dimensions
    private Cell [][] mCells;

    private int mPulseDiameter;				//Diameter of Pulsating cells
    private boolean mPulseIncreasing;	//Are the circles getting bigger or smaller
    private int mSideLength;						// The length of a cell
    private Paint mAlive, mDead;				// The clor of living and dead cells

    public Colony() {

    };

    public Colony(int rows, int columns, int side, Paint alive, Paint dead)
    {
        for(int i = 0; i < mRows; i++)
        {
            for (int j = 0; j < mColumns; i++)
            {
                mCells[i][j] = new Cell(i, j, side, alive, dead);
            }
        }

        this.mRows = rows;
        this.mColumns = columns;
        this.mSideLength = side;

        mGeneration = 0;

        ////Stuff for pulse delay?????
    }

    // Draws the squares with the grid.
    public void paintComponent(Canvas page){


        //EXTRA CREDIT
        //	Draw all cells with circle for pulse
        if (page!= null){
            for(int i = 0; i < mRows; i++){
                for(int j = 0; j < mColumns; j++){
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

    public Cell[][] getCells() {return mCells;}
    public Cell getCellatPos(int i, int j) {return mCells[i][j];}

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
}
