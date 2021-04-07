package com.zb.module_camera.utils;



import android.util.ArrayMap;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author 郭翰林
 * @date 2019/3/4 0004 11:13
 * 注释:
 */
public class SizeMap {

    private final ArrayMap<AspectRatio, SortedSet<Size>> mRatios = new ArrayMap<>();

    public boolean add(Size size) {
        for (AspectRatio ratio : mRatios.keySet()) {
            if (ratio.matches(size)) {
                final SortedSet<Size> sizes = mRatios.get(ratio);
                assert sizes != null;
                if (sizes.contains(size)) {
                    return false;
                } else {
                    sizes.add(size);
                    return true;
                }
            }
        }
        // None of the existing ratio matches the provided size; add a new key
        SortedSet<Size> sizes = new TreeSet<>();
        sizes.add(size);
        mRatios.put(AspectRatio.of(size.getWidth(), size.getHeight()), sizes);
        return true;
    }

    /**
     * Removes the specified aspect ratio and all sizes associated with it.
     *
     * @param ratio The aspect ratio to be removed.
     */
    public void remove(AspectRatio ratio) {
        mRatios.remove(ratio);
    }

    Set<AspectRatio> ratios() {
        return mRatios.keySet();
    }

    SortedSet<Size> sizes(AspectRatio ratio) {
        return mRatios.get(ratio);
    }

    void clear() {
        mRatios.clear();
    }

    boolean isEmpty() {
        return mRatios.isEmpty();
    }

}
