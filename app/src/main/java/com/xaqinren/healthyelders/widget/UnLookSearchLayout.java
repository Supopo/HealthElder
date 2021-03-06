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
import java.util.List;

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
     * ???????????????????????????
     * @param liteAvUserBean
     */
    public void addData(LiteAvUserBean liteAvUserBean) {
        if (this.liteAvUserBeans == null) {
            this.liteAvUserBeans = new LinkedList<>();
        }
        this.liteAvUserBeans.add(liteAvUserBean);
        adapter.addData(liteAvUserBean);
        //????????????edittext??????
        editText.addUser(1);
        resize();
        recyclerView.scrollBy(Integer.MAX_VALUE, 0);
    }

    /**
     * ???????????????????????????
     * @param liteAvUserBean
     */
    public void addData(List<LiteAvUserBean> liteAvUserBean) {
        if (this.liteAvUserBeans == null) {
            this.liteAvUserBeans = new LinkedList<>();
        }

        this.liteAvUserBeans.addAll(liteAvUserBean);
        adapter.addData(liteAvUserBean);
        //????????????edittext??????
        if (liteAvUserBean.isEmpty())return;
            editText.addUser(liteAvUserBean.size());
        resize();
        recyclerView.scrollBy(Integer.MAX_VALUE, 0);
    }


    public void removeUser() {
        //edittext ?????? ?????????,????????????????????????
        if (liteAvUserBeans.isEmpty())return;
        LiteAvUserBean bean = liteAvUserBeans.removeLast();
        adapter.remove(bean);
        //????????????edittext??????
        editText.removeUser();
        onSearchChangeListener.onUserRemove(bean.getRealId());
        resize();
    }
    public void removeUser(String id) {
        if (liteAvUserBeans.isEmpty())return;
        for (LiteAvUserBean liteAvUserBean : liteAvUserBeans) {
            if (liteAvUserBean.getRealId() .equals( id)) {
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
        //??????activity????????????????????????
        onSearchChangeListener.onTextChange(text);
    }

    @Override
    public void onDeleteUser() {
        removeUser();
    }
    //????????????????????????????????????????????????
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
        onSearchChangeListener.onUserRemove(bean.getRealId());
        resize();
    }

    public interface OnSearchChangeListener{
        void onTextChange(String text);
        void onUserRemove(String uId);
    }
}
