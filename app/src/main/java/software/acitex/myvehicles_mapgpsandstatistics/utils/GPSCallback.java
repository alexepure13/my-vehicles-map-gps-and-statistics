package software.acitex.myvehicles_mapgpsandstatistics.utils;

import android.location.Location;

public interface GPSCallback
{
    public abstract void onGPSUpdate(Location location);
}