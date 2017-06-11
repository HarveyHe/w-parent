package com.harvey.w.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;

public class SortOrderUtils {

    public static <T extends Ordered> Collection<T> sort(Collection<T> source) {
    	if(source == null || source.isEmpty()){
    		return source;
    	}
    	List<T> list = source instanceof List<?> ? (List<T>)source : new ArrayList<T>(source);
    	OrderComparator.sort(list);
    	return list;
    }
    
    public static <T extends Ordered> void sort(List<T> source) {
    	if(source == null){
    		return;
    	}
    	OrderComparator.sort(source);
    }
}
