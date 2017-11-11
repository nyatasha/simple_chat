package rmi

import java.rmi.{Naming, RemoteException}
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject

import scala.collection.mutable

@remote trait RemoteServer{
  def connect(client: RemoteClient): Unit
  def disconnect(client: RemoteClient): Unit
  def getClients: Seq[RemoteClient]
  def publicMessage(client: RemoteClient, text: String): Unit
}
object RMIChatServer extends UnicastRemoteObject with App with RemoteServer {
  LocateRegistry.createRegistry(1099)
  Naming.rebind("ChatServer", this)

  private val clients = mutable.Buffer[RemoteClient]()

  def connect(client: RemoteClient): Unit = {
    clients += client
  }
  def disconnect(client: RemoteClient): Unit={
    clients -= client
  }
  def getClients: Seq[RemoteClient]={
    clients
  }
  def publicMessage(client: RemoteClient, text: String): Unit={
    val message = client.name + " : " + text
    clients.foreach(_.message(client,message))
  }
}