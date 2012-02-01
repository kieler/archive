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

#include <BuilderKaom.hh>

BuilderKaom::~BuilderKaom() {
	// nothing
}

BuilderKaom::BuilderKaom(std::queue<std::string> input, std::string filename) :
		input_(input), outputFileName_(filename), isLinked_(false) {
}

BuilderKaom::BuilderKaom() {
	std::cout << "Need input queue: " << std::endl;
}

void BuilderKaom::deleteBlank() {

	//local variables used for extract and modify the content of the queue
	std::string entity;
	std::queue<int>::size_type sz = input_.size();

	//for all entries in the queue
	for (unsigned int t = 0; t < sz; t++) {
		entity = input_.front();
		unsigned int found, i = 0;

		//find the first occurrence of ":,;<-" in the string
		found = entity.find_first_of(":,;<-");

		//if one is found erase all blank chars before and after
		//then search the next one
		while (found != std::string::npos) {
			while (entity[found - i - 1] == ' ') {
				i++;
			}
			entity.erase(found - i, i);
			found -= i;
			i = 0;

			while (entity[found + i + 1] == ' ') {
				i++;
			}
			entity.erase(found + 1, i);
			i = 0;
			found = entity.find_first_of(":,;<-", found + 1);
		}

		//if no one more is found write the content back to queue
		input_.push(entity);
		input_.pop();
	}
}

