package com.example.model

enum class ToneStyle(val label: String, val badge: String, val guideline: String) {
  DIRECT("Direct", "🎯 Direct", "No fluff, lead with conclusion, zero corporate buzzwords"),
  WITTY("Witty", "😏 Witty", "Clever irony, sharp observation, self-aware startup realism"),
  ACADEMIC("Academic", "🔬 Academic", "First-principles reasoning, structured logic, citations & systems thinking"),
  CASUAL("Casual", "☕ Casual", "Conversational peer-to-peer tone, sounds like a warm DM between founders")
}

data class PersonaConfig(
  val role: String = "B2B SaaS Founder & Seed Investor",
  val tone: ToneStyle = ToneStyle.DIRECT,
  val customRules: String = "Ban buzzwords like 'game-changer' and 'delve'. Cite concrete numbers or trade-offs. Never re-explain what the author wrote.",
  val autoSubmit: Boolean = false,
  val includeSignatureSignoff: Boolean = false
)
