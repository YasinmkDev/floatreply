package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommentAngle
import com.example.model.LinkedInPost
import com.example.model.SentimentAnalysis
import com.example.ui.theme.Accent
import com.example.ui.theme.AccentDark
import com.example.ui.theme.AccentTint
import com.example.ui.theme.Hairline
import com.example.ui.theme.Ink
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Success
import com.example.ui.theme.SurfaceTint
import kotlin.math.roundToInt

/**
 * The floating overlay: a white minimal pill that drags 1:1 with the finger
 * and springs open into a white card. Dragging is scoped on purpose — the
 * whole pill drags when collapsed, but only the header row drags when the
 * card is open, so tapping angle chips or typing never moves the card by
 * accident. Dropping the pill (or tapping it) targets the post underneath
 * it; dragging the open card never retargets.
 */
@Composable
fun FloatingOverlayBubble(
  position: Offset,
  onPositionChanged: (Offset) -> Unit,
  onDropOrTap: () -> Unit,
  isExpanded: Boolean,
  onToggleExpand: (Boolean) -> Unit,
  targetedPost: LinkedInPost?,
  sentiment: SentimentAnalysis?,
  postSummary: String,
  generatedComments: Map<CommentAngle, String>,
  isGeneratingAi: Boolean,
  customThought: String,
  onCustomThoughtChanged: (String) -> Unit,
  onAngleSelected: (CommentAngle) -> Unit,
  onRefreshRequested: () -> Unit,
  maxX: Float,
  maxY: Float,
  onBubbleBoundsChanged: (Rect) -> Unit,
  modifier: Modifier = Modifier
) {
  // The drag handler runs inside pointerInput(Unit), which does NOT restart on
  // recomposition — so it must read the latest position/expanded state through
  // refs. (The original code read the captured values directly, which made
  // drags jump back toward the drag start on every move event.)
  val latestPosition by rememberUpdatedState(position)
  val latestExpanded by rememberUpdatedState(isExpanded)

  val dragModifier = Modifier.pointerInput(Unit) {
    detectDragGestures(
      onDragEnd = {
        // Only a pill drop retargets. Dragging the open card just moves it.
        if (!latestExpanded) onDropOrTap()
      }
    ) { change, dragAmount ->
      change.consume()
      val current = latestPosition
      val newX = (current.x + dragAmount.x).coerceIn(0f, maxX)
      val newY = (current.y + dragAmount.y).coerceIn(0f, maxY)
      onPositionChanged(Offset(newX, newY))
    }
  }

  val popIn = scaleIn(
    initialScale = 0.92f,
    animationSpec = spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMedium)
  ) + fadeIn()
  val popOut = scaleOut(
    targetScale = 0.92f,
    animationSpec = spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMedium)
  ) + fadeOut()

  Box(
    modifier = modifier
      .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
      .onGloballyPositioned { coords ->
        val pos = coords.positionInWindow()
        onBubbleBoundsChanged(
          Rect(
            left = pos.x,
            top = pos.y,
            right = pos.x + coords.size.width,
            bottom = pos.y + coords.size.height
          )
        )
      }
      .testTag("floating_overlay_bubble")
  ) {
    AnimatedVisibility(
      visible = !isExpanded,
      enter = popIn,
      exit = popOut
    ) {
      CollapsedPill(
        targetAuthor = targetedPost?.authorName,
        dragModifier = dragModifier,
        onTap = {
          onToggleExpand(true)
          onDropOrTap()
        }
      )
    }

    AnimatedVisibility(
      visible = isExpanded,
      enter = popIn,
      exit = popOut
    ) {
      ExpandedCard(
        dragModifier = dragModifier,
        targetedPost = targetedPost,
        sentiment = sentiment,
        postSummary = postSummary,
        generatedComments = generatedComments,
        isGeneratingAi = isGeneratingAi,
        customThought = customThought,
        onCustomThoughtChanged = onCustomThoughtChanged,
        onAngleSelected = onAngleSelected,
        onRefreshRequested = onRefreshRequested,
        onClose = { onToggleExpand(false) }
      )
    }
  }
}

