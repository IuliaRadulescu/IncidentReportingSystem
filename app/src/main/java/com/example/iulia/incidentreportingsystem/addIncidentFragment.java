package com.example.iulia.incidentreportingsystem;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;

/**
 * Created by Iulia on 11/5/2016.
 */

public class addIncidentFragment extends Fragment implements View.OnClickListener {

    private String description;
    private String email;
    private String incidentType;
    private File incidentImage;
    private static String filePath;

    private EditText emailHelper;
    private Spinner incidentTypeHelper;
    private EditText descriptionHelper;

    //pentru coordonate GPS
    public GetLocation getLocationService; //serviciul care preia locatia
    boolean mBound = false;
    public Location location;
    public double latitude;
    public double longitude;

    private final static int REQUEST_CODE = 10;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            GetLocation.LocationBinder binder = (GetLocation.LocationBinder) service;
            getLocationService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View FinalView = inflater.inflate(R.layout.addincident, container, false);

        Button submit = (Button) FinalView.findViewById(R.id.submit);
        Button imagePicker = (Button) FinalView.findViewById(R.id.imagePicker);

        emailHelper = (EditText)FinalView.findViewById(R.id.email);

        descriptionHelper = (EditText)FinalView.findViewById(R.id.description);

        incidentTypeHelper = (Spinner) FinalView.findViewById(R.id.incident_type);


        //preiau locatia curenta de la serviciul GetLocation

        Intent getCoords = new Intent(FinalView.getContext(), GetLocation.class);
        getActivity().bindService(getCoords, mConnection, Context.BIND_AUTO_CREATE);

        //actiuni pe butoane
        submit.setOnClickListener(this);
        imagePicker.setOnClickListener(this);


        return FinalView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.submit: //daca am apasat submit introducem valorile in baza de date

                //preiau coordonatele de la serviciu
                Location coords = getLocationService.getGPSCoords();
                System.out.println("Latitudine: "+coords.getLatitude()+" Longitudine: "+coords.getLongitude());
                String lat = String.valueOf(coords.getLatitude());
                String lng = String.valueOf(coords.getLongitude());
                //preiau celelalte valori
                email = emailHelper.getText().toString();
                System.out.println("email "+email);
                description = descriptionHelper.getText().toString();
                System.out.println("description "+description);

                incidentType = incidentTypeHelper.getSelectedItem().toString();

                //trimit la baza de date
                SendToDb db = new SendToDb();
                db.execute(email, incidentType, description, lat, lng);
                break;
            case R.id.imagePicker: //daca am apasat upload imagine, pornin Galeria de Imagini folosind Intenturi
                Intent pickFile = new Intent(Intent.ACTION_PICK);
                pickFile.setType("image/*");
                startActivityForResult(pickFile, REQUEST_CODE);


        }

    }

    private String getPathFromUri(Uri uri) { //sa ma mai uit pe functia asta
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this.getContext(), uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK) {

                    //operatiile pentru fisier

                    Uri selectedFile = data.getData();
                    filePath = getPathFromUri(selectedFile);
                    System.out.println("Calea catre fisier: "+filePath);
                    incidentImage = new File(filePath);


                }

        }
    }

    private class SendToDb extends AsyncTask<String, Void, String> {

        private PrintWriter printWriter;
        private OutputStream outputStream;
        private FileInputStream fileInputStream;
        @Override
        protected String doInBackground(String[] params) {
            // do above Server call here
            try{

                //utile pentru transmiterea datelor (imagine incidentImage + campuri)
                String attachedFileName = " ";
                if(incidentImage.getName() != null)
                {
                    attachedFileName = incidentImage.getName();
                }

                String fieldFileName = "incidentImage";
                //utile pentru formarea requestului la server
                String clrf = "\r\n";
                String twoHyphens = "--";
                String boundary =  "*****";
                //utile pentru primirea raspunsului de la server
                StringBuffer sb = new StringBuffer();
                String result="";

                System.out.println("Into Submit");

                URL url = new URL("http://192.168.0.15:80/incident_reporting_system/web_service.php");

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

                /*TRIMITERE FISIER - DACA EXISTA*/

                if(addIncidentFragment.filePath!=null) {
                    //setez content type-ul adecvat
                    printWriter.append(twoHyphens + boundary).append(clrf);
                    printWriter.append("Content-Disposition: form-data; name=\"imagineIncident\";filename=\"" + attachedFileName + "\"").append(clrf);
                    printWriter.append("Content-Type: " + URLConnection.guessContentTypeFromName(attachedFileName)).append(clrf);
                    printWriter.append("Content-Transfer-Encoding: binary").append(clrf);
                    printWriter.append(clrf);
                    printWriter.flush();

                    //prelucrari fisier pentru a-l putea trimite

                    fileInputStream = new FileInputStream(addIncidentFragment.filePath);

                    byte[] buffer = new byte[4096];
                    int bRead = -1;
                    while ((bRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bRead);
                    }
                    outputStream.flush();
                    fileInputStream.close();

                    printWriter.append(clrf);
                    printWriter.flush();
                }
                /*TRIMITERE CEILALTI PARAMETRI: email, incidentType, description, lat, lng*/
                //setez content type-ul adecvat si trimit valoarea

                //EMAIL
                printWriter.append(twoHyphens+boundary).append(clrf);
                printWriter.append("Content-Disposition: form-data; name=\"email\"").append(clrf);
                printWriter.append("Content-Type: text/plain; charset=UTF-8").append(clrf);
                printWriter.append(clrf);
                printWriter.append(params[0]).append(clrf);
                printWriter.flush();

                //TIP INCIDENT
                printWriter.append(twoHyphens+boundary).append(clrf);
                printWriter.append("Content-Disposition: form-data; name=\"incidentType\"").append(clrf);
                printWriter.append("Content-Type: text/plain; charset=UTF-8").append(clrf);
                printWriter.append(clrf);
                printWriter.append(params[1]).append(clrf);
                printWriter.flush();

                //DESCRIERE
                printWriter.append(twoHyphens+boundary).append(clrf);
                printWriter.append("Content-Disposition: form-data; name=\"description\"").append(clrf);
                printWriter.append("Content-Type: text/plain; charset=UTF-8").append(clrf);
                printWriter.append(clrf);
                printWriter.append(params[2]).append(clrf);
                printWriter.flush();

                //LAT
                printWriter.append(twoHyphens+boundary).append(clrf);
                printWriter.append("Content-Disposition: form-data; name=\"lat\"").append(clrf);
                printWriter.append("Content-Type: text/plain; charset=UTF-8").append(clrf);
                printWriter.append(clrf);
                printWriter.append(params[3]).append(clrf);
                printWriter.flush();

                //LNG
                printWriter.append(twoHyphens+boundary).append(clrf);
                printWriter.append("Content-Disposition: form-data; name=\"lng\"").append(clrf);
                printWriter.append("Content-Type: text/plain; charset=UTF-8").append(clrf);
                printWriter.append(clrf);
                printWriter.append(params[4]).append(clrf);
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

            }catch(Exception e){
                e.printStackTrace();
            }

            return "ok!";
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
        }
    }

}
