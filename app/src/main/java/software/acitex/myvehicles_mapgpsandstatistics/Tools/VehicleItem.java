package software.acitex.myvehicles_mapgpsandstatistics.Tools;

public class VehicleItem {
    private String vehicleName;
    private int vehicleImage;

    public VehicleItem(String vehicleName, int vehicleImage) {
        this.vehicleName = vehicleName;
        this.vehicleImage = vehicleImage;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public int getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(int vehicleImage) {
        this.vehicleImage = vehicleImage;
    }
}
