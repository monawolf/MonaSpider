package com.cofco.MonaSpider.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class ListHashMap<K, V> extends HashMap<K, List<V>> {
	public List<V> getNotNull(K key) {
		List<V> values = super.get(key);
		return values == null ? Collections.emptyList() : values;
	}
	
	public void addValue(K key, V value) {
		List<V> values = get(key);
		if (values == null) {
			values = new ArrayList<>();
			put(key, values);
		}
		
		values.add(value);
	}
	
	public void removeValue(K key, V value) {
		List<V> values = get(key);
		if (values != null) {
			values.remove(value);
		}
	}
	
	public boolean containsValue(K key, V value) {
		List<V> values = get(key);
		return values != null && values.contains(value);
	}
}


