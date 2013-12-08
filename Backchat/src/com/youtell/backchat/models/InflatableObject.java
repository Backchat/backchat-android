package com.youtell.backchat.models;

import android.os.Bundle;

public interface InflatableObject {
	void serialize(Bundle b);
	void deserialize(Bundle b);
}
