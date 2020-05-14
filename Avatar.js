import React from 'react';
import {View, Text} from 'react-native';
const Avatar = ({name, style}) => {
  const letter = name && name.length && name[0];
  return (
    <View
      style={[
        {
          backgroundColor: 'teal',
          justifyContent: 'center',
          alignItems:"center",
          width: 50,
          height: 50,
          borderRadius: 10,
        },
        style,
      ]}>
      <Text style={{fontSize: 25, fontWeight: '700', color: 'white'}}>
        {letter}
      </Text>
    </View>
  );
};
export default Avatar;
