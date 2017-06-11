package com.harvey.w.core.aop;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

public class InterceptionDescriptors extends ArrayList<InterceptionDescriptor> {

	public static final InterceptionDescriptors EMPTY_LIST = new InterceptionDescriptors(0);
	
	
	public InterceptionDescriptors() {
		super();
	}

	public InterceptionDescriptors(Collection<? extends InterceptionDescriptor> c) {
		super(c);
	}

	public InterceptionDescriptors(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean hasCondition = false;

	public boolean isHasCondition() {
		return hasCondition;
	}

	@Override
	public boolean add(InterceptionDescriptor e) {
		hasCondition |= StringUtils.isNotEmpty(e.getCondition());
		return super.add(e);
	}

	@Override
	public void add(int index, InterceptionDescriptor element) {
		hasCondition |= StringUtils.isNotEmpty(element.getCondition());
		super.add(index, element);
	}

}
