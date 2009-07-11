// ScadeSlaveTest.cpp : Definiert den Einstiegspunkt für die Konsolenanwendung.
//

#include "stdafx.h"
#include "SsmSlaveLib.h"


#define true  1
#define false 0

int _tmain(int argc, _TCHAR* argv[])
{
	printf("Opening SCADE Simulator\n");
	
	char* hostname = "10.6.3.5";
	SsmSetHostName(hostname);

	char* scadebin ="C:\\Programme\\Esterel Technologies\\Scade60b1\\bin\\";
	SsmSetScadeInstallPath(scadebin);

	char* workspace     = "U:\\shared\\modelgui\\trunk\\scade6_example\\scade6_example.vsw";
	char* configuration = "XYZSimulation";
	//char* rootnode      = "simulation::railway";
	char* rootnode      = "";
	int port            = 64064;

	SsmOpenScadeSimulator(workspace,configuration,rootnode,port);

	int i = 0;
	for(i=0;i<5;i++){
		SsmStep(1,true);
	}

	/* Retrieve output value related to pszPath */
	//extern int SsmGetOutput(char* pszPath, const char** ppszValue);
	char output[5000];
	const char* outputPointer = (const char*)output;
	SsmGetOutput("simulation::railway/displayData", &outputPointer);
	printf("Output: %s\n",outputPointer);

	/* Retrieve output values computed by SCADE Simulator */
	//extern int SsmGetOutputVector(const char** ppszOutputVector);
	char outputVector[1000]; 
	const char* outputVectorPointer = (const char*)outputVector;
	SsmGetOutputVector(&outputVectorPointer);
	printf("OutputVector: %s\n",outputVectorPointer);

	/* Set pszValue to input named pszPath */
	//extern int SsmSetInput(char* pszPath, char* pszValue);

	/* Send input to SCADE simulator in formatted vector */
	//extern int SsmSetInputVector(const char* pszInputVector);
	
	/*char input[5000];
	strcpy_s(input,"[");
	for(i=0;i<47;i++){
		strcat_s(input,"simulation::p_turn,");
	}
	strcat_s(input,"simulation::p_turn]");
	*/

	
	char* input = "(true, true, true)";
	printf("Input: %s\n",input);
//	SsmSetInput("simulation::railway/controlData", input);

	SsmSetInputVector(input);

	for(i=0;i<100;i++){
		SsmStep(1,true);
	}
	
	/* Ask SCADE GUI to be foreground window */
	extern int SsmGuiActivate();
	SsmGuiActivate();
	
	SsmStep(1,true);

	/* Ask SCADE GUI to refresh values */
	//extern int SsmGuiRefresh();
	SsmGuiRefresh();

	SsmStep(1,true);
	SsmDbgStep(1);
	SsmStep(1,true);

	const char* error = "Error";
	SsmGetLastError(&error);
	printf("Error: %s\n",error);
	
	SsmCloseScadeSimulator();

	printf("finished.");
	while(1){;/*nothing*/}
	return 0;
}

