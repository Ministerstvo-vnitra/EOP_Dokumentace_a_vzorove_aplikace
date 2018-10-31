//
//  MDConfig.h
//  user
//
//  Created by gemalto on 23/02/15.
//  Copyright (c) 2015 gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 @class MDConfig
 @brief Getters and setters for library configuration
 */
@interface MDConfig : NSObject

/*!
 @method getSupportedLanguages
 @brief Return a list of supported language codes.
 @returns An array containing code for each supported languages
 */
+(NSArray*)getSupportedLanguages;

/*!
 @method setLanguage
 @brief Set up the language to use if it is contained in the supported language list.
 @returns
 */
+(void) setLanguage:(NSString*)language;
/*!
 @method getLanguage
 @brief Getter for the language currently in use by the Library.
 @returns A language code.
 */
+(NSString*)getLanguage;

/*!
 @method setCardService
 @brief Set the PKI Type to use by the library.
 @param cardService The new PKI type to use.
 */
+(void)setCardService:(int)cardService;
/*!
 @method getCardService
 @brief Get the PKI type in use by the library
 @returns
 */
+(int) getCardService;

/*!
 @method setPersistentCacheEnabled
 @brief Enable the permanent caching of public information feature.
 @param enable Boolean flag corresponding to the new state.
 */
+(void)setPersistentCacheEnabled:(BOOL)enable;
/*!
 @method isPersistentCacheEnabled
 @brief Boolean flag indicating if the permanent caching of public information is enabled
 @returns Permanent cache state.
 */
+(BOOL)isPersistentCacheEnabled;




@end
