const {onRequest} = require("firebase-functions/v2/https");
const {onCall} = require("firebase-functions/v2/https");
const {onDocumentWritten} = require("firebase-functions/v2/firestore");
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const logger = require("firebase-functions/logger");

admin.initializeApp();

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

exports.deleteOldSessions = functions.pubsub.schedule('every 5 minutes').onRun(async (context) => {
    const now = Date.now();
    const cutoff = now - 5 * 60 * 1000; // 5 minutes in milliseconds
    const ref = admin.database().ref('qrCodes');
    const oldItemsQuery = ref.orderByChild('timestamp').endAt(cutoff);

    const snapshot = await oldItemsQuery.once('value');
    const updates = {};
    snapshot.forEach(child => {
        if (child.val().status === 'waiting_for_login') {
            updates[child.key] = null;
        }
    });

    return ref.update(updates);
});