#include <iomanip>
#include <numeric>

#include "apps_config.h"
#include "oneml/public/oneml.h"

int main(int argc, char** argv) {
#ifdef _WIN32
  _putenv_s("ONEML_CPP_MIN_LOG_LEVEL", "ERROR");
#else
  setenv("ONEML_CPP_MIN_LOG_LEVEL", "ERROR", 1);
#endif

  oneML::LicenseManager license_manager;
  license_manager.activate_trial();

  oneML::Utils utils(license_manager);
  oneML::face::EKYC ekyc(license_manager);

  // Images
  oneML::Image img1;
  oneML::Image img2;
  const std::string path1(ASSETS_DIR_PATH "/face-detect-set/face/8.jpg");
  const std::string path2(ASSETS_DIR_PATH "/face-detect-set/face/9.jpg");
  utils.read_image_cv(path1, img1);
  utils.read_image_cv(path2, img2);

  // RUN
  oneML::face::EKYCResult output;
  oneML::face::EKYCOps ops{true};
  ekyc.run(img1, img2, ops, ops, output);

  oneML::face::Pose pose1;
  oneML::face::Pose pose2;
  oneML::face::BBox bbox1;
  oneML::face::BBox bbox2;
  oneML::face::FaceLandmark5 landmarks1;
  oneML::face::FaceLandmark5 landmarks2;

  auto status = output.get_return_status();
  std::cout << "Status: " << status << std::endl;
  std::cout << "Same: " << output.is_same_person() << std::endl;
  std::cout << "Distance: " << output.get_distance() << std::endl;

  output.get_bboxes(bbox1, bbox2);
  output.get_landmarks(landmarks1, landmarks2);
  output.get_face_poses(pose1, pose2);

  std::cout << "Face 1" << std::endl;
  std::cout << "BBox: " << bbox1 << std::endl;
  std::cout << "Pose: " << pose1 << std::endl;
  std::cout << "Landmarks: " << landmarks1 << std::endl;

  std::cout << "Face 2" << std::endl;
  std::cout << "BBox: " << bbox2 << std::endl;
  std::cout << "Pose: " << pose2 << std::endl;
  std::cout << "Landmarks: " << landmarks2 << std::endl;

}
