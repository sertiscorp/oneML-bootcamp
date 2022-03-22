# Dynamically set a test asset path.
if(BUILD_FOR_ANDROID)
    set(ASSETS_DIR_PATH "/data/local/tmp/assets/")
else()
    set(ASSETS_DIR_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../assets/images/")
endif()

# Generate a new header file by replacing variables with CMake variables.
configure_file( 
    "${CMAKE_CURRENT_SOURCE_DIR}/include/apps_config.h.in" 
    "${CMAKE_CURRENT_BINARY_DIR}/include/apps_config.h" )

# Include a newly generated header and other headers.
include_directories("${CMAKE_CURRENT_BINARY_DIR}/include")

# face_embedder executable
add_executable(face_embedder ${CMAKE_CURRENT_SOURCE_DIR}/cpp/face_embedder.cc)
target_link_libraries(face_embedder PRIVATE oneml)

# face_detector_executable
add_executable(face_detector ${CMAKE_CURRENT_SOURCE_DIR}/cpp/face_detector.cc)
target_link_libraries(face_detector PRIVATE oneml)

# face_id executable
add_executable(face_id ${CMAKE_CURRENT_SOURCE_DIR}/cpp/face_id.cc)
target_link_libraries(face_id PRIVATE oneml)

# vehicle_detector executable
add_executable(vehicle_detector ${CMAKE_CURRENT_SOURCE_DIR}/cpp/vehicle_detector.cc)
target_link_libraries(vehicle_detector PRIVATE oneml)

# face_verification executable
add_executable(face_verification ${CMAKE_CURRENT_SOURCE_DIR}/cpp/face_verification.cc)
target_link_libraries(face_verification PRIVATE oneml)
