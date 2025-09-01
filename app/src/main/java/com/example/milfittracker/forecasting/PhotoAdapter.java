package com.example.milfittracker.forecasting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final List<Object> items = new ArrayList<>();

    public void setData(Map<String, List<File>> grouped) {
        items.clear();
        for (Map.Entry<String, List<File>> entry : grouped.entrySet()) {
            items.add(entry.getKey());
            items.addAll(entry.getValue());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            TextView tv = new TextView(parent.getContext());
            tv.setTextSize(18f);
            tv.setPadding(16,16,16,8);
            return new HeaderVH(tv);
        } else {
            ImageView iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(8,8,8,8);
            return new PhotoVH(iv);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).tv.setText((String) items.get(position));
        } else {
            File file = (File) items.get(position);
            Bitmap bmp = decodeAndRotate(file.getAbsolutePath());
            ((PhotoVH) holder).iv.setImageBitmap(bmp);
        }
    }

    private Bitmap decodeAndRotate(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }

            return Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderVH extends RecyclerView.ViewHolder {
        TextView tv;
        HeaderVH(@NonNull TextView itemView) { super(itemView); tv = itemView; }
    }

    static class PhotoVH extends RecyclerView.ViewHolder {
        ImageView iv;
        PhotoVH(@NonNull ImageView itemView) { super(itemView); iv = itemView; }
    }
}
