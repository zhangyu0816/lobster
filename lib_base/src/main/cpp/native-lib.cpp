#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>
#include "MD5.h"

#define LOG_TAG "TAG_C"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

using namespace std;

// 预设的包名
static const char *PACKAGE_NAME = "com.yimi.rentme";

extern "C" {

JNIEXPORT jstring
JNICALL
Java_com_zb_lib_EncryptionUtil_encryptionMD5(JNIEnv *env, jclass j_clz, jobject j_context,
                                             jstring j_input, jint j_result_type) {
    if (!j_context) {   // 上下文判空
        return env->NewStringUTF("error content");
    }

    if (!j_input) { // 字符串判空
        return env->NewStringUTF("error input string");
    }
    // region ----------------------- 包名校验 -----------------------

    // 获取上下文的 Class
    jclass j_context_clz = env->GetObjectClass(j_context);
    // 获取 getPackageName 方法的方法签名
    jmethodID j_mid = env->GetMethodID(j_context_clz, "getPackageName", "()Ljava/lang/String;");
    // 获取包名
    jstring j_package_name = static_cast<jstring>(env->CallObjectMethod(j_context, j_mid));
    const char *c_package_name = env->GetStringUTFChars(j_package_name, NULL);
    if (strcmp(c_package_name, PACKAGE_NAME) != 0) {    // 包名不一致
        return env->NewStringUTF("error_package");
    }
    // region ----------------------- 签名校验 -----------------------

    // 获取方法签名
    j_mid = env->GetMethodID(j_context_clz, "getPackageManager",
                             "()Landroid/content/pm/PackageManager;");
    // PackageManager
    jobject j_package_manager = env->CallObjectMethod(j_context, j_mid);
    // 获取PackageInfo
    jclass j_package_manager_clz = env->GetObjectClass(j_package_manager);
    j_mid = env->GetMethodID(j_package_manager_clz, "getPackageInfo",
                             "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    // 获取 PackageInfo 对象
    jobject j_package_info = env->CallObjectMethod(j_package_manager, j_mid, j_package_name,
                                                   0x00000040);
    //获取签名数组
    j_clz = env->GetObjectClass(j_package_info);
    jfieldID j_signature_id = env->GetFieldID(j_clz, "signatures",
                                              "[Landroid/content/pm/Signature;");
    jobjectArray signature_array = static_cast<jobjectArray>(env->GetObjectField(j_package_info,
                                                                                 j_signature_id));
    // 获取数组的第一个元素
    jobject signature_first = env->GetObjectArrayElement(signature_array, 0);

    j_clz = env->GetObjectClass(signature_first);
    j_mid = env->GetMethodID(j_clz, "toCharsString", "()Ljava/lang/String;");
    jstring j_signature = static_cast<jstring>(env->CallObjectMethod(signature_first, j_mid));
    const char *c_signature = env->GetStringUTFChars(j_signature, NULL);
    MD5 md5_signature;
    md5_signature.update(c_signature);
    string result_signature = md5_signature.bytesToHexString(md5_signature.digest(), 16);
    jstring j_result_signature = env->NewStringUTF(result_signature.c_str());
    // 获取需要加密的数据
    const char *c_result_signature = env->GetStringUTFChars(j_result_signature, NULL);

    const char *c_input = env->GetStringUTFChars(j_input, NULL);
    char *c_output = new char[strlen(c_input) + 1];
    strcpy(c_output, c_input);

    // 此处可以对需要加密的数据进行加密前的处理，增加破解难度
    MD5 md5_input;
    md5_input.update(c_output);
    string result = md5_input.bytesToHexString(md5_input.digest(), 16);

    if (j_result_type == 1) {
        // 转大写
        transform(result.begin(), result.end(), result.begin(), ::toupper);
    }

    return env->NewStringUTF(result.c_str());
}


JNIEXPORT jstring
JNICALL
Java_com_zb_lib_EncryptionUtil_encryption(JNIEnv *env, jclass j_clz, jobject j_context,
                                          jstring j_key, jstring j_input) {

    return env->NewStringUTF("");
}

}