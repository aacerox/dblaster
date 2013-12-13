package es.dblaster

import scala.actors.threadpool.ExecutorService
import scala.actors.threadpool.Executors
import scala.actors.threadpool.TimeUnit
import scala.collection.mutable.ListBuffer

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

object DblasterTest {
  val SIZE: Int = 10
  val BLASTER_SIZE: Int = 10
  val CHECKSUM: Int = 55
}

class DblasterTest {

  import DblasterTest._

 

  val _sourceList: ListBuffer[Int] = new ListBuffer[Int]
  val _targetList: ListBuffer[Int] = new ListBuffer[Int]
  var _sourceIndex: Int = 0
  var _targetIndex: Int = 0
  var _blaster: DBlaster[Integer] = _
  val _monitor: AnyRef = new AnyRef()

  private def createWriteWorker(sleep: Long): Runnable = new Thread(new Runnable {
    def run() {
      println("WRITER STARTED")
      var rangeIndex = 0
      while (rangeIndex <= SIZE - 1) {

        //take block read 10 elements from source list and write it to dblaster
        _monitor.synchronized {

          rangeIndex = (_sourceIndex + 10)
          for (i <- _sourceIndex until rangeIndex if (i < SIZE)) {
            // write to blaster with default write and wite strategy 
            // timeout set to 500 ms
            val value = _sourceList(i)
            val result: Option[Int] = _blaster.write(value, 500L)
            println("[" + Thread.currentThread().getName() + "]  sourceList.index = " + i + " writing to dblaster value (" + value + ") => " + result)
          }
          _sourceIndex = rangeIndex

        }
        Thread.sleep(sleep)
      }
      println("WRITER TERMINATED")
    }
  })

  private def createReadWorker(sleep: Long): Runnable = new Thread(new Runnable {
    def run() {
      println("READER STARTED")
      // read elements from dblaster with default read and wait strategy (500 ms timeout) 
      //and write it to targetList,  no locks
      while (_targetList.length < SIZE) {

        val result: Option[Integer] = _blaster.read(500L)
        println("[" + Thread.currentThread().getName() + "] targetList.length = " + _targetList.length + "] reading from dblaster = " + result)
        if (result != None)
          _targetList += result.get
        Thread.sleep(sleep)
      }
      println("READER TERMINATED")
    }
  })

  private def executeReaders(numReaders: Int, sleep: Long): (Int) => Unit = {

    val readPool = Executors.newFixedThreadPool(numReaders)

    var reader = createReadWorker(sleep)
    reader.asInstanceOf[Thread].setName("reader")

    for (i <- 1 to numReaders) readPool.execute(reader)

    (await: Int) => {
      readPool.awaitTermination(await, TimeUnit.SECONDS)
      readPool.shutdown()
    }
  }

  private def executeWriters(numWriters: Int, sleep: Long): (Int) => Unit = {
    val writePool = Executors.newFixedThreadPool(numWriters)

    var writer = createWriteWorker(sleep)
    writer.asInstanceOf[Thread].setName("writer")
    for (i <- 1 to numWriters) writePool.execute(writer)
    (await: Int) => {
      writePool.awaitTermination(await, TimeUnit.SECONDS)
      writePool.shutdown()
    }
  }

  @Before
  def initialize {
    //initialize data list and indexes    
    _sourceIndex = 0
    _targetIndex = 0

    _sourceList.clear
    _targetList.clear
    println("source list size = " + _sourceList.length)
    println("target list size = " + _targetList.length)
    for (i <- 1 to SIZE) _sourceList += i
    _blaster = new DBlaster[Integer](BLASTER_SIZE)
  }

  @Test
  def equal_writes_reads {

    for (i <- 1 to 1) {
      println(" ================= iteration " + i + " =========================")
      val awaitWrite = executeWriters(4, 0)
      val awaitRead = executeReaders(4, 0)

      awaitWrite(5)
      awaitRead(5)

      //      for (targetIndex <- 0 to _targetList.length) 
      //    	  println("targetList = " + _targetList(targetIndex) )

      _targetList.foreach(e => println("targetList = " + e))
      var sum: Int = 0
      _targetList.foreach(e => sum += e)

      assertEquals(SIZE, _targetList.length)
      assertEquals(CHECKSUM, sum)
      initialize
    }
  }

  @Test
  def more_writes_than_reads {

  }

  @Test
  def more_reads_than_writes {

  }

  @Test
  def handle_onWrite_events {

  }

  @Test
  def handle_onWriteAvailable_events {

  }

  @Test
  def handle_onRead_events {

  }

  @Test
  def handle_onReadAvailable_events {

  }

  @Test
  def custom_write_wait_strategy {

  }

  @Test
  def custom_read_wait_strategy {}
}