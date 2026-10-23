package com.linguapulse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.linguapulse.app.ui.navigation.LinguaPulseNavGraph
import com.linguapulse.app.ui.theme.LinguaPulseTheme
import com.linguapulse.app.ui.theme.PulseBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as LinguaPulseApplication

        setContent {
            LinguaPulseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PulseBackground
                ) {
                    LinguaPulseNavGraph(
                        lessonRepository = app.lessonRepository,
                        userProgressRepository = app.userProgressRepository,
                        offlineQueueRepository = app.offlineQueueRepository,
                        processLessonCompletionUseCase = app.processLessonCompletionUseCase
                    )
                }
            }
        }
    }
}
