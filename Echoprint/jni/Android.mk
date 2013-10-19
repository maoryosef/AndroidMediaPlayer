LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    :=echoprint-jni

LOCAL_SRC_FILES :=AndroidCodegen.cpp \
            codegen/src/Codegen.cxx \
            codegen/src/Whitening.cxx \
            codegen/src/SubbandAnalysis.cxx \
            codegen/src/MatrixUtility.cxx \
            codegen/src/Fingerprint.cxx \
            codegen/src/Base64.cxx \
            codegen/src/AudioStreamInput.cxx \
            codegen/src/AudioBufferInput.cxx

LOCAL_LDLIBS    :=-llog\
        -lz

LOCAL_C_INCLUDES :=c:\\\\GIT\\AndroidMediaPlayer\\Echoprint\\jni\\codegen\\src \
            c:\\GIT\\AndroidMediaPlayer\\Echoprint\\jni\\boost_1_54_0
LOCAL_CPPFLAGS += -fexceptions
include $(BUILD_SHARED_LIBRARY)