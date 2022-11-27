package software.acitex.myvehicles_mapgpsandstatistics.ui.vehicule;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import software.acitex.myvehicles_mapgpsandstatistics.MyDatabaseHelper;
import software.acitex.myvehicles_mapgpsandstatistics.R;
import software.acitex.myvehicles_mapgpsandstatistics.activities.AddVehicleDialog;
import software.acitex.myvehicles_mapgpsandstatistics.activities.CustomVehicleAdapter;

public class VehiculeFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton add_button;

//      cv.put(COLUMN_TITLE,numeVehicul);
//        cv.put(COLUMN_VEHICLE_TYPE,vehicleType);
//        cv.put(COLUMN_KM,vehicleKm);
//        cv.put(COLUMN_LAST_TRACK,vehicle_last_data);


    MyDatabaseHelper myDB;
    ArrayList<String>  _id, vehicle_name, vehicle_type, vehicle_km, vehicle_last_data;
    CustomVehicleAdapter customVehicleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vehicule, container, false);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerViewVehicule);
        add_button = view.findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddVehicleDialog addVehicleDialog = new AddVehicleDialog();
                addVehicleDialog.show(getActivity().getSupportFragmentManager(), "Adaugare vehicul");
            }
        });

        myDB = new MyDatabaseHelper(getActivity());
        _id = new ArrayList<>();
        vehicle_name = new ArrayList<>();
        vehicle_type = new ArrayList<>();
        vehicle_km = new ArrayList<>();
        vehicle_last_data = new ArrayList<>();
        storedDataInArrays();
        customVehicleAdapter = new CustomVehicleAdapter(getActivity(),getContext(),_id,vehicle_name,vehicle_type,vehicle_km,vehicle_last_data);
        recyclerView.setAdapter(customVehicleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    void storedDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(getActivity(), "No data.", Toast.LENGTH_SHORT).show();
        }
        else{
            while(cursor.moveToNext()){
                _id.add(cursor.getString(0));
                vehicle_name.add(cursor.getString(1));
                vehicle_type.add(cursor.getString(2));
                vehicle_km.add(cursor.getString(3));
                vehicle_last_data.add(cursor.getString(4));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater inflaterMenu = getActivity().getMenuInflater();
        inflaterMenu.inflate(R.menu.my_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.deleteAll){
            myDB.deleteAllData();
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigateUp();
            navController.navigate(R.id.navigation_vehicule);

        }
        return super.onOptionsItemSelected(item);
    }
}