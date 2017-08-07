package com.cofco.MonaSpider.util;

/**
 * 处理状态枚举
 * 
 * @author mona
 *
 */
public enum TypeEnum {
	DOWNLOAD("下载");

	private final String typeName;

	private TypeEnum(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}
}
