package com.yalingunayer.guess

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
  // perform an `ask` operation, and continue with `then` on success, or `stopWithError` on failure
  def askAndThen[T](ask: Future[T])(then: T => Any) {
    // go into the idle state immediately
    context.become(idle)
    
    ask onComplete {
      case Success(t) => then(t)
      case Failure(t: Throwable) => stopWithError(t)
    }
  }

  // ask for the player's guess and act based on the outcome
  def askForGuess = {
    println("Pick a number between 1 and 100 (inclusive)")

    askAndThen(Utils.readNumericResponse) {
      // the user has provided an answer, send it to the game actor and wait for the next step
      case Some(value: Int) => {
        game ! Player.Guess(value)
        context.become(waitingForRoundResult)
      }

      // the user wants to exit, just stop the player actor and the game actor will shut itself down
      case None => stop
    }
  }

  // ask the player if they'd like to take another try
  def askForRetry = {
    println("Aww, that's not correct. Try again? (Y/n)")
    
    askAndThen(Utils.readBooleanResponse) {
      // they do, so we can now ask for their guess
      case true => askForGuess

      // they don't, just stop the game
      case false => stop
    }
  }

  // ask the player if they would like to restart the game for another round
  def askForRestart = {
    println("You win! Play another round? (Y/n)")

    askAndThen(Utils.readBooleanResponse) {
      // they do, so we can restart the game
      case true => {
        game ! Player.Restart
        context.become(initializing)
      }

      // they don't, just stop the game
      case false => stop
    }
  }

  def stop {
    println(f"Goodbye!")
    game ! Player.Leave
    context.stop(self)
  }

  def stopWithError(t: Throwable) = {
    System.err.println(f"An error has occurred while reading the user's input $t")
    game ! Player.Leave
    context.stop(self)
  }

  // the default behavior or state is the `initializing` state
  def receive = initializing
  
  // this is a shortcut for an empty behavior, where all messages are ignored
  def idle = Actor.emptyBehavior

  def initializing: Receive = {
    case Game.Ready => askForGuess
  }

  def waitingForRoundResult: Receive = {
    case Game.Win => askForRestart
    case Game.TryAgain => askForRetry
  }
}
