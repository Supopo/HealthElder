package ${packageName}.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import ${packageName}.R;
import ${packageName}.bean.${beanClass};


public class ${adapterClass} extends BaseQuickAdapter<${beanClass}, BaseViewHolder> implements LoadMoreModule {
    public ${adapterClass}(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ${beanClass} item) {

		//注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemXxxBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }
	
	//局部刷新用的
	 @Override
    protected void convert(BaseViewHolder helper, ${beanClass} item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
        
        }
    }
}
