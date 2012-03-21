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

//extract comment line marked with KAOM

#ifndef FILTERXML_HH_
#define FILTERXML_HH_

#include <pugixml.hpp>
#include <string>
#include <queue>

using namespace std;

class FilterXml {
public:

	//constructor with arguments to set fileName_
	FilterXml(string filename);

	//simple destructor
	virtual ~FilterXml();

	//main function calling the internal functions
	int filter();

	//load inputfile
	bool loadFile();

	//getter result
	inline queue<string> GetResult() {
		return result_;
	}

	//getter outputFileName
	inline string GetOutputFileName() {
		return outputFileName_;
	}

private:

	//filter comment tags and store them in set_comment
	int xmltoXpath();

	//take tags from set_comment and filter tag marked with KAOM; store these tags in result
	int xpathtoQueue();

	//build the tags for the result
	int buildResult(string keyword);

	// empty queue for the result
	queue<string> result_;
	//internal variables
	string fileName_, comment_, entity_, outputFileName_,backupName_;
	pugi::xpath_node_set set_comment_;
	pugi::xml_document doc_;
	unsigned int startPos_;

};

#endif /* FILTERXML_HH_ */
