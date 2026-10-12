/**
 * B-E-Shell Call: Alternative Document Database Schema (MongoDB / Mongoose)
 * Schema definitions for users, conversations, calls, and stories
 */

const mongoose = require('mongoose');
const { Schema } = mongoose;

// 1. User & Profile Schema
const UserSchema = new Schema({
  phoneNumber: { type: String, required: true, unique: true, index: true },
  countryCode: { type: String, required: true },
  countryName: { type: String, required: true },
  displayName: { type: String, required: true },
  avatarUrl: { type: String, default: '' },
  bio: { type: String, default: 'Hey there! I am using B-E-Shell Call.' },
  fcmToken: { type: String, default: null },
  isOnline: { type: Boolean, default: false },
  lastSeenAt: { type: Date, default: Date.now },
  blockedNumbers: [{ type: String }],
  createdAt: { type: Date, default: Date.now }
});

// 2. Message Schema
const MessageSchema = new Schema({
  conversationId: { type: String, required: true, index: true },
  senderPhone: { type: String, required: true, index: true },
  receiverPhone: { type: String, required: true, index: true },
  content: { type: String, default: '' },
  mediaUrl: { type: String, default: '' },
  mediaType: { type: String, enum: ['TEXT', 'IMAGE', 'VIDEO', 'AUDIO'], default: 'TEXT' },
  status: { type: String, enum: ['SENT', 'DELIVERED', 'READ'], default: 'SENT' },
  createdAt: { type: Date, default: Date.now, index: true }
});

// 3. Call Record Schema
const CallRecordSchema = new Schema({
  callerPhone: { type: String, required: true, index: true },
  callerName: { type: String, required: true },
  receiverPhone: { type: String, required: true, index: true },
  receiverName: { type: String, required: true },
  callType: { type: String, enum: ['AUDIO', 'VIDEO'], required: true },
  status: { type: String, enum: ['COMPLETED', 'MISSED', 'REJECTED', 'FAILED'], required: true },
  durationSeconds: { type: Number, default: 0 },
  startedAt: { type: Date, default: Date.now, index: true },
  endedAt: { type: Date }
});

// 4. Story Schema with MongoDB 24-hour TTL index
const StorySchema = new Schema({
  authorPhone: { type: String, required: true, index: true },
  authorName: { type: String, required: true },
  contentText: { type: String, default: '' },
  mediaUrl: { type: String, default: '' },
  mediaType: { type: String, enum: ['TEXT', 'IMAGE', 'VIDEO'], default: 'TEXT' },
  bgColorHex: { type: String, default: '#0D9488' },
  createdAt: { type: Date, default: Date.now },
  // MongoDB automatically purges documents after 86400 seconds (24h)
  expireAt: { type: Date, default: () => new Date(Date.now() + 24 * 60 * 60 * 1000), index: { expires: 0 } },
  viewers: [{
    viewerPhone: String,
    viewerName: String,
    viewedAt: { type: Date, default: Date.now }
  }]
});

module.exports = {
  User: mongoose.model('User', UserSchema),
  Message: mongoose.model('Message', MessageSchema),
  CallRecord: mongoose.model('CallRecord', CallRecordSchema),
  Story: mongoose.model('Story', StorySchema)
};
