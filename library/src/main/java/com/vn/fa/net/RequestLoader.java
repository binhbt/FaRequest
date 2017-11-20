package com.vn.fa.net;


import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by binhbt on 6/20/2016.
 */
public class RequestLoader {
    public interface OnCancelRequest{
        public void onCancel();
    }
    public interface CallBack<T>{
        public void onStart();
        public void onError(Throwable t);
        public void onFinish(T result);
    }
    static volatile RequestLoader singleton = null;
    private Map<Object, CompositeDisposable > mapRequest = new HashMap<>();
    private Map<Object, CompositeDisposable> containerRequest = new HashMap<>();
    public static RequestLoader getDefault() {
        if (singleton == null) {
            synchronized (RequestLoader.class) {
                if (singleton == null) {
                    singleton = new RequestLoader();
                }
            }
        }
        return singleton;
    }
    public void add(Disposable subcribtion, Object tag){
        CompositeDisposable  compositeSubscription = mapRequest.get(tag);
        if (compositeSubscription == null){
            compositeSubscription = new CompositeDisposable ();
        }
        compositeSubscription.add(subcribtion);
        mapRequest.put(tag, compositeSubscription);
    }

    public void cancelByTag(Object tag){
        CompositeDisposable  compositeSubscription = mapRequest.get(tag);
        if (compositeSubscription != null){
            compositeSubscription.dispose();
            mapRequest.remove(tag);
        }else{
            //ignore it. i don't care
            //throw new IllegalArgumentException("Request not found. Can not cancel");
        }
    }
    public void addToContainerGroup(Disposable subcribtion, Object container){
        CompositeDisposable  compositeSubscription = containerRequest.get(container);
        if (compositeSubscription == null){
            compositeSubscription = new CompositeDisposable ();
        }
        compositeSubscription.add(subcribtion);
        containerRequest.put(container, compositeSubscription);
    }
    public void cancelAll(Object container){
        CompositeDisposable  compositeSubscription = containerRequest.get(container);
        if (compositeSubscription != null){
            compositeSubscription.dispose();
            containerRequest.remove(container);
        }else{
            //ignore it. i don't care
            //throw new IllegalArgumentException("Request not found. Can not cancel");
        }
    }
    public static final class Builder {
        public static final String DEFAULT_TAG = "VEGA_REQUEST";
        private OnCancelRequest cancel;
        private CallBack callback;
        private Object container;
        private Observable<?> observable;
        private Object tag = DEFAULT_TAG;
        public Builder cancel(OnCancelRequest cancel){
            this.cancel = cancel;
            return this;
        }
        public Builder callback(CallBack callback){
            this.callback = callback;
            return this;
        }
        public <C> Builder container(C container){
            this.container = container;
            return this;
        }
        public <T> Builder api(Observable<T> observable){
            this.observable = observable;
            return this;
        }
        public Builder tag(String tag){
            this.tag = tag;
            return this;
        }
        public Disposable build(){
            if (callback == null){
                callback = new CallBack() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onFinish(Object result) {

                    }
                };
            }
            if (cancel == null){
                cancel = new OnCancelRequest() {
                    @Override
                    public void onCancel() {

                    }
                };
            }
            //Onstart
            callback.onStart();
            Disposable sub = observable.subscribeOn(Schedulers.io())
                    // Observe result in the main thread to be able to update UI
                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnUnsubscribe(new Action0() {
//                        @Override
//                        public void call() {
//                            cancel.onCancel();
//                        }
//                    })
                    .subscribe((data) -> {
                        callback.onFinish(data);
                    },(error) -> {
                        callback.onError(error);
                    });
            if (tag != null)
            RequestLoader.getDefault().add(sub, tag);
            if (container != null)
            RequestLoader.getDefault().addToContainerGroup(sub, container);
            return sub;
        }
    }
}
