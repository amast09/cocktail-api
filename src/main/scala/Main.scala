package cocktail.api

import cats.effect.{IO, IOApp}
import io.circe._
import io.circe.parser._
import cats.effect.unsafe.implicits._

//object Main extends IOApp.Simple {
object Main {
  def main(args: Array[String]) =
    RawCocktailData.fromFile("iba-cocktails.json").map(println).unsafeRunSync()

  // val run = CocktailApiServer.run[IO]
}
