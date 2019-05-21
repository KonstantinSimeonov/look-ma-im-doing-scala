import Math._
import scala.util.Random

case class Rgb(r: Double, g: Double, b: Double) {
  def toJS() = "[" + r + ", " + g + ", " + b + "]"
}

object Main {
  // approximate the rgb color of the light emitted by a black body
  // that has the provided kelvin temperature
  def rgbFromKelvin(kelvins: Double): Rgb = {
    val clamp = (x: Double) => Math.max(0, Math.min(255, x))
    val temp = kelvins / 100

    val r = if (temp <= 66)
        255
      else
        clamp(329.698727446 * Math.pow(temp - 60, -0.1332047592))

    val g = if (temp <= 66)
        clamp(99.4708025861 * Math.log(temp) - 161.1195681661)
      else
        clamp(288.1221695283 * Math.pow(temp - 60, -0.0755148492))

    val b = if (temp >= 66)
      255
    else if (temp <= 19)
      0
    else
      clamp(138.5177312231 * Math.log(temp - 10) - 305.0447927307)

    Rgb(r, g, b)
  }

  // geometrically approximate the closest rgb color that can be a
  // emitted by the light of a heated black body and the respective
  // kelvin temperature
  def closestKelvinToRgb(rgb: Rgb, curve: List[(Double, Rgb)]): (Double, Rgb) = {
    def dist(krgb: Rgb): Double = {
      val dr = rgb.r - krgb.r
      val dg = rgb.g - krgb.g
      val db = rgb.b - krgb.b
      dr * dr + dg * dg + db * db
    }
    def closer(x: (Double, Rgb), y: (Double, Rgb)) =
      if (dist(x._2) < dist(y._2)) x else y
    val closestPoint = curve.reduceLeft(closer)

    val low = Math.max(1000, closestPoint._1 - 200)
    val high = Math.min(closestPoint._1 + 200, 40000)

    val approxClosest = (low to high by 20.0).foldLeft(closestPoint)(
      (closest: (Double, Rgb), k: Double) => closer((k, rgbFromKelvin(k)), closest)
    )
    approxClosest
  }

  // generate sample results of the algorithm and convert to JS code
  def sampleResultsToJS(): String = {
    val kelvinCurveSamples = (1000.0 to 40000.0 by 500.0).map(x => (x, rgbFromKelvin(x))).toList

    val r = Random
    val randomColors = (0 to 400).toList.map(i =>
      Rgb(r.nextDouble * 255, r.nextDouble * 255, r.nextDouble * 255)
    )

    val jsApproximationsData = {
      val otherSamples = (1000.0 to 40000.0 by 400.0).map(rgbFromKelvin).toList
      (randomColors ++ otherSamples).map(rgb => {
        val (k, krgb) = closestKelvinToRgb(rgb, kelvinCurveSamples)
        "[" + rgb.toJS() + ", " + k + ", " + krgb.toJS() + "]"
      })
    }

    "const xs = [" + jsApproximationsData.mkString(", ") + "];"
  }

  def main(args: Array[String]) = println(sampleResultsToJS())
}
