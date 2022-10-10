package main

import (
	"faceapps/utility"
	"fmt"
	"oneml/face"
	"os"
	"path"
)

func PrintResult(bbox face.BBox, pose int, lm face.Landmark5) {
	X := lm.GetX()
	Y := lm.GetY()
	fmt.Printf("BBox: BBox[top=%.6f,left=%.6f,bottom=%.6f,right=%.6f]\n", bbox.GetTop(), bbox.GetLeft(), bbox.GetBottom(), bbox.GetRight())
	fmt.Println("Pose:", face.PoseToText(pose))
	fmt.Printf("Landmarks: FaceLandmark5[x1=%.6f,x2=%.6f,x3=%.6f,x4=%.6f,x5=%.6f,y1=%.6f,y2=%.6f,y3=%.6f,y4=%.6f,y5=%.6f]\n",
		X.Get(0), X.Get(1), X.Get(2), X.Get(3), X.Get(4), Y.Get(0), Y.Get(1), Y.Get(2), Y.Get(3), Y.Get(4))
}

func main() {
	licenseManager := face.NewLicenseManager()
	licenseManager.ActivateTrial()

	currentPath, _ := os.Getwd()
	testAssetPath := path.Join(currentPath, "../../assets/images")

	oneMLUtils := face.NewUtils(licenseManager)
	ekyc := face.NewEKYC(licenseManager)
	defer face.DeleteEKYC(ekyc)
	defer face.DeleteUtils(oneMLUtils)

	filePath1 := path.Join(testAssetPath, "face-detect-set/face/8.jpg")
	filePath2 := path.Join(testAssetPath, "face-detect-set/face/9.jpg")
	image1 := oneMLUtils.ReadImageCV(filePath1)
	image2 := oneMLUtils.ReadImageCV(filePath2)

	ops := face.NewEKYCOps(true, true)

	// run EKYC
	result := ekyc.Run(image1, image2, ops, ops)

	distance := result.GetDistance()
	isSamePerson := result.IsSamePerson()
	bboxes := result.GetBboxes()
	landmarks := result.GetLandmarks()
	poses := result.GetFacePoses()
	status := result.GetReturnStatus()

	fmt.Println("Status:", status)
	fmt.Println("Same:", utility.BoolToInt(isSamePerson))
	fmt.Printf("Distance: %.6f\n", distance)

	fmt.Println("Face 1")
	bbox1 := bboxes.Get(0)
	pose1 := poses.Get(0)
	lm1 := landmarks.Get(0)
	PrintResult(bbox1, pose1, lm1)

	fmt.Println("Face 2")
	bbox2 := bboxes.Get(1)
	pose2 := poses.Get(1)
	lm2 := landmarks.Get(1)
	PrintResult(bbox2, pose2, lm2)

	report := ekyc.GetUsage()
	report.ToLog()
}
