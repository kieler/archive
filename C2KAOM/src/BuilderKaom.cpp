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

BuilderKaom::BuilderKaom(queue<string> input, string filename) :
		input_(input), outputFileName_(filename), isLinked_(false) {

	//todo
	for (int i = 0; i < input_.size(); i++) {
		cout << "constr 1 input_[" << i << "]: " << (input_.front()) << endl;
		input_.push(input_.front());
		input_.pop();
	}
	cout << "constr 1 outputFileName_: " << outputFileName_ << endl << endl;
}

BuilderKaom::BuilderKaom() {
	cout << "Need input queue: " << endl;
}

void BuilderKaom::replaceSpecial(string& str) {

	int max = str.length();

	for (int i = 0; i < max; i++) {
		if ((str[i] == 32) || (str[i] >= 48 && str[i] <= 58) || (str[i] >= 65 && str[i] <= 90) || (str[i] == 95)
				|| (str[i] >= 97 && str[i] <= 122)) {
			if (str[i] == 32) {
				str.erase(i, 1);
				i--;
				max--;
			}
		} else {
			str[i] = '_';
		}

	}

}

int BuilderKaom::deleteBlank() {

	//local variables used for extract and modify the content of the queue
	string entity;
	unsigned int lenght, i, found, sz = input_.size();

	//for all entries in the queue
	for (unsigned int t = 0; t < sz; t++) {
		entity = input_.front();
		i = 0;
		lenght = entity.length();
		//find the first occurrence of ":,;<-" in the string
		found = entity.find_first_of(":,;->.");

		//if one is found erase all blank chars before and after
		//then search the next one
		while (found != string::npos) {
			while ((found > i) && (entity[found - i - 1] == ' ')) {
				i++;
			}
			entity.erase(found - i, i);
			found -= i;
			i = 0;

			while ((found + i + 1 < lenght) && (entity[found + i + 1] == ' ')) {
				i++;
			}
			entity.erase(found + 1, i);
			i = 0;
			found = entity.find_first_of(":,;->.", found + 1);
		}

		//if no one more is found write the content back to queue
		input_.push(entity);
		input_.pop();
	}
	return 1;
}

