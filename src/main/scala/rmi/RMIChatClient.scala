package rmi

import scalafx.Includes._
import java.rmi.server.{RemoteServer, UnicastRemoteObject}
import java.rmi.Naming

import scalafx.application.JFXApp
import scalafx.scene.control._
import scalafx.scene.Scene
import scalafx.event.ActionEvent
import scalafx.scene.layout.BorderPane
import java.rmi.RemoteException

import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import scalafx.scene.control.Alert.AlertType
import scala.collection.mutable

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

  server.connect(this)

  val chatArea = new TextArea
  chatArea.editable = false
  var clients = server.getClients
  val userList = new ListView(clients.map(x=>x.name))
  val chatField = new TextField
  chatField.onAction = (ae: ActionEvent) => {
    if(chatField.text().trim.nonEmpty) {
      val recipients = if(userList.selectionModel().selectedItems.isEmpty) {
        clients
      } else {
        userList.selectionModel().selectedIndices.map(i => clients(i)).toSeq
      }
      recipients.foreach { r =>
        try {
          r.message(this, chatField.text().trim)
        } catch {
          case ex: RemoteException => chatArea.appendText("Couldn't send to on recipient")
        }
      }
      chatField.text = ""
    }
  }

  stage = new JFXApp.PrimaryStage {
    title = "RMI Chat"
    scene = new Scene(600, 600) {
      val chatScroll = new ScrollPane
      chatScroll.content = chatArea
      val userScroll = new ScrollPane
      userScroll.content = userList
      val leftborder = new BorderPane
      leftborder.top = userScroll
      val topborder = new BorderPane
      topborder.center = chatField
      val border = new BorderPane
      border.top = topborder
      border.left = leftborder//userScroll
      border.center = chatScroll
      root = border
    }
  }
  def name: String = _name

  def message(sender: RemoteClient, text: String): Unit = Platform.runLater {
    chatArea.appendText(sender.name + " : " + text+"\n")
  }
  def clientUpdate(clients: Seq[RemoteClient]): Unit = Platform.runLater {
    this.clients = clients
    if (userList != null) userList.items = ObservableBuffer(clients.map { c =>
      c.name
    })
  }
}