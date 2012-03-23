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
}

BuilderKaom::BuilderKaom() {
	cout << "Need input queue: " << endl;
}

void BuilderKaom::replaceSpecChar(string& str) {
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

void BuilderKaom::buildEntities() {
	//local variables used for store and modify sub-contents
	string entity, actEntity, entityID, pattern, key;

	unsigned int begPos, size, posMark, sclnPos;
	//condition indicates that if there are more entries in the content
	bool condition;

	//find the position of first occurrence in the entry
	begPos = argsQ_.top().first;
	key = argsQ_.top().second;
	argsQ_.pop();
	if (!argsQ_.empty()) {
		entity = entity_.substr(begPos, argsQ_.top().first - begPos);
	} else {
		entity = entity_.substr(begPos);
	}

	begPos = key.length();

	sclnPos = entity.find(";", begPos);

	if (sclnPos == string::npos) {
		cerr << "Missing semicolon after " << key << " in the annotation of " << entityType_ << endl;
		sclnPos = entity.length() - 1;
	}

	if (begPos > sclnPos) {
		cerr << key << " in the annotation of " << entityType_ << "is empty and has no semicolon. -> skipping" << endl;
		key = "error";
	}

	//switch for type, input and output
	switch (key[0]) {
	//in case of type
	case 'k':
		size = sclnPos - begPos;

		entityType_ = entity.substr(begPos, size);

		//start a new entityMap_ entry
		mapEntry_ = " @portConstraints Free entity <ID@lias> \"<@lias>\" {";

		break;
		//in case of input or output
	case 'i':
	case 'o':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			posMark = entity.find(",", begPos);
			if (posMark < sclnPos) {
				size = posMark - begPos;
				condition = true;
			} else {
				size = sclnPos - begPos;
				condition = false;
			}

			if (size != 0) {

				//extract the current content
				actEntity = entity.substr(begPos, size);
				begPos = posMark + 1;

				//if a dot is there the label the id stand before the dot else the whole id is the label too
				//in every case make a copy without spaces for internal identification in kaom
				size = actEntity.find("\"");
				if (size != string::npos) {
					remove_copy(actEntity.begin(), actEntity.begin() + size, back_inserter(entityID), ' ');

					posMark = actEntity.find("\"", size + 1);

					if (posMark != string::npos) {
						actEntity = actEntity.substr(size + 1, posMark - 1 - size);
					} else {
						cerr << "found single a quote within " << key << " in the annotation of " << entityType_ << " -> skipping label" << endl;
						actEntity = actEntity.substr(0, size);
					}

				} else {
					remove_copy(actEntity.begin(), actEntity.end(), back_inserter(entityID), ' ');
				}

				replaceSpecChar(entityID);
				//build kaom content for current input
				pattern = "port <ID@lias>_" + entityID + " \"" + actEntity + "\";";

				//add current input to the current entityMap entry
				mapEntry_.append(pattern);

				//update variables for next round
				entityID.clear();

			} else {
				cerr << "Found two consecutive commas within " << key << " in the annotation of " << entityType_ << " -> skipping" << endl;
				begPos = posMark + 1;
			}
			//is this not the last occurrence
		} while (condition);

		break;
	case 'l':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			posMark = entity.find(",", begPos);
			if (posMark < sclnPos) {
				size = posMark - begPos;
				condition = true;
			} else {
				size = sclnPos - begPos;
				condition = false;
			}

			if (size != 0) {

				//extract the current content
				actEntity = entity.substr(begPos, size);
				begPos = posMark + 1;

				//make a copy without spaces for internal identification in kaom
				remove_copy(actEntity.begin(), actEntity.end(), back_inserter(entityID), ' ');

				//look for links
				posMark = entityID.find("->");

				if (posMark != string::npos) {

					//extract identification for the source of the link
					actEntity = entityID.substr(posMark + 2);
					entityID = entityID.substr(0, posMark);

					posMark = actEntity.rfind(":");
					replaceSpecChar(actEntity);

					if (posMark == string::npos) {
						actEntity = "<ID@lias>_" + actEntity;
					} else {
						//replace the colon with a underline
						actEntity.replace(posMark, 1, "_");
					}

					posMark = entityID.rfind(":");
					replaceSpecChar(entityID);

					if (posMark == string::npos) {
						entityID = "<ID@lias>_" + entityID;
					} else {
						//replace the colon with a underline
						entityID.replace(posMark, 1, "_");
					}
					pattern = "link " + entityID + " to " + actEntity + ";";

					//add current link to the current entityMap entry
					mapEntry_.append(pattern);

					//update variables for next round
				} else {
					cerr << "found no arrow within " << key << " in the annotation of " << entityType_ << " -> skipping" << endl;
				}
				entityID.clear();
			} else {
				cerr << "Found two consecutive commas within " << key << " in the annotation of " << entityType_ << " -> skipping" << endl;
				begPos = posMark + 1;
			}

		} while (condition);
		break;
	case 'c':
		do {
			//if the current content is not the last content in the entry search for a comma else search for a semicolon
			posMark = entity.find(",", begPos);
			if (posMark < sclnPos) {
				size = posMark - begPos;
				condition = true;
			} else {
				size = sclnPos - begPos;
				condition = false;
			}

			if (size != 0) {

				//extract the current content
				actEntity = entity.substr(begPos, size);

				//build kaom content for current output
				pattern = "repl@ce " + actEntity + ";";

				//add current component to the current entityMap entry
				mapEntry_.append(pattern);

			} else {
				cerr << "Found two consecutive commas within " << key << " in the annotation of " << entityType_ << " -> skipping" << endl;
			}

			//update variables for next round
			begPos = posMark + 1;

			//is this not the last occurrence
		} while (condition);
		break;
	case 't':
		//find the marked ending of the content
		size = sclnPos - begPos;

		//compute the length of the content
		actEntity = entity.substr(begPos, size);
		result_.append("repl@ce " + entityType_ + ":" + actEntity + ";");

		break;
	default:
		//nothing
		break;
	}
}

