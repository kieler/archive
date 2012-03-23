/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2012 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */

#include <BuilderXml.hh>

using namespace std;

BuilderXml::BuilderXml(string inputPath, string outputPath) :
		inputPath_(inputPath), outputPath_(outputPath) {
	if (loadDoxyfile() || fillDoxyfile() || callDoxygen() || buildFileQueue()) {
		cerr << "An error occurred during construction of the XML file!!!" << endl;
	}
}

BuilderXml::~BuilderXml() {
	// nothing
}

int BuilderXml::loadDoxyfile() {
	ifstream inputFile("Doxyfile");

	if (!inputFile) {
		cerr << "Need a Doxyfile (configuration for Doxygen) in the same folder as C2KAOM!" << endl;
		return 1;
	}

	string buffer;

	while (inputFile.good()) {
		getline(inputFile, buffer);
		content_ += buffer;
		content_ += "\n";
	}
	inputFile.close();

	return 0;
}

int BuilderXml::fillDoxyfile() {
	ofstream outputFile("tempDoxyfile");

	content_ += "INPUT                  = " + inputPath_;
	content_ += "\n";
	content_ += "OUTPUT_DIRECTORY       = " + outputPath_;

	if (!outputFile) {
		cerr << "Cannot write a  modified tempDoxyfile in the same folder as C2KAOM!" << endl;
		return 1;
	}

	if (outputFile.good())
		outputFile << content_;

	outputFile.close();

	return 0;
}

int BuilderXml::callDoxygen() {
	//Checking if processor is available
	long endPos;

	if (!system(NULL)) {
		cerr << "system command is not available" << endl;
		return 1;
	}
	//"Executing command doxygen
	system("doxygen tempDoxyfile");

	FILE * doxyLog = fopen("logfile", "r");
	if (doxyLog != NULL) {
		fseek(doxyLog, 0L, SEEK_END);
		endPos = ftell(doxyLog);
		fclose(doxyLog);
	} else {
		cerr << "Cannot write or read the the doxygen logfile in the same folder as C2KAOM!" << endl;
		return 1;
	}

	if (endPos == 0) {
		return 0;
	} else {
		cerr << "Error occured in doxygen! Check logfile." << endl;
		return 1;
	}
}

int BuilderXml::buildFileQueue() {
	DIR *dp;
	struct dirent *ep;
	string fileName;

	outputPath_ += "/xml/";

	dp = opendir(outputPath_.c_str());
	if (dp != NULL) {
		while (ep = readdir(dp)) {
			fileName = ep->d_name;
			if ((fileName.size() > 3) && ((fileName.substr(0, 4) != "dir_")) && ((fileName.substr(fileName.length() - 4) == ".xml"))
					&& ((fileName != "index.xml")))
				inputFiles_.push(fileName);
		}
		(void) closedir(dp);
	} else {
		cerr << "Couldn't open the directory" << endl;
		return 1;
	}
	//todo input !empty
	return 0;
}
