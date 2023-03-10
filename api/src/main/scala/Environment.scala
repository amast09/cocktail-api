package mixologist

import com.comcast.ip4s.{Host, Port}

case class MixologistEnvironment(apiVersion: String, apiPort: Port, apiHost: Host)

object Environment {
  def getFromEnv: MixologistEnvironment = MixologistEnvironment(
    apiVersion = scala.util.Properties.envOrElse("MIXOLOGIST_API_VERSION", "Unknown"),
    apiPort = Port.fromString(scala.util.Properties.envOrElse("MIXOLOGIST_API_PORT", "8080")).get,
    apiHost = Host.fromString(scala.util.Properties.envOrElse("MIXOLOGIST_API_HOST", "0.0.0.0")).get
  )
}
