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
    face_id = api.FaceId(embedder, manager)
    utils = api.Utils(manager)

    # Images
    img1 = utils.read_image_cv(
        os.path.join(
            assets_path, "register-set/Colin_Powell/colin_powell_0074.jpg"
        )
    )

    img2 = utils.read_image_cv(
        os.path.join(
            assets_path, "register-set/Colin_Powell/colin_powell_0078.jpg"
        )
    )

    img3 = utils.read_image_cv(
        os.path.join(
            assets_path, "register-set/George_Robertson/george_robertson_0000.jpg"
        )
    )

    img4 = utils.read_image_cv(
        os.path.join(
            assets_path, "register-set/George_Robertson/george_robertson_0002.jpg"
        )
    )

    # is_the_same_person_img
    is_same = face_id.is_the_same_person_img(img1, img2)
    print("Is the same person: " + str(int(is_same)))

    # is_the_same_person_emb
    embeddings = embedder.embed(img1)
    is_same, new_emb = face_id.is_the_same_person_emb(embeddings.get_embedding(), img2)
    print("Is the same person: " + str(int(is_same)))

    # register_id_emb
    size = face_id.register_id_emb("Colin_Powell", embeddings.get_embedding())
    print("Registered size is: " + str(size))

    # register_id_images
    emb = face_id.register_id_image("George_Robertson", img3)
    print(
        "Registered emb is: ["
        + "".join("{:.6f}, ".format(k) for k in emb[0:5])[:-2]
        + "]"
    )

    # predict
    result = face_id.predict(img2)

    print("Is identifiable: " + str(int(result.is_identifiable())))
    print("Id: " + str(result.get_id()))
    print(
        "Nearest node distance: "
        + "".join("{:.6f}".format(result.get_nearest_node_distance()))
    )
    print(
        "Combined distance: " + "".join("{:.6f}".format(result.get_combined_distance()))
    )

    # update_embedding_dynamically_img
    face_id.update_embedding_dynamically_img("George_Robertson", img3, emb)
    result = face_id.predict(img4)

    print("Is identifiable: " + str(int(result.is_identifiable())))
    print("Id: " + str(result.get_id()))
    print(
        "Nearest node distance: "
        + "".join("{:.6f}".format(result.get_nearest_node_distance()))
    )
    print(
        "Combined distance: " + "".join("{:.6f}".format(result.get_combined_distance()))
    )

    # update_embedding_dynamically_emb
    face_id.update_embedding_dynamically_emb(
        "Colin_Powell", new_emb, embeddings.get_embedding()
    )
    result = face_id.predict(img1)

    print("Is identifiable: " + str(int(result.is_identifiable())))
    print("Id: " + str(result.get_id()))
    print(
        "Nearest node distance: "
        + "".join("{:.6f}".format(result.get_nearest_node_distance()))
    )
    print(
        "Combined distance: " + "".join("{:.6f}".format(result.get_combined_distance()))
    )

    # update_embedding
    face_id.update_embedding("George_Robertson", emb)
    result = face_id.predict(img3)

    print("Is identifiable: " + str(int(result.is_identifiable())))
    print("Id: " + str(result.get_id()))
    print(
        "Nearest node distance: "
        + "".join("{:.6f}".format(result.get_nearest_node_distance()))
    )
    print(
        "Combined distance: " + "".join("{:.6f}".format(result.get_combined_distance()))
    )

    # remove_id
    face_id.remove_id("George_Robertson")
    face_id.remove_id("Colin_Powell")

    # get_ids
    ids = face_id.get_ids()
    print("Gallery size: " + str(len(ids)))


if __name__ == "__main__":
    main()