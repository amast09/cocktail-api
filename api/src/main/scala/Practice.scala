package mixologist

import scala.collection.mutable.HashMap
// import scala.annotation.tailrec


sealed trait Tree
case class Node(leftBranch: Node, rightBranch: Node)
case object Leaf

case class Flight(origin: String, destination: String)


object Practice {
  type FlightTree = HashMap[String, List[String]]
  type FlightPaths = Set[List[String]]

  def getFlightPaths(destination: String, flightTree: FlightTree): FlightPaths = {
    // @tailrec
    def traverseFlightTree(airportsToVisit: List[String], visitedAirports: Set[String]): List[String] = {
      airportsToVisit match {
        case head :: next => {
          if (head == destination) {
            List(destination)
          } else {
            val additionalAirportsToVisit: List[String] = flightTree.getOrElse(head, List.empty[String]).filter(airport => !visitedAirports.contains(airport))
            head :: traverseFlightTree(additionalAirportsToVisit ++ next, visitedAirports + head)
          }
        }
        case Nil => List.empty[String]
      }
    }

    
  }
  

  def getPossibleTrips(origin: String, destination: String, availableFlights: List[Flight]): FlightPaths = {
    val flightTree = availableFlights.foldLeft(HashMap.empty[String, Set[String]]) {(acc, nextFlight) =>
      val existingFlightDestinations = acc.getOrElse(nextFlight.origin, Set())
      acc(nextFlight.origin) = existingFlightDestinations + nextFlight.destination
      acc
    }

    val flightPaths = getFlightPaths(destination, flightTree)

    Set()
  }
}
