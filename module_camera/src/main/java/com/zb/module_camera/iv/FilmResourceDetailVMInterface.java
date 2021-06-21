package com.zb.module_camera.iv;

import android.view.View;

import com.zb.lib_base.model.FilmComment;

public interface FilmResourceDetailVMInterface {
    void doLike(View view);

    void cameraDoLike();

    void cameraCancelLike();

    void doComment(View view);

    void toUserDetail(long otherUserId);

    void toReview(FilmComment filmComment);

    void findCameraFilmsResource();

    void cameraSeeLikers(int pageNo);

    void cameraSeeReviews();

    void cameraReview();
}
