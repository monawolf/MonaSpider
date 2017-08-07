package com.cofco.MonaSpider.event;

import java.util.List;

/**
 * 事件总线
 */
public class EventBus {
	private static final EventBus instance = new EventBus();
	
	public static EventBus getInstance() {
		return instance;
	}
	
	private EventBus() {}
	
	private ListHashMap<Class<?>, EventListener<?>> eventClassToEventListenersMap = new ListHashMap<>();
	
	public synchronized <E> void addEventListener(Class<E> eventClass, EventListener<E> listener) {
		eventClassToEventListenersMap.addValue(eventClass, listener);
	}
	
	public synchronized <E> void removeEventListener(Class<E> eventClass, EventListener<E> listener) {
		eventClassToEventListenersMap.removeValue(eventClass, listener);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <E> void fireEvent(E eventObject) {
		List<EventListener<?>> listeners = eventClassToEventListenersMap.get(eventObject.getClass());
		if (listeners != null) {
			int n = listeners.size();
			for (int i = 0; i < n; i++) {
				((EventListener<E>) listeners.get(i)).onEvent(eventObject);
			}
		}
	}
}
