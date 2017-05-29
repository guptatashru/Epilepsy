package com.hp.epilepsy.widget.customComponents;

import android.content.Context;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.hp.epilepsy.R;
import com.hp.epilepsy.widget.model.NewMedication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by elmalah on 4/28/2016.
 */
public class MyMarkerView extends MarkerView {
    private TextView line_medication1, line_medication2, line_medication3, line_medication4,
            line_medication5, line_medication6;
    GestureDetector gestureDetector = null;
    HorizontalScrollView horizontalScrollView;
    ArrayList<TextView> layouts;
    int currPosition;
    HashMap<Integer, HashMap<Integer, ArrayList<com.hp.epilepsy.widget.model.NewMedication>>> ParentMedicationsMap;

    public MyMarkerView(Context context, int layoutResource, HashMap<Integer, HashMap<Integer, ArrayList<NewMedication>>> ParentMedicationsMap) {
        super(context, layoutResource);
        try {
            this.ParentMedicationsMap = ParentMedicationsMap;
            layouts = new ArrayList<>();

            horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv_layout);
            gestureDetector = new GestureDetector(new MyGestureDetector());


            line_medication1 = (TextView) findViewById(R.id.line_medication1);
            line_medication2 = (TextView) findViewById(R.id.line_medication2);
            line_medication3 = (TextView) findViewById(R.id.line_medication3);
            line_medication4 = (TextView) findViewById(R.id.line_medication4);
            line_medication5 = (TextView) findViewById(R.id.line_medication5);
            line_medication6 = (TextView) findViewById(R.id.line_medication6);

            layouts.add(line_medication1);
            layouts.add(line_medication2);
            layouts.add(line_medication3);
            layouts.add(line_medication4);
            layouts.add(line_medication5);
            layouts.add(line_medication6);

            horizontalScrollView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (gestureDetector.onTouchEvent(event)) {
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        try {
            if (ParentMedicationsMap.get(e.getXIndex()).get(0)!=null&&ParentMedicationsMap.get(e.getXIndex()).get(0).size() > 0) {
                line_medication1.setVisibility(VISIBLE);
                line_medication1.setText(ParentMedicationsMap.get(e.getXIndex()).get(0).size() + "");
            } else {
                line_medication1.setVisibility(GONE);
            }


            // Medication1
            if (ParentMedicationsMap.get(e.getXIndex()).get(1)!=null&&ParentMedicationsMap.get(e.getXIndex()).get(1).size() > 0) {
                line_medication2.setVisibility(VISIBLE);
                line_medication2.setText(ParentMedicationsMap.get(e.getXIndex()).get(1).size() + "");
            } else {
                line_medication2.setVisibility(GONE);
            }


            //Medication3
            if (ParentMedicationsMap.get(e.getXIndex()).get(2)!=null&&ParentMedicationsMap.get(e.getXIndex()).get(2).size() > 0) {
                line_medication3.setVisibility(VISIBLE);
                line_medication3.setText(ParentMedicationsMap.get(e.getXIndex()).get(2).size() + "");
            } else {
                line_medication3.setVisibility(GONE);
            }


            //Medication4

            if (ParentMedicationsMap.get(e.getXIndex()).get(3)!=null&&ParentMedicationsMap.get(e.getXIndex()).get(3).size() > 0) {
                line_medication4.setVisibility(VISIBLE);
                line_medication4.setText(ParentMedicationsMap.get(e.getXIndex()).get(3).size() + "");
            } else {
                line_medication4.setVisibility(GONE);
            }


            //Medication5

            if (ParentMedicationsMap.get(e.getXIndex()).get(4)!=null&&ParentMedicationsMap.get(e.getXIndex()).get(4).size() > 0) {
                line_medication5.setVisibility(VISIBLE);
                line_medication5.setText(ParentMedicationsMap.get(e.getXIndex()).get(4).size() + "");
            } else {
                line_medication5.setVisibility(GONE);
            }


            //Medication6
            if (ParentMedicationsMap.get(e.getXIndex()).get(5) != null && ParentMedicationsMap.get(e.getXIndex()).get(5).size() > 0) {
                line_medication6.setVisibility(VISIBLE);
                line_medication6.setText(ParentMedicationsMap.get(e.getXIndex()).get(5).size() + "");
            } else {
                line_medication6.setVisibility(GONE);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }


    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1.getX() < e2.getX()) {
                currPosition = getVisibleViews("left");
            } else {
                currPosition = getVisibleViews("right");
            }

            horizontalScrollView.smoothScrollTo(layouts.get(currPosition)
                    .getLeft(), 0);
            return true;
        }


    }

    public int getVisibleViews(String direction) {
        int position = 0;
        try {
            Rect hitRect = new Rect();
            position = 0;
            int rightCounter = 0;
            for (int i = 0; i < layouts.size(); i++) {
                if (layouts.get(i).getLocalVisibleRect(hitRect)) {
                    if (direction.equals("left")) {
                        position = i;
                        break;
                    } else if (direction.equals("right")) {
                        rightCounter++;
                        position = i;
                        if (rightCounter == 2)
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }
}
