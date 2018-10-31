//
//  MinidriverService.h
//  miniDriver
//
//  Created by Riad Baatouche on 18/09/13.
//  Copyright (c) 2013 Gemalto. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "GemErrors.h"
#import "IDGMainConfig.h"
@interface MinidriverService : NSObject

+ (id) sharedService;
+ (void) resetSharedService;

// TO REMOVE
- (int) MD_CheckFileName:(NSString *)path;

// Service Management

/**
 
 @brief Initializes the PKI Service with the desired card type and opens a session with the service.
 @param cardType The desired card type service.
 The following values are supported:
  -1: Default PKI Service selected in IDGo 800 app.
  1: IDPrime MD PKI Service.
  2: IDPrime .NET PKI Service.
  3: IDPrime PIV PKI Service.
 
 @param appName The App name of third party app (String type).
 @param appSignature The signature of the third party app to authenticate with IDGo 800 (Byte array type).
 The appSignature is delivered by Gemalto to each customer who signs an agreement to use the IDGo 800 middleware for iOS. It is a unique signature associated with the package name of the third party app and contains information about the license such as its validity period and available features.

 @return SessionID The session ID of the opened session.
 */
- (int) MD_Init:(int)cardType appName:(NSString*)appName appSignature:(NSMutableData*)appSignature __deprecated_msg("Use MD_Init:cardType:config:");

/**
 
 @brief Initializes the PKI Service with the desired card type and opens a session with the service.
 @param cardType The desired card type service.
 The following values are supported:
 -1: Default PKI Service selected in IDGo 800 app.
 1: IDPrime MD PKI Service.
 2: IDPrime .NET PKI Service.
 3: IDPrime PIV PKI Service.
 

 @param config The IDGMainConfig object with Signature (Byte array type) of the third party app to authenticate with IDGo 800.
 The appSignature is delivered by Gemalto to each customer who signs an agreement to use the IDGo 800 middleware for iOS. It is a unique signature associated with the package name of the third party app and contains information about the license such as its validity period and available features.
 
 @return SessionID The session ID of the opened session.
 */

- (int) MD_Init:(int)cardType config:(IDGMainConfig*)config;

/**
 
 @brief Finalizes a session with the PKI Service previously established with “MD_Init” method.
 @param sessionId The session ID previously returned by MD_Init.
 */
- (void) MD_Finalize:(int)sessionId;

/**
 
 @brief Returns the currently selected card type service.
 @param sessionId The session ID previously returned by MD_Init.
 @return The following card type services can be returned:
 ■ 0: Software PKI Service.
 ■ 1: IDPrime MD PKI Service.
 ■ 2: IDPrime .NET PKI Service
 ■ 3: IDPrime PIV PKI Service.
 */
- (int) MD_GetCardType:(int)sessionId;
// Error management

/**
 
 @brief Returns the execution status code of the last executed method. These can be found in the GemErrors.h file.
 @return execution status code of the last executed method
 */
- (int) MD_GetLastError;

// MD Transaction management

/**
 
 @brief Deprecated. This function does not do anything.
 @param sessionId The session ID previously returned by MD_Init.
 */
- (BOOL) MD_BeginTransaction:(int)sessionId;

/**
 
 @brief Deprecated. This function does not do anything.
 @param sessionId The session ID previously returned by MD_Init.
 */
- (BOOL) MD_EndTransaction:(int)sessionId;

// Authentication methods

/**
 
 @brief Indicates if the Admin role has already been personalized. By default the Admin role is always personalized.
 @param sessionId The session ID previously returned by MD_Init.
 @return true if the Admin role has been personalized, else false.
 */
- (BOOL) MD_IsAdminPersonalized:(int)sessionId;

/**
 
 @brief Indicates if the User role has already been personalized. By default the User role is always personalized.
 @param sessionId The session ID previously returned by MD_Init.
 @return true if the User role has been personalized, else false.
 */
- (BOOL) MD_IsUserPersonalized:(int)sessionId;

/**
 
 @brief Generates a challenge for Admin authentication or PIN unblock.
 @param sessionId The session ID previously returned by MD_Init.
 @return The generated challenge on 8 bytes.
 */
- (NSMutableData*) MD_GetChallenge:(int)sessionId;

/**
 
 @brief Authenticates the Admin role with a response to a challenge previously returned by MD_GetChallenge command.

 @param sessionId The session ID previously returned by MD_Init.
 @param response The response to the challenge = 3DES_CBC(challenge, Admin Key)
 Note: If the response parameter is nil, the PKI Service automatically generates a challenge and displays a pop-up to enter the response.
 */
- (void) MD_ExternalAuthenticate:(int)sessionId response:(NSMutableData*)response;

/**
 
 @brief Authenticates the User PIN role or an extended PIN role (role #3 to #7).

 @param sessionId The session ID previously returned by MD_Init.
 @param role The PIN identifier to authenticate.
 @param pin Value of the PIN to authenticate.
 Note: If the pin parameter is nil, the PKI Service automatically displays a pop-up to enter the PIN value.
 */
- (void) MD_AuthenticatePin:(int)sessionId role:(Byte)role pin:(NSMutableData*)pin;

/**
 
 @brief Deauthenticates any role.

 @param sessionId The session ID previously returned by MD_Init.
 @param role The role identifier to deauthenticate.
 */
- (void) MD_Deauthenticate:(int)sessionId role:(Byte)role;

/**
 
 @brief Changes or unblocks the value of a PIN or Admin role. The Admin role cannot be unblocked (changed only).
 Note: If the oldPin parameter or newPin parameter is nil, the PKI Service automatically displays a pop-up to enter the old PIN or New PIN value.
 @param sessionId The session ID previously returned by MD_Init.
 @param mode 0x00 to change the role or PIN, 0x01h to unblock a PIN.
 @param role The role or PIN identifier to authenticate.
 @param oldPin To change PIN – old PIN value; to unblock PIN – challenge/response value.
 @param newPin New PIN or Admin Role value
 @param maxTries The new max tries value of the PIN, -1 if no change.
 */
- (void) MD_ChangeReferenceData:(int)sessionId mode:(Byte)mode role:(Byte)role oldPin:(NSMutableData*)oldPin newPin:(NSMutableData*)newPin maxTries:(int)maxTries;

/**
 
 @brief Returns the number of remaining tries for the requested role or PIN.
 @param sessionId The session ID previously returned by MD_Init.
 @param role The requested role identifier.
 @return The number of remaining tries of the requested role identifier.
 */
- (int) MD_GetTriesRemaining:(int)sessionId role:(Byte)role;

