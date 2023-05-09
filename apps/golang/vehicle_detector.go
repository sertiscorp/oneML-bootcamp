package main

import (
	"fmt"
	"oneml/alpr"
	"os"
	"path"
)

func main() {
	currentPath, _ := os.Getwd()
	testAssetPath := path.Join(currentPath, "../../assets/images")

	licenseManager := alpr.NewLicenseManager()
	licenseManager.ActivateTrial()

	// load Image
	filePath := path.Join(testAssetPath, "vehicle-detect-set/vehicle/cars.jpg")
	oneMLUtils := alpr.NewUtils(licenseManager)
	defer alpr.DeleteUtils(oneMLUtils)
	img := oneMLUtils.ReadImageCV(filePath)

	// create vehicle detector
	vehicleDetector := alpr.NewVehicleDetector(licenseManager)
	defer alpr.DeleteVehicleDetector(vehicleDetector)
	result := vehicleDetector.Detect(img)

	fmt.Println("Vehicles:", result.GetSize())
	scores := result.GetScores()
	bboxes := result.GetBboxes()
	for i := 0; i < result.GetSize(); i++ {
		fmt.Println("Vehicle", i)
		fmt.Printf("Score: %.6f\n", scores.Get(i))
		fmt.Printf("BBox[top=%.6f,left=%.6f,bottom=%.6f,right=%.6f]\n", bboxes.Get(i).GetTop(), bboxes.Get(i).GetLeft(), bboxes.Get(i).GetBottom(), bboxes.Get(i).GetRight())
	}

}
