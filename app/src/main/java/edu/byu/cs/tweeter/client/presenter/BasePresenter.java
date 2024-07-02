package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;

public abstract class BasePresenter<T> {

    protected final T view;

    public interface BaseView {
        void displayFailureMessage(String message);
        void displayInfoMessage(String message);
        void displayExceptionMessage(String message, Exception exception);
        void clearInfoMessage();
    }

    public BasePresenter(T view){
        this.view = view;
    }

    // these will be implemented in the subclasses to handle their error messages.
    protected abstract void failureMessage(String failureType, String message);
    protected abstract void exceptionMessage(String exceptionType, Exception exception);

    // this gets implemented by the other observers so that they can overloaded the getDescription
    // and we then handle the errors and exceptions here.
    public abstract class Observer implements ServiceObserver {
        //To be overloaded and describe what the observer calling does.
        protected abstract String getDescription();

        @Override
        public void handleFailure(String message) {
            failureMessage(getDescription(), message);

        }

        @Override
        public void handleException(Exception exception) {
            exceptionMessage(getDescription(), exception);
        }
    }

}
