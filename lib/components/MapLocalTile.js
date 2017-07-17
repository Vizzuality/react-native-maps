import PropTypes from 'prop-types';
import React from 'react';

import {
  ViewPropTypes,
} from 'react-native';

import decorateMapComponent, {
  USES_DEFAULT_IMPLEMENTATION,
  SUPPORTED,
} from './decorateMapComponent';

const propTypes = {
  ...ViewPropTypes,

  /**
   * The url template of the local path. The patterns {x} {y} {z} will be replaced at runtime
   * For example, http://c.tile.openstreetmap.org/{z}/{x}/{y}.png
   */
  localTemplate: PropTypes.string.isRequired,

  /**
   * The order in which this tile overlay is drawn with respect to other overlays. An overlay
   * with a larger z-index is drawn over overlays with smaller z-indices. The order of overlays
   * with the same z-index is arbitrary. The default zIndex is -1.
   *
   * @platform android
   */
  zIndex: PropTypes.number,
};

class MapLocalTile extends React.Component {
  render() {
    const AIRMapLocalTile = this.getAirComponent();
    return (
      <AIRMapLocalTile
        {...this.props}
      />
    );
  }
}

MapLocalTile.propTypes = propTypes;

module.exports = decorateMapComponent(MapLocalTile, {
  componentType: 'LocalTile',
  providers: {
    google: {
      ios: SUPPORTED,
      android: USES_DEFAULT_IMPLEMENTATION,
    },
  },
});
