package software.acitex.myvehicles_mapgpsandstatistics.Tools;

public class ModeItem {
    private String modeName;
    private int modeImage;

    public ModeItem(String modeName, int flagImage) {
        this.modeName = modeName;
        this.modeImage = flagImage;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public int getModeImage() {
        return modeImage;
    }

    public void setModeImage(int flagImage) {
        this.modeImage = flagImage;
    }
}
