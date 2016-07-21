package com.yalingunayer.guess

import akka.actor.Actor
import scala.util.Random
import akka.actor.Props

/**
 * Companion object to the `Game` class
 */
object Game {
  // message to send when the game is ready to be played
  case class Ready()

  // message to send to a player actor when the guess is correct
  case class Win()

  // message to send to a player actor when the guess is incorrect
  case class TryAgain()

  // follow the initialization technique from the first article 
  def props = Props(classOf[Game])
}

/**
 * The actor that represents the entire game
 */
class Game extends Actor {
  // pick a number right away
  var number = generate()

  // and also initialize the player actor
  val player = context.actorOf(Player.props(self))

  // the range of our numbers is [1, 100]
  def generate(): Int = Random.nextInt(99) + 1

  // we'll implement this later
  def receive = ???
}
