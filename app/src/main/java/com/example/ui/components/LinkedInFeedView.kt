package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LinkedInPost
import com.example.model.PostComment
import com.example.ui.theme.AccentDark
import com.example.ui.theme.AccentTint
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.Hairline
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.LinkedInBlue
import com.example.ui.theme.LinkedInBorder
import com.example.ui.theme.LinkedInCardBg
import com.example.ui.theme.LinkedInDarkBg
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.SurfaceTint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LinkedInFeedView(
  posts: List<LinkedInPost>,
  targetedPostId: String?,
  onPostTargeted: (String) -> Unit,
  onLikeClicked: (String) -> Unit,
  onCommentBoxToggle: (String) -> Unit,
  onDraftCommentChange: (String, String) -> Unit,
  onSubmitComment: (String) -> Unit,
  onOpenAddPostDialog: () -> Unit,
  onPostBoundsChanged: (String, Rect) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(LinkedInDarkBg)
  ) {
    // Top LinkedIn Mobile Navigation Bar
    LinkedInTopBar(onOpenAddPostDialog = onOpenAddPostDialog)

    // Scrollable Feed
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("linkedin_feed_list"),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Quick Banner to paste user custom post
      item {
        QuickPasteBanner(onOpenAddPostDialog = onOpenAddPostDialog)
      }

      items(posts, key = { it.id }) { post ->
        val isTargeted = post.id == targetedPostId
        LinkedInPostCard(
          post = post,
          isTargeted = isTargeted,
          onTargetClicked = { onPostTargeted(post.id) },
          onLikeClicked = { onLikeClicked(post.id) },
          onCommentBoxToggle = { onCommentBoxToggle(post.id) },
          onDraftCommentChange = { onDraftCommentChange(post.id, it) },
          onSubmitComment = { onSubmitComment(post.id) },
          onBoundsChanged = { rect -> onPostBoundsChanged(post.id, rect) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(60.dp))
      }
    }
  }
}

@Composable
private fun LinkedInTopBar(onOpenAddPostDialog: () -> Unit) {
  Surface(
    color = LinkedInCardBg,
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // LinkedIn "in" logo
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(LinkedInBlue),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "in",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
      }

      // Search bar
      Box(
        modifier = Modifier
          .weight(1f)
          .height(34.dp)
          .clip(RoundedCornerShape(17.dp))
          .background(AccentTint)
          .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "Search posts, jobs...",
            color = TextMuted,
            fontSize = 12.sp
          )
        }
      }

      // Add custom post button
      IconButton(
        onClick = onOpenAddPostDialog,
        modifier = Modifier
          .size(34.dp)
          .testTag("add_custom_post_icon_button")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add custom post",
          tint = CyanGlow
        )
      }
    }
  }
}

@Composable
private fun QuickPasteBanner(onOpenAddPostDialog: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(AccentTint)
      .border(BorderStroke(1.dp, Hairline), RoundedCornerShape(10.dp))
      .clickable { onOpenAddPostDialog() }
      .padding(horizontal = 12.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(CyanPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
        Column {
          Text(
            text = "Test FloatReply on your own post",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "Tap here to paste real LinkedIn content",
            color = CyanGlow,
            fontSize = 11.sp
          )
        }
      }
      Text(
        text = "+ Paste",
        color = CyanPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }
  }
}

