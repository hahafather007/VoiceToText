package com.hahafather007.voicetotext.utils;

import android.databinding.Observable.OnPropertyChangedCallback;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.databinding.ObservableList.OnListChangedCallback;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Supplier;

import java.util.List;

import io.reactivex.Observable;

public class RxField {
    private static <T> Observable<T> from(android.databinding.Observable observable, Supplier<T> getter) {
        return Observable.create(emitter -> {
            emitter.onNext(getter.get());

            final OnPropertyChangedCallback callback = new OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(getter.get());
                }
            };
            emitter.setCancellable(() -> observable.removeOnPropertyChangedCallback(callback));
            observable.addOnPropertyChangedCallback(callback);
        });
    }

    public static <T> Observable<T> of(ObservableField<T> observable) {
        return from(observable, observable::get);
    }

    public static <T> Observable<Optional<T>> ofNullable(ObservableField<T> observable) {
        return from(observable, () -> Optional.ofNullable(observable.get()));
    }

    public static <T> Observable<T> ofNonNull(ObservableField<T> observable) {
        return ofNullable(observable)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public static Observable<Boolean> of(ObservableBoolean observable) {
        return from(observable, observable::get);
    }

    public static Observable<Integer> of(ObservableInt observable) {
        return from(observable, observable::get);
    }

    public static <T> Observable<List<T>> of(ObservableList<T> observable) {
        return Observable.create(emitter -> {
            emitter.onNext(observable);

            final OnListChangedCallback<ObservableList<T>> callback =
                    new ObservableListCallback<>(emitter::onNext);
            emitter.setCancellable(() -> observable.removeOnListChangedCallback(callback));
            observable.addOnListChangedCallback(callback);
        });
    }
}
