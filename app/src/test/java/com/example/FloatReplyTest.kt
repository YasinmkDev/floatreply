package com.example

import com.example.model.CommentAngle
import com.example.model.ToneStyle
import com.example.viewmodel.FloatReplyViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FloatReplyTest {

  private lateinit var viewModel: FloatReplyViewModel

  @Before
  fun setup() {
    viewModel = FloatReplyViewModel()
  }

  @Test
  fun initialPostsLoaded() {
    val state = viewModel.uiState.value
    assertEquals(4, state.posts.size)
    assertEquals("post_1", state.targetedPostId)
    assertNotNull(state.activeSentiment)
  }

  @Test
  fun updatePersonaRoleAndTone() {
    viewModel.updatePersonaRole("Staff AI Engineer")
    viewModel.updatePersonaTone(ToneStyle.WITTY)
    viewModel.toggleAutoSubmit(true)

    val state = viewModel.uiState.value
    assertEquals("Staff AI Engineer", state.persona.role)
    assertEquals(ToneStyle.WITTY, state.persona.tone)
    assertTrue(state.isAutoSubmitEnabled)
  }

  @Test
  fun addCustomPostToFeed() {
    viewModel.addCustomPost(
      authorName = "Satya Nadella",
      headline = "Chairman and CEO at Microsoft",
      content = "Exciting advancements in autonomous AI systems."
    )

    val state = viewModel.uiState.value
    assertEquals(5, state.posts.size)
    val firstPost = state.posts.first()
    assertEquals("Satya Nadella", firstPost.authorName)
    assertEquals(firstPost.id, state.targetedPostId)
  }

  @Test
  fun submitCommentUpdatesPostComments() {
    viewModel.targetPost("post_1")
    viewModel.updateDraftComment("post_1", "This is an insightful test comment.")
    viewModel.submitComment("post_1")

    val state = viewModel.uiState.value
    val post1 = state.posts.find { it.id == "post_1" }
    assertNotNull(post1)
    assertTrue(post1!!.comments.any { it.text == "This is an insightful test comment." })
  }
}
