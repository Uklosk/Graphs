LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include ../sdk/native/jni/OpenCV.mk
include ../sdk/native/jni/OpenCV-tegra3.mk

LOCAL_MODULE    := Digitizer
LOCAL_SRC_FILES := Digitizer.cpp

include $(BUILD_SHARED_LIBRARY)
