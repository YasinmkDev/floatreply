package com.example.model

import androidx.compose.ui.graphics.Color

data class PostComment(
  val id: String,
  val authorName: String,
  val authorHeadline: String,
  val authorAvatarInitials: String,
  val text: String,
  val timeAgo: String,
  val likes: Int = 0,
  val isAiInjected: Boolean = false
)

data class LinkedInPost(
  val id: String,
  val authorName: String,
  val authorHeadline: String,
  val authorInitials: String,
  val avatarBgColor: Long = 0xFF0284C7,
  val timeAgo: String,
  val connectionDegree: String = "1st",
  val content: String,
  val hashtags: List<String> = emptyList(),
  val likesCount: Int,
  val commentsCount: Int,
  val repostsCount: Int,
  val isLiked: Boolean = false,
  val comments: List<PostComment> = emptyList(),
  val isCommentBoxOpen: Boolean = false,
  val currentDraftComment: String = "",
  val isTypingAnimationActive: Boolean = false,
  val sentiment: SentimentAnalysis
)
