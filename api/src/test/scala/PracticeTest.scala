package mixologist

import munit.CatsEffectSuite
import munit.ScalaCheckEffectSuite

class PracticeTest extends CatsEffectSuite with ScalaCheckEffectSuite {
  test("the thing") {
    val flights: List[Flight] = List(Flight("A", "B"), Flight("B", "C"), Flight("A", "C"))
    val expectedFlightList = Set(List("A", "C"), List("A", "B", "C"))
    
    val actualFlightList = Practice.getPossibleTrips("A", "C", flights)

    assertEquals(actualFlightList, expectedFlightList)
  }
}