package es.dblaster.io.wait

import scala.actors.threadpool.TimeUnit

import es.dblaster.io.WaitStrategy
import es.dblaster.sequencer.Sequencer

/**
 * Wait strategy for a timeout period if no wait bucket available during that period of time
 * return -1, else return number buckets index available.
 *
 * Timeout is passed as first parameter to the wait function and is set in milliseconds
 *
 * @author aacero
 *
 */
class TimedWaitStrategy[T] extends WaitStrategy {

  def wait(sequencer: Sequencer, isReading: Boolean, args: Any*): Int = {

    val startTime = System.nanoTime()
    var nanosTimeout = TimeUnit.MILLISECONDS.toNanos(args(0) match {
      case time: Long => time
      case _ => throw new ClassCastException("first parameter must represent timeout and must be of type Long: found " + args(0).getClass)
    })
    var next: Int = -1
    var leftTime: Long = 0

    while (true) {

      //try to aquire new bucket or slot to write
      next = isReading match {
        case true => sequencer.nextRead
        case _ => sequencer.nextWrite
      }
      if (next != -1) return next

      // get current time in nanos and substract it from startTime
      // result must be gt timeout 
      var now: Long = System.nanoTime();
      leftTime = nanosTimeout - (now - startTime)

      if (leftTime <= 0) return -1
    }

    next
  }

}