//
// HYBLanguage.m
// [y] hybris Platform
//
// Copyright (c) 2000-2016 hybris AG
// All rights reserved.
//
// This software is the confidential and proprietary information of hybris
// ("Confidential Information"). You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the
// license agreement you entered into with hybris.
//
// Warning:This file was auto-generated by OCC2Ojbc.
//

#import "HYBLanguage.h"
#import "NSValueTransformer+MTLPredefinedTransformerAdditions.h"



@implementation HYBLanguage

+ (instancetype)languageWithParams:(NSDictionary*)params {

NSError *error = nil;
HYBLanguage *object = [MTLJSONAdapter modelOfClass:[HYBLanguage class] fromJSONDictionary:params error:&error];

if (error) {
    NSLog(@"Couldn't convert JSON to model HYBLanguage");
    return nil;
}

return object;
}

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
   return @{
@"nativeName" : @"nativeName",
@"isocode" : @"isocode",
@"name" : @"name",
@"active" : @"active"
};
}






@end