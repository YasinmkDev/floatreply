package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommentAngle
import com.example.model.LinkedInPost
import com.example.model.SentimentAnalysis
import com.example.ui.theme.Hairline
import com.example.ui.theme.Ink
import com.example.ui.theme.InkMuted
import com.example.ui.theme.LinkedInDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun PhoneSimulatorFrame(
  posts: List<LinkedInPost>,
  targetedPostId: String?,
  onPostTargeted: (String) -> Unit,
  onLikeClicked: (String) -> Unit,
  onCommentBoxToggle: (String) -> Unit,
  onDraftCommentChange: (String, String) -> Unit,
  onSubmitComment: (String) -> Unit,
  onOpenAddPostDialog: () -> Unit,
  overlayPosition: Offset,
  onOverlayPositionChanged: (Offset) -> Unit,
  onOverlayDropOrTap: (Int?) -> Unit,
  isOverlayExpanded: Boolean,
  onToggleOverlayExpand: (Boolean) -> Unit,
  sentiment: SentimentAnalysis?,
  postSummary: String,
  generatedComments: Map<CommentAngle, String>,
  isGeneratingAi: Boolean,
  customThought: String,
  onCustomThoughtChanged: (String) -> Unit,
  onAngleSelected: (CommentAngle) -> Unit,
  onRefreshRequested: () -> Unit,
  modifier: Modifier = Modifier
) {
  val targetedPost = posts.find { it.id == targetedPostId }

  // Drop-coordinate targeting: post cards report their window bounds and the
  // bubble reports its own window rect. On drop, the post under the bubble's
  // center wins; otherwise the first post is targeted (previous behavior).
  // Plain holders, not state, so scroll/drag reporting never recomposes.
  val postBounds = remember { mutableMapOf<String, Rect>() }
  val bubbleRectHolder = remember { object { var rect: Rect? = null } }
  val handleBubbleDropOrTap = {
    val center = bubbleRectHolder.rect?.center
    val index = if (center != null) {
      posts.indexOfFirst { post -> postBounds[post.id]?.contains(center) == true }
    } else -1
    if (index >= 0) onOverlayDropOrTap(index) else onOverlayDropOrTap(null)
  }

  // Phone Frame Outer Container
  Box(
    modifier = modifier
      .widthIn(max = 412.dp)
      .shadow(16.dp, RoundedCornerShape(38.dp))
      .border(BorderStroke(2.dp, Color.Black), RoundedCornerShape(38.dp))
      .clip(RoundedCornerShape(38.dp))
      .background(Color.Black)
      .testTag("realistic_smartphone_frame")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(LinkedInDarkBg)
    ) {
      // Phone Status Bar with Notch & System Indicators
      PhoneStatusBar()

      // Phone Viewport Area hosting LinkedIn Feed + Draggable Floating Overlay
      BoxWithConstraints(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        // Drag bounds in px: keep the bubble fully on screen with a small margin.
        val density = LocalDensity.current
        val maxX = with(density) { (maxWidth - 216.dp).toPx().coerceAtLeast(0f) }
        val maxY = with(density) { (maxHeight - 120.dp).toPx().coerceAtLeast(0f) }
        // Mock LinkedIn Feed View
        LinkedInFeedView(
          posts = posts,
          targetedPostId = targetedPostId,
          onPostTargeted = onPostTargeted,
          onLikeClicked = onLikeClicked,
          onCommentBoxToggle = onCommentBoxToggle,
          onDraftCommentChange = onDraftCommentChange,
          onSubmitComment = onSubmitComment,
          onOpenAddPostDialog = onOpenAddPostDialog,
          onPostBoundsChanged = { id, rect -> postBounds[id] = rect },
          modifier = Modifier.fillMaxSize()
        )

        // Floating Overlay Bubble (SYSTEM_ALERT_WINDOW simulation)
        FloatingOverlayBubble(
          position = overlayPosition,
          onPositionChanged = onOverlayPositionChanged,
          onDropOrTap = handleBubbleDropOrTap,
          isExpanded = isOverlayExpanded,
          onToggleExpand = onToggleOverlayExpand,
          targetedPost = targetedPost,
          sentiment = sentiment,
          postSummary = postSummary,
          generatedComments = generatedComments,
          isGeneratingAi = isGeneratingAi,
          customThought = customThought,
          onCustomThoughtChanged = onCustomThoughtChanged,
          onAngleSelected = onAngleSelected,
          onRefreshRequested = onRefreshRequested,
          maxX = maxX,
          maxY = maxY,
          onBubbleBoundsChanged = { rect -> bubbleRectHolder.rect = rect },
          modifier = Modifier
        )
      }

      // Android Bottom Gesture Navigation Bar Pill
      PhoneGestureNavigationBar()
    }
  }
}

@Composable
private fun PhoneStatusBar() {
  Surface(
    color = Color.White,
    modifier = Modifier
      .fillMaxWidth()
      .height(32.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Time
      Text(
        text = "9:41",
        color = TextPrimary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
      )

      // Center Punch-hole selfie camera & speaker
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Speaker slit
        Box(
          modifier = Modifier
            .width(36.dp)
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Hairline)
        )
        // Punch hole camera
        Box(
          modifier = Modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(Color.Black)
            .border(0.5.dp, Hairline, CircleShape)
        )
      }

      // Icons: 5G, Wi-Fi, Battery
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(text = "5G", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Icon(
          imageVector = Icons.Default.Wifi,
          contentDescription = "Wi-Fi",
          tint = TextPrimary,
          modifier = Modifier.size(12.dp)
        )
        Icon(
          imageVector = Icons.Default.BatteryFull,
          contentDescription = "Battery",
          tint = Ink,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}

@Composable
private fun PhoneGestureNavigationBar() {
  Surface(
    color = Color.White,
    modifier = Modifier
      .fillMaxWidth()
      .height(20.dp)
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      // Gesture pill
      Box(
        modifier = Modifier
          .width(80.dp)
          .height(3.5.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(InkMuted)
      )
    }
  }
}
