//
//  UIViewController+Utils.h
//  canvas_BLE
//
//  Created by gemalto on 01/06/15.
//  Copyright (c) 2015 Gemalto. All rights reserved.
//

#ifndef canvas_BLE_UIViewController_Utils_h
#define canvas_BLE_UIViewController_Utils_h

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface UtilsUI : NSObject

+(UIViewController*)currentViewController;

+(void)showPopover:(UIViewController*)parent content:(UIViewController*)content;
+(void)dismissPopover:(UIViewController*)parent content:(UIViewController*)content;

@end


#endif
