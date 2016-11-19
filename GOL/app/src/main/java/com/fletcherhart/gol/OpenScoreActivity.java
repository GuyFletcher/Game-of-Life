package com.fletcherhart.gol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class OpenScoreActivity extends AppCompatActivity {

    private ListView mListView;
    ArrayList<String> grid = new ArrayList<String>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_score);

        new Thread(new Runnable() {
            public void run() {


                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-west-2:81639732-a618-4d90-a474-a9809fe1c72e", // Identity Pool ID
                        Regions.US_WEST_2 // Region
                );

                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                ddbClient.setEndpoint("https://dynamodb.us-west-2.amazonaws.com");

                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                PaginatedScanList<Grid> result = mapper.scan(Grid.class, scanExpression);

                for (int x = 0; x < result.size(); x++) {
                    setArray(result.get(x).getId());
                    System.out.println(result.get(x).getId());
                }

            }
        }).start();


        setList();


    }

    private void setList() {

        //Hack I'm sorry

        // Get ListView object from xml
        mListView = (ListView) findViewById(R.id.pattern_list);

        // Defined Array values to show in ListView
        String[] values = grid.toArray(new String[grid.size()]);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        mListView.setAdapter(adapter);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String  itemValue    = (String) mListView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();

            }

        });
    }

    private void setArray(int id) {
        grid.add(Integer.toString(id));
    }

}
