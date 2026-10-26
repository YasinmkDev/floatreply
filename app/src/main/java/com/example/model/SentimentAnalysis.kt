package com.example.model

data class SentimentAnalysis(
  val sentimentType: String,
  val toneBadge: String,
  val sentimentScore: Float, // 0.0 to 1.0
  val keyTheme: String,
  val engagementPotential: String,
  val controversyLevel: String
)
