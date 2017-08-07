package com.cofco.MonaSpider.event;

/**
 * 事件监听器接口
 */
public interface EventListener<E> {
	void onEvent(E event);
}
