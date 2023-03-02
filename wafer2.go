package main

import (
	"encoding/json"
	"fmt"
	"math/rand"
	"net/http"
	"time"
)

// Defining the Wafer struct that will hold the wafer information
type Wafer struct {
	Etime    string
	Stime    string
	Diameter int
	Numchip  float64
}

var global int

// Creating an instance of Wafer with predefined values for the initial run
var w2 = Wafer{Etime: time.Now().Add(40 * time.Minute).Format("2006-01-02 15:04:05"), Stime: time.Now().Format("2006-01-02 15:04:05"), Numchip: 200, Diameter: 20}

// The main function that runs the HTTP server
func main() {
	global = 0
	// Creating a handler function for the "/wafers" endpoint
	http.HandleFunc("/wafers", handleUsers)
	// Creating a default handler function for other endpoints
	http.HandleFunc("/", index)
	// Starting the HTTP server on port 4445
	fmt.Println("starting server")
	http.ListenAndServe(":4445", nil)
}

// The default handler function that sets the content type of the response
func index(w http.ResponseWriter, req *http.Request) {
	w.Header().Add("Content-Type", "application/json")
}

// The handler function for the "/wafers" endpoint
func handleUsers(w http.ResponseWriter, req *http.Request) {
	// Checking the request method
	if req.Method == "GET" {
		// If the request method is GET, call the get function
		get(w, req)
	} else {
		// If the request method is not GET, return an error message
		errorHandler(w, req, http.StatusMethodNotAllowed, fmt.Errorf("Invalid Method"))
	}
}

// The function that handles GET requests for the "/wafers" endpoint
func get(w http.ResponseWriter, req *http.Request) {
	// http://localhost:8090/wafers
	var result []byte
	var err error
	// Marshaling the Wafer instance into a JSON object
	result, err = json.Marshal(w2)
	if err != nil {
		// If there is an error marshaling the Wafer instance, return an error message
		errorHandler(w, req, http.StatusInternalServerError, err)
		return
	}
	// Setting the content type of the response
	w.Header().Add("Content-Type", "application/json")
	fmt.Fprintf(w, string(result))
	// Waiting for a random amount of time between 0 and 9 seconds
	v := (rand.Intn(10))
	time.Sleep(time.Duration(v) * (time.Second))
	// Updating the values of the Wafer instance with random values
	w2.Numchip = w2.Numchip + 1
	w2.Etime = randate().String()
	w2.Stime = randate().String()
	w2.Diameter = rand.Intn(2000-50) + 50

}

// The function that generates a random date within a certain range
func randate() time.Time {
	min := time.Date(2023, 1, 0, 0, 0, 0, 0, time.UTC).Unix()
	max := time.Date(2024, 1, 0, 0, 0, 0, 0, time.UTC).Unix()
	delta := max - min

	sec := rand.Int63n(delta) + min
	return time.Unix(sec, 0)

}

// function is designed to handle and format errors that occur during HTTP requests
func errorHandler(w http.ResponseWriter, req *http.Request, status int, err error) {
	w.WriteHeader(status)

	w.Header().Add("Content-Type", "application/json")
	fmt.Fprintf(w, `{error:%v}`, err.Error())
}
