package main

import (
	"faceapps/utility"
	"fmt"
	"oneml/face"
	"os"
	"path"
)

func main() {
	currentPath, _ := os.Getwd()
	testAssetPath := path.Join(currentPath, "../../assets/images")

	// Initialize oneML modules
	licenseManager := face.NewLicenseManager()
	licenseManager.ActivateTrial()

	faceEmbedder := face.NewFaceEmbedder(licenseManager)
	faceId := face.NewFaceId(faceEmbedder, licenseManager)
	oneMLUtils := face.NewUtils(licenseManager)
	defer face.DeleteUtils(oneMLUtils)
	defer face.DeleteFaceEmbedder(faceEmbedder)
	defer face.DeleteFaceId(faceId)

	// load Image
	filePath1 := path.Join(testAssetPath, "register-set/Colin_Powell/colin_powell_0074.jpg")
	img1 := oneMLUtils.ReadImageCV(filePath1)

	filePath2 := path.Join(testAssetPath, "register-set/Colin_Powell/colin_powell_0078.jpg")
	img2 := oneMLUtils.ReadImageCV(filePath2)

	filePath3 := path.Join(testAssetPath, "register-set/George_Robertson/george_robertson_0000.jpg")
	img3 := oneMLUtils.ReadImageCV(filePath3)

	filePath4 := path.Join(testAssetPath, "register-set/George_Robertson/george_robertson_0002.jpg")
	img4 := oneMLUtils.ReadImageCV(filePath4)

	// IsTheSamePersonImg
	isSame := faceId.IsTheSamePersonImg(img1, img2)
	fmt.Println("Is the same person:", utility.BoolToInt(isSame))

	// IsTheSamePersonEmb
	embeddings := faceEmbedder.Embed(img1)
	samePersonResult := faceId.IsTheSamePersonEmb(embeddings.GetEmbedding(), img2)
	fmt.Println("Is the same person:", utility.BoolToInt(samePersonResult.GetFirst()))

	// RegisterIdEmb
	size := faceId.RegisterIdEmb("Colin_Powell", embeddings.GetEmbedding())
	fmt.Println("Registered size is:", size)

	// RegisterIdImage
	emb := faceId.RegisterIdImage("George_Robertson", img3)
	fmt.Printf("Registered emb is: [%.6f, %.6f, %.6f, %.6f, %.6f]\n", emb.Get(0), emb.Get(1), emb.Get(2), emb.Get(3), emb.Get(4))

	// Predict
	result := faceId.Predict(img2)
	fmt.Println("Is identifiable:", utility.BoolToInt(result.IsIdentifiable()))
	fmt.Println("Id:", result.GetId())
	fmt.Printf("Nearest node distance: %.6f\n", result.GetNearestNodeDistance())
	fmt.Printf("Combined distance: %.6f\n", result.GetCombinedDistance())

	// UpdateEmbeddingDynamicallyImg
	faceId.UpdateEmbeddingDynamicallyImg("George_Robertson", img3, emb)

	result = faceId.Predict(img4)
	fmt.Println("Is identifiable:", utility.BoolToInt(result.IsIdentifiable()))
	fmt.Println("Id:", result.GetId())
	fmt.Printf("Nearest node distance: %.6f\n", result.GetNearestNodeDistance())
	fmt.Printf("Combined distance: %.6f\n", result.GetCombinedDistance())

	// UpdateEmbeddingDynamicallyEmb
	faceId.UpdateEmbeddingDynamicallyEmb("Colin_Powell", samePersonResult.GetSecond(), embeddings.GetEmbedding())

	result = faceId.Predict(img1)
	fmt.Println("Is identifiable:", utility.BoolToInt(result.IsIdentifiable()))
	fmt.Println("Id:", result.GetId())
	fmt.Printf("Nearest node distance: %.6f\n", result.GetNearestNodeDistance())
	fmt.Printf("Combined distance: %.6f\n", result.GetCombinedDistance())

	// UpdateEmbedding
	faceId.UpdateEmbedding("George_Robertson", emb)

	result = faceId.Predict(img3)
	fmt.Println("Is identifiable:", utility.BoolToInt(result.IsIdentifiable()))
	fmt.Println("Id:", result.GetId())
	fmt.Printf("Nearest node distance: %.6f\n", result.GetNearestNodeDistance())
	fmt.Printf("Combined distance: %.6f\n", result.GetCombinedDistance())

	// RemoveId
	faceId.RemoveId("Colin_Powell")
	faceId.RemoveId("George_Robertson")

	// GetIds
	ids := faceId.GetIds()
	fmt.Println("Gallery size:", ids.Size())

	report := faceId.GetUsage()
	report.ToLog()
}
