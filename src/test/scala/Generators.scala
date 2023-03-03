package cocktail.api

import org.scalacheck.Gen
import cats.data.NonEmptyList

object Generators {
  def nonEmptyListGen[T](gen: Gen[T]): Gen[NonEmptyList[T]] = for {
    head <- gen
    tail <- Gen.listOf(gen)
  } yield NonEmptyList(head, tail)

  val glassGen: Gen[Glass] = Gen.oneOf(
    Martini,
    OldFashioned,
    Collins,
    Highball,
    ChampagneFlute,
    Margarita,
    ChampagneTulip,
    Hurricane,
    Shot,
    HotDrink,
    WhiteWine
  )

  val centiliterGen: Gen[Centiliter] = Gen.posNum[Double].map(Centiliter)
  val teaspoonGen: Gen[Teaspoon]     = Gen.posNum[Double].map(Teaspoon)
  val relativeGen: Gen[Relative]     = Gen.chooseNum[Double](0, 1).map(Relative)
  val sliceGen: Gen[Slice]           = Gen.posNum[Double].map(Slice)
  val dashGen: Gen[Dash]             = Gen.posNum[Int].map(Dash)
  val dropGen: Gen[Drop]             = Gen.posNum[Int].map(Drop)
  val sprigGen: Gen[Sprig]           = Gen.posNum[Int].map(Sprig)
  val pinchGen: Gen[Pinch]           = Gen.posNum[Int].map(Pinch)

  val amountGen: Gen[Amount] = Gen.oneOf(
    centiliterGen,
    teaspoonGen,
    relativeGen,
    sliceGen,
    dashGen,
    dropGen,
    sprigGen,
    pinchGen,
    Gen.const(Splash()),
    Gen.const(ToTaste()),
    Gen.const(TopOff())
  )

  val ingredientGen: Gen[Ingredient] = Gen.uuid.map(uuid => Ingredient(uuid.toString()))

  val cocktailIngredientGen: Gen[CocktailIngredient] = for {
    ingredient <- ingredientGen
    amount     <- amountGen
  } yield CocktailIngredient(ingredient, amount)

  val cocktailGen: Gen[Cocktail] = for {
    uuidName    <- Gen.uuid
    glass       <- glassGen
    ingredients <- nonEmptyListGen(cocktailIngredientGen)
  } yield Cocktail(uuidName.toString(), glass, ingredients)
}
