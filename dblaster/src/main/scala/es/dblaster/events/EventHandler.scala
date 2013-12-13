package es.dblaster.events

class ChainHandler[T](val handle: (T) => Boolean) {

  var sucessor: ChainHandler[T] = _

}

class EventHandler[T] {

  protected type logicHandler = (T) => Boolean

  protected val eventRegistry: Map[String, ChainHandler[T]] = Map()

  def on(event: String, data: T): Boolean = {

    /**
     * local method to traverse event handler chain
     *
     * @param data passed to eventHandler
     * @param handler eventHandler
     * @return true if continue with chain, false if not
     */
    def traverseChain(data: T, handler: ChainHandler[T]): Boolean = {
      var continue = handler.handle(data)
      if (continue && handler.sucessor != null) traverseChain(data, handler.sucessor)

      continue
    }

    eventRegistry.contains(event) match {
      case true => traverseChain(data, eventRegistry(event)) // traverse chain from event registry firing each handler and continue normal process depends of the result of this method
      case _ => true // no event handler registered continue normal process//throw new IllegalArgumentException("no eventhandler found for event " + event);
    }

  }

  def registerHandler(handler: logicHandler, event: String): Unit = {
    //get current chain handler for event if none, initialize and add to chain
    // else add sucessor to chain
    if (eventRegistry.contains(event))
      eventRegistry + (event -> new ChainHandler(handler))
    else
      eventRegistry(event).sucessor = new ChainHandler(handler)

  }

}