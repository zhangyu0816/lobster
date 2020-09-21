package com.zb.lib_base.utils.water;

/**
 * Created by karan on 13/2/15.
 */
public interface CompressListener {
    void onExecSuccess(String message);

    void onExecFail(String reason);

    void onExecProgress(String message);
}
