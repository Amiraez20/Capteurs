package com.example.capteurs.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class DataPlotView extends View {

    private final List<Float> dataPoints = new LinkedList<>();
    private final int limitPoints = 90;

    private final Paint axesPaint = new Paint();
    private final Paint gridPaint = new Paint();
    private final Paint curvePaint = new Paint();
    private final Paint labelPaint = new Paint();

    public DataPlotView(Context ctx) {
        super(ctx);

        axesPaint.setColor(Color.DKGRAY);
        axesPaint.setStrokeWidth(4);

        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(1);

        curvePaint.setColor(Color.parseColor("#FF5722")); // Deep Orange
        curvePaint.setStrokeWidth(6);
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setAntiAlias(true);

        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(32);
        labelPaint.setAntiAlias(true);

        setBackgroundColor(Color.WHITE);
    }

    public void insertValue(float val) {
        if (dataPoints.size() >= limitPoints) {
            dataPoints.remove(0);
        }
        dataPoints.add(val);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas cv) {
        super.onDraw(cv);

        int w = getWidth();
        int h = getHeight();

        // Trace grid
        for (int i = 1; i < 5; i++) {
            float y = 30 + i * (h - 80) / 5f;
            cv.drawLine(50, y, w - 30, y, gridPaint);
        }
        for (int i = 1; i < 5; i++) {
            float x = 50 + i * (w - 80) / 5f;
            cv.drawLine(x, 30, x, h - 50, gridPaint);
        }

        // Trace axes
        cv.drawLine(50, h - 50, w - 30, h - 50, axesPaint); // X
        cv.drawLine(50, 30, 50, h - 50, axesPaint); // Y

        if (dataPoints.size() < 2) {
            float textWidth = labelPaint.measureText("En attente de variations...");
            cv.drawText("En attente de variations...", (w - textWidth) / 2f, h / 2f, labelPaint);
            return;
        }

        float minVal = Float.MAX_VALUE;
        float maxVal = -Float.MAX_VALUE;

        for (float v : dataPoints) {
            if (v < minVal) minVal = v;
            if (v > maxVal) maxVal = v;
        }

        if (maxVal == minVal) {
            maxVal = minVal + 1;
            minVal = minVal - 1;
        }

        Path p = new Path();

        for (int i = 0; i < dataPoints.size(); i++) {
            float posX = 50 + i * ((w - 100f) / (limitPoints - 1));
            float normalized = (dataPoints.get(i) - minVal) / (maxVal - minVal);
            float posY = h - 50 - normalized * (h - 100);

            if (i == 0) {
                p.moveTo(posX, posY);
            } else {
                p.lineTo(posX, posY);
            }
        }

        cv.drawPath(p, curvePaint);
        cv.drawText(String.format("Minimum : %.2f | Maximum : %.2f", minVal, maxVal), 70, 60, labelPaint);
    }
}
