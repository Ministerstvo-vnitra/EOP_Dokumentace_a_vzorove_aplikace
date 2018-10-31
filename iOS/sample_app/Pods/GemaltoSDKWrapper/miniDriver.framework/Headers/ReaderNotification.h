//
//  ReaderNotification.h
//  sdkIOS
//
//  Created by Riad Baatouche on 04/09/13.
//  Copyright (c) 2013 Gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>

#ifndef _READER_NOTIFICATION_H_
#define _READER_NOTIFICATION_H_

// List of possible notifications
#define NO_READER                   1
#define NO_CARD                     2
#define CARD_READY                  3
#define PAIRING_START               5
#define PAIRING_STOP                6
#define PAIRING_DONE                7
#define PAIRING_DONE_WITH_DEVICE_ID 8

/*!
 @class ReaderNotification
 @brief IDGo 800 Event management API.
 */
@interface ReaderNotification : NSObject

/*!
 @method startObserver
 @brief Register an object as a listener
 @param observer The object listening
 @param aSelector The selector to perform in case of an event.
 */
+(void) startObserver:(id)observer selector:(SEL)aSelector;

/*!
 @method stopObserver
 @brief Remove an object from the list of listener.
 @param observer The object to remove
 */
+ (void) stopObserver:(id)observer;

/*!
 @method sendNotificationStatus
 @brief Send a notification to all the listeners.
 @param status
 */
+ (void) sendNotificationStatus:(int)status;

@end

#endif
