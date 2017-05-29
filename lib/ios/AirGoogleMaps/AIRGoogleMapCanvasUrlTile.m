//
//  AIRGoogleMapCanvasUrlTile.m
//  ForestWatcher
//
//  Created by Gerardo Pacheco on 21/01/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "AIRGoogleMapCanvasUrlTile.h"
#import "AIRGoogleMapCanvasTile.h"
#import <math.h>

@implementation AIRGoogleMapCanvasUrlTile

- (void)setUrlTemplate:(NSString *)urlTemplate
{
    _urlTemplate = urlTemplate;
//    _tileLayer = [AIRGoogleMapCanvasTile tileLayerWithURLConstructor:[self _getTileURLConstructor]];
    _tileLayer = [[AIRGoogleMapCanvasTile alloc] init];
}

@end
