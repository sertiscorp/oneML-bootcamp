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

  // image - 1 face
  oneML::Image img;
  const std::string path(ASSETS_DIR_PATH "/face-detect-set/face/0.jpg");
  utils.read_image_cv(path, img);

  // RUN
  oneML::face::FaceDetectorResult output;
  detector.detect(img, output);

  std::cout << "Faces: " << output.get_size() << std::endl;

  for (int z = 0; z < output.get_size(); z++) {
    std::cout << "Face " << z << std::endl;

    std::vector<float> scores;
    std::vector<oneML::face::Pose> poses;
    std::vector<oneML::face::BBox> bboxes;
    std::vector<oneML::face::FaceLandmark<N_LANDMARKS>> landmarks;

    output.get_scores(scores);
    output.get_poses(poses);
    output.get_bboxes(bboxes);
    output.get_landmarks(landmarks);

    std::cout << "Score: " << std::fixed << std::setprecision(6) << scores[z] << std::endl;
    std::cout << "Pose: " << poses[z] << std::endl;
    std::cout << "BBox: [(" << bboxes[z].top << ", " << bboxes[z].left << "), (" << bboxes[z].bottom
              << ", " << bboxes[z].right << ")]" << std::endl;
    for (int l = 0; l < N_LANDMARKS; l++) {
      std::cout << "Landmark " << l << ": (" << landmarks[z].x.at(l) << ", " << landmarks[z].y.at(l)
                << ")" << std::endl;
    }
  }
}
