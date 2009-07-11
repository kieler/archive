/* Include files */
#include "elevator2_sfun.h"
#include "c1_elevator2.h"
#define CHARTINSTANCE_CHARTNUMBER       (chartInstance.chartNumber)
#define CHARTINSTANCE_INSTANCENUMBER    (chartInstance.instanceNumber)
#include "elevator2_sfun_debug_macros.h"

/* Type Definitions */

/* Named Constants */
#define c1_event_ButtonUp               (2U)
#define c1_event_ButtonDown             (0U)
#define c1_event_ButtonAlarm            (1U)
#define c1_event_Second                 (3U)
#define c1_event_IsUp                   (7U)
#define c1_event_IsDown                 (6U)
#define c1_event_Dummy                  (8U)
#define c1_IN_NO_ACTIVE_CHILD           (0)
#define c1_IN_Normal                    (3)
#define c1_IN_Error                     (2)
#define c1_IN_Alarm                     (1)
#define c1_IN_Off                       (1)
#define c1_IN_On                        (2)
#define c1_IN_Down                      (1)

/* Variable Declarations */

/* Variable Definitions */
static SFc1_elevator2InstanceStruct chartInstance;

/* Function Declarations */
static void initialize_c1_elevator2(void);
static void initialize_params_c1_elevator2(void);
static void enable_c1_elevator2(void);
static void disable_c1_elevator2(void);
static void finalize_c1_elevator2(void);
static void sf_c1_elevator2(void);
static void c1_c1_elevator2(void);
static boolean_T c1__bool_s32_(int32_T c1_b);
static boolean_T c1__bool_u32_(uint32_T c1_b);
static real_T *c1_Move(void);
static int8_T *c1_ButtonUp(void);
static int8_T *c1_ButtonDown(void);
static int8_T *c1_ButtonAlarm(void);
static int8_T *c1_Second(void);
static int8_T *c1_IsUp(void);
static int8_T *c1_IsDown(void);
static int8_T *c1_Dummy(void);
static boolean_T *c1_AlarmLamp(void);
static boolean_T *c1_OpenDoor(void);
static void init_test_point_addr_map(void);
static void **get_test_point_address_map(void);
static rtwCAPI_ModelMappingInfo *get_test_point_mapping_info(void);
static void init_dsm_address_info(void);

/* Function Definitions */
static void initialize_c1_elevator2(void)
{
  _sfTime_ = (real_T)ssGetT(chartInstance.S);
  chartInstance.c1_is_Alarm = 0U;
  chartInstance.c1_tp_Alarm = 0U;
  chartInstance.c1_tp_Off = 0U;
  chartInstance.c1_tp_On = 0U;
  chartInstance.c1_tp_Error = 0U;
  chartInstance.c1_is_Normal = 0U;
  chartInstance.c1_was_Normal = 0U;
  chartInstance.c1_tp_Normal = 0U;
  chartInstance.c1_tp_Down = 0U;
  chartInstance.c1_is_active_c1_elevator2 = 0U;
  chartInstance.c1_is_c1_elevator2 = 0U;
  chartInstance.c1_data = 0.0;
  chartInstance.c1_Dir = 0.0;
  if(!((boolean_T)cdrGetOutputPortReusable(chartInstance.S, 1) != 0)) {
    *c1_Move() = 0.0;
  }
  chartInstance.c1_AlarmLampEventCounter = 0U;
  *c1_AlarmLamp() = 0U;
  chartInstance.c1_OpenDoorEventCounter = 0U;
  *c1_OpenDoor() = 0U;
}

static void initialize_params_c1_elevator2(void)
{
}

static void enable_c1_elevator2(void)
{
}

static void disable_c1_elevator2(void)
{
}

static void finalize_c1_elevator2(void)
{
}

