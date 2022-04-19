module faceapps

go 1.15

// A relative path to oneML's golang binding files.
// NOTE: an environment var is not allowed here.
replace oneml => ../../assets/binaries/x86_64/bindings/go

require (
	github.com/bitly/go-simplejson v0.5.0
	github.com/bmizerany/assert v0.0.0-20160611221934-b7ed37b82869 // indirect
	github.com/google/uuid v1.3.0
	github.com/gorilla/mux v1.8.0
	github.com/kinbiko/jsonassert v1.0.2
	github.com/kr/pretty v0.3.0 // indirect
	github.com/sirupsen/logrus v1.8.1
	github.com/stretchr/testify v1.7.0
	oneml v0.0.0-00010101000000-000000000000
)
