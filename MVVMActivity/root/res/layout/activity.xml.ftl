<?xml version="1.0" encoding="utf-8"?>
<layout 
	xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:binding="http://schemas.android.com/apk/res-auto"
	xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="viewModel"
            type="${packageName}.viewModel.${viewModelClass}" />
    </data>
	<LinearLayout
		android:id="@+id/container"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:background="@color/pageColor"
		android:orientation="vertical">
		
	<#if useAndroidX && creatAdapter>
	
		<#if hasRefresh>
	    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
            android:id="@+id/srl_content"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
		</#if>
			
		 <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rv_content"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:overScrollMode="never" />
		<#if hasRefresh>
		</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
		</#if>
	</#if>
		
	</LinearLayout>
</layout>
