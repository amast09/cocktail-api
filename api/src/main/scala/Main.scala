package cocktail.api

import cats.effect.{IO, IOApp}
import cats.implicits._
import com.comcast.ip4s._
import org.http4s.server.middleware.CORS
import org.http4s.headers.Origin
import org.http4s.ember.server.EmberServerBuilder
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import org.http4s.Uri

//  TODO: Rename `cocktail.api` => `mixologist`

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

        val mixologistApi = MixologistApi(cocktailService)
        val apiRoutes     = (mixologistApi.routes <+> mixologistApi.docsRoute).orNotFound

        val serviceWithCors = CORS.policy.withAllowOriginHost(
          Set(
            Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(5173)),
            Origin.Host(Uri.Scheme.https, Uri.RegName("mixologist.app"), None)
          )
        )(apiRoutes)

        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080") // TODO: move to config
          .withHttpApp(serviceWithCors)
          .build
          .useForever
    }
}
