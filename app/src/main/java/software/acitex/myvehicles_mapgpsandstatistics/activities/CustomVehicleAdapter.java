package software.acitex.myvehicles_mapgpsandstatistics.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import software.acitex.myvehicles_mapgpsandstatistics.MyDatabaseHelper;
import software.acitex.myvehicles_mapgpsandstatistics.R;
import software.acitex.myvehicles_mapgpsandstatistics.Tools.VehicleItem;

public class CustomVehicleAdapter extends RecyclerView.Adapter<CustomVehicleAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList _id, vehicle_name, vehicle_type, vehicle_km, vehicle_last_data;

    int position;
    private ArrayList<VehicleItem> vehicleItems2;
    String tipVehicul;
    Spinner spinner2;
    EditText numeVehicul;
    TextView vehicleID;
    VehicleAdapter vehicleAdapter2;
    MyDatabaseHelper myDb;


    public CustomVehicleAdapter(Activity activity, Context context, ArrayList _id, ArrayList vehicle_name, ArrayList vehicle_type, ArrayList vehicle_km, ArrayList vehicle_last_data){
    this.context = context;
    this._id = _id;
    this.vehicle_name = vehicle_name;
    this.vehicle_type = vehicle_type;
    this.vehicle_km = vehicle_km;
    this.vehicle_last_data = vehicle_last_data;
    this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_vehicle_row, parent,false);

        myDb = new MyDatabaseHelper(context);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
      this.position = position;
      holder._idTv.setText(String.valueOf(_id.get(position)));
      holder.vehicle_nameTv.setText(String.valueOf(vehicle_name.get(position)));
      holder.vehicle_type_Tv.setText(String.valueOf(vehicle_type.get(position)));
      holder.vehicle_km_Tv.setText(String.valueOf(vehicle_km.get(position)));
      holder.vehicle_last_data_Tv.setText(String.valueOf(vehicle_last_data.get(position)));
      if(String.valueOf(vehicle_type.get(position)).equals("MOTORCYCLE")){
          holder.vehicleImage.setBackgroundResource(R.drawable.motorcycle_100);
      }
      else if(String.valueOf(vehicle_type.get(position)).equals("CAR")){
          holder.vehicleImage.setBackgroundResource(R.drawable.car_96);
      }
      else if(String.valueOf(vehicle_type.get(position)).equals("BICYCLE")){
          holder.vehicleImage.setBackgroundResource(R.drawable.bicycle_100);
      }
      else
          holder.vehicleImage.setBackgroundResource(R.drawable.other_vehicle);

      holder.myLayoutVehicle.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              // custom dialog
              final Dialog dialog = new Dialog(context);
              dialog.setContentView(R.layout.dialog_update_vehicul);
              dialog.setTitle("Title");
              dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