static void sf_c1_elevator2(void)
{
  int32_T c1_inputEventFiredFlag;
  uint8_T c1_previousEvent;
  uint8_T c1_b_previousEvent;
  uint8_T c1_c_previousEvent;
  uint8_T c1_d_previousEvent;
  uint8_T c1_e_previousEvent;
  uint8_T c1_f_previousEvent;
  uint8_T c1_g_previousEvent;
  _sfTime_ = (real_T)ssGetT(chartInstance.S);
  _SFD_DATA_RANGE_CHECK(*c1_Move(), 0U);
  _SFD_DATA_RANGE_CHECK(chartInstance.c1_data, 1U);
  _SFD_DATA_RANGE_CHECK(chartInstance.c1_Dir, 2U);
  c1_inputEventFiredFlag = 0;
  if(*c1_ButtonUp() != 0) {
    c1_inputEventFiredFlag = 1;
    c1_previousEvent = _sfEvent_;
    _sfEvent_ = c1_event_ButtonUp;
    _SFD_CE_CALL(EVENT_BEFORE_BROADCAST_TAG,c1_event_ButtonUp);
    c1_c1_elevator2();
    _SFD_CE_CALL(EVENT_AFTER_BROADCAST_TAG,c1_event_ButtonUp);
    _sfEvent_ = c1_previousEvent;
  }
  if(*c1_ButtonDown() != 0) {
    c1_inputEventFiredFlag = 1;
    c1_b_previousEvent = _sfEvent_;
    _sfEvent_ = c1_event_ButtonDown;
    _SFD_CE_CALL(EVENT_BEFORE_BROADCAST_TAG,c1_event_ButtonDown);
    c1_c1_elevator2();
    _SFD_CE_CALL(EVENT_AFTER_BROADCAST_TAG,c1_event_ButtonDown);
    _sfEvent_ = c1_b_previousEvent;
  }
  if(*c1_ButtonAlarm() != 0) {
    c1_inputEventFiredFlag = 1;
    c1_c_previousEvent = _sfEvent_;
    _sfEvent_ = c1_event_ButtonAlarm;
    _SFD_CE_CALL(EVENT_BEFORE_BROADCAST_TAG,c1_event_ButtonAlarm);
    c1_c1_elevator2();
    _SFD_CE_CALL(EVENT_AFTER_BROADCAST_TAG,c1_event_ButtonAlarm);
    _sfEvent_ = c1_c_previousEvent;
  }
  if(*c1_Second() == 1) {
    c1_inputEventFiredFlag = 1;
    c1_d_previousEvent = _sfEvent_;
    _sfEvent_ = c1_event_Second;
    _SFD_CE_CALL(EVENT_BEFORE_BROADCAST_TAG,c1_event_Second);
    c1_c1_elevator2();
    _SFD_CE_CALL(EVENT_AFTER_BROADCAST_TAG,c1_event_Second);
    _sfEvent_ = c1_d_previousEvent;
  }
  if(*c1_IsUp() == 1) {
    c1_inputEventFiredFlag = 1;
    c1_e_previousEvent = _sfEvent_;
    _sfEvent_ = c1_event_IsUp;
    _SFD_CE_CALL(EVENT_BEFORE_BROADCAST_TAG,c1_event_IsUp);
    c1_c1_elevator2();
    _SFD_CE_CALL(EVENT_AFTER_BROADCAST_TAG,c1_event_IsUp);
    _sfEvent_ = c1_e_previousEvent;
  }
  if(*c1_IsDown() == 1) {
    c1_inputEventFiredFlag = 1;
    c1_f_previousEvent = _sfEvent_;
    _sfEvent_ = c1_event_IsDown;
    _SFD_CE_CALL(EVENT_BEFORE_BROADCAST_TAG,c1_event_IsDown);
    c1_c1_elevator2();
    _SFD_CE_CALL(EVENT_AFTER_BROADCAST_TAG,c1_event_IsDown);
    _sfEvent_ = c1_f_previousEvent;
  }
  if(*c1_Dummy() != 0) {
    c1_inputEventFiredFlag = 1;
    c1_g_previousEvent = _sfEvent_;
    _sfEvent_ = c1_event_Dummy;
    _SFD_CE_CALL(EVENT_BEFORE_BROADCAST_TAG,c1_event_Dummy);
    c1_c1_elevator2();
    _SFD_CE_CALL(EVENT_AFTER_BROADCAST_TAG,c1_event_Dummy);
    _sfEvent_ = c1_g_previousEvent;
  }
  if((c1__bool_s32_(c1_inputEventFiredFlag) != 0) &&
   (chartInstance.c1_AlarmLampEventCounter > 0U)) {
    *c1_AlarmLamp() = !*c1_AlarmLamp();
    chartInstance.c1_AlarmLampEventCounter =
      chartInstance.c1_AlarmLampEventCounter - 1U;
  }
  if((c1__bool_s32_(c1_inputEventFiredFlag) != 0) &&
   (chartInstance.c1_OpenDoorEventCounter > 0U)) {
    *c1_OpenDoor() = !*c1_OpenDoor();
    chartInstance.c1_OpenDoorEventCounter =
      chartInstance.c1_OpenDoorEventCounter - 1U;
  }
  sf_debug_check_for_state_inconsistency(_elevator2MachineNumber_,
   chartInstance.chartNumber, chartInstance.instanceNumber);
}

