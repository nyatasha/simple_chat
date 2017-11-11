package rmi

@remote trait RemoteClient {
  def name: String
  def message(sender: RemoteClient, text: String): Unit
  def clientUpdate(clients: Seq[RemoteClient]): Unit
}
object RMIChatClient {

}
