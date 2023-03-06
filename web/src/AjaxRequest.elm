module AjaxRequest exposing (AjaxRequest(..))

type AjaxRequest a
    = Failure
    | Loading
    | Success a