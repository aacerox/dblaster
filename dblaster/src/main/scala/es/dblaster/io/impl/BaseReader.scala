package es.dblaster.io.impl

import es.dblaster.io.DBlasterReader
import es.dblaster.sequencer.Sequencer

class BaseReader[T](val sequencer: Sequencer,
  private val buckets: Array[T]) extends DBlasterReader[T] {

  def read(args: Any*): Option[T] = {
    // get next value from sequencer ring    
    var next = -1
    var result: Option[T] = None
    println("[" + Thread.currentThread().getName() + "] reader solicita secuencia")
    
    //execute wait strategy that reads to dblaster bucket or slot
    next = this.waitStrategy.wait(sequencer, true, args: _*)
    println("[" + Thread.currentThread().getName() + "] secuencia devuelta a reader = " + next )
    //no read space available
    if (next == -1) return None

    // invoke eventHandlers and if allowed continue reading data
    if (!this.on("readAvailable", buckets(next))) return None

    result = this.readStrategy.read(next, buckets)

    this.on("read", result.get)

    result
  }

}