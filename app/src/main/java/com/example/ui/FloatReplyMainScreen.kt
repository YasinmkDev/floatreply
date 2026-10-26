package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CustomPostDialog
import com.example.ui.components.PersonaCalibrationSidebar
import com.example.ui.components.PhoneSimulatorFrame
import com.example.ui.theme.Accent
import com.example.ui.theme.AccentDark
import com.example.ui.theme.AccentTint
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.SuccessTint
import com.example.ui.theme.SurfaceTint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.FloatReplyViewModel
import com.example.viewmodel.SimulatorTab

@Composable
fun FloatReplyMainScreen(
  viewModel: FloatReplyViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.statusMessage) {
    uiState.statusMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearStatusMessage()
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground),
    containerColor = DarkBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopSimulatorBar(
        activeRole = uiState.persona.role,
        activeTone = uiState.persona.tone.label,
        autoSubmit = uiState.isAutoSubmitEnabled
      )
    }
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      val isWideScreen = maxWidth > 760.dp

      if (isWideScreen) {
        // Desktop / Tablet Landscape: Split Layout (Left Sidebar + Centered Phone Simulator)
        Row(modifier = Modifier.fillMaxSize()) {
          // Left Sidebar: Voice & Persona Calibration
          PersonaCalibrationSidebar(
            persona = uiState.persona,
            isAutoSubmitEnabled = uiState.isAutoSubmitEnabled,
            onRoleChanged = { viewModel.updatePersonaRole(it) },
            onToneChanged = { viewModel.updatePersonaTone(it) },
            onCustomRulesChanged = { viewModel.updateCustomRules(it) },
            onAutoSubmitToggled = { viewModel.toggleAutoSubmit(it) },
            onSignoffToggled = { viewModel.updatePersonaSignoff(it) },
            onRegenerateSuggestions = { viewModel.refreshAiSuggestions() },
            telemetryLogs = uiState.simulatedSystemLogs,
            modifier = Modifier
              .width(360.dp)
              .fillMaxHeight()
              .border(0.5.dp, DarkSurfaceBorder)
          )

          // Right Area: Realistic Smartphone Frame centered
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            PhoneSimulatorFrame(
              posts = uiState.posts,
              targetedPostId = uiState.targetedPostId,
              onPostTargeted = { viewModel.targetPost(it) },
              onLikeClicked = { viewModel.toggleLikePost(it) },
              onCommentBoxToggle = { viewModel.toggleCommentBox(it) },
              onDraftCommentChange = { id, text -> viewModel.updateDraftComment(id, text) },
              onSubmitComment = { viewModel.submitComment(it) },
              onOpenAddPostDialog = { viewModel.showAddCustomPostDialog(true) },
              overlayPosition = uiState.overlayPosition,
              onOverlayPositionChanged = { viewModel.setOverlayPosition(it) },
              onOverlayDropOrTap = { viewModel.onOverlayDropOrTap(it) },
              isOverlayExpanded = uiState.isOverlayHudExpanded,
              onToggleOverlayExpand = { viewModel.toggleOverlayHud(it) },
              sentiment = uiState.activeSentiment,
              postSummary = uiState.activePostSummary,
              generatedComments = uiState.generatedComments,
              isGeneratingAi = uiState.isGeneratingAi,
              customThought = uiState.customThoughtInput,
              onCustomThoughtChanged = { viewModel.updateCustomThought(it) },
              onAngleSelected = { viewModel.injectComment(it) },
              onRefreshRequested = { viewModel.refreshAiSuggestions() },
              modifier = Modifier.fillMaxHeight()
            )
          }
        }
      } else {
        // Handheld / Compact Screen: Tab Switcher or Dual Pane
        Column(modifier = Modifier.fillMaxSize()) {
          // Mobile Segmented Tab Row
          val tabIndex = when (uiState.activeTab) {
            SimulatorTab.FEED -> 0
            SimulatorTab.VOICE_CALIBRATION -> 1
            SimulatorTab.TELEMETRY -> 2
          }
          TabRow(
            selectedTabIndex = tabIndex,
            containerColor = DarkSurface,
            contentColor = CyanPrimary,
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                color = CyanPrimary,
                height = 2.dp
              )
            }
          ) {
            Tab(
              selected = uiState.activeTab == SimulatorTab.FEED,
              onClick = { viewModel.setActiveTab(SimulatorTab.FEED) },
              text = {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Text("Phone Simulator", fontWeight = FontWeight.Bold)
                }
              },
              modifier = Modifier.testTag("tab_phone_simulator")
            )
            Tab(
              selected = uiState.activeTab == SimulatorTab.VOICE_CALIBRATION,
              onClick = { viewModel.setActiveTab(SimulatorTab.VOICE_CALIBRATION) },
              text = {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Text("Voice & Persona", fontWeight = FontWeight.Bold)
                }
              },
              modifier = Modifier.testTag("tab_voice_calibration")
            )
            Tab(
              selected = uiState.activeTab == SimulatorTab.TELEMETRY,
              onClick = { viewModel.setActiveTab(SimulatorTab.TELEMETRY) },
              text = {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Text("Telemetry", fontWeight = FontWeight.Bold)
                }
              },
              modifier = Modifier.testTag("tab_telemetry")
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
          ) {
            when (uiState.activeTab) {
              SimulatorTab.FEED -> {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                PhoneSimulatorFrame(
                  posts = uiState.posts,
                  targetedPostId = uiState.targetedPostId,
                  onPostTargeted = { viewModel.targetPost(it) },
                  onLikeClicked = { viewModel.toggleLikePost(it) },
                  onCommentBoxToggle = { viewModel.toggleCommentBox(it) },
                  onDraftCommentChange = { id, text -> viewModel.updateDraftComment(id, text) },
                  onSubmitComment = { viewModel.submitComment(it) },
                  onOpenAddPostDialog = { viewModel.showAddCustomPostDialog(true) },
                  overlayPosition = uiState.overlayPosition,
                  onOverlayPositionChanged = { viewModel.setOverlayPosition(it) },
                  onOverlayDropOrTap = { viewModel.onOverlayDropOrTap(it) },
                  isOverlayExpanded = uiState.isOverlayHudExpanded,
                  onToggleOverlayExpand = { viewModel.toggleOverlayHud(it) },
                  sentiment = uiState.activeSentiment,
                  postSummary = uiState.activePostSummary,
                  generatedComments = uiState.generatedComments,
                  isGeneratingAi = uiState.isGeneratingAi,
                  customThought = uiState.customThoughtInput,
                  onCustomThoughtChanged = { viewModel.updateCustomThought(it) },
                  onAngleSelected = { viewModel.injectComment(it) },
                  onRefreshRequested = { viewModel.refreshAiSuggestions() },
                  modifier = Modifier.fillMaxSize()
                )
              }
              }
              SimulatorTab.VOICE_CALIBRATION -> {
              PersonaCalibrationSidebar(
                persona = uiState.persona,
                isAutoSubmitEnabled = uiState.isAutoSubmitEnabled,
                onRoleChanged = { viewModel.updatePersonaRole(it) },
                onToneChanged = { viewModel.updatePersonaTone(it) },
                onCustomRulesChanged = { viewModel.updateCustomRules(it) },
                onAutoSubmitToggled = { viewModel.toggleAutoSubmit(it) },
                onSignoffToggled = { viewModel.updatePersonaSignoff(it) },
                onRegenerateSuggestions = {
                  viewModel.refreshAiSuggestions()
                  viewModel.setActiveTab(SimulatorTab.FEED)
                },
                telemetryLogs = uiState.simulatedSystemLogs,
                modifier = Modifier.fillMaxSize()
              )
              }
              SimulatorTab.TELEMETRY -> {
                TelemetryScreen(
                  logs = uiState.simulatedSystemLogs,
                  modifier = Modifier.fillMaxSize()
                )
              }
            }
          }
        }
      }
    }

    // Modal Dialog to paste custom LinkedIn Post
    if (uiState.showCustomPostDialog) {
      CustomPostDialog(
        onDismiss = { viewModel.showAddCustomPostDialog(false) },
        onSubmit = { author, headline, content ->
          viewModel.addCustomPost(author, headline, content)
        }
      )
    }
  }
}

