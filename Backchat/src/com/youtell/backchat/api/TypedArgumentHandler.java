package com.youtell.backchat.api;

import com.youtell.backchat.models.DatabaseObject;
import com.youtell.backchat.models.Gab;

import android.os.Bundle;
import android.util.Log;

public class TypedArgumentHandler<T extends DatabaseObject> extends ArgumentHandler { 
	public T object;
	private Class<? extends DatabaseObject> clazz;
	
	public TypedArgumentHandler(Class<T> clazz, Request owner) {
		this.clazz = clazz;
		owner.addArgumentHandler(this);
	}
	
	public void setObject(T object) {
		this.object = object;
	}
	
	public void setObjectByID(int id) {
		getObject(id);
	}

	@Override
	public void addArguments(Bundle b) {
		b.putInt(this.clazz.getName(), object.getID());
	}

	@SuppressWarnings("unchecked")
	private void getObject(int id) {
		try {
			object = (T) clazz.getMethod("getByID", int.class).invoke(null, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(this.getClass().getName(), "getObject failed", e);
			object = null;
		}
	}
	
	@Override
	public void inflateArguments(Bundle args) {
		int id = args.getInt(this.clazz.getName());
		getObject(id);
		object.refresh();
	}

}
