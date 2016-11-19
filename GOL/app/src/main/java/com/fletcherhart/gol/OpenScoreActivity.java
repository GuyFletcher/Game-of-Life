package com.fletcherhart.gol;

import android.content.Intent;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OpenScoreActivity extends AppCompatActivity {

    private static final String EXTRA = "com.fletcher.gol";
    //List view
    private ListView mListView;
    //The 'grid' for a cell
    private ArrayList<String> grid = new ArrayList<String>();
    //Cell array
    private Cell[] mCell = new Cell[400];
    //Loaded data from thread
    List<Grid> finalData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_score);

        //Future variable to bring data outside of the thread and allow it to be usable.

        Future<List<Grid>> data = Executors.newSingleThreadExecutor().submit(new Callable<List<Grid>>() {
               @Override
               public List<Grid> call() throws Exception {
                   //Use Amazon Cognito to access DynamoDB
                   CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                           getApplicationContext(),
                           "us-west-2:81639732-a618-4d90-a474-a9809fe1c72e", // Identity Pool ID
                           Regions.US_WEST_2 // Region
                   );

                   AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                   ddbClient.setEndpoint("https://dynamodb.us-west-2.amazonaws.com");

                   DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                   DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                    //Return the list
                   return mapper.scan(Grid.class, scanExpression);


               }
           });


        try {
            //Get data from 'Future' typed variable
            finalData = data.get();
            //Add to the list
            for(int x = 0; x < finalData.size(); x++) {
                grid.add("Board with random ID " + Integer.toString(finalData.get(x).getId()));
            }
            //Set the list
            setList();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        } catch (ExecutionException ex) {
            System.out.println(ex);
        }




    }


    private void setList() {


        // Get ListView object from xml
        mListView = (ListView) findViewById(R.id.pattern_list);

        // Defined Array values to show in ListView
        String[] values = grid.toArray(new String[grid.size()]);

        //Adapter for the array to the list
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
                        "Opening : "+itemValue , Toast.LENGTH_LONG)
                        .show();


                System.out.println(position);

                ArrayList<Boolean> pos = finalData.get(position).getGrid();

                //Make cells to pass back to view
                for(int x = 0; x < 400; x++) {
                    mCell[x] = new Cell();
                    mCell[x].setStatus(pos.get(x));
                }
                //Send data
                Intent i = new Intent(getApplicationContext(), LifeActivity.class);
                i.putExtra(EXTRA, mCell);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);
                finish();
            }

        });
    }

}
