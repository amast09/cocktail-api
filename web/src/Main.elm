module Main exposing (main)

import AjaxRequest exposing (AjaxRequest)
import Api exposing (send, withBasePath)
import Api.Data exposing (Ingredient, PotentialCocktail, PotentialCocktailsJsonPayload)
import Api.Request.Default as MixologistApi
import Browser
import Html exposing (Html, button, div, text)
import Html.Events exposing (onClick)
import Http exposing (..)
import IngredientCheckbox
import Msg exposing (Msg(..))
import Set exposing (Set(..))


errorToString : Http.Error -> String
errorToString error =
    case error of
        BadUrl url ->
            "The URL " ++ url ++ " was invalid"

        Timeout ->
            "Unable to reach the server, try again"

        NetworkError ->
            "Unable to reach the server, check your network connection"

        BadStatus 500 ->
            "The server had a problem, try again later"

        BadStatus 400 ->
            "Verify your information and try again"

        BadStatus _ ->
            "Unknown error"

        BadBody errorMessage ->
            errorMessage


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
    send PotentialCocktailsRequestComplete (withBasePath apiBasePath (MixologistApi.postPotentialIngredients payload))


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


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        IngredientsRequestRetry ->
            ( { ingredientsRequest = AjaxRequest.Loading, checkedIngredients = Set.empty, maybePotentialIngredientsRequest = Nothing }, getIngredients )

        IngredientsRequestComplete result ->
            case result of
                Ok ingredientsResponse ->
                    case ingredientsResponse.data of
                        Just ingredients ->
                            ( { ingredientsRequest = AjaxRequest.Success ingredients, checkedIngredients = Set.empty, maybePotentialIngredientsRequest = Nothing }, Cmd.none )

                        Nothing ->
                            ( { ingredientsRequest = AjaxRequest.Failure, checkedIngredients = Set.empty, maybePotentialIngredientsRequest = Nothing }, Cmd.none )

                Err _ ->
                    ( { ingredientsRequest = AjaxRequest.Failure, checkedIngredients = Set.empty, maybePotentialIngredientsRequest = Nothing }, Cmd.none )

        ToggleIngredient toggledIngredient ->
            ( { model | checkedIngredients = toggleElement toggledIngredient.name model.checkedIngredients }, Cmd.none )

        PotentialCocktailsRequestSubmitted ->
            ( { model | maybePotentialIngredientsRequest = Just AjaxRequest.Loading }, getPotentialCocktails (PotentialCocktailsJsonPayload (Just (List.map Ingredient (Set.toList model.checkedIngredients)))) )

        PotentialCocktailsRequestComplete result ->
            case result of
                Ok potentialCocktailsResponse ->
                    case potentialCocktailsResponse.data of
                        Just potentialCocktails ->
                            ( { model | maybePotentialIngredientsRequest = Just (AjaxRequest.Success potentialCocktails) }, Cmd.none )

                        Nothing ->
                            ( { model | maybePotentialIngredientsRequest = Just AjaxRequest.Failure }, Cmd.none )

                Err error ->
                    -- TODO: Currently failing due to NonEmptyList runtime decoding differing from what is output by
                    --  generated openapispec, look to define custom decoder for NEL
                    let
                        _ =
                            Debug.log "error" (errorToString error)
                    in
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
                        text "potential cocktails loaded"
        ]
