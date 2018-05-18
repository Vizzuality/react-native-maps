//
//  AIRGoogleMapLocalTile.m
//  Created by j8seangel on 4/08/17.
//

#import "AIRGoogleMapLocalTile.h"

int MEM_MAX_SIZE = 8;

@interface LocalTileLayer : GMSSyncTileLayer
@property NSString* pathTemplate;
@end

@implementation LocalTileLayer

- (id)init:(NSString *)pathTemplate {
  _pathTemplate = pathTemplate;
  return self;
}

- (NSString *)applicationDocumentsDirectory {
  return [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
}

- (UIImage *)readTileFromFile: (NSString *)url {
  NSString *urlPng = [NSString stringWithFormat: @"%@.png", url];
  NSString *imagePath=[[self applicationDocumentsDirectory] stringByAppendingPathComponent: urlPng];

  NSFileManager *fileManager = [NSFileManager defaultManager];
  BOOL isFileExist = [fileManager fileExistsAtPath: imagePath];
  if (isFileExist) {
    return [UIImage imageWithContentsOfFile:imagePath];
  } else {
    NSString *urlJpg = [NSString stringWithFormat: @"%@.jpg", url];
    NSString *imagePath=[[self applicationDocumentsDirectory] stringByAppendingPathComponent: urlJpg];
    BOOL isFileExist = [fileManager fileExistsAtPath: imagePath];
    if (isFileExist) {
      return [UIImage imageWithContentsOfFile: imagePath];
    }
  }
  return nil;
}

- (UIImage *)getRescaledTileBitmap: (UIImage*)image x:(int)x y:(int)y z:(int)z {
  int zSteps = z - _maxZoom;
  int relation = (int) pow(2, zSteps);
  int cropSize = (_tileSize / relation);
  int cropX = (fmod(x, relation)) * (_tileSize / relation);
  int cropY = (fmod(y, relation)) * (_tileSize / relation);
  int scaleSize = (relation <= MEM_MAX_SIZE) ? _tileSize * relation : _tileSize * MEM_MAX_SIZE;


  CGRect cropRect = CGRectMake(cropX, cropY, cropSize, cropSize);
  CGImageRef croppedRef = CGImageCreateWithImageInRect([image CGImage], cropRect);

  CGSize size = CGSizeMake(scaleSize, scaleSize);
  UIGraphicsBeginImageContextWithOptions(size, NO, 1.0);
  [[UIImage imageWithCGImage:croppedRef] drawInRect:CGRectMake(0, 0, size.width, size.height)];
  CGImageRelease(croppedRef);
  UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
  UIGraphicsEndImageContext();
  return newImage;
}

- (UIImage *)tileForX:(NSUInteger)x y:(NSUInteger)y zoom:(NSUInteger)zoom {
  NSUInteger xCoord = x;
  NSUInteger yCoord = y;
  int zCoord = (int)zoom;
  bool shouldRescaleTile =  (zoom > maxZoom);

  if (shouldRescaleTile) {
    int zSteps = (int)zCoord - maxZoom;
    int relation = (int) pow(2, zSteps) ;
    xCoord = (int)(x / relation);
    yCoord = (int)(y / relation);
    zCoord = _maxZoom;
  }

  NSString *url = _pathTemplate;
  url = [url stringByReplacingOccurrencesOfString:@"{x}" withString:[NSString stringWithFormat: @"%ld", (long)xCoord]];
  url = [url stringByReplacingOccurrencesOfString:@"{y}" withString:[NSString stringWithFormat: @"%ld", (long)yCoord]];
  url = [url stringByReplacingOccurrencesOfString:@"{z}" withString:[NSString stringWithFormat: @"%d", zCoord]];

  NSLog(@"%@", url);

  UIImage *image = [self readTileFromFile:url];
  if (image == nil) {
    return kGMSTileLayerNoTile;
  }
  if (shouldRescaleTile) {
    return [self getRescaledTileBitmap:image x:(int)x y:(int)y z:(int)zoom];
  } else {
    return image;
  }
}

@end

@implementation AIRGoogleMapLocalTile

- (void)setZIndex:(int)zIndex
{
  _zIndex = zIndex;
  _tileLayer.zIndex = zIndex;
}

- (void)setPathTemplate:(NSString *)pathTemplate
{
  _pathTemplate = pathTemplate;
  _tileLayer = [[LocalTileLayer alloc] init:_pathTemplate];
}

- (void)setMaxZoom:(int)maxZoom
{
  _maxZoom = maxZoom;
}

- (void)setTileSize:(int)maxZoom
{
  _tileSize = tileSize;
}
@end
