package com.example.fernando.eventsapp;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class ListofEvents extends ActionBarActivity {
    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADD ="location";
    private static final String EventsURL = "http://174.77.55.217:8080/query.php";
    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_events);
        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<>();
        getData();
    }
    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String response = "";
                try {
                    Log.d("lol","waiting");
                    url = new URL(EventsURL);
                    Log.d("lol","ioio");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    //conn.setRequestMethod("POST");
                    //conn.setDoInput(true);
                    conn.setDoOutput(true);

                    Log.d("lol","waiting");
                    //OutputStream os = conn.getOutputStream();
                    //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    //writer.write(getPostDataString(postDataParams));

                    //writer.flush();
                    //writer.close();
                    //os.close();
                    int responseCode=conn.getResponseCode();
                    Log.d("lol", Integer.toString(responseCode));
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        //response = br.readLine();
                        StringBuilder everything = new StringBuilder();
                        String line;
                        while( (line = br.readLine()) != null) {
                            everything.append(line);
                        }
                        response = everything.toString();

                    }
                    else {
                        response="Error";
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

                return response;



            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String name = c.getString(TAG_NAME);
                String location = c.getString(TAG_ADD);

                HashMap<String,String> persons = new HashMap<>();

                persons.put(TAG_ID,id);
                persons.put(TAG_NAME,name);
                persons.put(TAG_ADD,location);

                personList.add(persons);
            }
            ListAdapter adapter = new SimpleAdapter(
                    this,
                    personList,
                    R.layout.list_item,
                    new String[]{TAG_ID,TAG_NAME,TAG_ADD},
                    new int[]{R.id.id, R.id.name, R.id.location}
            );

            list.setAdapter(adapter);
            list.setTextFilterEnabled(true);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected =((TextView)view.findViewById(R.id.location)).getText().toString();
                    Toast.makeText(getApplicationContext(),selected,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListofEvents.this,ActivityInformation.class);
                    ListofEvents.this.startActivity(intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
