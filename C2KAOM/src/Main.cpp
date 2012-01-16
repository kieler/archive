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
#include <iostream>

using namespace std;

int main(int argc, char *argv[]) {

	if (argc != 3) {
		cout << "usage: " << argv[0] << " infile.xml outfile.kaot" << endl;
		return 0;
	}

	FilterXml filterXml((string(argv[1])));

	if (!filterXml.loadFile()) {
		cout << "can not load xml-file: " << argv[1] << endl;
		return 1;
	} else {
		filterXml.filter();
	}

	BuilderKaom builderKaom(filterXml.GetResult(), string(argv[2]));

	builderKaom.buildResult();

	return 0;
}

