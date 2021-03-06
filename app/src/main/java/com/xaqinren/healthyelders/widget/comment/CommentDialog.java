package com.xaqinren.healthyelders.widget.comment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.PopCommentBinding;
import com.xaqinren.healthyelders.databinding.PopShareBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoCommentBean;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;

//??????PopUpWindow
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
    private Context mContext;
    private Activity activity;
    private int page = 1;
    private int mCommentCount;
    private Disposable commentDisposable;

    public CommentDialog(Context context, String videoId) {
        this.context = new SoftReference<>(context);
        this.videoId = videoId;
        mContext = context;
        activity = (Activity) context;
        init();
    }

    public CommentDialog(Context context, String videoId, Activity activity) {
        this.context = new SoftReference<>(context);
        this.videoId = videoId;
        mContext = context;
        this.activity = activity;
        init();
    }

    public void setOnChildClick(OnChildClick onChildClick) {
        this.onChildClick = onChildClick;
    }


    private void init() {

        commentDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(bean -> {
            if (bean != null) {
                if (bean.msgId == CodeTable.VIDEO_SEND_COMMENT_OVER) {
                    if (TextUtils.isEmpty(bean.content)) {
                        binding.tvComment.setText("???????????????????????????");
                    }else {
                        binding.tvComment.setText(bean.content);
                    }
                }
            }
        });


        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        viewModel = ViewModelProviders.of((FragmentActivity) context.get()).get(CommentViewModel.class);
        contentView = View.inflate(context.get(), R.layout.pop_comment, null);
        binding = DataBindingUtil.bind(contentView);
        int height = (int) (MScreenUtil.getScreenHeight(context.get()) * 0.6f);
        popupWindow = new PopupWindow(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.DialogBottomAnimation);
        popupWindow.setOnDismissListener(() -> {

        });
        //??????dialog
        binding.close.setOnClickListener(view -> {
            if (Constant.DEBUG) {
                rushData();
            } else {
                popupWindow.dismiss();
            }
        });
        commentAdapter = new CommentListAdapter(R.layout.item_comment_list, new CommentListAdapter.OnChildLoadMoreCommentListener() {
            @Override
            public void onLoadMore(int position, CommentListBean iCommentBean, int page, int pageSize) {
                iCommentBean.parentPos = position;
                getCommentReplyList(iCommentBean);
            }

            @Override
            public void onPackUp(int position, CommentListBean iCommentBean, int page, int pageSize) {
                iCommentBean.itemPage = 1;
                iCommentBean.replyList.clear();
                commentAdapter.notifyItemChanged(position);

            }
        }, new CommentListAdapter.OnOperationItemClickListener() {
            @Override
            public void toComment(CommentListBean iCommentBean) {
                onChildClick.toComment(iCommentBean);
            }

            @Override
            public void toCommentReply(CommentListBean iCommentBean) {
                onChildClick.toCommentReply(iCommentBean);
            }

            @Override
            public void toLike(CommentListBean iCommentBean) {
                onChildClick.toLike(iCommentBean);
                //?????????????????????
                if (!InfoCache.getInstance().checkLogin()) {
                    mContext.startActivity(new Intent(mContext, SelectLoginActivity.class));
                    return;
                }
                if (UserInfoMgr.getInstance().getUserInfo() == null) {
                    mContext.startActivity(new Intent(mContext, SelectLoginActivity.class));
                    return;
                }
                //????????????????????????
                if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                    mContext.startActivity(new Intent(mContext, PhoneLoginActivity.class));
                    return;
                }

                //??????
                setCommentLike(videoId, iCommentBean.id, !iCommentBean.hasFavorite, false);

                CommentListBean commentBean = commentAdapter.getData().get(iCommentBean.parentPos);

                if (commentBean.hasFavorite) {
                    if (commentBean.favoriteCount > 0) {
                        commentBean.favoriteCount--;
                    }
                } else {
                    commentBean.favoriteCount++;
                }

                commentBean.hasFavorite = !commentBean.hasFavorite;

                //????????????Item
                commentAdapter.notifyItemChanged(iCommentBean.parentPos, 99);
            }

            @Override
            public void toLikeReply(CommentListBean iCommentBean) {
                //?????????????????????
                if (!InfoCache.getInstance().checkLogin()) {
                    mContext.startActivity(new Intent(mContext, SelectLoginActivity.class));
                    return;
                }
                if (UserInfoMgr.getInstance().getUserInfo() == null) {
                    mContext.startActivity(new Intent(mContext, SelectLoginActivity.class));
                    return;
                }
                //????????????????????????
                if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                    mContext.startActivity(new Intent(mContext, PhoneLoginActivity.class));
                    return;
                }

                //??????
                setCommentLike(videoId, iCommentBean.id, !iCommentBean.hasFavorite, true);

                CommentListBean commentBean = commentAdapter.getData().get(iCommentBean.parentPos);
                commentBean.itemPos = iCommentBean.itemPos;
                if (commentBean.allReply != null) {
                    CommentListBean commentChild = commentBean.allReply.get(iCommentBean.itemPos);
                    if (commentChild != null) {
                        if (commentChild.hasFavorite) {
                            if (commentChild.favoriteCount > 0) {
                                commentChild.favoriteCount--;
                            }
                        } else {
                            commentChild.favoriteCount++;
                        }


                        commentChild.hasFavorite = !commentChild.hasFavorite;
                    }
                }

                commentAdapter.notifyItemChanged(iCommentBean.parentPos, 98);


            }

            @Override
            public void toUser(CommentListBean iCommentBean) {
                onChildClick.toUser(iCommentBean);
            }
        });

        commentAdapter.setAnimationEnable(false);
        binding.commentList.setLayoutManager(new LinearLayoutManager(context.get(), LinearLayoutManager.VERTICAL, false));
        binding.commentList.setAdapter(commentAdapter);
        binding.commentLayout.setOnClickListener(view -> {
            onChildClick.toCommentVideo(videoId);
        });
        binding.ivFace.setOnClickListener(view -> {
            onChildClick.toOpenFeace(videoId);
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
                //????????????????????????
                loadMoreModule.loadMoreComplete();
            }
            if (page == 1) {
                commentAdapter.setList(dataList);
            } else {
                if (dataList.size() == 0) {
                    //????????????????????????
                    loadMoreModule.loadMoreEnd(true);
                    page--;
                }
                commentAdapter.addData(dataList);
            }
            binding.commentCountTv.setText(commentAdapter.getData().size() + "?????????");
        });

        commentReplyList.observe((LifecycleOwner) mContext, replyDatas -> {
            if (replyDatas != null) {

                if (replyDatas.replyList != null && replyDatas.replyList.size() > 0) {

                    commentAdapter.getData().get(replyDatas.parentPos).itemPage++;

                    //????????????????????????
                    replyDatas.replyList.get(replyDatas.replyList.size() - 1).lodaState = 1;
                    replyDatas.replyList.get(replyDatas.replyList.size() - 1).itemPage = commentAdapter.getData().get(replyDatas.parentPos).itemPage;


                    int index = commentAdapter.getData().get(replyDatas.parentPos).replyList.size() > 0 ? commentAdapter.getData().get(replyDatas.parentPos).replyList.size() : 0;
                    commentAdapter.getData().get(replyDatas.parentPos)
                            .replyList.addAll(index,
                            replyDatas.replyList);


                    commentAdapter.getData().get(replyDatas.parentPos).shortVideoCommentReplyList.removeAll(commentAdapter.getData().get(replyDatas.parentPos).mReplyList);

                    commentAdapter.notifyItemChanged(replyDatas.parentPos);

                }

            }


        });
    }


    //???????????????????????????
    public void addMCommentData(CommentListBean commentListBean) {

        mCommentCount++;
        binding.commentCountTv.setText(mCommentCount + "?????????");
        commentAdapter.addData(0, commentListBean);
        //?????????????????????????????????item???parentPos???????????????
        //        commentAdapter.notifyItemChanged(0);
        commentAdapter.notifyDataSetChanged();
        binding.commentList.scrollToPosition(0);


    }

    public void addMReplyData(CommentListBean commentListBean) {
        mCommentCount++;
        binding.commentCountTv.setText(mCommentCount + "?????????");


        CommentListBean adapterCommentBean = commentAdapter.getData().get(commentListBean.parentPos);

        adapterCommentBean.shortVideoCommentReplyList.add(0, commentListBean);
        adapterCommentBean.mReplyList.add(commentListBean);
        adapterCommentBean.commentCount++;
        adapterCommentBean.lodaState = 1;//??????  ????????????

        commentAdapter.notifyItemChanged(commentListBean.parentPos);
    }

    public void rushData() {
        page = 1;
        getCommentList();
    }


    public void addDataList(List<CommentListBean> dataList) {
        this.dataList.addAll(dataList);
    }

    public void setOverData(boolean overData) {
        isOverData = overData;
        //??????????????????,????????????
    }

    public void show(View Parent, String commentCount) {
        if (popupWindow == null) {
            init();
        }
        if (TextUtils.isEmpty(commentCount)) {
            commentCount = "0";
        }
        mCommentCount = Integer.parseInt(commentCount);
        binding.commentCountTv.setText(commentCount + "?????????");
        popupWindow.showAsDropDown(Parent, Gravity.BOTTOM, 0, 0);
    }


    public void hide() {
        popupWindow.dismiss();
    }


    public interface OnChildClick {
        //????????????[??????????????????????????????,?????????XXX ?????? XXX????????????]
        void toComment(CommentListBean iCommentBean);

        //????????????
        void toCommentReply(CommentListBean iCommentBean);

        //??????????????????
        void toCommentVideo(String videoId);

        //????????????
        void toLike(CommentListBean iCommentBean);

        //????????????
        void toUser(CommentListBean iCommentBean);

        void toOpenFeace(String videoId);
    }

    public MutableLiveData<List<CommentListBean>> commentList = new MutableLiveData<>();
    public MutableLiveData<CommentListBean> commentReplyList = new MutableLiveData<>();


    public void getCommentList() {
        LiveRepository.getInstance().getCommentList(commentList, page, videoId);
    }

    public void getCommentReplyList(CommentListBean commentListBean) {
        LiveRepository.getInstance().getCommentReplyList(commentListBean, commentReplyList);
    }

    public void setCommentLike(String shortVideoId, String commentId, boolean favoriteStatus, boolean notRoot) {
        LiveRepository.getInstance().toLikeComment(shortVideoId, commentId, favoriteStatus, notRoot);
    }
}
