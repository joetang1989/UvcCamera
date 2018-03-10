NDK_TOOLCHAIN_VERSION=4.9
APP_ABI := armeabi armeabi-v7a arm64-v8a
#APP_ABI := arm64-v8a
#APP_ABI := all

APP_STL := gnustl_static
APP_CPPFLAGS := -std=c++11 -frtti -fexceptions -pie -fPIE -fPIC
APP_PLATFORM := android-14
#APP_OPTIM := release
