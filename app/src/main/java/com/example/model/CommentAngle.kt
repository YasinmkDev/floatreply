package com.example.model

enum class CommentAngle(val chipLabel: String, val description: String, val emoji: String) {
  CONTRARIAN("Contrarian", "Challenge the core premise respectfully with hard counter-data", "🔥"),
  ADD_EXPERIENCE("Add Experience", "Share a real battle-tested lesson from the trenches", "💡"),
  SMART_QUESTION("Smart Question", "Ask a penetrating second-order question that drives comments", "❓"),
  PUNCHY_ONELINER("Punchy 1-Liner", "High-signal, memorable drop-the-mic insight", "⚡")
}
