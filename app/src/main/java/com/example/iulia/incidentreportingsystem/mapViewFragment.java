package com.example.iulia.incidentreportingsystem;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Iulia on 11/5/2016.
 */

public class mapViewFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap googleMap;
    private String locatiiJsonHelper;
    private JSONArray locatii;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View FinalView =  inflater.inflate(R.layout.mapview, container, false);
        mMapView = (MapView) FinalView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch(Exception e){
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return FinalView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //preiau coordonatele din baza de date in functie de locatia curenta a userului
        RetrieveLocations retrieveLocations = new RetrieveLocations(this.getContext(), googleMap);
        retrieveLocations.execute("65.9667", "-18.5333");

    }


    class RetrieveLocations extends AsyncTask<String, Integer, String>{
        private PrintWriter printWriter;
        private OutputStream outputStream;
        Context c;
        GoogleMap mapIn;
        public RetrieveLocations(Context context, GoogleMap googleMap){
            c = context;
            mapIn = googleMap;
        }

        @Override
        protected String doInBackground(String[] params) { //preiau locatiile din baza de date apropiate de lat si lng user curent

            //utile pentru trimiterea requestului
            String clrf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            StringBuffer sb = new StringBuffer();
            String result="";

            try{
                URL url = new URL("http://192.168.0.15:80/incident_reporting_system/web_service_retrieve.php");

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                //parametrii conexiune
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);

                conn.setUseCaches(false);//set true to enable Cache for the req
                conn.setDoOutput(true);//enable to write data to output stream

                outputStream = conn.getOutputStream();
                printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                //latitudine curenta user
                printWriter.append(twoHyphens+boundary).append(clrf);
                printWriter.append("Content-Disposition: form-data; name=\"lat\"").append(clrf);
                printWriter.append("Content-Type: text/plain; charset=UTF-8").append(clrf);
                printWriter.append(clrf);
                printWriter.append(params[0]).append(clrf);
                printWriter.flush();

                //longitudine curenta user
                printWriter.append(twoHyphens+boundary).append(clrf);
                printWriter.append("Content-Disposition: form-data; name=\"lng\"").append(clrf);
                printWriter.append("Content-Type: text/plain; charset=UTF-8").append(clrf);
                printWriter.append(clrf);
                printWriter.append(params[1]).append(clrf);
                printWriter.flush();

                /*FINALIZARE TRIMITERE DATE*/
                printWriter.append(clrf).flush();
                printWriter.append(twoHyphens+boundary+twoHyphens).append(clrf);
                printWriter.close();


                conn.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();

                System.out.println("Out: "+result);


            } catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(s);
            JSONObject locatiiJson;
            ArrayList<HashMap<String, String>> locatiiList = new ArrayList<HashMap<String, String>>();

            locatiiJsonHelper = result;
            try{
                locatiiJson = new JSONObject(locatiiJsonHelper);
                locatii = locatiiJson.getJSONArray("result");

                for(int i=0; i<locatii.length(); i++){

                    JSONObject o = locatii.getJSONObject(i);
                    String lat = o.getString("lat");
                    String lng = o.getString("lng");
                    String type = o.getString("type");
                    String description = o.getString("description");

                    HashMap<String, String> locatiiHash = new HashMap<String, String>();

                    locatiiHash.put("lat", lat);
                    locatiiHash.put("lng", lng);
                    locatiiHash.put("type", type);
                    locatiiHash.put("description", description);

                    locatiiList.add(locatiiHash);
                }

                for(int i=0; i<locatiiList.size(); i++){
                    //adaugam markerii
                    LatLng latLng = new LatLng(Double.parseDouble(locatiiList.get(i).get("lat")), Double.parseDouble(locatiiList.get(i).get("lng")));
                    System.out.println("lat= "+Double.parseDouble(locatiiList.get(i).get("lat"))+" lng= "+Double.parseDouble(locatiiList.get(i).get("lng")));
                    mapIn.addMarker(new MarkerOptions().position(latLng).title(locatiiList.get(i).get("description")));
                }

                mapIn.addMarker(new MarkerOptions().position(new LatLng(65.9667, -18.5333)).title("here"));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(65.9667, -18.5333)).zoom(5).build();
                mapIn.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            }catch(Exception e){
                e.printStackTrace();
            }


        }
    }
}
