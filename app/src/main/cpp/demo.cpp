#include <jni.h>
#include <dlfcn.h>
#include "android/native_activity.h"

#include <android/log.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "PELauncherDemo", __VA_ARGS__)

extern "C" {
    int JNI_OnLoad(JavaVM *vm, void *reserved) {
        return JNI_VERSION_1_6;
    }
    void ANativeActivity_onCreate(ANativeActivity *activity, void* savedInstanceState, size_t savedInstanceStateSize) {
        ((void (*)(ANativeActivity *, void *, size_t)) (dlsym(dlopen("libminecraftpe.so", RTLD_NOLOAD), "ANativeActivity_onCreate")))(activity, savedInstanceState, savedInstanceStateSize);
    }
}