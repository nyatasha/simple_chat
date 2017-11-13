package rmi

import java.rmi.Naming

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ChatSuite extends FunSuite {
  test("connection to server") {
    RMIChatServer.main(Array[String]())
    var connected = true
    val machine = "localhost"
    Naming.lookup(s"rmi://$machine/ChatServer") match {
      case server: RemoteServer =>
        ("test", server)
        connected = true
      case _ =>
        println("There were problems.")
        connected = false
    }
    assert(connected)
  }
}
