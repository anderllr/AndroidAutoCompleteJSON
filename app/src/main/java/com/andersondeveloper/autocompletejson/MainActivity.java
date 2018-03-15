/***
 * Developed by: Anderson Rocha - andersondeveloper.com
 * This project is an example that explain how to consume a JSON object from a URL and puts the content
 * in an AutocompleteView
 *
 * JSON Object is getting from a URL and gives information about car brands and your relative codes and
 *   shows de code that was selected before
 *
 * This example uses String Resources for Labels.
 */

package com.andersondeveloper.autocompletejson;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static String url = "https://fabideia.com/devs/json/"; //path to car brands with codes
    private HashMap<String, String> carBrandList; //HashMap create to get car brand list and codes

    private static final ArrayList<String> CARS = new ArrayList<String>(); // Array to return only CARS
    private AutoCompleteTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carBrandList = new HashMap<>();

        textView = (AutoCompleteTextView)
                findViewById(R.id.cars_list);

        //component to show how car brand and code was selected
        final TextView textSelection = (TextView) findViewById(R.id.textSelection);

        //Event to get information after user selecion in autocomplete
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String)parent.getItemAtPosition(position);
                String id = carBrandList.get(selection);
                //Alterar aqui
                String id_name = String.format("ID: %s -> Selection: %s", id, selection);
                textSelection.setText(id_name);
            }
        });

        //Event to start an async class to get json
        new GetCarBrands().execute();
    }

    /**
     *  Private async task class to get json
     */

    private class GetCarBrands extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //You can put a progress dialog here

        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler handler = new HttpHandler();

            String jsonS = handler.getJSON(url);


            if (jsonS != null) {
                try {
                  //  JSONArray carBrands = new JSONArray(jsonS);
                    JSONObject jObj = new JSONObject(jsonS);

                    // Getting JSON Object nodes -> In this case Array has no name
                    JSONArray carBrands = jObj.getJSONArray("carbrands");

                    for (int i = 0; i < carBrands.length(); i++) {
                        JSONObject cb = carBrands.getJSONObject(i);

                        String id = cb.getString("id");
                        String name = cb.getString("name");



                        //adding each child node to HashMap
                        carBrandList.put(name, id);

                        //adding in a list of CARS
                        CARS.add(name);

                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("ER1", e.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    "JSON Parse error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "There is no JSON File in Server",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }

        //After get json I populate the adapter to show list of brands
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_dropdown_item_1line, CARS);

            textView.setAdapter(adapter);

            //This is executed on finish
            Log.e("JSON", carBrandList.toString());
        }
    }
}


