package com.yalingunayer.guess

import akka.actor.Actor
import akka.actor.ActorSystem

object Application extends App {
  val system = ActorSystem()
  
  val game = system.actorOf(Game.props)
}
