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

class FilterXml {
public:

	//constructor with no arguments to manually set fileName_
	FilterXml();

	//constructor with arguments to set fileName_
	FilterXml(std::string filename);

	//simple destructor
	virtual ~FilterXml();

	//main function calling the internal functions
	int filter();

	//load inputfile
	bool loadFile();

	//getter result
	inline std::queue<std::string> GetResult() {
		return result_;
	}

	//getter outputFileName
	inline std::string GetOutputFileName() {
		return outputFileName_;
	}

private:

	//filter comment tags and store them in set_comment
	void xmltoXpath();

	//take tags from set_comment and filter tag marked with KAOM; store these tags in result
	void xpathtoQueue();

	//build the tags for the result
	void buildResult(std::string keyword);

	// empty queue for the result
	std::queue<std::string> result_;
	//internal variables
	std::string fileName_, comment_, entity_, outputFileName_;
	pugi::xpath_node_set set_comment_;
	pugi::xml_document doc_;

};

#endif /* FILTERXML_HH_ */