/**
 
 @brief Indicate if the requested role is authenticated or not.
 @param sessionId The session ID previously returned by MD_Init.
 @param role The requested role identifier.
 @return true if the requested role has been authenticated, else false.
 */
- (BOOL) MD_IsAuthenticated:(int)sessionId role:(Byte)role;

// Container management methods

/**
 
 @brief Import or generate (OBKG) a key-pair into a key container of the PKI Service.
 @param sessionId The session ID previously returned by MD_Init.
 @param ctrIndex 0x00...0x0F container index. If 0xFF, the PKI Service chooses the first
 available container.
 @param keyImport false for OBKG, true for key import.
 @param keySpec keySpec: 
 – 0x01 = RSA Exchange
 – 0x02 = RSA Signature
 – 0x03 = ECDSA 256
 – 0x04 = ECDSA 384
 – 0x05 = ECDSA 521
 – 0x06 = ECDH 256
 – 0x07 = ECDH 384
 – 0x08 = ECDH 521
 @param keySize The key size in bits.
 @param keyValue The key blob that contains the key to import, nil for OBKG.
 @discussion The keyValue parameter must be formatted as follows for a RSA key-pair:
 keyValue =  Prime P || Prime Q || Inverse Q || DP || DQ || Private Exponent D || Modulus || Public Exponent E
 Where:
 Prime P length = Key_Size_Bytes / 2 bytes
 Prime Q length = Key_Size_Bytes / 2 bytes
 Inverse Q length = Key_Size_Bytes / 2 bytes
 DP length = Key_Size_Bytes / 2 bytes
 DQ length = Key_Size_Bytes / 2 bytes
 Private Exponent D length = Key_Size_Bytes bytes
 Modulus length = Key_Size_Bytes bytes
 Public Exponent E length = = 4 bytes
 
 The keyValue parameter must be formatted as follows for an ECC key-pair: keyValue = Key Type || Key Size (in bytes) || X || Y || D
 Where:
 Key Type length = 4 bytes
 Key Size length = 4 bytes
 X length = Key Size bytes
 Y length = Key Size bytes
 D length = Key Size bytes
 The supported ECC Key Types are as follows:
 ECDSA 256: 0x32534345
 ECDSA 384: 0x34534345
 ECDSA 521: 0x36534345
 ECDH 256: 0x324B4345
 ECDH 384: 0x344B4345
 ECDH 521: 0x364B4345
 
 @return The index of used container if method was called with ctrIndex = 0xFF.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if
 it is not already authenticated.
 Only the IDPrime MD PKI Service supports ECC key-pairs. The IDPrime .NET, IDPrime PIV and Software Services support RSA key-pairs only.
 The IDPrime PIV Card PKI Service is read-only and so does not support this function at all. In this case MD_GetLastError returns GE_NOT_SUPPORTED.
 */
- (Byte) MD_CreateContainer:(int)sessionId ctrIndex:(Byte)ctrIndex keyImport:(BOOL)keyImport keySpec:(Byte)keySpec keySize:(int)keySize keyValue:(NSMutableData*)keyValue;

/**
 @brief Deletes a key-pair from a key container of the PKI Service.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 The IDPrime PIV Card PKI Service is read-only and so does not support this function at all. In this case MD_GetLastError returns GE_NOT_SUPPORTED.
 
 @param sessionId The session ID previously returned by MD_Init.
 @param ctrIndex 0x00...0x0F container index.
 */
- (void) MD_DeleteContainer:(int)sessionId ctrIndex:(Byte)ctrIndex;

/**
 @brief Returns the public key(s) from a key container of the PKI Service.

 @param sessionId The session ID previously returned by MD_Init.
 @param ctrIndex 0x00...0x0F container index.
 Note: IDPrime PIV cards have 4 containers only, so the range of values is 0x00...0X03.
 @return A byte array blob that contains the signature and/or exchange public key(s) stored in the requested container.
 
 The returned key blob is formatted as follows: Blob = [Signature Pub Key] || [Exchange Pub Key]
 Signature Pub Key and Exchange Pub Key are optional depending on which key exists in the container and are sequences of TLV formatted as follows:
 T_Key_Type = 0x03 L_Key_Type = 0x01
 V_Key_Type = 0x01 for RSA Exchange, 0x02 for RSA Signature, 0x03 for ECDSA 256, 0x04 for ECDSA 384, 0x05 for ECDSA 521, 0x06 for ECDH 256, 0x07 for ECDH 384 and 0x08 for ECDH 521.
 T_Key_RSA_Pub_Exp = 0x01
 L_Key_RSA_Pub_Exp = 0x04
 V_Key_RSAPub_Exp = Value of Public key Exponent on 4 bytes.
 T_Key_RSA_Modulus = 0x02
 L_Key_RSA_Modulus = Key Size (in bytes) >> 4 (1 byte!) V_Key_RSA_Modulus = Value of Public key Modulus on Key Size bytes.
 T_Key_ECC_X = 0x04 L_Key_ECC_X = Key Size (in bytes) V_Key_ECC_X = Value of X.
 T_Key_ECC_Y = 0x05 L_Key_ECC_Y = Key Size (in bytes) V_Key_ECC_Y = Value of Y.
 The 4-bit shift on L_Key_RSA_Modulus is to be able to pass the Modulus length on 1 byte for values 64 to 256 (512 bits to 2,048 bits).

 */
- (NSMutableData*) MD_GetContainer:(int)sessionId ctrIndex:(Byte)ctrIndex;

// Crypto methods

/**
 @brief Decrypts data with a key container of the PKI Service.
 @param sessionId The session ID previously returned by MD_Init.
 @param ctrIndex 0x00...0x0F container index.
 @param keyType  0x01 = RSA Exchange, 0x02 = RSA Signature.
 @param paddingType 0x01 = None, 0x02 = PKCS#1, 0x08 = OAEP
 The available Padding types depends on the type of card, as follows: IDPrime MD: PKCS#1 and OAEP, IDPrime .NET: None and PKCS#1, IDPrime PIV: None and PKCS#1
 @param algo 0x04 = SHA-1, 0x05 = SHA-256, 0x06 = SHA-384, 0x07 = SHA-512(Only used for OAEP padding type)
 @param encryptedData The data to decrypt, length must be equal to key length
 @return The decrypted data (padding removed). 
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 */
- (NSMutableData*) MD_RsaDecrypt:(int)sessionId ctrIndex:(Byte)ctrIndex keyType:(Byte)keyType paddingType:(Byte)paddingType algo:(Byte)algo encryptedData:(NSMutableData*)encryptedData;

