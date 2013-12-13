package es.dblaster.sequencer

import scala.actors.threadpool.AtomicInteger

/**
 * @author aacero
 *
 */
trait Sequencer {

  protected var _readPointer: AtomicInteger = new AtomicInteger(-1)
  protected var _writePointer: AtomicInteger = new AtomicInteger(-1)
  protected var _distance: AtomicInteger = new AtomicInteger(0);
  var size: Int = 0

  /**
   * @return next available sequence to write values and move pointer as an atomic operation. If no available return -1
   */
  def nextWrite: Int

  /**
   * @return next available sequence to read values for and move pointer as an atomic operation. If no available return -1
   */
  def nextRead: Int

  def distance = _distance

  protected def checkSurpassPointers(isReading: Boolean): Boolean = {

    def nextPointerPosition(pointer: AtomicInteger): Int =
      synchronized {
        pointer.get() match {
          case x if ((x + 1) == size) => 0
          case _ => pointer.get() + 1
        }
      }
    var surpass = false
    synchronized {
      if (isReading)
        // check if surpass write pointer while getting next read pointer pos      
        surpass = _writePointer.get() match {
          case writePointerPos if (writePointerPos == -1) => true // if no write done yet cannot read
          case writePointerPos if (writePointerPos > -1) => (_distance.get() == 0 && (nextPointerPosition(_readPointer) > writePointerPos)) // if no distance between pointers or next read pointer pos bigger than current WritePointer pos, surpass

        }
      else
        // check if surpass read pointer while getting next write pointer pos
        surpass = _readPointer.get() match {
          case readPointerPos if (readPointerPos == -1) => _writePointer.get() > -1 && nextPointerPosition(_writePointer) == 0 // if no read done yet and write pointer has reached end of the ring, surpass
          case readPointerPos if (readPointerPos > -1) => ((_distance.get() == size) && (nextPointerPosition(_writePointer) > readPointerPos)) //distance between pointer equals buckets size or next write pointer pos bigger than current read pointer pos, surpass
        }
      
      println(if(surpass) "[" + Thread.currentThread().getName() + "] no puede continuar" else "[" + Thread.currentThread().getName() + "] puede continuar")
    }
    surpass

  }

}

abstract class AbstractSequencer[T](buckets: Array[T]) extends Sequencer {

  protected var _sequencer: Array[T] = buckets

  size = _sequencer.length

} 


