package mixologist

import cats.effect.{IO, IOApp}
import cats.implicits._
import org.http4s.server.middleware.CORS
import org.http4s.headers.Origin
import org.http4s.ember.server.EmberServerBuilder
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import org.http4s.Uri

object Main extends IOApp.Simple {
  val run = RawCocktailData
    .fromFile("iba-cocktails.json")
    .flatMap {
      case Invalid(cocktailDataErrors) =>
        println("API failed to boot due to invalid Cocktail Data")
        println(cocktailDataErrors)
        IO.pure(())
      case Valid(cocktailList) =>
        val mixologistService = MixologistServiceFromList(cocktailList)

        val mixologistApi = MixologistApi(mixologistService)
        val apiRoutes     = (mixologistApi.routes <+> MixologistApi.docsRoute).orNotFound

        val serviceWithCors = CORS.policy.withAllowOriginHost(
          Set(
            Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(5173)),
            Origin.Host(Uri.Scheme.https, Uri.RegName("mixologist.app"), None)
          )
        )(apiRoutes)

        EmberServerBuilder
          .default[IO]
          .withHost(Environment.getFromEnv.apiHost)
          .withPort(Environment.getFromEnv.apiPort)
          .withHttpApp(serviceWithCors)
          .build
          .useForever
    }
}