static void c1_c1_elevator2(void)
{
  real_T c1_b_Move;
  real_T c1_c_Move;
  real_T c1_d_Move;
  real_T c1_e_Move;
  _SFD_CC_CALL(CHART_ENTER_DURING_FUNCTION_TAG,0);
  if(chartInstance.c1_is_active_c1_elevator2 == 0) {
    _SFD_CC_CALL(CHART_ENTER_ENTRY_FUNCTION_TAG,0);
    chartInstance.c1_is_active_c1_elevator2 = 1U;
    _SFD_CC_CALL(EXIT_OUT_OF_FUNCTION_TAG,0);
    _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,0);
    _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,0);
    chartInstance.c1_is_c1_elevator2 = (uint8_T)c1_IN_Normal;
    _SFD_CS_CALL(STATE_ACTIVE_TAG,3);
    chartInstance.c1_tp_Normal = 1U;
    _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,4);
    _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,4);
    chartInstance.c1_is_Normal = (uint8_T)c1_IN_Down;
    chartInstance.c1_was_Normal = (uint8_T)c1_IN_Down;
    _SFD_CS_CALL(STATE_ACTIVE_TAG,1);
    chartInstance.c1_tp_Down = 1U;
  } else {
    switch(chartInstance.c1_is_c1_elevator2) {
     case c1_IN_Alarm:
      CV_CHART_EVAL(0,0,1);
      _SFD_CS_CALL(STATE_ENTER_DURING_FUNCTION_TAG,4);
      switch(chartInstance.c1_is_Alarm) {
       case c1_IN_Off:
        CV_STATE_EVAL(4,0,1);
        _SFD_CS_CALL(STATE_ENTER_DURING_FUNCTION_TAG,0);
        _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,8);
        if(c1__bool_u32_(CV_TRANSITION_EVAL(8U,
           (int32_T)_SFD_CCP_CALL(8,0,((_sfEvent_ == c1_event_Second)!=0)))) !=
         0) {
          _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,8);
          _SFD_CS_CALL(STATE_ENTER_EXIT_FUNCTION_TAG,0);
          chartInstance.c1_tp_Off = 0U;
          _SFD_CS_CALL(STATE_INACTIVE_TAG,0);
          _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,0);
          chartInstance.c1_is_Alarm = (uint8_T)c1_IN_On;
          _SFD_CS_CALL(STATE_ACTIVE_TAG,2);
          chartInstance.c1_tp_On = 1U;
        }
        _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,0);
        break;
       case c1_IN_On:
        CV_STATE_EVAL(4,0,2);
        _SFD_CS_CALL(STATE_ENTER_DURING_FUNCTION_TAG,2);
        _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,1);
        if(c1__bool_u32_(CV_TRANSITION_EVAL(1U,
           (int32_T)_SFD_CCP_CALL(1,0,((_sfEvent_ == c1_event_Second)!=0)))) !=
         0) {
          _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,1);
          _SFD_CS_CALL(STATE_ENTER_EXIT_FUNCTION_TAG,2);
          chartInstance.c1_tp_On = 0U;
          _SFD_CS_CALL(STATE_INACTIVE_TAG,2);
          _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,2);
          chartInstance.c1_is_Alarm = (uint8_T)c1_IN_Off;
          _SFD_CS_CALL(STATE_ACTIVE_TAG,0);
          chartInstance.c1_tp_Off = 1U;
        } else {
          sf_mex_printf("%s\\n", "AlarmLamp");
          chartInstance.c1_AlarmLampEventCounter =
            chartInstance.c1_AlarmLampEventCounter + 1U;
        }
        _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,2);
        break;
       default:
        CV_STATE_EVAL(4,0,0);
        chartInstance.c1_is_Alarm = (uint8_T)c1_IN_NO_ACTIVE_CHILD;
        _SFD_CS_CALL(STATE_INACTIVE_TAG,0);
        break;
      }
      _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,4);
      break;
     case c1_IN_Error:
      CV_CHART_EVAL(0,0,2);
      _SFD_CS_CALL(STATE_ENTER_DURING_FUNCTION_TAG,5);
      _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,3);
      if(c1__bool_u32_(CV_TRANSITION_EVAL(3U,
         (int32_T)_SFD_CCP_CALL(3,0,((_sfEvent_ == c1_event_ButtonAlarm)!=0))))
       != 0) {
        if(sf_debug_transition_conflict_check_enabled()) {
          unsigned int transitionList[2];
          unsigned int numTransitions=1;
          transitionList[0] = 3;
          sf_debug_transition_conflict_check_begin();
          if(_sfEvent_ == c1_event_Second) {
            transitionList[numTransitions] = 5;
            numTransitions++;
          }
          sf_debug_transition_conflict_check_end();
          if(numTransitions>1) {
            _SFD_TRANSITION_CONFLICT(&(transitionList[0]),numTransitions);
          }
        }
        _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,3);
        _SFD_CS_CALL(STATE_ENTER_EXIT_FUNCTION_TAG,5);
        chartInstance.c1_tp_Error = 0U;
        _SFD_CS_CALL(STATE_INACTIVE_TAG,5);
        _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,5);
        chartInstance.c1_is_c1_elevator2 = (uint8_T)c1_IN_Alarm;
        _SFD_CS_CALL(STATE_ACTIVE_TAG,4);
        chartInstance.c1_tp_Alarm = 1U;
        _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,6);
        _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,6);
        chartInstance.c1_is_Alarm = (uint8_T)c1_IN_On;
        _SFD_CS_CALL(STATE_ACTIVE_TAG,2);
        chartInstance.c1_tp_On = 1U;
      } else {
        _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,5);
        if(c1__bool_u32_(CV_TRANSITION_EVAL(5U,
           (int32_T)_SFD_CCP_CALL(5,0,((_sfEvent_ == c1_event_Second)!=0)))) !=
         0) {
          _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,5);
          _SFD_CS_CALL(STATE_ENTER_EXIT_FUNCTION_TAG,5);
          chartInstance.c1_tp_Error = 0U;
          _SFD_CS_CALL(STATE_INACTIVE_TAG,5);
          _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,5);
          _SFD_DATA_RANGE_CHECK(*c1_Move(), 0U);
          c1_b_Move = *c1_Move() = -1.0;
          sf_mex_printf("%s =\\n", "Move");
          sf_mex_call("disp", 0U, 1U, 6, c1_b_Move);
          chartInstance.c1_is_c1_elevator2 = (uint8_T)c1_IN_Normal;
          _SFD_CS_CALL(STATE_ACTIVE_TAG,3);
          chartInstance.c1_tp_Normal = 1U;
          _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,4);
          _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,4);
          chartInstance.c1_is_Normal = (uint8_T)c1_IN_Down;
          chartInstance.c1_was_Normal = (uint8_T)c1_IN_Down;
          _SFD_CS_CALL(STATE_ACTIVE_TAG,1);
          chartInstance.c1_tp_Down = 1U;
        } else {
          sf_mex_printf("%s\\n", "AlarmLamp");
          chartInstance.c1_AlarmLampEventCounter =
            chartInstance.c1_AlarmLampEventCounter + 1U;
        }
      }
      _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,5);
      break;
     case c1_IN_Normal:
      CV_CHART_EVAL(0,0,3);
      _SFD_CS_CALL(STATE_ENTER_DURING_FUNCTION_TAG,3);
      _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,7);
      if(c1__bool_u32_(CV_TRANSITION_EVAL(7U,
         (int32_T)_SFD_CCP_CALL(7,0,((_sfEvent_ == c1_event_ButtonAlarm)!=0))))
       != 0) {
        _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,7);
        _SFD_CS_CALL(STATE_ENTER_EXIT_FUNCTION_TAG,1);
        chartInstance.c1_tp_Down = 0U;
        chartInstance.c1_is_Normal = (uint8_T)c1_IN_NO_ACTIVE_CHILD;
        _SFD_CS_CALL(STATE_INACTIVE_TAG,1);
        _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,1);
        _SFD_CS_CALL(STATE_ENTER_EXIT_FUNCTION_TAG,3);
        chartInstance.c1_tp_Normal = 0U;
        chartInstance.c1_is_c1_elevator2 = (uint8_T)c1_IN_NO_ACTIVE_CHILD;
        _SFD_CS_CALL(STATE_INACTIVE_TAG,3);
        _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,3);
        _SFD_DATA_RANGE_CHECK(*c1_Move(), 0U);
        c1_c_Move = *c1_Move() = 0.0;
        sf_mex_printf("%s =\\n", "Move");
        sf_mex_call("disp", 0U, 1U, 6, c1_c_Move);
        chartInstance.c1_is_c1_elevator2 = (uint8_T)c1_IN_Error;
        _SFD_CS_CALL(STATE_ACTIVE_TAG,5);
        chartInstance.c1_tp_Error = 1U;
      } else {
        _SFD_CS_CALL(STATE_ENTER_DURING_FUNCTION_TAG,1);
        _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,2);
        if(c1__bool_u32_(CV_TRANSITION_EVAL(2U,
           (int32_T)_SFD_CCP_CALL(2,0,((_sfEvent_ == c1_event_ButtonDown)!=0))))
         != 0) {
          if(sf_debug_transition_conflict_check_enabled()) {
            unsigned int transitionList[2];
            unsigned int numTransitions=1;
            transitionList[0] = 2;
            sf_debug_transition_conflict_check_begin();
            if(_sfEvent_ == c1_event_ButtonUp) {
              transitionList[numTransitions] = 9;
              numTransitions++;
            }
            sf_debug_transition_conflict_check_end();
            if(numTransitions>1) {
              _SFD_TRANSITION_CONFLICT(&(transitionList[0]),numTransitions);
            }
          }
          _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,2);
          _SFD_CS_CALL(STATE_ENTER_EXIT_FUNCTION_TAG,1);
          chartInstance.c1_tp_Down = 0U;
          _SFD_CS_CALL(STATE_INACTIVE_TAG,1);
          _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,1);
          _SFD_DATA_RANGE_CHECK(*c1_Move(), 0U);
          c1_d_Move = *c1_Move() = -1.0;
          sf_mex_printf("%s =\\n", "Move");
          sf_mex_call("disp", 0U, 1U, 6, c1_d_Move);
          sf_mex_printf("%s\\n", "OpenDoor");
          chartInstance.c1_OpenDoorEventCounter =
            chartInstance.c1_OpenDoorEventCounter + 1U;
          chartInstance.c1_is_Normal = (uint8_T)c1_IN_Down;
          chartInstance.c1_was_Normal = (uint8_T)c1_IN_Down;
          _SFD_CS_CALL(STATE_ACTIVE_TAG,1);
          chartInstance.c1_tp_Down = 1U;
        } else {
          _SFD_CT_CALL(TRANSITION_BEFORE_PROCESSING_TAG,9);
          if(c1__bool_u32_(CV_TRANSITION_EVAL(9U,
             (int32_T)_SFD_CCP_CALL(9,0,((_sfEvent_ == c1_event_ButtonUp)!=0))))
           != 0) {
            _SFD_CT_CALL(TRANSITION_ACTIVE_TAG,9);
            _SFD_CS_CALL(STATE_ENTER_EXIT_FUNCTION_TAG,1);
            chartInstance.c1_tp_Down = 0U;
            _SFD_CS_CALL(STATE_INACTIVE_TAG,1);
            _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,1);
            _SFD_DATA_RANGE_CHECK(*c1_Move(), 0U);
            c1_e_Move = *c1_Move() = 1.0;
            sf_mex_printf("%s =\\n", "Move");
            sf_mex_call("disp", 0U, 1U, 6, c1_e_Move);
            sf_mex_printf("%s\\n", "OpenDoor");
            chartInstance.c1_OpenDoorEventCounter =
              chartInstance.c1_OpenDoorEventCounter + 1U;
            chartInstance.c1_is_Normal = (uint8_T)c1_IN_Down;
            chartInstance.c1_was_Normal = (uint8_T)c1_IN_Down;
            _SFD_CS_CALL(STATE_ACTIVE_TAG,1);
            chartInstance.c1_tp_Down = 1U;
          }
        }
        _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,1);
      }
      _SFD_CS_CALL(EXIT_OUT_OF_FUNCTION_TAG,3);
      break;
     default:
      CV_CHART_EVAL(0,0,0);
      chartInstance.c1_is_c1_elevator2 = (uint8_T)c1_IN_NO_ACTIVE_CHILD;
      _SFD_CS_CALL(STATE_INACTIVE_TAG,4);
      break;
    }
  }
  _SFD_CC_CALL(EXIT_OUT_OF_FUNCTION_TAG,0);
}

