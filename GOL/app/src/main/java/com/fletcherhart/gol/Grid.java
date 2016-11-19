package com.fletcherhart.gol;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonahallibone on 11/18/16.
 */

@DynamoDBTable(tableName = "gol_cells")
public class Grid {
    private int id;
    private ArrayList<Boolean> grid = new ArrayList<Boolean>();

    @DynamoDBHashKey
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "grid")
    public ArrayList<Boolean> getGrid() {
        return grid;
    }

    public void setGrid(ArrayList<Boolean> cells) {
        this.grid = cells;
    }

    public void setGrid(Cell cells[]) {

        for(int x = 0; x < cells.length; x++) {
            this.grid.add(cells[x].getStatus());
        }
    }

}