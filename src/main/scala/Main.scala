package cocktail.api

import cats.effect.{IO, IOApp}
import io.circe._
import io.circe.parser._
import cats.effect.unsafe.implicits._
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import cats.data.Validated.Invalid
import cats.data.Validated.Valid

object Main extends IOApp.Simple {
  val run = RawCocktailData
    .fromFile("iba-cocktails.json")
    .flatMap {
      case Invalid(cocktailDataErrors) =>
        println("API failed to boot due to invalid Cocktail Data")
        println(cocktailDataErrors)
        IO.pure(())
      case Valid(cocktailList) =>
        val cocktailService = CocktailServiceFromList(cocktailList)
        val httpRoutes = CocktailApiRoutes
          .cocktailRoutes(cocktailService)
          .orNotFound

        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpRoutes)
          .build
          .useForever
    }
}
