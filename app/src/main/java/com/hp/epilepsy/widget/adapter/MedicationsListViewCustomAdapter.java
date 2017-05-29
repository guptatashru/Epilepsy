package com.hp.epilepsy.widget.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.model.NewMedication;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mahmoumo on 12/2/2015.
 */
public class MedicationsListViewCustomAdapter extends BaseAdapter {
    public Activity context;
    public LayoutInflater inflater;
    String[] MedicationNames;
    String[] StartDate;
    String[] Image1Path;
    String[] Image2Path;
    int[] MedicationIDS;
    int where;


    public MedicationsListViewCustomAdapter(Activity context, String[] Names, String[] Date, String[] Image1, String[] Image2, int[] ID,int where) {
        super();
        this.context = context;
        this.MedicationNames = Names;
        this.StartDate = Date;
        this.Image1Path = Image1;
        this.Image2Path = Image2;
        this.MedicationIDS = ID;
        this.where=where;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return MedicationNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        try {
            if(convertView==null)
            {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.medication_list_row_layout, null);
                holder.medicine_image1 = (ImageView) convertView.findViewById(R.id.medicine_image1);
                holder.medicine_image2 = (ImageView) convertView.findViewById(R.id.medicine_image2);
                holder.mediceneName = (TextView) convertView.findViewById(R.id.medicine_name);
                holder.MedicineStartDate = (TextView) convertView.findViewById(R.id.start_date);
                convertView.setTag(holder);
            }
            else
                holder=(ViewHolder)convertView.getTag();
            holder.mediceneName.setText(MedicationNames[position]);
            if (where==1)
                        holder.MedicineStartDate.setText("Ex Date: "+StartDate[position]);
            else
                holder.MedicineStartDate.setText("Start Date: "+StartDate[position]);

            if (Image1Path[position] != null) {
                File imgFile1 = new File(Image1Path[position]);
                Bitmap myBitmap1 = PrepareImage(imgFile1.getAbsolutePath());
                holder.medicine_image1.setImageBitmap(myBitmap1);
            }
            if (Image2Path[position] != null) {
                File imgFile2 = new File(Image2Path[position]);
                Bitmap myBitmap2 = PrepareImage(imgFile2.getAbsolutePath());
                holder.medicine_image2.setImageBitmap(myBitmap2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public Bitmap PrepareImage(String Path) {

        Bitmap bm = null;
        try {
            bm = null;
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(Path, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 220, 220);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(Path, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static class ViewHolder
    {
        ImageView medicine_image1;
        ImageView medicine_image2;
        TextView mediceneName;
        TextView MedicineStartDate;
    }
}
