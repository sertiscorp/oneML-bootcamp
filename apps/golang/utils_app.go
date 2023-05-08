package main

import (
	"fmt"
	"oneml/face"
	"os"
	"path"
)

func main() {
	// Read an image with oneML API
	currentPath, _ := os.Getwd()
	testAssetPath := path.Join(currentPath, "../../assets/images")
	filePath := path.Join(testAssetPath, "face-detect-set/face/0.jpg")
	fmt.Println("Loading an image from", filePath)

	// Initialize
	licenseManager := face.NewLicenseManager()
	licenseManager.ActivateTrial()

	oneMLUtils := face.NewUtils(licenseManager)
	defer face.DeleteUtils(oneMLUtils)
	image := oneMLUtils.ReadImageCV(filePath)

	// Remove these lines if CropAlignFaceLandmark is not available.
	// Detect face landmarks and execute CropAlignFaceLandmark
	faceDetector := face.NewFaceDetector(licenseManager)
	result := faceDetector.Detect(image)

	fmt.Println("Faces:", result.GetSize())
	for i := 0; i < result.GetSize(); i++ {
		landmarks := result.GetLandmarks().Get(i)
		cropImage := oneMLUtils.CropAlignFaceLandmark(image, landmarks)
		fmt.Printf("Cropped face: i=%d\n", i)
		fmt.Printf("cropImage: %T\n", cropImage)
	}

}
