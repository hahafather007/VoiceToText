package com.hahafather007.voicetotext.utils;

import android.databinding.ObservableList;

import com.annimon.stream.function.Consumer;

import java.util.List;

public class ObservableListCallback<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {
    private final Consumer<List<T>> callback;

    public ObservableListCallback(Consumer<List<T>> callback) {
        this.callback = callback;
    }

    @Override
    public void onChanged(ObservableList<T> sender) {
        callback.accept(sender);
    }

    @Override
    public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
        callback.accept(sender);
    }

    @Override
    public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
        callback.accept(sender);
    }

    @Override
    public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition,
                                 int itemCount) {
        callback.accept(sender);
    }

    @Override
    public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
        callback.accept(sender);
    }
}