static boolean_T c1__bool_s32_(int32_T c1_b)
{
  boolean_T c1_a;
  c1_a = (boolean_T)c1_b;
  if(c1_a != c1_b) {
    sf_debug_overflow_detection(0);
  }
  return c1_a;
}

static boolean_T c1__bool_u32_(uint32_T c1_b)
{
  boolean_T c1_a;
  c1_a = (boolean_T)c1_b;
  if((uint32_T)c1_a != c1_b) {
    sf_debug_overflow_detection(0);
  }
  return c1_a;
}

static real_T *c1_Move(void)
{
  return (real_T *)ssGetOutputPortSignal(chartInstance.S, 1);
}

static int8_T *c1_ButtonUp(void)
{
  return (int8_T *)*(ssGetInputPortSignalPtrs(chartInstance.S, 0) + 0);
}

static int8_T *c1_ButtonDown(void)
{
  return (int8_T *)*(ssGetInputPortSignalPtrs(chartInstance.S, 0) + 1);
}

static int8_T *c1_ButtonAlarm(void)
{
  return (int8_T *)*(ssGetInputPortSignalPtrs(chartInstance.S, 0) + 2);
}

static int8_T *c1_Second(void)
{
  return (int8_T *)*(ssGetInputPortSignalPtrs(chartInstance.S, 0) + 3);
}

