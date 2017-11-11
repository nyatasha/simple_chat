package rmi

@remote trait RemoteServer{
  def connect(client: RemoteClient): Unit
  def disconnect(client: RemoteClient): Unit
  def getClients: Seq[RemoteClient]
  def publicMessage(client: RemoteClient, text: String): Unit
}

object RMIChatServer {

}