@Composable
private fun CollapsedPill(
  targetAuthor: String?,
  dragModifier: Modifier,
  onTap: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = Color.White,
    shadowElevation = 6.dp,
    border = BorderStroke(1.dp, Hairline),
    modifier = dragModifier
      .clip(RoundedCornerShape(24.dp))
      .clickable { onTap() }
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = Icons.Default.DragIndicator,
        contentDescription = "Drag overlay",
        tint = InkMuted,
        modifier = Modifier.size(16.dp)
      )

      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(Accent),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(14.dp)
        )
      }

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "FloatReply",
            color = Ink,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.width(4.dp))
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(Success)
          )
        }
        Text(
          text = if (targetAuthor != null) "Locked: $targetAuthor" else "Drop on a post…",
          color = if (targetAuthor != null) AccentDark else InkMuted,
          fontSize = 10.sp,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
private fun ExpandedCard(
  dragModifier: Modifier,
  targetedPost: LinkedInPost?,
  sentiment: SentimentAnalysis?,
  postSummary: String,
  generatedComments: Map<CommentAngle, String>,
  isGeneratingAi: Boolean,
  customThought: String,
  onCustomThoughtChanged: (String) -> Unit,
  onAngleSelected: (CommentAngle) -> Unit,
  onRefreshRequested: () -> Unit,
  onClose: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    shadowElevation = 8.dp,
    border = BorderStroke(1.dp, Hairline),
    modifier = Modifier
      .widthIn(max = 350.dp)
      .testTag("expanded_glassmorphic_hud")
  ) {
    Column(
      modifier = Modifier
        .heightIn(max = 540.dp)
        .verticalScroll(rememberScrollState())
        .padding(12.dp)
    ) {
      // Header doubles as the drag handle — the only draggable part of the card.
      Row(
        modifier = dragModifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = "Drag card",
            tint = InkMuted,
            modifier = Modifier.size(16.dp)
          )
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "FloatReply",
            color = Ink,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SurfaceTint)
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = "OVERLAY",
              color = InkSecondary,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }
        }

        IconButton(
          onClick = onClose,
          modifier = Modifier.size(26.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close card",
            tint = InkMuted,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Targeted post + sentiment strip
      if (targetedPost != null) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceTint)
            .border(BorderStroke(1.dp, Hairline), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Target: ${targetedPost.authorName}",
              color = Ink,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            )
            if (postSummary.isNotBlank()) {
              Text(
                text = postSummary,
                color = InkSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          if (sentiment != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AccentTint)
                .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
              Text(
                text = sentiment.toneBadge,
                color = AccentDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Full sentiment detail — previously computed but never shown.
        if (sentiment != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "${sentiment.keyTheme} · ${(sentiment.sentimentScore * 100).toInt()}% confidence · ${sentiment.engagementPotential} reach · ${sentiment.controversyLevel} controversy",
            color = InkMuted,
            fontSize = 10.sp,
            lineHeight = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Select an angle to inject:",
        color = InkSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
      )

      Spacer(modifier = Modifier.height(6.dp))

      if (isGeneratingAi) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(
            color = Accent,
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Generating angles…",
            color = InkSecondary,
            fontSize = 11.sp
          )
        }
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          CommentAngle.entries.forEach { angle ->
            AngleCard(
              angle = angle,
              commentSnippet = generatedComments[angle] ?: "",
              onClick = { onAngleSelected(angle) }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Custom thought input + refresh
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceTint)
            .border(BorderStroke(1.dp, Hairline), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          var textState by remember(customThought) { mutableStateOf(customThought) }
          androidx.compose.foundation.text.BasicTextField(
            value = textState,
            onValueChange = {
              textState = it
              onCustomThoughtChanged(it)
            },
            textStyle = androidx.compose.ui.text.TextStyle(
              color = Ink,
              fontSize = 11.sp
            ),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("custom_thought_input"),
            decorationBox = { innerTextField ->
              if (textState.isEmpty()) {
                Text(
                  text = "Add a quick thought…",
                  color = InkMuted,
                  fontSize = 11.sp
                )
              }
              innerTextField()
            }
          )
        }

        IconButton(
          onClick = onRefreshRequested,
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, Hairline), RoundedCornerShape(8.dp))
            .testTag("refresh_ai_suggestions_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Regenerate angles",
            tint = Accent,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun AngleCard(
  angle: CommentAngle,
  commentSnippet: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color.White,
    border = BorderStroke(1.dp, Hairline),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable { onClick() }
      .testTag("angle_chip_${angle.name.lowercase()}")
  ) {
    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = angle.chipLabel,
          color = Ink,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
        Text(
          text = "Tap to inject",
          color = Accent,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      if (commentSnippet.isNotBlank()) {
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = commentSnippet,
          color = InkSecondary,
          fontSize = 11.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 15.sp
        )
      }
    }
  }
}
