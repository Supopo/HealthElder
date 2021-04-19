<?xml version="1.0"?>
<#import "root://activities/common/kotlin_macros.ftl" as kt>
<recipe>
    <@kt.addAllKotlinDependencies />

    <instantiate from="root/res/layout/fragment.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(fragmentLayout)}.xml" />

    <open file="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(fragmentLayout)}.xml" />

    <instantiate from="root/src/app_package/Fragment.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/fragment/${fragmentClass}.${ktOrJavaExt}" />
    <open file="${escapeXmlAttribute(srcOut)}/fragment/${fragmentClass}.${ktOrJavaExt}" />

    <instantiate from="root/src/app_package/ViewModel.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/viewModel/${viewModelClass}.${ktOrJavaExt}" />
    <open file="${escapeXmlAttribute(srcOut)}/viewModel/${viewModelClass}.${ktOrJavaExt}" />
	
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
