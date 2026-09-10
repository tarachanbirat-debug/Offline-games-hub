package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.adapters.AdapterRegistry
import com.example.model.RepoInspection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Game Vault", appName)
  }

  @Test
  fun `adapter registry selects HTML5 adapter for html files`() {
    val repo = RepoInspection(
      repoUrl = "https://github.com/test/game",
      repoName = "Test HTML5 Game",
      rawFiles = listOf("index.html", "game.js")
    )
    val adapter = AdapterRegistry.selectAdapter(repo)
    assertNotNull(adapter)
    assertEquals("HTML5Adapter", adapter.name)
  }

  @Test
  fun `adapter registry selects Godot adapter for godot project`() {
    val repo = RepoInspection(
      repoUrl = "https://github.com/test/godot-game",
      repoName = "Godot Project",
      rawFiles = listOf("project.godot", "main.tscn")
    )
    val adapter = AdapterRegistry.selectAdapter(repo)
    assertNotNull(adapter)
    assertEquals("GodotAdapter", adapter.name)
  }

  @Test
  fun `online arcade assets exist and are readable`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val assetManager = context.assets

    val htmlStream = assetManager.open("online_arcade/arcade.html")
    assertNotNull("arcade.html must be present in assets", htmlStream)
    val htmlContent = htmlStream.bufferedReader().use { it.readText() }
    assert(htmlContent.contains("ARCADE") || htmlContent.contains("arcade"))
    assert(htmlContent.contains("allowfullscreen=\"true\""))
    assert(htmlContent.contains("btn-back-home") || htmlContent.contains("Back"))
    assert(htmlContent.contains("btn-fullscreen") || htmlContent.contains("Full Screen"))

    val jsStream = assetManager.open("online_arcade/games_config.js")
    assertNotNull("games_config.js must be present in assets", jsStream)
    val jsContent = jsStream.bufferedReader().use { it.readText() }
    assert(jsContent.contains("ONLINE_GAMES"))
  }
}

