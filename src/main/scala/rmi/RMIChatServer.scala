package rmi

import java.rmi.Naming
import java.rmi.server.UnicastRemoteObject

import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListView, TextInputDialog}

@remote trait RemoteClient {
  def name: String
  def message(sender: RemoteClient, text: String): Unit
  def clientUpdate(clients: Seq[RemoteClient]): Unit
}
object RMIChatClient extends UnicastRemoteObject with JFXApp with RemoteClient {
  val dialog = new TextInputDialog("localhost")
  dialog.title = "Server Maccompile" +
    "hine"
  dialog.contentText = "What server do you want to connect to?"
  dialog.headerText = "Server Name"
  val (_name, server) = dialog.showAndWait() match {
    case Some(machine) =>
      Naming.lookup(s"rmi://$machine/ChatServer") match {
        case server: RemoteServer =>
          val dialog = new TextInputDialog("")
          dialog.title = "Chat Name"
          dialog.contentText = "What name do you want to go by?"
          dialog.headerText = "User Name"
          dialog.showAndWait() match {
            case Some(name) => (name, server)
            case None => sys.exit(0)
          }
        case _ =>
          println("There were problems.")
          sys.exit(0)
      }
    case None => sys.exit(0)
  }

  def name: String = _name

  def message(sender: RemoteClient, text: String): Unit = Platform.runLater {
  }
  var clients = server.getClients
  lazy val userList = new ListView(clients.map(_.name))
  def clientUpdate(clients: Seq[RemoteClient]): Unit = Platform.runLater {
    this.clients = clients
    if (userList != null) userList.items = ObservableBuffer(clients.map { c =>
      c.name
    })
  }
}