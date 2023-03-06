module Msg exposing (Msg(..))
import Cocktail exposing (Ingredient, IngredientsResponse)
import Http


type Msg
    = IngredientsRequestRetry
    | IngredientsRequestComplete (Result Http.Error IngredientsResponse)
    | ToggleIngredient Ingredient


-- type MsgTwo
--     = ToggleIngredient Ingredient
--     | IngredientsRequestRetry
--     | PotentialCocktailsRequestRetry
--     | IngredientsRequestComplete (Result Http.Error IngredientsResponse)
--     | PotentialCocktailsRequestComplete