void BuilderKaom::extractArgument(std::string keyword) {
	//local variables used for store and modify sub-contents
	std::string entity, currentEntity, linkEntity, blankLessEntity, replace;

	//todo reallocate
	unsigned int startPos, endPos, foundDot, foundArrow = 0;
	//condition indicates that if there are more entries in the content
	bool condition;

	//read first entry of the queue
	entity = input_.front();

	//find the position of first occurrence in the entry
	startPos = entity.find(keyword);

	if (startPos == std::string::npos) {
		//todo debug
		//std::cout << keyword << " not found in " << entityType_ << std::endl;
		//std::cout << entity << std::endl;
		return;
	} else {
		startPos += keyword.length();
	}

	//switch for type, input and output
	switch (keyword[0]) {
	//in case of type
	case 'k':

		//find the marked ending of the content
		endPos = entity.find(";", startPos);
		endPos = endPos - startPos;

		//compute the length of the content
		entity = entity.substr(startPos, endPos);

		//save type
		entityType_ = entity;

		//start a new entityMap_ entry
		mapEntry_ = " @portConstraints Free entity <blankLess@lias> \" <@lias> \" {";

		break;
		//in case of input
	case 'i':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			if (entity.find(",", startPos) < entity.find(";", startPos)) {
				endPos = entity.find(",", startPos);
				endPos = endPos - startPos;
				condition = true;
			} else {
				endPos = entity.find(";", startPos);
				endPos = endPos - startPos;
				condition = false;
			}

			//extract the current content
			currentEntity = entity.substr(startPos, endPos);

			//make a copy without spaces for internal identification in kaom
			std::remove_copy(currentEntity.begin(), currentEntity.end(), back_inserter(blankLessEntity), ' ');

			//build kaom content for current input
			replace = "port <blankLess@lias>_" + blankLessEntity + " \"" + currentEntity + "\";";

			//add current input to the current entityMap entry
			mapEntry_.append(replace);

			//update variables for next round
			blankLessEntity.clear();
			startPos = entity.find(",", startPos) + 1;

			//is this not the last occurrence
		} while (condition);

		break;
		//in case of output
	case 'o':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			if (entity.find(",", startPos) < entity.find(";", startPos)) {
				endPos = entity.find(",", startPos);
				endPos = endPos - startPos;
				condition = true;
			} else {
				endPos = entity.find(";", startPos);
				endPos = endPos - startPos;
				condition = false;
			}

			//extract the current content
			currentEntity = entity.substr(startPos, endPos);

			//make a copy without spaces for internal identification in kaom
			std::remove_copy(currentEntity.begin(), currentEntity.end(), back_inserter(blankLessEntity), ' ');

			//build kaom content for current output
			replace = "port <blankLess@lias>_" + blankLessEntity + " \"" + currentEntity + "\";";

			//add current output to the current entityMap entry
			mapEntry_.append(replace);

			//update variables for next round
			blankLessEntity.clear();
			startPos = entity.find(",", startPos) + 1;

			//is this not the last occurrence
		} while (condition);
		break;
	case 'l':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			if (entity.find(",", startPos) < entity.find(";", startPos)) {
				endPos = entity.find(",", startPos);
				endPos = endPos - startPos;
				condition = true;
			} else {
				endPos = entity.find(";", startPos);
				endPos = endPos - startPos;
				condition = false;
			}

			//extract the current content
			currentEntity = entity.substr(startPos, endPos);

			//make a copy without spaces for internal identification in kaom
			std::remove_copy(currentEntity.begin(), currentEntity.end(), back_inserter(blankLessEntity), ' ');

			//look for links
			foundArrow = blankLessEntity.find("->");

			//extract identification for the source of the link
			linkEntity = blankLessEntity.substr(foundArrow + 2);

			//search the position of the dot after the arrow
			foundDot = linkEntity.find(".");

			//if a colon is found it's a internal link else it is a link between the current and a internal entity
			if (foundDot != std::string::npos) {
				//replace the colon with a underline
				linkEntity.replace(foundDot, 1, "_");
				linkEntity = "<blankLess@lias>_" + linkEntity;
			} else {
				foundDot = linkEntity.rfind(":", foundArrow);
				if (foundDot == std::string::npos) {
					linkEntity = "<blankLess@lias>_" + linkEntity;
				} else {
					//replace the colon with a underline
					linkEntity.replace(foundDot, 1, "_");
				}
			}

			//extract identification of the target for koam without spaces
			blankLessEntity = blankLessEntity.substr(0, foundArrow);
			//search the position of the dot before the arrow
			foundDot = blankLessEntity.rfind(".", foundArrow);
			//if a colon is found it's a internal link else it is a link between the current and a internal entity
			if (foundDot != std::string::npos) {
				//replace the colon with a underline
				blankLessEntity.replace(foundDot, 1, "_");
				blankLessEntity = "<blankLess@lias>_" + blankLessEntity;
			} else {
				foundDot = blankLessEntity.rfind(":", foundArrow);
				if (foundDot == std::string::npos) {
					blankLessEntity = "<blankLess@lias>_" + blankLessEntity;
				} else {
					//replace the colon with a underline
					blankLessEntity.replace(foundDot, 1, "_");
				}
			}

			//build kaom content for current link
			replace = "link " + blankLessEntity + " to " + linkEntity + ";";

			//add current link to the current entityMap entry
			mapEntry_.append(replace);

			//update variables for next round
			blankLessEntity.clear();
			startPos = entity.find(",", startPos) + 1;

		} while (condition);
		break;
	case 'c':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			if (entity.find(",", startPos) < entity.find(";", startPos)) {
				endPos = entity.find(",", startPos);
				endPos = endPos - startPos;
				condition = true;
			} else {
				endPos = entity.find(";", startPos);
				endPos = endPos - startPos;
				condition = false;
			}

			//extract the current content
			currentEntity = entity.substr(startPos, endPos);

			//build kaom content for current output
			replace = "replace " + currentEntity + ";";

			//add current component to the current entityMap entry
			mapEntry_.append(replace);

			//update variables for next round
			startPos = entity.find(",", startPos) + 1;

			//is this not the last occurrence
		} while (condition);
		break;
	case 't':
		//find the marked ending of the content
		endPos = entity.find(";", startPos);
		endPos = endPos - startPos;

		//compute the length of the content
		currentEntity = entity.substr(startPos, endPos);

		result_.append("replace " + currentEntity + ":" + entityType_ + ";");

		break;
	default:
		//nothing
		break;
	}
}

