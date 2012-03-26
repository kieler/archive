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
		input_(input), outputFileName_(filename), isValid_(true) {
}

void BuilderKaom::replaceSpecChar(string& str) {
	int max = str.length();

	//test every char in the string
	for (int i = 0; i < max; i++) {
		//erase blank chars
		if (str[i] == 32) {
			str.erase(i, 1);
			i--;
			max--;
		} else {
			//replace special chars(!(48-58,65-90,95,97-122) (ASCII)) with underlines
			if ((str[i] >= 48 && str[i] <= 58) || (str[i] >= 65 && str[i] <= 90) || (str[i] == 95) || (str[i] >= 97 && str[i] <= 122)) {
				//nothing
			} else {
				str[i] = '_';
			}
		}
	}
}

int BuilderKaom::deleteBlank() {
	//local variables used for extract and modify the content of the queue
	string entity;
	unsigned int lenght, i, found, sz = input_.size();

	//for all entries in the input queue
	for (unsigned int t = 0; t < sz; t++) {
		entity = input_.front();
		i = 0;
		lenght = entity.length();

		//find all occurrences of ": , ; - > . " in the string
		//and erase all blank chars before and after each occurrence
		found = entity.find_first_of(":,;->.");
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

		//write the content back to the input queue
		input_.push(entity);
		input_.pop();
	}
	return 0;
}

