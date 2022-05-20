package net.syntessense.app.todolist_dai2


import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class SpeechAnalysis(private val activity: AppCompatActivity) {

    private var working = false
    private var callback = {str: String -> }
    private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        var answer = if (result.resultCode == Activity.RESULT_OK && data != null) {
            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: "???"
        } else "???"
        callback(answer)
        working = false
    }

    fun start() {
        start {}

    }

    fun start(callback: (String) -> Unit) {
        if (working)
            return
        working = true
        this.callback = callback
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        //intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
        //intent.putExtra("android.speech.extra.GET_AUDIO", true)
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "...")
        launcher.launch(intent)
    }

}