/**
 @brief Signs data with a key container of the PKI Service.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 Only the IDPrime MD PKI Service supports ECC key-pairs. The IDPrime .NET, IDPrime PIV and Software Services support RSA key-pairs only.
 @param sessionId The session ID previously returned by MD_Init.
 @param ctrIndex 0x00...0x0F container index.
 @param keyType keyType: 0x01 = RSA Exchange, 0x02 = RSA Signature, 0x03 = ECDSA 256, 0x04 = ECDSA 384, 0x05 = ECDSA 521, 0x06 = ECDH 256, 0x07 = ECDH 384, 0x08 = ECDH 521.
 @param paddingType padding type: 0x02 = PKCS#1, 0x04 = PSS
 The available Padding types depends on the type of card, as follows: IDPrime MD: PKCS#1 and PSS, IDPrime .NET: PKCS#1 only, IDPrime PIV: PKCS#1 only
 @param algo Algo: 0x01 = MD2, 0x02 = MD4, 0x03 = MD5, 0x04 = SHA-1, 0x05 = SHA-256, 0x06 = SHA-384, 0x07 = SHA-512.
 @param dataToSign The data to sign, length according to hash algorithm used.
 @return The data signature.
 */
- (NSMutableData*) MD_SignData:(int)sessionId ctrIndex:(Byte)ctrIndex keyType:(Byte)keyType paddingType:(Byte)paddingType algo:(Byte)algo dataToSign:(NSMutableData*)dataToSign;

/**
 @brief Constructs a DH agreement (for key derivation) with a key container of the PKI Service.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 Only the IDPrime MD PKI Service supports this command.
 @param sessionId The session ID previously returned by MD_Init.
 @param ctrIndex 0x00...0x0F container index.
 @param dataQx The Qx parameter
 @param dataQy The Qy parameter
 @return The generated ECC DH agreement.
 */
- (NSMutableData*) MD_ConstructDHAgreement:(int)sessionId ctrIndex:(Byte)ctrIndex dataQx:(NSMutableData*)dataQx dataQy:(NSMutableData*)dataQy;

// Information methods

/**
 @brief Queries the card capabilities from the PKI Service.

 @param sessionId The session ID previously returned by MD_Init.
 @return A 2-byte array formatted as follows:
 ■ byte[0]: Cert compression
 – 0x00: No
 – 0x01: Yes
 ■ byte[1]: OBKG supported.
 – 0x00: No
 – 0x01: Yes
 */
- (NSMutableData*) MD_QueryCapabilities:(int)sessionId;

/**
 @brief Queries the card free space from the PKI Service.

 @param sessionId The session ID previously returned by MD_Init.
 @return A 3-integer array formatted as follows:
 ■ int[0]: Number of free containers
 ■ int[1]: Maximum number of containers.
 ■ int[2]: Free bytes memory
 */
- (NSMutableArray*) MD_QueryFreeSpace:(int)sessionId;

/**
 @brief Asks the card for the supported RSA key sizes from the PKI Service.
 @param sessionId The session ID previously returned by MD_Init.
 @return A 4-integer array formatted as follows:
 ■ int[0]: Minimum key size (bits).
 ■ int[1]: Maximum key size (bits).
 ■ int[2]: Key size increment (bits).
 ■ int[3]: Default key size.
 */
- (NSMutableArray*) MD_QueryKeySizes:(int)sessionId;

/**
 @brief This extended version of the command asks the card for the supported key sizes for the specified key type from the PKI Service.
 @param sessionId The session ID previously returned by MD_Init.
 @param keySpec keySpec: Specified key type
 – 0x01 = RSA Exchange
 – 0x02 = RSA Signature.
 – 0x03 = ECDSA 256
 – 0x04 = ECDSA 384
 – 0x05 = ECDSA 521
 – 0x06 = ECDH 256
 – 0x07 = ECDH 384
 – 0x08 = ECDH 521.
 @return A 4-integer array formatted as follows:
 ■ int[0]: Minimum key size (bits).
 ■ int[1]: Maximum key size (bits).
 ■ int[2]: Key size increment (bits).
 ■ int[3]: Default key size.
 */
- (NSMutableArray*) MD_QueryKeySizesEx:(int)sessionId keySpec:(Byte)keySpec;

/**
 @brief Gets the card serial number from the PKI Service.
 @param sessionId The session ID previously returned by MD_Init.
 @return 16-byte array that contains the card serial number (same as cardid).
 */
- (NSMutableData*) MD_GetCardSerialNumber:(int)sessionId;

/**
 @brief Gets the card application version from the PKI Service.
 @param sessionId The session ID previously returned by MD_Init.
 @return A string that indicates the card application version.
 */
- (NSString*) MD_GetVersion:(int)sessionId;

/**
 @brief Indicate if the PKI Service supports ECC.

 @param sessionId The session ID previously returned by MD_Init.
 @return true if the PKI Service supports ECC, false if the PKI Service does not support ECC.
 */
- (BOOL) MD_IsECC:(int)sessionId;

// File system management methods

/**
 @brief Creates a directory in the PKI Service file system.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 Only the IDPrime MD and IDPrime .NET PKI Services support this command. It is not supported by the IDPrime PIV Card PKI Service.
 @param sessionId The session ID previously returned by MD_Init.
 @param path The name of the directory to create. Maximum length is 8 characters.
 @param acls Access conditions of the directory
 The acls parameter is a 3- byte array formatted as follows:
 ■ acls[0]: Admin AC.
 ■ acls[1]: User AC.
 ■ acls[2]: Everyone AC.
 Each AC is a bit mask of the following rights:
 ■ Right_Write = 0x02 // Apply to CreateFile and DeleteFile in this directory.
 ■ Right_Read = 0x04 // Apply to EnumFiles in this directory.
 */
- (void) MD_CreateDirectory:(int)sessionId path:(NSString*)path acls:(NSMutableData*)acls;

/**
 @brief Deletes a directory from the PKI Service file system.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 Only the IDPrime MD and IDPrime .NET PKI Services support this command. It is not supported by the IDPrime PIV Card PKI Service.
 @param sessionId The session ID previously returned by MD_Init.
 @param path The name of the directory to delete. Maximum length is 8 characters.
 */
- (void) MD_DeleteDirectory:(int)sessionId path:(NSString*)path;

