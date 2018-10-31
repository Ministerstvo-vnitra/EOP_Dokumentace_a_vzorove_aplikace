//
//  ReaderService.h
//
//  Created by gemalto on 26/05/15.
//  Copyright (c) 2015 Gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "GemErrors.h"
#import "BLEUIService.h"
#import "Reader.h"

/*!
 @enum SCAN_MODE
 @brief  Scanning mode available. SCAN_PAIRING will only detect devices close to the user iPhone or iPad and in pairing mode while SCAN_KNOWN will only detect devices already known.
 */
typedef NS_ENUM(NSInteger, SCAN_MODE) {
    SCAN_PAIRING,
    SCAN_KNOWN
};



/*!
 @class ReaderService
 @brief Main API for managing Bluetooth low energy readers.
 */
@interface ReaderService : NSObject


/*!
 @method sharedService
 @brief Return an instance of the ReaderService.
 @returns initialized ReaderService
 */
+ (id) sharedService;


/*!
 @method
 @brief Return an error code for the last operation done. Error value are defined in the GemErrors.h file. Should only be called from the pairing engine for consistency.
 @param
 @returns
 */
-(int)getLastError;

#pragma mark - BCCID TOOLS

/*!
 @method BccidInitialize
 @brief Function to call before any other functions of the ReaderService. This function will check the Bluetooth state of the user device.
 If the bluetooth isn't activated at that point, the system will prompt the user to activate the bluetooth.

 */
-(void)BccidInitialize;

//
/*!
 @method BccidFinalize
 @brief  To call when usage of the BLE reader is done.
 */
-(void)BccidFinalize;

//
/*!
 @method BccidStartScanning
 @brief Initialize a scanning operation. This has to be done at least once in pairing mode to register the user reader.
 Each session will need to call this once to connect to a known reader once the pairing has been done (Pairing will also connect).
 @param mode Filter the type of device that will be detected: SCAN_KNOWN for device already paired, SCAN_PAIRING for devices in pairing mode.
 @param silent Boolean flag asking for a flow with or without UI.
 @param duration The maximum duration for the scan. If no device is detected before, the scan will automatically stop. (in seconds)
 @returns
 */
-(void)BccidStartScanning:(SCAN_MODE)mode silent:(BOOL)silent duration:(NSInteger)duration;

//
/*!
 @method BccidStopScanning
 @brief Cancel a scan in progress.
 */
-(void)BccidStopScanning;

/*!
 @method BccidPair
 @brief Initialize a pairing with the given reader. Reader information can be retrieved by implementing the Ble_DeviceDetected function of the BLE_UI protocol.
 @param reader The reader to pair with.
 */
-(void)BccidPair:(Reader*)reader;

//
/*!
 @method BccidConnect
 @brief Initialize a connection to the given reader. Reader information can be retrieved by implementing the Ble_DeviceDetected function of the BLE_UI protocol.
 @param reader The reader to connect to.
 */
-(void)BccidConnect:(Reader*)reader;

/*!
@method BccidDisconnect
@brief Disconnect from the currently used reader.
*/
-(void)BccidDisconnect;

/*!
@method BccidDevicesList
@brief List all the devices known by the library
@returns An array containing all the devices known.
*/
-(NSArray*)BccidDevicesList;

/*!
@method BccidForgetDevice
@brief Remove the device from the library database. If the device is connected, unpair it completely.
@param reader The reader to remove*/
-(void)BccidForgetDevice:(Reader*)reader;

/*!
 @method BccidGetConnectedDevice
 @brief return the currently connected device if there is one.
 @returns Currently connected bluetooth reader.
 */
-(Reader*)BccidGetConnectedDevice;


/*!
 @method initBLEUIService
 @brief Sets up a custom extended BLEUIService to handle BLE Events for the ID Go 800.
 @param bleUIService The new BLEService extended object to handle BLE events for the ID Go 800.
 */
-(void) initBLEUIService:(id)bleUIService;

@end
