import pathlib
import os

from oneML import faceAPI as api

os.environ['ONEML_CPP_MIN_LOG_LEVEL'] = "ERROR"
assets_path = os.path.join(
    str(pathlib.Path(__file__).resolve().parent.parent.parent.absolute()),
    "assets/images",
)


# helper function
def which_pose(pose):
    if pose == api.Pose_Undefined:
        return "Undefined"
    elif pose == api.Pose_Front:
        return "Front"
    elif pose == api.Pose_Left:
        return "Left"
    elif pose == api.Pose_Right:
        return "Right"
    elif pose == api.Pose_Up:
        return "Up"
    elif pose == api.Pose_Down:
        return "Down"


def main():
    manager = api.LicenseManager()
    manager.activate_trial()
    detector = api.FaceDetector(manager)
    utils = api.Utils(manager)

    img = utils.read_image_cv(
        os.path.join(assets_path, "face-detect-set/face/0.jpg")
    )

    result = detector.detect(img)

    print("Faces: " + str(result.get_size()))
    for i in range(result.get_size()):
        bbox = result.get_bboxes()[i]
        landmarks = result.get_landmarks()[i]

        print("Face " + str(i))
        print("Score: " + "".join("{:.6f}".format(result.get_scores()[i])))
        print("Pose: " + which_pose(result.get_poses()[i]))
        print(
            "BBox: [("
            + "".join("{:.6f}, {:.6f}), (".format(bbox.top, bbox.left))
            + "".join("{:.6f}, {:.6f})]".format(bbox.bottom, bbox.right))
        )

        for j in range(5):
            print(
                "Landmark "
                + "".join(
                    "{:d}: ({:.6f}, {:.6f})".format(j, landmarks.x[j], landmarks.y[j])
                )
            )

    report = detector.get_usage()
    report.to_log()


if __name__ == "__main__":
    main()