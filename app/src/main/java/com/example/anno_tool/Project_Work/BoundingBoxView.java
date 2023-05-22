package com.example.anno_tool.Project_Work;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.anno_tool.Fragment.Work_label_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
public class BoundingBoxView extends View {
    public List<RectF> boundingBoxes = new ArrayList<>();
    static String label_name;
    private Paint boxPaint = new Paint();
    private Paint textPaint = new Paint();
    private RectF currentBox = null;
    private Path path = new Path();
    float Xmin,Xmax,Ymin,Ymax;
    FragmentActivity activity;
    Work_label_2 workLabel2=new Work_label_2();
    public Stack<List<RectF>> undoStack = new Stack<>();
    public Stack<List<RectF>> redoStack = new Stack<>();

    public BoundingBoxView(Context context) {
        super(context);
        init();
    }
    public BoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5);

        textPaint.setColor(Color.RED);
        textPaint.setTextSize(30);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw existing bounding boxes
        for (RectF box : boundingBoxes) {
            canvas.drawRect(box, boxPaint);
            canvas.drawText(box.left + ", " + box.top+", "+box.right+", "+box.bottom, box.left, box.top - 10, textPaint);
            Xmin=box.left;
            Xmax=box.right;
            Ymin=box.bottom;
            Ymax=box.top;
            workLabel2.senddataCordinate(Xmax,Xmin,Ymax,Ymin);
        }
        Log.d("box",""+boundingBoxes.size());
        // Draw current drawing path
        if (currentBox != null) {
            canvas.drawRect(currentBox, boxPaint);
            canvas.drawText(currentBox.left + ", " + currentBox.top, currentBox.left, currentBox.top - 10, textPaint);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Start drawing new bounding box
                currentBox = new RectF(x, y, x, y);
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                // Update current bounding box
                currentBox.right = x;
                currentBox.bottom = y;
                path.reset();
                path.addRect(currentBox, Path.Direction.CW);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                // Finish drawing new bounding box
                if (currentBox != null) {
                    boundingBoxes.add(currentBox);
                    currentBox = null;
                    path.reset();
                    undoStack.push(new ArrayList<>(boundingBoxes));
                    redoStack.clear();
                    opendialog();
                    invalidate();
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
    private void opendialog() {
        SelectLabel selectLabel=new SelectLabel();
//        selectLabel.setSpinner(items);
//        selectLabel.show(fragmentActivity.getSupportFragmentManager(selectLabel,"label Dialog"));;
        try{
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            selectLabel.show(fragmentManager,null);
        }catch (Exception e){

        }
    }
    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(new ArrayList<>(boundingBoxes));
            boundingBoxes = undoStack.pop();
            invalidate();
        }
    }
    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(new ArrayList<>(boundingBoxes));
            boundingBoxes = redoStack.pop();
            invalidate();
        }
    }

    public void setActivity(FragmentActivity activity) {
        this.activity=activity;
    }

    public void delete() {
        if(!undoStack.isEmpty()){
            boundingBoxes.remove(1);
            invalidate();
        }

    }

    public void setLabel(String labelname) {
        label_name=labelname;
    }
}