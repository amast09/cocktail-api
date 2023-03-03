package cocktail.api

import io.circe.generic.auto._
import io.circe.parser._
import cats.data.NonEmptyList
import cats.effect.IO
import cats.data.Validated
import cats.syntax.either._
import cats.syntax.traverse._
import io.circe.DecodingFailure
import io.circe.ParsingFailure

sealed trait RawCocktailParserError

sealed trait InvalidJson                                 extends RawCocktailParserError
case class JsonParsingFailure(failure: ParsingFailure)   extends InvalidJson
case class JsonDecodingFailure(failure: DecodingFailure) extends InvalidJson

case class InvalidGlass(glass: String) extends RawCocktailParserError

sealed trait InvalidIngredient                                           extends RawCocktailParserError
case class InvalidUnit(ingredient: RawCocktailIngredient)                extends InvalidIngredient
case class MissingAmount(ingredient: RawCocktailIngredient)              extends InvalidIngredient
case class InvalidIntQuantity(ingredient: RawCocktailIngredient)         extends InvalidIngredient
case class InvalidAmountSpecification(ingredient: RawCocktailIngredient) extends InvalidIngredient

final case class RawCocktailIngredient(ingredient: String, unit: String, amount: Option[Double]) {
  def toIngredient(): Either[InvalidIngredient, CocktailIngredient] =
    parseAmount()
      .map(amount => CocktailIngredient(Ingredient(this.ingredient), amount))

  def parseAmount(): Either[InvalidIngredient, Amount] = {
    val maybeAmount = this.amount.toRight(MissingAmount(this))

    this.unit.toLowerCase match {
      case "cl"       => maybeAmount.map(Centiliter)
      case "teaspoon" => maybeAmount.map(Teaspoon)
      case "relative" => maybeAmount.map(Relative)
      case "slice"    => maybeAmount.map(Slice)
      case "dash"     => maybeAmount.flatMap(intFromDouble).map(Dash)
      case "drop"     => maybeAmount.flatMap(intFromDouble).map(Drop)
      case "sprig"    => maybeAmount.flatMap(intFromDouble).map(Sprig)
      case "pinch"    => maybeAmount.flatMap(intFromDouble).map(Pinch)
      case "splash" =>
        this.amount match {
          case None    => Right(Splash())
          case Some(_) => Left(InvalidAmountSpecification(this))
        }
      case "totaste" =>
        this.amount match {
          case None    => Right(ToTaste())
          case Some(_) => Left(InvalidAmountSpecification(this))
        }
      case "topoff" =>
        this.amount match {
          case None    => Right(TopOff())
          case Some(_) => Left(InvalidAmountSpecification(this))
        }
      case _ => Left(InvalidUnit(this))
    }
  }

  private def intFromDouble(d: Double): Either[InvalidIntQuantity, Int] = {
    val intValue     = d.toInt
    val backToDouble = intValue.toDouble

    d == backToDouble match {
      case true  => Right(intValue)
      case false => Left(InvalidIntQuantity(this))
    }
  }
}

final case class RawCocktail(name: String, glass: String, ingredients: NonEmptyList[RawCocktailIngredient]) {
  def toCocktail(): Validated[NonEmptyList[RawCocktailParserError], Cocktail] = {
    val maybeIngredients: Validated[NonEmptyList[RawCocktailParserError], NonEmptyList[CocktailIngredient]] =
      this.ingredients
        .traverse(rawIngredient => rawIngredient.toIngredient().toValidatedNel)

    val maybeGlass: Validated[NonEmptyList[RawCocktailParserError], Glass] = this.maybeGlass().toValidatedNel

    (maybeIngredients, maybeGlass) match {
      case (Validated.Valid(ingredients), Validated.Valid(glass)) =>
        Validated.Valid(
          Cocktail(
            name = this.name,
            glass = glass,
            ingredients = ingredients
          )
        )
      case (Validated.Valid(_), Validated.Invalid(errors)) => Validated.Invalid(errors)
      case (Validated.Invalid(errors), Validated.Valid(_)) => Validated.Invalid(errors)
      case (Validated.Invalid(ingredientErrors), Validated.Invalid(glassErrors)) =>
        Validated.Invalid(ingredientErrors ::: glassErrors)
    }
  }

  def maybeGlass(): Either[InvalidGlass, Glass] =
    this.glass match {
      case "martini"         => Right(Martini)
      case "old-fashioned"   => Right(OldFashioned)
      case "collins"         => Right(Collins)
      case "highball"        => Right(Highball)
      case "champagne-flute" => Right(ChampagneFlute)
      case "margarita"       => Right(Margarita)
      case "champagne-tulip" => Right(ChampagneTulip)
      case "hurricane"       => Right(Hurricane)
      case "shot"            => Right(Shot)
      case "hot-drink"       => Right(HotDrink)
      case "white-wine"      => Right(WhiteWine)
      case _                 => Left(InvalidGlass(this.glass))
    }
}
final case class RawCocktailData(data: List[RawCocktail])
object RawCocktailData {
  def fromFile(fileName: String): IO[Validated[NonEmptyList[RawCocktailParserError], List[Cocktail]]] = {
    val cocktailsIO = ResourceIO(fileName).read()

    cocktailsIO
      .map { jsonString =>
        val maybeRawCocktails = for {
          parsedJson         <- parse(jsonString).leftMap(JsonParsingFailure)
          parsedCocktailData <- parsedJson.as[RawCocktailData].leftMap(JsonDecodingFailure)
        } yield parsedCocktailData.data

        maybeRawCocktails.toValidatedNel match {
          case Validated.Invalid(e)          => Validated.Invalid(e)
          case Validated.Valid(rawCocktails) => rawCocktails.map(_.toCocktail()).sequence
        }
      }
  }
}

// TODO: add tests for the above code
