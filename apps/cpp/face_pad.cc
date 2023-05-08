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
  oneML::face::FacePad pad_rgb(oneML::face::PadType::Rgb, license_manager);
  oneML::face::FacePad pad_paper(oneML::face::PadType::Paper, license_manager);

  // PAD RGB
  // image
  oneML::Image img;
  std::string path(ASSETS_DIR_PATH "/pad-rgb-set/spoof/1.jpg");
  utils.read_image_cv(path, img);

  // RUN
  oneML::face::FacePadResult output;
  pad_rgb.classify(img, output);

  std::cout << "status: " << output.get_return_status() << std::endl;
  std::cout << "Spoof probability: " << std::fixed << std::setprecision(6)
            << output.get_spoof_prob() << std::endl;
  std::cout << "Spoof classification: " << output.is_spoof() << std::endl;

  // PAD PAPER
  path = ASSETS_DIR_PATH "/pad-paper-set/spoof/1.jpg";
  utils.read_image_cv(path, img);

  // RUN
  pad_paper.classify(img, output);

  std::cout << "status: " << output.get_return_status() << std::endl;
  std::cout << "Spoof probability: " << std::fixed << std::setprecision(6)
            << output.get_spoof_prob() << std::endl;
  std::cout << "Spoof classification: " << output.is_spoof() << std::endl;

}
