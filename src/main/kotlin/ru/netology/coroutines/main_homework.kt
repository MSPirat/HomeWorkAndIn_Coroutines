package ru.netology.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext


/**
 * Вопросы: Cancellation
 *
 * 1. Не отработает, так как функция job.cancelAndJoin() отменяет корутины вывода на печать "ок" до их выполнения.
 * Можно заменить job.cancelAndJoin() -> job.join() или изменить задержку на её выполнение, установив значение
 * в delay больше 500
 */
/*fun main() = runBlocking {
    val job = CoroutineScope(EmptyCoroutineContext).launch {
        launch {
            delay(500)
            println("first ok") // <--
        }
        launch {
            delay(500)
            println("second ok")
        }
    }
    delay(100)
//    job.cancelAndJoin()
    job.join()
}
*/

/**
 * 2. Не отработает, так как функция child.cancel() отменяет корутину вывода на печать первой "ок" до её выполнения.
 * При этом второй "ок" выводится на печать нормально.
 * Можно заменить child.cancel() -> child.join() или изменить задержку на её выполнение, установив значение
 * в delay больше 500
 */
/*
fun main() = runBlocking {
    val job = CoroutineScope(EmptyCoroutineContext).launch {
        val child = launch {
            delay(500)
            println("first ok") // <--
        }
        launch {
            delay(500)
            println("second ok")
        }
        delay(1000)
        child.cancel()
    }
    delay(100)
    job.join()
}
*/

/**
 * Вопросы: Exception Handling
 *
 * 1. Не отработает, так как прежде выведется исключение Exception("something bad happened")
 * Из презентации к лекции © "Нет смысла писать вот такой код, он не перехватит никаких исключений"
 */
/*
fun main() {
    with(CoroutineScope(EmptyCoroutineContext)) {
        try {
            launch {
                throw Exception("something bad happened")
            }
        } catch (e: Exception) {
            e.printStackTrace() // <--
        }
    }
    Thread.sleep(1000)
}
*/

/**
 * 2. Отработает, выбросится исключение Exception "something bad happened".
 * и в отладчике можно наблюдать Coroutine Creation Stack Trace
 */
/*
fun main() {
	CoroutineScope(EmptyCoroutineContext).launch {
		try {
			coroutineScope {
				throw Exception("something bad happened")
			}
		} catch (e: Exception) {
			println("111")
			e.printStackTrace() // <--
		}
	}
	Thread.sleep(1000)
}
*/

/**
 * 3. Отработает, так как функция supervisorScope приостановит выброс исключения Exception "something bad happened"
 * до выполнения её child с печатью stacktrace.
 * Также в отладчике можно наблюдать Coroutine Creation Stack Trace.
 */

fun main() {
	CoroutineScope(EmptyCoroutineContext).launch {
		try {
			supervisorScope {
				throw Exception("something bad happened")
			}
		} catch (e: Exception) {
			println("222")
			e.printStackTrace() // <--
		}
	}
	Thread.sleep(1000)
}


/**
 * 4. Не отработает, так как функция delay приостановит поток, в результате чего выйдет второй Exception.
 * Можно отключить задержку выполнения корутины.
 */
/*
fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            coroutineScope {
                launch {
                    delay(500)
                    throw Exception("first something bad happened") // <--
                }
                launch {
                    throw Exception("second something bad happened")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    Thread.sleep(1000)
}
*/

/**
 * 5. Отработает, так как корутины запускаются в функции supervisorScope, которая выполняется, когда завершаются
 * все её child.
 */
/*
fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            supervisorScope {
                launch {
                    delay(500)
                    throw Exception("first something bad happened") // <--
                }
                launch {
                    throw Exception("second something bad happened")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace().toString() // <--
        }
    }
    Thread.sleep(1000)
}
*/

/**
 * 6. Не отработает, так как прежде выведется исключение Exception("something bad happened").
 * Для выполнения печати первой "ок", нужно заменить функцию launch на async и уменьшить время задержки delay
 */
/*
fun main() {
	CoroutineScope(EmptyCoroutineContext).launch {
		CoroutineScope(EmptyCoroutineContext).launch {
			async {
//				delay(1000)
				println("first ok") // <--
			}
			launch {
				delay(500)
				println("second ok")
			}
			throw Exception("something bad happened")
		}
	}
	Thread.sleep(1000)
}
*/

/**
 * 7. Не отработает, так как прежде выведется исключение Exception("something bad happened").
 * Для выполнения печати первой "ок", можно обрабатывать ошибки только вывода на печать второй "ок",
 * тогда он не будет влиять на вывод печати первой "ок" и уменьшить время задержки delay первой "ок"
 */
/*
fun main() {
	CoroutineScope(EmptyCoroutineContext).launch {
		CoroutineScope(EmptyCoroutineContext + SupervisorJob()).launch {
			launch {
				delay(500)
				println("first ok") // <--
			}
			launch {
				delay(500)
				println("second ok")
				throw Exception("something bad happened")
			}
		}
	}
	Thread.sleep(1000)
}
*/