package com.emq;

import java.util.HashMap;
import java.util.Map;

public class Global {

	// 下拉框全部
	public static Map IS_ALL = new HashMap();

	// 下拉框全部请选择
	public static Map HAS_SELECT = new HashMap();

	static {

		// 是否有全部属性设置

		IS_ALL.put("code", "all");
		IS_ALL.put("text", "-全部-");

		// 是否包含请选择属性设置

		HAS_SELECT.put("code", "");
		HAS_SELECT.put("text", "-请选择-");
	}

}