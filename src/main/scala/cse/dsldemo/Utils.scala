package cse.dsldemo

object Utils {
  def TEST_DRIVE_ME: Nothing = {
    throw new UnsupportedOperationException ("Test-drive me")
  }

  def TEST_DRIVE_ME (msg: String): Nothing = {
    throw new UnsupportedOperationException (msg)
  }
}
