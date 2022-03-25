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
    embedder = api.FaceEmbedder(manager)
    utils = api.Utils(manager)

    img = utils.read_image_cv(
        os.path.join(
            assets_path, "register-set/Colin_Powell/colin_powell_0074.jpg"
        )
    )

    result = embedder.embed(img)

    print("Embedding size: " + str(result.get_size()))
    print(
        "Embedding sample: ["
        + "".join("{:.6f}, ".format(k) for k in result.get_embedding()[0:5])[:-2]
        + "]"
    )
    # TODO: MO-623
    print("Embedding sum: " + "".join("{:.5f}".format(sum(result.get_embedding()))))


if __name__ == "__main__":
    main()