import { useRef } from 'react';
import {
  Button,
  FlatList,
  KeyboardAvoidingView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import type {
  BluechatMessage,
  GattConnectionChangeCallback,
} from 'react-native-bluechat';

interface ConnectedAreaProps {
  connectionState: GattConnectionChangeCallback;
  disconnectFromDevice: () => void;
  sendMessage: (value: string) => void;
  messages: BluechatMessage[];
}

export const ConnectedArea = ({
  connectionState,
  disconnectFromDevice,
  messages,
  sendMessage,
}: ConnectedAreaProps) => {
  const inputRef = useRef<TextInput>(null);
  const messageRef = useRef<string>('');

  return (
    <View style={styles.container}>
      <Text>{`You are connected to <${connectionState?.connectedDevice}>`}</Text>
      <Text>{`Your role is <${connectionState.role}>`}</Text>
      <Button
        title="Disconnect from device"
        onPress={() => disconnectFromDevice()}
      />
      <View style={{ flex: 1, justifyContent: 'space-between' }}>
        <FlatList
          data={messages}
          ListHeaderComponent={() => <Text>Messages</Text>}
          contentContainerStyle={{ gap: 8 }}
          renderItem={({ item }) => (
            <Text
              style={[
                styles.text,
                {
                  textAlign: item.direction === 'left' ? 'left' : 'right',
                  backgroundColor:
                    item.direction === 'left' ? 'olive' : 'olivedrab',
                },
              ]}
            >
              {item.value}
            </Text>
          )}
        />
        <KeyboardAvoidingView style={{ flex: 1 }} behavior="padding">
          <View style={styles.inputbar}>
            <TextInput
              ref={inputRef}
              placeholder="Message"
              placeholderTextColor="black"
              onChangeText={(text) => (messageRef.current = text)}
              style={styles.textInput}
            />
            <Button
              title="Send Message"
              onPress={() => {
                sendMessage(messageRef.current);
                inputRef.current?.clear();
                messageRef.current = '';
              }}
            />
          </View>
        </KeyboardAvoidingView>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { paddingVertical: 36, flex: 1 },
  inputbar: {
    gap: 8,
    width: '100%',
    flexDirection: 'row',
    alignItems: 'center',
    padding: 8,
    borderTopWidth: 1,
    borderTopColor: '#eee',
  },
  textInput: {
    color: 'black',
    borderWidth: 1,
    padding: 6,
    flex: 1,
  },
  text: { fontSize: 22, padding: 4, color: 'white' },
});
