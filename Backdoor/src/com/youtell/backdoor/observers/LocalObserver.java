package com.youtell.backdoor.observers;

abstract public class LocalObserver<ObserverType extends LocalObserver.Observer> extends BaseLocalObserver<ObserverType> {
	public LocalObserver(ObserverType observer) {
		super(observer);
	}

	public interface Observer {
		public void refresh();
	}
	
	@Override
	public void startListening() {
		this.observer.refresh();
		super.startListening();
	}
	
	public void startListening(int priority) {
		this.observer.refresh();
		super.startListening(priority);
	}
}