// set the custom dialog components - text, image and button
              TextView numeVehiculUpdate = (TextView) dialog.findViewById(R.id.numeVehiculUpdate);
              numeVehiculUpdate.setText(String.valueOf(vehicle_name.get(position)));
              TextView vehiculID = (TextView) dialog.findViewById(R.id.vehicleID);
              TextView tipVehicul2 = (TextView) dialog.findViewById(R.id.vehicleType);


              Button buttonCancelUpdate = (Button) dialog.findViewById(R.id.btnCancelUpdate);
              Button buttonUpdate = (Button) dialog.findViewById(R.id.btnUpdateVehicle);

              spinner2 = (Spinner) dialog.findViewById(R.id.spinner_vehicle_update);
              initSpinner();

              ImageView deleteVehicle =  (ImageView) dialog.findViewById(R.id.deleteVehicle);


              vehicleAdapter2 = new VehicleAdapter(v.getContext(), vehicleItems2);
              spinner2.setAdapter(vehicleAdapter2);
              if(String.valueOf(vehicle_type.get(position)).equals("MOTORCYCLE")){
                  spinner2.setSelection(2);
              }
              else if(String.valueOf(vehicle_type.get(position)).equals("CAR")){
                  spinner2.setSelection(0);
              }
              else if(String.valueOf(vehicle_type.get(position)).equals("BICYCLE")){
                  spinner2.setSelection(1);
              }
              else if(String.valueOf(vehicle_type.get(position)).equals("OTHER")) {
                  spinner2.setSelection(3);
              }

              spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      VehicleItem clickedItem = (VehicleItem) parent.getItemAtPosition(position);
                      tipVehicul = clickedItem.getVehicleName();
                      Toast.makeText(context, tipVehicul + "is selected", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });


              buttonCancelUpdate.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      dialog.dismiss();
                  }
              });

              buttonUpdate.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int a = 0;

                      if(vehicle_km.get(position)==String.valueOf(a)) {
                          myDb.UpdateData(String.valueOf(_id.get(position)).trim(), numeVehiculUpdate.getText().toString().trim(), tipVehicul, 0.0, String.valueOf(vehicle_last_data.get(position)).trim());
                      }
                      else{
                          myDb.UpdateData(String.valueOf(_id.get(position)).trim(), numeVehiculUpdate.getText().toString().trim(), tipVehicul, Double.parseDouble(vehicle_km.get(position).toString().trim()), String.valueOf(vehicle_last_data.get(position)).trim());
                      }
                      NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
                      navController.navigateUp();
                      navController.navigate(R.id.navigation_vehicule);
                      dialog.dismiss();
                  }
              });

              spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      VehicleItem clickedItem = (VehicleItem) parent.getItemAtPosition(position);
                      tipVehicul = clickedItem.getVehicleName();
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });

              deleteVehicle.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      final Dialog dialogDelete = new Dialog(context);
                      dialogDelete.setContentView(R.layout.dialog_delete_vehicul);
                      Button btnCancelDelete = (Button) dialogDelete.findViewById(R.id.btnCancelDelete);
                      Button btnAcceptDelete = (Button) dialogDelete.findViewById(R.id.btnAcceptDelete);

                      dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                      btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              dialogDelete.dismiss();
                          }
                      });

                      btnAcceptDelete.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                             myDb.deleteOneRow(String.valueOf(_id.get(position)).trim());
                              NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
                              navController.navigateUp();
                              navController.navigate(R.id.navigation_vehicule);
                             dialogDelete.dismiss();
                          }
                      });


                      dialogDelete.show();
                      dialog.dismiss();
                  }
              });


              dialog.show();
          }
      });

    }

    @Override
    public int getItemCount() {
        return _id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView  _idTv, vehicle_nameTv, vehicle_type_Tv, vehicle_km_Tv, vehicle_last_data_Tv;
        ImageView vehicleImage;
        LinearLayout myLayoutVehicle;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            _idTv = itemView.findViewById(R.id.vehicleID);
            vehicle_nameTv = itemView.findViewById(R.id.vehicleName);
            vehicle_type_Tv = itemView.findViewById(R.id.vehicleType);
            vehicle_km_Tv = itemView.findViewById(R.id.vehicleKM);
            vehicle_last_data_Tv = itemView.findViewById(R.id.vehicleLastTrack);
            vehicleImage = itemView.findViewById(R.id.vehicleImage);
            myLayoutVehicle = itemView.findViewById(R.id.myLayoutVehicle);
        }
    }



    private void initSpinner(){
        vehicleItems2 = new ArrayList<>();
        vehicleItems2.add(new VehicleItem("CAR", R.drawable.car_96));
        vehicleItems2.add(new VehicleItem("BICYCLE", R.drawable.bicycle_100));
        vehicleItems2.add(new VehicleItem("MOTORCYCLE", R.drawable.motorcycle_100));
        vehicleItems2.add(new VehicleItem("OTHER", R.drawable.other_vehicle));
    }
}