static int8_T *c1_IsUp(void)
{
  return (int8_T *)*(ssGetInputPortSignalPtrs(chartInstance.S, 0) + 4);
}

static int8_T *c1_IsDown(void)
{
  return (int8_T *)*(ssGetInputPortSignalPtrs(chartInstance.S, 0) + 5);
}

static int8_T *c1_Dummy(void)
{
  return (int8_T *)*(ssGetInputPortSignalPtrs(chartInstance.S, 0) + 6);
}

static boolean_T *c1_AlarmLamp(void)
{
  return (boolean_T *)ssGetOutputPortSignal(chartInstance.S, 2);
}

static boolean_T *c1_OpenDoor(void)
{
  return (boolean_T *)ssGetOutputPortSignal(chartInstance.S, 3);
}

static void init_test_point_addr_map(void)
{
  chartInstance.c1_testPointAddrMap[0] = &chartInstance.c1_data;
  chartInstance.c1_testPointAddrMap[1] = &chartInstance.c1_Dir;
  chartInstance.c1_testPointAddrMap[2] = &chartInstance.c1_tp_Alarm;
  chartInstance.c1_testPointAddrMap[3] = &chartInstance.c1_tp_Off;
  chartInstance.c1_testPointAddrMap[4] = &chartInstance.c1_tp_On;
  chartInstance.c1_testPointAddrMap[5] = &chartInstance.c1_tp_Error;
  chartInstance.c1_testPointAddrMap[6] = &chartInstance.c1_tp_Normal;
  chartInstance.c1_testPointAddrMap[7] = &chartInstance.c1_tp_Down;
}

static void **get_test_point_address_map(void)
{
  return &chartInstance.c1_testPointAddrMap[0];
}

static rtwCAPI_ModelMappingInfo *get_test_point_mapping_info(void)
{
  return &chartInstance.c1_testPointMappingInfo;
}

static void init_dsm_address_info(void)
{
}

/* SFunction Glue Code */
static void init_test_point_mapping_info(SimStruct *S);
void sf_c1_elevator2_get_check_sum(mxArray *plhs[])
{
  ((real_T *)mxGetPr((plhs[0])))[0] = (real_T)(2439276863U);
  ((real_T *)mxGetPr((plhs[0])))[1] = (real_T)(3354547589U);
  ((real_T *)mxGetPr((plhs[0])))[2] = (real_T)(2991496976U);
  ((real_T *)mxGetPr((plhs[0])))[3] = (real_T)(2801652625U);
}

mxArray *sf_c1_elevator2_get_autoinheritance_info(void)
{
  const char *autoinheritanceFields[] =
  {"checksum","inputs","parameters","outputs"};
  mxArray *mxAutoinheritanceInfo =
  mxCreateStructMatrix(1,1,4,autoinheritanceFields);
  {
    mxArray *mxChecksum = mxCreateDoubleMatrix(4,1,mxREAL);
    double *pr = mxGetPr(mxChecksum);
    pr[0] = (double)(154077026U);
    pr[1] = (double)(3237348466U);
    pr[2] = (double)(1651706553U);
    pr[3] = (double)(650357864U);
    mxSetField(mxAutoinheritanceInfo,0,"checksum",mxChecksum);
  }
  {
    mxSetField(mxAutoinheritanceInfo,0,"inputs",mxCreateDoubleMatrix(0,0,mxREAL));
  }
  {
    mxSetField(mxAutoinheritanceInfo,0,"parameters",mxCreateDoubleMatrix(0,0,mxREAL));
  }
  {
    const char *dataFields[] = {"size","type","complexity"};
    mxArray *mxData = mxCreateStructMatrix(1,1,3,dataFields);
    {
      mxArray *mxSize = mxCreateDoubleMatrix(1,2,mxREAL);
      double *pr = mxGetPr(mxSize);
      pr[0] = (double)(1);
      pr[1] = (double)(1);
      mxSetField(mxData,0,"size",mxSize);
    }
    {
      const char *typeFields[] = {"base","aliasId","fixpt"};
      mxArray *mxType = mxCreateStructMatrix(1,1,3,typeFields);
      mxSetField(mxType,0,"base",mxCreateDoubleScalar(10));
      mxSetField(mxType,0,"aliasId",mxCreateDoubleScalar(0));
      mxSetField(mxType,0,"fixpt",mxCreateDoubleMatrix(0,0,mxREAL));
      mxSetField(mxData,0,"type",mxType);
    }
    mxSetField(mxData,0,"complexity",mxCreateDoubleScalar(0));
    mxSetField(mxAutoinheritanceInfo,0,"outputs",mxData);
  }
  return(mxAutoinheritanceInfo);
}