/**
 @brief Creates a file in the PKI Service file system.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 The IDPrime PIV Card Service is read-only and so does not support this function at all. In this case MD_GetLastError returns GE_NOT_SUPPORTED.
 @param sessionId The session ID previously returned by MD_Init.
 @param path The name of the file to create. Maximum length of short file name is 8 characters.
 @param acls Access conditions of the file
 @param initialSize Initial size of the file in bytes
 The acls parameter is a 3- byte array formatted as follows:
 ■ acls[0]: Admin AC.
 ■ acls[1]: User AC.
 ■ acls[2]: Everyone AC.
 Each AC is a bit mask of the following rights:
 ■ Right_Execute = 0x01 (RFU).
 ■ Right_Write = 0x02 // Apply to CreateFile and DeleteFile in this directory.
 ■ Right_Read = 0x04 // Apply to EnumFiles in this directory.
 */
- (void) MD_CreateFile:(int)sessionId path:(NSString*)path acls:(NSMutableData*)acls initialSize:(int)initialSize;

/**
 @brief This extended version of the command creates a file in the PKI Service file system and updates it with a data value.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 The IDPrime PIV Card Service is read-only and so does not support this function at all. In this case MD_GetLastError returns GE_NOT_SUPPORTED.
 @param sessionId The session ID previously returned by MD_Init.
 @param path The name of the file to create. Maximum length of short file name is 8
 characters.
 @param acls Access conditions of the file
 The acls parameter is a 3- byte array formatted as follows:
 ■ acls[0]: Admin AC.
 ■ acls[1]: User AC.
 ■ acls[2]: Everyone AC.
 Each AC is a bit mask of the following rights:
 ■ Right_Execute = 0x01 (RFU).
 ■ Right_Write = 0x02 // Apply to CreateFile and DeleteFile in this directory.
 ■ Right_Read = 0x04 // Apply to EnumFiles in this directory.
 @param data Initial data value to put in the file
 */
- (void) MD_CreateFileEx:(int)sessionId path:(NSString*)path acls:(NSMutableData*)acls data:(NSMutableData*)data;

/**
 @brief Deletes a file in the PKI Service file system.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 The IDPrime PIV Card Service is read-only and so does not support this function at all. In this case MD_GetLastError returns GE_NOT_SUPPORTED.
 @param sessionId The session ID previously returned by MD_Init.
 @param path The full path name of the file to delete. Maximum length of short file name is
 8 characters.
 */
- (void) MD_DeleteFile:(int)sessionId path:(NSString*)path;

/**
 @brief Write a data value in a file in the PKI Service file system.
 Note: The PKI Service automatically displays a pop-up to authenticate the User role if it is not already authenticated.
 The IDPrime PIV Card Service is read-only and so does not support this function at all. In this case MD_GetLastError returns GE_NOT_SUPPORTED.
 @param sessionId The session ID previously returned by MD_Init.
 @param path The full path name of the file to write to. Maximum length of short file name is
 8 characters.
 @param data Data value to write in the file
 */
- (void) MD_WriteFile:(int)sessionId path:(NSString*)path data:(NSMutableData*)data;

/**
 @brief Read a file in the PKI Service file system.

 @param sessionId The session ID previously returned by MD_Init.
 @param path The full path name of the file to read from. Maximum length of short file name
 is 8 characters.
 @param maxBytesToRead Maximum number of bytes to read in the file.
 @return The data read from the file.
 */
- (NSMutableData*) MD_ReadFile:(int)sessionId path:(NSString*)path maxBytesToRead:(int)maxBytesToRead;

/**
 @brief Return files/sub-directories list from a directory in the PKI Service file system.

 @param sessionId The session ID previously returned by MD_Init.
 @param path The full path name of the directory to be listed. Maximum length of directory
 name is 8 characters.
 – If path = nil; returns files from root directory.
 – If path = “root”; returns sub-directories list from root directory
 @return The files/sub-directories list from the directory.
 */
- (NSMutableArray*) MD_GetFiles:(int)sessionId path:(NSString*)path;

/**
 @brief Return properties of a file in the PKI Service file system.

 @param sessionId The session ID previously returned by MD_Init.
 @param path The full path name of the file whose properties are to be returned. Maximum
 length of short file name is 8 characters.
 @return A 7-byte array with the file properties as follows:
 ■ bytes[0...2] = File acls.
 ■ bytes[3...6] = File size coded on 4 bytes (big-endian).

 */
- (NSMutableData*) MD_GetFileProperties:(int)sessionId path:(NSString*)path;

// Minidriver Extended methods (V6/V7 spec)

/**
 @brief This extended version of the command generates a challenge for Admin authentication or PIN unblock for the specified role.
 Note: Only the Admin role is supported in the current version.
 @param sessionId The session ID previously returned by MD_Init.
 @param role The requested role identifier.
 @return The generated challenge on 8 bytes.
 */
- (NSMutableData*) MD_GetChallengeEx:(int)sessionId role:(Byte)role;

/**
 @brief Authenticates a role according to the specified authentication mode.
 
 @param sessionId The session ID previously returned by MD_Init.
 @param mode Authentication mode:
 – 0x00: clear mode
 – 0x01: generate session PIN mode
 – 0x02: verify session PIN mode

 @param role The role identifier to authenticate.
 @param pin Value of the PIN to authenticate or in the case of Admin role, the response to a
 challenge previously returned by MD_GetChallenge command.
 Note: If the pin parameter is nil, the PKI Service automatically displays a pop-up to
 enter the PIN value.
 @return Session PIN value if authentication mode is 0x01, else nil.
 */
- (NSMutableData*) MD_AuthenticateEx:(int)sessionId mode:(Byte)mode role:(Byte)role pin:(NSMutableData*)pin;

/**
 @brief Deauthenticates one or more roles.
 @param sessionId The session ID previously returned by MD_Init.
 @param roles A bit mask of the role identifiers to deauthenticate.
 */
- (void) MD_DeauthenticateEx:(int)sessionId roles:(Byte)roles;

/**
 @brief Changes or unblocks the value of a PIN or Admin role. The Admin role cannot be unblocked (changed only). This command is an extended version of MD_ChangeReferenceData that can be used for cases where the unblock operation is protected by a role other than that to be unblocked (for example a User role is protected by the Admin role).
 Note: If the oldPin parameter or newPin parameter is nil, the PKI Service automatically displays a pop-up to enter the old PIN or New PIN value.
 @param sessionId The session ID previously returned by MD_Init.
 @param mode 0x02 to change the role value or 0x01h to unblock the role value.
 @param oldRole – For change role value: ID of role to change
 – For unblock role value: the ID of the unblocking role (for example, the Admin
 role value).
 @param oldPin – For change role value: old role value
 – For unblock role value: the IDchallenge/response value.
 @param newRole role ID of role to be changed or unblocked
 @param newPin New role value (PIN or Admin Key)
 @param maxTries The new max tries value of the PIN, -1 if no change.
 */
