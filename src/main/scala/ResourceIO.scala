package cocktail.api

import cats.effect.kernel.Resource
import cats.effect.IO
import scala.io.Source

final case class ResourceIO(fileName: String) {

  def read(): IO[String] =
    Resource
      .make(
        IO(Source.fromResource(this.fileName))
      )(source => IO(source.close()))
      .use(source => IO(source.getLines().mkString("\n")))
}