@Composable
private fun TopSimulatorBar(
  activeRole: String,
  activeTone: String,
  autoSubmit: Boolean
) {
  Surface(
    color = DarkSurface,
    shadowElevation = 0.dp,
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Accent),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "FloatReply",
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(AccentTint)
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = "SIMULATOR",
                color = AccentDark,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
            }
          }
          Text(
            text = "Floating AI Comment Overlay for LinkedIn",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
      }

      // Persona & Auto-submit live badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AccentTint)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = activeTone,
            color = AccentDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        if (autoSubmit) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(SuccessTint)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "AUTO-POST",
              color = StatusGreen,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TelemetryScreen(
  logs: List<String>,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = "Overlay Telemetry",
      color = TextPrimary,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Text(
      text = "${logs.size} events · newest first",
      color = TextSecondary,
      fontSize = 12.sp
    )
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(SurfaceTint)
        .border(BorderStroke(1.dp, DarkSurfaceBorder), RoundedCornerShape(8.dp))
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (logs.isEmpty()) {
        Text(
          text = "No events yet. Drag the pill onto a post to start.",
          color = TextMuted,
          fontSize = 11.sp
        )
      } else {
        logs.forEach { log ->
          Text(
            text = log,
            color = if (log.contains("Gemini")) CyanPrimary else TextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 15.sp
          )
        }
      }
    }
  }
}
