package cocktail.api

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = CocktailApiServer.run[IO]
}
