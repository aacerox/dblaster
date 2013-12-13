package es.dblaster.io

import es.dblaster.sequencer.Sequencer

trait WaitStrategy {

  def wait(sequencer: Sequencer, isReading: Boolean, args: Any*): Int

}