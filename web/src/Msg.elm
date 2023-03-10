module Msg exposing (Msg(..))

import Api.Data exposing (Ingredient, IngredientsResponse)
import Http


type Msg
    = IngredientsRequestRetry
    | IngredientsRequestComplete (Result Http.Error IngredientsResponse)
    | ToggleIngredient Ingredient
