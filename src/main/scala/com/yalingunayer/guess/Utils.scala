package com.yalingunayer.guess

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Utils {
  // asynchronously read a line from the standard input
  def readResponse: Future[String] = Future {
    scala.io.StdIn.readLine()
  }
  
  // try reading an integer from the standard input
  def readNumericResponse: Future[Option[Int]] = {
    readResponse.map(s => {
      try {
        Some(s.toInt)
      } catch {
        case _: Throwable => None
      }
    })
  }
  
  // convert a yes/no response to boolean for easier use
  def readBooleanResponse: Future[Boolean] = {
    readResponse.map(s => s match {
      case "y" | "yes" | "1" | "" => true
      case _ => false
    })
  }
}
