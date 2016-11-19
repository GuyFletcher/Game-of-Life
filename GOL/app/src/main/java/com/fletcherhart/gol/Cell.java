package com.fletcherhart.gol;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;


import android.graphics.Color;
import android.graphics.*;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

import java.io.*;

@DynamoDBDocument
public class Cell implements Serializable{

    // EXTRA CREDIT

    // Cells live for a maximum of 10 generations
    private final static int LIFESPAN = 10;

    // EXTRA CREDIT
    private int mAge;					// The age of the cell
    // Instance Variables
    private int mRow, mColumn;	// The mRow and column of the cell
    private boolean mStatus = false; 	// The status of the cell
    private int mSideLength;						// The length of a cell

    // Constructor for a cell that sets the status, the mRow and the column
    public Cell (){
        mStatus = false;
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

    public void setAge(int a){
        mAge = a;
    }

    public int getAge(){
        return mAge;
    }
}
