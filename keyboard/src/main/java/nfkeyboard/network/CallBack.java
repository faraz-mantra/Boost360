package nfkeyboard.network;

/**
 * Created by NowFloats on 27-02-2018.
 */

public interface CallBack<T> {
    public void onSuccess(T data);

    public void onError(Throwable t);
}
