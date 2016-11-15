package com.fletcherhart.gol;

/**
 * Created by Fletcher on 11/13/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LifeLab {

    private static LifeLab sLifeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private Colony mColony;

    public static LifeLab get(Context context) {
        if (sLifeLab == null) {
            sLifeLab = new LifeLab(context);
        }
        return sLifeLab;
    }

    private LifeLab(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setColony(Colony c) {mColony = c;}
    public Colony getColony() {return mColony;}
}
