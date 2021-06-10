package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityWebBindingImpl;
import com.xaqinren.healthyelders.databinding.ActivityWithdrawBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.impl.TextWatcherImpl;
import com.xaqinren.healthyelders.moduleMine.adapter.BankListAdapter;
import com.xaqinren.healthyelders.moduleMine.bean.BankCardBean;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.WalletViewModel;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.widget.BottomDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 提现
 */
public class WithdrawActivity extends BaseActivity<ActivityWithdrawBinding, WalletViewModel> {
    private WalletBean walletBean;
    private BottomDialog bottomDialog;
    private BankCardBean bankCardBean;
    private Pattern patternAt = Pattern.compile("^([0-9]*)+(\\.[0-9]{1,2})?$");

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_withdraw;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("提现");
        binding.layoutCard.setOnClickListener(v -> {
            //选择银行卡
            showSelBankCardList();
        });
        binding.allAmount.setOnClickListener(v -> {
            binding.inputAmount.setText(walletBean.getWalletAccount().getAccountBalance() + "");
        });
        binding.confirm.setOnClickListener(v -> {
            if (bankCardBean == null) {
                ToastUtils.showShort("请选择提现银行卡");
                return;
            }
            String amount = binding.inputAmount.getText().toString();
            if (StringUtils.isEmpty(amount)) {
                ToastUtils.showShort("请输入提现金额");
                return;
            }
            double v1 = Double.parseDouble(amount);
            if (v1 <= 0) {
                ToastUtils.showShort("提现金额需大于0");
                return;
            }
            Matcher matcher = patternAt.matcher(amount);
            if (!matcher.find()) {
                ToastUtils.showShort("输入金额不合法");
                return;
            }
            showDialog();
            viewModel.getWithdraw(v1,bankCardBean.getAccountBank(),bankCardBean.getAccountName(),
                    bankCardBean.getAccountNo(),bankCardBean.getCardType());
        });
        binding.inputAmount.addTextChangedListener(new TextWatcherImpl(){
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                binding.confirm.setEnabled(editable.length() > 0);
            }
        });
        binding.serviceClause.setOnClickListener(v -> {
            //服务条款
            UniUtil.openUniApp(this, Constant.JKZL_MINI_APP_ID, Constant.MINI_WITHDRAW_SERVICE, null, true);
        });
        binding.inputAmount.setFocusable(true);
        binding.inputAmount.setFocusableInTouchMode(true);
        viewModel.getWalletInfo();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.request.observe(this,aBoolean -> {
            dismissDialog();
        });
        viewModel.wallet.observe(this, walletBean -> {
            this.walletBean = walletBean;
            binding.setData(this.walletBean);
            if (this.walletBean.getUserBankCardList() != null && !this.walletBean.getUserBankCardList().isEmpty()) {
                bankCardBean = walletBean.getUserBankCardList().get(0);
                bankCardBean.setSel(true);
                bindCardInfo();
            }
        });
        viewModel.appeal.observe(this, aBoolean -> {
            if (aBoolean) {
                //
                Bundle bundle = new Bundle();
                bundle.putString("amount",binding.inputAmount.getText().toString());
                bundle.putString("card",bankCardBean.getAccountNo());
                startActivity(WithdrawApplySuccess.class, bundle);
            } else {
                ToastUtils.showShort("提现失败");
            }
        });
    }

    private void bindCardInfo() {
        binding.cardAccount.setText(bankCardBean.getShortCardNo());
    }

    RecyclerView bankList;
    TextView confirm;
    private BankListAdapter bankListAdapter;
    private int currentIndex = 0;
    private void showSelBankCardList() {
        if (bottomDialog==null) {
            View bankView = View.inflate(this, R.layout.pop_sel_bank_card, null);
            bottomDialog = new BottomDialog(this, bankView, null);
            bankList = bankView.findViewById(R.id.card_list);
            confirm = bankView.findViewById(R.id.confirm);
            bankListAdapter = new BankListAdapter(R.layout.item_bank_card_sel);
            bankListAdapter.setList(walletBean.getUserBankCardList());
            bankList.setLayoutManager(new LinearLayoutManager(this));
            bankList.setAdapter(bankListAdapter);

            if (walletBean.getUserBankCardList() == null || walletBean.getUserBankCardList().isEmpty()) {
                confirm.setEnabled(false);
            }

            confirm.setOnClickListener(v -> {
                if (currentIndex < 0) {
                    return;
                }
                bankCardBean = bankListAdapter.getData().get(currentIndex);
                bottomDialog.dismiss();
                bindCardInfo();
            });
            bankListAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (currentIndex != -1) {
                    if (currentIndex == position) {
                        return;
                    }
                    bankListAdapter.getData().get(currentIndex).setSel(false);
                }
                currentIndex = position;
                bankListAdapter.getData().get(currentIndex).setSel(true);
                bankListAdapter.notifyDataSetChanged();
            });
        }
        bottomDialog.show();
    }
}
