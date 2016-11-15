package com.fletcherhart.gol;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;


import android.graphics.Color;
import android.graphics.*;

import java.io.*;

public class Cell implements Serializable {

    // EXTRA CREDIT

    // Cells live for a maximum of 10 generations
    private final static int LIFESPAN = 10;
    // The speed that the color changes at.
    private final static int COLORCHANGESPEED = 20;

    // EXTRA CREDIT
    private int mAge;					// The age of the cell
    // Instance Variables
    private int mRow, mColumn;	// The mRow and column of the cell
    private boolean mStatus = false; 	// The status of the cell
    // false means dead; true means alive
    private int mSideLength;						// The length of a cell
    private Paint mAlive, mDead;				// The clor of living and dead cells

    // Constructor for a cell that sets the status, the mRow and the column
    public Cell (int row, int column, int sideLength, Paint alive, Paint dead){
        mStatus = false;
        this.mRow = row;
        this.mColumn = column;
        this.mSideLength = sideLength;
        this.mAlive = alive;
        this.mDead = dead;
    }

    public void setAlive(Paint aliveColor){
        this.mAlive = aliveColor;
    }

    public void setDead(Paint deadColor){
        this.mDead = deadColor;
    }

    public void setSideLength(int sideLength){
        this.mSideLength = sideLength;
    }

    public void changeStatus(){
        mStatus = !mStatus;

        // EXTRA CREDIT
        //If the cell is alive set it's age to zero
        if (mStatus){
            mAge = 0;
        }
    }

    // Sets the status.
    public void setStatus(boolean newStatus){
        mStatus = newStatus;
    }

    // Returns the status.
    public boolean getStatus(){
        return mStatus;
    }

    // EXTRA CREDIT
    //Self Explanatory
    public void resetAge(){
        mAge = 0;
    }

    // EXTRA CREDIT
    // Kills the cell if it is too old and increments the age.
    public void age(){
        //If age is greater than or equal to LIFESPAN, kill it
        //Otherwise increase the age
        if (mAge >= LIFESPAN){
            mStatus = false;
        }
        else
            mAge++;
    }

    //EXTRA CREDIT
    //draws cells as circles
    public void drawCellAsCircle(Canvas page, int diameter){
        //	All get a 'dead color' background
        //page.setColor(mDead);
        page.drawRect(mColumn * mSideLength, mRow * mSideLength, mSideLength, mSideLength, mDead);

        //	 EXTRA CREDIT

        if (mStatus) {

            // The color change based on the age of the cell.
            int ageFactor = mAge * COLORCHANGESPEED;

            // Sets the color to a new color based on the age factor
            // making sure that the value given does not exceed 255.
            Paint currentColor = new Paint(
                    Math.min(255, mAlive.getColor() + ageFactor));
            //page.setColor(currentColor);

            page.drawOval(((mColumn * mSideLength + mSideLength / 2) - diameter / 2),
                    ((mRow * mSideLength + mSideLength / 2) - diameter / 2), diameter, diameter, currentColor);

        }
    }


    // Draws the cell as a different color depending on whether it is alive
    // or dead.
    public void drawCell(Canvas page){

        // EXTRA CREDIT
        if (mStatus){

            // The color change based on the age of the cell.
            int ageFactor = mAge * COLORCHANGESPEED;

            // Sets the color to a new color based on the age factor
            // making sure that the value given does not exceed 255.
            Paint currentColor = new Paint(
                    Math.min(255, mAlive.getColor() + ageFactor));
            //page.setColor(currentColor);
        }

        if (!mStatus){
            // page.setColor(mDead);
        }

        page.drawRect(mColumn * mSideLength, mRow * mSideLength, mSideLength, mSideLength, mDead);
    }

    public void setAge(int a){
        mAge = a;
    }

    public int getAge(){
        return mAge;
    }
}
