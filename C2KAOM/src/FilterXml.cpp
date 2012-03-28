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

map<string, string> FilterXml::globalMap_=map<string, string>();

FilterXml::FilterXml(string filename) :
		fileName_(filename) {
}

int FilterXml::filter() {
	//check if it is a subfiles from a header file
	//if so store the result in the static globalMap and do not build kaom model fpr this XML-file
	xpathCompound();
	if ((set_comment_.size() > 1)) {
		xpathtoMap();
		return 1;
	} else {
		//check if a annotation was found
		//and if so evaluate the xpath node set to a queue
		xpathMember();
		if ((set_comment_.size() > 1) && (xpathtoQueue() == 0)) {

			//the leading entry is the filename
			//print the file name and the number of annotations found on the console
			outputFileName_ = set_comment_[0].node().first_child().value();
			cout << outputFileName_;
			outputFileName_ = outputFileName_.substr(0, outputFileName_.rfind(".")) + ".kaot";
			cout << "->" << outputFileName_ << " -- Found " << result_.size() << " Objects with labeled comments --" << endl;
			return 0;
		} else {
			return 1;
		}
	}
}

bool FilterXml::loadFile() {
	//load file specified XML-file with pugixml
	pugi::xml_parse_result result = doc_.load_file(fileName_.c_str());
	return result.operator bool();
}

int FilterXml::xpathCompound() {
	//search for object names and briefdescription tags in the definition of the object
	//comments marked with @kaom are stored in briefdescription tags
	//the object name is stored in compoundname
	pugi::xpath_query query_comment("//compounddef/briefdescription/para | //compoundname");

	//store the result in a node set
	set_comment_ = query_comment.evaluate_node_set(doc_);
	return 0;
}

int FilterXml::xpathMember() {
	//search for function names and briefdescription tags in the definition of methods
	//comments marked with @kaom are stored in briefdescription tags
	//the filename is stored in compoundname
	pugi::xpath_query query_comment(
			"//memberdef/briefdescription/para | //memberdef/briefdescription/para/preceding::name[1] | //compoundname | //compounddef/innerclass");

	//store the result in a node set
	set_comment_ = query_comment.evaluate_node_set(doc_);
	return 0;
}

int FilterXml::xpathtoQueue() {
	unsigned int i = 1, max = set_comment_.size();

	//look for external entries in set_comment_
	while ((i < max) && (string(set_comment_[i].node().name()) == "innerclass")) {
		//check if the object is listed
		if (globalMap_.count(string(set_comment_[i].node().first_child().value())) > 0) {
			result_.push(globalMap_[string(set_comment_[i].node().first_child().value())]);
		}
		i++;
	}

	//analyze the inner entries in set_comment_
	for (; i < max; ++i) {
		//add the type information to the entity
		backupName_ = set_comment_[i].node().first_child().value();
		entity_.append("kind: " + backupName_ + ";");

		//search in the briefdescription value for the keywords
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

		//clear global variables for next round
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

int FilterXml::xpathtoMap() {
	//add the object names as type information for entity
	backupName_ = set_comment_[0].node().first_child().value();
	entity_.append("kind: " + backupName_ + ";");

	//compose the value of the briefdescription which is stored in the child tags
	for (pugi::xml_node content = set_comment_[1].node().first_child(); content; content = content.next_sibling()) {
		comment_.append(content.value());
		for (pugi::xml_node subContent = content.first_child(); subContent; subContent = subContent.next_sibling()) {
			comment_.append(subContent.value());
		}
	}

	//search in the briefdescription value for the keywords
	//and add every found keyword to the entity
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

	//check if there is already a entity with the same key
	//and then store the entity in the map
	if (!entity_.empty()) {
		if (globalMap_.count(backupName_) > 0) {
			cerr << "Found two annotations for " << backupName_ << " -> skip first annotation" << endl;
		}
		globalMap_[backupName_] = entity_;
	} else {
		cerr << "Found annotation without content for " << backupName_ << " ->skipping" << endl;
	}

	return 0;
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
