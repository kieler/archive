/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2011 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */

//creates kaom code from the input queue
#ifndef BUILDERKAOM_HH_
#define BUILDERKAOM_HH_

#include <queue>
#include <string>
#include <map>
#include <fstream>
#include <iostream>
#include <algorithm>
#include <Templates.hh>

using namespace std;

class BuilderKaom {
public:

	BuilderKaom();
	BuilderKaom(queue<string> input, string filename);

	virtual ~BuilderKaom();

	//getter result
	inline string GetResult() {
		return result_;
	}

	//getter input
	inline queue<string> GetInput() {
		return input_;
	}

	//main method calling the internal methods
	void buildResult();

	//build priority queue
	void buildArgsQueue(string keyword);

	void  ReplaceSpecial(string& str);

private:

	//delete blank char before and after one of these chars ", ; : < -"
	int deleteBlank();

	//fill internal map with koam code for every method from the input queue
	void extractArgument();

	//build kaom code from the internal map
	void composeArgument();

	//write the result to the output file
	int SaveKaom(const string &filename);

	//test if the output file is valid
	bool Valid();

	//priority queue that store the a argument and its position in the entity
	//least position at top
	priority_queue<pair<unsigned int, string> ,vector<pair<unsigned int, string> > , PairComparator<unsigned int, string> > argsQueue_;
	map<string, string> entityMap_;
	// empty queue for the arguments
	queue<string> input_;
	//empty strings for the result,the name of the output file and a temporary for the map entries
	string outputFileName_, result_ , mapEntry_;
	//internal variables
	string fileName_, comment_, entity_, entityType_, linkEntity_;
	bool isLinked_;

};

#endif /* BUILDERKAOM_HH_ */