void BuilderKaom::buildPattern() {
	//local variables used to store and modify sub-contents
	string entity, actEntity, entityID, pattern, key;
	unsigned int begPos, size, posMark, sclnPos;
	bool listEmpty;

	//extract the content from one keyword until the next keyword and store it as the working entity
	begPos = argsQ_.top().first;
	key = argsQ_.top().second;
	argsQ_.pop();
	if (!argsQ_.empty()) {
		entity = entity_.substr(begPos, argsQ_.top().first - begPos);
	} else {
		entity = entity_.substr(begPos);
	}

	//set the beginning to the end of the keyword
	//and search for a marked ending
	//or set the ending to the end of the working entity
	begPos = key.length();
	sclnPos = entity.find(";", begPos);
	if (sclnPos == string::npos) {
		cerr << "Missing semicolon after " << key << " in the annotation of " << entityType_ << endl;
		sclnPos = entity.length() - 1;
	}

	//test if there is a content before the ending
	if (begPos > sclnPos) {
		cerr << key << "the annotation of " << entityType_ << "is partially empty without a semicolon. -> skipping" << endl;
		key = "error";
	}

	//switch to the current keyword
	switch (key[0]) {
	//in case of kind
	case 'k':
		//compute the length of the entry and extract it
		size = sclnPos - begPos;
		entityType_ = entity.substr(begPos, size);

		//start a new entityMap_ entry
		mapEntry_ = " @portConstraints Free entity <ID@lias> \"<@lias>\" {";
		break;

		//in case of input or output
	case 'i':
	case 'o':
		//for all entries in the current entity
		do {
			//compute the length of the current content
			//if this is not the last entry then use the next comma
			//else use the semicolon as marked ending
			posMark = entity.find(",", begPos);
			if (posMark < sclnPos) {
				size = posMark - begPos;
				listEmpty = true;
			} else {
				size = sclnPos - begPos;
				listEmpty = false;
			}

			//check if the entry has content
			if (size != 0) {
				//extract the entry and update the position of the next comma
				actEntity = entity.substr(begPos, size);
				begPos = posMark + 1;

				//check if there is a label
				size = actEntity.find("\"");
				if (size != string::npos) {
					//extract the ID without blanks and find the second quote
					remove_copy(actEntity.begin(), actEntity.begin() + size, back_inserter(entityID), ' ');
					posMark = actEntity.find("\"", size + 1);

					//extract the label or skip it if it is corrupted
					if (posMark != string::npos) {
						actEntity = actEntity.substr(size + 1, posMark - 1 - size);
					} else {
						cerr << "found single a quote within " << key << " in the annotation of " << entityType_ << " -> skipping label" << endl;
						actEntity = actEntity.substr(0, size);
					}
				} else {
					//erase all blanks from the ID
					remove_copy(actEntity.begin(), actEntity.end(), back_inserter(entityID), ' ');
				}
				//replace all special chars in the ID
				replaceSpecChar(entityID);

				//build KAOM content for the port and add it to the entityMap_ entry
				pattern = "port <ID@lias>_" + entityID + " \"" + actEntity + "\";";
				mapEntry_.append(pattern);

				//update variables for next round
				entityID.clear();
			} else {
				//if there is no content skip the entry
				cerr << "Found two consecutive commas within " << key << " in the annotation of " << entityType_ << " -> skipping" << endl;
				begPos = posMark + 1;
			}
		} while (listEmpty);
		break;

		//in case of link
	case 'l':
		//for all entries in the current entity
		do {
			//compute the length of the current content
			//if this is not the last entry then use the next comma
			//else use the semicolon as marked ending
			posMark = entity.find(",", begPos);
			if (posMark < sclnPos) {
				size = posMark - begPos;
				listEmpty = true;
			} else {
				size = sclnPos - begPos;
				listEmpty = false;
			}

			//check if the entry has content
			if (size != 0) {
				//extract the entry without blanks and update the position of the next comma
				actEntity = entity.substr(begPos, size);
				begPos = posMark + 1;
				remove_copy(actEntity.begin(), actEntity.end(), back_inserter(entityID), ' ');

				//separate source and target of the link
				posMark = entityID.find("->");
				if (posMark != string::npos) {
					actEntity = entityID.substr(posMark + 2);
					entityID = entityID.substr(0, posMark);

					//build KAOM content for the target
					//separate function name and replace special chars
					//and check if the target is specified directly
					posMark = actEntity.rfind(":");
					replaceSpecChar(actEntity);
					if (posMark == string::npos) {
						actEntity = "<ID@lias>_" + actEntity;
					} else {
						actEntity.replace(posMark, 1, "_");
					}

					//build KAOM content for the source
					//separate function name and replace special chars
					//and check if the source is specified directly
					posMark = entityID.rfind(":");
					replaceSpecChar(entityID);
					if (posMark == string::npos) {
						entityID = "<ID@lias>_" + entityID;
					} else {
						//replace the colon with a underline
						entityID.replace(posMark, 1, "_");
					}

					//build KAOM content for the link  and add it to the entityMap_ entry
					pattern = "link " + entityID + " to " + actEntity + ";";
					mapEntry_.append(pattern);
				}

				//skip corrupted entries
				else {
					cerr << "found no arrow within " << key << " in the annotation of " << entityType_ << " -> skipping" << endl;
				}

				//update variables for next round
				entityID.clear();
			} else {
				cerr << "Found two consecutive commas within " << key << " in the annotation of " << entityType_ << " -> skipping" << endl;
				begPos = posMark + 1;
			}
		} while (listEmpty);
		break;

		//in case of content
	case 'c':
		//for all entries in the current entity
		do {
			//compute the length of the current content
			//if this is not the last entry then use the next comma
			//else use the semicolon as marked ending
			posMark = entity.find(",", begPos);
			if (posMark < sclnPos) {
				size = posMark - begPos;
				listEmpty = true;
			} else {
				size = sclnPos - begPos;
				listEmpty = false;
			}

			//check if the entry has content
			if (size != 0) {
				//extract the entry
				actEntity = entity.substr(begPos, size);

				//build KAOM content for the inner entity and add it to the entityMap_ entry
				pattern = "repl@ce " + actEntity + ";";
				mapEntry_.append(pattern);
			} else {
				//if there is no content skip the entry
				cerr << "Found two consecutive commas within " << key << " in the annotation of " << entityType_ << " -> skipping" << endl;
			}

			//update the position of the next comma
			begPos = posMark + 1;
		} while (listEmpty);
		break;

		//in case of toplevel
	case 't':
		//compute the length of the entry and extract it
		size = sclnPos - begPos;
		actEntity = entity.substr(begPos, size);

		//build KAOM content for the entity and add it to the result
		result_.append("repl@ce " + entityType_ + ":" + actEntity + ";");
		break;

	default:
		//nothing
		break;
	}
}

