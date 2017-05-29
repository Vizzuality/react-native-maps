//
//  AIRGoogleMapCanvasURLTileManager.m
//  Created by Gerardo Pacheco on 21/01/2017.
//

#import "AIRGoogleMapCanvasUrlTileManager.h"
#import "AIRGoogleMapCanvasUrlTile.h"

@interface AIRGoogleMapCanvasUrlTileManager()

@end

@implementation AIRGoogleMapCanvasUrlTileManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
  AIRGoogleMapCanvasUrlTile *tileLayer = [AIRGoogleMapCanvasUrlTile new];
  return tileLayer;
}

RCT_EXPORT_VIEW_PROPERTY(urlTemplate, NSString)
RCT_EXPORT_VIEW_PROPERTY(zIndex, int)
RCT_EXPORT_VIEW_PROPERTY(maxZoom, int)
RCT_EXPORT_VIEW_PROPERTY(areaId, NSString)
RCT_EXPORT_VIEW_PROPERTY(alertType, NSString)
RCT_EXPORT_VIEW_PROPERTY(isConnected, BOOL)
RCT_EXPORT_VIEW_PROPERTY(minDate, NSString)
RCT_EXPORT_VIEW_PROPERTY(maxDate, NSString)

@end

