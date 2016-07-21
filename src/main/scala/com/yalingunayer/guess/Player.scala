package com.yalingunayer.guess

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef

/*
 * Companion object to the `Player` actor
 */
object Player {
  // the message to send when providing a guess
  case class Guess(n: Int)

  // the message to send when the user wants to play another round
  case class Restart()

  // the message to send when the user wants to exit
  case class Leave()

  // notice how we're passing the `ActorRef` to the game actor, even though it'll end up as the `parent`
  def props(game: ActorRef) = Props(classOf[Player], game)
}

class Player(game: ActorRef) extends Actor {
  // we'll implement this later
  def receive = ???
}
