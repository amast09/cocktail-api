package cocktail.api

import cats.data.NonEmptyList

sealed trait Glass
case object Martini        extends Glass
case object OldFashioned   extends Glass
case object Collins        extends Glass
case object Highball       extends Glass
case object ChampagneFlute extends Glass
case object Margarita      extends Glass
case object ChampagneTulip extends Glass
case object Hurricane      extends Glass
case object Shot           extends Glass
case object HotDrink       extends Glass
case object WhiteWine      extends Glass

sealed trait Amount
case class Centiliter(quantity: Double) extends Amount
case class Teaspoon(quantity: Double)   extends Amount
case class Relative(quantity: Double)   extends Amount
case class Slice(quantity: Double)      extends Amount
case class Dash(quantity: Int)          extends Amount
case class Drop(quantity: Int)          extends Amount
case class Sprig(quantity: Int)         extends Amount
case class Pinch(quantity: Int)         extends Amount

sealed trait AmountNoParams extends Amount
case object Splash          extends AmountNoParams
case object ToTaste         extends AmountNoParams
case object TopOff          extends AmountNoParams

final case class Ingredient(name: String)

final case class CocktailIngredient(ingredient: Ingredient, amount: Amount)

final case class Cocktail(name: String, glass: Glass, ingredients: NonEmptyList[CocktailIngredient])
