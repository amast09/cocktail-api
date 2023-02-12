package cocktail.api

import cats.effect.Async
import cats.syntax.all._
import com.comcast.ip4s._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object CocktailApiServer {

  def run[F[_]: Async]: F[Nothing] = {
    for {
      client <- EmberClientBuilder.default[F].build
      cocktailService = CocktailService.impl[F]

      httpApp = CocktailApiRoutes
        .cocktailRoutes[F](cocktailService)
        .orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <-
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}
