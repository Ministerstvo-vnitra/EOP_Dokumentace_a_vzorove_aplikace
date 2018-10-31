//
//  IDGMainConfig.h
//  miniDriver
//
//  Created by Gemalto on 29/6/17.
//  Copyright © 2017 Gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>
@class IDGMainConfigBuilder;

@interface IDGMainConfig : NSObject

@property (nonatomic, readonly) NSData   * _Nullable appSignature;

-(_Nonnull instancetype)init NS_UNAVAILABLE;
-( IDGMainConfig* _Nullable ) build :(IDGMainConfigBuilder*_Nonnull) config;

@end

@interface IDGMainConfigBuilder :NSObject

@property (nonatomic) NSData   * _Nullable appSignature;

-(_Nullable instancetype )initWithSignature:(NSData*_Nullable)appSignature;
-(_Nonnull instancetype)init NS_UNAVAILABLE;
-(IDGMainConfig*_Nonnull)build;

@end

