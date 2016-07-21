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

  // the range of our numbers is [1, 100], but the `nextInt` method has a range of [from, to)
  def generate(): Int = 42

  def receive = {
    // the player has provided a guess, check if it's correct and send the appropriate response
    case Player.Guess(n: Int) => {
      if (n == number) player ! Game.Win
      else player ! Game.TryAgain
    }
    
    // the player wants to restart the game, generate a new number and inform the player that a new round has begun
    case Player.Restart => {
      number = generate
      player ! Game.Ready
    }
    
    // the player has left, shut down the actor system
    case Player.Leave => {
      context.system.shutdown
    }
  }
  
  // inform the player that the game is ready
  override def preStart() = {
    player ! Game.Ready
  }
}
