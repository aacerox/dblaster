package es.dblaster.sequencer

import scala.actors.threadpool.AtomicInteger

object RingSequencer {
  implicit val (logger, formatter, appender) = ZeroLoggerFactory.newLogger(this)
}

class RingSequencer[T](buckets: Array[T]) extends AbstractSequencer(buckets) {

  import RingSequencer._

  private def getPointerPos(pointer: AtomicInteger): Int = {
	
    println("[" + Thread.currentThread().getName() + "]  su puntero actual es " + pointer.get())
    val result = pointer.get() match {
      case x if (x == (this.size - 1)) => pointer.addAndGet(-(this.size - 1))
      case _ => pointer.addAndGet(1)
    }

    println("[" + Thread.currentThread().getName() + "]  su puntero devuelto es " + result)
    result
  }

  def nextWrite: Int = {
    // return write pointer avoiding surpass read pointer
    try {
      if (checkSurpassPointers(false)) return -1
      getPointerPos(_writePointer);
    } finally {
      _distance.incrementAndGet()
    }

  }

  def nextRead: Int = {
    // return write pointer avoiding surpass read pointer
    try {
      if (checkSurpassPointers(true)) return -1
      getPointerPos(_readPointer);
    } finally {
      if (_distance.get() > 0) _distance.decrementAndGet()
    }

  }

}