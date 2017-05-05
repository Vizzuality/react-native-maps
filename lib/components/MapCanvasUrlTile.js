import React, { PropTypes } from 'react';

import {
  View,
} from 'react-native';

import decorateMapComponent, {
  USES_DEFAULT_IMPLEMENTATION,
  SUPPORTED,
} from './decorateMapComponent';

const propTypes = {
  ...View.propTypes,

  /**
   * The url template of the tile server. The patterns {x} {y} {z} will be replaced at runtime
   * For example, http://c.tile.openstreetmap.org/{z}/{x}/{y}.png
   */
  urlTemplate: PropTypes.string.isRequired,

  /**
   * The order in which this tile overlay is drawn with respect to other overlays. An overlay
   * with a larger z-index is drawn over overlays with smaller z-indices. The order of overlays
   * with the same z-index is arbitrary. The default zIndex is -1.
   *
   * @platform android
   */
  zIndex: PropTypes.number,
  /**
   * Flag to use the offline tiles instead of the url version
   */
  isConnected: PropTypes.bool,
  /**
   * Area name to get the tiles from the corrent folder
   */
  areaName: PropTypes.string,
  /**
   * Min date to get tiles
   */
  minDate: PropTypes.string,
  /**
   * Max date to get tiles
   */
  maxDate: PropTypes.string,
  /**
   * Max zoom when the tiles have data
   */
  maxZoom: PropTypes.number,
};

class CanvasUrlTile extends React.Component {
  render() {
    const AIRMapCanvasUrlTile = this.getAirComponent();
    return (
      <AIRMapCanvasUrlTile
        {...this.props}
      />
    );
  }
}

CanvasUrlTile.propTypes = propTypes;

module.exports = decorateMapComponent(CanvasUrlTile, {
  componentType: 'CanvasUrlTile',
  providers: {
    google: {
      ios: SUPPORTED,
      android: USES_DEFAULT_IMPLEMENTATION,
    },
  },
});
