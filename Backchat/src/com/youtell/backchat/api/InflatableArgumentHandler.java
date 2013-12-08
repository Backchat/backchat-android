package com.youtell.backchat.api;

import java.lang.reflect.InvocationTargetException;

import com.youtell.backchat.models.InflatableObject;

import android.os.Bundle;
import android.util.Log;

public class InflatableArgumentHandler<T extends InflatableObject> extends ArgumentHandler {
	public T object;
	private Class<T> clazz;
	
	public InflatableArgumentHandler(Class<T> clazz, Request owner) {
		this.clazz = clazz;
		owner.addArgumentHandler(this);
	}
	
	public void setObject(T object) {
		this.object = object;
	}
	
	@Override
	public void addArguments(Bundle b) {
		Bundle inner = new Bundle();
		object.serialize(inner);
		b.putBundle(this.clazz.getName(), inner);
	}
	
	@Override
	public void inflateArguments(Bundle args) {
		Bundle inner = args.getBundle(this.clazz.getName());
		try {
			object = this.clazz.getConstructor().newInstance();
			object.deserialize(inner);
		} catch (Exception e) {
			Log.e("inflate", "failed", e);
		}
	}
}
