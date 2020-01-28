/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import {NativeModules} from 'react-native';
const DYReact = NativeModules.DYReact;

import React, {Component} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Image,
  Button,
} from 'react-native';

const styles = StyleSheet.create({
  imageContainer: {},
  image: {
    width: 150,
    height: 150,
  },
});

export default class App extends Component {
  constructor() {
    super();
    this.state = {
      image_containers: [],
      DYReturned: false,
    };
    DYReact.setSecret('569bb4b05f49228f315e133c', state => {
      console.log(`returned with state: ${state} `);
      this.setState({DYReturned: true});
    });
  }

  getObjects = () => {
    return [
      this.getButton(this.callPageView, 'PageView'),
      this.getButton(this.callRCOM, 'RCOM'),
      this.getScrollView(),
      this.getButton(this.callTrackEvent, 'send event'),
      this.getButton(this.callConsentOptOut, 'consentOptOut'),
      this.getButton(this.callConsentOptIn, 'consentOptIn'),
      this.getButton(this.callIdentifyUser, 'identifyUser'),
      this.getButton(this.callSmartVariable, 'callSmartVar'),
      this.getButton(this.callSetEvaluator, 'setEvaluator'),
    ];
  };

  getScrollView = () => {
    return (
      <ScrollView
        key={'scrollView'}
        horizontal={true}
        onScroll={this.handleScroll}
        style={styles.imageContainer}>
        {this.state.image_containers}
      </ScrollView>
    );
  };

  handleScroll = (event: Object) => {
    console.log(JSON.stringify(event.nativeEvent));
  };

  getButton = (onPress, title) => {
    return <Button key={title} onPress={onPress} title={title} />;
  };

  callIdentifyUser = () => {
    DYReact.identifyUser({cuid: '12345678'}, err => {
      if (err) {
        console.log(`got error: ${err}`);
      }
    });
  };

  callConsentOptOut = () => {
    DYReact.consentOptOut();
  };

  callConsentOptIn = () => {
    DYReact.consentOptIn();
  };

  callSetEvaluator = () => {
    DYReact.setEvaluator('15665', ['test1'], true, () => {
      this.callSmartVariable();
    });
  };

  callSmartVariable = () => {
    DYReact.getSmartVariable('testSticky', 'AppDefValue', (varName, value) => {
      console.log(`smartVar: ${varName} returned ${value}`);
      this.callRCOM(value);
    });
  };

  callRCOM = widgetIDParam => {
    this.setState({image_containers: []});
    console.log(
      `calling sendRecommendationRequest with widgetID: ${widgetIDParam}`,
    );
    DYReact.sendRecommendationRequest(
      '31002',
      {type: 'HOMEPAGE'},
      (rcom, widgetID) => {
        console.log(`widgetID: ${widgetID} returned`);
        const containers = [];
        const skus = [];
        rcom.forEach((item, index) => {
          skus.push(item.item.sku);
          containers.push(
            <TouchableOpacity
              key={index}
              onPress={() => this.reportRCOMClick(item.item.sku)}>
              <Image style={styles.image} source={{uri: item.item.image_url}} />
            </TouchableOpacity>,
          );
        });
        this.reportRCOMRimp(skus);
        this.setState({image_containers: containers});
      },
    );
  };

  reportRCOMClick = sku => {
    console.log(`report ${sku} clicked`);
    DYReact.trackRecomItemClick('31002', sku);
  };

  reportRCOMRimp = skus => {
    console.log(`report ${skus} viewed`);
    DYReact.trackRecomItemRealImpression('31002', skus);
  };

  callTrackEvent = () => {
    DYReact.trackEvent('testEvent', {paramA: 'a'}, name => {
      if (name) {
        console.log(`event ${name} processed`);
      }
    });
  };

  callPageView = () => {
    DYReact.pageView(
      'testPageView',
      {lng: 'en_US', type: 'HOMEPAGE', data: ['a', 'b']},
      name => {
        if (name) {
          console.log(`page ${name} processed`);
        }
      },
    );
  };

  render() {
    return (
      <SafeAreaView>
        {this.state.DYReturned ? this.getObjects() : []}
      </SafeAreaView>
    );
  }
}
