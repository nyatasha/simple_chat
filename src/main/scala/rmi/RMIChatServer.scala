package rmi

import java.rmi.{Naming, RemoteException}
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject

import scala.collection.mutable

@remote trait RemoteServer{
  def connect(client: RemoteClient): Unit
  def disconnect(client: RemoteClient): Unit
  def getClients: Seq[RemoteClient]
  def getDeadClients: Seq[RemoteClient]
  def publicMessage(client: RemoteClient, text: String): Unit
}
object RMIChatServer extends UnicastRemoteObject with App with RemoteServer {
  LocateRegistry.createRegistry(1099)
  Naming.rebind("ChatServer", this)

  private val clients = mutable.Buffer[RemoteClient]()
  private val deadClients = mutable.Buffer[RemoteClient]()

  def connect(client: RemoteClient): Unit = {
    if (deadClients contains client)
      deadClients -= client
    clients += client
    sendUpdate
  }

  def disconnect(client: RemoteClient): Unit = {
    clients -= client
    deadClients += client
    sendUpdate
  }

  def getClients: Seq[RemoteClient] = {
    clients
  }

  def getDeadClients: Seq[RemoteClient] = {
    deadClients
  }

  def publicMessage(client: RemoteClient, text: String): Unit = {
    val message = client.name + " : " + text
    clients.foreach(_.message(client, message))
  }

  private def sendUpdate: Unit = {
    val dead = clients.filter(c =>
      try {
        c.clientUpdate(clients)
        c.deadClientUpdate(deadClients)
        false
      } catch {
        case ex: RemoteException => true
      })
    clients --= dead
  }
}

