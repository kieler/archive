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

#ifndef TEMPLATES_HH_
#define TEMPLATES_HH_

using namespace std;

template<typename FirstType, typename SecondType>
struct PairComparator {
	bool operator()(const pair<FirstType, SecondType>& p1, const pair<FirstType, SecondType>& p2) const {
		return p1.first > p2.first ? true : false;
		//return p1.second > p2.second;
	}
};


#endif /* TEMPLATES_HH_ */
