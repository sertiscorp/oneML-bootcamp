import os
import pathlib

from oneML import alprAPI as api

os.environ['ONEML_CPP_MIN_LOG_LEVEL'] = "ERROR"
assets_path = os.path.join(
    str(pathlib.Path(__file__).resolve().parent.parent.parent.absolute()),
    "assets/images",
)


def main():
    manager = api.LicenseManager()
    manager.activate_trial()
    detector = api.VehicleDetector(manager)
    utils = api.Utils(manager)

    img = utils.read_image_cv(
        os.path.join(assets_path, "vehicle-detect-set/vehicle/cars.jpg")
    )

    result = detector.detect(img)

    print("Vehicles: " + str(result.get_size()))
    scores = result.get_scores()
    bboxes = result.get_bboxes()
    for i in range(result.get_size()):
        print("Vehicle " + str(i))
        print("Score: %.6f" % scores[i])
        bb = bboxes[i]
        print("BBox[top=%.6f,left=%.6f,bottom=%.6f,right=%.6f]" % (bb.top, bb.left, bb.bottom, bb.right))

if __name__ == "__main__":
    main()