-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    father_name TEXT,
    dob DATE,
    role TEXT NOT NULL DEFAULT 'स्वयंसेवक',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Organizations table
CREATE TABLE IF NOT EXISTS organizations (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    metro TEXT,
    city TEXT,
    basti TEXT,
    branch TEXT,
    mohalla TEXT
);

-- Attendance table
CREATE TABLE IF NOT EXISTS attendance (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    date DATE DEFAULT CURRENT_DATE,
    status TEXT NOT NULL -- 'PRESENT', 'ABSENT'
);

-- Polls table
CREATE TABLE IF NOT EXISTS polls (
    id SERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    options JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Poll Votes table
CREATE TABLE IF NOT EXISTS poll_votes (
    id SERIAL PRIMARY KEY,
    poll_id INTEGER REFERENCES polls(id),
    user_id UUID REFERENCES users(id),
    selected_option TEXT NOT NULL,
    UNIQUE(poll_id, user_id)
);
