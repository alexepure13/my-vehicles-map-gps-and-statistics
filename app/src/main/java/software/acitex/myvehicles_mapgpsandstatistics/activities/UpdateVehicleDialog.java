package software.acitex.myvehicles_mapgpsandstatistics.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;

import software.acitex.myvehicles_mapgpsandstatistics.MyDatabaseHelper;
import software.acitex.myvehicles_mapgpsandstatistics.R;
import software.acitex.myvehicles_mapgpsandstatistics.Tools.VehicleItem;

public class UpdateVehicleDialog extends AppCompatDialogFragment {
    //Spinner Mode
    private ArrayList<VehicleItem> vehicleItems;
    private VehicleAdapter vehicleAdapter;
    String tipVehicul;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_vehicul, null);

        EditText numeVehicul;
        Spinner spinner;
        MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());

        numeVehicul = view.findViewById(R.id.numeVehicul);
        spinner = view.findViewById(R.id.spinner_vehicle);



        initSpinner();


        vehicleAdapter = new VehicleAdapter(getContext(), vehicleItems);
        spinner.setAdapter(vehicleAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VehicleItem clickedItem = (VehicleItem) parent.getItemAtPosition(position);
                tipVehicul = clickedItem.getVehicleName();
                Toast.makeText(getContext(), tipVehicul + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(view)
                .setTitle("Update vehicle")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int a = 0;
                        myDB.addVehicul(numeVehicul.getText().toString().trim(), tipVehicul.toString().trim(),0.0, "NO DATA");
                    }
                });


        return builder.create();

    }

    private void initSpinner(){
        vehicleItems = new ArrayList<>();
        vehicleItems.add(new VehicleItem("CAR", R.drawable.car_96));
        vehicleItems.add(new VehicleItem("BICYCLE", R.drawable.bicycle_100));
        vehicleItems.add(new VehicleItem("MOTORCYCLE", R.drawable.motorcycle_100));
        vehicleItems.add(new VehicleItem("OTHER", R.drawable.other_vehicle));
    }
}


