package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CustomPostDialog(
  onDismiss: () -> Unit,
  onSubmit: (author: String, headline: String, content: String) -> Unit
) {
  var author by remember { mutableStateOf("") }
  var headline by remember { mutableStateOf("") }
  var content by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("custom_post_dialog"),
    containerColor = DarkSurface,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = "Paste Custom LinkedIn Post",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Test FloatReply's floating AI overlay on any post from your actual LinkedIn feed.",
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        OutlinedTextField(
          value = author,
          onValueChange = { author = it },
          label = { Text("Author Name", color = TextSecondary) },
          placeholder = { Text("e.g. Satya Nadella", color = Color(0xFF64748B)) },
          modifier = Modifier.fillMaxWidth().testTag("custom_author_input"),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanPrimary,
            unfocusedBorderColor = DarkSurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        OutlinedTextField(
          value = headline,
          onValueChange = { headline = it },
          label = { Text("Author Headline", color = TextSecondary) },
          placeholder = { Text("e.g. Chairman and CEO at Microsoft", color = Color(0xFF64748B)) },
          modifier = Modifier.fillMaxWidth().testTag("custom_headline_input"),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanPrimary,
            unfocusedBorderColor = DarkSurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Post Body Content", color = TextSecondary) },
          placeholder = { Text("Paste the LinkedIn post copy here...", color = Color(0xFF64748B)) },
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .testTag("custom_content_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanPrimary,
            unfocusedBorderColor = DarkSurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (content.isNotBlank()) {
            onSubmit(
              author.ifBlank { "LinkedIn Creator" },
              headline.ifBlank { "Tech Industry Leader" },
              content
            )
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = CyanPrimary,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("submit_custom_post_button")
      ) {
        Text("Add to Feed", fontWeight = FontWeight.SemiBold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
      ) {
        Text("Cancel")
      }
    }
  )
}
