/* Include files */
#include "elevator2_sfun.h"
#include "c1_elevator2.h"

/* Type Definitions */

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */
uint8_T _sfEvent_;
uint32_T _elevator2MachineNumber_;
real_T _sfTime_;

/* Function Declarations */

/* Function Definitions */
void elevator2_initializer(void)
{
  _sfEvent_ = CALL_EVENT;
}

void elevator2_terminator(void)
{
}

/* SFunction Glue Code */
unsigned int sf_elevator2_method_dispatcher(SimStruct *simstructPtr, const char
 *chartName, int_T method, void *data)
{
  if(!strcmp_ignore_ws(chartName,"elevator2/Controller/ SFunction ")) {
    c1_elevator2_method_dispatcher(simstructPtr, method, data);
    return 1;
  }
  return 0;
}
unsigned int sf_elevator2_process_check_sum_call( int nlhs, mxArray * plhs[],
 int nrhs, const mxArray * prhs[] )
{
#ifdef MATLAB_MEX_FILE
  char commandName[20];
  if (nrhs<1 || !mxIsChar(prhs[0]) ) return 0;
  /* Possible call to get the checksum */
  mxGetString(prhs[0], commandName,sizeof(commandName)/sizeof(char));
  commandName[(sizeof(commandName)/sizeof(char)-1)] = '\0';
  if(strcmp(commandName,"sf_get_check_sum")) return 0;
  plhs[0] = mxCreateDoubleMatrix( 1,4,mxREAL);
  if(nrhs>1 && mxIsChar(prhs[1])) {
    mxGetString(prhs[1], commandName,sizeof(commandName)/sizeof(char));
    commandName[(sizeof(commandName)/sizeof(char)-1)] = '\0';
    if(!strcmp(commandName,"machine")) {
      ((real_T *)mxGetPr((plhs[0])))[0] = (real_T)(1771347330U);
      ((real_T *)mxGetPr((plhs[0])))[1] = (real_T)(187719994U);
      ((real_T *)mxGetPr((plhs[0])))[2] = (real_T)(2475943248U);
      ((real_T *)mxGetPr((plhs[0])))[3] = (real_T)(584231384U);
    }else if(!strcmp(commandName,"exportedFcn")) {
      ((real_T *)mxGetPr((plhs[0])))[0] = (real_T)(0U);
      ((real_T *)mxGetPr((plhs[0])))[1] = (real_T)(0U);
      ((real_T *)mxGetPr((plhs[0])))[2] = (real_T)(0U);
      ((real_T *)mxGetPr((plhs[0])))[3] = (real_T)(0U);
    }else if(!strcmp(commandName,"makefile")) {
      ((real_T *)mxGetPr((plhs[0])))[0] = (real_T)(999426590U);
      ((real_T *)mxGetPr((plhs[0])))[1] = (real_T)(1916787250U);
      ((real_T *)mxGetPr((plhs[0])))[2] = (real_T)(3986423938U);
      ((real_T *)mxGetPr((plhs[0])))[3] = (real_T)(2478448413U);
    }else if(nrhs==3 && !strcmp(commandName,"chart")) {
      unsigned int chartFileNumber;
      chartFileNumber = (unsigned int)mxGetScalar(prhs[2]);
      switch(chartFileNumber) {
       case 1:
        {
          extern void sf_c1_elevator2_get_check_sum(mxArray *plhs[]);
          sf_c1_elevator2_get_check_sum(plhs);
          break;
        }

       default:
        ((real_T *)mxGetPr((plhs[0])))[0] = (real_T)(0.0);
        ((real_T *)mxGetPr((plhs[0])))[1] = (real_T)(0.0);
        ((real_T *)mxGetPr((plhs[0])))[2] = (real_T)(0.0);
        ((real_T *)mxGetPr((plhs[0])))[3] = (real_T)(0.0);
      }
    }else if(!strcmp(commandName,"target")) {
      ((real_T *)mxGetPr((plhs[0])))[0] = (real_T)(2553529877U);
      ((real_T *)mxGetPr((plhs[0])))[1] = (real_T)(1250385535U);
      ((real_T *)mxGetPr((plhs[0])))[2] = (real_T)(3747036769U);
      ((real_T *)mxGetPr((plhs[0])))[3] = (real_T)(84901116U);
    }else {
      return 0;
    }
  } else{
    ((real_T *)mxGetPr((plhs[0])))[0] = (real_T)(1241465750U);
    ((real_T *)mxGetPr((plhs[0])))[1] = (real_T)(2989255692U);
    ((real_T *)mxGetPr((plhs[0])))[2] = (real_T)(1421337686U);
    ((real_T *)mxGetPr((plhs[0])))[3] = (real_T)(3633543581U);
  }
  return 1;
#else
  return 0;
#endif
}

unsigned int sf_elevator2_autoinheritance_info( int nlhs, mxArray * plhs[], int
 nrhs, const mxArray * prhs[] )
{
#ifdef MATLAB_MEX_FILE
  char commandName[32];
  if (nrhs<2 || !mxIsChar(prhs[0]) ) return 0;
  /* Possible call to get the autoinheritance_info */
  mxGetString(prhs[0], commandName,sizeof(commandName)/sizeof(char));
  commandName[(sizeof(commandName)/sizeof(char)-1)] = '\0';
  if(strcmp(commandName,"get_autoinheritance_info")) return 0;
  {
    unsigned int chartFileNumber;
    chartFileNumber = (unsigned int)mxGetScalar(prhs[1]);
    switch(chartFileNumber) {
     case 1:
      {
        extern mxArray *sf_c1_elevator2_get_autoinheritance_info(void);
        plhs[0] = sf_c1_elevator2_get_autoinheritance_info();
        break;
      }

     default:
      plhs[0] = mxCreateDoubleMatrix(0,0,mxREAL);
    }
  }
  return 1;
#else
  return 0;
#endif
}
void elevator2_debug_initialize(void)
{
  _elevator2MachineNumber_ =
  sf_debug_initialize_machine("elevator2","sfun",0,1,0,0,0);
  sf_debug_set_machine_event_thresholds(_elevator2MachineNumber_,0,0);
  sf_debug_set_machine_data_thresholds(_elevator2MachineNumber_,0);
}

void elevator2_register_exported_symbols(SimStruct* S)
{
}
