package es.dblaster

import es.dblaster.io.DBlasterReader
import es.dblaster.io.DBlasterWriter
import es.dblaster.io.ReadEventHandler
import es.dblaster.io.ReadStrategy
import es.dblaster.io.WaitStrategy
import es.dblaster.io.WriteEventHandler
import es.dblaster.io.WriteStrategy
import es.dblaster.io.impl.BaseReader
import es.dblaster.io.impl.BaseWriter
import es.dblaster.io.read.SimpleReadStrategy
import es.dblaster.io.wait.TimedWaitStrategy
import es.dblaster.io.write.SimpleWriteStrategy
import es.dblaster.sequencer.RingSequencer

class DBlaster[T: Manifest](val size: Int) extends ReadEventHandler[T] with WriteEventHandler[T] {

  private val _buckets: Array[T] = new Array[T](size)
  private val _sequencer: RingSequencer[T] = new RingSequencer(_buckets)

  private var writer: DBlasterWriter[T] = new BaseWriter[T](_sequencer, _buckets)
  private var reader: DBlasterReader[T] = new BaseReader[T](_sequencer, _buckets)

  private def initialize {
    // set default wait, write and read strategy
    writer.writeStrategy = new SimpleWriteStrategy[T]
    writer.waitStrategy = new TimedWaitStrategy
    reader.readStrategy = new SimpleReadStrategy[T]
    reader.waitStrategy = new TimedWaitStrategy
  }

  def write(data: T, args: Any*): Option[Int] = writer.write(data, args: _*);

  def read(args: Any*): Option[T] = reader.read(args: _*)

  /*  strategy methods */
  def setWriteStrategy(strategy: WriteStrategy[T]): Unit = writer.writeStrategy = strategy

  def setWriteWaitStrategy(strategy: WaitStrategy): Unit = writer.waitStrategy = strategy

  def setReadStrategy(strategy: ReadStrategy[T]): Unit = reader.readStrategy = strategy

  def setReadWaitStrategy(strategy: WaitStrategy): Unit = reader.waitStrategy = strategy

  initialize
}