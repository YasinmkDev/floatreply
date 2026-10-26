package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PersonaConfig
import com.example.model.ToneStyle
import com.example.ui.theme.Accent
import com.example.ui.theme.AccentDark
import com.example.ui.theme.AccentTint
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.Hairline
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.SurfaceTint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PersonaCalibrationSidebar(
  persona: PersonaConfig,
  isAutoSubmitEnabled: Boolean,
  onRoleChanged: (String) -> Unit,
  onToneChanged: (ToneStyle) -> Unit,
  onCustomRulesChanged: (String) -> Unit,
  onAutoSubmitToggled: (Boolean) -> Unit,
  onSignoffToggled: (Boolean) -> Unit,
  onRegenerateSuggestions: () -> Unit,
  telemetryLogs: List<String>,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var showTelemetry by remember { mutableStateOf(false) }

  val rolePresets = listOf(
    "B2B SaaS Founder & Seed Investor",
    "Staff Distributed Systems Engineer",
    "VP Sales & Outbound Growth",
    "Chief AI Product Architect"
  )

  Column(
    modifier = modifier
      .background(DarkBackground)
      .verticalScroll(scrollState)
      .padding(16.dp)
      .testTag("persona_calibration_sidebar"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Accent),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }

      Column {
        Text(
          text = "Voice & Persona",
          color = TextPrimary,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Calibrates FloatReply AI overlay tone",
          color = CyanGlow,
          fontSize = 11.sp
        )
      }
    }

    HorizontalDivider(color = DarkSurfaceBorder, thickness = 1.dp)

    // Section 1: Role / Expertise
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "Role & Expertise:",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )

      OutlinedTextField(
        value = persona.role,
        onValueChange = onRoleChanged,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("persona_role_input"),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = CyanPrimary,
          unfocusedBorderColor = DarkSurfaceBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        )
      )

      // Role Preset Chips
      Text(
        text = "Quick Presets:",
        color = TextMuted,
        fontSize = 11.sp
      )

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        rolePresets.forEach { preset ->
          val isSelected = persona.role == preset
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) AccentTint else DarkSurfaceCard,
            border = BorderStroke(
              1.dp,
              if (isSelected) CyanGlow else DarkSurfaceBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onRoleChanged(preset) }
              .padding(0.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = preset,
                color = if (isSelected) AccentDark else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
              )
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = CyanGlow,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
    }

    // Section 2: Tone of Voice
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "Tone of Voice:",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ToneStyle.entries.forEach { toneStyle ->
          val isSelected = persona.tone == toneStyle
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) AccentTint else DarkSurface,
            border = BorderStroke(
              1.dp,
              if (isSelected) IndigoAccent else DarkSurfaceBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onToneChanged(toneStyle) }
              .testTag("tone_chip_${toneStyle.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(16.dp)
                  .clip(CircleShape)
                  .border(
                    BorderStroke(1.5.dp, if (isSelected) CyanPrimary else TextMuted),
                    CircleShape
                  ),
                contentAlignment = Alignment.Center
              ) {
                if (isSelected) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(CyanPrimary)
                  )
                }
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = toneStyle.badge,
                  color = if (isSelected) CyanGlow else TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Text(
                  text = toneStyle.guideline,
                  color = TextSecondary,
                  fontSize = 10.sp,
                  lineHeight = 14.sp
                )
              }
            }
          }
        }
      }
    }

    // Section 3: Custom Rules & Anti-AI Slop Guardrails
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Custom Rules & Guardrails:",
          color = TextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(AccentTint)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "ANTI-SLOP ENFORCED",
            color = CyanGlow,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      OutlinedTextField(
        value = persona.customRules,
        onValueChange = onCustomRulesChanged,
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp)
          .testTag("custom_rules_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = CyanPrimary,
          unfocusedBorderColor = DarkSurfaceBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        )
      )
    }

    // Section 4: Auto-Submit Comment Toggle
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color.White,
      border = BorderStroke(
        1.dp,
        if (isAutoSubmitEnabled) CyanPrimary.copy(alpha = 0.6f) else DarkSurfaceBorder
      ),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Auto-Submit Comment",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = "Directly publishes to LinkedIn after typing",
            color = if (isAutoSubmitEnabled) CyanGlow else TextMuted,
            fontSize = 10.sp
          )
        }

        Switch(
          checked = isAutoSubmitEnabled,
          onCheckedChange = onAutoSubmitToggled,
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = CyanPrimary,
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = Hairline
          ),
          modifier = Modifier.testTag("auto_submit_toggle")
        )
      }
    }

    // Section 5: Role sign-off on injected comments
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color.White,
      border = BorderStroke(1.dp, DarkSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Sign comments with role",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = "Appends your role under injected comments",
            color = if (persona.includeSignatureSignoff) CyanGlow else TextMuted,
            fontSize = 10.sp
          )
        }

        Switch(
          checked = persona.includeSignatureSignoff,
          onCheckedChange = onSignoffToggled,
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = CyanPrimary,
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = Hairline
          ),
          modifier = Modifier.testTag("signoff_toggle")
        )
      }
    }

    // Re-generate button
    Button(
      onClick = onRegenerateSuggestions,
      colors = ButtonDefaults.buttonColors(
        containerColor = CyanPrimary,
        contentColor = Color.White
      ),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .testTag("calibrate_and_regenerate_button")
    ) {
      Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = null,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text("Apply & Refresh AI Overlay", fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }

    // Collapsible Simulated Accessibility & Telemetry logs
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = DarkSurfaceCard,
      border = BorderStroke(1.dp, DarkSurfaceBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showTelemetry = !showTelemetry },
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = null,
              tint = CyanGlow,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Overlay Telemetry (${telemetryLogs.size})",
              color = TextPrimary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = if (showTelemetry) "Hide" else "Inspect",
            color = CyanPrimary,
            fontSize = 10.sp
          )
        }

        AnimatedVisibility(visible = showTelemetry) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(SurfaceTint)
              .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            telemetryLogs.forEach { log ->
              Text(
                text = log,
                color = if (log.contains("Gemini")) CyanGlow else TextSecondary,
                fontSize = 9.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                lineHeight = 12.sp
              )
            }
          }
        }
      }
    }
  }
}
