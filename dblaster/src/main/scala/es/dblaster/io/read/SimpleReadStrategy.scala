package es.dblaster.io.read

import es.dblaster.io.ReadStrategy

class SimpleReadStrategy[T] extends ReadStrategy[T] {

  def read(sequence: Int, buckets: Array[T]): Option[T] = {

    // no room, nothing to do
    if (sequence == -1) return None
    val data = buckets(sequence)
    // return single value from array
    println("[" + Thread.currentThread().getName() + "] read data " + data + " from sequence " + sequence)
    Some(data)

  }

}