static void chart_debug_initialization(SimStruct *S)
{
  if(ssIsFirstInitCond(S)) {
    /* do this only if simulation is starting */
    if(!sim_mode_is_rtw_gen(S)) {
      {
        unsigned int chartAlreadyPresent;
        chartAlreadyPresent = sf_debug_initialize_chart(_elevator2MachineNumber_,
         1,
         6,
         10,
         3,
         9,
         0,
         0,
         0,
         &(chartInstance.chartNumber),
         &(chartInstance.instanceNumber),
         ssGetPath(S),
         (void *)S);
        if(chartAlreadyPresent==0) {
          /* this is the first instance */
          sf_debug_set_chart_disable_implicit_casting(_elevator2MachineNumber_,chartInstance.chartNumber,1);
          sf_debug_set_chart_event_thresholds(_elevator2MachineNumber_,
           chartInstance.chartNumber,
           9,
           9,
           9);

          _SFD_SET_DATA_PROPS(0,2,0,1,SF_DOUBLE,0,NULL,0,0,0,0.0,1.0,0,"Move",0,NULL);
          _SFD_SET_DATA_PROPS(1,0,0,0,SF_DOUBLE,0,NULL,0,0,0,0.0,1.0,0,"data",0,NULL);
          _SFD_SET_DATA_PROPS(2,0,0,0,SF_DOUBLE,0,NULL,0,0,0,0.0,1.0,0,"Dir",0,NULL);
          _SFD_EVENT_SCOPE(2,1);
          _SFD_EVENT_SCOPE(0,1);
          _SFD_EVENT_SCOPE(1,1);
          _SFD_EVENT_SCOPE(5,2);
          _SFD_EVENT_SCOPE(3,1);
          _SFD_EVENT_SCOPE(7,1);
          _SFD_EVENT_SCOPE(6,1);
          _SFD_EVENT_SCOPE(4,2);
          _SFD_EVENT_SCOPE(8,1);
          _SFD_STATE_INFO(4,0,0);
          _SFD_STATE_INFO(0,0,0);
          _SFD_STATE_INFO(2,0,0);
          _SFD_STATE_INFO(5,0,0);
          _SFD_STATE_INFO(3,0,0);
          _SFD_STATE_INFO(1,0,0);
          _SFD_CH_SUBSTATE_COUNT(3);
          _SFD_CH_SUBSTATE_DECOMP(0);
          _SFD_CH_SUBSTATE_INDEX(0,4);
          _SFD_CH_SUBSTATE_INDEX(1,5);
          _SFD_CH_SUBSTATE_INDEX(2,3);
          _SFD_ST_SUBSTATE_COUNT(4,2);
          _SFD_ST_SUBSTATE_INDEX(4,0,0);
          _SFD_ST_SUBSTATE_INDEX(4,1,2);
          _SFD_ST_SUBSTATE_COUNT(0,0);
          _SFD_ST_SUBSTATE_COUNT(2,0);
          _SFD_ST_SUBSTATE_COUNT(5,0);
          _SFD_ST_SUBSTATE_COUNT(3,1);
          _SFD_ST_SUBSTATE_INDEX(3,0,1);
          _SFD_ST_SUBSTATE_COUNT(1,0);
        }
        _SFD_CV_INIT_CHART(3,1,0,0);
        {
          _SFD_CV_INIT_STATE(4,2,1,0,0,0,NULL,NULL);
        }
        {
          _SFD_CV_INIT_STATE(0,0,0,0,0,0,NULL,NULL);
        }
        {
          _SFD_CV_INIT_STATE(2,0,0,0,0,0,NULL,NULL);
        }
        {
          _SFD_CV_INIT_STATE(5,0,0,0,0,0,NULL,NULL);
        }
        {
          _SFD_CV_INIT_STATE(3,1,0,0,0,0,NULL,NULL);
        }
        {
          _SFD_CV_INIT_STATE(1,0,0,0,0,0,NULL,NULL);
        }

        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {10};
          static int sPostFixPredicateTree[] = {0};
          _SFD_CV_INIT_TRANS(2,1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),1,&(sPostFixPredicateTree[0]));
        }
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {11};
          static int sPostFixPredicateTree[] = {0};
          _SFD_CV_INIT_TRANS(7,1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),1,&(sPostFixPredicateTree[0]));
        }
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {6};
          static int sPostFixPredicateTree[] = {0};
          _SFD_CV_INIT_TRANS(5,1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),1,&(sPostFixPredicateTree[0]));
        }
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {11};
          static int sPostFixPredicateTree[] = {0};
          _SFD_CV_INIT_TRANS(3,1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),1,&(sPostFixPredicateTree[0]));
        }
        _SFD_CV_INIT_TRANS(6,0,NULL,NULL,0,NULL);

        _SFD_CV_INIT_TRANS(4,0,NULL,NULL,0,NULL);

        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {6};
          static int sPostFixPredicateTree[] = {0};
          _SFD_CV_INIT_TRANS(1,1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),1,&(sPostFixPredicateTree[0]));
        }
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {8};
          static int sPostFixPredicateTree[] = {0};
          _SFD_CV_INIT_TRANS(9,1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),1,&(sPostFixPredicateTree[0]));
        }
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {6};
          static int sPostFixPredicateTree[] = {0};
          _SFD_CV_INIT_TRANS(8,1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),1,&(sPostFixPredicateTree[0]));
        }
        _SFD_CV_INIT_TRANS(0,0,NULL,NULL,0,NULL);

        _SFD_TRANS_COV_WTS(2,0,1,0,2);
        if(chartAlreadyPresent==0)
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {10};
          _SFD_TRANS_COV_MAPS(2,
           0,NULL,NULL,
           1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),
           0,NULL,NULL,
           2,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(7,0,1,0,1);
        if(chartAlreadyPresent==0)
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {11};
          _SFD_TRANS_COV_MAPS(7,
           0,NULL,NULL,
           1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),
           0,NULL,NULL,
           1,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(5,0,1,0,1);
        if(chartAlreadyPresent==0)
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {6};
          _SFD_TRANS_COV_MAPS(5,
           0,NULL,NULL,
           1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),
           0,NULL,NULL,
           1,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(3,0,1,0,0);
        if(chartAlreadyPresent==0)
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {11};
          _SFD_TRANS_COV_MAPS(3,
           0,NULL,NULL,
           1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),
           0,NULL,NULL,
           0,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(6,0,0,0,0);
        if(chartAlreadyPresent==0)
        {
          _SFD_TRANS_COV_MAPS(6,
           0,NULL,NULL,
           0,NULL,NULL,
           0,NULL,NULL,
           0,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(4,0,0,0,0);
        if(chartAlreadyPresent==0)
        {
          _SFD_TRANS_COV_MAPS(4,
           0,NULL,NULL,
           0,NULL,NULL,
           0,NULL,NULL,
           0,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(1,0,1,0,0);
        if(chartAlreadyPresent==0)
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {6};
          _SFD_TRANS_COV_MAPS(1,
           0,NULL,NULL,
           1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),
           0,NULL,NULL,
           0,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(9,0,1,0,2);
        if(chartAlreadyPresent==0)
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {8};
          _SFD_TRANS_COV_MAPS(9,
           0,NULL,NULL,
           1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),
           0,NULL,NULL,
           2,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(8,0,1,0,0);
        if(chartAlreadyPresent==0)
        {
          static unsigned int sStartGuardMap[] = {0};
          static unsigned int sEndGuardMap[] = {6};
          _SFD_TRANS_COV_MAPS(8,
           0,NULL,NULL,
           1,&(sStartGuardMap[0]),&(sEndGuardMap[0]),
           0,NULL,NULL,
           0,NULL,NULL);
        }
        _SFD_TRANS_COV_WTS(0,0,0,0,0);
        if(chartAlreadyPresent==0)
        {
          _SFD_TRANS_COV_MAPS(0,
           0,NULL,NULL,
           0,NULL,NULL,
           0,NULL,NULL,
           0,NULL,NULL);
        }
        _SFD_SET_DATA_VALUE_PTR(0U, c1_Move());
        _SFD_SET_DATA_VALUE_PTR(1U, &chartInstance.c1_data);
        _SFD_SET_DATA_VALUE_PTR(2U, &chartInstance.c1_Dir);
      }
    }
  } else {
    sf_debug_reset_current_state_configuration(_elevator2MachineNumber_,chartInstance.chartNumber,chartInstance.instanceNumber);
  }
}

