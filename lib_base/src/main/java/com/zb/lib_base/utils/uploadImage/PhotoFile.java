package com.zb.lib_base.utils.uploadImage;

import java.io.File;

public class PhotoFile {

    private String fileHashCode; //哈希值
    private long fileSize;  //大小
    private String filePath; //路径
    private String srcFilePath;//原图
    private File photoeFile;//文件

    private int uploadStatus;//上传状态  1 未上传  2.正在上传  3.上传成功  4.上传失败
    private String webUrl; //网络url
    long cameraFilmId;
    int cameraFilmType;//胶卷类型

    public PhotoFile() {
        super();
    }

    public PhotoFile(String srcFilePath, File file) {
        this.srcFilePath = srcFilePath;
        this.fileSize = file.length();
        this.fileHashCode = "" + file.hashCode();
        this.filePath = file.getAbsolutePath();
        this.photoeFile = file;
        this.uploadStatus = 1;
        this.webUrl = "";
    }

    public PhotoFile(String srcFilePath, File file, long cameraFilmId, int cameraFilmType) {
        this.cameraFilmId = cameraFilmId;
        this.cameraFilmType = cameraFilmType;
        this.srcFilePath = srcFilePath;
        this.fileSize = file.length();
        this.fileHashCode = "" + file.hashCode();
        this.filePath = file.getAbsolutePath();
        this.photoeFile = file;
        this.uploadStatus = 1;
        this.webUrl = "";
    }

    public PhotoFile(String url) {
        this.srcFilePath = url;
        this.fileSize = 0;
        this.fileHashCode = "";
        this.filePath = url;
        this.photoeFile = null;
        this.uploadStatus = 3;
        this.webUrl = url;
    }


    public String getFileHashCode() {
        return fileHashCode;
    }

    public void setFileHashCode(String fileHashCode) {
        this.fileHashCode = fileHashCode;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getPhotoeFile() {
        return photoeFile;
    }

    public void setPhotoeFile(File photoeFile) {
        this.photoeFile = photoeFile;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }


    public String getSrcFilePath() {
        return srcFilePath;
    }

    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }

    public long getCameraFilmId() {
        return cameraFilmId;
    }

    public void setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
    }

    public int getCameraFilmType() {
        return cameraFilmType;
    }

    public void setCameraFilmType(int cameraFilmType) {
        this.cameraFilmType = cameraFilmType;
    }
}
