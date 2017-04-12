package nowfloats.bubblebutton.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 12-04-2017.
 */

public class SmsCallAdapter extends RecyclerView.Adapter<SmsCallAdapter.MyHolder> {

    /*SmsCallAdapter(Context context, ArrayList<> list){

    }*/
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public MyHolder(View itemView) {
            super(itemView);
        }
    }
}
