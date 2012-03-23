/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2012 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */

#ifndef BUILDERXML_HH_
#define BUILDERXML_HH_

#include <string>
#include <iostream>
#include <fstream>
#include <queue>
#include <dirent.h>
#include <stdlib.h>

using namespace std;

class BuilderXml {
public:

	BuilderXml(string inputPath, string outputPath);

	//simple destructor
	virtual ~BuilderXml();

	int loadDoxyfile();

	int fillDoxyfile();

	int callDoxygen();

	int buildFileQueue();

	//getter inputFiles
	inline queue<string> GetInput() {
		return inputFiles_;
	}

private:
	queue<string> inputFiles_;
	string inputPath_, outputPath_, content_;

};

#endif /* BUILDERXML_HH_ */
