package rmi

import java.rmi.Naming

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scalafx.Includes._
import scalafx.scene.control.TextInputDialog

@RunWith(classOf[JUnitRunner])
class ChatSuite extends FunSuite {
  test("connection to server") {
    RMIChatServer.main()
    var ok = true
    val machine = "localhost"
    Naming.lookup(s"rmi://$machine/ChatServer") match {
      case server: RemoteServer =>
        ("test", server)
        ok = true
      case _ =>
        println("There were problems.")
        ok = false
        sys.exit(0)
    }
    assert(ok === true)
  }
}
