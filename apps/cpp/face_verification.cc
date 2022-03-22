#include <iomanip>

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
  oneML::face::FaceDetector detector(license_manager);
  oneML::face::FaceId face_id(license_manager);

  oneML::Image img1;
  oneML::Image img2;
  oneML::Image img3;
  oneML::Image img4;

  const std::string path1(ASSETS_DIR_PATH "/register-set/Colin_Powell/colin_powell_0074.jpg");
  const std::string path2(ASSETS_DIR_PATH "/evaluate-set/Colin_Powell/colin_powell_0097.jpg");
  const std::string path3(ASSETS_DIR_PATH "/register-set/George_Robertson/george_robertson_0000.jpg");
  const std::string path4(ASSETS_DIR_PATH "/evaluate-set/George_Robertson/george_robertson_0009.jpg");
  
  utils.read_image_cv(path1, img1);
  utils.read_image_cv(path2, img2);
  utils.read_image_cv(path3, img3);  
  utils.read_image_cv(path4, img4);

  oneML::MultiImage register_inputs{img1, img3};
  oneML::MultiImage eval_inputs{img2, img4};

  std::vector<oneML::face::FaceDetectorResult> register_outputs;
  std::vector<oneML::face::FaceDetectorResult> eval_outputs;

  // RUN
  detector.detect(register_inputs, register_outputs);
  detector.detect(eval_inputs, eval_outputs);

  std::vector<oneML::face::FaceLandmark<N_LANDMARKS>> landmarks1;
  register_outputs[0].get_landmarks(landmarks1);
  oneML::Image align_image1;
  utils.crop_align_face_landmark(img1, landmarks1[0], align_image1);

  std::vector<oneML::face::FaceLandmark<N_LANDMARKS>> landmarks3;
  register_outputs[1].get_landmarks(landmarks3);
  oneML::Image align_image3;
  utils.crop_align_face_landmark(img3, landmarks3[0], align_image3);

  std::vector<oneML::face::FaceLandmark<N_LANDMARKS>> landmarks2;
  eval_outputs[0].get_landmarks(landmarks2);
  oneML::Image align_image2;
  utils.crop_align_face_landmark(img2, landmarks2[0], align_image2);

  std::vector<oneML::face::FaceLandmark<N_LANDMARKS>> landmarks4;
  eval_outputs[1].get_landmarks(landmarks4);
  oneML::Image align_image4;
  utils.crop_align_face_landmark(img4, landmarks4[0], align_image4);

  face_id.register_id_images("colin_powell", align_image1);
  face_id.register_id_images("george_robertson", align_image3);

  std::vector<oneML::face::FaceIdResult> face_id_results;
  oneML::MultiImage align_image_predict{align_image2, align_image4};
  face_id.predict(align_image_predict, face_id_results);

  // Run with registration & predict style to print scores for validating with other language
  // applications since is_the_sample_person API doesn't provide any score.
  std::cout << "First person nearest node distance: " << std::fixed << std::setprecision(8)
            << face_id_results[0].get_nearest_node_distance() << std::endl;
  std::cout << "Second person nearest node distance: " << std::fixed << std::setprecision(8)
            << face_id_results[1].get_nearest_node_distance() << std::endl;
  std::cout << "First person: " << face_id_results[0].get_id() << std::endl;
  std::cout << "Second person: " << face_id_results[1].get_id() << std::endl;

  // Also test the actual face verification API (is_the_same_person)
  std::pair<oneML::ReturnStatus, bool> is_same1 = face_id.is_the_same_person(img1, img2);
  std::cout << "Is the same person (colin_powell): " << is_same1.second << std::endl;

  std::pair<oneML::ReturnStatus, bool> is_same2 = face_id.is_the_same_person(img3, img4);
  std::cout << "Is the same person (george_robertson): " << is_same2.second << std::endl;
}
