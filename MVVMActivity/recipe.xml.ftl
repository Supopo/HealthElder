<?xml version="1.0"?>
<recipe>

	<merge from="root/AndroidManifest.xml.ftl"             
			to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />

    <instantiate from="root/res/layout/activity.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(activityLayout)}.xml" />
    <open file="${escapeXmlAttribute(resOut)}/layout/${activityLayout}.xml" />

    <instantiate from="root/src/app_package/Activity.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/activity/${activityClass}.${ktOrJavaExt}" />
    <open file="${escapeXmlAttribute(srcOut)}/activity/${activityClass}.${ktOrJavaExt}" />
	
	<instantiate from="root/src/app_package/ViewModel.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/viewModel/${viewModelClass}.${ktOrJavaExt}" />
    <open file="${escapeXmlAttribute(srcOut)}/viewModel/${viewModelClass}.${ktOrJavaExt}"/>


<#if creatAdapter && creatBean>
	<instantiate from="root/src/app_package/Bean.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/bean/${beanClass}.${ktOrJavaExt}" />
    <open file="${escapeXmlAttribute(srcOut)}/bean/${beanClass}.${ktOrJavaExt}"/>
</#if>


<#if creatAdapter>
	<instantiate from="root/src/app_package/Adapter.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/adapter/${adapterClass}.${ktOrJavaExt}" />
    <open file="${escapeXmlAttribute(srcOut)}/adapter/${adapterClass}.${ktOrJavaExt}"/>
</#if>

<#if creatAdapter>
    <instantiate from="root/res/layout/item.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(itemLayout)}.xml" />
    <open file="${escapeXmlAttribute(resOut)}/layout/${itemLayout}.xml" />
</#if>
	
</recipe>