@Composable
fun LinkedInPostCard(
  post: LinkedInPost,
  isTargeted: Boolean,
  onTargetClicked: () -> Unit,
  onLikeClicked: () -> Unit,
  onCommentBoxToggle: () -> Unit,
  onDraftCommentChange: (String) -> Unit,
  onSubmitComment: () -> Unit,
  onBoundsChanged: (Rect) -> Unit
) {
  // Targeted cards get a static blue border — no pulsing glow.
  val borderModifier = if (isTargeted) {
    Modifier.border(
      BorderStroke(1.5.dp, CyanPrimary),
      RoundedCornerShape(12.dp)
    )
  } else {
    Modifier.border(
      BorderStroke(1.dp, LinkedInBorder),
      RoundedCornerShape(12.dp)
    )
  }

  Surface(
    color = LinkedInCardBg,
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 10.dp, vertical = 4.dp)
      .then(borderModifier)
      .onGloballyPositioned { coords ->
        val pos = coords.positionInWindow()
        onBoundsChanged(
          Rect(
            left = pos.x,
            top = pos.y,
            right = pos.x + coords.size.width,
            bottom = pos.y + coords.size.height
          )
        )
      }
      .testTag("linkedin_post_card_${post.id}")
      .clickable { onTargetClicked() }
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Target bounds indicator tag for simulated AccessibilityNodeInfo
      if (isTargeted) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(AccentTint)
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(CyanGlow)
            )
            Text(
              text = "ACCESSIBILITY_BOUNDS_LOCKED [Node #${post.id}]",
              color = CyanGlow,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }

          Text(
            text = post.sentiment.toneBadge,
            color = AccentDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Author Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Avatar circle
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color(post.avatarBgColor)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = post.authorInitials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = post.authorName,
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = " • ${post.connectionDegree}",
              color = TextMuted,
              fontSize = 12.sp
            )
          }
          Text(
            text = post.authorHeadline,
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = post.timeAgo,
            color = TextMuted,
            fontSize = 10.sp
          )
        }

        // Quick drop target icon button
        IconButton(
          onClick = onTargetClicked,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.GpsFixed,
            contentDescription = "Target this post",
            tint = if (isTargeted) CyanGlow else TextMuted,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Post Content
      Text(
        text = post.content,
        color = TextPrimary,
        fontSize = 13.sp,
        lineHeight = 18.sp
      )

      if (post.hashtags.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = post.hashtags.joinToString(" "),
          color = LinkedInBlue,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Reactions & Comments counts
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(text = "👍💡❤️", fontSize = 11.sp)
          Text(
            text = "${post.likesCount}",
            color = TextMuted,
            fontSize = 11.sp
          )
        }
        Text(
          text = "${post.commentsCount} comments • ${post.repostsCount} reposts",
          color = TextMuted,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = LinkedInBorder, thickness = 0.5.dp)
      Spacer(modifier = Modifier.height(4.dp))

      // Action Bar: Like, Comment, Repost, Send
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        ActionButton(
          icon = if (post.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
          label = "Like",
          tint = if (post.isLiked) LinkedInBlue else TextSecondary,
          onClick = onLikeClicked
        )
        ActionButton(
          icon = Icons.Default.ChatBubbleOutline,
          label = "Comment",
          tint = if (post.isCommentBoxOpen) CyanGlow else TextSecondary,
          onClick = onCommentBoxToggle
        )
        ActionButton(
          icon = Icons.Default.Repeat,
          label = "Repost",
          tint = TextSecondary,
          onClick = {}
        )
        ActionButton(
          icon = Icons.AutoMirrored.Filled.Send,
          label = "Send",
          tint = TextSecondary,
          onClick = {}
        )
      }

      // Expandable Comment Section
      AnimatedVisibility(
        visible = post.isCommentBoxOpen,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          HorizontalDivider(color = LinkedInBorder, thickness = 0.5.dp)
          Spacer(modifier = Modifier.height(10.dp))

          // Comment Input Box with live auto-injection typing support
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(IndigoAccent),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "AR",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .border(
                    BorderStroke(
                      if (post.isTypingAnimationActive) 1.5.dp else 1.dp,
                      if (post.isTypingAnimationActive) CyanGlow else DarkSurfaceBorder
                    ),
                    RoundedCornerShape(8.dp)
                  )
                  .background(DarkSurface)
                  .padding(8.dp)
              ) {
                if (post.currentDraftComment.isEmpty() && !post.isTypingAnimationActive) {
                  Text(
                    text = "Add a comment as Alex Rivera...",
                    color = TextMuted,
                    fontSize = 12.sp
                  )
                } else {
                  Row {
                    Text(
                      text = post.currentDraftComment,
                      color = TextPrimary,
                      fontSize = 12.sp,
                      lineHeight = 16.sp
                    )
                    if (post.isTypingAnimationActive) {
                      Text(
                        text = "▌",
                        color = CyanGlow,
                        fontSize = 12.sp
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (post.isTypingAnimationActive) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(CyanGlow)
                    )
                    Text(
                      text = "FloatReply Typing...",
                      color = CyanGlow,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                } else {
                  Text(
                    text = "Use FloatReply overlay to auto-type",
                    color = TextMuted,
                    fontSize = 10.sp
                  )
                }

                Button(
                  onClick = onSubmitComment,
                  enabled = post.currentDraftComment.isNotBlank(),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = LinkedInBlue,
                    contentColor = Color.White
                  ),
                  shape = RoundedCornerShape(16.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 14.dp,
                    vertical = 4.dp
                  ),
                  modifier = Modifier
                    .height(28.dp)
                    .testTag("post_comment_button_${post.id}")
                ) {
                  Text("Post", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }

          // Existing Comments
          if (post.comments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Comments (${post.comments.size})",
              color = TextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            post.comments.forEach { comment ->
              CommentRow(comment = comment)
              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CommentRow(comment: PostComment) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(if (comment.isAiInjected) CyanPrimary else Color(0xFF475569)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = comment.authorAvatarInitials,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      )
    }

    Column(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(8.dp))
        .background(SurfaceTint)
        .padding(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = comment.authorName,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          Text(
            text = comment.authorHeadline,
            color = TextMuted,
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        if (comment.isAiInjected) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(AccentTint)
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ElectricBolt,
              contentDescription = null,
              tint = CyanGlow,
              modifier = Modifier.size(10.dp)
            )
            Text(
              text = "FloatReply",
              color = CyanGlow,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        } else {
          Text(text = comment.timeAgo, color = TextMuted, fontSize = 9.sp)
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = comment.text,
        color = TextPrimary,
        fontSize = 11.sp,
        lineHeight = 15.sp
      )
    }
  }
}

@Composable
private fun ActionButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  tint: Color,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = tint,
      modifier = Modifier.size(16.dp)
    )
    Text(
      text = label,
      color = tint,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium
    )
  }
}
