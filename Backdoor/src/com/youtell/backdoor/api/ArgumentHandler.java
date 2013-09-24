package com.youtell.backdoor.api;

import android.os.Bundle;

abstract public class ArgumentHandler {
	abstract public void addArguments(Bundle b);
	abstract public void inflateArguments(Bundle args);
}
