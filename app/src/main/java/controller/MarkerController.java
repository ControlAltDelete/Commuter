package controller;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.machineproject.commuter.R;

import model.Terminal;

/**
 * Created by aljon on 24/11/2016.
 */

public class MarkerController extends AsyncTask<Void, Void, Void> {

    private GoogleMap mMap;
    private Context getBaseContext;
    private Context parent;

    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private View inflatedView;
    private Spinner spinner;

    public MarkerController(GoogleMap googleMap, Context base, Context parent)
    {
        mMap = googleMap;
        getBaseContext = base;
        this.parent = parent;
    }
    @Override
    protected Void doInBackground(Void... params) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("termLocations");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try
                {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final Terminal temp = (Terminal) postSnapshot.getValue(Terminal.class);
                        Marker mark = mMap.addMarker(new MarkerOptions().position(new LatLng(temp.getLatitude(), temp.getLongitude())).title(temp.getTerminalName()));
                        Log.d("MARKER", temp.toString());
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                initializeVarForDialog(getBaseContext, parent, temp.getStatus());
                                Log.d("STATUS", temp.getStatus() + "");
                                builder.setView(inflatedView)
                                        .setPositiveButton("Report", new DialogInterface.OnClickListener(){

                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                try
                                                {

                                                }

                                                catch(Exception ex)
                                                {

                                                }}})
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {

                                            }
                                        })
                                        .setTitle(marker.getTitle());

                                builder.show();
                                return false;
                            }
                        });
                    }
                }

                catch(Exception ex)
                {
                    Toast.makeText(getBaseContext, ex + "", Toast.LENGTH_LONG).show();
                    Log.v("LOAD", ex + "");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FB", "Failed to read value.", error.toException());
            }
        });
        return null;
    }

    private void initializeVarForDialog(Context context, Context parent, int status) {
        builder = new AlertDialog.Builder(parent);
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        inflatedView = inflater.inflate(R.layout.view_terminal_dialog, null);
        TextView statusText = (TextView) inflatedView.findViewById(R.id.status);

        switch (status)
        {
            case 0: statusText.setText("No reports yet");
                    break;
            case 1: statusText.setText("Light");
                    statusText.setTextColor(Color.GREEN);
                    break;
            case 2: statusText.setText("Medium");
                    statusText.setTextColor(Color.YELLOW);
                    break;
            case 3: statusText.setText("Heavy");
                    statusText.setTextColor(Color.RED);
                    break;
            default: statusText.setText("Error");
                     break;
        }
    }
}
