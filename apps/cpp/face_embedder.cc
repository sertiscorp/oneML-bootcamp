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
  oneML::face::FaceEmbedder embedder(license_manager);

  // image
  oneML::Image img;
  const std::string path(ASSETS_DIR_PATH "/register-set/Colin_Powell/colin_powell_0074.jpg");
  utils.read_image_cv(path, img);

  // RUN
  oneML::face::FaceEmbedderResult output;
  embedder.embed(img, output);

  std::cout << "Embedding size: " << output.get_size() << std::endl;

  std::array<float, EMB_SIZE> emb{};
  output.get_embedding(emb);
  std::cout << "Embedding sample: [" << std::fixed << std::setprecision(6) << emb.at(0) << ", "
            << emb.at(1) << ", " << emb.at(2) << ", " << emb.at(3) << ", " << emb.at(4) << "]"
            << std::endl;
  std::cout << "Embedding sum: " << std::fixed << std::setprecision(5)
            << std::accumulate(emb.begin(), emb.end(), decltype(emb)::value_type(0)) << std::endl;

  oneML::UsageReport report = embedder.get_usage();
  report.to_log();
}
