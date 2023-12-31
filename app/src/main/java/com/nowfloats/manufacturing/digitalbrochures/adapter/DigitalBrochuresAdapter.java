package com.nowfloats.manufacturing.digitalbrochures.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.manufacturing.API.model.GetBrochures.Data;
import com.nowfloats.manufacturing.digitalbrochures.Interfaces.DigitalBrochuresListener;
import com.nowfloats.manufacturing.webview.WebViewActivity;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DigitalBrochuresAdapter extends RecyclerView.Adapter<DigitalBrochuresAdapter.ViewHolder> {

  Context context;
  private List<Data> itemList;
  private DigitalBrochuresListener listener;
  private int menuPosition = -1;
  private boolean menuStatus = false;

  public DigitalBrochuresAdapter(List<Data> itemList, DigitalBrochuresListener listener) {
    this.itemList = itemList;
    this.listener = listener;
  }


  @NotNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_digital_brochures, null);
    context = v.getContext();
    return new ViewHolder(v);
  }

  public void menuOption(int pos, boolean status) {
    menuPosition = pos;
    menuStatus = status;
  }

  public void updateList(List<Data> list) {
    itemList = list;
    notifyDataSetChanged();
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Data data = itemList.get(position);
    holder.menuOptionLayout.setVisibility(View.GONE);
    if (menuPosition == position) {
      if (menuStatus) {
        holder.menuOptionLayout.setVisibility(View.VISIBLE);
      } else {
        holder.menuOptionLayout.setVisibility(View.GONE);
      }
    }
    holder.mainLayout.setOnClickListener(v -> listener.itemMenuOptionStatus(position, false));

    holder.menuButton.setOnClickListener(v -> {
      if (holder.menuOptionLayout.getVisibility() == View.GONE) {
        listener.itemMenuOptionStatus(position, true);
//                    holder.menuOptionLayout.setVisibility(View.VISIBLE);
      } else {
        listener.itemMenuOptionStatus(position, false);
//                    holder.menuOptionLayout.setVisibility(View.GONE);
      }
    });

    holder.title.setText(data.getTitle());
    if (data.getUploadpdf() != null) {
      holder.description.setText(data.getUploadpdf().getDescription());
      holder.documentURL.setText(data.getUploadpdf().getUrl());
    } else{
      holder.description.setText("");
      holder.documentURL.setText("");
    }

    holder.documentURL.setOnClickListener(v -> {
      if (!data.getUploadpdf().getUrl().isEmpty()) {
//                    String googleDocs = "https://docs.google.com/viewer?url="+itemList.get(position).getUploadpdf().getUrl();
//                    Intent intent = new Intent(context, WebViewActivity.class);
//                    intent.putExtra("url", googleDocs );
//                    context.startActivity(intent);

        if (data.getUploadpdf() != null) {
          String url =data.getUploadpdf().getUrl();
          if (!url.startsWith("https://") && !url.startsWith("http://")){
            url = "http://" + url;
          }
          Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
          context.startActivity(browserIntent);
        }
      } else {
        Toast.makeText(context, "Facebook URL is Empty!!", Toast.LENGTH_LONG).show();
      }
    });

    holder.editButton.setOnClickListener(v -> listener.editOptionClicked(itemList.get(position)));

    holder.deleteButton.setOnClickListener(v -> listener.deleteOptionClicked(itemList.get(position)));

    holder.dummyView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

  }

  @Override
  public int getItemCount() {
    return itemList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    ImageView menuButton;
    LinearLayout menuOptionLayout;
    ConstraintLayout mainLayout;
    TextView title, description, documentURL, editButton, deleteButton;
    View dummyView1;

    public ViewHolder(View itemView) {
      super(itemView);

      menuButton = (ImageView) itemView.findViewById(R.id.single_item_menu_button);
      menuOptionLayout = (LinearLayout) itemView.findViewById(R.id.menu_options);
      mainLayout = (ConstraintLayout) itemView.findViewById(R.id.main_layout);

      title = itemView.findViewById(R.id.title);
      description = itemView.findViewById(R.id.description);
      documentURL = itemView.findViewById(R.id.document_url);

      editButton = itemView.findViewById(R.id.edit_button);
      deleteButton = itemView.findViewById(R.id.delete_button);

      dummyView1 = itemView.findViewById(R.id.dummy_view1);
    }
  }
}
