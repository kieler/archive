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

/*
 * The class BuilderKaom realizes the synthesis of the data flow diagrams to KAOM-model.
 * It also realizes that the data flow diagrams are stored as .kaot-files in the destination directory.
 */

class BuilderKaom {
public:

	//simple constructor with annotation queue and target file path
	BuilderKaom(queue<string> input, string filename);

	virtual ~BuilderKaom();

	//getter result_
	inline string GetResult() {
		return result_;
	}

	//getter input_
	inline queue<string> GetInput() {
		return input_;
	}

	//main method calling the internal methods and fill the internal map with patterns
	void buildResult();

	//build priority queue
	void buildArgsQueue(string keyword);

	//replace all chars expect 32,48-58,65-90,95,97-122 (ASCII) from a string with a underline
	void replaceSpecChar(string& str);

private:

	//delete all blank chars before and after one of the chars ", ; : - > ."
	int deleteBlank();

	//build a pattern for every entity from the input queue
	void buildPattern();

	//build KAOM-code from the internal map
	void buildKaom();

	//write the result to the output file
	int saveKaom(const string &filename);

	//priority queue that store the a argument and its position in the entity, least position at top
	priority_queue<pair<unsigned int, string> ,vector<pair<unsigned int, string> > , PairComparator<unsigned int, string> > argsQ_;
	//map which contains the function patterns
	map<string, string> entityMap_;
	//empty queue for the annotations
	queue<string> input_;
	//strings for the the name of the output file, the result and the elemts for the map
	string outputFileName_, result_ , mapEntry_;
	//internal variables
	string fileName_, comment_, entity_, entityType_, linkEntity_;
	bool isValid_;

};

#endif /* BUILDERKAOM_HH_ */