static void sf_opaque_initialize_c1_elevator2(void *chartInstanceVar)
{
  chart_debug_initialization(chartInstance.S);
  initialize_params_c1_elevator2();
  initialize_c1_elevator2();
}

static void sf_opaque_enable_c1_elevator2(void *chartInstanceVar)
{
  enable_c1_elevator2();
}

static void sf_opaque_disable_c1_elevator2(void *chartInstanceVar)
{
  disable_c1_elevator2();
}

static void sf_opaque_gateway_c1_elevator2(void *chartInstanceVar)
{
  sf_c1_elevator2();
}

static void sf_opaque_terminate_c1_elevator2(void *chartInstanceVar)
{
  finalize_c1_elevator2();
}

static void mdlProcessParameters_c1_elevator2(SimStruct *S)
{
  int i;
  for(i=0;i<ssGetNumRunTimeParams(S);i++) {
    if(ssGetSFcnParamTunable(S,i)) {
      ssUpdateDlgParamAsRunTimeParam(S,i);
    }
  }
  initialize_params_c1_elevator2();
}

static void mdlSetWorkWidths_c1_elevator2(SimStruct *S)
{
  if(sim_mode_is_rtw_gen(S)) {
    int_T chartIsInlinable =
      (int_T)sf_is_chart_inlinable("elevator2",1);
    ssSetStateflowIsInlinable(S,chartIsInlinable);
    ssSetEnableFcnIsTrivial(S,1);
    ssSetDisableFcnIsTrivial(S,1);
    ssSetNotMultipleInlinable(S,sf_rtw_info_uint_prop("elevator2",1,"gatewayCannotBeInlinedMultipleTimes"));
    if(chartIsInlinable) {
      sf_mark_chart_reusable_outputs(S,"elevator2",1,1);
    }
    ssSetInputPortOptimOpts(S, 0, SS_REUSABLE_AND_LOCAL);
    if (!sf_is_chart_instance_optimized_out("elevator2",1)) {
      int dtId;
      char *chartInstanceTypedefName =
        sf_chart_instance_typedef_name("elevator2",1);
      dtId = ssRegisterDataType(S, chartInstanceTypedefName);
      if (dtId == INVALID_DTYPE_ID ) return;
      /* Register the size of the udt */
      if (!ssSetDataTypeSize(S, dtId, 8)) return;
      if(!ssSetNumDWork(S,1)) return;
      ssSetDWorkDataType(S, 0, dtId);
      ssSetDWorkWidth(S, 0, 1);
      ssSetDWorkName(S, 0, "ChartInstance"); /*optional name, less than 16 chars*/
      sf_set_rtw_identifier(S);
    }
    ssSetHasSubFunctions(S,!(chartIsInlinable));
    ssSetOptions(S,ssGetOptions(S)|SS_OPTION_WORKS_WITH_CODE_REUSE);
  }

  ssSetChecksum0(S,(1069311621U));
  ssSetChecksum1(S,(1201943146U));
  ssSetChecksum2(S,(4193840226U));
  ssSetChecksum3(S,(1385185042U));

  ssSetExplicitFCSSCtrl(S,1);
}

static void mdlRTW_c1_elevator2(SimStruct *S)
{
  if(sim_mode_is_rtw_gen(S)) {
    sf_write_symbol_mapping(S, "elevator2", 1);
    ssWriteRTWStrParam(S, "StateflowChartType", "Stateflow");
  }
}

static void mdlStart_c1_elevator2(SimStruct *S)
{
  chartInstance.chartInfo.chartInstance = NULL;
  chartInstance.chartInfo.isEMLChart = 0;
  chartInstance.chartInfo.chartInitialized = 0;
  chartInstance.chartInfo.sFunctionGateway = sf_opaque_gateway_c1_elevator2;
  chartInstance.chartInfo.initializeChart = sf_opaque_initialize_c1_elevator2;
  chartInstance.chartInfo.terminateChart = sf_opaque_terminate_c1_elevator2;
  chartInstance.chartInfo.enableChart = sf_opaque_enable_c1_elevator2;
  chartInstance.chartInfo.disableChart = sf_opaque_disable_c1_elevator2;
  chartInstance.chartInfo.mdlRTW = mdlRTW_c1_elevator2;
  chartInstance.chartInfo.mdlStart = mdlStart_c1_elevator2;
  chartInstance.chartInfo.mdlSetWorkWidths = mdlSetWorkWidths_c1_elevator2;
  chartInstance.chartInfo.restoreLastMajorStepConfiguration = NULL;
  chartInstance.chartInfo.restoreBeforeLastMajorStepConfiguration = NULL;
  chartInstance.chartInfo.storeCurrentConfiguration = NULL;
  chartInstance.S = S;
  ssSetUserData(S,(void *)(&(chartInstance.chartInfo))); /* register the chart instance with simstruct */

  if(!sim_mode_is_rtw_gen(S)) {
    init_test_point_mapping_info(S);
    init_dsm_address_info();
  }
}

