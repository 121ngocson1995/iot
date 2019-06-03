'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/opened').onWrite((change, context) => {
  // Determine the condition
  if (change.before.val() === "false" && change.after.val() === "true") {
    // Notification details.
    payload = {
      notification: {
        title: 'Someone entered your house!',
        body: 'Please take neccessary actions',
        badge: '1',
        sound: 'default'
      }
    };

    // Send notification to topic
    // It will be sent to all devices subscribing to the same topic
    return admin.messaging().sendToTopic('Alarm', payload).then(response => {
      console.log('Notification success: ', response);
    }).catch(error => {
      console.log('Notification error: ', error);
    });
  }

  return true;
});
