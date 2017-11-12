package rmi

import java.rmi.Naming

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scalafx.scene.control.TextInputDialog

@RunWith(classOf[JUnitRunner])
class ChatSuite extends FunSuite {
  test("connection to server") {
    var ok = true
    val m = "localhost" match {
      case machine =>
        Naming.lookup(s"rmi://$machine/ChatServer") match {
          case server: RemoteServer =>
              ("test", server)
              ok = true
          case _ =>
            println("There were problems.")
            ok = false
            sys.exit(0)
        }
      case _ =>
        ok = false
        sys.exit(0)
    }
/*      val machine = "localhost"
      var ok = true
      Naming.lookup(s"rmi://$machine/ChatServer") match {
        case server: RemoteServer =>
          ("test", server)
          ok = true
        case _ =>
          println("There were problems.")
          ok = false
      }*/
      assert(ok === true)
  }

}