void BuilderKaom::buildKaom() {
	//local variables used to select and modify contents
	unsigned int begPos, size, posMark, rPos;
	bool remaindID;
	string actEntity, actType, entityID;
	entityID.clear();

	//complete the result and find the position of first replace occurrence in the result
	begPos = result_.find("repl@ce ");
	result_.append("}");

	//check if there is a start and then proceed with the filling of the model
	if (begPos != string::npos) {
		do {
			//calculate the call to the current entity and select it
			size = result_.find(";", begPos) - begPos;
			actEntity = result_.substr(begPos, size);

			//check if the call is not corrupted
			//and extract the arguments of the call
			posMark = actEntity.find(":");
			if (posMark != string::npos) {
				actType = actEntity.substr(8, posMark - 8);
				actEntity = actEntity.substr(posMark + 1);

				//check if the function name exists
				//and insert the appropriate pattern
				if (entityMap_.count(actType) > 0) {
					result_.replace(begPos, size + 1, entityMap_[actType]);

					//check if a label exists
					actType = actEntity;
					posMark = actEntity.find("\"");
					if (posMark != string::npos) {
						//select the ID and look for the next quote
						//if there is one select the label else select only the ID
						actType.erase(actType.begin() + posMark, actType.end());
						if (actEntity.find("\"", posMark + 1) != string::npos) {
							actEntity = actEntity.substr(posMark + 1, actEntity.find("\"", posMark + 1) - 1 - posMark);
						} else {
							cerr << "found single a quote within " << actEntity << " -> skipping label" << endl;
							actEntity = actType;
						}
					}

					//replace the special chars in the ID
					replaceSpecChar(actType);
					remove_copy(actType.begin(), actType.end(), back_inserter(entityID), ' ');

					//insert the label
					if (result_.find("<@lias>", begPos) < result_.find("repl@ce ", begPos)) {
						posMark = result_.find("<@lias>", begPos);
						result_.replace(posMark, 7, actEntity);
					}

					//insert all IDs
					do {
						//find the position of the next ID and the next replace marker
						posMark = result_.find("<ID@lias>", begPos);
						rPos = result_.find("repl@ce ", begPos);

						//check if the ID is in the current scope and insert the ID
						if (posMark < rPos) {
							result_.replace(posMark, 9, entityID);
							remaindID = true;
						}

						//count the closing braces and update the predecessor ID
						else {
							begPos = result_.find("}", begPos);
							while (begPos < rPos) {

								//for every closing brace erase one predecessor
								posMark = entityID.rfind("@@");
								posMark = (posMark == string::npos ? 0 : posMark);
								entityID.erase(entityID.begin() + posMark, entityID.end());
								begPos = result_.find("}", begPos + 1);
							}

							//if this is not a toplevel entity then add a marker for a predecessor
							if (entityID.length() > 0)
								entityID += "@@";
							remaindID = false;
						}
					} while (remaindID);

					//update beginning position to the next replace marker
					begPos = rPos;
				}

				//skip corrupted entries
				else {
					cerr << "found no object with ID " << actType << " -> skipping" << endl;
					result_.replace(begPos, size + 1, "");
					begPos = result_.find("repl@ce ", begPos);
				}
			} else {
				cerr << "found no colon in " << actEntity << " -> skipping" << endl;
				result_.replace(begPos, size + 1, "");
				begPos = result_.find("repl@ce ", begPos);
			}
		} while (begPos != string::npos);

		//replace all predecessor markers with underlines
		posMark = result_.rfind("@@");
		while (posMark != string::npos) {
			result_.replace(posMark, 2, "_");
			posMark = result_.rfind("@@");
		}

	} else {
		cerr << "found no toplevel for " << outputFileName_ << endl;
		isValid_ = false;
	}
}

void BuilderKaom::buildArgsQueue(string keyword) {
	//local starting position
	unsigned int startPos;

	//find the position of first occurrence for the keyword in the entry
	//and store the position with the keyword
	startPos = entity_.find(keyword);
	if (startPos != string::npos) {
		argsQ_.push(make_pair(startPos, keyword));
	}
}

void BuilderKaom::buildResult() {
	//start canvas and set spacing to 100
	result_.append("@spacing 100.0 entity model{");

	//delete blanks in all entries of the input queue
	deleteBlank();

	//build patterns for all entries from the input queue
	while (!input_.empty()) {
		//select the first entry
		//and sort the internal keywords, ascending to their position
		entity_ = input_.front();
		buildArgsQueue("kind:");
		buildArgsQueue("input:");
		buildArgsQueue("output:");
		buildArgsQueue("link:");
		buildArgsQueue("content:");
		buildArgsQueue("toplevel:");

		//build a pattern for the current entity and add it to the internal map
		if (!argsQ_.empty()) {
			while (!argsQ_.empty()) {
				buildPattern();
			}
			mapEntry_.append("}");
			if (entityMap_.count(entityType_) > 0) {
				cerr << "Found two annotations for " << entityType_ << " -> skip first annotation" << endl;
			}
			entityMap_[entityType_] = mapEntry_;
		}
		input_.pop();
		entityType_.clear();
	}

	//build KAOM-model from the patterns and store it in a file
	buildKaom();
	saveKaom(outputFileName_);
}

int BuilderKaom::saveKaom(const string &filename) {
	//if model is empty do not write it
	if (!isValid_) {
		cerr << "KAOM-model is empty" << endl;
		return 1;
	}

	//open output file
	ofstream file;
	file.open(filename.c_str(), ios::out | ios::binary);

	// check if file is ready for writing
	if (!file.good()) {
		cerr << "output file not writable" << endl;
		return 1;
	}

	// write content
	file << result_ << endl;
	file.close();
	return 0;
}
