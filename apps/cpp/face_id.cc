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
  oneML::face::FaceId face_id(embedder, license_manager);

  // Images
  oneML::Image img1;
  oneML::Image img2;
  oneML::Image img3;
  oneML::Image img4;
  const std::string path1(ASSETS_DIR_PATH "/register-set/Colin_Powell/colin_powell_0074.jpg");
  const std::string path2(ASSETS_DIR_PATH "/register-set/Colin_Powell/colin_powell_0078.jpg");
  const std::string path3(ASSETS_DIR_PATH "/register-set/George_Robertson/george_robertson_0000.jpg");
  const std::string path4(ASSETS_DIR_PATH "/register-set/George_Robertson/george_robertson_0002.jpg");
  utils.read_image_cv(path1, img1);
  utils.read_image_cv(path2, img2);
  utils.read_image_cv(path3, img3);
  utils.read_image_cv(path4, img4);

  // is_the_same_person
  std::pair<oneML::ReturnStatus, bool> result1 = face_id.is_the_same_person(img1, img2);
  std::cout << "Is the same person: " << result1.second << std::endl;

  oneML::face::FaceEmbedderResult output;
  embedder.embed(img1, output);

  std::array<float, EMB_SIZE> img1_emb{};
  std::array<float, EMB_SIZE> img2_emb{};
  output.get_embedding(img1_emb);
  std::pair<oneML::ReturnStatus, bool> result2 =
      face_id.is_the_same_person(img1_emb, img2, img2_emb);
  std::cout << "Is the same person: " << result2.second << std::endl;

  // register_id_emb
  std::pair<oneML::ReturnStatus, int> result3 = face_id.register_id_emb("Colin_Powell", img1_emb);
  std::cout << "Registered size is: " << result3.second << std::endl;

  // register_id_images
  std::pair<oneML::ReturnStatus, std::array<float, EMB_SIZE>> result4 =
      face_id.register_id_images("George_Robertson", img3);
  std::array<float, EMB_SIZE> emb = result4.second;
  std::cout << "Registered emb is: [" << std::fixed << std::setprecision(6) << emb.at(0) << ", "
            << emb.at(1) << ", " << emb.at(2) << ", " << emb.at(3) << ", " << emb.at(4) << "]"
            << std::endl;

  // predict
  oneML::face::FaceIdResult output2;
  face_id.predict(img2, output2);

  std::cout << "Is identifiable: " << output2.is_identifiable() << std::endl;
  std::cout << "Id: " << output2.get_id() << std::endl;
  std::cout << "Nearest node distance: " << output2.get_nearest_node_distance() << std::endl;
  std::cout << "Combined distance: " << output2.get_combined_distance() << std::endl;

  // update_embedding_dynamically
  face_id.update_embedding_dynamically("George_Robertson", img3, emb);
  oneML::face::FaceIdResult output4;
  face_id.predict(img4, output4);

  std::cout << "Is identifiable: " << output4.is_identifiable() << std::endl;
  std::cout << "Id: " << output4.get_id() << std::endl;
  std::cout << "Nearest node distance: " << output4.get_nearest_node_distance() << std::endl;
  std::cout << "Combined distance: " << output4.get_combined_distance() << std::endl;

  face_id.update_embedding_dynamically("Colin_Powell", img2_emb, img1_emb);
  oneML::face::FaceIdResult output1;
  face_id.predict(img1, output1);

  std::cout << "Is identifiable: " << output1.is_identifiable() << std::endl;
  std::cout << "Id: " << output1.get_id() << std::endl;
  std::cout << "Nearest node distance: " << output1.get_nearest_node_distance() << std::endl;
  std::cout << "Combined distance: " << output1.get_combined_distance() << std::endl;

  // update_embedding
  face_id.update_embedding("George_Robertson", emb);
  oneML::face::FaceIdResult output3;
  face_id.predict(img3, output3);

  std::cout << "Is identifiable: " << output3.is_identifiable() << std::endl;
  std::cout << "Id: " << output3.get_id() << std::endl;
  std::cout << "Nearest node distance: " << output3.get_nearest_node_distance() << std::endl;
  std::cout << "Combined distance: " << output3.get_combined_distance() << std::endl;

  // remove_id
  face_id.remove_id("George_Robertson");
  face_id.remove_id("Colin_Powell");

  // get_ids
  std::vector<std::string> ids;
  face_id.get_ids(ids);
  std::cout << "Gallery size: " << ids.size() << std::endl;
}
