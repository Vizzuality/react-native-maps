import React from 'react';
import {
  StyleSheet,
  View,
  Text,
  Dimensions,
  Platform,
  PermissionsAndroid,
} from 'react-native';

import MapView, { MAP_TYPES, PROVIDER_DEFAULT } from 'react-native-maps';

const geoViewport = require('@mapbox/geo-viewport');
const tilebelt = require('@mapbox/tilebelt');

const { width, height } = Dimensions.get('window');

const ASPECT_RATIO = width / height;
const LATITUDE = -8.380882;
const LONGITUDE = -74.448166;
const LATITUDE_DELTA = 0.2222;
const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;

class CustomTiles extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.state = {
      coordinates: {
        latitude: null,
        longitude: null,
        tile: [], // tile coordinates x, y, z
      },
      region: {
        latitude: LATITUDE,
        longitude: LONGITUDE,
        latitudeDelta: LATITUDE_DELTA,
        longitudeDelta: LONGITUDE_DELTA,
      },
    };
  }

  get mapType() {
    // MapKit does not support 'none' as a base map
    return this.props.provider === PROVIDER_DEFAULT ?
      MAP_TYPES.STANDARD : MAP_TYPES.NONE;
  }

  componentDidMount() {
    if (Platform.OS === 'android') {
      PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE);
      PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE);
    }
  }

  getMapZoom() {
    const position = this.state.region;

    const bounds = [
      position.longitude - (position.longitudeDelta / 2),
      position.latitude - (position.latitudeDelta / 2),
      position.longitude + (position.longitudeDelta / 2),
      position.latitude + (position.latitudeDelta / 2),
    ];

    return geoViewport.viewport(bounds, [height, width]).zoom || null;
  }

  onRegionChange = (region) => {
    this.setState({ region });
  }

  onMapPress = (e) => {
    const coordinates = e.nativeEvent.coordinate;
    const zoom = this.getMapZoom();
    const tile = tilebelt.pointToTile(coordinates.longitude, coordinates.latitude, zoom);
    this.setState({
      coordinates: {
        ...coordinates,
        tile,
      },
    });
  }

  render() {
    const { region, coordinates } = this.state;
    const hasCoordinates = (coordinates.latitude && coordinates.longitude && coordinates.tile !== null) || false;

    return (
      <View style={styles.container}>
        <MapView
          provider={this.props.provider}
          mapType={this.mapType}
          style={styles.map}
          mapType="hybrid"
          onPress={this.onMapPress}
          initialRegion={region}
          onRegionChangeComplete={this.onRegionChange}
        >
          <MapView.CanvasUrlTile
            urlTemplate="http://wri-tiles.s3.amazonaws.com/glad_prod/tiles/{z}/{x}/{y}.png"
            zIndex={-1}
            maxZoom={12}
            areaName="Download"
            isConnected
            minDate="2017/01/01"
            maxDate="2017/03/01"
          />
          {hasCoordinates &&
            <MapView.CanvasInteractionUrlTile
              coordinates={coordinates}
              urlTemplate="http://wri-tiles.s3.amazonaws.com/glad_prod/tiles/{z}/{x}/{y}.png"
              zIndex={-1}
              maxZoom={12}
              areaName="Download"
              isConnected
              minDate="2017/01/01"
              maxDate="2017/03/01"
            />
          }
        </MapView>
        <View style={styles.buttonContainer}>
          <View style={styles.bubble}>
            <Text>Custom Tiles</Text>
          </View>
        </View>
      </View>
    );
  }
}

CustomTiles.propTypes = {
  provider: MapView.ProviderPropType,
};

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'flex-end',
    alignItems: 'center',
  },
  map: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
  },
  bubble: {
    flex: 1,
    backgroundColor: 'rgba(255,255,255,0.7)',
    paddingHorizontal: 18,
    paddingVertical: 12,
    borderRadius: 20,
  },
  latlng: {
    width: 200,
    alignItems: 'stretch',
  },
  button: {
    width: 80,
    paddingHorizontal: 12,
    alignItems: 'center',
    marginHorizontal: 10,
  },
  buttonContainer: {
    flexDirection: 'row',
    marginVertical: 20,
    backgroundColor: 'transparent',
  },
});

module.exports = CustomTiles;