void c1_elevator2_method_dispatcher(SimStruct *S, int_T method, void *data)
{
  switch (method) {
   case SS_CALL_MDL_START:
    mdlStart_c1_elevator2(S);
    break;
   case SS_CALL_MDL_SET_WORK_WIDTHS:
    mdlSetWorkWidths_c1_elevator2(S);
    break;
   case SS_CALL_MDL_PROCESS_PARAMETERS:
    mdlProcessParameters_c1_elevator2(S);
    break;
   default:
    /* Unhandled method */
    sf_mex_error_message("Stateflow Internal Error:\n"
     "Error calling c1_elevator2_method_dispatcher.\n"
     "Can't handle method %d.\n", method);
    break;
  }
}

static const rtwCAPI_DataTypeMap dataTypeMap[] = {
  /* cName, mwName, numElements, elemMapIndex, dataSize, slDataId, isComplex, isPointer */
  {"real_T", "real_T", 0, 0, sizeof(real_T), SS_DOUBLE, 0, 0},
  {"uint8_T", "uint8_T", 0, 0, sizeof(uint8_T), SS_UINT8, 0, 0}
};

static const rtwCAPI_FixPtMap fixedPointMap[] = {
  /* *fracSlope, *bias, scaleType, wordLength, exponent, isSigned */
  {NULL, NULL, rtwCAPI_FIX_RESERVED, 64, 0, 0}
};

static const rtwCAPI_DimensionMap dimensionMap[] = {
  /* dataOrientation, dimArrayIndex, numDims*/
  {rtwCAPI_SCALAR, 0, 2}
};

static const uint_T dimensionArray[] = {
  1, 1
};

static real_T sfCAPIsampleTimeZero = 0.0;
static const rtwCAPI_SampleTimeMap sampleTimeMap[] = {
  /* *period, *offset, taskId, mode */
  {&sfCAPIsampleTimeZero, &sfCAPIsampleTimeZero, 0, 0}
};

static const rtwCAPI_Signals testPointSignals[] = {
  /* addrMapIndex, sysNum, SFRelativePath, dataName, portNumber, dataTypeIndex, dimIndex, fixPtIdx, sTimeIndex */
  {0, 0,"StateflowChart/data", "data", 0, 0, 0, 0, 0},
  {1, 0,"StateflowChart/Dir", "Dir", 0, 0, 0, 0, 0},
  {2, 0, "StateflowChart/Alarm", "Alarm", 0, 1, 0, 0, 0},
  {3, 0, "StateflowChart/Alarm.Off", "Off", 0, 1, 0, 0, 0},
  {4, 0, "StateflowChart/Alarm.On", "On", 0, 1, 0, 0, 0},
  {5, 0, "StateflowChart/Error", "Error", 0, 1, 0, 0, 0},
  {6, 0, "StateflowChart/Normal", "Normal", 0, 1, 0, 0, 0},
  {7, 0, "StateflowChart/Normal.Down", "Down", 0, 1, 0, 0, 0}
};

static rtwCAPI_ModelMappingStaticInfo testPointMappingStaticInfo = {
  /* block signal monitoring */
  {
    testPointSignals,                   /* Block signals Array  */
    8                                   /* Num Block IO signals */
  },

  /* parameter tuning */
  {
    NULL,                               /* Block parameters Array    */
    0,                                  /* Num block parameters      */
    NULL,                               /* Variable parameters Array */
    0                                   /* Num variable parameters   */
  },

  /* block states */
  {
    NULL,                               /* Block States array        */
    0                                   /* Num Block States          */
  },

  /* Static maps */
  {
    dataTypeMap,                        /* Data Type Map            */
    dimensionMap,                       /* Data Dimension Map       */
    fixedPointMap,                      /* Fixed Point Map          */
    NULL,                               /* Structure Element map    */
    sampleTimeMap,                      /* Sample Times Map         */
    dimensionArray                      /* Dimension Array          */
  },

  /* Target type */
  "float"
};

static void init_test_point_mapping_info(SimStruct *S) {
  rtwCAPI_ModelMappingInfo *testPointMappingInfo;
  void **testPointAddrMap;

  init_test_point_addr_map();
  testPointMappingInfo = get_test_point_mapping_info();
  testPointAddrMap = get_test_point_address_map();

  rtwCAPI_SetStaticMap(*testPointMappingInfo, &testPointMappingStaticInfo);
  rtwCAPI_SetLoggingStaticMap(*testPointMappingInfo, NULL);
  rtwCAPI_SetInstanceLoggingInfo(*testPointMappingInfo, NULL);
  rtwCAPI_SetPath(*testPointMappingInfo, "");
  rtwCAPI_SetFullPath(*testPointMappingInfo, NULL);
  rtwCAPI_SetDataAddressMap(*testPointMappingInfo, testPointAddrMap);
  rtwCAPI_SetChildMMIArray(*testPointMappingInfo, NULL);
  rtwCAPI_SetChildMMIArrayLen(*testPointMappingInfo, 0);

  ssSetModelMappingInfoPtr(S, testPointMappingInfo);
}

