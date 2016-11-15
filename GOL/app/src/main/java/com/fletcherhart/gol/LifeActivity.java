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

public class LifeActivity extends SingleFragmentActivity {
    protected Fragment createFragment() {
        return new LifeFragment();
    }
}
