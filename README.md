#  AIChatBot — Android LLM Chatbot App

An AI-powered Android chat application built with Java that integrates the **Groq API** to deliver fast, intelligent conversations. Users authenticate with a username and interact with an LLM (Large Language Model) in a real-time messaging interface. Chat history is persisted locally using **Room (SQLite)** so conversations are saved across sessions.

---

##  Features

- **User Authentication** — Simple username-based login screen to identify users
- **AI-Powered Chat** — Real-time conversations powered by Groq's `llama-3.3-70b-versatile` model
- **Persistent Chat History** — Previous conversations are saved locally using Room database and restored on next login
- **Timestamped Messages** — Every message bubble displays the time it was sent
- **Clean Messaging UI** — WhatsApp-style chat interface with user messages on the right (blue) and bot responses on the left (grey)

---

## 🛠️ Tech Stack

| Component | Technology |
|---|---|
| Language | Java |
| Minimum SDK | API 24 (Android 7.0) |
| Target SDK | API 36 |
| AI Backend | Groq API (`llama-3.3-70b-versatile`) |
| Networking | Retrofit2 + OkHttp3 |
| Local Database | Room (SQLite) |
| Architecture | MVC |

---

##  Project Structure

```
app/
├── java/com/keerthana/aichatbot/
│   ├── database/
│   │   ├── Message.java          # Room entity for chat messages
│   │   ├── MessageDao.java       # Database access operations
│   │   └── ChatDatabase.java     # Room database singleton
│   ├── network/
│   │   ├── ChatRequest.java      # Groq API request model
│   │   ├── ChatResponse.java     # Groq API response model
│   │   └── GroqApiService.java   # Retrofit API interface
│   ├── adapter/
│   │   └── MessageAdapter.java   # RecyclerView adapter for chat bubbles
│   ├── LoginActivity.java        # Username entry screen
│   └── ChatActivity.java         # Main chat screen
├── res/
│   ├── layout/
│   │   ├── activity_login.xml    # Login screen layout
│   │   ├── activity_chat.xml     # Chat screen layout
│   │   ├── item_message_user.xml # User message bubble
│   │   └── item_message_bot.xml  # Bot message bubble
```

---

##  Getting Started

### Prerequisites

- Android Studio (latest stable version)
- Android device or emulator running API 24+
- A free [Groq API key](https://console.groq.com/)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/AIChatBot.git
   cd AIChatBot
   ```

2. **Add your Groq API key**

   Open `gradle.properties` in the root of the project and add:
   ```properties
   GROQ_API_KEY=Bearer your_actual_groq_api_key_here
   ```
   >  `gradle.properties` is listed in `.gitignore` and will never be committed. Never share your API key publicly.

3. **Open in Android Studio**
   - File → Open → select the cloned project folder
   - Wait for Gradle sync to complete

4. **Run the app**
   - Connect a physical device via USB with USB Debugging enabled, or start an emulator
   - Click the **Run ▶** button in Android Studio

---

## 🖥️ How to Use

1. Launch the app — you'll see the **"Welcome, Lets Chat!"** login screen
2. Enter any username and tap **Go**
3. You'll be taken to the chat screen showing **Welcome [username]!**
4. Type a message in the input field and tap the send button
5. The AI will respond within seconds
6. Close and reopen the app, log in with the **same username** — your previous conversation will be restored

---

## ⚙️ How It Works

### Authentication
The login screen captures a username which is passed to the chat screen via `Intent`. This username is used as a key to store and retrieve conversation history from the local Room database.

### Networking
Retrofit2 makes a POST request to Groq's OpenAI-compatible endpoint:
```
POST https://api.groq.com/openai/v1/chat/completions
```
The full conversation history is sent with each request so the model maintains context across the conversation.

### Chat History
Every message (user and bot) is saved to a Room database with the username, message text, sender type, and timestamp. On login, messages for that username are loaded from the database and displayed in order.

### Message Bubbles
A `RecyclerView` with two view types (`VIEW_TYPE_USER` and `VIEW_TYPE_BOT`) renders messages differently based on the sender field, aligning user messages to the right and bot messages to the left.

---

##  Requirements Met

-  User authentication screen with username input
-  Chat interface with send/receive functionality
-  UI layout matching the provided wireframe
-  Chat history persisted using Room (SQLite)
-  Timestamps displayed on each message bubble

---

##  Security Notes

- The Groq API key is stored in `gradle.properties` which is excluded from version control via `.gitignore`
- The API key is accessed at build time via `BuildConfig.GROQ_API_KEY` and never hardcoded in source files

---
