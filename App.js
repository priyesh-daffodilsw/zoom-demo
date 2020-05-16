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
} from 'react-native';

import Zoom, {VideoView} from 'react-native-zoom-us';
import {Colors} from 'react-native/Libraries/NewAppScreen';
import Avatar from './Avatar';

const App = () => {
  const [mute, setMute] = useState(false);
  const [activeUser, setActiveUser] = useState();
  const [myUserId, setMyUser] = useState(0);
  const [meetingStartedFlag, setMeetingStartedFlag] = useState(false);
  useEffect(() => {
    Zoom.initialize(
      'C6Psll8UXJrBuARBNWEWURfmRR3wP3RGhqkY',
      '809LSR3uSOiLAfxbFLOpFUDPgHJqk0K9FhoP',
      'zoom.us',
    );
  }, []);

  const _createMeeting = useCallback(async () => {
    try {
      console.warn('Create Meeting: ');
      const result = await Zoom.startMeeting(
        'meetingabhi',
        '9999',
        null, // can be 'null'?
        1, // for pro user use 2
        'eyJ6bV9za20iOiJ6bV9vMm0iLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJjbGllbnQiLCJ1aWQiOiJTdUdld01lS1M3dUdlQ2dmMlhCUFlnIiwiaXNzIjoid2ViIiwic3R5IjoxLCJ3Y2QiOiJhdzEiLCJjbHQiOjAsInN0ayI6Imo4cUVwMFlheUVrT2pmZi11ZjU1TDlYWWhuanp1M0JTOENMNnpQQVJ2RlUuRWdJQUFBRnhGdjFEZ1FBQUhDQWdTV0ZvVWxsbFVHc3ZjVVkzU2pkc2IwWmxkRzlwZDB3eFdHd3JkWG94ZG1NQURETkRRa0YxYjJsWlV6TnpQUU5oZHpFIiwiZXhwIjoxNTg1MjM1ODI4LCJpYXQiOjE1ODUyMjg2MjgsImFpZCI6IllyRFY1VVExUXd5ZXV4ejdJVXNLUHciLCJjaWQiOiIifQ.Qd6F9trNsOW0VlsjoFwY--Y9vyNRVSqwQKLxhAbGEAM', // zak token
        null, // can be 'null'?

        // NOTE: userId, userType, zoomToken should be taken from user hosting this meeting (not sure why it is required)
        // But it works with putting only zoomAccessToken
      );
    } catch (error) {
      console.warn('error : ', error.message);
    }
  }, []);

  const _getInMeetingUserIds = useCallback(async () => {
    let userIds = await Zoom.getInMeetingUserIds();
    return userIds;
  }, []);

  const _joinMeeting = useCallback(async () => {
    try {
      await Zoom.joinMeetingWithPassword('Sharma Ji', '86257382777', '879383'); // pwd - 879383
      setMeetingStartedFlag(true);
      startSlideShow();
      let myUserId = await Zoom.getMyUserId();
      setMyUser(myUserId);
    } catch (e) {
      console.warn(e.code, e.message);
    }
  });

  const startSlideShow = () => {
    let count = 0;
    setInterval(async () => {
      let userIds = await _getInMeetingUserIds();
      if (Array.isArray(userIds)) {
        setActiveUser(userIds[count]);
        count++;
        count %= userIds.length;
      }
    }, 3000);
  };
  const toggleAudio = () => {
    Zoom.toggleAudio();
  };

  const toggleVideo = () => {
    Zoom.toggleVideo();
  };

  const toggleCamera = () => {
    Zoom.toggleCamera();
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
        <View style={{position: 'absolute', top: 10, right: 10}}>
          {!!myUserId && (
            <VideoView
              style={{height: 50, width: 100}}
              attendeeUserId={myUserId}
            />
          )}
        </View>
        <View style={styles.container}>
          {meetingStartedFlag && (
            <VideoView
              style={{height: 400, width: 350}}
              attendeeUserId={activeUser}
            />
          )}
        </View>
        <View style={styles.button}>
          <Button title="Create Meeting" onPress={_createMeeting} />
        </View>

        <View style={styles.button}>
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
        </View>
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
