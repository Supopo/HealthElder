package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.cos.xml.model.tag.CopyObject;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUnLookAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;

import java.util.LinkedList;

public class UnLookSearchLayout extends FrameLayout implements SearchEditText.OnTextChangeListener, OnItemClickListener {
    private String TAG = "UnLookSearchLayout";
    private RecyclerView recyclerView;
    private SearchEditText editText;
    private LinkedList<LiteAvUserBean> liteAvUserBeans;
    private float editMinWidth;
    private float recyclerMaxWidth;
    private float recyclerItemSize;
    private ChooseUnLookAdapter adapter;
    private OnSearchChangeListener onSearchChangeListener;

    private float layoutWidth;
    private boolean isResize;

    public void setOnSearchChangeListener(OnSearchChangeListener onSearchChangeListener) {
        this.onSearchChangeListener = onSearchChangeListener;
    }

    public UnLookSearchLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public UnLookSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public UnLookSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_un_look_search_bar, this);
        recyclerView = findViewById(R.id.avatar_list);
        editText = findViewById(R.id.search_view);
        editMinWidth = getResources().getDimension(R.dimen.dp_100);
        adapter = new ChooseUnLookAdapter(R.layout.item_un_look_user);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        editText.setOnTextChangeListener(this);
        editMinWidth = getResources().getDimension(R.dimen.dp_100);
        recyclerItemSize = getResources().getDimension(R.dimen.dp_44);
        liteAvUserBeans = new LinkedList<>();
        adapter.setAnimationEnable(true);
        adapter.setAnimationFirstOnly(true);
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn);

        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (layoutWidth != 0 && !isResize) {
            isResize = true;
            resize();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 选中用户添加到表中
     * @param liteAvUserBean
     */
    public void addData(LiteAvUserBean liteAvUserBean) {
        if (this.liteAvUserBeans == null) {
            this.liteAvUserBeans = new LinkedList<>();
        }
        this.liteAvUserBeans.add(liteAvUserBean);
        adapter.addData(liteAvUserBean);
        //需要通知edittext添加
        editText.addUser();
        resize();
        recyclerView.scrollBy(Integer.MAX_VALUE, 0);
    }

    public void removeUser() {
        //edittext 按下 删除键,移除最后一条用户
        if (liteAvUserBeans.isEmpty())return;
        LiteAvUserBean bean = liteAvUserBeans.removeLast();
        adapter.remove(bean);
        //需要通知edittext移除
        editText.removeUser();
        onSearchChangeListener.onUserRemove(bean.id);
        resize();
    }
    public void removeUser(int id) {
        if (liteAvUserBeans.isEmpty())return;
        for (LiteAvUserBean liteAvUserBean : liteAvUserBeans) {
            if (liteAvUserBean.id == id) {
                adapter.remove(liteAvUserBean);
                liteAvUserBeans.remove(liteAvUserBean);
                editText.removeUser();
                resize();
                return;
            }
        }
    }

    @Override
    public void onTextChange(String text) {
        //告诉activity，需要搜索内容了
        onSearchChangeListener.onTextChange(text);
    }

    @Override
    public void onDeleteUser() {
        removeUser();
    }
    //添加或移除后需要重设左右空间宽度
    private void resize() {
        if (layoutWidth == 0) {
            return;
        }

        if (recyclerMaxWidth == 0) {
            recyclerMaxWidth = layoutWidth - editMinWidth;
        }
        if (liteAvUserBeans.size() == 0) {
            ViewGroup.LayoutParams p = recyclerView.getLayoutParams();
            p.width = 0;
            recyclerView.setLayoutParams(p);

            ViewGroup.LayoutParams p1 = editText.getLayoutParams();
            p1.width = (int) layoutWidth;
            editText.setLayoutParams(p1);
        }else{
            int cWidth = (int) (recyclerItemSize * liteAvUserBeans.size());
            if (cWidth > recyclerMaxWidth) {
                cWidth = (int) recyclerMaxWidth;
            }
            ViewGroup.LayoutParams p = recyclerView.getLayoutParams();
            p.width = cWidth;
            recyclerView.setLayoutParams(p);

            ViewGroup.LayoutParams p1 = editText.getLayoutParams();
            p1.width = (int) (layoutWidth - cWidth);
            editText.setLayoutParams(p1);
        }
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        LiteAvUserBean bean = liteAvUserBeans.remove(position);
        adapter.removeAt(position);
        editText.removeUser();
        onSearchChangeListener.onUserRemove(bean.id);
        resize();
    }



    public interface OnSearchChangeListener{
        void onTextChange(String text);
        void onUserRemove(int uId);
    }
}
