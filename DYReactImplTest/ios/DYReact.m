//
//  DYReact.m
//  DYReactImplTest
//
//  Created by Idan Oshri on 23/12/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "DYReact.h"

@implementation DYReact

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(setSecret:(NSString*)secret callback:(RCTResponseSenderBlock)callback){
  [[DYApi getInstance] setSecretKey:secret completionHandler:^(ExperimentsState state) {
    if (state == DY_READY_AND_UPDATED || state == DY_READY_NO_UPDATE_NEEDED){
      static dispatch_once_t onceToken;
      dispatch_once(&onceToken, ^{
        callback(@[[NSString stringWithFormat:@"%d",state]]);
      });
    }
  }];
}

RCT_EXPORT_METHOD(trackEvent:(NSString*)eventName params:(NSDictionary*)params callback:(RCTResponseSenderBlock)callback){
  [[DYApi getInstance] trackEvent:eventName prop:params completionHandler:^(NSString * _Nonnull name) {
    callback(@[name]);
  }];
}

RCT_EXPORT_METHOD(pageView:(NSString* _Nonnull)uniqueID context:(NSDictionary* _Nullable)context callback:(RCTResponseSenderBlock)callback){
  DYPageContext* dyContext = [[DYPageContext alloc] initWithJSONDic:context];
  [[DYApi getInstance] pageView:uniqueID context:dyContext completionHandler:^(NSString * _Nonnull name) {
    callback(@[name]);
  }];
}

RCT_EXPORT_METHOD(consentOptIn){
  [[DYApi getInstance] consentOptin];
}

RCT_EXPORT_METHOD(consentOptOut){
  [[DYApi getInstance] consentOptout];
}

RCT_EXPORT_METHOD(setEvaluator:(NSString* _Nonnull)evaluatorID forParams:(NSArray* _Nonnull)params saveBetweenSessions:(BOOL)save  callback:(RCTResponseSenderBlock)callback){
  
  [[DYApi getInstance] setEvaluator:evaluatorID forParams:params saveBetweenSessions:save completionHandler:^(NSString * _Nonnull evaluatorID, NSArray * _Nonnull params, BOOL isPersistent) {
    callback(@[evaluatorID,params,[NSNumber numberWithBool:isPersistent]]);
  }];
}

RCT_EXPORT_METHOD(getSmartObjectData:(id _Nonnull)smartObjectID callback:(RCTResponseSenderBlock)callback){
  id result = [[DYApi getInstance] getSmartObjectData:smartObjectID];
  callback(@[smartObjectID,result]);
}

RCT_EXPORT_METHOD(getSmartVariable:(NSString* _Nonnull)varName defaultValue:(id _Nullable)defaultValue callback:(RCTResponseSenderBlock)callback){
  id result = [[DYApi getInstance] getSmartVariable:varName defaultValue:defaultValue];
  callback(@[varName,result]);
}

RCT_EXPORT_METHOD(identifyUser:(NSDictionary*)userData callback:(RCTResponseSenderBlock)callback){
  if (userData){
    DYUserData* dyUserData = [DYUserData createFromDictionary:userData];
    [[DYApi getInstance] identifyUser:dyUserData];
  } else {
    callback(@[@"No user data supplied"]);
  }
}

RCT_EXPORT_METHOD(sendRecommendationRequest:(NSString* _Nonnull)widgetID context:(NSDictionary* _Nullable)context callback:(RCTResponseSenderBlock)callback){
  DYPageContext* dyContext = [[DYPageContext alloc] initWithJSONDic:context];
  [[DYApi getInstance] sendRecommendationRequest:widgetID withContext:dyContext completionHandler:^(NSArray * _Nullable recommendations, NSString * _Nonnull widgetID) {
    callback(@[recommendations,widgetID]);
  }];
}

RCT_EXPORT_METHOD(trackRecomItemRealImpression:(NSString* _Nonnull)widgetID itemIDs:(NSArray* _Nonnull)itemIDs){
  [[DYApi getInstance] TrackRecomItemsRealImpression:widgetID andItemID:itemIDs];
}

RCT_EXPORT_METHOD(trackRecomItemClick:(NSString* _Nonnull)widgetID itemID:(NSString* _Nonnull)itemID){
  [[DYApi getInstance] TrackRecomItemClick:widgetID andItemID:itemID];
}

RCT_EXPORT_METHOD(setUseEuropeanServer:(BOOL)on){
  [[DYApi getInstance] setUseEuropeanServer:on];
}

@end
