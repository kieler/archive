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

#ifndef FILTERXML_HH_
#define FILTERXML_HH_

#include <pugixml.hpp>
#include <string>
#include <queue>
#include <map>

/*
 * The class FilterXml realizes the execution of XPath
 * and thus the conversion from XML to the filtered annotations.
 */

using namespace std;

class FilterXml {
public:

	//constructor with arguments to set fileName_
	FilterXml(string filename);

	//simple destructor
	virtual ~FilterXml();

	//main function which calls the internal functions
	int filter();

	//load input file
	bool loadFile();

	//getter result_
	inline queue<string> GetResult() {
		return result_;
	}

	//getter outputFileName_
	inline string GetOutputFileName() {
		return outputFileName_;
	}

private:

	//filter compound descriptions and store them in set_comment
	int xpathCompound();

	//filter member and innerclass descriptions and store them in set_comment
	int xpathMember();

	//take tags from set_comment and filter tag marked with KAOM; store these tags in result
	int xpathtoQueue();

	//take tags from set_comment and filter tag marked with KAOM; store these tags in externMap_
	int xpathtoMap();

	//build the tags for the result
	int buildResult(string keyword);

	//map which contains annotations from other XML-Files -- used for headers which are bursted to multiple XML-Files
	static map<string, string> globalMap_;
	// empty queue for the result
	queue<string> result_;
	//internal variables
	string fileName_, comment_, entity_, outputFileName_,backupName_;
	pugi::xpath_node_set set_comment_;
	pugi::xml_document doc_;
	unsigned int startPos_;

};

#endif /* FILTERXML_HH_ */
