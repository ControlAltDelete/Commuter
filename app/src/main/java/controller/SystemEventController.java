package controller;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
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

import java.util.Timer;
import java.util.TimerTask;

import model.Terminal;

/**
 * Created by aljon on 23/11/2016.
 */

public class SystemEventController {

    private static SystemEventController instance = null;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private View inflatedView;
    private Spinner spinner;
    private String[] items;
    private ArrayAdapter<String> adapter2;

    private SystemEventController() {

    }

    public static SystemEventController getInstance() {
        if (instance == null) {
            instance = new SystemEventController();
        }

        return instance;
    }

    public void onMainMapClick(final GoogleMap mMap, final Context context, final Context parent) {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                try{

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                    initializeVarForDialog(context, parent);
                    spinner.setAdapter(adapter2);
                    builder.setView(inflatedView)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    try
                                    {
                                        EditText terminalN = (EditText) inflatedView.findViewById(R.id.terminal_name);
                                        Terminal temp = new Terminal(terminalN.getText().toString(), latLng.latitude, latLng.longitude, spinner.getSelectedItemPosition());

                                        mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude))).setTitle(terminalN.getText().toString());
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("termLocations");

                                        myRef.child(temp.getTerminalName()).setValue(temp);
                                    }

                                    catch(Exception ex)
                                    {

                                    }
                                }
                            })
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {

                                }
                            }).setTitle("Add terminal");

                    builder.show();
                }

                catch(Exception ex)
                {
                    Log.d("User err", ex.toString());
                }
            }
        });
    }

    public void loadAllMarkers(final GoogleMap mMap, final Context context, final Context parent) {


        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new MarkerController(mMap, context, parent).execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 50000);
    }

    public void refreshMarkers(final GoogleMap mMap, final Context context, final Context parent) {
        new MarkerController(mMap, context, parent);
    }


    private void initializeVarForDialog(Context context, Context parent) {
        items = new String[]{"Transit", "Bus", "Jeep", "UV"};
        builder = new AlertDialog.Builder(parent);
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        inflatedView = inflater.inflate(R.layout.new_terminal_dialog, null);
        spinner = (Spinner) inflatedView.findViewById(R.id.dropdownMenu);

        adapter2 = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_dropdown_item, items);

    }

    private void addTerminalToDatabase() {

    }
}
