package com.tetris.random;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import java.util.Random;

public class MainActivity extends Activity {

    static final int[][][] PIECES = {
        // I
        {{0,0},{1,0},{2,0},{3,0}},
        // O
        {{0,0},{1,0},{0,1},{1,1}},
        // T
        {{0,0},{1,0},{2,0},{1,1}},
        // S
        {{1,0},{2,0},{0,1},{1,1}},
        // Z
        {{0,0},{1,0},{1,1},{2,1}},
        // J
        {{0,0},{0,1},{1,1},{2,1}},
        // L
        {{2,0},{0,1},{1,1},{2,1}}
    };

    static final int[] COLORS = {
        Color.parseColor("#00CFCF"), // I — cyan
        Color.parseColor("#CFCF00"), // O — yellow
        Color.parseColor("#9F00CF"), // T — purple
        Color.parseColor("#00CF00"), // S — green
        Color.parseColor("#CF0000"), // Z — red
        Color.parseColor("#0000CF"), // J — blue
        Color.parseColor("#CF7F00")  // L — orange
    };

    static final String[] NAMES = {"I", "O", "T", "S", "Z", "J", "L"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#1A1A2E"));

        TetrisView tetrisView = new TetrisView(this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        tetrisView.setLayoutParams(viewParams);

        Button button = new Button(this);
        button.setText("Случайная фигура");
        button.setTextSize(20f);
        button.setBackgroundColor(Color.parseColor("#E94560"));
        button.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(32, 16, 32, 48);
        button.setLayoutParams(btnParams);

        button.setOnClickListener(v -> {
            tetrisView.randomPiece();
        });

        layout.addView(tetrisView);
        layout.addView(button);
        setContentView(layout);
    }

    static class TetrisView extends View {
        private int currentPiece = 0;
        private final Random random = new Random();
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint bgPaint = new Paint();
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public TetrisView(Context context) {
            super(context);
            bgPaint.setColor(Color.parseColor("#16213E"));
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(48f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            randomPiece();
        }

        public void randomPiece() {
            currentPiece = random.nextInt(PIECES.length);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.parseColor("#1A1A2E"));

            int w = getWidth();
            int h = getHeight();
            int cellSize = Math.min(w, h) / 7;
            int gridW = 4 * cellSize;
            int gridH = 4 * cellSize;
            int startX = (w - gridW) / 2;
            int startY = (h - gridH) / 2 - cellSize;

            // Draw grid background
            bgPaint.setColor(Color.parseColor("#16213E"));
            canvas.drawRect(startX - 4, startY - 4,
                startX + gridW + 4, startY + gridH + 4, bgPaint);

            // Draw grid lines
            paint.setColor(Color.parseColor("#0F3460"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            for (int i = 0; i <= 4; i++) {
                canvas.drawLine(startX + i * cellSize, startY,
                    startX + i * cellSize, startY + gridH, paint);
                canvas.drawLine(startX, startY + i * cellSize,
                    startX + gridW, startY + i * cellSize, paint);
            }

            // Draw piece blocks
            int[][] blocks = PIECES[currentPiece];
            int color = COLORS[currentPiece];
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);

            Paint shadowPaint = new Paint();
            shadowPaint.setColor(darken(color));
            shadowPaint.setStyle(Paint.Style.FILL);

            int pad = cellSize / 8;
            for (int[] block : blocks) {
                int bx = startX + block[0] * cellSize;
                int by = startY + block[1] * cellSize;
                // Main block
                canvas.drawRect(bx + pad, by + pad,
                    bx + cellSize - pad, by + cellSize - pad, paint);
                // Inner highlight
                Paint highlightPaint = new Paint();
                highlightPaint.setColor(lighten(color));
                highlightPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(bx + pad, by + pad,
                    bx + cellSize / 2, by + cellSize / 3, highlightPaint);
            }

            // Draw piece name
            textPaint.setTextSize(cellSize * 0.9f);
            textPaint.setColor(color);
            canvas.drawText("Фигура: " + NAMES[currentPiece],
                w / 2f, startY + gridH + cellSize * 1.5f, textPaint);
        }

        private int darken(int color) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.6f;
            return Color.HSVToColor(hsv);
        }

        private int lighten(int color) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] = Math.min(1f, hsv[2] * 1.4f);
            hsv[1] *= 0.5f;
            return Color.HSVToColor(hsv);
        }
    }
}
