<h1 align="center">Welcome to DYReactNative 👋</h1>
<p>
</p>

> How to integrate DY native mobile SDKs into react native apps

### 🏠 [Homepage](https://www.dynamicyield.com)

## iOS Integration

1. Create a ReactNative project or open your project
1. Navigate into folder 'ios' inside your project's path
1. add pod ```'Dynamic-Yield-iOS-SDK'``` to the end of the pod list inside ```Podfile```
1. run ```pod install```
1. open your project ```.xcworkspace``` file
1. clone this repo
1. copy ```DYReactImplTest/ios/DYReact.m``` & ```DYReactImplTest/ios/DYReact.h``` into your project


## Android Integration

1. Create a ReactNative project or open your project
1. Start Android studio and open the project that inside the folder
```android``` inside your project's path
1. add ```implementation ('com.dynamicyield:DYAPISDK:+'){ transitive = false;}```
to the end of the dependencies list inside the ```build.gradle``` file of your app module
1. Sync the gradle file
1. clone this repo
1. copy ```DYReactImplTest/android/app/src/main/java/com/dyreactimpltest/bridge/DYReact.java``` & ```DYReactImplTest/android/app/src/main/java/com/dyreactimpltest/bridge/DYReactPackage.java``` into your project
1. add ```packages.add(new DYReactPackage());``` in the ```getPackages``` method of the ```MainApplication.java``` file 


## React Native Usage


1. Add the following code sample to files that will use DY SDK:
      ```js
      import {NativeModules} from 'react-native';
      const DYReact = NativeModules.DYReact;
      ```
2. Add the following code sample to your default class App:
      ```js
      export default class App extends Component {
        constructor() {
        super();
        this.state = {
          image_containers: [],
          DYReturned: false,
        };
        DYReact.setSecret('569bb4b05f49228f315e133c', state => {
           console.log('returned with state: ' + state);
           this.setState({DYReturned: true});
        });
      }
      ```
3. Listen to ```this.state.DYReturned```, or start using DY inside the ```setSecret``` callback


## API Documentation


| Name        | description           | Params  |
| ------------- |:-------------:| -----:|
| DYReact.setSecret(secretKey,callback)      | Initiate DYSDK | secretKey: String, callback: function(state: ExperimentsState) |
| DYReact.identifyUser(userData, callback)      | identify user for cross platform usage | userData: JSONObject, callback: function(err: JSONObject) |



## Author

👤 **DynamicYield**
