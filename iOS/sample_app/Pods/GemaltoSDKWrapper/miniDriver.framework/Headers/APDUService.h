//
//  IAPDU.h
//  MiniDriver
//
//  Created by Riad Baatouche on 29/08/13.
//  Copyright (c) 2013 Gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "GemErrors.h"
#import "IDGMainConfig.h"

@interface APDUService : NSObject

+ (id) sharedService;

// Service management

/**
 @brief Initializes the APDU Service and opens a session with the service.

 @param appName The package name of third party app (String type).
 @param appSignature The signature of the third party app to authenticate with IDGo 800 (Byte array type).
 The appSignature is delivered by Gemalto to each customer who signs an agreement to use the IDGo 800 middleware for iOS. It is a unique signature associated with the package name of the third party app.
 @return The session ID of the opened session.
 */

- (int) APDU_Init:(NSString*) appName appSignature:(NSMutableData*)appSignature __deprecated_msg("Use APDU_InitWithonfig: instead.");


/**
 @brief Initializes the APDU Service and opens a session with the service.
 @param config The IDGMainConfig object with signature (Byte array type) of the third party app to authenticate with IDGo 800
 The appSignature is delivered by Gemalto to each customer who signs an agreement to use the IDGo 800 middleware for iOS. It is a unique signature associated with the package name of the third party app.
 @return The session ID of the opened session.
 */

- (int) APDU_InitWithConfig:(IDGMainConfig*)config;





/**
 @brief Finalizes a session with the APDU Service previously established with “APDU_Init” method.
 @param sessionId The session ID previously returned by APDU_Init.
 */
- (void) APDU_Finalize:(int) sessionId;

// Error management

/**
 @brief Returns the execution status code of the last executed method.

 @return The following execution status codes can be returned:
 GE_OK = 0 GE_NO_SERVICE = 1, GE_BAD_ARGUMENT = 2, GE_NULL_ARGUMENT = 3 ,GE_NOT_SUPPORTED = 10, GE_UNAUTHORIZED_ACCESS = 11, GE_UI_CANCELLED_BY_USER = 100, GE_UI_PIN_CONF_WRONG = 101, GE_UI_TIMEOUT = 102, GE_TRANSACTION_LOCKED = 200, GE_TRANSACTION_INVALID_ID = 201, GE_WRONG_APP_SIGNATURE = 300, GE_INVALID_SESSION_ID = 301, GE_NO_SESSION_AVAILABLE = 302, GE_NO_LICENSE = 400, GE_UNKNOWN = 1000
 */
- (int) APDU_GetLastError;

// APDU Transaction management

/**
 @brief Deprecated. This function does not do anything.

 @param sessionId The session ID previously returned by APDU_Init.
 */
- (BOOL) APDU_BeginTransaction:(int) sessionId;

/**
 @brief Deprecated. This function does not do anything.
 @param sessionId The session ID previously returned by APDU_Init.
 */
- (BOOL) APDU_EndTransaction:(int) sessionId;

// APDU service methods

/**
 @brief Lists the smart card readers available on the device.
 Note: As IDGO 800 iOS middleware currently only support one reader at a time, this function will always return the same value and is reserved for future use.
 @param sessionId The session ID previously returned by APDU_Init.
 @return An integer array that contains the available readers found on the device.
 */
- (NSMutableArray*) APDU_ListReaders:(int) sessionId;

/**
 @brief Checks if a smart card is present in the indicated reader.

 @param sessionId The session ID previously returned by APDU_Init.
 @param readerId A reader type from the list returned by APDU_ListReaders
 @return true if a smart card is present in the indicated reader, else false.
 */
- (BOOL) APDU_IsCardPresent:(int) sessionId readerId:(int) readerId;

/**
 @brief Connects to the smart card inserted in the indicated reader.

 @param sessionId The session ID previously returned by APDU_Init.
 @param readerId A reader type from the list returned by APDU_ListReaders
 @return true if it succeeds in connecting to the smart card inserted in the indicated reader, else false.
 */
- (BOOL) APDU_Connect:(int) sessionId readerId:(int) readerId;

/**
 @brief Disconnects from the smart card connected with the selected session.

 @param sessionId The session ID previously returned by APDU_Init.
 @return true if the smart card is correctly disconnected, else false.
 */
- (BOOL) APDU_Disconnect:(int) sessionId;

/**
 @brief Indicates if the selected session is already connected to a smart card.

 @param sessionId The session ID previously returned by APDU_Init.
 @return true if the session is connected to a smart card, else false.
 */
- (BOOL) APDU_IsConnected:(int) sessionId;

/**
 @brief Transmits an APDU command to the smart card connected to the indicated session.
 @param sessionId The session ID previously returned by APDU_Init.
 @param apduCommand The APDU command to transmit to the smart card.
 @param timeout The timeout in milliseconds for command execution.
 @return The smart card response to the APDU command. The response includes the SW two- byte status code.
 */
- (NSData*) APDU_Transmit:(int) sessionId apduCommand:(NSMutableData*)apduCommand timeout:(int) timeout;

/**
 @brief Transmits a Raw APDU command (without post processing) to the smart card connected to the indicated session.
 @param sessionId The session ID previously returned by APDU_Init.
 @param apduCommand The APDU command to transmit to the smart card.
 @param timeout The timeout in milliseconds for command execution.
 @return The smart card response to the APDU command. The response includes the SW two- byte status code.
 */
- (NSData*) APDU_TransmitRaw:(int) sessionId apduCommand:(NSMutableData*)apduCommand timeout:(int) timeout;
- (NSMutableData*) APDU_GenericCommand:(int)sessionId cmdId:(int)cmdId data:(NSMutableData*)data;

/**
 @brief Get the ATR of the card currently connected.
 @param sessionId The session ID previously returned by APDU_Init.
 @return The smart card ATR.
 */
- (NSData*)APDU_GetATR:(int)sessionId;

@end
