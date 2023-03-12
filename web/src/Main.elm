module Main exposing (Model, main)

import AjaxRequest exposing (AjaxRequest)
import Api exposing (send, withBasePath)
import Api.Data
    exposing
        ( Amount(..)
        , CocktailIngredient
        , Ingredient
        , PotentialCocktail
        , PotentialCocktailsJsonPayload
        )
import Api.Request.Default as MixologistApi
import Browser
import Html exposing (Html, button, div, h3, label, li, p, text, ul)
import Html.Events exposing (onClick)
import IngredientCheckbox
import Msg exposing (Msg(..))
import Set exposing (Set)


apiBasePath : String
apiBasePath =
    "http://localhost:8080"


isChecked : Set String -> String -> Bool
isChecked checkedIngredients candaditeIngredient =
    Set.member candaditeIngredient checkedIngredients


toggleElement : comparable -> Set comparable -> Set comparable
toggleElement elementToToggle setToToggle =
    if Set.member elementToToggle setToToggle then
        Set.remove elementToToggle setToToggle

    else
        Set.insert elementToToggle setToToggle


getIngredients : Cmd Msg
getIngredients =
    send IngredientsRequestComplete (withBasePath apiBasePath MixologistApi.getIngredients)


getPotentialCocktails : PotentialCocktailsJsonPayload -> Cmd Msg
getPotentialCocktails payload =
    send PotentialCocktailsRequestComplete (withBasePath apiBasePath (MixologistApi.postPotentialCocktails payload))


type alias Model =
    { ingredientsRequest : AjaxRequest (List Ingredient)
    , checkedIngredients : Set String
    , maybePotentialIngredientsRequest : Maybe (AjaxRequest (List PotentialCocktail))
    }


main : Program () Model Msg
main =
    Browser.element { init = init, update = update, subscriptions = subscriptions, view = view }


init : () -> ( Model, Cmd Msg )
init _ =
    ( { ingredientsRequest = AjaxRequest.Loading, checkedIngredients = Set.empty, maybePotentialIngredientsRequest = Nothing }, getIngredients )


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



-- TODO: Make this less naive


renderAmount : Amount -> Html Msg
renderAmount amount =
    case amount of
        AmountCentiliter centiliter ->
            text ("centiliter(s)" ++ String.fromFloat centiliter.quantity)

        AmountRelative relative ->
            text ("parts(s)" ++ String.fromFloat relative.quantity)

        AmountTeaspoon teaspoon ->
            text ("teaspoon(s)" ++ String.fromFloat teaspoon.quantity)

        AmountSlice slice ->
            text ("slice(s)" ++ String.fromFloat slice.quantity)

        AmountDash dash ->
            text ("dash(s)" ++ String.fromInt dash.quantity)

        AmountDrop drop ->
            text ("drop(s)" ++ String.fromInt drop.quantity)

        AmountPinch pinch ->
            text ("pinch(s)" ++ String.fromInt pinch.quantity)

        AmountSprig sprig ->
            text ("sprig(s)" ++ String.fromInt sprig.quantity)

        AmountSplash _ ->
            text "splash"

        AmountToTaste _ ->
            text "To Taste"

        AmountTopOff _ ->
            text "Top Off"


renderIngredient : Ingredient -> Html Msg
renderIngredient ingredient =
    li [] [ p [] [ text ingredient.name ] ]


renderCocktailIngredient : CocktailIngredient -> Html Msg
renderCocktailIngredient cocktailIngredient =
    li [] [ label [] [ text cocktailIngredient.ingredient.name ], p [] [ renderAmount cocktailIngredient.amount ] ]


