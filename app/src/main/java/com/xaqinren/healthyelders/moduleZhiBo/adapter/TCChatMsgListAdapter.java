package com.xaqinren.healthyelders.moduleZhiBo.adapter;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCChatEntity;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.utils.GlideUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Module:   TCChatMsgListAdapter
 * <p>
 * Function: 消息列表的 Adapter。
 */
public class TCChatMsgListAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private static String TAG = TCChatMsgListAdapter.class.getSimpleName();
    private static final int ITEM_COUNT = 7;
    private List<TCChatEntity> mList;
    private int mTotalHeight;
    private Context mContext;
    private ListView mListView;
    private ArrayList<TCChatEntity> mArray = new ArrayList<>();

    class AnimatorInfo {
        long createTime;

        public AnimatorInfo(long uTime) {
            createTime = uTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }

    private static final int MAXANIMATORCOUNT = 8;
    private static final int MAXLISTVIEWHEIGHT = 450;
    private static final int ANIMATORDURING = 8000;
    private static final int MAXITEMCOUNT = 50;
    private LinkedList<AnimatorSet> mAnimatorSetList;
    private LinkedList<AnimatorInfo> mAnimatorInfoList;
    private boolean mScrolling = false;

    public TCChatMsgListAdapter(Context context, ListView listview, List<TCChatEntity> objects) {
        this.mContext = context;
        mListView = listview;
        this.mList = objects;

        mAnimatorSetList = new LinkedList<>();
        mAnimatorInfoList = new LinkedList<>();

        mListView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 通过名称计算颜色
     */
    private int calcNameColor(String strName) {
        if (strName == null)
            return 0;
        byte idx = 0;
        byte[] byteArr = strName.getBytes();
        for (int i = 0; i < byteArr.length; i++) {
            idx ^= byteArr[i];
        }

        switch (idx & 0x7) {
            case 1:
                return mContext.getResources().getColor(R.color.colorSendName1);
            case 2:
                return mContext.getResources().getColor(R.color.colorSendName2);
            case 3:
                return mContext.getResources().getColor(R.color.colorSendName3);
            case 4:
                return mContext.getResources().getColor(R.color.colorSendName4);
            case 5:
                return mContext.getResources().getColor(R.color.colorSendName5);
            case 6:
                return mContext.getResources().getColor(R.color.colorSendName6);
            case 7:
                return mContext.getResources().getColor(R.color.colorSendName7);
            case 0:
            default:
                return mContext.getResources().getColor(R.color.colorSendName);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SpannableString spanString;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_live_msg, null);
            holder.llLeave = (LinearLayout) convertView.findViewById(R.id.ll_leave);
            holder.ivLeave = (ImageView) convertView.findViewById(R.id.iv_leave);
            holder.tvLeave = (TextView) convertView.findViewById(R.id.tv_leave);
            holder.sendName = (TextView) convertView.findViewById(R.id.sendName);
            holder.sendContext = (TextView) convertView.findViewById(R.id.sendcontext);
            holder.sendImg = (ImageView) convertView.findViewById(R.id.iv_gift);
            convertView.setTag(R.id.tag_first, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_first);
        }

        TCChatEntity item = mList.get(position);

        //        if (mCreateAnimator && mBLiveAnimator) {
        //            playViewAnimator(convertView, position, item);
        //        }

        // 判断是否展示文本  展示文本设置展示文本的颜色 提示语 消息文本要设置名字颜色
        if (item.getType() == LiveConstants.TYPE_SHOW) {
            //展示消息
            holder.sendName.setText("");
            holder.sendContext.setTextColor(mContext.getResources().getColor(R.color.colorShowMsg));
            holder.sendContext.setText(item.getContent());
            holder.llLeave.setVisibility(View.GONE);
        } else if (item.getType() == LiveConstants.TYPE_DES) {
            holder.sendName.setText("");
            //展示消息
            holder.sendContext.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.sendContext.setText(item.getContent());
            holder.llLeave.setVisibility(View.GONE);
        } else if (item.getType() == LiveConstants.IMCMD_GIFT) {
            holder.sendName.setText("");
            //礼物消息
            holder.sendContext.setTextColor(mContext.getResources().getColor(R.color.colorGiftMsg));
            holder.sendContext.setText(item.getSenderName() + "  " + item.getContent());

            showLeaveIcon(holder, item);

        } else if (item.getType() == LiveConstants.IMCMD_TEXT_MSG) {
            spanString = new SpannableString(item.getSenderName() + ":  " + item.getContent());
            //文字消息
            holder.sendContext.setText(item.getContent());
            holder.sendContext.setTextColor(mContext.getResources().getColor(R.color.colorTextMsg));
            holder.sendContext.setText(spanString);

            showLeaveIcon(holder, item);

            //设置处理加载聊天表情文字
            FaceManager.handlerZBMsgEmojiText(item.getSenderName().length() + 3, holder.sendContext, holder.sendContext.getText().toString(), false);
        } else {
            holder.sendName.setText("");
            // 其他消息类型
            spanString = new SpannableString(item.getSenderName() + ":  " + item.getContent());
            spanString.setSpan(spanString, 0, item.getSenderName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.sendContext.setTextColor(mContext.getResources().getColor(R.color.colorSendName8));
            holder.sendContext.setText(item.getSenderName() + "  " + item.getContent());

            showLeaveIcon(holder, item);

        }
        holder.sendImg.setVisibility(View.GONE);

        // 设置控件实际宽度以便计算列表项实际高度
        //holder.sendContext.fixViewWidth(mListView.getWidth());

        return convertView;
    }

    public void showLeaveIcon(ViewHolder holder, TCChatEntity item) {
        if (item.getLevelName().equals("0")) {
            holder.llLeave.setVisibility(View.GONE);
        } else {
            holder.llLeave.setVisibility(View.VISIBLE);
            holder.tvLeave.setText(item.getLevelName());
            if (TextUtils.isEmpty(item.getLevelIcon())) {
                GlideUtil.intoImageView(mContext, R.mipmap.icon_dj_def, holder.ivLeave);
            } else {
                GlideUtil.intoImageView(mContext, item.getLevelIcon(), holder.ivLeave);
            }
        }
    }


    static class ViewHolder {
        public TextView sendName;
        public TextView sendContext;
        public TextView tvLeave;
        public ImageView sendImg;
        public ImageView ivLeave;
        public LinearLayout llLeave;

    }

    /**
     * 停止View属性动画
     *
     * @param itemView 当前执行动画View
     */
    private void stopViewAnimator(View itemView) {
        AnimatorSet aniSet = (AnimatorSet) itemView.getTag(R.id.tag_second);
        if (null != aniSet) {
            aniSet.cancel();
            mAnimatorSetList.remove(aniSet);
        }
    }

    /**
     * 播放View属性动画
     *
     * @param itemView   动画对应View
     * @param startAlpha 初始透明度
     * @param duringTime 动画时长
     */
    private void playViewAnimator(View itemView, float startAlpha, long duringTime) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", startAlpha, 0f);
        AnimatorSet aniSet = new AnimatorSet();
        aniSet.setDuration(duringTime);
        aniSet.play(animator);
        aniSet.start();
        mAnimatorSetList.add(aniSet);
        itemView.setTag(R.id.tag_second, aniSet);
    }

    /**
     * 播放渐消动画
     *
     * @param pos  位置
     * @param view 执行动画View
     */
    public void playDisappearAnimator(int pos, View view) {
        int firstVisable = mListView.getFirstVisiblePosition();
        if (firstVisable <= pos) {
            playViewAnimator(view, 1f, ANIMATORDURING);
        } else {
            Log.d(TAG, "playDisappearAnimator->unexpect pos: " + pos + "/" + firstVisable);
        }
    }

    /**
     * 继续播放渐消动画
     *
     * @param itemView 执行动画View
     * @param position 位置
     * @param item
     */
    private void continueAnimator(View itemView, int position, final TCChatEntity item) {
        int animatorIdx = mList.size() - 1 - position;

        if (animatorIdx < MAXANIMATORCOUNT) {
            float startAlpha = 1f;
            long during = ANIMATORDURING;

            stopViewAnimator(itemView);

            // 播放动画
            if (position < mAnimatorInfoList.size()) {
                AnimatorInfo info = mAnimatorInfoList.get(position);
                long time = info.getCreateTime();  //  获取列表项加载的初始时间
                during = during - (System.currentTimeMillis() - time);     // 计算动画剩余时长
                startAlpha = 1f * during / ANIMATORDURING;                    // 计算动画初始透明度
                if (during < 0) {   // 剩余时长小于0直接设置透明度为0并返回
                    itemView.setAlpha(0f);
                    Log.v(TAG, "continueAnimator->already end animator:" + position + "/" + item.getContent() + "-" + during);
                    return;
                }
            }

            // 创建属性动画并播放
            Log.v(TAG, "continueAnimator->pos: " + position + "/" + mList.size() + ", alpha:" + startAlpha + ", dur:" + during);
            playViewAnimator(itemView, startAlpha, during);
        } else {
            Log.v(TAG, "continueAnimator->ignore pos: " + position + "/" + mList.size());
        }
    }

    /**
     * 播放消失动画
     */
    private void playDisappearAnimator() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View itemView = mListView.getChildAt(i);
            if (null == itemView) {
                Log.w(TAG, "playDisappearAnimator->view not found: " + i + "/" + mListView.getCount());
                break;
            }

            // 更新动画创建时间
            int position = mListView.getFirstVisiblePosition() + i;
            if (position < mAnimatorInfoList.size()) {
                mAnimatorInfoList.get(position).setCreateTime(System.currentTimeMillis());
            } else {
                Log.e(TAG, "playDisappearAnimator->error: " + position + "/" + mAnimatorInfoList.size());
            }

            playViewAnimator(itemView, 1f, ANIMATORDURING);
        }
    }

    /**
     * 播放列表项动画
     *
     * @param itemView 要播放动画的列表项
     * @param position 列表项的位置
     * @param item     列表数据
     */
    private void playViewAnimator(View itemView, int position, final TCChatEntity item) {
        if (!mArray.contains(item)) {  // 首次加载的列表项动画
            mArray.add(item);
            mAnimatorInfoList.add(new AnimatorInfo(System.currentTimeMillis()));
        }

        if (mScrolling) {  // 滚动时不播放动画，设置透明度为1
            itemView.setAlpha(1f);
        } else {
            continueAnimator(itemView, position, item);
        }
    }

    /**
     * 删除超过上限(MAXITEMCOUNT)的列表项
     */
    private void clearFinishItem() {
        // 删除超过的列表项
        while (mList.size() > MAXITEMCOUNT) {
            mList.remove(0);
            if (mAnimatorInfoList.size() > 0) {
                mAnimatorInfoList.remove(0);
            }
        }

        // 缓存列表延迟删除
        while (mArray.size() > (MAXITEMCOUNT << 1)) {
            mArray.remove(0);
        }

        while (mAnimatorInfoList.size() >= mList.size()) {
            Log.e(TAG, "clearFinishItem->error size: " + mAnimatorInfoList.size() + "/" + mList.size());
            if (mAnimatorInfoList.size() > 0) {
                mAnimatorInfoList.remove(0);
            } else {
                break;
            }
        }
    }

    /**
     * 重新计算ITEMCOUNT条记录的高度，并动态调整ListView的高度
     */
    private void redrawListViewHeight() {

        int totalHeight = 0;
        int start = 0, lineCount = 0;

        if (mList.size() <= 0) {
            return;
        }

        if (mTotalHeight >= MAXLISTVIEWHEIGHT) {
            return;
        }

        // 计算底部ITEMCOUNT条记录的高度
        //        mCreateAnimator = false;    // 计算高度时不播放属性动画
        for (int i = mList.size() - 1; i >= start && lineCount < ITEM_COUNT; i--, lineCount++) {
            View listItem = getView(i, null, mListView);

            listItem.measure(View.MeasureSpec.makeMeasureSpec(MAXLISTVIEWHEIGHT, View.MeasureSpec.AT_MOST)
                    , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            // add item height
            totalHeight += listItem.getMeasuredHeight();
            if (totalHeight > MAXLISTVIEWHEIGHT) {
                totalHeight = MAXLISTVIEWHEIGHT;
                break;
            }
        }
        //        mCreateAnimator = true;


        mTotalHeight = totalHeight;
        // 调整ListView高度
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight + (mListView.getDividerHeight() * (lineCount - 1));
        mListView.setLayoutParams(params);
    }

    /**
     * 停止当前所有属性动画并重置透明度
     */
    private void stopAnimator() {
        // 停止动画
        for (AnimatorSet anSet : mAnimatorSetList) {
            anSet.cancel();
        }
        mAnimatorSetList.clear();
    }

    /**
     * 重置透明度
     */
    private void resetAlpha() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View view = mListView.getChildAt(i);
            view.setAlpha(1f);
        }
    }

    /**
     * 继续可视范围内所有动画
     */
    private void continueAllAnimator() {
        //        int startPos = mListView.getFirstVisiblePosition();
        //
        //        for (int i = 0; i < mListView.getChildCount(); i++) {
        //            View view = mListView.getChildAt(i);
        //            if (null != view && startPos + i < mList.size()) {
        //                continueAnimator(view, startPos + i, mList.get(startPos + i));
        //            }
        //        }
    }

    /**
     * 重载notifyDataSetChanged方法实现渐消动画并动态调整ListView高度
     */
    @Override
    public void notifyDataSetChanged() {
        Log.v(TAG, "notifyDataSetChanged->scroll: " + mScrolling);
        //        if (mScrolling) {
        //            // 滑动过程中不刷新
        //            super.notifyDataSetChanged();
        //            return;
        //        }
        //
        //        // 删除多余项
        //        clearFinishItem();
        //
        //        if (mBLiveAnimator) {
        //            // 停止之前动画
        //            stopAnimator();
        //
        //            // 清除动画
        //            mAnimatorSetList.clear();
        //        }

        super.notifyDataSetChanged();

        // 重置ListView高度
        redrawListViewHeight();

        //        if (mBLiveAnimator && mList.size() >= MAXITEMCOUNT) {
        //            continueAllAnimator();
        //        }

        // 自动滚动到底部
        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(mListView.getCount() - 1);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                //                if (mBLiveAnimator) {
                //                    // 开始滚动时停止所有属性动画
                //                    stopAnimator();
                //                    resetAlpha();
                //                }
                mScrolling = true;
                break;
            case SCROLL_STATE_IDLE:
                mScrolling = false;
                //                if (mBLiveAnimator) {
                //                    // 停止滚动时播放渐消动画
                //                    playDisappearAnimator();
                //                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
