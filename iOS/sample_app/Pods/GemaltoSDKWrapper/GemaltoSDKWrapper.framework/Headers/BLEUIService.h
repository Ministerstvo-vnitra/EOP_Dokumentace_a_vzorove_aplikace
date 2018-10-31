//
//  UIService.h
//
//  Created by gemalto on 27/05/15.
//  Copyright (c) 2015 Gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Reader.h"
#import <UIKit/UIKit.h>


/*!
 @class BLEUIService
 @brief Required to handle pairing and initial connection to the Bluetooth Low Energy Reader.
  Integrators can declare an object extend this class in order to handle the UI for those actions.
  The ID Go 800 implement a BLEUIService class and that will be used by default.
  All the function declared here will be called from the application's main thread to allow for easy UI operation.
 */

/*!
 @class BLEUIService
 @brief Default implementation of the BLEEvents used by the ID Go 800.
 */
@interface BLEUIService : NSObject


/*!
 @method BLE_ScanStart
 @brief Function called by the ID Go 800 when a scanning operation is starting.
 Such operation can last between 0.2 and a few seconds, depending mainly
 on the time needed for the user to start its reader.
 */
-(void)BLE_ScanStart;



/*!
 @method BLE_ScanDone
 @brief This function is called by IDGo 800 when a scan has completed, typically after the ReaderService BccidStopScanning function is called.
 */
-(void)BLE_ScanDone;



/*!
 @method BLE_ConnectStarted
 @brief This function is called by IDGo 800 when a connection operation has started, the ReaderService BccidConnect function has been called. Such an operation lasts for only a few seconds (2).
 */
-(void)BLE_ConnectStarted;


/*!
 @method BLE_ConnectDone
 @brief This function is called by IDGo 800 when a connection has been performed, after function BLE_ConnectStarted was called. Operation results can be retrieved by calling the ReaderService getLastError function. The IDGo 800 sends a message notifying when the PKI can be used.
 */
-(void)BLE_ConnectDone;


/*!
 @method BLE_PairingStarted
 @brief Function called by the ID Go 800 when a pairing operation is starting.
 Such operation usually last a few seconds (4).
 @param requireCodePopup Boolean flag indicating if the pairing process will need to show a code entry popup. This should only happen with the Ezio Mobile Reader and can be ignored the rest of the time.
 */
-(void)BLE_PairingStarted:(BOOL)requireCodePopup;

/*!
 @method BLE_PairingDone
 @brief This function is called by IDGo 800 when a pairing operation has completed. Operation results may be retrieved by calling the ReaderService getLastError function. The IDGo 800 sends a message notifying when the PKI can be used. The reader will also be connected at the same time. Operation result can be retrieved by calling the ReaderService getLastError function.

 */
-(void)BLE_PairingDone;


/*!
 @method BLE_DeviceDetected
 @brief Function called by the ID Go 800 when a reader is detected while scanning.
 The device object given as parameter is dependant on the scanning mode:
 * Pairing mode (SCAN_PAIRING): the device will be nil if the scan detected a device that is too far or not in pairing mode. Otherwise, it will contain the details of the detected reader.
 * onnecting mode (SCAN_KNOWN): the device will be nil if the device is unknown (i.e it has never been paired to the application).
 @param device The Reader object corresponding to the device detected. If the device detected isn't in the correct mode (Pairing for a SCAN_PAIRING, Advertising for SCAN_KNOWN) this object will be nil.
 */
-(void)BLE_DeviceDetected:(Reader*)device;



/*!
 @method BLE_SetMode
 @brief Set up the Pairing engine in the proper mode corresponding to the action in progress
 @param mode The scan mode (SCAN_KNOWN or SCAN_PAIRING) in progress.
 */
-(void)BLE_SetMode:(NSInteger)mode;



/*!
 @method BLE_PairingDoneWithDeviceId
 @brief Function called by the ID Go 800 when a pairing operation is complete.
 Operation result can be retrieved by calling the ReaderService getLastError function.
 The ID Go 800 should send a notification as documented in the integrator guide when the PKI can be used
 as the reader will also be connected at the same time.
 @param  The device id which is paired.
 */
-(void)BLE_PairingDoneWithDeviceId:(NSString*) deviceId;

/*!
 @method BLE_ConnectDoneWithDeviceId
 @brief Function called by the ID Go 800 when a connection has been done.
 Operation result can be retrieved by calling the ReaderService getLastError function.
 The ID Go 800 should send a notification as documented in the integrator guide when the PKI can be used.
 @param The device id which is connected.
 */
-(void)BLE_ConnectDoneWithDeviceId:(NSString*)deviceId;


@property (readonly,strong) UIAlertController* alert;

@end



