package software.acitex.myvehicles_mapgpsandstatistics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Vehicule.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_vehicles";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "vehicle_name";
    private static final String COLUMN_KM = "vehicle_km";
    private static final String COLUMN_VEHICLE_TYPE = "vehicle_type";
    private static final String COLUMN_LAST_TRACK = "vehicle_last_data";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    String query =
            "CREATE TABLE " + TABLE_NAME +
            " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_VEHICLE_TYPE + " TEXT, " +
            COLUMN_KM + " DOUBLE, " +
            COLUMN_LAST_TRACK + " TEXT);";
   db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
     onCreate(db);
    }


   public void addVehicul(String numeVehicul, String vehicleType, Double vehicleKm, String vehicle_last_data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE,numeVehicul);
        cv.put(COLUMN_VEHICLE_TYPE,vehicleType);
        cv.put(COLUMN_KM,vehicleKm);
        cv.put(COLUMN_LAST_TRACK,vehicle_last_data);

       long result = db.insert(TABLE_NAME, null, cv);
       if(result == -1){
           Toast.makeText(context, "Failed!",Toast.LENGTH_SHORT).show();
       }
       else
           Toast.makeText(context, "Added succesfully!", Toast.LENGTH_SHORT).show();

    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void UpdateData(String _id, String numeVehicul, String vehicleType, Double vehicleKm, String vehicle_last_data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE,numeVehicul);
        cv.put(COLUMN_VEHICLE_TYPE,vehicleType);
        cv.put(COLUMN_KM,vehicleKm);
        cv.put(COLUMN_LAST_TRACK,vehicle_last_data);

        long result = db.update(TABLE_NAME,cv,"_id=?", new String[]{_id});
        if(result == -1){
            Toast.makeText(context,"Failed to update!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context,"Successfully updated!", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME,"_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context,"Failed to delete!",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Successfully deleted!",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
      /*  db.execSQL("DELETE FROM "+TABLE_NAME);*/

        db.delete("SQLITE_SEQUENCE","NAME=?",new String[]{TABLE_NAME});
        int affectedRows = db.delete(this.TABLE_NAME, null, null);
        return affectedRows > 1;
    }
}
