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
// 预设的签名MD5值
static const char *APP_SIGNATURE_MD5 = "22f65cc4f98cbdb4fc33f02d0efae0ee";
// 22F65CC4F98CBDB4FC33F02D0EFAE0EE
// 预设的签名
const char *APP_SIGNATURE = "308203d9308202c1a003020102020445fde784300d06092a864886f70d01010b050030819b310b300906035504061302636e3111300f060355040813087a68656a69616e673110300e0603550407130777656e7a686f7531293027060355040a132057656e7a686f752059696d6920546563686e6f6c6f677920436f2e204c74642e31293027060355040b132057656e7a686f752059696d6920546563686e6f6c6f677920436f2e204c74642e3111300f0603550403130878756a69616e64613020170d3135303831323032353932375a180f32313135303731393032353932375a30819b310b300906035504061302636e3111300f060355040813087a68656a69616e673110300e0603550407130777656e7a686f7531293027060355040a132057656e7a686f752059696d6920546563686e6f6c6f677920436f2e204c74642e31293027060355040b132057656e7a686f752059696d6920546563686e6f6c6f677920436f2e204c74642e3111300f0603550403130878756a69616e646130820122300d06092a864886f70d01010105000382010f003082010a0282010100a91dee8f515e9850f9b1abafe0fc02ae58dc3872ea96bd416828509e4d670eceab9b2f06c9ce66b311f075c8859a8acc59c29cba3371588be48f919f79fe712c53041a27a04d522dae38ee898adf2960d5da1104a8";

extern "C" {

JNIEXPORT jstring
JNICALL
Java_com_yimi_rentme_EncryptionUtil_encryptionMD5(JNIEnv *env, jclass j_clz, jobject j_context,
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
    // LOGD("input package name --> %s ", c_package_name);
    if (strcmp(c_package_name, PACKAGE_NAME) != 0) {    // 包名不一致
        return env->NewStringUTF("error_package");
    }

    // endregion -----------------------------------------------------

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
    // LOGD("input signature1 = %s", c_signature);
    // LOGD("input signature2 = %s", APP_SIGNATURE);

    if (strncmp(c_signature, APP_SIGNATURE, strlen(APP_SIGNATURE)) != 0) {  // 签名不一致
        return env->NewStringUTF("error_signature");
    }

    // endregion -----------------------------------------------------

    // 获取需要加密的数据
    const char *c_input = env->GetStringUTFChars(j_input, NULL);

    // 此处可以对需要加密的数据进行加密前的处理，增加破解难度

    MD5 md5_input;
    md5_input.update(c_input);
    string result = md5_input.bytesToHexString(md5_input.digest(), 16);

    if (j_result_type == 1) {
        // 转大写
        transform(result.begin(), result.end(), result.begin(), ::toupper);
    }

    return env->NewStringUTF(result.c_str());
}


JNIEXPORT jstring
JNICALL
Java_com_yimi_rentme_EncryptionUtil_encryption(JNIEnv *env, jclass j_clz, jobject j_context,
                                               jstring j_key, jstring j_input) {

    return env->NewStringUTF("");
}

}