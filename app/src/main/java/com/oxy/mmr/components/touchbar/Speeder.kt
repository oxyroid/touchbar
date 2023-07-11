package com.oxy.mmr.components.touchbar


import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Immutable
interface Speeder {
    val speed: Float
    fun increase()
    fun decrease()
}

@Composable
fun rememberSpeeder(
    forgiveness: Long = 150,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): Speeder = remember(forgiveness, coroutineScope) {
    CoroutineSpeeder(forgiveness, coroutineScope)
}


class CoroutineSpeeder(
    private val forgiveness: Long,
    coroutineScope: CoroutineScope
) : Speeder {
    init {
        coroutineScope.launch {
            withContext(Dispatchers.Default) {
                while (true) {
                    delay(forgiveness)
                    if (_speed < 0.0f) {
                        _speed += 0.1f
                    } else if (_speed > 0.0f) {
                        _speed -= 0.1f
                    }
                }
            }
        }
    }

    private var _speed: Float = 0f

    override val speed: Float get() = _speed

    override fun increase() {
        _speed += 0.1f
    }

    override fun decrease() {
        _speed -= 0.1f
    }
}