#ifndef __c1_elevator2_h__
#define __c1_elevator2_h__

/* Include files */
#include "sfc_sf.h"
#include "sfc_mex.h"
#include "rtw_capi.h"
#include "rtw_modelmap.h"

/* Type Definitions */

typedef struct {
  real_T c1_Dir;
  real_T c1_data;
  SimStruct *S;
  void *c1_testPointAddrMap[8];
  uint32_T c1_AlarmLampEventCounter;
  uint32_T c1_OpenDoorEventCounter;
  uint32_T chartNumber;
  uint32_T instanceNumber;
  uint8_T c1_is_Alarm;
  uint8_T c1_is_Normal;
  uint8_T c1_is_active_c1_elevator2;
  uint8_T c1_is_c1_elevator2;
  uint8_T c1_tp_Alarm;
  uint8_T c1_tp_Down;
  uint8_T c1_tp_Error;
  uint8_T c1_tp_Normal;
  uint8_T c1_tp_Off;
  uint8_T c1_tp_On;
  uint8_T c1_was_Normal;
  rtwCAPI_ModelMappingInfo c1_testPointMappingInfo;
  ChartInfoStruct chartInfo;
} SFc1_elevator2InstanceStruct;

/* Named Constants */

/* Variable Declarations */

/* Variable Definitions */

/* Function Declarations */

/* Function Definitions */

extern void sf_c1_elevator2_get_check_sum(mxArray *plhs[]);
extern void c1_elevator2_method_dispatcher(SimStruct *S, int_T method, void
 *data);

#endif

