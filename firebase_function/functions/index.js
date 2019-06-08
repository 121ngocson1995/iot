'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/opened').onWrite((change, context) => {
  const eventBefore = change.before;
  const eventAfter = change.after;

  // Determine the condition
  if (eventBefore.val() === 'false'
      && eventAfter.val() === 'true') {
    return eventAfter.ref.parent.child('locked').once('value').then(locked => {
      if (locked.val() === 'true') {
        // Notification details.
        const payload = {
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
  }

  return true;
});
