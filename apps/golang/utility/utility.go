package utility

func BoolToInt(v bool) int {
	if v {
		return 1
	}
	return 0
}