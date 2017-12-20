import React from 'react';
import PropTypes from 'prop-types';


import {
  ViewPropTypes,
} from 'react-native';

import decorateMapComponent, {
  USES_DEFAULT_IMPLEMENTATION,
  SUPPORTED,
} from './decorateMapComponent';

const viewConfig = {
  uiViewClassName: 'AIR<provider>CanvasInteractionUrlTile',
};

const propTypes = {
  ...ViewPropTypes,

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
   * Area id to get the tiles from the corrent folder
   */
  areaId: PropTypes.string,
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
  /**
   * Max zoom when the tiles have data
   */
  coordinates: PropTypes.shape({
    tile: PropTypes.arrayOf(PropTypes.number),
    precision: PropTypes.arrayOf(PropTypes.number),
  }),
};

const defaultProps = {
  maxZoom: 12,
  isConnected: true,
};

class CanvasInteractionUrlTile extends React.Component {
  render() {
    const AIRMapCanvasInteractionUrlTile = this.getAirComponent();
    return (
      <AIRMapCanvasInteractionUrlTile
        {...this.props}
      />
    );
  }
}

CanvasInteractionUrlTile.viewConfig = viewConfig;
CanvasInteractionUrlTile.propTypes = propTypes;
CanvasInteractionUrlTile.defaultProps = defaultProps;

module.exports = decorateMapComponent(CanvasInteractionUrlTile, {
  componentType: 'CanvasInteractionUrlTile',
  providers: {
    google: {
      ios: SUPPORTED,
      android: USES_DEFAULT_IMPLEMENTATION,
    },
  },
});
