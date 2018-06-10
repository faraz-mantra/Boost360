package nfkeyboard.interface_contracts;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by NowFloats on 28-02-2018.
 */

public interface UrlToBitmapInterface {
    Context getContext();

    void onResourcesReady(Bitmap bitmap, String text, String imageId);
}
