import React, {useState, useCallback, useEffect} from 'react';
import {Text, Button} from 'react-native';
const NewComponent = name => {
  const [age, setAge] = useState(10);
  const onClick = useCallback(() => {}, [age]);
  return <Button onPress={onClick}>Raghu</Button>;
};
export default NewComponent;
