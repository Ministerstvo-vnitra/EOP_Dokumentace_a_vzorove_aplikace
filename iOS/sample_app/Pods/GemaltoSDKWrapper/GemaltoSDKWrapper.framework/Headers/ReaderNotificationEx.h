//
//  ReaderNotificationEx.h
//  miniDriver
//
//  Created by Arun Kumar Nama on 7/6/17.
//  Copyright © 2017 Gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ReaderNotification.h"

/*!
 @class ReaderNotificationEx  - This is extended version for ReaderNotification for sending notifications in case of device pairing and connection states with the device id in the NSNotificationCenter userInfo Object. Added this extended version to support the previous version of ReaderNofitification client implementations.
 @brief IDGo 800 Event management API.
 */
@interface ReaderNotificationEx : ReaderNotification
/*!
 @method sendNotificationStatus:WithDeviceID - Send the notification with the device information with nofitication name READER_STATUS_NOTIFY
 @brief Send a notification to all the listeners.
 @param status and deviceId
 */
+ (void) sendNotificationStatus:(int)status withDeviceID:(NSString*)deviceId;

@end

