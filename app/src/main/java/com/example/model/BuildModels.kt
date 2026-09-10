package com.example.model

enum class ClassificationStatus {
  READY,
  OFFLINE_READY,
  ONLINE,
  BUILD_REQUIRED,
  LICENSE_REVIEW,
  UNSUPPORTED
}

enum class LicenseVerdict {
  LICENSE_VERIFIED,
  LICENSE_REVIEW_REQUIRED,
  LICENSE_NOT_SUITABLE
}

data class RepoInspection(
  val repoUrl: String,
  val repoName: String,
  val description: String = "",
  val author: String = "Open Contributor",
  val branch: String = "main",
  val rawFiles: List<String> = emptyList(),
  val licenseType: String = "MIT",
  val licenseContentSnippet: String = "",
  val packageJsonText: String? = null,
  val projectFileText: String? = null
)

data class InspectionReport(
  val gameId: String,
  val title: String,
  val detectedTechnology: String,
  val entryPoint: String,
  val dependencies: List<String>,
  val externalNetworkRequired: Boolean,
  val licenseVerdict: LicenseVerdict,
  val licenseType: String,
  val redistributionSuitable: Boolean,
  val offlineCapable: Boolean,
  val mobileTouchCompatible: Boolean,
  val performanceScore: Int, // 0 - 100
  val buildRequirements: String,
  val recommendedAdapter: String,
  val classification: ClassificationStatus,
  val notes: List<String>
)

data class BuildStepLog(
  val stepName: String,
  val status: StepStatus,
  val output: String,
  val durationMs: Long
)

enum class StepStatus {
  PENDING,
  IN_PROGRESS,
  SUCCESS,
  FAILED,
  SKIPPED
}

data class BuildExecution(
  val id: String,
  val gameId: String,
  val gameTitle: String,
  val adapterName: String,
  val targetPlatform: String, // "WEB", "ANDROID", "STANDALONE"
  val steps: List<BuildStepLog>,
  val overallStatus: StepStatus,
  val failureReason: DiagnosticFailure? = null,
  val generatedWorkflowYaml: String = "",
  val packageOutputPath: String = ""
)

data class DiagnosticFailure(
  val game: String,
  val technology: String,
  val buildSystem: String,
  val detectedIssue: String,
  val failedStep: String,
  val error: String,
  val possibleSolution: String
)

data class GamePackageMeta(
  val packageId: String,
  val title: String,
  val version: String,
  val technology: String,
  val entryPoint: String,
  val fileTree: List<String>,
  val manifestJson: String,
  val isOfflineReady: Boolean,
  val sizeBytes: Long
)