- (void) MD_ChangeAuthenticatorEx:(int)sessionId mode:(Byte)mode oldRole:(Byte)oldRole oldPin:(NSMutableData*)oldPin newRole:(Byte)newRole newPin:(NSMutableData*)newPin maxTries:(int)maxTries;

// Property management methods

/**
 @brief Return properties of a key container in the PKI Service file system.

 @param sessionId The session ID previously returned by MD_Init.
 @param ctrIndex 0x00...0x0F container index.
 @param property The identifier of the property to be returned.
 The following properties are currently supported:
 ■ CONTAINER_INFO (0x00):
 Returns a byte array blob containing the public key(s) in the selected container.
 The returned blob is formatted as follows:
 Blob = [Signature Pub Key] || [Exchange Pub Key]
 Signature Pub Key and Exchange Pub Key are optional depending on which key exists in the container and are sequences of TLV formatted as follows:
 T_Key_Type = 0x03 L_Key_Type = 0x01
 V_Key_Type = 0x01 for RSA Exchange, 0x02 for RSA Signature, 0x03 for ECDSA 256, 0x04 for ECDSA 384, 0x05 for ECDSA 521, 0x06 for ECDH 256, 0x07 for ECDH 384 and 0x08 for ECDH 521.
 T_Key_RSA_Pub_Exp = 0x01
 L_Key_RSA_Pub_Exp = 0x04
 V_Key_RSAPub_Exp = Value of Public key Exponent on 4 bytes.
 T_Key_RSA_Modulus = 0x02
 L_Key_RSA_Modulus = Key Size (in bytes) >> 4 (1 byte!) V_Key_RSA_Modulus = Value of Public key Modulus on Key Size bytes.
 T_Key_ECC_X = 0x04 L_Key_ECC_X = Key Size (in bytes) V_Key_ECC_X = Value of X.
 T_Key_ECC_Y = 0x05 L_Key_ECC_Y = Key Size (in bytes) V_Key_ECC_Y = Value of Y.
 The 4-bit shift on L_Key_RSA_Modulus is to be able to pass the Modulus length on 1 byte for values 64 to 256 (512 bits to 2048 bits).
 ■ PIN_IDENTIFIER (0x01):
 Returns a byte array of one byte that indicates the role (User PIN or PIN#3 to
 PIN#7) that must be authenticated to use the keys in the container.
 ■ CONTAINER_TYPE (0x80):
 Returns a byte array of two bytes that indicates if the keys in the container were imported or generated on board.
 The returned blob is formatted as follows:
 blob[0] = 0x00 if signature key was imported, 0x01 if it was OBKG. blob[1] = 0x00 if exchange key was imported, 0x01 if it was OBKG.
 @param flags Additional information depending on the property (RFU).
 @return The property value as a byte array blob.
 */
- (NSMutableData*) MD_GetContainerProperty:(int)sessionId ctrIndex:(Byte)ctrIndex property:(Byte)property flags:(Byte)flags;

/**
 @brief Sets a property of a key container in the PKI Service file system.
 Note: The PKI Service automatically displays a pop-up to authenticate the Admin role if it is not already authenticated.
 @param sessionId The session ID previously returned by MD_Init.
 @param ctrIndex 0x00...0x0F container index.
 @param property The identifier of the property to be set.
 The following properties are currently supported:
 ■ PIN_IDENTIFIER (0x01):
 A byte array of one byte that indicates the role (User PIN or PIN#3 to PIN#7) that must be authenticated to use the keys in the container.
 @param data The property value to set as a byte array blob.
 @param flags Additional information depending on the property (RFU).
 */
- (void) MD_SetContainerProperty:(int)sessionId ctrIndex:(Byte)ctrIndex property:(Byte)property data:(NSMutableData*)data flags:(Byte)flags;

