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
	std::string entity, subEntity, linkEntity, blankLessEntity, replace;

	unsigned int startPos, endPos, foundAt, foundArrow = 0;
	//condition indicates that if there are more entries in the content
	bool condition;

	//read first entry of the queue
	entity = input_.front();

	//find the position of first occurrence in the entry
	startPos = entity.find(keyword) + keyword.length();

	//switch for name, input and output
	switch (keyword[0]) {
	//in case of name
	case 'n':

		//find the marked ending of the content
		endPos = entity.find(";", startPos);
		endPos = endPos - startPos;

		//compute the length of the content
		entity = entity.substr(startPos, endPos);

		//make a copy without spaces for internal identification in kaom
		std::remove_copy(entity.begin(), entity.end(), back_inserter(blankLessEntity), ' ');

		//build content added to result

		entityName_ = blankLessEntity;

		replace = "@portConstraints Free entity " + blankLessEntity + " \"" + entity + "\" {}}";

		//add content to result
		result_.replace(result_.end() - 1, result_.end(), replace);
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
			subEntity = entity.substr(startPos, endPos);

			//make a copy without spaces for internal identification in kaom
			std::remove_copy(subEntity.begin(), subEntity.end(), back_inserter(blankLessEntity), ' ');

			//look for links
			foundArrow = blankLessEntity.find("<-", foundArrow);

			// if a link is found extract the method it comes from and
			if (foundArrow != std::string::npos) {
				//search the position of colon after the arrow
				foundAt = blankLessEntity.find(":", foundArrow + 2);
				//build identification for the source of the link
				linkEntity = blankLessEntity.substr(foundAt + 1) + "_" + blankLessEntity.substr(foundArrow + 2, foundAt - foundArrow - 2);
				//extract identification of the target for koam without spaces
				blankLessEntity = blankLessEntity.substr(0, foundArrow);
				//extract name of the target for koam with spaces
				subEntity = entity.substr(startPos, entity.find("<-", startPos) - startPos);
				isLinked_ = true;
			}

			//build kaom content for current input
			replace = "port " + entityName_ + "_" + blankLessEntity + " \"" + subEntity + "\";}}";

			//store and add kaom content for current links
			linkEntity_ += "link " + linkEntity + " to " + entityName_ + "_" + blankLessEntity + ";";

			//add current input to kaom result
			result_.replace(result_.end() - 2, result_.end(), replace);

			//update variables for next round
			blankLessEntity.clear();
			startPos = entity.find(",", startPos) + 1;
			foundArrow += 2;

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
			subEntity = entity.substr(startPos, endPos);

			//make a copy without spaces for internal identification in kaom
			std::remove_copy(subEntity.begin(), subEntity.end(), back_inserter(blankLessEntity), ' ');

			//build kaom content for current output
			replace = "port " + entityName_ + "_" + blankLessEntity + " \"" + subEntity + "\";}}";

			//add current output to kaom result
			result_.replace(result_.end() - 2, result_.end(), replace);

			//update variables for next round
			blankLessEntity.clear();
			startPos = entity.find(",", startPos) + 1;

			//is this not the last occurrence
		} while (condition);

		break;
	default:
		//nothing
		break;
	}
}

void BuilderKaom::buildResult() {

	//set spacing to 100
	result_.append("@spacing 100.0 entity model{}");

	//delete blankchars in the entries of the queue
	deleteBlank();

	//until the queue is not empty extract the arguments
	while (!input_.empty()) {
		extractArgument("name:");
		extractArgument("input:");
		extractArgument("output:");

		//if a link was found add it now at the end
		if (isLinked_) {
			result_.replace(result_.end() - 2, result_.end(), "}" + linkEntity_ + "}");
			isLinked_ = false;
		}

		//delete edited entry and update the rest of the variables for the next entry
		input_.pop();
		linkEntity_.clear();
		entityName_.clear();
	}

	std::cout << "Start writing file with \n" << result_ << std::endl;
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
