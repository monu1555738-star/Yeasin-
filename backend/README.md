# B-E-Shell Call: Backend Infrastructure & Architecture Guide

This document describes the backend architecture, real-time communication stack, database schema, and notification dispatch services built for **B-E-Shell Call**.

---

## 1. Chosen Technologies Overview

| Layer | Chosen Technology | Rationale |
|---|---|---|
| **Real-time Messaging** | **WebSockets (`ws` / RFC 6455)** | Full-duplex bidirectional communication with low latency (<50ms) for chat bubbles, typing status, and read receipts. |
| **Call Signaling** | **WebRTC Signaling Protocol over WebSockets** | Manages `call_offer`, `call_answer`, and `ice_candidate` SDP negotiation for end-to-end P2P encrypted audio/video calling. |
| **Primary Relational Database** | **PostgreSQL 16** | ACID compliance, strong relational integrity for user accounts, phone verification, partitioned message histories, and call logs. |
| **Document Database Alternative** | **MongoDB / Mongoose** | Document-oriented alternative with native 24-hour TTL indexes (`expireAfterSeconds`) for ephemeral Stories. |
| **Local App Cache** | **Room Database (SQLite on Android)** | Offline-first instant rendering and seamless syncing when connectivity drops. |
| **Push Notifications** | **Firebase Cloud Messaging (FCM v1)** | Dispatches high-priority VoIP alerts and message previews to wake up Android devices when the app is backgrounded or killed. |
| **SMS OTP Verification** | **Twilio Verify / Firebase Phone Auth** | Global carrier delivery across 190+ countries with automated replay prevention. |

---

## 2. WebRTC Audio & Video Signaling Architecture

When a user initiates an audio or video call on **B-E-Shell Call**:

```
[Caller Android App]                [Signaling Server: ws://]               [Recipient Android App]
         |                                     |                                      |
         | --- (1) { type: 'call_offer' } ---> |                                      |
         |                                     | --- (2) { type: 'call_offer' } ----> |
         |                                     |                                      |
         |                                     | <--- (3) { type: 'call_answer' } --- |
         | <--- (4) { type: 'call_answer' } -- |                                      |
         |                                     |                                      |
         | <============ (5) P2P WebRTC Media Stream (SRTP/DTLS) ============> |
```

1. **Offer Generation:** The caller creates an SDP session description (`callType: "AUDIO"` or `"VIDEO"`) and sends it over WebSocket.
2. **Offer Forwarding:** The server checks if the recipient is connected.
   - If **Online**: Forwards the offer instantly to the recipient.
   - If **Offline**: Automatically triggers an FCM high-priority VoIP push notification to ring the recipient's phone in the background.
3. **Answer Generation:** Upon the recipient tapping **Accept**, an SDP answer is sent back.
4. **ICE Candidate Exchange:** Both devices exchange STUN/TURN ICE candidates to traverse NAT/firewalls.
5. **Direct Media Stream:** Audio/video streams directly peer-to-peer using hardware-accelerated Opus (Audio) and VP8/H.264 (Video) codecs.

---

## 3. Database Schema Design

### PostgreSQL 16 (`backend/schema.sql`)
- `users`: Stores verified phone number, dial code, display name, avatar, bio, and FCM token.
- `contacts`: User contact lists with block status (`is_blocked`), favorites, and custom labels.
- `messages`: Chronological conversation logs with media URLs, delivery statuses (`SENT`, `DELIVERED`, `READ`).
- `call_records`: Detailed call history with direction (`INCOMING`, `OUTGOING`, `MISSED`, `REJECTED`), duration in seconds, and timestamps.
- `stories`: 24-hour status updates with viewer counts and viewer lists.

### MongoDB Mongoose Models (`backend/mongodb_schema.js`)
- Includes TTL collection for automatic 24-hour expiration of Stories without needing cron jobs.

---

## 4. Running the Backend Service

```bash
cd backend
npm install
npm start
```

The server will bind to port `4000`:
- **REST API:** `http://localhost:4000/api`
- **WebSocket Gateway:** `ws://localhost:4000/ws`
