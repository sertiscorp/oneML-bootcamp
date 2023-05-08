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
	padRgb := face.NewFacePad(face.PadType_Rgb, licenseManager)
	padPaper := face.NewFacePad(face.PadType_Paper, licenseManager)
	defer face.DeleteUtils(oneMLUtils)
	defer face.DeleteFacePad(padPaper)
	defer face.DeleteFacePad(padRgb)

	// PAD RGB
	// load Image
	filePath := path.Join(testAssetPath, "pad-rgb-set/spoof/1.jpg")
	input := oneMLUtils.ReadImageCV(filePath)

	result := padRgb.Classify(input)

	fmt.Println("Status: ", result.GetReturnStatus())
	fmt.Printf("Spoof probability: %.6f\n", result.GetSpoofProb())
	fmt.Println("Spoof classification:", result.IsSpoof())

	// PAD PAPER
	// load Image
	filePath = path.Join(testAssetPath, "pad-paper-set/spoof/1.jpg")
	input = oneMLUtils.ReadImageCV(filePath)

	result = padPaper.Classify(input)

	fmt.Println("Status: ", result.GetReturnStatus())
	fmt.Printf("Spoof probability: %.6f\n", result.GetSpoofProb())
	fmt.Println("Spoof classification:", result.IsSpoof())

}