void BuilderKaom::buildKaom() {
	//close canvas

	unsigned int begPos, size, posMark, rPos;
	//condition indicates that if there are more entries in the content
	bool condition;
	string actEntity, actType, entityID;

	entityID.clear();

	//find the position of first occurrence in the entry
	begPos = result_.find("repl@ce ");
	result_.append("}");
	if (begPos != string::npos) {

		do {
			size = result_.find(";", begPos) - begPos;

			actEntity = result_.substr(begPos, size);
			//search the position of the colon
			posMark = actEntity.find(":");

			if (posMark != string::npos) {
				actType = actEntity.substr(8, posMark - 8);
				actEntity = actEntity.substr(posMark + 1);

				if (entityMap_.count(actType) > 0) {
					result_.replace(begPos, size + 1, entityMap_[actType]);
					actType = actEntity;
					posMark = actEntity.find("\"");

					if (posMark != string::npos) {
						actType.erase(actType.begin() + posMark, actType.end());
						if (actEntity.find("\"", posMark + 1) != string::npos) {
							actEntity = actEntity.substr(posMark + 1, actEntity.find("\"", posMark + 1) - 1 - posMark);
						} else {
							cerr << "found single a quote within " << actEntity << " -> skipping label" << endl;
							actEntity = actType;
						}
					}
					replaceSpecChar(actType);
					//make a copy without spaces for internal identification in kaom
					remove_copy(actType.begin(), actType.end(), back_inserter(entityID), ' ');

					if (result_.find("<@lias>", begPos) < result_.find("repl@ce ", begPos)) {
						posMark = result_.find("<@lias>", begPos);
						result_.replace(posMark, 7, actEntity);
					}
					do {
						posMark = result_.find("<ID@lias>", begPos);
						rPos = result_.find("repl@ce ", begPos);
						if (posMark < rPos) {
							result_.replace(posMark, 9, entityID);
							condition = true;
						} else {
							begPos = result_.find("}", begPos);
							while (begPos < rPos) {
								posMark = entityID.rfind("@@");
								posMark = (posMark == string::npos ? 0 : posMark);
								entityID.erase(entityID.begin() + posMark, entityID.end());
								begPos = result_.find("}", begPos + 1);
							}
							if (entityID.length() > 0)
								entityID += "@@";
							condition = false;
						}
					} while (condition);
					begPos = rPos;
				} else {
					cerr << "found no object with ID " << actType << " -> skipping" << endl;
					result_.replace(begPos, size + 1, "");
					begPos = result_.find("repl@ce ", begPos);
				}
			} else {
				cerr << "found no colon in " << actEntity << " -> skipping" << endl;
				result_.replace(begPos, size + 1, "");
				begPos = result_.find("repl@ce ", begPos);
			}
			//blankLessEntity.clear();
		} while (begPos != string::npos);
		posMark = result_.rfind("@@");
		while (posMark != string::npos) {
			result_.replace(posMark, 2, "_");
			posMark = result_.rfind("@@");
		}
	} else {
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
		argsQ_.push(make_pair(startPos, keyword));
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

		if (!argsQ_.empty()) {

			while (!argsQ_.empty()) {
				buildEntities();
			}

			//close current entity
			mapEntry_.append("}");

			//create new entry in entityMap_ with key entityType_ and value mapEntry_
			//abfragen ob schlüssel schon vorhanden wenn ja warnung und überschreiben
			entityMap_[entityType_] = mapEntry_;
		}

		//delete edited entry and update the rest of the variables for the next entry
		input_.pop();
		entityType_.clear();
	}

	buildKaom();

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
