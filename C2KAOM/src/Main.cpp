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
#include <FilterXml.hh>
#include <BuilderKaom.hh>
#include <BuilderXml.hh>

/*
 * The function main function implements the basic sequence of C2KAOM and creates the instances of all other classes.
 * There are two arguments.
 * The source directory and the target directory.
 */
int main(int argc, char *argv[]) {

	//Test if all arguments are passed.
	if (argc != 3) {
		cout << "usage: " << argv[0] << " [sourcePath] [outputPath]" << endl;
		return 1;
	}

	//Call Doxygen and build XML-files.
	BuilderXml builderXml(argv[1], argv[2]);
	queue<string> inputFiles = builderXml.GetInput();

	/*
	 * For every XML-file in the subdirectory "xml"
	 * call pugixml and filter the annotations.
	 * If annotations are found, build the KAOM-modell,
	 * else skip the XML-file,
	 */
	while (!inputFiles.empty()) {
		FilterXml filterXml(string(argv[2]) + "/xml/" + inputFiles.front());
		//load xml-file
		if (!filterXml.loadFile()) {
			cerr << "can not load xml-file: " << inputFiles.front() << endl;
			return 1;
		} else {
			//filter xml-file
			if (filterXml.filter() == 0) {
				//build KAOM-modell
				BuilderKaom builderKaom(filterXml.GetResult(), string(argv[2]) + "/" + filterXml.GetOutputFileName());
				builderKaom.buildResult();
			}
		}
		inputFiles.pop();
	}
	return 0;
}
