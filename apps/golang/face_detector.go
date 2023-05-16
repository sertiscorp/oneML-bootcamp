package main

import (
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

	oneMLUtils := face.NewUtils(licenseManager)
	faceDetector := face.NewFaceDetector(licenseManager)
	defer face.DeleteUtils(oneMLUtils)
	defer face.DeleteFaceDetector(faceDetector)

	// load Image
	filePath := path.Join(testAssetPath, "face-detect-set/face/0.jpg")
	input := oneMLUtils.ReadImageCV(filePath)

	result := faceDetector.Detect(input)

	fmt.Println("Faces:", result.GetSize())
	for i := 0; i < result.GetSize(); i++ {
		bbox := result.GetBboxes().Get(i)
		landmarks := result.GetLandmarks().Get(i)

		fmt.Println("Face", i)
		fmt.Printf("Score: %.6f\n", result.GetScores().Get(i))
		fmt.Println("Pose:", face.PoseToText(result.GetPoses().Get(i)))
		fmt.Printf("BBox: [(%.6f, %.6f), (%.6f, %.6f)]\n", bbox.GetTop(), bbox.GetLeft(), bbox.GetBottom(), bbox.GetRight())

		for j := 0; j < 5; j++ {
			fmt.Printf("Landmark %d: (%.6f, %.6f)\n", j, landmarks.GetX().Get(j), landmarks.GetY().Get(j))
		}
	}

}
