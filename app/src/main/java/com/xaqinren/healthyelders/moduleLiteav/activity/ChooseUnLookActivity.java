package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.loadmore.LoadMoreStatus;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.nostra13.dcloudimageloader.utils.L;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiteAvLookModeBinding;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUserAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvConstant;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseUnLookViewModel;
import com.xaqinren.healthyelders.widget.UnLookSearchLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.StringUtils;

/**
 * 选择不给谁看
 */
public class ChooseUnLookActivity extends BaseActivity<ActivityLiteAvLookModeBinding, ChooseUnLookViewModel> implements UnLookSearchLayout.OnSearchChangeListener, OnItemClickListener {

    private static final String TAG = "ChooseUnLookActivity";
    private List<LiteAvUserBean> liteSelAvUserBeans = new ArrayList<>();
    private List<LiteAvUserBean> liteAvUserBeans = new ArrayList<>();
    private List<LiteAvUserBean> searchUserBeans = new ArrayList<>();
    private List<LiteAvUserBean> filterUserBeans = new ArrayList<>();

    private ChooseUserAdapter userAdapter;
    //全部里面请求的ID,用于比较从搜索中选择了用户当前列表是否存在该用户
    private HashMap<String, String> allIdMap = new HashMap<>();
    //选择的id
    private HashMap<String, String> idMap = new HashMap<>();
    /**
     * 请求单一的数据，请求返回数量 == 0 时，切换请求模式
     */
    private String FRIEND = "FRIEND";//朋友
    private String FANS = "FANS";//粉丝
    private String ATTENTION = "FOLLOW";//关注的人
    private String STRANGER = "STRANGER";//陌生人
    private String All = "All";//陌生人
    private String currentRequestType = FRIEND;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean[] tagAdd = {false, false, false};
    private int[] filterCount = {0, 0, 0};
    private boolean isSearch = false;
    private boolean isFilter = false;

    LoadMoreStatus loadMoreStatus ;
    /**
     * 当前筛选
     */
    private String currentFilterPage = All;

    /**
     * 搜索用的
     */
    private int searchCurrentPage = 1;
    private int searchPageSize = 10;
    private BaseLoadMoreModule loadMoreModule;

