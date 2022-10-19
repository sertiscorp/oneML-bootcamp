import pathlib
import os
import argparse

from oneML import faceAPI as api

os.environ['ONEML_CPP_MIN_LOG_LEVEL'] = "ERROR"
assets_path = os.path.join(
    str(pathlib.Path(__file__).resolve().parent.parent.parent.absolute()),
    "assets/images",
)


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


def print_face_result(bbox, pose, lm):
    print("BBox: BBox[top={:.6f},left={:.6f},bottom={:.6f},right={:.6f}]".format(bbox.top, bbox.left, bbox.bottom, bbox.right))
    print("Pose:", which_pose(pose))
    print("Landmarks: FaceLandmark5[x1={:.6f},x2={:.6f},x3={:.6f},x4={:.6f},x5={:.6f},".format(*lm.x) + "" +
            "y1={:.6f},y2={:.6f},y3={:.6f},y4={:.6f},y5={:.6f}]".format(*lm.y))


def main():
    manager = api.LicenseManager()
    manager.activate_trial()

    ekyc = api.EKYC(manager)
    utils = api.Utils(manager)

    image1 = utils.read_image_cv(
        os.path.join(assets_path, "face-detect-set/face/8.jpg")
    )
    image2 = utils.read_image_cv(
        os.path.join(assets_path, "face-detect-set/face/9.jpg")
    )

    ops = api.EKYCOps(True, True)
    result = ekyc.run(image1, image2, ops, ops)

    bbox1, bbox2 = result.get_bboxes()
    pose1, pose2 = result.get_face_poses()
    lm1, lm2 = result.get_landmarks()
    is_same_person = result.is_same_person()
    distance = result.get_distance()
    status = result.get_return_status()

    print("Status:", status)
    print("Same:", int(is_same_person))
    print("Distance: {:.6f}".format(distance))
    
    print("Face 1")
    print_face_result(bbox1, pose1, lm1)

    print("Face 2")
    print_face_result(bbox2, pose2, lm2)

    report = ekyc.get_usage()
    report.to_log()


if __name__ == "__main__":
    main()