/**
 @brief Returns a card property from the PKI Service.

 @param sessionId The session ID previously returned by MD_Init.
 @param property The identifier of the property to be returned.
 
 The following properties are currently supported:
 ■ CARD_FREE_SPACE (0x00):
 Returns a byte array blob of 12 bytes containing the free space information. The returned blob is formatted as follows:
 bytes[0...3] = Free memory size (in bytes) in the card (big-endian). bytes[4...7] = Number of free containers in the card (big-endian). bytes[8...11] = Maximum number of containers in the card (big-endian).
 Using the IDGo 800 PKI with Header File Interface 64
 
 65 IDGo 800 Middleware and SDK for iOS V2.1 Integration Guide
 ■ CARD_KEY_SIZES (0x02):
 Returns a byte array of 16 bytes containing the key sizes information. The returned blob is formatted as follows:
 bytes[0...3] = Minimum key length (big-endian).
 bytes[4...7] = Default key length (big-endian).
 bytes[8...11] = Maximum key length (big-endian).
 bytes[12...15] = Increment key length (big-endian).
 ■ CARD_READ_ONLY (0x03):
 Returns a byte array of 1 byte that indicates if card is read-only or not. The returned blob is formatted as follows:
 blob[0] = Read-only mode.
 – 0x00 = Card is read/write (default).
 – 0x01 = Card is read only.
 ■ CARD_CACHE_MODE (0x04):
 Returns a byte array of 1 byte that indicates the card cache mode. The returned blob is formatted as follows:
 blob[0] = Cache mode.
 – 0x01 = Global cache mode (default).
 – 0x02 = Session cache mode.
 – 0x03 = No cache mode.
 ■ CARD_GUID (0x05):
 Returns a byte array of 16 bytes that indicates the GUID (unique identifier) of the
 card. This is the same value as the 'cardid' file content. The returned blob is formatted as follows: bytes[0...15] = 16-byte GUID (unique identifier).
 ■ CARD_SERIAL_NUMBER (0x06):
 Returns a byte array of 12 bytes that indicates the Serial Number of the card. This
 is the unique chip serial number.
 The returned blob is formatted as follows:
 bytes[0...11] = 12-byte serial number (unique identifier).
 Using the IDGo 800 PKI with Header File Interface 66
 ■ CARD_PIN_INFO (0x07):
 Returns a byte array blob of 12 bytes that indicates the PIN Information of a role.
 The flags parameter indicates the role identifier (User PIN, Admin Key, PIN#3, PIN#4, PIN#5, PIN#6 or PIN#7).
 The returned blob is formatted as follows:
 blob[0] = PIN Type:
 – 0x00 -> Normal Alphanumeric PIN (default for User PIN and PIN#3 to PIN#7).
 – 0x01 -> External PIN (used for Biometrics or PIN pad).
 – 0x02 -> Challenge/Response PIN (Default for Admin Key).
 – 0x03 -> No PIN (used for not protected keys).
 blob[1] = PIN Purpose:
 – 0x00 -> Authentication PIN.
 – 0x01 -> Digital Signature PIN.
 – 0x02 -> Encryption PIN.
 – 0x03 -> Non repudiation PIN.
 – 0x04 -> Admin PIN (default for Admin Key role).
 – 0x05 -> Primary PIN (default for User PIN and PIN#3 to PIN#7 roles).
 – 0x06 -> Unblock PIN (PUK).
 blob[2] = Bit-mask of roles identifier that allows unblock of the PIN. Default is Admin Keys for all PIN roles.
 blob[3] = PIN Cache type, this is used by the Base CSP to manage PIN caching:
 – 0x00 -> Normal cache, the Base CSP maintains cache per application (default).
 – 0x01 -> Timed cache, the Base CSP flush its cache after a time period.
 – 0x02 -> No Cache, the Base CSP not maintains a cache.
 blob[4-7] = Time period (in seconds) if PIN cache type is a timed cache (big- endian).
 blob[8-11] = RFU.
 ■ CARD_ROLES_LIST (0x08):
 Returns a byte array blob of 1 byte that indicates the roles supported by the card. The returned blob is formatted as follows:
 blob[0] = Bit-mask of supported role identifiers. 0x7F in current version -> All roles.
 ■ CARD_AUTHENTICATED_ROLES (0x09):
 Returns a byte array blob of 1 byte that indicates the roles currently authenticated
 by the card.
 The returned blob is formatted as follows:
 blob[0] = Bit-mask of currently authenticated role identifiers.
 ■ CARD_PIN_STRENGTH (0x0A):
 Returns a byte array blob of one byte that indicates the PIN strength of a role.
 The flags parameter indicates the role identifier (User PIN, Admin Key, PIN#3, PIN#4, PIN#5, PIN#6 or PIN#7).
 The returned blob is formatted as follows:
 67 IDGo 800 Middleware and SDK for iOS V2.1 Integration Guide
 blob[0] = Bit-mask of PIN Strength of the role:
 – 0x01 -> Supports plaintext mode verification.
 – 0x02 -> Supports session PIN mode verification.
 ■ CARD_X509_ENROLL (0x0D):
 Returns a byte array blob of 1 byte that indicates if the card supports X509
 certificates enrollment/renewal.
 The returned blob is formatted as follows: blob[0] = X509 certificates mode:
 – 0x00 -> The card does not support X509 certificate enrollment.
 – 0x01 -> The card supports X509 certificate enrollment (default).
 ■ CARD_PIN_POLICY (0x80):
 Returns a byte array blob of 14 bytes that indicates the PIN Policy currently set on
 the card.
 The flags parameter indicates the role identifier (User PIN, Admin Key, PIN#3, PIN#4, PIN#5, PIN#6 or PIN#7).
 The returned blob is formatted as follows:
 blob[0] = Maximum attempts before the PIN is blocked (1-255), default value is 5.
 blob[1] = Minimum PIN length (4-255), default value is 4.
 blob[2] = Maximum PIN length (4-255), default value is 255.
 blob[3] = Authorized char set(s). This is a bits mask with the following char sets:
 – 0x01 -> Numeric (0x30...0x39)
 – 0x02 -> Alphabetic uppercase (0x41...0x5A)
 – 0x04 -> Alphabetic lowercase (0x61...0x7A)
 – 0x08 -> Non alphanumeric (0x20...0x2F + 0x3A...0x40 + 0x5B...0x60 +0x7B...0x7F)
 – 0x10 -> Non ASCII (0x80...0xFF)
 – 0x20 -> Alphabetic all (0x41...0x5A + 0x61...0x7A)
 blob[4] = Number of different characters that can be repeated at least once (0-255), 255 if no limitation (default). This is also known as complexity rule 1.
 blob[5] = Maximum number of times a character can appear (1-255), 255 if no limitation (default). This is also known as complexity rule 2.
 blob[6] = Adjacent characters policy:
 – 0x00 -> Repeated characters cannot be adjacent.
 – 0x01 -> Repeated characters can be adjacent (default).
 blob[7] = Number of previous PIN values a new PIN cannot match (0-10), 0 if no history (default).
 blob[8] = Unblock policy:
 – 0x00 -> PIN unblock is not permitted.
 – 0x01 -> PIN unblock is permitted (default). blob[9] = SSO policy:
 – 0x00 -> PIN SSO is not activated (default).
 – 0x01 -> PIN SSO is activated.
 Using the IDGo 800 PKI with Header File Interface 68 blob[10] = One character from each set usage policy:
 – 0x00 -> Do not enforce PIN value composed with at least one character of each char set (default).
 – 0x01 -> Enforce PIN value composed with at least one character of each char set.
 blob[11] = Mandatory char set(s). This is a bits mask with the following char sets:
 – 0x01 -> Numeric (0x30...0x39)
 – 0x02 -> Alphabetic uppercase (0x41...0x5A)
 – 0x04 -> Alphabetic lowercase (0x61...0x7A)
 – 0x08 -> Non alphanumeric (0x20...0x2F + 0x3A...0x40 + 0x5B...0x60 +0x7B...0x7F)
 – 0x10 -> Non ASCII (0x80...0xFF)
 – 0x20 -> Alphabetic all (0x41...0x5A + 0x61...0x7A)
 – 0x1F -> All chars (0x20...0xFF) (default)
 blob[12] = Maximum length of character sequences e.g., 1,2,3,4 or a,b,c,d. For example, if set to 4, 1,2,3,4,a,5 is allowed, but 1,2,3,4,5,a is not allowed. Range is 1-255. Default is 255 (no limitation)
 blob[13] = Maximum number of adjacent characters. Range is 1-255. Default is 255 (no limitation). This byte is ignored if adjacent characters policy is set to 00 (not allowed). This value cannot exceed the value of complexity rule 2.
 ■ CARD_CHANGE_PIN_FIRST (0xFA):
 Returns a byte array blob of 1 byte that indicates the status of the “Force PIN
 change at first use” property.
 The flags parameter indicates the role identifier (User PIN, PIN#3, PIN#4, PIN#5, PIN#6 or PIN#7).
 The returned blob is formatted as follows: data[0] = Change PIN at first use mode:
 – 0x00 -> Feature is not activated.
 – 0x01 -> Feature is activated.
 ■ CARD_IMPORT_ALLOWED (0x90):
 Returns a byte array blob of 1 byte that indicates if RSA key injection is permitted or
 not.
 The returned blob is formatted as follows: blob[0] = Key injection allowed mode:
 – 0x00 -> The card does not support RSA key injection.
 – 0x01 -> The card supports RSA key injection (default).
 ■ CARD_IMPORT_CHANGE_ALLOWED (0x91): Returns a byte array blob of 1 byte that indicates if the
 CARD_IMPORT_ALLOWED property can be changed or not. The returned blob is formatted as follows:
 blob[0] = CARD_IMPORT_ALLOWED change mode:
 – 0x00 -> CARD_IMPORT_ALLOWED property cannot be changed.
 – 0x01 -> CARD_IMPORT_ALLOWED property can be changed (default).
 
 69 IDGo 800 Middleware and SDK for iOS V2.1 Integration Guide ■ CARD_VERSION_INFO (0xFF):
 Returns a byte array blob of 4 bytes that indicates the exact version number of the card application.
 The returned blob is formatted as follows: blob[0-3] = 4-byte Version Number.
 
 @param flags Additional information depending on the property (RFU).
 @return The property value as a byte array blob.
 */
