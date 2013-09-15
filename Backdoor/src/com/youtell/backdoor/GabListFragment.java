package com.youtell.backdoor;

import com.youtell.backdoor.adapters.GabListAdapter;
import com.youtell.backdoor.dummy.DummyContent;
import com.youtell.backdoor.models.Gab;

public class GabListFragment extends ListAdapterCallbackFragment<GabListAdapter, Gab> {
	public interface Callbacks extends ListAdapterCallbackFragment.Callbacks<Gab> {}
	@Override
	protected GabListAdapter createAdapter() {
		// TODO Auto-generated method stub
		return new GabListAdapter(getActivity(), DummyContent.ITEMS);
	}
  

}
