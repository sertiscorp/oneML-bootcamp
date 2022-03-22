#include <iomanip>

#include "apps_config.h"
#include "oneml/public/oneml.h"

int main() {
#ifdef _WIN32
  _putenv_s("ONEML_CPP_MIN_LOG_LEVEL", "ERROR");
#else
  setenv("ONEML_CPP_MIN_LOG_LEVEL", "ERROR", 1);
#endif

  oneML::LicenseManager license_manager;
  license_manager.activate_trial();

  oneML::Utils utils(license_manager);
  oneML::alpr::VehicleDetector detector(license_manager);

  std::string path{ASSETS_DIR_PATH "/vehicle-detect-set/vehicle/cars.jpg"};
  oneML::Image img;
  utils.read_image_cv(path, img);

  oneML::MultiImage inputs{img};
  std::vector<oneML::alpr::VehicleDetectorResult> results;

  detector.detect(inputs, results);

  for (auto& result : results) {
    std::cout << "Vehicles: " << result.get_size() << std::endl;

    for (int i = 0; i < result.get_size(); ++i) {
      std::cout << "Vehicle " << i << std::endl;

      std::vector<float> scores;
      std::vector<oneML::alpr::BBox> bboxes;

      result.get_bboxes(bboxes);
      result.get_scores(scores);

      std::cout << "Score: " << std::fixed << std::setprecision(6) << scores[i] << std::endl;
      std::cout << bboxes[i] << std::endl;
    }
  }
}