- (NSMutableData*) MD_GetCardProperty:(int)sessionId property:(Byte)property flags:(Byte)flags;

/**
 @brief Sets a card property in the PKI Service.
 Note: The PKI Service automatically displays a pop-up to authenticate the Admin role if it is not already authenticated.

 @param sessionId The session ID previously returned by MD_Init.
 @param property The identifier of the property to be set.
 
 The following properties are currently supported:
 ■ CARD_READ_ONLY (0x03):
 A 1-byte array blob that indicates if card is read-only or not. The blob is formatted as follows:
 blob[0] = Read-only mode.
 – 0x00 = Card is read/write (default).
 – 0x01 = Card is read only.
 ■ CARD_CACHE_MODE (0x04):
 A 1-byte array blob that indicates the card cache mode. The blob is formatted as follows:
 blob[0] = Cache mode.
 – 0x01 = Global cache mode (default).
 – 0x02 = Session cache mode.
 – 0x03 = No cache mode.
 ■ CARD_GUID (0x05):
 A 16-byte array blob that indicates the GUID (unique identifier) of the card. This is the same value as the 'cardid' file content.
 The blob is formatted as follows:
 bytes[0...15] = 16-byte GUID (unique identifier).
 
 Using the IDGo 800 PKI with Header File Interface 70
 ■ CARD_SERIAL_NUMBER (0x06):
 A 12-byte array blob that indicates the Serial Number of the card. This is the unique chip serial number.
 The blob is formatted as follows:
 bytes[0...11] = 12-byte serial number (unique identifier).
 ■ CARD_PIN_INFO (0x07):
 A 12-byte array blob that indicates the PIN Information of a role.
 The flags parameter indicates the role identifier (User PIN, Admin Key, PIN#3, PIN#4, PIN#5, PIN#6 or PIN#7).
 The blob is formatted as follows:
 blob[0] = PIN Type:
 – 0x00 -> Normal Alphanumeric PIN (default for User PIN and PIN#3 to PIN#7).
 – 0x01 -> External PIN (used for Biometrics or PIN pad).
 – 0x02 -> Challenge/Response PIN (Default for Admin Key).
 – 0x03 -> No PIN (used for not protected keys).
 blob[1] = PIN Purpose:
 – 0x00 -> Authentication PIN.
 – 0x01 -> Digital Signature PIN.
 – 0x02 -> Encryption PIN.
 – 0x03 -> Non repudiation PIN.
 – 0x04 -> Admin PIN (default for Admin Key role).
 – 0x05 -> Primary PIN (default for User PIN and PIN#3 to PIN#7 roles).
 – 0x06 -> Unblock PIN (PUK).
 blob[2] = Bit-mask of roles identifier that allows unblock of the PIN. Default is Admin Keys for all PIN roles.
 blob[3] = PIN Cache type, this is used by the Base CSP to manage PIN caching:
 – 0x00 -> Normal cache, the Base CSP maintains cache per application (default).
 – 0x01 -> Timed cache, the Base CSP flush its cache after a time period.
 – 0x02 -> No Cache, the Base CSP not maintains a cache.
 blob[4-7] = Time period (in seconds) if PIN cache type is a timed cache (big- endian).
 blob[8-11] = RFU.
 ■ CARD_X509_ENROLL (0x0D):
 A 1-byte array blob that indicates if the card supports X509 certificates enrollment/ renewal.
 The blob is formatted as follows: blob[0] = X509 certificates mode:
 – 0x00 -> The card does not support X509 certificate enrollment.
 – 0x01 -> The card supports X509 certificate enrollment (default).
 
 71 IDGo 800 Middleware and SDK for iOS V2.1 Integration Guide
 ■ CARD_PIN_POLICY (0x80):
 A 12-byte array blob that indicates the PIN Policy currently set on the card.
 The flags parameter indicates the role identifier (User PIN, Admin Key, PIN#3, PIN#4, PIN#5, PIN#6 or PIN#7).
 The blob is formatted as follows:
 blob[0] = Maximum attempts before the PIN is blocked (1-255), default value is 5.
 blob[1] = Minimum PIN length (4-255), default value is 4.
 blob[2] = Maximum PIN length (4-255), default value is 255.
 blob[3] = Authorized char set(s). This is a bits mask with the following char sets:
 – 0x01 -> Numeric (0x30...0x39)
 – 0x02 -> Alphabetic uppercase (0x41...0x5A)
 – 0x04 -> Alphabetic lowercase (0x61...0x7A)
 – 0x08 -> Non alphanumeric (0x20...0x2F + 0x3A...0x40 + 0x5B...0x60 +0x7B...0x7F)
 – 0x10 -> Non ASCII (0x80...0xFF)
 – 0x20 -> Alphabetic all (0x41...0x5A + 0x61...0x7A)
 blob[4] = Number of different characters that can be repeated at least once (0-255), 255 if no limitation (default). This is also known as complexity rule 1.
 blob[5] = Maximum number of times a character can appear (1-255), 255 if no limitation (default). This is also known as complexity rule 2.
 blob[6] = Adjacent characters policy:
 – 0x00 -> Repeated characters cannot be adjacent.
 – 0x01 -> Repeated characters can be adjacent (default).
 blob[7] = Number of previous PIN values a new PIN cannot match (0-10), 0 if no history (default).
 blob[8] = Unblock policy:
 – 0x00 -> PIN unblock is not permitted.
 – 0x01 -> PIN unblock is permitted (default).
 blob[9] = SSO policy:
 – 0x00 -> PIN SSO is not activated (default).
 – 0x01 -> PIN SSO is activated.
 blob[10] = One character from each set usage policy:
 – 0x00 -> Do not enforce PIN value composed with at least one character of each char set (default).
 – 0x01 -> Enforce PIN value composed with at least one character of each char set.
 Using the IDGo 800 PKI with Header File Interface 72
 blob[11] = Mandatory char set(s). This is a bits mask with the following char sets:
 – 0x01 -> Numeric (0x30...0x39)
 – 0x02 -> Alphabetic uppercase (0x41...0x5A)
 – 0x04 -> Alphabetic lowercase (0x61...0x7A)
 – 0x08 -> Non alphanumeric (0x20...0x2F + 0x3A...0x40 + 0x5B...0x60 +0x7B...0x7F)
 – 0x10 -> Non ASCII (0x80...0xFF)
 – 0x20 -> Alphabetic all (0x41...0x5A + 0x61...0x7A)
 – 0x1F -> All chars (0x20...0xFF) (default)
 blob[12] = Maximum length of character sequences e.g., 1,2,3,4 or a,b,c,d. For example, if set to 4, 1,2,3,4,a,5 is allowed, but 1,2,3,4,5,a is not allowed. Range is 1-255. Default is 255 (no limitation)
 blob[13] = Maximum number of adjacent characters. Range is 1-255. Default is 255 (no limitation). This byte is ignored if adjacent characters policy is set to 00 (not allowed). This value cannot exceed the value of complexity rule 2.
 ■ CARD_CHANGE_PIN_FIRST (0xFA):
 A 1-byte array blob that indicates the status of the “Force PIN change at first use”
 property.
 The flags parameter indicates the role identifier (User PIN, PIN#3, PIN#4, PIN#5, PIN#6 or PIN#7).
 The blob is formatted as follows: data[0] = Change PIN at first use mode:
 – 0x00 -> Feature is not activated.
 – 0x01 -> Feature is activated.
 ■ CARD_IMPORT_ALLOWED (0x90):
 A 1-byte array blob that indicates if RSA key injection is permitted or not. The blob is formatted as follows:
 blob[0] = Key injection allowed mode:
 – 0x00 -> The card does not support RSA key injection.
 – 0x01 -> The card supports RSA key injection (default).
 ■ CARD_IMPORT_CHANGE_ALLOWED (0x91):
 A 1-byte array blob that indicates if the CARD_IMPORT_ALLOWED property can
 be changed or not.
 The returned blob is formatted as follows:
 blob[0] = CARD_IMPORT_ALLOWED change mode:
 – 0x00 -> CARD_IMPORT_ALLOWED property cannot be changed.
 – 0x01 -> CARD_IMPORT_ALLOWED property can be changed (default).
 @param data The value of the property to set as a byte array blob.
 @param flags Additional information depending on the property (RFU).
 */
