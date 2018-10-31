//
// UIServiceManaged.h
//
//  Created by gemalto on 20/07/15.
//  Copyright (c) 2015 Gemalto. All rights reserved.
//

#import "BLEUIService.h"


/*!
 @enum UI_VIEW
 @brief The available view to display using the UIService
 */
typedef NS_ENUM(NSInteger, UI_VIEW) {
    UI_VIEW_MAIN = 0,           // Main management view (Reader + Pairing + Configuration)
    UI_VIEW_CONFIG = 1,         // Configuration view
    UI_VIEW_READERS = 2         // Reader management view
};



/*!
 @class UIService(managed)
 @brief Extension of the UIService class. Provides several UI for Bluetooth readers usage.
 */
@interface UIServiceManaged : BLEUIService
/*!
 @method UI_ShowView
 @brief Display the required UI. The UI will be pushed on the navigation controller stack. This mean that the parent needs to have a navigation controller.
 @param viewType The view required, see UI_VIEW enum.
 @param parent the calling view controller.
 */
+(int)UI_ShowView:(NSInteger)viewType parentView:(UIViewController*)parent;

@end