    /**
     * 筛选用的
     */
    private int filterCurrentPage = 1;
    private int filterPageSize = 10;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lite_av_look_mode;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        List<LiteAvUserBean> temp = (List<LiteAvUserBean>) getIntent().getSerializableExtra(LiteAvConstant.UnLookList);
        initView();
        if (temp != null) {
            mergeList(temp);
        }
        viewModel.getUserList(currentPage, pageSize, currentRequestType);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this, a -> {
            dismissDialog();
        });
        viewModel.userList.observe(this, liteAvUserBeans -> {
            if (currentRequestType.equals(FRIEND)) {
                if (this.liteAvUserBeans.isEmpty() && !liteAvUserBeans.content.isEmpty() && !tagAdd[0]) {
                    LiteAvUserBean bean = new LiteAvUserBean();
                    bean.viewType = 1;
                    bean.setName(liteAvUserBeans.totalElements+"个好友");
                    filterCount[0] = liteAvUserBeans.totalElements;
                    this.liteAvUserBeans.add(bean);
                    tagAdd[0] = true;
                }
            } else if (currentRequestType.equals(FANS)) {
                if (!tagAdd[1] && (this.liteAvUserBeans.isEmpty() || this.liteAvUserBeans.get(this.liteAvUserBeans.size() - 1).viewType == 0) && !liteAvUserBeans.content.isEmpty()) {
                    LiteAvUserBean bean = new LiteAvUserBean();
                    bean.viewType = 1;
                    bean.setName(liteAvUserBeans.totalElements+"个粉丝");
                    this.liteAvUserBeans.add(bean);
                    tagAdd[1] = true;
                    filterCount[2] = liteAvUserBeans.totalElements;
                }
            } else {
                if (!tagAdd[2] && (this.liteAvUserBeans.isEmpty()
                        || this.liteAvUserBeans.get(this.liteAvUserBeans.size() - 1).viewType == 0) && !liteAvUserBeans.content.isEmpty()) {
                    LiteAvUserBean bean = new LiteAvUserBean();
                    bean.viewType = 1;
                    bean.setName(liteAvUserBeans.totalElements+"个关注");
                    this.liteAvUserBeans.add(bean);
                    tagAdd[2] = true;
                    filterCount[1] = liteAvUserBeans.totalElements;
                }
            }
            boolean loadEnd = false;
            if (liteAvUserBeans.last) {
                if (next()) {
                    //进去下一次请求
                    currentPage = 1;
                } else {
                    loadEnd = true;
                }
            }

            for (LiteAvUserBean bean : liteAvUserBeans.content) {
                allIdMap.put(bean.getRealId(), "");
            }

            this.liteAvUserBeans.addAll(liteAvUserBeans.content);

            userAdapter.setList(this.liteAvUserBeans);

            if (loadEnd) {
                loadMoreModule.loadMoreEnd(false);
            } else {
                loadMoreModule.loadMoreComplete();
                if (!liteAvUserBeans.last) {
                    currentPage++;
                }
            }

            loadMoreStatus = userAdapter.getLoadMoreModule().getLoadMoreStatus();

        });
        viewModel.searchUserList.observe(this, liteAvUserBeans -> {
            //搜索列表
            if (searchCurrentPage == 1)
                searchUserBeans.clear();
            searchUserBeans.addAll(liteAvUserBeans.content);

            userAdapter.setList(searchUserBeans);

            if (liteAvUserBeans.last) {
                loadMoreModule.loadMoreEnd(false);
            } else {
                loadMoreModule.loadMoreComplete();
                searchCurrentPage++;
            }
        });

        viewModel.filterList.observe(this, liteAvUserBeans -> {
            //筛选列表
            if (filterCurrentPage == 1)
                filterUserBeans.clear();
            filterUserBeans.addAll(liteAvUserBeans.content);

            userAdapter.setList(filterUserBeans);

            if (liteAvUserBeans.last) {
                loadMoreModule.loadMoreEnd(false);
            } else {
                loadMoreModule.loadMoreComplete();
                filterCurrentPage++;
            }

            if (currentFilterPage.equals(FRIEND)) {
                filterCount[0] = liteAvUserBeans.totalElements;
            } else if (currentFilterPage.equals(ATTENTION)) {
                filterCount[1] = liteAvUserBeans.totalElements;
            } else if (currentFilterPage.equals(FANS)) {
                filterCount[2] = liteAvUserBeans.totalElements;
            }

        });
    }

    private boolean next() {
        if (currentRequestType.equals(FRIEND)) {
            currentRequestType = FANS;
            return true;
        } else if (currentRequestType.equals(FANS)) {
            currentRequestType = ATTENTION;
            return true;
        } else {
            currentRequestType = ATTENTION;
            return false;
        }
    }

    /**
     * @param temp
     */
    private void mergeList(List<LiteAvUserBean> temp) {
        for (int i = 0; i < temp.size(); i++) {
            LiteAvUserBean bean = temp.get(i);
            liteSelAvUserBeans.add(bean);
            addId(bean.getRealId());
        }
        binding.confirmButton.setEnabled(!liteSelAvUserBeans.isEmpty());


        binding.searchBar.addData(liteSelAvUserBeans);
    }

    private void mergeData(List<LiteAvUserBean> lite) {
        for (LiteAvUserBean bean : liteSelAvUserBeans) {
            for (LiteAvUserBean user : lite) {
                if (user.viewType == 1) continue;
                if (user.getRealId().equals(bean.getRealId())) {
                    user.isSel = true;
                }
            }
        }
    }

    private void mergerSearch(List<LiteAvUserBean> temp) {

    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        userAdapter = new ChooseUserAdapter();
        binding.friendList.setLayoutManager(new LinearLayoutManager(this));
        binding.friendList.setAdapter(userAdapter);
        userAdapter.setList(liteAvUserBeans);
        userAdapter.setOnItemClickListener(this);
        userAdapter.setAnimationEnable(false);
        userAdapter.setAnimationFirstOnly(true);
        binding.searchBar.setOnSearchChangeListener(this);

        binding.confirmButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("list", (Serializable) liteSelAvUserBeans);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        loadMoreModule = userAdapter.getLoadMoreModule();
        loadMoreModule.setAutoLoadMore(true);
        loadMoreModule.setOnLoadMoreListener(() ->
                {
                    if (!isSearch) {
                        if (currentFilterPage.equals(All)) {
                            viewModel.getUserList(currentPage, pageSize, currentRequestType);
                        } else if (currentFilterPage.equals(FRIEND)) {
                            viewModel.filterUserList(filterCurrentPage, filterPageSize, FRIEND);
                        } else if (currentFilterPage.equals(ATTENTION)) {
                            viewModel.filterUserList(filterCurrentPage, filterPageSize, ATTENTION);
                        } else if (currentFilterPage.equals(FANS)) {
                            viewModel.filterUserList(filterCurrentPage, filterPageSize, FANS);
                        }
                    } else {
                        viewModel.searchUserList(searchCurrentPage, searchPageSize, currentSearch);
                    }
                }
        );
        binding.allUser.setSelected(true);
        binding.allUser.setOnClickListener(v -> {
            if (currentFilterPage.equals(All)) {
                return;
            }
            unSelAllTag();
            v.setSelected(true);
            userAdapter.setList(liteAvUserBeans);
            currentFilterPage = All;
            isFilter = false;
            if (loadMoreStatus == LoadMoreStatus.Complete) {
                loadMoreModule.loadMoreComplete();
            } else if (loadMoreStatus == LoadMoreStatus.End) {
                loadMoreModule.loadMoreEnd(false);
            }
        });
        binding.justFriend.setOnClickListener(v -> {
            if (currentFilterPage.equals(FRIEND)) {
                return;
            }
            unSelAllTag();
            v.setSelected(true);
            filterUserBeans.clear();
            currentFilterPage = FRIEND;
            filterCurrentPage = 1;
            isFilter = true;
            userAdapter.setList(filterUserBeans);
            viewModel.filterUserList(filterCurrentPage, filterPageSize, FRIEND);

        });
        binding.justLook.setOnClickListener(v -> {
            if (currentFilterPage.equals(ATTENTION)) {
                return;
            }
            unSelAllTag();
            v.setSelected(true);
            filterUserBeans.clear();
            currentFilterPage = ATTENTION;
            filterCurrentPage = 1;
            isFilter = true;
            userAdapter.setList(filterUserBeans);
            viewModel.filterUserList(filterCurrentPage, filterPageSize, ATTENTION);

        });
        binding.justFans.setOnClickListener(v -> {
            if (currentFilterPage.equals(FANS)) {
                return;
            }
            unSelAllTag();
            v.setSelected(true);
            filterUserBeans.clear();
            currentFilterPage = FANS;
            filterCurrentPage = 1;
            isFilter = true;
            userAdapter.setList(filterUserBeans);
            viewModel.filterUserList(filterCurrentPage, filterPageSize, FANS);
        });
        binding.close.setOnClickListener(v -> finish());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.activity_bottom_2exit);
    }

    private String currentSearch = "";

    private void unSelAllTag() {
        binding.allUser.setSelected(false);
        binding.justFans.setSelected(false);
        binding.justFriend.setSelected(false);
        binding.justLook.setSelected(false);
    }

    @Override
    public void onTextChange(String text) {
        searchCurrentPage = 1;
        currentSearch = text;
        if (!StringUtils.isEmpty(text)) {
            isSearch = true;
            searchUserBeans.clear();
            userAdapter.setList(searchUserBeans);
            binding.tagLayout.setVisibility(View.GONE);
            viewModel.searchUserList(searchCurrentPage, searchPageSize, text);
        } else {
            binding.tagLayout.setVisibility(View.VISIBLE);
            isSearch = false;
            if (isFilter) {
                fillingFilter();
            }else{
                fillingUserBean();
            }
        }
    }

    private void fillingUserBean() {
        userAdapter.setList(liteAvUserBeans);
        if (loadMoreStatus == LoadMoreStatus.Complete) {
            loadMoreModule.loadMoreComplete();
        } else if (loadMoreStatus == LoadMoreStatus.End) {
            loadMoreModule.loadMoreEnd(false);
        }
    }

    private void fillingFilter() {
        userAdapter.setList(filterUserBeans);
        boolean end = false;
        if (currentFilterPage.equals(FRIEND)) {
            if (filterUserBeans.size() == filterCount[0]){
                end = true;
            }
        } else if (currentFilterPage.equals(ATTENTION)) {
            if (filterUserBeans.size() == filterCount[1]){
                end = true;
            }
        } else if (currentFilterPage.equals(FANS)) {
            if (filterUserBeans.size() == filterCount[2]){
                end = true;
            }
        }
        if (end) {
            loadMoreModule.loadMoreEnd(false);
        }else{
            loadMoreModule.loadMoreComplete();
        }
    }

    @Override
    public void onUserRemove(String uid) {
        //通知列表刷新选中状态
        for (int i = 0; i < liteSelAvUserBeans.size(); i++) {
            String id = liteSelAvUserBeans.get(i).getRealId();
            if (id.equals(uid)) {
                liteSelAvUserBeans.remove(i);
                break;
            }
        }
        removeId(uid);
        selCurrentPage();
        binding.confirmButton.setEnabled(!liteSelAvUserBeans.isEmpty());
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        LiteAvUserBean liteAvUserBean;
        if (isSearch) {
            liteAvUserBean  = searchUserBeans.get(position);
        } else if(isFilter){
            liteAvUserBean = filterUserBeans.get(position);
        }else{
            liteAvUserBean  = liteAvUserBeans.get(position);
        }

        if (isSearch) {
            //搜索列表点击
            if (!liteAvUserBean.isSel) {
                isSearch = false;
                if (!allIdMap.containsKey(liteAvUserBean.getRealId())
                && !liteAvUserBean.isRelated()) {
                    liteAvUserBeans.add(0, liteAvUserBean);
                }
                addItem(liteAvUserBean);
                //选择需要切换数据源
                if (!isFilter) {
                    fillingUserBean();
                }else{
                    fillingFilter();
                }
                searchUserBeans.clear();
            }else{
                removeItem(liteAvUserBean);
            }
            selCurrentPage();
        }else if (isFilter) {
            if (!liteAvUserBean.isSel) {
               addItem(liteAvUserBean);
                //选择需要切换数据源
            }else{
                removeItem(liteAvUserBean);
            }
            selCurrentPage();
        }else {
            if (liteAvUserBean.viewType == 0) {
                liteAvUserBean.isSel = !liteAvUserBean.isSel;
                if (liteAvUserBean.isSel) {
                    addItem(liteAvUserBean);
                } else {
                    removeItem(liteAvUserBean);
                }
                selCurrentPage();
            }
            userAdapter.setData(position, liteAvUserBean);
        }
        binding.confirmButton.setEnabled(!liteSelAvUserBeans.isEmpty());
    }

    private void addItem(LiteAvUserBean liteAvUserBean) {
        allIdMap.put(liteAvUserBean.getRealId(), "");
        liteAvUserBean.isSel = true;
        addId(liteAvUserBean.getRealId());
        liteSelAvUserBeans.add(liteAvUserBean);
        binding.searchBar.addData(liteAvUserBean);
    }

    private void removeItem(LiteAvUserBean liteAvUserBean){
        liteAvUserBean.isSel = false;
        removeId(liteAvUserBean.getRealId());
        for (int i = 0; i < liteSelAvUserBeans.size(); i++) {
            String realId = liteSelAvUserBeans.get(i).getRealId();
            if (realId.equals(liteAvUserBean.getRealId())) {
                liteSelAvUserBeans.remove(i);
            }
        }
        binding.searchBar.removeUser(liteAvUserBean.getRealId());
    }

    public void selCurrentPage() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.friendList.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        if (firstVisibleItemPosition > 0) {
            firstVisibleItemPosition--;
        }
        if (lastVisibleItemPosition < userAdapter.getData().size()) {
            lastVisibleItemPosition++;
        }
        userAdapter.notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition - firstVisibleItemPosition);
    }

    public void addId(String id) {
        idMap.put(id, "");
        userAdapter.addId(id);
    }

    public void removeId(String id) {
        if (idMap.containsKey(id)) {
            idMap.remove(id);
        }
        userAdapter.removeId(id);
    }

}
