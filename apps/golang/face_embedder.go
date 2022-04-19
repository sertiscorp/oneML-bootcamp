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
	faceEmbedder := face.NewFaceEmbedder(licenseManager)

	// load Image
	filePath := path.Join(testAssetPath, "register-set/Colin_Powell/colin_powell_0074.jpg")
	input := oneMLUtils.ReadImageCV(filePath)

	result := faceEmbedder.Embed(input)

	fmt.Println("Embedding size:", result.GetSize())

	embed := result.GetEmbedding()
	fmt.Printf("Embedding sample: [%.6f, %.6f, %.6f, %.6f, %.6f]\n", embed.Get(0), embed.Get(1), embed.Get(2), embed.Get(3), embed.Get(4))

	var sum float32 = 0
	for i := 0; i < int(embed.Size()); i++ {
		sum += embed.Get(i)
	}
	
	fmt.Printf("Embedding sum: %.5f\n", sum)
}