void BuilderKaom::extractArgument() {
	//local variables used for store and modify sub-contents
	string entity, currentEntity, linkEntity, blankLessEntity, replace, keyword;

	//todo reallocate
	unsigned int startPos, endPos, foundDot, foundArrow = 0, foundSemi;
	//condition indicates that if there are more entries in the content
	bool condition;

	//find the position of first occurrence in the entry
	startPos = argsQueue_.top().first;
	keyword = argsQueue_.top().second;
	argsQueue_.pop();
	if (!argsQueue_.empty()) {
		entity = entity_.substr(startPos, argsQueue_.top().first - startPos);
	} else {
		entity = entity_.substr(startPos);
	}

	//todo
	cout << "args 1 entity_: " << entity_ << "." << endl;
	cout << "args 1 entity: " << entity << "." << endl;

	startPos = keyword.length();

	foundSemi = entity.find(";", startPos);

	if (foundSemi == string::npos) {
		cerr << "Missing semicolon after " << keyword << " in the annotation of " << entityType_ << endl;
		foundSemi = entity.length() - 1;
	}

	if (startPos > foundSemi) {
		cerr << keyword << " in the annotation of " << entityType_ << "is empty and has no semicolon. -> skipping" << endl;
		keyword = "error";
	}

	cout << "args 2 foundSemi: " << foundSemi << "." << endl;
	cout << "args 2 entity.length()-1: " << (entity.length() - 1) << "." << endl;

	//switch for type, input and output
	switch (keyword[0]) {
	//in case of type
	case 'k':
		endPos = foundSemi;
		endPos -= startPos;

		entityType_ = entity.substr(startPos, endPos);

		//todo
		cout << "kind 1 entityType_: " << entityType_ << "." << endl;
		cout << "kind 1 entity: " << entity << "." << endl << endl;

		//start a new entityMap_ entry
		mapEntry_ = " @portConstraints Free entity <blankLess@lias> \"<@lias>\" {";

		break;
		//in case of input
	case 'i':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			if (entity.find(",", startPos) < foundSemi) {
				endPos = entity.find(",", startPos);
				endPos -= startPos;
				condition = true;
			} else {
				endPos = foundSemi;
				endPos -= startPos;
				condition = false;
			}

			if (endPos != 0) {

				//extract the current content
				currentEntity = entity.substr(startPos, endPos);

				//todo
				cout << "input 1 currentEntity: " << currentEntity << "." << endl;
				cout << "input 1 endPos: " << endPos << "." << endl;
				cout << "input 1 startPos: " << startPos << "." << endl;

				//if a dot is there the label the id stand before the dot else the whole id is the label too
				//in every case make a copy without spaces for internal identification in kaom
				endPos = currentEntity.find("\"");
				if (endPos != string::npos) {
					remove_copy(currentEntity.begin(), currentEntity.begin() + endPos, back_inserter(blankLessEntity), ' ');

					foundArrow = currentEntity.find("\"", endPos + 1);

					if (foundArrow != string::npos) {
						currentEntity = currentEntity.substr(endPos + 1, foundArrow - 1 - endPos);
					} else {
						cerr << "found single a quote within " << keyword << " in the annotation of " << entityType_ << " -> skipping label"
								<< endl;
						currentEntity = currentEntity.substr(0, endPos);
					}

				} else {
					remove_copy(currentEntity.begin(), currentEntity.end(), back_inserter(blankLessEntity), ' ');
				}

				//todo
				cout << "input 2 currentEntity: " << currentEntity << "." << endl;
				cout << "input 2 blankLessEntity: " << blankLessEntity << "." << endl;

				replaceSpecial(blankLessEntity);
				//build kaom content for current input
				replace = "port <blankLess@lias>_" + blankLessEntity + " \"" + currentEntity + "\";";

				//todo
				cout << "input 3 blankLessEntity: " << blankLessEntity << "." << endl << endl;

				//add current input to the current entityMap entry
				mapEntry_.append(replace);

				//update variables for next round
				blankLessEntity.clear();

			} else {
				cerr << "Found two consecutive commas within " << keyword << " in the annotation of " << entityType_ << " -> skipping" << endl;
			}
			startPos = entity.find(",", startPos) + 1;
			//is this not the last occurrence
		} while (condition);

		break;
		//in case of output
	case 'o':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			if (entity.find(",", startPos) < foundSemi) {
				endPos = entity.find(",", startPos);
				endPos -= startPos;
				condition = true;
			} else {
				endPos = foundSemi;
				endPos -= startPos;
				condition = false;
			}

			if (endPos != 0) {

				//extract the current content
				currentEntity = entity.substr(startPos, endPos);

				//todo
				cout << "output 1 currentEntity: " << currentEntity << "." << endl;
				cout << "output 1 endPos: " << endPos << "." << endl;
				cout << "output 1 startPos: " << startPos << "." << endl;

				//if a dot is there the label the id stand before the dot else the whole id is the label too
				//in every case make a copy without spaces for internal identification in kaom
				endPos = currentEntity.find("\"");
				if (endPos != string::npos) {
					remove_copy(currentEntity.begin(), currentEntity.begin() + endPos, back_inserter(blankLessEntity), ' ');
					foundArrow = currentEntity.find("\"", endPos + 1);

					if (foundArrow != string::npos) {
						currentEntity = currentEntity.substr(endPos + 1, foundArrow - 1 - endPos);
					} else {
						cerr << "found single a quote within " << keyword << " in the annotation of " << entityType_ << " -> skipping label"
								<< endl;
						currentEntity = currentEntity.substr(0, endPos);
					}
				} else {
					remove_copy(currentEntity.begin(), currentEntity.end(), back_inserter(blankLessEntity), ' ');
				}

				//todo
				cout << "output 2 currentEntity: " << currentEntity << "." << endl;
				cout << "output 2 blankLessEntity: " << blankLessEntity << "." << endl;

				replaceSpecial(blankLessEntity);

				cout << "output 3 blankLessEntity: " << blankLessEntity << "." << endl << endl;

				//build kaom content for current output
				replace = "port <blankLess@lias>_" + blankLessEntity + " \"" + currentEntity + "\";";

				//add current output to the current entityMap entry
				mapEntry_.append(replace);

				//update variables for next round
				blankLessEntity.clear();

			} else {
				cerr << "Found two consecutive commas within " << keyword << " in the annotation of " << entityType_ << " -> skipping" << endl;
			}

			startPos = entity.find(",", startPos) + 1;

			//is this not the last occurrence
		} while (condition);
		break;
	case 'l':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			if (entity.find(",", startPos) < foundSemi) {
				endPos = entity.find(",", startPos);
				cout << "link 0 entity.find(, , startPos): " << endPos << "." << endl;
				endPos -= startPos;
				cout << "link 0 endPos -= startPos;: " << endPos << "." << endl;
				condition = true;
			} else {
				endPos = foundSemi;
				cout << "link 0 endPos = foundSemi: " << endPos << "." << endl;
				endPos -= startPos;
				cout << "link 0 endPos -= startPos;: " << endPos << "." << endl;
				condition = false;
			}

			if (endPos != 0) {

				//extract the current content
				currentEntity = entity.substr(startPos, endPos);

				//make a copy without spaces for internal identification in kaom
				remove_copy(currentEntity.begin(), currentEntity.end(), back_inserter(blankLessEntity), ' ');

				//look for links

				//todo
				cout << "link 1 currentEntity: " << currentEntity << "." << endl;
				cout << "link 1 blankLessEntity: " << blankLessEntity << "." << endl;
				cout << "link 1 endPos: " << endPos << "." << endl;
				cout << "link 1 startPos: " << startPos << "." << endl;

				foundArrow = blankLessEntity.find("->");

				if (foundArrow != string::npos) {

					//extract identification for the source of the link
					linkEntity = blankLessEntity.substr(foundArrow + 2);
					blankLessEntity = blankLessEntity.substr(0, foundArrow);

					//todo
					cout << "link 2 linkEntity: " << linkEntity << "." << endl;
					cout << "link 2 blankLessEntity: " << blankLessEntity << "." << endl;

					foundArrow = linkEntity.rfind(":");
					foundDot = blankLessEntity.rfind(":");
					replaceSpecial(blankLessEntity);
					replaceSpecial(linkEntity);

					//todo
					cout << "link 3 linkEntity: " << linkEntity << "." << endl;
					cout << "link 3 blankLessEntity: " << blankLessEntity << "." << endl;

					if (foundArrow == string::npos) {
						linkEntity = "<blankLess@lias>_" + linkEntity;

					} else {
						//replace the colon with a underline
						linkEntity.replace(foundArrow, 1, "_");
					}

					if (foundDot == string::npos) {
						blankLessEntity = "<blankLess@lias>_" + blankLessEntity;
					} else {
						//replace the colon with a underline
						blankLessEntity.replace(foundDot, 1, "_");
					}

					//todo
					cout << "link 4 linkEntity: " << linkEntity << "." << endl;
					cout << "link 4 blankLessEntity: " << blankLessEntity << "." << endl << endl;

					replace = "link " + blankLessEntity + " to " + linkEntity + ";";

					//add current link to the current entityMap entry
					mapEntry_.append(replace);

					//update variables for next round
				} else {
					cerr << "found no arrow within " << keyword << " in the annotation of " << entityType_ << " -> skipping" << endl;
				}
				blankLessEntity.clear();
			} else {
				cerr << "Found two consecutive commas within " << keyword << " in the annotation of " << entityType_ << " -> skipping" << endl;
			}

			startPos = entity.find(",", startPos) + 1;

		} while (condition);
		break;
	case 'c':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			if (entity.find(",", startPos) < foundSemi) {
				endPos = entity.find(",", startPos);
				endPos -= startPos;
				condition = true;
			} else {
				endPos = foundSemi;
				endPos -= startPos;
				condition = false;
			}

			if (endPos != 0) {

				//extract the current content
				currentEntity = entity.substr(startPos, endPos);

				//todo
				cout << "content 1 currentEntity: " << currentEntity << "." << endl;
				cout << "content 1 entity: " << entity << "." << endl << endl;
				cout << "content 1 endPos: " << endPos << "." << endl;
				cout << "content 1 startPos: " << startPos << "." << endl;

				//build kaom content for current output
				replace = "repl@ce " + currentEntity + ";";

				//add current component to the current entityMap entry
				mapEntry_.append(replace);

			} else {
				cerr << "Found two consecutive commas within " << keyword << " in the annotation of " << entityType_ << " -> skipping" << endl;
			}

			//update variables for next round
			startPos = entity.find(",", startPos) + 1;

			//is this not the last occurrence
		} while (condition);
		break;
	case 't':
		//find the marked ending of the content
		endPos = foundSemi;
		endPos -= startPos;

		//compute the length of the content
		currentEntity = entity.substr(startPos, endPos);

		//todo
		cout << "toplevel 1 currentEntity: " << currentEntity << "." << endl;
		cout << "toplevel 1 entity: " << entity << "." << endl << endl;

		result_.append("repl@ce " + entityType_ + ":" + currentEntity + ";");

		break;
	default:
		//nothing
		break;
	}
}

