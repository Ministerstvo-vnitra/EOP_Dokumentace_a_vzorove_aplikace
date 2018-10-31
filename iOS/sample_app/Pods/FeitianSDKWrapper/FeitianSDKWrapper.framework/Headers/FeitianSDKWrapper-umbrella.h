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

#import "FtBleReader.h"
#import "ReaderInterface.h"
#import "winscard.h"
#import "wintypes.h"

FOUNDATION_EXPORT double FeitianSDKWrapperVersionNumber;
FOUNDATION_EXPORT const unsigned char FeitianSDKWrapperVersionString[];