- (void) MD_SetCardProperty:(int)sessionId property:(Byte)property data:(NSMutableData*)data flags:(Byte)flags;

/**
 @brief Returns all the certificates and their associated key containers information from the PKI Service.

 @param sessionId The session ID previously returned by MD_Init.
 @return An array blob formatted as follows: Blob = Header || [Containers]*
 Header is formatted as follows:
 ■ header[0] = 0x00
 ■ header[1] = 0x01
 ■ header[2] = Number of containers.
 Containers is a set of “Number of containers” TLV sequences (header[2]) formatted as follows:
 // The Certificate associated with the container
 T_Cert = 0x01
 L_Cert = Length of certificate value on 2 bytes (big endian). V_Cert = Value of certificate on L_Cert bytes.
 // The Key Container index associated with the certificate T_Key_Id = 0x02
 L_Key_Id = 0x01
 V_Key_Id = Container Index (always one byte).
 // The Key Type T_Key_Type = 0x03 L_Key_Type = 0x01
 V_Key_Type = 0x01 for RSA Exchange, 0x02 for RSA Signature, 0x03 for ECDSA 256, 0x04 for ECDSA 384, 0x05 for ECDSA 521, 0x06 for ECDH 256, 0x07 for ECDH 384 and 0x08 for ECDH 521 (always one byte).
 // The Key Length in bits
 T_Key_Len = 0x04
 L_Key_Len = 0x02
 V_Key_Len = Length of the key in bits on L_Key_Len bytes (big endian).
 */
- (NSMutableData*) MD_GetKeysAndCertificates:(int)sessionId;

/**
 @brief Returns all the certificates and their associated key containers information from the PKI Service.
 
 @param sessionId The session ID previously returned by MD_Init.
 @return An array blob formatted as follows: Blob = Header || [Containers]*
 Header is formatted as follows:
 ■ header[0] = 0x00
 ■ header[1] = 0x01
 ■ header[2] = Number of containers.
 Containers is a set of “Number of containers” TLV sequences (header[2]) formatted as follows:
 // The Certificate associated with the container
 T_Cert = 0x01
 L_Cert = Length of certificate value on 2 bytes (big endian). V_Cert = Value of certificate on L_Cert bytes.
 // The Key Container index associated with the certificate T_Key_Id = 0x02
 L_Key_Id = 0x01
 V_Key_Id = Container Index (always one byte).
 // The Key Type T_Key_Type = 0x03 L_Key_Type = 0x01
 V_Key_Type = 0x01 for RSA Exchange, 0x02 for RSA Signature, 0x03 for ECDSA 256, 0x04 for ECDSA 384, 0x05 for ECDSA 521, 0x06 for ECDH 256, 0x07 for ECDH 384 and 0x08 for ECDH 521 (always one byte).
 // The Key Length in bits
 T_Key_Len = 0x04
 L_Key_Len = 0x02
 V_Key_Len = Length of the key in bits on L_Key_Len bytes (big endian).
 // The Key Label
 T_Key_Label = 0x05
 L_Key_Label = length of the key label on 1 byte
 V_Key_label = Label of the key on L_Key_Label bytes (string).
 */
- (NSMutableData*) MD_GetKeysAndCertificatesEx:(int)sessionId;

// Generic command (RFU)
- (NSMutableData*) MD_GenericCommand:(int)sessionId cmdId:(int)cmdId data:(NSMutableData*)data;

@end
