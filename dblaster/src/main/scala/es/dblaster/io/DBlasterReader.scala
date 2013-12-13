package es.dblaster.io

import es.dblaster.events.EventHandler

trait ReadStrategy[T] {

  def read(sequence: Int, buckets: Array[T]): Option[T]

}

trait ReadEventHandler[T] extends EventHandler[T] {

  def onRead(handler: logicHandler): Unit = registerHandler(handler,"read")

  def onReadAvailable(handler: logicHandler): Unit = registerHandler(handler,"readAvailable")

}

trait DBlasterReader[T] extends ReadEventHandler[T] {

  var readStrategy: ReadStrategy[T] = _

  var waitStrategy: WaitStrategy = _

  def read(args: Any*): Option[T]

}