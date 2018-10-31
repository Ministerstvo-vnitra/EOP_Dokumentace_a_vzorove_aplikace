#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "APDUService.h"
#import "BLEUIService.h"
#import "CardModuleServiceConstant.h"
#import "GemErrors.h"
#import "IDGMainConfig.h"
#import "MDConfig.h"
#import "MinidriverService.h"
#import "Reader.h"
#import "ReaderNotification.h"
#import "ReaderNotificationEx.h"
#import "ReaderService.h"
#import "UIServiceManaged.h"
#import "UtilsUI.h"

FOUNDATION_EXPORT double GemaltoSDKWrapperVersionNumber;
FOUNDATION_EXPORT const unsigned char GemaltoSDKWrapperVersionString[];

