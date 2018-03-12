package nowfloats.nfkeyboard.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NowFloats on 27-02-2018.
 */

public class Updates {
    @SerializedName("floats")
    @Expose
    private List<Float> floats = null;
    @SerializedName("moreFloatsAvailable")
    @Expose
    private Boolean moreFloatsAvailable;
    @SerializedName("totalCount")
    @Expose
    private Integer totalCount;

    public List<Float> getFloats() {
        return floats;
    }

    public void setFloats(List<Float> floats) {
        this.floats = floats;
    }

    public Boolean getMoreFloatsAvailable() {
        return moreFloatsAvailable;
    }

    public void setMoreFloatsAvailable(Boolean moreFloatsAvailable) {
        this.moreFloatsAvailable = moreFloatsAvailable;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
