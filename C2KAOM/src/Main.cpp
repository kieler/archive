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

#include <queue>
#include <dirent.h>
#include <FilterXml.hh>
#include <BuilderKaom.hh>
#include <iostream>

using namespace std;

int main(int argc, char *argv[]) {

	if (argc != 3) {
		cout << "usage: " << argv[0] << " [sourcePath] [outputPath]" << endl;
		return 0;
	}

	string tempPath;

	tempPath = string(argv[2]);

	string buffer, content;
	ifstream file("Doxyfile");

	while (file.good()) {
		getline(file, buffer);
		content += buffer;
		content += "\n";
	}
	file.close();

	content += "INPUT                  = ";
	content += argv[1];
	content += "\n";
	content += "OUTPUT_DIRECTORY       = ";
	content += tempPath;

	tempPath = string(argv[2]) + "/xml/";

	buffer = "tempDoxyfile";

	ofstream file1(buffer.c_str());

	if (file1.good())
		file1 << content;

	file1.close();

	//Checking if processor is available
	if (!system(NULL))
		return 1;

	//"Executing command doxygen

	buffer = "doxygen " + buffer;

	system(buffer.c_str());

	DIR *dp;
	struct dirent *ep;
	queue<string> inputFiles;
	string fileName;

	dp = opendir(tempPath.c_str());
	if (dp != NULL) {
		while (ep = readdir(dp)) {
			fileName = ep->d_name;
			if ((fileName.size() > 3) && ((fileName.substr(0, 4) != "dir_")) && ((fileName.substr(fileName.length() - 4) == ".xml"))
					&& ((fileName != "index.xml")))
				inputFiles.push(fileName);
		}
		(void) closedir(dp);
	} else {
		cerr << "Couldn't open the directory" << endl;
		return 1;
	}

	while (!inputFiles.empty()) {

		FilterXml filterXml(tempPath + inputFiles.front());

		if (!filterXml.loadFile()) {
			cout << "can not load xml-file: " << inputFiles.front() << endl;
			return 1;
		} else {
			if (filterXml.filter() == 0) {
				BuilderKaom builderKaom(filterXml.GetResult(), string(argv[2]) + "/" + filterXml.GetOutputFileName());

				builderKaom.buildResult();
			} else {
				cout << "Nothing to do here." << endl;
			}
		}

		inputFiles.pop();
	}

	return 0;
}

