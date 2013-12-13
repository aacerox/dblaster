package es.dblaster.io.write

import es.dblaster.io.WriteStrategy

class SimpleWriteStrategy[T] extends WriteStrategy[T] {

  def write(sequence: Int, data: T, buckets: Array[T]): Unit = {
    // no room, nothing to do
    if (sequence == -1) return
    		
    buckets(sequence) = data
    println("[" +Thread.currentThread().getName() + "] written " + data + " in seq " + sequence)

  }

}