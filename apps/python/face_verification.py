import pathlib
import os

from oneML import faceAPI as api
import numpy as np

os.environ['ONEML_CPP_MIN_LOG_LEVEL'] = "ERROR"
assets_path = os.path.join(
    str(pathlib.Path(__file__).resolve().parent.parent.parent.absolute()),
    "assets/images",
)


def main():
    manager = api.LicenseManager()
    manager.activate_trial()
    detector = api.FaceDetector(manager)
    embedder = api.FaceEmbedder(manager)
    face_id = api.FaceId(embedder, manager)
    utils = api.Utils(manager)

    img1 = utils.read_image_cv(
        os.path.join(
            assets_path, "register-set/Colin_Powell/colin_powell_0074.jpg"
        )
    )

    img2 = utils.read_image_cv(
        os.path.join(
            assets_path, "evaluate-set/Colin_Powell/colin_powell_0097.jpg"
        )
    )

    img3 = utils.read_image_cv(
        os.path.join(
            assets_path, "register-set/George_Robertson/george_robertson_0000.jpg"
        )
    )

    img4 = utils.read_image_cv(
        os.path.join(
            assets_path, "evaluate-set/George_Robertson/george_robertson_0009.jpg"
        )
    )

    register_images = np.stack((img1, img3), axis=0)
    eval_images = np.stack((img2, img4), axis=0)

    register_results = detector.detect_batch(register_images)
    eval_results = detector.detect_batch(eval_images)

    align_img1 = utils.crop_align_face_landmark(
        img1, register_results[0].get_landmarks()[0]
    )
    align_img3 = utils.crop_align_face_landmark(
        img3, register_results[1].get_landmarks()[0]
    )
    align_img2 = utils.crop_align_face_landmark(
        img2, eval_results[0].get_landmarks()[0]
    )
    align_img4 = utils.crop_align_face_landmark(
        img4, eval_results[1].get_landmarks()[0]
    )

    face_id.register_id_image("colin_powell", align_img1)
    face_id.register_id_image("george_robertson", align_img3)

    results = face_id.predict_batch(np.stack((align_img2, align_img4), axis=0))

    # Run with registration & predict style to print scores for validating with other language applications
    # since is_the_same_person_img API doesn't provide any score.
    print(
        "First person nearest node distance:",
        "{:.8f}".format(results[0].get_nearest_node_distance()),
    )
    print(
        "Second person nearest node distance:",
        "{:.8f}".format(results[1].get_nearest_node_distance()),
    )
    print("First person:", results[0].get_id())
    print("Second person:", results[1].get_id())

    # Also test the actual face verification API (is_the_same_person_img)
    is_same = face_id.is_the_same_person_img(img1, img2)
    print("Is the same person (colin_powell): " + str(int(is_same)))

    is_same = face_id.is_the_same_person_img(img3, img4)
    print("Is the same person (george_robertson): " + str(int(is_same)))

    report = detector.get_usage()
    report.to_log()

    report = face_id.get_usage()
    report.to_log()


if __name__ == "__main__":
    main()
