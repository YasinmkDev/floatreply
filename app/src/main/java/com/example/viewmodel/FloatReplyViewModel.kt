package com.example.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CommentAngle
import com.example.model.LinkedInPost
import com.example.model.PersonaConfig
import com.example.model.PostComment
import com.example.model.SentimentAnalysis
import com.example.model.ToneStyle
import com.example.network.GeminiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class SimulatorTab {
  FEED,
  VOICE_CALIBRATION,
  TELEMETRY
}

data class FloatReplyUiState(
  val posts: List<LinkedInPost> = emptyList(),
  val persona: PersonaConfig = PersonaConfig(),
  val targetedPostId: String? = null,
  val overlayPosition: Offset = Offset(260f, 220f),
  val isOverlayHudExpanded: Boolean = false,
  val isGeneratingAi: Boolean = false,
  val activeSentiment: SentimentAnalysis? = null,
  val activePostSummary: String = "",
  val generatedComments: Map<CommentAngle, String> = emptyMap(),
  val customThoughtInput: String = "",
  val activeTab: SimulatorTab = SimulatorTab.FEED,
  val isAutoSubmitEnabled: Boolean = false,
  val simulatedSystemLogs: List<String> = emptyList(),
  val showCustomPostDialog: Boolean = false,
  val statusMessage: String? = null,
  val lastInjectedAngle: CommentAngle? = null
)

class FloatReplyViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(FloatReplyUiState())
  val uiState: StateFlow<FloatReplyUiState> = _uiState.asStateFlow()

  private var typingJob: Job? = null
  private var generationJob: Job? = null

  init {
    initializeSeedPosts()
    logTelemetry("[SYSTEM_ALERT_WINDOW] FloatReply overlay service bound with TYPE_APPLICATION_OVERLAY.")
    logTelemetry("[AccessibilityService] FloatReplyAccessibilityNodeListener started.")
  }

  private fun initializeSeedPosts() {
    val initialPosts = listOf(
      LinkedInPost(
        id = "post_1",
        authorName = "Marcus Vance",
        authorHeadline = "Founder & CEO @ ScaleAgent AI | Ex-YC W21 | Scaling Autonomous B2B Agents",
        authorInitials = "MV",
        avatarBgColor = 0xFF0284C7,
        timeAgo = "2h • 🌐",
        connectionDegree = "1st",
        content = """SDRs are dead. We just replaced our entire outbound sales team with autonomous AI voice & email agents.

Results after 60 days:
• 14,800 accounts touched per week (up 11x)
• 3.8% positive response rate
• $410k added to pipeline at 1/10th the cost

If your company is still paying $80k OTE for junior reps to copy-paste cold emails, you are burning capital on an obsolete model.

The future of B2B sales is algorithmic. Agree?""",
        hashtags = listOf("#SaaS", "#ArtificialIntelligence", "#SalesTech", "#Outbound"),
        likesCount = 1842,
        commentsCount = 428,
        repostsCount = 94,
        comments = listOf(
          PostComment(
            id = "c1",
            authorName = "Sarah Jenkins",
            authorHeadline = "VP Sales @ HyperGrowth",
            authorAvatarInitials = "SJ",
            text = "Interesting metrics Marcus, but what is the retention rate on those deals? Quantity does not equal pipeline quality.",
            timeAgo = "1h",
            likes = 34
          )
        ),
        sentiment = SentimentAnalysis(
          sentimentType = "Controversial / High-Stakes",
          toneBadge = "🔥 Heated Debate",
          sentimentScore = 0.96f,
          keyTheme = "AI Outbound vs Human Relationship Selling",
          engagementPotential = "Very High",
          controversyLevel = "High"
        )
      ),

      LinkedInPost(
        id = "post_2",
        authorName = "Elena Rostova",
        authorHeadline = "VP of Engineering | Ex-Stripe | Distributed Systems & Monolith Advocate",
        authorInitials = "ER",
        avatarBgColor = 0xFF7C3AED,
        timeAgo = "4h • 🌐",
        connectionDegree = "2nd",
        content = """We just dismantled our 45-microservice Kubernetes cluster and collapsed everything back into a single modular Postgres monolith.

Here is what happened:
1. AWS infrastructure bill dropped from $48,000/mo to $17,200/mo.
2. p99 latency went from 340ms to 28ms.
3. Onboarding time for new engineers plummeted from 3 weeks to 2 days.
4. Deployment frequency tripled because developers can run the entire system on a MacBook Air.

Stop splitting your architecture before you have distributed organizational scale. Most teams don't need Kafka and Istio; they need clean database indices.""",
        hashtags = listOf("#SoftwareEngineering", "#Architecture", "#DevOps", "#Cloud"),
        likesCount = 3490,
        commentsCount = 612,
        repostsCount = 285,
        comments = listOf(
          PostComment(
            id = "c2",
            authorName = "Liam O'Connor",
            authorHeadline = "Principal SRE @ FinTech Co",
            authorAvatarInitials = "LO",
            text = "Preach. Microservices became a resume-driven development trap for companies with 15 engineers.",
            timeAgo = "3h",
            likes = 89
          )
        ),
        sentiment = SentimentAnalysis(
          sentimentType = "Technical Architecture Truth",
          toneBadge = "⚡ Monolith vs Microservices",
          sentimentScore = 0.94f,
          keyTheme = "Complexity Tax & Developer Velocity",
          engagementPotential = "High",
          controversyLevel = "Medium"
        )
      ),

      LinkedInPost(
        id = "post_3",
        authorName = "Dev Patel",
        authorHeadline = "Bootstrapping to $3.2M ARR | Solo Founder | Sharing the real numbers",
        authorInitials = "DP",
        avatarBgColor = 0xFF059669,
        timeAgo = "7h • 🌐",
        connectionDegree = "1st",
        content = """Unpopular truth: Bootstrapping is actually lower risk than taking a $5M seed round.

22 months ago I quit my FAANG job with $8,000 in savings. No investors. No fancy pitch deck. Just a Stripe account and a problem I wanted to solve for freelance designers.

Today we hit $270k MRR ($3.2M ARR) at 86% net margin with 4 full-time people.

When you take VC money:
- You must chase a $1B outcome or fail trying.
- You hire before product-market fit to show growth.
- You dilute away your life's work.

When you bootstrap, a $3M business changes your entire family's generational trajectory. Choose your game wisely.""",
        hashtags = listOf("#Startups", "#Bootstrapping", "#SaaS", "#FounderJourney"),
        likesCount = 5210,
        commentsCount = 780,
        repostsCount = 415,
        comments = emptyList(),
        sentiment = SentimentAnalysis(
          sentimentType = "Founder Vulnerability & Growth",
          toneBadge = "📈 Bootstrapped Reality",
          sentimentScore = 0.98f,
          keyTheme = "Cash-Flow Discipline & Product Focus",
          engagementPotential = "Viral",
          controversyLevel = "Low"
        )
      ),

      LinkedInPost(
        id = "post_4",
        authorName = "Dr. Aris Thorne",
        authorHeadline = "Chief AI Scientist @ Frontier Labs | LLM Architectures & Reasoning",
        authorInitials = "AT",
        avatarBgColor = 0xFFEA580C,
        timeAgo = "11h • 🌐",
        connectionDegree = "2nd",
        content = """Stop building brittle Vector RAG pipelines. 2M+ token context windows just made embeddings and chunking obsolete.

Why spend months tuning chunk overlaps, vector databases, and re-ranking models when you can just pass the entire documentation repository into the prompt context?

In our recent evals, pure long-context attention beat chunked vector search in recall accuracy on complex multi-hop reasoning by 31%.

The vector database craze was an architectural stopgap for short context windows. It's time to adapt.""",
        hashtags = listOf("#AI", "#MachineLearning", "#LLM", "#DataScience"),
        likesCount = 2190,
        commentsCount = 540,
        repostsCount = 138,
        comments = emptyList(),
        sentiment = SentimentAnalysis(
          sentimentType = "AI Infrastructure Shift",
          toneBadge = "🧠 Deep Tech Analysis",
          sentimentScore = 0.91f,
          keyTheme = "Token Economics & Needle-in-a-Haystack Retrieval",
          engagementPotential = "High",
          controversyLevel = "High"
        )
      )
    )

    _uiState.update { it.copy(posts = initialPosts) }
    // Pre-target post 1 so HUD is ready to explore immediately
    targetPost("post_1")
  }

  fun updatePersonaRole(newRole: String) {
    _uiState.update { it.copy(persona = it.persona.copy(role = newRole)) }
    refreshAiSuggestions()
  }

  fun updatePersonaTone(newTone: ToneStyle) {
    _uiState.update { it.copy(persona = it.persona.copy(tone = newTone)) }
    refreshAiSuggestions()
  }

  fun updateCustomRules(newRules: String) {
    _uiState.update { it.copy(persona = it.persona.copy(customRules = newRules)) }
  }

  fun toggleAutoSubmit(enabled: Boolean) {
    _uiState.update {
      it.copy(
        isAutoSubmitEnabled = enabled,
        persona = it.persona.copy(autoSubmit = enabled)
      )
    }
    logTelemetry("[Persona] Auto-Submit toggle updated to: $enabled")
  }

  fun updatePersonaSignoff(enabled: Boolean) {
    _uiState.update { it.copy(persona = it.persona.copy(includeSignatureSignoff = enabled)) }
    logTelemetry("[Persona] Role sign-off updated to: $enabled")
  }

  fun updateCustomThought(thought: String) {
    _uiState.update { it.copy(customThoughtInput = thought) }
  }

  fun setActiveTab(tab: SimulatorTab) {
    _uiState.update { it.copy(activeTab = tab) }
  }

  fun setOverlayPosition(newPos: Offset) {
    _uiState.update { it.copy(overlayPosition = newPos) }
  }

  /**
   * Drops overlay onto a post or targets a post by coordinate / tap.
   */
  fun onOverlayDropOrTap(postIndex: Int? = null) {
    val posts = _uiState.value.posts
    val target = if (postIndex != null && postIndex in posts.indices) {
      posts[postIndex]
    } else {
      // Find post or pick first
      posts.firstOrNull()
    }

    if (target != null) {
      targetPost(target.id)
      _uiState.update { it.copy(isOverlayHudExpanded = true) }
      logTelemetry("[AccessibilityService] Target node locked: ${target.authorName}'s post (id=${target.id})")
    }
  }

  fun targetPost(postId: String) {
    val post = _uiState.value.posts.find { it.id == postId } ?: return
    _uiState.update {
      it.copy(
        targetedPostId = postId,
        activeSentiment = post.sentiment
      )
    }
    logTelemetry("[AccessibilityNodeInfo] Bounds detected for post '${post.authorName}' (chars=${post.content.length})")
    fetchAiSuggestionsForPost(post)
  }

  fun toggleOverlayHud(expand: Boolean? = null) {
    _uiState.update { current ->
      val next = expand ?: !current.isOverlayHudExpanded
      current.copy(isOverlayHudExpanded = next)
    }
  }

  fun refreshAiSuggestions() {
    val currentTargetId = _uiState.value.targetedPostId ?: return
    val post = _uiState.value.posts.find { it.id == currentTargetId } ?: return
    fetchAiSuggestionsForPost(post)
  }

  private fun fetchAiSuggestionsForPost(post: LinkedInPost) {
    generationJob?.cancel()
    _uiState.update { it.copy(isGeneratingAi = true) }

    generationJob = viewModelScope.launch {
      val persona = _uiState.value.persona
      val thought = _uiState.value.customThoughtInput

      val bundle = GeminiService.generateAnglesAndSentiment(post, persona, thought)

      _uiState.update {
        it.copy(
          isGeneratingAi = false,
          activeSentiment = bundle.sentiment,
          activePostSummary = bundle.postSummary,
          generatedComments = bundle.commentsByAngle
        )
      }
      logTelemetry("[Gemini API] 4 angle comments generated with persona '${persona.role}' [Tone: ${persona.tone.label}]")
    }
  }

  /**
   * Injects the selected angle comment into the targeted LinkedIn post comment box.
   * Runs typing animation, highlights the Post button, and auto-submits if enabled.
   */
  fun injectComment(angle: CommentAngle) {
    val targetPostId = _uiState.value.targetedPostId ?: return
    val baseText = _uiState.value.generatedComments[angle] ?: return
    val persona = _uiState.value.persona
    // A sign-off lands in the editable draft (never silently posted), so the
    // user can delete it before submitting.
    val commentText = if (persona.includeSignatureSignoff) {
      "$baseText\n— ${persona.role}"
    } else {
      baseText
    }

    typingJob?.cancel()

    // 1. Open the post's comment box and set typing active
    _uiState.update { state ->
      val updatedPosts = state.posts.map { post ->
        if (post.id == targetPostId) {
          post.copy(
            isCommentBoxOpen = true,
            currentDraftComment = "",
            isTypingAnimationActive = true
          )
        } else post
      }
      state.copy(
        posts = updatedPosts,
        lastInjectedAngle = angle,
        statusMessage = "Injecting ${angle.chipLabel} comment..."
      )
    }

    logTelemetry("[Auto-Injection] Opened comment box on post '$targetPostId' for angle ${angle.name}")

    // 2. Animate typing character by character
    typingJob = viewModelScope.launch {
      val chars = commentText.toCharArray()
      val builder = StringBuilder()

      for (i in chars.indices) {
        builder.append(chars[i])
        val partial = builder.toString()
        _uiState.update { state ->
          val updated = state.posts.map { p ->
            if (p.id == targetPostId) p.copy(currentDraftComment = partial) else p
          }
          state.copy(posts = updated)
        }
        delay(12L) // fast realistic typing animation
      }

      // Finish typing animation
      _uiState.update { state ->
        val updated = state.posts.map { p ->
          if (p.id == targetPostId) p.copy(isTypingAnimationActive = false) else p
        }
        state.copy(
          posts = updated,
          statusMessage = if (state.isAutoSubmitEnabled) "Comment typed! Auto-submitting..." else "Comment injected! Ready to post."
        )
      }

      logTelemetry("[Auto-Injection] Finished typing ${chars.size} characters.")

      // 3. Auto-submit if enabled
      if (_uiState.value.isAutoSubmitEnabled) {
        delay(600L)
        submitComment(targetPostId)
      }
    }
  }

  fun submitComment(postId: String) {
    val post = _uiState.value.posts.find { it.id == postId } ?: return
    val textToSubmit = post.currentDraftComment.trim()
    if (textToSubmit.isEmpty()) return

    val persona = _uiState.value.persona
    val newComment = PostComment(
      id = UUID.randomUUID().toString(),
      authorName = "Alex Rivera (You)",
      authorHeadline = persona.role,
      authorAvatarInitials = "AR",
      text = textToSubmit,
      timeAgo = "Just now",
      likes = 1,
      isAiInjected = true
    )

    _uiState.update { state ->
      val updated = state.posts.map { p ->
        if (p.id == postId) {
          p.copy(
            comments = listOf(newComment) + p.comments,
            commentsCount = p.commentsCount + 1,
            currentDraftComment = "",
            isCommentBoxOpen = true
          )
        } else p
      }
      state.copy(
        posts = updated,
        statusMessage = "Comment published to LinkedIn feed!"
      )
    }

    logTelemetry("[LinkedIn Feed] Comment submitted by Alex Rivera [Injected via FloatReply]")
  }

  fun toggleLikePost(postId: String) {
    _uiState.update { state ->
      val updated = state.posts.map { p ->
        if (p.id == postId) {
          val nextLiked = !p.isLiked
          p.copy(
            isLiked = nextLiked,
            likesCount = if (nextLiked) p.likesCount + 1 else p.likesCount - 1
          )
        } else p
      }
      state.copy(posts = updated)
    }
  }

  fun toggleCommentBox(postId: String) {
    _uiState.update { state ->
      val updated = state.posts.map { p ->
        if (p.id == postId) p.copy(isCommentBoxOpen = !p.isCommentBoxOpen) else p
      }
      state.copy(posts = updated)
    }
  }

  fun updateDraftComment(postId: String, newText: String) {
    _uiState.update { state ->
      val updated = state.posts.map { p ->
        if (p.id == postId) p.copy(currentDraftComment = newText) else p
      }
      state.copy(posts = updated)
    }
  }

  fun showAddCustomPostDialog(show: Boolean) {
    _uiState.update { it.copy(showCustomPostDialog = show) }
  }

  fun addCustomPost(authorName: String, headline: String, content: String) {
    val newPost = LinkedInPost(
      id = "custom_" + System.currentTimeMillis(),
      authorName = authorName.ifBlank { "Industry Colleague" },
      authorHeadline = headline.ifBlank { "VP Product & AI Strategy" },
      authorInitials = authorName.take(2).uppercase().ifBlank { "IC" },
      avatarBgColor = 0xFF4F46E5,
      timeAgo = "Just now • 🌐",
      connectionDegree = "1st",
      content = content.ifBlank { "Excited to share our latest product updates..." },
      hashtags = listOf("#ProductStrategy", "#Tech", "#Innovation"),
      likesCount = 42,
      commentsCount = 6,
      repostsCount = 2,
      sentiment = SentimentAnalysis(
        sentimentType = "Custom Post Perspective",
        toneBadge = "💡 Community Post",
        sentimentScore = 0.90f,
        keyTheme = "Custom User-Pasted Post",
        engagementPotential = "High",
        controversyLevel = "Medium"
      )
    )

    _uiState.update { state ->
      state.copy(
        posts = listOf(newPost) + state.posts,
        showCustomPostDialog = false,
        statusMessage = "Custom post added to feed!"
      )
    }

    // Automatically target the new post
    targetPost(newPost.id)
    logTelemetry("[Feed] Custom post inserted: '${newPost.authorName}' (id=${newPost.id})")
  }

  private fun logTelemetry(entry: String) {
    _uiState.update { state ->
      val updated = (listOf(entry) + state.simulatedSystemLogs).take(40)
      state.copy(simulatedSystemLogs = updated)
    }
  }

  fun clearStatusMessage() {
    _uiState.update { it.copy(statusMessage = null) }
  }
}
