package com.taghawk.base;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.taghawk.model.FailureResponse;

public abstract class RichMediatorLiveData<T> extends MediatorLiveData<T> {

    private MutableLiveData<Throwable> errorLiveData;
    private MutableLiveData<FailureResponse> failureResponseLiveData;

    private void initLiveData() {
        errorLiveData = new MutableLiveData<>();
        failureResponseLiveData = new MutableLiveData<>();
    }

    protected abstract Observer<FailureResponse> getFailureObserver();

    protected abstract Observer<Throwable> getErrorObserver();

    @Override
    protected void onInactive() {
        super.onInactive();
        removeSource(failureResponseLiveData);
        removeSource(errorLiveData);
    }

    @Override
    protected void onActive() {
        super.onActive();
        initLiveData();
        addSource(failureResponseLiveData, getFailureObserver());
        addSource(errorLiveData, getErrorObserver());
    }

    public void setFailure(FailureResponse failureResponse) {
        failureResponseLiveData.setValue(failureResponse);
    }

    public void setError(Throwable t) {
        errorLiveData.setValue(t);
    }
}
