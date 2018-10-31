//
//  CardModuleServiceConstant.h
//
//  Created by Riad Baatouche on 20/09/13.
//  Copyright (c) 2013 Gemalto. All rights reserved.
//

#ifndef __CardModuleServiceConstant_h__ 
#define __CardModuleServiceConstant_h__ 

// ------------------------------------------------------
// Constants : public
// ------------------------------------------------------
#define EVERYONE   0x00                 // 0x00 = 0
#define USER       0x01                 // 0x01 = 1
#define ADMIN      0x02                 // 0x02 = 2
#define ROLE3      0x04                 // 0x04 = 4
#define ROLE4      0x08         		// 0x08 = 8
#define ROLE5      0x10                 // 0x10 = 16
#define ROLE6      0x20                 // 0x20 = 32
#define ROLE7      0x40                 // 0x40 = 64
#define CARD_ADMIN 0x80                 // 0x80 = 128
#define ALL_ROLES  0xFF

#define AC_ADMIN    0x00
#define AC_USER     0x01
#define AC_EVERYONE 0x02

#define AC_RIGHT_EXECUTE  0x01
#define AC_RIGHT_WRITE    0x02
#define AC_RIGHT_READ     0x04


#endif