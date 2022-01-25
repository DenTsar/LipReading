# Learning to Read Lips App

An app to practice lip reading from snippets of Youtube videos.

## Overview
- Used Youtube API to get captions from [vlogbrothers](https://www.youtube.com/vlogbrothers) videos and added them to Firebase
- User is shown random clips from databse without sound and types what they think was said
- After successfully answering or giving up, the clip is replayed with sound and the caption is revealed
- Option to report an issue with the video clip (improperly timed, face not shown, etc.)
- Reports get sent to Analytics so a change can be made in the databse
- Score is tracked and saved upon closing the app

## Technologies
- [YouTube Android Player API](https://developers.google.com/youtube/android/player)
- [Google Cloud Firestore](https://firebase.google.com/docs/firestore)
- [Google Analytics for Firebase](https://firebase.google.com/docs/analytics)

## Demo
<img src="lipreadingapp-gif.gif" width="20%" height="20%"/>
