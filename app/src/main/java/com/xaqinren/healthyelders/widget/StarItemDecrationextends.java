package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleMine.activity.CoinDetailActivity;
import com.xaqinren.healthyelders.moduleMine.adapter.CoinDetailAdapter;

public class StarItemDecrationextends extends RecyclerView.ItemDecoration {
    private int HEAD_HEIGHT;

    private Paint mPaint;

    private Paint textPaint;

    private Rect textRect;

    private int margin16;

    public StarItemDecrationextends(Context context) {

        HEAD_HEIGHT = (int) context.getResources().getDimension(R.dimen.dp_40);

        mPaint = new Paint();

        mPaint.setColor(Color.parseColor("#F3F3F3"));

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);

        textPaint.setTextSize(context.getResources().getDimension(R.dimen.sp_12));

        textPaint.setColor(Color.parseColor("#666666"));

        textRect = new Rect();

        margin16 = (int) context.getResources().getDimension(R.dimen.dp_16);

    }

    @Override

    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        super.onDraw(canvas, parent, state);

        if (parent.getAdapter() instanceof CoinDetailAdapter) {

            CoinDetailAdapter startAdapter = (CoinDetailAdapter) parent.getAdapter();

            int childCount = parent.getChildCount();

            int left = parent.getPaddingLeft();

            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount; i++) {

                View view = parent.getChildAt(i);

                int positon = parent.getChildLayoutPosition(view);

                boolean isHead = startAdapter.isGroupHeader(positon);

                if (isHead && view.getTop()  - parent.getPaddingTop() >= 0) {

                    canvas.drawRect(left, view.getTop() - HEAD_HEIGHT, right, view.getTop(), mPaint);

                    String groupName = startAdapter.getGroupName(positon);

                    textPaint.getTextBounds(groupName, 0, groupName.length(), textRect);

                    canvas.drawText(groupName, left + margin16, view.getTop() - HEAD_HEIGHT / 2 + textRect.height() / 2, textPaint);

                    String incomeName = startAdapter.getTotalName(positon);

                    textPaint.getTextBounds(incomeName, 0, incomeName.length(), textRect);

                    canvas.drawText(incomeName, right - textRect.width() - margin16 , view.getTop() - HEAD_HEIGHT / 2 + textRect.height() / 2, textPaint);

                } else if (view.getTop() - HEAD_HEIGHT - parent.getPaddingTop() >= 0) {

//                    canvas.drawRect(left, view.getTop() - 3, right, view.getTop(), mPaint);

                }

            }

        }

    }

    @Override

    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        super.onDrawOver(c, parent, state);

        if (parent.getAdapter() instanceof CoinDetailAdapter) {

            CoinDetailAdapter startAdapter = (CoinDetailAdapter) parent.getAdapter();

            int position = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
            RecyclerView.ViewHolder viewHolderForAdapterPosition = parent.findViewHolderForAdapterPosition(position);
            if (viewHolderForAdapterPosition == null) {
                return;
            }
            View itemView = viewHolderForAdapterPosition.itemView;

            int left = parent.getLeft();

            int right = parent.getWidth() - parent.getPaddingRight();

            int top = parent.getPaddingTop();

            String groupName = startAdapter.getGroupName(position);
            String incomeName = startAdapter.getTotalName(position);

            if (startAdapter.isGroupHeader(position + 1)) {

                int bottom = Math.min(HEAD_HEIGHT, itemView.getBottom() - top);

                c.drawRect(left, top, right, top + bottom, mPaint);

                textPaint.getTextBounds(groupName, 0, groupName.length(), textRect);
                int y ;
                int height = itemView.getBottom() - HEAD_HEIGHT;
                if (height > 0) {
                    y = HEAD_HEIGHT / 2 + textRect.height() / 2;
                }else{
                    y = HEAD_HEIGHT / 2 + textRect.height() / 2 + itemView.getBottom() - HEAD_HEIGHT;
                }
                c.drawText(groupName, left + margin16, y , textPaint);



                textPaint.getTextBounds(incomeName, 0, incomeName.length(), textRect);

                c.drawText(incomeName, right - textRect.width() - margin16 , y, textPaint);
            } else {

                c.drawRect(left, top, right, top + HEAD_HEIGHT, mPaint);

                textPaint.getTextBounds(groupName, 0, groupName.length(), textRect);

                c.drawText(groupName, left + margin16, top + HEAD_HEIGHT / 2 + textRect.height() / 2, textPaint);


                textPaint.getTextBounds(incomeName, 0, incomeName.length(), textRect);

                c.drawText(incomeName, right - textRect.width() - margin16 , top + HEAD_HEIGHT / 2 + textRect.height() / 2, textPaint);

            }

        }

    }

    @Override

    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getAdapter() instanceof CoinDetailAdapter) {

            CoinDetailAdapter startAdapter = (CoinDetailAdapter) parent.getAdapter();

            int positon = parent.getChildLayoutPosition(view);

            if (startAdapter.isGroupHeader(positon)) {

                outRect.set(0, HEAD_HEIGHT, 0, 0);

            } else {

                outRect.set(0, 0, 0, 0);

            }

        }

    }

    private int dp2px(Context context, float dp) {

        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dp * scale * 0.5f);

    }

}
