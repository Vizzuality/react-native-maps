//
//  AIRGoogleMapCanvasUrlTile.h
//  ForestWatcher
//
//  Created by Gerardo Pacheco on 21/01/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GoogleMaps/GoogleMaps.h>

@interface AIRGoogleMapCanvasUrlTile : UIView

@property (nonatomic, strong) GMSURLTileLayer *tileLayer;
@property (nonatomic, assign) NSString *urlTemplate;
@property (nonatomic, assign) int zIndex;
@property (nonatomic, assign) int *maxZoom;
@property (nonatomic, assign) NSString *areaId;
@property (nonatomic, assign) NSString *alertType;
@property (nonatomic, assign) BOOL *isConnected;
@property (nonatomic, assign) NSString *minDate;
@property (nonatomic, assign) NSString *maxDate;

@end