renderPotentialCocktail : PotentialCocktail -> Html Msg
renderPotentialCocktail p =
    div []
        [ h3 [] [ text p.cocktail.name ]
        , label [] [ text "Ingredients" ]
        , ul []
            (case p.cocktail.ingredients of
                Just ingredients ->
                    List.map renderCocktailIngredient ingredients

                Nothing ->
                    [ text "" ]
            )
        , label [] [ text ("Missing Ingredients - " ++ Maybe.withDefault "0" (Maybe.map (\missingIngredients -> String.fromInt (List.length missingIngredients)) p.missingIngredients)) ]
        , ul []
            (case p.missingIngredients of
                Just ingredients ->
                    List.map renderIngredient ingredients

                Nothing ->
                    [ text "" ]
            )
        ]


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        IngredientsRequestRetry ->
            ( { ingredientsRequest = AjaxRequest.Loading
              , checkedIngredients = Set.empty
              , maybePotentialIngredientsRequest = Nothing
              }
            , getIngredients
            )

        IngredientsRequestComplete result ->
            case result of
                Ok ingredientsResponse ->
                    case ingredientsResponse.data of
                        Just ingredients ->
                            ( { ingredientsRequest = AjaxRequest.Success ingredients
                              , checkedIngredients = Set.empty
                              , maybePotentialIngredientsRequest = Nothing
                              }
                            , Cmd.none
                            )

                        Nothing ->
                            ( { ingredientsRequest = AjaxRequest.Failure
                              , checkedIngredients = Set.empty
                              , maybePotentialIngredientsRequest = Nothing
                              }
                            , Cmd.none
                            )

                Err _ ->
                    ( { ingredientsRequest = AjaxRequest.Failure
                      , checkedIngredients = Set.empty
                      , maybePotentialIngredientsRequest = Nothing
                      }
                    , Cmd.none
                    )

        ToggleIngredient toggledIngredient ->
            ( { model | checkedIngredients = toggleElement toggledIngredient.name model.checkedIngredients }, Cmd.none )

        PotentialCocktailsRequestSubmitted ->
            ( { model | maybePotentialIngredientsRequest = Just AjaxRequest.Loading }
            , getPotentialCocktails (PotentialCocktailsJsonPayload (Just (List.map Ingredient (Set.toList model.checkedIngredients))))
            )

        PotentialCocktailsRequestComplete result ->
            case result of
                Ok potentialCocktailsResponse ->
                    case potentialCocktailsResponse.data of
                        Just potentialCocktails ->
                            ( { model | maybePotentialIngredientsRequest = Just (AjaxRequest.Success potentialCocktails) }, Cmd.none )

                        Nothing ->
                            ( { model | maybePotentialIngredientsRequest = Just AjaxRequest.Failure }, Cmd.none )

                Err _ ->
                    ( { model | maybePotentialIngredientsRequest = Just AjaxRequest.Failure }, Cmd.none )


view : Model -> Html Msg
view model =
    div []
        [ case model.ingredientsRequest of
            AjaxRequest.Failure ->
                div []
                    [ text "Failed to load ingredients"
                    , button [ onClick IngredientsRequestRetry ] [ text "Try Again!" ]
                    ]

            AjaxRequest.Loading ->
                text "Loading ingredients..."

            AjaxRequest.Success ingredients ->
                div []
                    [ div []
                        (List.map (\i -> IngredientCheckbox.component { ingredient = i, isChecked = isChecked model.checkedIngredients i.name, onCheck = ToggleIngredient }) ingredients)
                    , button
                        [ onClick PotentialCocktailsRequestSubmitted ]
                        [ text "Find Cocktails" ]
                    ]
        , case model.maybePotentialIngredientsRequest of
            Nothing ->
                text ""

            Just potentialIngredientsRequest ->
                case potentialIngredientsRequest of
                    AjaxRequest.Failure ->
                        div []
                            [ text "Failed to load potential cocktails"
                            , button [ onClick IngredientsRequestRetry ] [ text "Try Again!" ]
                            ]

                    AjaxRequest.Loading ->
                        text "Loading potential cocktails..."

                    AjaxRequest.Success potentialCocktails ->
                        div [] (List.map renderPotentialCocktail potentialCocktails)
        ]
