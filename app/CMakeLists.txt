cmake_minimum_required(VERSION 3.4.1)

include_directories(libs/include)
add_library( native-lib
             SHARED
             src/main/cpp/native-lib.cpp
              )
set(DIR ../../../../libs)
add_library(opencv_java3
            SHARED
            IMPORTED)
set_target_properties(opencv_java3
                      PROPERTIES IMPORTED_LOCATION
                      ${DIR}/armeabi-v7a/libopencv_java3.so)

target_link_libraries( # Specifies the target library.
                       native-lib
                        log
                        jnigraphics
                        opencv_java3 )