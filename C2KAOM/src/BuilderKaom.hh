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

#include <string>
#include <queue>
#include <fstream>
#include <iostream>
#include <algorithm>

class BuilderKaom {
public:

	BuilderKaom();
	BuilderKaom(std::queue<std::string> input, std::string filename);

	virtual ~BuilderKaom();

	//getter result
	inline std::string GetResult() {
		return result_;
	}

	//getter input
	inline std::queue<std::string> GetInput() {
		return input_;
	}

	//main method calling the internal methods
	void buildResult();

private:

	//delete blank char before and after one of these chars ", ; : < -"
	void deleteBlank();

	//build kaom code from the input queue
	void extractArgument(std::string keyword);

	//write the result to the output file
	int SaveKaom(const std::string &filename);

	//test if the output file is valid
	bool Valid();

	//empty strings for the result and name of the output file
	std::string result_, outputFileName_;
	// empty queue for the arguments
	std::queue<std::string> input_;
	//internal variables
	std::string fileName_, comment_, entity_, entityName_,linkEntity_;
	bool isLinked_;

};

#endif /* BUILDERKAOM_HH_ */
