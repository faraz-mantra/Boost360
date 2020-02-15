package nfkeyboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.separ.neural.inputmethod.indic.R;
import nfkeyboard.interface_contracts.ItemClickListener;
import nfkeyboard.models.AllSuggestionModel;

/**
 * Created by Shimona on 01-06-2018.
 */

public class ImageShareAdapter extends BaseAdapter<AllSuggestionModel> {

    private final ItemClickListener listener;

    ViewGroup parent;

    private Context mContext;

    ImageShareAdapter(Context context, ItemClickListener listener) {
        super(context, listener);
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        this.parent = parent;
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_photos, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, AllSuggestionModel suggestion) {
        if (holder instanceof ImageHolder) {
            ImageHolder myHolder = (ImageHolder) holder;
            myHolder.setImageView(suggestion);
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView imageView, checkedIv, addIv;
        private AllSuggestionModel model;
        private View overlayView, checkedBgView;
        private boolean selected;

        ImageHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkedIv = itemView.findViewById(R.id.iv_checked);
            overlayView = itemView.findViewById(R.id.overlay);
            checkedBgView = itemView.findViewById(R.id.view_background_checked);
            addIv = itemView.findViewById(R.id.iv_add_photos);
            addIv.setVisibility(View.GONE);
            checkedIv.setVisibility(View.GONE);
            overlayView.setVisibility(View.GONE);
            checkedBgView.setVisibility(View.GONE);
            selected = false;
        }

        void setImageView(AllSuggestionModel suggestionModel) {
            model = suggestionModel;
            if (model.getImageUri() != null) {
                /*Glide.with(mContext)
                        .load(suggestionModel.getImageUri())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder_ic_image_padded)
                                .fitCenter())
                        .into(imageView);*/


                Picasso.get().load(suggestionModel.getImageUri())
                        .placeholder(R.drawable.placeholder_ic_image_padded)
                        .fit().centerCrop()
                        .into(imageView);

                addIv.setVisibility(View.GONE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected = !selected;
                        model.setSelected(selected);
                        if (listener.onClick(model, selected)) {
                            if (selected) {
                                checkedIv.setVisibility(View.VISIBLE);
                                overlayView.setVisibility(View.VISIBLE);
                                checkedBgView.setVisibility(View.VISIBLE);
                            } else {
                                checkedIv.setVisibility(View.GONE);
                                overlayView.setVisibility(View.GONE);
                                checkedBgView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
                selected = model.getSelected();
                if (selected) {
                    checkedIv.setVisibility(View.VISIBLE);
                    overlayView.setVisibility(View.VISIBLE);
                    checkedBgView.setVisibility(View.VISIBLE);
                } else {
                    checkedIv.setVisibility(View.GONE);
                    overlayView.setVisibility(View.GONE);
                    checkedBgView.setVisibility(View.GONE);
                }
            } else {
                if (model.getSelected()) {
                    imageView.setImageBitmap(null);
                    addIv.setVisibility(View.VISIBLE);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("nowfloats://" + mContext.getApplicationContext().getPackageName() + ".keyboard.home/photos"));
                            intent.putExtra("from", "notification");
                            intent.putExtra("url", "imagegallery");
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                                mContext.startActivity(intent);
                            }
                        }
                    });
                } else {
                    addIv.setVisibility(View.GONE);
                }
            }
        }
    }

}
