package com.youtell.backchat.api;

import android.os.Bundle;

public class IntegerArgumentHandler extends ArgumentHandler {
	public int value;
	private String name;
	
	public IntegerArgumentHandler(String name, Request r) {
		this.name = name;
		r.addArgumentHandler(this);
	}
	
	@Override
	public void addArguments(Bundle b) {
		b.putInt(name, value);
	}

	@Override
	public void inflateArguments(Bundle args) {
		value = args.getInt(name);
	}

}
