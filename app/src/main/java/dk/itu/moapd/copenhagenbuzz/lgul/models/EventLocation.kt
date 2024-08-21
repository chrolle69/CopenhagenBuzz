package dk.itu.moapd.copenhagenbuzz.lgul.models

data class EventLocation(
    var address: String ?= "N/A",
    var lng: Double ?= 0.0,
    var lat: Double ?= 0.0
)

