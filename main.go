package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

var testData = map[Name]Neighbors{
	"a": {
		"b": 2,
		"c": 6,
	},
	"b": {
		"a": 2,
		"c": 3,
		"d": 5,
	},
	"c": {
		"a": 6,
		"b": 3,
		"d": 1,
	},
	"d": {
		"c": 1,
		"b": 5,
	},
	"e": {},
}

func changePath(a Name, b Name, price Cost) {
	testData[a][b] = price
	testData[b][a] = price
}

func delPath(a Name, b Name) {
	delete(testData[a], b)
	delete(testData[b], a)
}

func delNode(a Name) {
	for k := range testData {
		delete(testData[k], a)
	}
	delete(testData, a)
}

func findPath(a Name, b Name) {
	r := GetPath(testData, "a")
	if int(r["b"]) == 255 {
		fmt.Println("not found")
	} else {
		fmt.Println("found, cost: ", int(r["b"]))
	}
}

func getRandomNode() Name {
	var keys []Name
	for k := range testData {
		keys = append(keys, k)
	}
	return keys[rand.Intn(len(keys))]
}

func getRandomPath() (Name, Name) {
	mapIsEmpty := true
	for k := range testData {
		if len(testData[k]) > 0 {
			mapIsEmpty = false
			break
		}
	}
	if mapIsEmpty {
		panic("map is empty")
	}
	nodeIsEmpty := true
	a := getRandomNode()
	r := GetPath(testData, a)
	for nodeIsEmpty {
		a = getRandomNode()
		r = GetPath(testData, a)
		//check if there is a path
		for _, node := range r {
			if node != 255 {
				nodeIsEmpty = false
				break
			}
		}
	}
	for {
		b := getRandomNode()
		if int(r[b]) != 255 {
			return a, b
		}
	}
}

func getRandomNodesWithoutPath() (Name, Name) {
	mapIsFull := true
	for k := range testData {
		if len(testData[k]) < len(testData)-1 {
			mapIsFull = false
			break
		}
	}
	if mapIsFull {
		panic("map is full")
	}
	nodeIsFull := true
	a := getRandomNode()
	r := GetPath(testData, a)
	for nodeIsFull {
		a = getRandomNode()
		r = GetPath(testData, a)
		//check if there is a path
		for _, node := range r {
			if node == 255 {
				nodeIsFull = false
				break
			}
		}
	}
	for {
		b := getRandomNode()
		if int(r[b]) == 255 && a != b {
			return a, b
		}
	}
}

func isNodeExist(a Name) bool {
	_, ok := testData[a]
	return ok
}

func main() {

	var mutex sync.RWMutex
	wg := sync.WaitGroup{}

	go func(mutex *sync.RWMutex) {
		for {
			time.Sleep(time.Second)

			mutex.Lock()
			fmt.Println("				change path")
			time.Sleep(time.Millisecond * 100)
			a, b := getRandomPath()
			changePath(a, b, Cost(rand.Intn(10)+1))
			fmt.Println("change cost", a, b)
			mutex.Unlock()
		}
	}(&mutex)

	go func(mutex *sync.RWMutex) {
		for {
			time.Sleep(time.Second)

			mutex.Lock()
			fmt.Println("				delete 1")
			time.Sleep(time.Millisecond * 100)
			a, b := getRandomPath()
			delPath(a, b)
			fmt.Println("del path", a, b)
			mutex.Unlock()

			time.Sleep(time.Second * 2)

			mutex.Lock()
			fmt.Println("				add 1")
			time.Sleep(time.Millisecond * 100)
			a, b = getRandomNodesWithoutPath()
			changePath(a, b, Cost(rand.Intn(10)+1))
			fmt.Println("add path", a, b)
			mutex.Unlock()
		}
	}(&mutex)

	go func(mutex *sync.RWMutex) {
		for {
			time.Sleep(time.Second)

			mutex.Lock()
			fmt.Println("				delete 2")
			time.Sleep(time.Millisecond * 100)
			a := getRandomNode()
			delNode(a)
			fmt.Println("del node: ", a)
			mutex.Unlock()

			time.Sleep(time.Second)

			mutex.Lock()
			fmt.Println("				add 2")
			time.Sleep(time.Millisecond * 100)
			b := Name(rand.Intn(50) + 10)
			for {
				b := Name(rand.Intn(50) + 10)
				if !isNodeExist(b) {
					break
				}
			}
			testData[b] = make(Neighbors)
			fmt.Println("add node: ", b)
			mutex.Unlock()
		}
	}(&mutex)

	go func(mutex *sync.RWMutex) {
		for {
			time.Sleep(time.Second)
			mutex.Lock()
			fmt.Println("				finding path")
			time.Sleep(time.Millisecond * 100)
			findPath(getRandomNode(), getRandomNode())
			mutex.Unlock()
		}
	}(&mutex)

	wg.Add(4)
	wg.Wait()
}
