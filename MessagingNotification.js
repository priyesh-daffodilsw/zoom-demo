import {NativeModules} from 'react-native';
const {MessagingNotification} = NativeModules;
export class Person {
  constructor(key: String, name: String, icon: String) {
    this.key = key;
    this.name = name;
    this.icon = icon;
  }
  toMap = () => ({key: this.key, name: this.name, icon: this.icon});
}

export const getIntentData = async () => MessagingNotification.getIntentData();

export class MessagingStyleNotification {
  constructor(person: Person) {
    this.mPerson = person;
    this.id = new Date().getTime();
    this.messageArray = [];
    this.isGroupNotification = false;
  }

  setGroupConversation(isGroupNotification: Boolean) {
    this.isGroupNotification = isGroupNotification;
    return this;
  }

  addMessage(messageId: Int, text: String, timestamp: Long, person: Person) {
    this.messageArray = [
      ...this.messageArray,
      {
        text,
        messageId,
        timestamp,
        person: person.toMap(),
      },
    ];
    return this;
  }

  getAllMessages() {
    return [...this.messageArray];
  }

  removeMessage(messageId) {
    let index = -1;
    for (let message of this.messageArray) {
      index++;
      if (message.messageId == messageId) {
        break;
      }
    }
    if (index >= 0) {
      this.messageArray.splice(index, 1);
    }
  }

  clearAllMessages() {
    this.messageArray.splice(0, this.messageArray.length);
  }

  setConversationTitle(title: String) {
    this.conversationTitle = title;
    return this;
  }

  show(id = 100) {
    console.warn(id, this.toMap());
    MessagingNotification.createAndNotify(id, this.toMap());
  }

  toMap() {
    let allData = {
      admin: this.mPerson.toMap(),
      messages: this.messageArray,
      conversationTitle: this.conversationTitle,
      isGroupNotification: this.isGroupNotification,
    };
    return allData;
  }

  static cancelNotification(id: Number) {
    MessagingNotification.cancelNotification(id);
  }

  static cancelAllNotifications(id: Number) {
    MessagingNotification.cancelAllNotifications();
  }
}
