module Msg exposing (Msg(..))

import Api.Data exposing (CocktailApiIngredient, CocktailApiMixologistRoutesIngredientsResponse)
import Http


type Msg
    = IngredientsRequestRetry
    | IngredientsRequestComplete (Result Http.Error CocktailApiMixologistRoutesIngredientsResponse)
    | ToggleIngredient CocktailApiIngredient
