//
//  Reader.h
//
//  Created by gemalto on 10/06/15.
//  Copyright (c) 2015 Gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 @class Reader
 @brief Reader object
 */
@interface Reader : NSObject

/*!
 @property identifier
 @brief Unique identifier string for the device
 */
@property(readonly, strong, nonatomic) NSString *identifier;

/*!
 @property deviceName
 @brief Name of the device as returned by the device itself
 */
@property(readonly, strong, nonatomic) NSString *deviceName;

/*!
 @property deviceFriendlyName
 @brief Friendly name for the device. Can be overwritten using the function -(void) saveNewName:(NSString*)name;
 */
@property(readonly, strong, nonatomic) NSString *deviceFriendlyName;

/*!
 @property firmwareRevision
 @brief Firware revision of the device. Only available if the device is connected.
 */
@property(readonly, strong, nonatomic) NSString *firmwareRevision;

/*!
 @property softwareRevision
 @brief Software revision of the device. Only available if the device is connected.
 */
@property(readonly, strong, nonatomic) NSString *softwareRevision;

/*!
 @property hardwareRevision
 @brief Hardware revision of the device. Only available if the device is connected.
 */
@property(readonly, strong, nonatomic) NSString *hardwareRevision;

/*!
 @property hardwareRevision
 @brief Hardware revision of the device. Only available if the device is connected.
 */
@property(readonly, strong, nonatomic) NSString *serialNumber;

/*!
 @property manufacturer
 @brief The manufacturer string as returned by the device. Only available if the device is connected.
 */
@property(readonly, strong, nonatomic) NSString *manufacturer;

/*!
 @property lastSeen
 @brief The last time the device was connected and used by the application.
 */
@property(readonly, readonly, strong,nonatomic) NSDate *lastSeen;

/*!
 @property batteryLevel
 @brief Battery level of the device. Only available if the device is connected.
 */
@property(readonly, nonatomic) NSInteger batteryLevel;

/*!
 @property isSecurelyPaired
 @brief Boolean flag indicating if the application as already performed a secure pairing with this device and is able to connect with it.
 */
@property(readonly, nonatomic) BOOL isSecurelyPaired;

/*!
 @property isPairingEnabled
 @brief Boolean flag indicating if the device is currently in pairing mode.
 */
@property(readonly, nonatomic) BOOL isPairingEnabled;

/*!
 @property isClose
 @brief Boolean flag indicating if the device is close to the iPhone/iPad of the user (about 1 meter). Only available if the device is connected.
 */
@property(readonly, nonatomic) BOOL isClose;

/*!
 @property RSSI
 @brief RSSI of the device. (signal strength)
 */
@property(readonly, nonatomic) NSInteger RSSI;

/*!
 @method saveNewName
 @brief Update the friendly name of the device to the parameter given in the library database.
 @param name the new friendly name of the device
 */
-(void) saveNewName:(NSString*)name;

@end



