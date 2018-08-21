package com.hahafather007.voicetotext.common;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.annimon.stream.IntPair;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import java.util.List;


import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

public class Binding {
    @BindingAdapter("visible")
    public static void setVisibility(View view, boolean b) {
        if (b) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("items")
    public static void setItems(RecyclerView view, List items) {
        final MultiTypeAdapter adapter = getOrCreateAdapter(view);
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    private static MultiTypeAdapter getOrCreateAdapter(RecyclerView view) {
        if (!(view.getAdapter() instanceof MultiTypeAdapter))
            view.setAdapter(new MultiTypeAdapter());

        return (MultiTypeAdapter) view.getAdapter();
    }

    @BindingAdapter({"itemLayout", "onBindItem"})
    public static void setAdapter(RecyclerView view, int resId, DataBindingItemViewBinder.OnBindItem onBindItem) {
        final MultiTypeAdapter adapter = getOrCreateAdapter(view);
        //noinspection unchecked
        adapter.register(Object.class, new DataBindingItemViewBinder(resId, onBindItem));
    }

    @BindingAdapter({"linkers", "onBindItem"})
    public static void setAdapter(RecyclerView view, List<Linker> linkers, DataBindingItemViewBinder.OnBindItem
            onBindItem) {
        final MultiTypeAdapter adapter = getOrCreateAdapter(view);
        //noinspection unchecked
        final ItemViewBinder[] binders = Stream.of(linkers)
                .map(Linker::getLayoutId)
                .map(v -> new DataBindingItemViewBinder(v, onBindItem))
                .toArray(ItemViewBinder[]::new);
        //noinspection unchecked
        adapter.register(Object.class)
                .to(binders)
                .withLinker(o -> Stream.of(linkers)
                        .map(Linker::getMatcher)
                        .indexed()
                        .filter(v -> v.getSecond().apply(o))
                        .findFirst()
                        .map(IntPair::getFirst)
                        .orElse(0));
    }

    public static class Linker {
        private final Function<Object, Boolean> matcher;
        private final int layoutId;

        public static Linker of(Function<Object, Boolean> matcher, int layoutId) {
            return new Linker(matcher, layoutId);
        }

        Linker(Function<Object, Boolean> matcher, int layoutId) {
            this.matcher = matcher;
            this.layoutId = layoutId;
        }

        Function<Object, Boolean> getMatcher() {
            return matcher;
        }

        int getLayoutId() {
            return layoutId;
        }
    }
}
