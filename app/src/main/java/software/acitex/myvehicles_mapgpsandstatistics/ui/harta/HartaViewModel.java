package software.acitex.myvehicles_mapgpsandstatistics.ui.harta;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HartaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HartaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}