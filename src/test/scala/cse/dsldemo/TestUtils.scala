package cse.dsldemo

object TestUtils {

  def exceptionFrom[T] (act: => T): Exception = {
    try {
      act
      throw new IllegalStateException("Should have thrown exception but didn't")
    }
    catch {
      case e: Exception => e
    }
  }
}
