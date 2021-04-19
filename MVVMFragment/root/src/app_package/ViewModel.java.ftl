package ${packageName}.viewModel;

   <#if useAndroidX>
        import androidx.annotation.NonNull;
    </#if>
public class ${viewModelClass} extends BaseViewModel {
	<#if creatAdapter>
    public MutableLiveData<List<${beanClass}>> dataList = new MutableLiveData<>();
	<#if hasLoadMore>
    public MutableLiveData<Integer> loadStatus = new MutableLiveData<>();
    </#if>
	</#if>
   
	public  ${viewModelClass}(@NonNull Application application) {
        super(application);
    }
	
}
