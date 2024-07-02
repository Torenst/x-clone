package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;

public class PagedHandler <T> extends BackgroundTaskHandler<PagedItemObserver<T>> {

    public PagedHandler(PagedItemObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedItemObserver<T> observer, Bundle data) {
        List<T> statuses = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(statuses, hasMorePages);
    }
}
