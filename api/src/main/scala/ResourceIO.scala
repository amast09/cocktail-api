package mixologist

import cats.effect.kernel.Resource
import cats.effect.IO
import scala.io.Source
import java.io.FileWriter

final case class ResourceIO(fileName: String) {

  def read(): IO[String] =
    Resource
      .make(
        IO(Source.fromResource(this.fileName))
      )(source => IO(source.close()))
      .use(source => IO(source.getLines().mkString("\n")))

  def write(content: String): IO[Unit] = Resource
    .make(
      IO(
        new FileWriter(this.fileName)
      )
    )(writer => IO(writer.close()))
    .use(writer => IO(writer.write(content)))
}
