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

	faceDetector := face.NewFaceDetector(licenseManager)
	faceEmbedder := face.NewFaceEmbedder(licenseManager)
	faceId := face.NewFaceId(faceEmbedder, licenseManager)
	utils := face.NewUtils(licenseManager)

	// Load images
	img1 := utils.ReadImageCV(path.Join(testAssetPath, "register-set/Colin_Powell/colin_powell_0074.jpg"))
	img2 := utils.ReadImageCV(path.Join(testAssetPath, "evaluate-set/Colin_Powell/colin_powell_0097.jpg"))
	img3 := utils.ReadImageCV(path.Join(testAssetPath, "register-set/George_Robertson/george_robertson_0000.jpg"))
	img4 := utils.ReadImageCV(path.Join(testAssetPath, "evaluate-set/George_Robertson/george_robertson_0009.jpg"))

	registerImages := face.NewMultiImage()
	registerImages.Add(img1)
	registerImages.Add(img3)

	evalImages := face.NewMultiImage()
	evalImages.Add(img2)
	evalImages.Add(img4)

	registerResults := faceDetector.DetectBatch(registerImages)
	evalResults := faceDetector.DetectBatch(evalImages)

	alignImg1 := utils.CropAlignFaceLandmark(img1, registerResults.Get(0).GetLandmarks().Get(0))
	alignImg3 := utils.CropAlignFaceLandmark(img3, registerResults.Get(1).GetLandmarks().Get(0))
	alignImg2 := utils.CropAlignFaceLandmark(img2, evalResults.Get(0).GetLandmarks().Get(0))
	alignImg4 := utils.CropAlignFaceLandmark(img4, evalResults.Get(1).GetLandmarks().Get(0))

	faceId.RegisterIdImage("colin_powell", alignImg1)
	faceId.RegisterIdImage("george_robertson", alignImg3)

	predictImages := face.NewMultiImage()
	predictImages.Add(alignImg2)
	predictImages.Add(alignImg4)

	results := faceId.PredictBatch(predictImages)

	// Run with registration & predict style to print scores for validating with other language applications
	// since isTheSamePerson API doesn't provide any score.
	fmt.Printf("First person nearest node distance: %.8f\n", results.Get(0).GetNearestNodeDistance())
	fmt.Printf("Second person nearest node distance: %.8f\n", results.Get(1).GetNearestNodeDistance())
	fmt.Println("First person:", results.Get(0).GetId())
	fmt.Println("Second person:", results.Get(1).GetId())

	// Also test the actual face verification API (IsTheSamePerson)
	isSame := faceId.IsTheSamePersonImg(img1, img2)
	fmt.Println("Is the same person (colin_powell):", utility.BoolToInt(isSame))

	isSame = faceId.IsTheSamePersonImg(img3, img4)
	fmt.Println("Is the same person (george_robertson):", utility.BoolToInt(isSame))

	report := faceDetector.GetUsage()
	report.ToLog()

	report = faceId.GetUsage()
	report.ToLog()
}
