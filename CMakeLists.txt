cmake_minimum_required(VERSION 3.10)

# Project name and version
project(SAPI4 VERSION 1.0 LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find required packages
find_package(Threads REQUIRED)

# Define include directories
# Note: You may need to adjust this path based on your Microsoft Speech SDK installation
set(SPEECH_SDK_PATH "C:/Program Files (x86)/Microsoft Speech SDK")
include_directories(${SPEECH_SDK_PATH}/Include)

# Define the SAPI4 library
add_library(sapi4 SHARED
    sapi4.cpp
)

# Link libraries
target_link_libraries(sapi4 
    ole32
    user32
    uuid
    odbc32
    odbccp32
)

# Define the sapi4out executable
add_executable(sapi4out
    sapi4out.cpp
)

target_link_libraries(sapi4out 
    sapi4
    ole32
    user32
    uuid
    odbc32
    odbccp32
)

# Define the sapi4limits executable
add_executable(sapi4limits
    sapi4limits.cpp
)

target_link_libraries(sapi4limits
    sapi4
    ole32
    user32
    uuid
    odbc32
    odbccp32
)

# Set output directory for executables
set_target_properties(sapi4out sapi4limits PROPERTIES
    RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin
)

# Set output directory for the library
set_target_properties(sapi4 PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin
    RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin  # For Windows DLL
)

# Compiler-specific options
if(MSVC)
    target_compile_definitions(sapi4 PRIVATE WIN32_LEAN_AND_MEAN)
    target_compile_definitions(sapi4out PRIVATE WIN32_LEAN_AND_MEAN)
    target_compile_definitions(sapi4limits PRIVATE WIN32_LEAN_AND_MEAN)
    
    # Set additional MSVC-specific flags
    target_compile_options(sapi4 PRIVATE /O2 /DNDEBUG)
    target_compile_options(sapi4out PRIVATE /O2 /DNDEBUG)
    target_compile_options(sapi4limits PRIVATE /O2 /DNDEBUG)
endif()

# Installation rules
install(TARGETS sapi4 sapi4out sapi4limits
    RUNTIME DESTINATION bin
    LIBRARY DESTINATION lib
    ARCHIVE DESTINATION lib
)