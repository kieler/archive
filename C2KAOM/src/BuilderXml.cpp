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
	//todo path !empty
	loadDoxyfile();
	completeDoxyfile();
	callDoxygen();
	buildFileQueue();
}

BuilderXml::~BuilderXml() {
	// nothing
}

int BuilderXml::loadDoxyfile() {
	ifstream inputFile("Doxyfile");
	string buffer;

	//todo Doxyfile is there
	while (inputFile.good()) {
		getline(inputFile, buffer);
		content_ += buffer;
		content_ += "\n";
	}
	inputFile.close();

	return 0;
}

int BuilderXml::completeDoxyfile() {
	ofstream outputFile("tempDoxyfile");

	content_ += "INPUT                  = " + inputPath_;
	content_ += "\n";
	content_ += "OUTPUT_DIRECTORY       = " + outputPath_;

	//todo file bad ?
	if (outputFile.good())
		outputFile << content_;

	outputFile.close();

	return 0;
}

int BuilderXml::callDoxygen() {
	//Checking if processor is available
	if (!system(NULL))
		return 1;
	//"Executing command doxygen
	//todo doxygen error
	system("doxygen tempDoxyfile");
	return 0;
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
