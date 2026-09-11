package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GamePixResponse(
  @Json(name = "status") val status: String? = null,
  @Json(name = "code") val code: Int? = null,
  @Json(name = "data") val data: List<GamePixGameDto>? = null,
  @Json(name = "items") val items: List<GamePixGameDto>? = null
)

@JsonClass(generateAdapter = true)
data class GamePixGameDto(
  @Json(name = "id") val id: String,
  @Json(name = "title") val title: String,
  @Json(name = "category") val category: String? = "Arcade",
  @Json(name = "thumbnailUrl") val thumbnailUrl: String? = null,
  @Json(name = "image") val image: String? = null,
  @Json(name = "bannerUrl") val bannerUrl: String? = null,
  @Json(name = "url") val url: String? = null,
  @Json(name = "direct_url") val directUrl: String? = null,
  @Json(name = "description") val description: String? = null,
  @Json(name = "orientation") val orientation: String? = "PORTRAIT",
  @Json(name = "featured") val featured: Boolean? = false,
  @Json(name = "views") val views: Int? = 0,
  @Json(name = "quality") val quality: Double? = 5.0
) {
  fun getEffectiveThumbnail(): String {
    return thumbnailUrl?.takeIf { it.isNotBlank() }
      ?: image?.takeIf { it.isNotBlank() }
      ?: bannerUrl?.takeIf { it.isNotBlank() }
      ?: "https://img.gamepix.com/games/$id/icon/icon.png"
  }

  fun getEffectivePlayUrl(): String {
    return url?.takeIf { it.isNotBlank() }
      ?: directUrl?.takeIf { it.isNotBlank() }
      ?: "https://play.gamepix.com/$id"
  }

  fun getNormalizedCategory(): String {
    val cat = category?.trim()?.uppercase() ?: "ARCADE"
    return when {
      cat.contains("ACTION") -> "ACTION"
      cat.contains("PUZZLE") || cat.contains("BRAIN") -> "PUZZLE"
      cat.contains("SPORT") -> "SPORTS"
      cat.contains("RACE") || cat.contains("RACING") -> "ACTION"
      cat.contains("BOARD") || cat.contains("CARD") -> "PUZZLE"
      cat.contains("ADVENTURE") -> "ACTION"
      else -> "ARCADE"
    }
  }
}
