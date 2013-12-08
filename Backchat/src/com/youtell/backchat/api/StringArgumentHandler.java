package com.youtell.backchat.api;

import android.os.Bundle;

public class StringArgumentHandler extends ArgumentHandler {
	public String content;
	private String name;
	
	public StringArgumentHandler(String name, Request r) {
		this.name = name;
		r.addArgumentHandler(this);
	}
	
	@Override
	public void addArguments(Bundle b) {
		b.putString(name, content);
	}

	@Override
	public void inflateArguments(Bundle args) {
		content = args.getString(name);
	}

}
