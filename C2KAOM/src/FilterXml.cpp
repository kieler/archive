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

FilterXml::FilterXml(string filename) :
		fileName_(filename) {
}

int FilterXml::filter() {
	//evaluate the XML-file to a XPath node set
	xmltoXpath();

	//the leading entry is the filename tag
	//print the file name and the number of annotations found on the console
	outputFileName_ = set_comment_[0].node().first_child().value();
	cout << outputFileName_;
	outputFileName_ = outputFileName_.substr(0, outputFileName_.rfind(".")) + ".kaot";
	cout << "->" << outputFileName_ << " -- Found " << set_comment_.size() / 2 << " Objects with labeled comments --" << endl;

	//if nothing was found except the file name, then there is nothing to do here
	//else the xpath node set is evaluated to a queue
	if ((set_comment_.size() > 1) && (xpathtoQueue() == 0)) {
		return 0;
	} else {
		return 1;
	}
}

bool FilterXml::loadFile() {
	//load file specified XML-file with pugixml
	pugi::xml_parse_result result = doc_.load_file(fileName_.c_str());
	return result.operator bool();
}

int FilterXml::xmltoXpath() {
	//search for function names and briefdescription tags in the definition of methods
	//comments marked with @kaom are stored in briefdescription tags
	//the filename is stored in compoundname
	pugi::xpath_query query_comment("//memberdef/briefdescription/para | //memberdef/briefdescription/para/preceding::name[1] | //compoundname");

	//store the result in a node set
	set_comment_ = query_comment.evaluate_node_set(doc_);
	return 0;
}

int FilterXml::xpathtoQueue() {
	//analyze all entries in set_comment_
	for (unsigned int i = 1; i < set_comment_.size(); ++i) {

		//add the type information to the entity
		backupName_ = set_comment_[i].node().first_child().value();
		entity_.append("kind: " + backupName_ + ";");

		//search in the briefdescription tags for keywords
		//and add every found keyword to the entity
		comment_.append(set_comment_[++i].node().first_child().value());
		startPos_ = comment_.find("input:");
		if (string::npos != startPos_) {
			buildResult("input:");
		}
		startPos_ = comment_.find("output:");
		if (string::npos != startPos_) {
			buildResult("output:");
		}
		startPos_ = comment_.find("link:");
		if (string::npos != startPos_) {
			buildResult("link:");
		}
		startPos_ = comment_.find("content:");
		if (string::npos != startPos_) {
			buildResult("content:");
		}
		//normaly there is no toplevel
		startPos_ = comment_.find("toplevel:");
		if (string::npos == startPos_) {
		} else {
			buildResult("toplevel:");
		}

		//store the entity in the queue
		if (!entity_.empty()) {
			result_.push(entity_);
		} else {
			cerr << "Found annotation without content for " << backupName_ << " ->skipping" << endl;
		}

		//clear global entries for next round
		comment_.clear();
		entity_.clear();
	}

	//test if a entity with annotations were found
	if (!result_.empty()) {
		return 0;
	} else {
		return 1;
	}
}

int FilterXml::buildResult(string keyword) {
	//local ending position
	unsigned int endPos;

	//find the marked ending
	endPos = comment_.find(";", startPos_);
	if (endPos == string::npos) {
		cerr << "Missing semicolon after " << keyword << " in the annotation of " << backupName_ << endl;
	}

	//compute length of content
	endPos -= startPos_;
	endPos++;

	//append content to the entity
	entity_.append(comment_.substr(startPos_, endPos));
	return 0;
}