void BuilderKaom::composeArgument() {
	//close canvas

	//todo reallocate
	unsigned int startPos, endPos, foundColon, dotsPos;
	//condition indicates that if there are more entries in the content
	bool condition;
	string currentEntity, currentType, blankLessEntity;

	blankLessEntity.clear();

	//find the position of first occurrence in the entry
	startPos = result_.find("repl@ce ");
	result_.append("}");
	if (startPos != string::npos) {

	//todo
	cout << "comp 0 result_: " << result_ << endl;

	do {
		endPos = result_.find(";", startPos);
		endPos = endPos - startPos;

		currentEntity = result_.substr(startPos, endPos);

		//todo
		cout << "comp 1 currentEntity: " << currentEntity << endl;

		//search the position of the colon
		//todo no : warnung und raustreichen
		foundColon = currentEntity.find(":");

		currentType = currentEntity.substr(8, foundColon - 8);
		currentEntity = currentEntity.substr(foundColon + 1);

		//todo
		cout << "comp 2 currentType: " << currentType << endl;
		cout << "comp 2 currentEntity: " << currentEntity << endl;

		//todo schlüssel ist nicht da dann warnung und aufruf raustreichen
		result_.replace(startPos, endPos + 1, entityMap_[currentType]);

		//todo
		cout << "comp 3 result_: " << result_ << endl;

		currentType = currentEntity;
		dotsPos = currentEntity.find("\"");

		if (dotsPos != string::npos) {
			currentType.erase(currentType.begin() + dotsPos, currentType.end());
		}

		//todo
		cout << "comp 4 currentType: " << currentType << endl;

		replaceSpecial(currentType);
		//make a copy without spaces for internal identification in kaom
		remove_copy(currentType.begin(), currentType.end(), back_inserter(blankLessEntity), ' ');

		//todo
		cout << "comp 5 currentType: " << currentType << endl;
		cout << "comp 5 blankLessEntity: " << blankLessEntity << endl;

		if (result_.find("<@lias>", startPos) < result_.find("repl@ce ", startPos)) {
			endPos = result_.find("<@lias>", startPos);
			if (dotsPos != string::npos) {
				//todo another dot is there
				currentEntity = currentEntity.substr(dotsPos + 1, currentEntity.find("\"", dotsPos + 1) - 1 - dotsPos);
			}

			result_.replace(endPos, 7, currentEntity);
		}

		//todo
		cout << "comp 6 currentEntity: " << currentEntity << endl;
		cout << "comp 6 result_: " << result_ << endl;

		do {
			if (result_.find("<blankLess@lias>", startPos) < result_.find("repl@ce ", startPos)) {
				endPos = result_.find("<blankLess@lias>", startPos);
				result_.replace(endPos, 16, blankLessEntity);
				condition = true;

				//todo
				cout << "comp 7 result_: " << result_ << endl;

			} else {
				while (result_.find("}", startPos) < result_.find("repl@ce ", startPos)) {
					startPos = result_.find("}", startPos) + 1;
					foundColon = (blankLessEntity.rfind("@@") == string::npos ? 0 : blankLessEntity.rfind("@@"));
					blankLessEntity.erase(blankLessEntity.begin() + foundColon, blankLessEntity.end());

					//todo
					cout << "comp 8 blankLessEntity: " << blankLessEntity << endl;

				}
				startPos = result_.find("repl@ce ", startPos);
				if (blankLessEntity.length() > 0)
					blankLessEntity += "@@";
				condition = false;

				//todo
				cout << "comp 9 blankLessEntity: " << blankLessEntity << endl << endl;
			}

		} while (condition);

		//blankLessEntity.clear();
	} while (startPos != string::npos);

	foundColon = result_.rfind("@@");
	if (foundColon != string::npos) {
		do {
			result_.replace(foundColon, 2, "_");
			foundColon = result_.rfind("@@");

			//todo
			cout << "comp 10 result_: " << result_ << endl;

		} while (foundColon != string::npos);
	}

	}
	else {
		cerr << "found no toplevel for " << outputFileName_ << endl;
	}

}

