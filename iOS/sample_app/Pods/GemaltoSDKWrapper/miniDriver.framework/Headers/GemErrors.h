//
//  GemErrors.h
//  MiniDriver
//
//  Copyright (c) 2015 Gemalto. All rights reserved.
//

#define GE_OK                           0

#define GE_NO_SERVICE                   1
#define GE_BAD_ARGUMENT 				2
#define GE_NULL_ARGUMENT 				3
#define GE_INVALID_CONTAINER_INDEX      4
#define GE_OUT_OF_RANGE_ARGUMENT 		5
#define GE_DIRECTORY_NOT_FOUND          6
#define GE_FILE_NOT_FOUND 				7
#define GE_DIRECTORY_EXISTS 			8
#define GE_FILE_EXISTS                  9
#define GE_NOT_SUPPORTED 				10
#define GE_UNAUTHORIZED_ACCESS          11
#define GE_BUFFER_TOO_SMALL 			12

#define GE_UI_CANCELLED_BY_USER 		100
#define GE_UI_PIN_CONF_WRONG 			101
#define GE_UI_TIMEOUT 			        102
#define GE_NO_RESOURCE                  103

#define GE_TRANSACTION_LOCKED 		    200
#define GE_TRANSACTION_INVALID_ID 	    201

#define GE_WRONG_APP_SIGNATURE          300
#define GE_INVALID_SESSION_ID 	        301
#define GE_NO_SESSION_AVAILABLE         302

#define GE_NO_LICENSE                   400

#define GE_NO_SCAN                      500
#define GE_NO_DEVICE                    501
#define GE_ALREADY_CONNECTED            502
#define GE_SCAN_IN_PROGRESS             503

#define GE_BLUETOOTH_NOT_ENABLED        600

#define GE_UNKNOWN                      1000
