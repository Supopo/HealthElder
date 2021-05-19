package com.xaqinren.healthyelders.widget.comment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.chad.library.adapter.base.animation.SlideInBottomAnimation;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.databinding.PopCommentBinding;
import com.xaqinren.healthyelders.databinding.PopShareBinding;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoCommentBean;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

//评论PopUpWindow
public class CommentDialog {
    private final String TAG = this.getClass().getSimpleName();

    private PopupWindow popupWindow;
    private View contentView;
    private SoftReference<Context> context;
    private PopCommentBinding binding;
    private CommentListAdapter commentAdapter;
    private List<CommentListBean> dataList;
    private boolean isOverData;
    private CommentViewModel viewModel;
    private OnChildClick onChildClick;
    private String videoId;
    private BaseLoadMoreModule loadMoreModule;
    private OnLoadMoreListener onLoadMoreListener;
    private Context mContext;
    private int page = 1;

    public CommentDialog(Context context, String videoId) {
        this.context = new SoftReference<>(context);
        this.videoId = videoId;
        mContext = context;
        init();
    }

    public void setOnChildClick(OnChildClick onChildClick) {
        this.onChildClick = onChildClick;
    }

    private void init() {
        viewModel = ViewModelProviders.of((FragmentActivity) context.get()).get(CommentViewModel.class);
        contentView = View.inflate(context.get(), R.layout.pop_comment, null);
        binding = DataBindingUtil.bind(contentView);
        int height = (int) (ScreenUtil.getScreenHeight(context.get()) * 0.8f);
        popupWindow = new PopupWindow(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.DialogBottomAnimation);
        popupWindow.setOnDismissListener(() -> {

        });
        binding.close.setOnClickListener(view -> popupWindow.dismiss());
        commentAdapter = new CommentListAdapter(R.layout.item_comment_list, new CommentListAdapter.OnChildLoadMoreCommentListener() {
            @Override
            public void onLoadMore(int position, ICommentBean iCommentBean, int page, int pageSize) {

            }

            @Override
            public void onPackUp(int position, ICommentBean iCommentBean, int page, int pageSize) {

            }
        }, new CommentListAdapter.OnOperationItemClickListener() {
            @Override
            public void toComment(ICommentBean iCommentBean) {
                onChildClick.toComment(iCommentBean);
            }

            @Override
            public void toLike(ICommentBean iCommentBean) {
                onChildClick.toLike(iCommentBean);
            }

            @Override
            public void toUser(ICommentBean iCommentBean) {
                onChildClick.toUser(iCommentBean);
            }
        });

        commentAdapter.setAnimationEnable(false);
        binding.commentList.setLayoutManager(new LinearLayoutManager(context.get(), LinearLayoutManager.VERTICAL, false));
        binding.commentList.setAdapter(commentAdapter);
        binding.commentLayout.setOnClickListener(view -> {
            onChildClick.toCommentVideo(videoId);
        });

        loadMoreModule = commentAdapter.getLoadMoreModule();
        loadMoreModule.setAutoLoadMore(true);
        loadMoreModule.setEnableLoadMore(true);
        loadMoreModule.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                getCommentList();
            }
        });

        getCommentList();
        commentList.observe((LifecycleOwner) mContext, dataList -> {
            if (dataList.size() > 0) {
                //加载更多加载完成
                loadMoreModule.loadMoreComplete();
            }
            if (page == 1) {
                commentAdapter.setList(dataList);
            } else {
                if (dataList.size() == 0) {
                    //加载更多加载结束
                    loadMoreModule.loadMoreEnd(true);
                    page--;
                }
                commentAdapter.addData(dataList);
            }
        });
    }


    public void setData() {

    }

    public void rushData(){
        page = 1;
        getCommentList();
    }


    public void addDataList(List<CommentListBean> dataList) {
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
        //        setDataList(getCommentData());
        popupWindow.showAsDropDown(Parent, Gravity.BOTTOM, 0, 0);
    }


    public void hide() {
        popupWindow.dismiss();
    }


    public interface OnChildClick {
        //评论评论[存在评论本身的可能性,即没有XXX 回复 XXX的可能性]
        void toComment(ICommentBean iCommentBean);

        //评论视频本身
        void toCommentVideo(String videoId);

        //点击喜欢
        void toLike(ICommentBean iCommentBean);

        //查看用户
        void toUser(ICommentBean iCommentBean);
    }

    public MutableLiveData<List<CommentListBean>> commentList = new MutableLiveData<>();


    public void getCommentList() {
        LiveRepository.getInstance().getCommentList(commentList, page, videoId);
    }
}