void BuilderKaom::composeArgument() {
	//close canvas

	//todo reallocate
	unsigned int startPos, endPos, foundColon;
	//condition indicates that if there are more entries in the content
	bool condition;
	std::string currentEntity, currentType, blankLessEntity;

	blankLessEntity.clear();

	//find the position of first occurrence in the entry
	startPos = result_.find("replace ");

	result_.append("}");
	do {
		endPos = result_.find(";", startPos);
		endPos = endPos - startPos;

		currentEntity = result_.substr(startPos, endPos);

		//search the position of the colon
		foundColon = currentEntity.find(":");

		//
		currentType = currentEntity.substr(foundColon + 1);
		currentEntity = currentEntity.substr(8, foundColon - 8);

		//make a copy without spaces for internal identification in kaom
		std::remove_copy(currentEntity.begin(), currentEntity.end(), back_inserter(blankLessEntity), ' ');

		result_.replace(startPos, endPos + 1, entityMap_[currentType]);

		if (result_.find("<@lias>", startPos) < result_.find("replace ", startPos)) {
			endPos = result_.find("<@lias>", startPos);
			result_.replace(endPos, 7, currentEntity);
		}

		do {
			if (result_.find("<blankLess@lias>", startPos) < result_.find("replace ", startPos)) {
				endPos = result_.find("<blankLess@lias>", startPos);
				result_.replace(endPos, 16, blankLessEntity);
				condition = true;
			} else {

				while (result_.find("}", startPos) < result_.find("replace ", startPos)) {
					startPos = result_.find("}", startPos) + 1;
					foundColon = (blankLessEntity.rfind("_") == std::string::npos ? 0 : blankLessEntity.rfind("_"));
					blankLessEntity.erase(blankLessEntity.begin() + foundColon, blankLessEntity.end());
				}
				startPos = result_.find("replace ", startPos);
				if (blankLessEntity.length() > 0)
					blankLessEntity += "_";

				condition = false;
			}
		} while (condition);

		//blankLessEntity.clear();
	} while (startPos != std::string::npos);
}

void BuilderKaom::buildResult() {

	if (input_.empty()) {
		std::cerr << "Empty Queue in builder. This should not happen." << std::endl;
		return;
	}

	//start canvas and set spacing to 100
	result_.append("@spacing 100.0 entity model{");

	//delete blankchars in the entries of the queue
	deleteBlank();

	//until the queue is not empty extract the arguments
	while (!input_.empty()) {
		extractArgument("kind:");
		extractArgument("input:");
		extractArgument("output:");
		extractArgument("link:");
		extractArgument("content:");
		extractArgument("toplevel:");

		//close current entity
		mapEntry_.append("}");

		//create new entry in entityMap_ with key entityType_ and value mapEntry_
		entityMap_[entityType_] = mapEntry_;

		//delete edited entry and update the rest of the variables for the next entry
		input_.pop();
		entityType_.clear();
	}

	//todo debug

	std::map<std::string, std::string>::iterator it;

	// show content:
	for (it = entityMap_.begin(); it != entityMap_.end(); it++)
		std::cout << (*it).first << " => " << (*it).second << std::endl;

	std::cout << result_ << std::endl;

	composeArgument();

	//todo debug
	//std::cout << "Start writing file" << std::endl;
	//std::cout << "with \n" << result_ << std::endl;

	//Write the result to the output file
	SaveKaom(outputFileName_);
}

bool BuilderKaom::Valid() {
	//is the result empty
	return !result_.empty();
}

int BuilderKaom::SaveKaom(const std::string &filename) {

	//is result empty
	if (!Valid()) {
		std::cout << "result_ is empty" << std::endl;
		return -1;
	}
	// open file
	std::ofstream file;
	file.open(filename.c_str(), std::ios::out | std::ios::binary);
	// check if file is ready for writing
	if (!file.good()) {
		std::cout << "file not writable" << std::endl;
		return -1;
	}
	// write data
	file << result_ << std::endl;

	file.close();
	return 0;
}
