package es.dblaster.io.impl

import es.dblaster.io.DBlasterWriter

import es.dblaster.sequencer.RingSequencer
import es.dblaster.sequencer.Sequencer

class BaseWriter[T](val sequencer: Sequencer,
  private val buckets: Array[T]) extends DBlasterWriter[T] {

  def write(data: T, args: Any*): Option[Int] = {
    // get next value from write sequencer ring    
    var next = -1
   
      //execute wait strategy and wait for available bucket or slot to write in
       println("[" + Thread.currentThread().getName() + "] writer solicita secuencia")
      next = this.waitStrategy.wait(sequencer,false, args:_*)
      println("[" + Thread.currentThread().getName() + "] secuencia devuelta a writer = " + next )
      //no write bucket or slot available
      if (next == -1) return None

      if (this.on("writeAvailable",data))
        this.writeStrategy.write(next, data, buckets)

      this.on("write",data)

      Some(next)

   

  }

}


