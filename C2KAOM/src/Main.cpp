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

int main(int argc, char *argv[]) {

	if (argc != 3) {
		cout << "usage: " << argv[0] << " [sourcePath] [outputPath]" << endl;
		return 1;
	}

	BuilderXml builderXml(argv[1], argv[2]);

	queue<string> inputFiles = builderXml.GetInput();

	while (!inputFiles.empty()) {

		FilterXml filterXml(string(argv[2]) + "/xml/" + inputFiles.front());

		if (!filterXml.loadFile()) {
			cout << "can not load xml-file: " << inputFiles.front() << endl;
			return 1;
		} else {
			if (filterXml.filter() == 0) {
				BuilderKaom builderKaom(filterXml.GetResult(), string(argv[2]) + "/" + filterXml.GetOutputFileName());

				builderKaom.buildResult();
			}
		}

		inputFiles.pop();
	}

	return 0;
}

