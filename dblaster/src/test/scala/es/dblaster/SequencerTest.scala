package es.dblaster

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

import es.dblaster.sequencer.RingSequencer

class SequencerTest {

  @Test
  def initialized_to_parameter_size {

    val circular = new RingSequencer[Int](new Array[Int](10))
    assertEquals(10, circular.size)

  }

  @Test
  def readPointer_cannot_start_if_no_write_done {
    val circular = new RingSequencer[Int](new Array[Int](10))
    assertEquals(-1, circular.nextRead)

  }

  @Test
  def readPointer_cannot_surpass_writePointer_no_cycle {

    val circular = new RingSequencer[Int](new Array[Int](5))

    // advance write 2 positions and read 3 positions
    for (i <- 1 to 2) circular.nextWrite
    for (i <- 1 to 3)
      if (i <= 2)
        assertTrue(circular.nextRead > -1)
      else
        assertTrue(circular.nextRead == -1)

  }

  @Test
  def readPointer_cannot_surpass_writePointer_cycle {
    val circular = new RingSequencer[Int](new Array[Int](5))
    var seq1, seq2 = 0
    println("readPointer_cannot_surpass_writePointer_cycle")
    // advance write 5 positions, read one, advance 2 more positions and read
    for (i <- 1 to 5) {
      seq2 = circular.nextWrite
      println("1.iteration=", i, "read=", seq1, "write", seq2, "distance", circular.distance)

    }
    for (i <- 1 to 3) {
      seq1 = circular.nextRead
      println("2.iteration=", i, "read=", seq1, "write", seq2, "distance", circular.distance)
      assertTrue(seq1 != -1)
    }
    for (i <- 1 to 2) {
      seq2 = circular.nextWrite
      println("3.iteration=", i, "read=", seq1, "write", seq2, "distance", circular.distance)
    }
    for (i <- 1 to 5) {
      seq1 = circular.nextRead
      println("4.iteration=", i, "read=", seq1, "write", seq2, "distance", circular.distance)
      if (i <= 4) assertTrue(seq1 != -1) else assertTrue(seq1 == -1)

    }

  }

  @Test
  def writePointer_cannot_start_cycle_no_read_done {
    val circular = new RingSequencer[Int](new Array[Int](5))
    var seq = 0
    for (i <- 1 to 6) seq += circular.nextWrite
    assertEquals(9, seq)

  }

  @Test
  def writePointer_cannot_surpass_readPointer_cycle {
    val circular = new RingSequencer[Int](new Array[Int](5))
    var seq1,seq2 = 0
    // write 4 stop, read 4 then write 6
    for (i <- 1 to 4) {
      seq2 =circular.nextWrite
      println("1.iteration=", i, "read=", seq1, "write", seq2, "distance", circular.distance)
    }
    for (i <- 1 to 4) {
      seq1=circular.nextRead
      println("2.iteration=", i, "read=", seq1, "write", seq2, "distance", circular.distance)
    }
    for (i <- 1 to 6) {
      seq1 = circular.nextWrite
      println("3.iteration=", i, "read=", seq1, "write", seq2, "distance", circular.distance)
      if (i <= 5)
        assertTrue(seq1 != -1)
      else
        assertTrue(seq1 == -1)
    }
  }

  @Test
  def readPointer_must_cycle_sequences {
    val circular = new RingSequencer[Int](new Array[Int](5))
    var seq = 0
    // advance write 5 positions, read one, advance 2 more positions and read
    for (i <- 1 to 5) circular.nextWrite
    for (i <- 1 to 3) circular.nextRead
    for (i <- 1 to 2) circular.nextWrite
    for (i <- 1 to 4) seq += circular.nextRead

    assertEquals(8, seq)
  }

  @Test
  def writePointer_must_cycle_sequences {
    val circular = new RingSequencer[Int](new Array[Int](5))
    var seq = 0

    // write 4 stop, read 4 then write 6
    for (i <- 1 to 4) circular.nextWrite
    for (i <- 1 to 4) circular.nextRead
    for (i <- 1 to 5) seq += circular.nextWrite

    assertEquals(10, seq)
  }

}