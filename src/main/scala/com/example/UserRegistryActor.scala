package com.example

//#user-registry-actor
import akka.actor.{ Actor, ActorLogging, Props }

//#user-case-classes
final case class User(login: String, location: Option[String],company:Option[String])
final case class Users(users: Seq[User])
//#user-case-classes

object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  import UserRegistryActor._

  var users = Set.empty[User]

  def receive: Receive = {
    case GetUsers =>
      sender() ! Users(users.toSeq)
    case CreateUser(user) =>
      users += user
      sender() ! ActionPerformed(s"User ${user.login} created.")
    case GetUser(name) =>
      sender() ! users.find(_.login == name)
    case DeleteUser(name) =>
      users.find(_.login == name) foreach { user => users -= user }
      sender() ! ActionPerformed(s"User ${name} deleted.")
  }
}
//#user-registry-actor