void BuilderKaom::buildArgsQueue(string keyword) {
	//find the position of first occurrence in the entry
	unsigned int startPos;

	startPos = entity_.find(keyword);

	if (startPos == string::npos) {
		return;
	} else {
		argsQueue_.push(make_pair(startPos, keyword));
	}
}

void BuilderKaom::buildResult() {

	if (input_.empty()) {
		cerr << "Empty input queue in builder. This should not happen." << endl;
		return;
	}

	//start canvas and set spacing to 100
	result_.append("@spacing 100.0 entity model{");

	//delete blankchars in the entries of the queue
	deleteBlank();

	//until the queue is not empty extract the arguments
	while (!input_.empty()) {

		entity_ = input_.front();

		buildArgsQueue("kind:");
		buildArgsQueue("input:");
		buildArgsQueue("output:");
		buildArgsQueue("link:");
		buildArgsQueue("content:");
		buildArgsQueue("toplevel:");

		//todo !(argsQueue_.empty())
		while (!argsQueue_.empty()) {
			extractArgument();
		}

		//close current entity
		mapEntry_.append("}");

		//create new entry in entityMap_ with key entityType_ and value mapEntry_
		//abfragen ob schlüssel schon vorhanden wenn ja warnung und überschreiben
		entityMap_[entityType_] = mapEntry_;

		//delete edited entry and update the rest of the variables for the next entry
		input_.pop();
		entityType_.clear();
	}

	//todo debug

	map<string, string>::iterator it;

	// show content:
	for (it = entityMap_.begin(); it != entityMap_.end(); it++)
		cout << (*it).first << " => " << (*it).second << endl;

	cout << result_ << endl;

	composeArgument();

	//Write the result to the output file
	saveKaom(outputFileName_);

}

bool BuilderKaom::valid() {
	//is the result empty
	return !result_.empty();
}

int BuilderKaom::saveKaom(const string &filename) {

	//is result empty
	if (!valid()) {
		cout << "result_ is empty" << endl;
		return -1;
	}
	// open file
	ofstream file;
	file.open(filename.c_str(), ios::out | ios::binary);
	// check if file is ready for writing
	if (!file.good()) {
		cout << "file not writable" << endl;
		return -1;
	}
	// write data
	file << result_ << endl;

	file.close();
	return 0;
}
