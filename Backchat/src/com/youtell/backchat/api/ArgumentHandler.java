package com.youtell.backchat.api;

import android.os.Bundle;

abstract public class ArgumentHandler {
	abstract public void addArguments(Bundle b);
	abstract public void inflateArguments(Bundle args);
}
