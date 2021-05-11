package com.xaqinren.healthyelders.widget.comment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.PopCommentBinding;
import com.xaqinren.healthyelders.databinding.PopShareBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoCommentBean;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class CommentDialog {
    private PopupWindow popupWindow;
    private View contentView;
    private SoftReference<Context> context;
    private PopCommentBinding binding;
    private CommentAdapter commentAdapter;
    private List<ICommentBean> dataList;
    private boolean isOverData;
    private CommentViewModel viewModel;
    public  CommentDialog(Context context) {
        this.context = new SoftReference<>(context);
        init();
    }

    private void init() {
        viewModel = ViewModelProviders.of((FragmentActivity) context.get()).get(CommentViewModel.class);
        contentView = View.inflate(context.get(), R.layout.pop_comment, null);
        binding = DataBindingUtil.bind(contentView);
        int height = (int) (ScreenUtil.getScreenHeight(context.get()) * 0.8f);
        popupWindow = new PopupWindow(binding.getRoot() , ViewGroup.LayoutParams.MATCH_PARENT ,height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.DialogBottomAnimation);
        popupWindow.setOnDismissListener(() -> {

        });
        binding.close.setOnClickListener(view -> popupWindow.dismiss());
        commentAdapter = new CommentAdapter(R.layout.item_comment, new CommentAdapter.OnChildLoadMoreCommentListener() {
            @Override
            public void onLoadMore(CommentAdapter.ViewHolder viewHolder, int position, ICommentBean iCommentBean,int page , int pageSize) {
                //掉接口
                List<VideoCommentBean> list = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    VideoCommentBean bean1 = new VideoCommentBean();
                    bean1.avatar = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.juimg.com%2Ftuku%2Fyulantu%2F140703%2F330746-140f301555752.jpg&refer=http%3A%2F%2Fimg.juimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623312032&t=dd12bc18844a0c4cfd47510b2ec89c4b";
                    bean1.nickName = "你好呀";
                    bean1.canComment = true;
                    bean1.comment = "评论内容评论内容评论内容评论内容评论内容评论内容评论内容";
                    bean1.commentTime = "12小时前";
                    bean1.likeCount = "666";
                    bean1.isLike = false;
                    list.add(bean1);
                }
                iCommentBean.childComment.addAll(list);
//                viewHolder.commentChildAdapter.addData(list);
                commentAdapter.notifyItemChanged(position);
            }

            @Override
            public void onPackUp(CommentAdapter.ViewHolder baseViewHolder, int position, ICommentBean iCommentBean, int page, int pageSize) {
                iCommentBean.childComment.clear();
//                baseViewHolder.commentChildAdapter.setList(null);
                commentAdapter.notifyItemChanged(position);
            }
        });
        binding.commentList.setLayoutManager(new LinearLayoutManager(context.get(), LinearLayoutManager.VERTICAL, false));
        binding.commentList.setAdapter(commentAdapter);

    }

    public void setDataList(List<ICommentBean> dataList) {
        this.dataList = dataList;
        commentAdapter.setList(this.dataList);
    }

    public void addDataList(List<ICommentBean> dataList) {
        this.dataList.addAll(dataList);

    }

    public void setOverData(boolean overData) {
        isOverData = overData;
        //没有更多评论,变为收起

    }

    public void show(View Parent) {
        if (popupWindow == null) {
            init();
        }
        popupWindow.showAsDropDown(Parent, Gravity.BOTTOM, 0, 0);
    }

    public void hide() {
        popupWindow.dismiss();
    }


    public interface OnChildLoadMore{
        void loadMore(ICommentBean iCommentBean, int page, int pageSize);
    }

}
