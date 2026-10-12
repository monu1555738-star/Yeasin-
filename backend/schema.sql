-- ============================================================================
-- B-E-Shell Call: Production Database Schema (PostgreSQL 16)
-- High-performance relational database for users, messaging, calling & stories
-- ============================================================================

-- Enable UUID extension for globally unique IDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. USERS & PROFILES TABLE
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone_number VARCHAR(32) NOT NULL UNIQUE,
    country_code VARCHAR(8) NOT NULL,
    country_name VARCHAR(64) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    avatar_url TEXT,
    bio TEXT DEFAULT 'Hey there! I am using B-E-Shell Call.',
    fcm_token TEXT,
    is_online BOOLEAN DEFAULT FALSE,
    last_seen_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_phone ON users(phone_number);

-- 2. CONTACTS & ROSTER TABLE
CREATE TABLE IF NOT EXISTS contacts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    contact_phone VARCHAR(32) NOT NULL,
    contact_name VARCHAR(100) NOT NULL,
    is_blocked BOOLEAN DEFAULT FALSE,
    is_favorite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, contact_phone)
);

CREATE INDEX idx_contacts_user_id ON contacts(user_id);
CREATE INDEX idx_contacts_blocked ON contacts(user_id, is_blocked);

-- 3. MESSAGES TABLE (Partitionable by date in production)
CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_phone VARCHAR(32) NOT NULL,
    receiver_phone VARCHAR(32) NOT NULL,
    conversation_id VARCHAR(64) NOT NULL,
    content TEXT,
    media_url TEXT,
    media_type VARCHAR(16) DEFAULT 'TEXT' CHECK (media_type IN ('TEXT', 'IMAGE', 'VIDEO', 'AUDIO', 'DOCUMENT')),
    status VARCHAR(16) DEFAULT 'SENT' CHECK (status IN ('SENT', 'DELIVERED', 'READ')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_messages_conversation ON messages(conversation_id, created_at DESC);
CREATE INDEX idx_messages_sender ON messages(sender_phone);
CREATE INDEX idx_messages_receiver ON messages(receiver_phone);

-- 4. CALL LOGS & RECORDS TABLE
CREATE TABLE IF NOT EXISTS call_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    caller_phone VARCHAR(32) NOT NULL,
    caller_name VARCHAR(100) NOT NULL,
    receiver_phone VARCHAR(32) NOT NULL,
    receiver_name VARCHAR(100) NOT NULL,
    call_type VARCHAR(16) NOT NULL CHECK (call_type IN ('AUDIO', 'VIDEO')),
    status VARCHAR(16) NOT NULL CHECK (status IN ('COMPLETED', 'MISSED', 'REJECTED', 'BUSY', 'FAILED')),
    duration_seconds INTEGER DEFAULT 0,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_call_records_caller ON call_records(caller_phone);
CREATE INDEX idx_call_records_receiver ON call_records(receiver_phone);
CREATE INDEX idx_call_records_started_at ON call_records(started_at DESC);

-- 5. STORIES & STATUS TABLE (24-Hour Expiration)
CREATE TABLE IF NOT EXISTS stories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    author_phone VARCHAR(32) NOT NULL,
    author_name VARCHAR(100) NOT NULL,
    content_text TEXT,
    media_url TEXT,
    media_type VARCHAR(16) DEFAULT 'TEXT' CHECK (media_type IN ('TEXT', 'IMAGE', 'VIDEO')),
    bg_color_hex VARCHAR(10) DEFAULT '#0D9488',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE DEFAULT (CURRENT_TIMESTAMP + INTERVAL '24 hours')
);

CREATE INDEX idx_stories_active ON stories(expires_at DESC);
CREATE INDEX idx_stories_author ON stories(author_phone);

-- 6. STORY VIEWS TABLE
CREATE TABLE IF NOT EXISTS story_views (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    story_id UUID NOT NULL REFERENCES stories(id) ON DELETE CASCADE,
    viewer_phone VARCHAR(32) NOT NULL,
    viewer_name VARCHAR(100) NOT NULL,
    viewed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (story_id, viewer_phone)
);

CREATE INDEX idx_story_views_story_id ON story_views(story_id);
