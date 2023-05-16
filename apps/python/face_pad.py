import pathlib
import os

from oneML import faceAPI as api

os.environ['ONEML_CPP_MIN_LOG_LEVEL'] = "ERROR"
assets_path = os.path.join(
    str(pathlib.Path(__file__).resolve().parent.parent.parent.absolute()),
    "assets/images",
)


def main():
    manager = api.LicenseManager()
    manager.activate_trial()
    pad_rgb = api.FacePad(api.PadType_Rgb, manager)
    pad_paper = api.FacePad(api.PadType_Paper, manager)
    utils = api.Utils(manager)

    # PAD RGB
    img = utils.read_image_cv(
        os.path.join(assets_path, "pad-rgb-set/spoof/1.jpg")
    )

    result = pad_rgb.classify(img)

    print("Status: " + str(result.get_return_status()))
    print("Spoof probability: " + "".join("{:.6f}".format(result.get_spoof_prob())))
    print("Spoof classification: " + str(result.is_spoof()))

    # PAD PAPER
    img = utils.read_image_cv(
        os.path.join(assets_path, "pad-paper-set/spoof/1.jpg")
    )

    result = pad_paper.classify(img)

    print("Status: " + str(result.get_return_status()))
    print("Spoof probability: " + "".join("{:.6f}".format(result.get_spoof_prob())))
    print("Spoof classification: " + str(result.is_spoof()))


if __name__ == "__main__":
    main()