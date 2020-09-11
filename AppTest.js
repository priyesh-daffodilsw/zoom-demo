/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

/* 
  @ReactMethod
  public void muteSelf (boolean mute) {
    try {
      ZoomSDK zoomSDK = ZoomSDK.getInstance();
      if(!zoomSDK.isInitialized()) {
        return;
      }
      final InMeetingService inMeetingService = zoomSDK.getInMeetingService();
      final InMeetingAudioController audioController = inMeetingService.getInMeetingAudioController();
      audioController.muteMyAudio(mute);
    } catch (Exception ex) {
    }
  }
 */

import React, {useCallback, useState, useEffect} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  Button,
  StatusBar,
  AppState,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';
import Avatar from './Avatar';
import {
  Person,
  MessagingStyleNotification,
  getIntentData,
} from './MessagingNotification';
let person = new Person(
  '100',
  'Priyesh',
  'https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/PM_Narendra_Modi.jpg/1200px-PM_Narendra_Modi.jpg',
);
let messagingNotification = new MessagingStyleNotification(person);
let messagingNotificationGroup = new MessagingStyleNotification(person);
let person1 = new Person('200', 'Navin', 'this is icon');
let i = 0;
const App = () => {
  const listener = useCallback(event => {
    if (event && event == 'active') {
      getIntentData()
        .then(res => {
          console.warn(res);
        })
        .catch(err => {
          console.warn(err);
        });
    }
  }, []);
  useEffect(() => {
    AppState.addEventListener('change', listener);
    return () => {
      AppState.removeEventListener('change', listener);
    };
  }, []);
  const _createNotification = () => {
    // console.warn(
    //   'id1: ' +
    //     messagingNotification.id +
    //     ' id2: ' +
    //     messagingNotificationGroup.id,
    // );
    i++;
    let personA;
    if (i % 2 == 0) {
      personA = person;
    } else {
      personA = person1;
    }
    messagingNotification
      .addMessage(i, 'Hi Single: ' + i, new Date().getTime(), personA)
      .setSummary(`${i} unread messages`)
      .setGroupConversation(false);
    messagingNotification.show(100);
  };

  const _createGroupNotification = (notid = 200) => {
    i++;
    let personA;
    if (i % 2 == 0 || true) {
      personA = person;
    } else {
      personA = person1;
    }
    messagingNotificationGroup
      .addMessage(i, 'Hi Group: ' + i, new Date().getTime(), personA)
      .setConversationTitle('Hmara Pyaar Group')
      .setGroupConversation(true)
      .setSummary(`${i} unread messages`)
      .setExtraData({name: 'Priyesh'})
      .setGroupIcon(
        'https://icon2.cleanpng.com/20180330/spw/kisspng-iphone-emoji-apple-ios-11-emojis-5abe1fe31ed9c6.7613688515224094431264.jpg',
      );
    messagingNotificationGroup.show(notid);
  };

  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView style={styles.container}>
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
          }}>
          <Avatar
            name="Priyesh"
            style={{marginRight: 5, backgroundColor: 'red'}}
          />
          <Avatar name="Raghu" style={{marginRight: 5}} />
          <Avatar name="Ismail Khan" style={{marginRight: 5}} />
          <Avatar name="Yamraj" style={{marginRight: 5}} />
          <Avatar name="Emma Watson" style={{marginRight: 5}} />
          <Avatar name="Sunny leone" style={{marginRight: 5}} />
          <Avatar name="Harkishan" style={{marginRight: 5}} />
        </View>
        <View style={styles.button}>
          <Button title="CreateNotification" onPress={_createNotification} />
        </View>
        <View style={styles.button}>
          <Button
            title="Create Gorup Notification"
            onPress={() => _createGroupNotification()}
          />
        </View>
        {/* <View style={styles.button}>
          <Button title="Toggle Audio" onPress={toggleAudio} />
        </View>
        <View style={styles.button}>
          <Button title="Toggle Video" onPress={toggleVideo} />
        </View>
        <View style={styles.button}>
          <Button title="Toggle Camera" onPress={toggleCamera} />
        </View>
        <View style={styles.button}>
          <Button title="Join Meeting" onPress={_joinMeeting} />
        </View> */}
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  button: {
    padding: 20,
  },
  container: {
    justifyContent: 'center',
    alignItems: 'center',
    flex: 1,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
