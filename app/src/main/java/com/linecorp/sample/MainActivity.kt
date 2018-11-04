package com.linecorp.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.linecorp.highlightview.HighlightTarget
import com.linecorp.highlightview.HighlightView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val imageView: ImageView by lazy {
        findViewById<ImageView>(R.id.image_view)
    }
    private val textView: TextView by lazy {
        findViewById<TextView>(R.id.text_view)
    }
    private val button: Button by lazy {
        findViewById<Button>(R.id.button)
    }
    private val highlightView: HighlightView by lazy {
        findViewById<HighlightView>(R.id.highlight_view)
    }
    private val editText: EditText by lazy {
        findViewById<EditText>(R.id.edit_text)
    }
    private val checkBox: CheckBox by lazy {
        findViewById<CheckBox>(R.id.check_box)
    }
    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        autoPlayHighlights(
            targetViewGroups =
            arrayOf(
                arrayOf(imageView, textView),
                arrayOf(button, editText),
                arrayOf(checkBox),
                arrayOf(imageView, textView, button, editText, checkBox)
            ),
            shouldRepeat = true
        )
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun autoPlayHighlights(
        targetViewGroups: Array<Array<out View>>,
        initialDelayMs: Long = AUTO_PLAY_INITIAL_DELAY_MS,
        delayMs: Long = AUTO_PLAY_DELAY_MS,
        shouldRepeat: Boolean = false
    ) {
        if (targetViewGroups.isEmpty()) {
            return
        }
        Observable.interval(initialDelayMs, delayMs, TimeUnit.MILLISECONDS)
            .take(targetViewGroups.size.toLong())
            .map { idx: Long ->
                mutableListOf<HighlightTarget>()
                    .apply {
                        targetViewGroups[idx.toInt()].forEach { view: View ->
                            add(HighlightTarget(view))
                        }
                    }
                    .toTypedArray()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                if (shouldRepeat) {
                    autoPlayHighlights(
                        targetViewGroups,
                        initialDelayMs = delayMs,
                        shouldRepeat = true
                    )
                }
            }
            .subscribe(
                (highlightView::highlight),
                { /* Do nothing. */ },
                { /* Do nothing. */ }
            )
            .run(compositeDisposable::add)
    }

    companion object {
        private const val AUTO_PLAY_INITIAL_DELAY_MS = 0L
        private const val AUTO_PLAY_DELAY_MS = 1_500L
    }
}
