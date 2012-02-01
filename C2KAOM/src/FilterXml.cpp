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

#include <FilterXml.hh>
#include <iostream>

FilterXml::~FilterXml() {
	// nothing
}

FilterXml::FilterXml(std::string filename) :
		fileName_(filename) {
}

FilterXml::FilterXml() {
	std::cout << "Enter XML-Filename: " << std::endl;
	std::cin >> fileName_;
}

int FilterXml::filter() {
	//First the xml document is evaluated to a xpath node set
	xmltoXpath();

	// todo debug
	//the leading entry is the filename tag
	outputFileName_ = set_comment_[0].node().first_child().value();
	std::cout << outputFileName_;
	outputFileName_ = outputFileName_.substr(0, outputFileName_.length() - 4) + ".kaot";
	std::cout << "->" << outputFileName_ << std::endl;
	//each method has two entries
	std::cout << "Found " << set_comment_.size() / 2 << " Methods with labeled comments." << std::endl;


	//if kaom discriptions are found
	//then the xpath node set is evaluated to a queue
	if (set_comment_.size() > 1) {
		xpathtoQueue();
		return 0;
	} else {
		//else nothing was found and here is nothing to do
		return 1;
	}
}

bool FilterXml::loadFile() {
	//load file specified in fileName_
	pugi::xml_parse_result result = doc_.load_file(fileName_.c_str());
	//todo debug
	//std::cout << "Load result: " << result.description() << std::endl;
	return result.operator bool();
}

void FilterXml::xmltoXpath() {
	//todo debug
	//std::cout << "Using XPath: " << std::endl;

	//search for name and briefdescription tags in the definition of methods
	//comments marked with @kaom are stored in briefdescription tags
	pugi::xpath_query query_comment("//memberdef/briefdescription/para | //memberdef/briefdescription/para/preceding::name[1] | //compoundname");

	//store the result in a node set
	set_comment_ = query_comment.evaluate_node_set(doc_);
}

void FilterXml::xpathtoQueue() {
	//local variable used to store methods name witch are not explicit set
	std::string backupName;

	//evaluate all other entries in set_comment
	for (unsigned int i = 1; i < set_comment_.size(); ++i) {

		//clear global entries for new use
		comment_.clear();
		entity_.clear();

		//the first entry is the name tag
		backupName = set_comment_[i].node().first_child().value();

		//the second entry is the briefdescription
		comment_.append(set_comment_[++i].node().first_child().value());

		//todo rename
		//at the type information to the entity
		entity_.append("kind: " + backupName + ";");

		//if a input value is found append it to the entity else do nothing
		if (std::string::npos != comment_.find("input:")) {
			buildResult("input:");
		}
		//if a output value is found append it to the entity else do nothing
		if (std::string::npos != comment_.find("output:")) {

			buildResult("output:");
		}
		//if a link value is found append it to the entity else do nothing
		if (std::string::npos != comment_.find("link:")) {

			buildResult("link:");
		}

		//if a container value is found append it to the entity else do nothing
		if (std::string::npos != comment_.find("content:")) {

			buildResult("content:");
		}

		//if a toplevel value is found append toplevel to the entity else do nothing
		if (std::string::npos != comment_.find("toplevel:")) {

			buildResult("toplevel:");
		}

		//store the finished entity in the queue
		result_.push(entity_);
	}
}

void FilterXml::buildResult(std::string keyword) {
	//local variables for append command
	int startPos, endPos;

	startPos = comment_.find(keyword);
	endPos = comment_.find(";", startPos);

	//compute length of content
	endPos = endPos - startPos + 1;

	//append content to the entity
	entity_.append(comment_.substr(startPos, endPos));
}
