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
	//First the xml document is evaluated to a xpath node set
	xmltoXpath();

	//the leading entry is the filename tag
	outputFileName_ = set_comment_[0].node().first_child().value();
	cout << outputFileName_;
	outputFileName_ = outputFileName_.substr(0, outputFileName_.rfind(".")) + ".kaot";
	cout << "->" << outputFileName_;
	//each method has two entries
	cout << " -- Found " << set_comment_.size() / 2 << " Objects with labeled comments --" << endl;

	//if nothing was found except the file name, there's nothing to do here.
	if ((set_comment_.size() < 2) || (xpathtoQueue() == 1)) {
		return 1;
	}
	//then the xpath node set is evaluated to a queue
	return 0;

}

bool FilterXml::loadFile() {
	//load file specified in fileName_
	pugi::xml_parse_result result = doc_.load_file(fileName_.c_str());
	return result.operator bool();
}

int FilterXml::xmltoXpath() {
	//search for name and briefdescription tags in the definition of methods
	//comments marked with @kaom are stored in briefdescription tags
	pugi::xpath_query query_comment("//memberdef/briefdescription/para | //memberdef/briefdescription/para/preceding::name[1] | //compoundname");

	//store the result in a node set
	set_comment_ = query_comment.evaluate_node_set(doc_);
	return 0;
}

int FilterXml::xpathtoQueue() {
	//local variable used to store methods name witch are not explicit set

	//evaluate all other entries in set_comment
	for (unsigned int i = 1; i < set_comment_.size(); ++i) {

		//the first entry is the name tag
		backupName_ = set_comment_[i].node().first_child().value();

		//the second entry is the briefdescription
		comment_.append(set_comment_[++i].node().first_child().value());

		//todo rename
		//add the type information to the entity
		entity_.append("kind: " + backupName_ + ";");

		//if a input value is found append it to the entity else do nothing
		startPos_ = comment_.find("input:");
		if (string::npos != startPos_) {
			buildResult("input:");
		}
		//if a output value is found append it to the entity else do nothing
		startPos_ = comment_.find("output:");
		if (string::npos != startPos_) {
			buildResult("output:");
		}
		//if a link value is found append it to the entity else do nothing
		startPos_ = comment_.find("link:");
		if (string::npos != startPos_) {
			buildResult("link:");
		}
		//if a container value is found append it to the entity else do nothing
		startPos_ = comment_.find("content:");
		if (string::npos != startPos_) {
			buildResult("content:");
		}
		//if a toplevel value is found append toplevel to the entity else do nothing
		startPos_ = comment_.find("toplevel:");
		if (string::npos == startPos_) {

		} else {
			buildResult("toplevel:");
		}

		//store the finished entity in the queue
		result_.push(entity_);

		//clear global entries for new use
		comment_.clear();
		entity_.clear();
	}
	if (!result_.empty()) {
		return 0;
	} else {
		return 1;
	}
}

int FilterXml::buildResult(string keyword) {
	//local variables for append command
	unsigned int endPos;

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
