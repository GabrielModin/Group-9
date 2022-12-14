import React, { Component } from 'react';
import LocationFormAnswers from './LocationFormAnswers';
import LocationFormMedia from './LocationFormMedia';
import LocationFormUseQR from './LocationFormUseQR';
import ModGeolocate from './ModGeolocate';
//import axios from 'axios';


/**
 * TODO:
 *      
 *      setState för media så de kan få rätt url
 *      ordna lägg till content med begränsning på 5
 *      se över API anrop och lägg till TA BORT ett svar
 *      
 *      skicka formulär
 * 
 * DONE:
 *      radiobutton för correct answer
 */

export default class LocationForm extends Component {
    constructor(props) {
        super(props);
        //Här definierar vi alla förifyllda värden baserat på props


        this.state = {
            locationName: this.props.currentData.name,
            locationInfo: this.props.currentData.text_info,
            locationID: this.props.currentData.location_id,
            locationMediaUrl: this.props.currentData.media[0].mediaURL,
            locationMediaType: "",
            locationMediaExternal: this.props.currentData.media[0].externalMedia,
            locationUseQR: this.props.currentData.qr,
            locationDirections: this.props.currentData.directions,
            locationLongitude: this.props.currentData.x_coords,
            locationLatitude: this.props.currentData.y_coords,

            locationAnswer1: "",
            locationContentID1: "",
            locationCorrect1: "",

            locationAnswer2: "",
            locationContentID2: "",
            locationCorrect2: "",

            locationAnswer3: "",
            locationContentID3: "",
            locationCorrect3: "",

            locationAnswer4: "",
            locationContentID4: "",
            locationCorrect4: "",

            locationAnswer5: "",
            locationContentID5: "",
            locationCorrect5: ""            
        };

        this.handleAddAnswer = this.handleAddAnswer.bind(this);
        this.handleRemoveAnswer = this.handleRemoveAnswer.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleGeoLocation = this.handleGeoLocation.bind(this);
        this.handleContentIDState = this.handleContentIDState.bind(this);
        this.handleMediaOptions = this.handleMediaOptions.bind(this);
        //this.handleRenderAnswers = this.handleRenderAnswers.bind(this);
        this.setParentMediaUrl = this.setParentMediaUrl.bind(this);
    }
    componentDidMount() {        

        if (this.props.currentData.media[0].mediaType === "video") {
            this.setState({ locationMediaType: false })
        } else if (this.props.currentData.media[0].mediaType === "image") {
            this.setState({ locationMediaType: true })
        } else {
            this.setState({ locationMediaType: false })
        }

        if (this.props.currentData.media[0].externalMedia === false) {

            this.setState({ locationMediaExternal: false })
        } else if (this.props.currentData.media[0].externalMedia === true) {
            this.setState({ locationMediaExternal: true })
        }



        let contentLength = this.props.currentData.content.length
        if (this.props.currentData.content.length !== 0) {

            switch (contentLength) {
                case 1:
                    this.setState({
                        locationAnswer1: this.props.currentData.content[0].answer,
                        locationContentID1: this.props.currentData.content[0]["content-id"],
                        locationCorrect1: this.props.currentData.content[0].correct
                    })
                    break;
                case 2:
                    this.setState({
                        locationAnswer1: this.props.currentData.content[0].answer,
                        locationContentID1: this.props.currentData.content[0]["content-id"],
                        locationCorrect1: this.props.currentData.content[0].correct,
                        locationAnswer2: this.props.currentData.content[1].answer,
                        locationContentID2: this.props.currentData.content[1]["content-id"],
                        locationCorrect2: this.props.currentData.content[1].correct
                    })
                    break;
                case 3:
                    this.setState({
                        locationAnswer1: this.props.currentData.content[0].answer,
                        locationContentID1: this.props.currentData.content[0]["content-id"],
                        locationCorrect1: this.props.currentData.content[0].correct,
                        locationAnswer2: this.props.currentData.content[1].answer,
                        locationContentID2: this.props.currentData.content[1]["content-id"],
                        locationCorrect2: this.props.currentData.content[1].correct,
                        locationAnswer3: this.props.currentData.content[2].answer,
                        locationContentID3: this.props.currentData.content[2]["content-id"],
                        locationCorrect3: this.props.currentData.content[2].correct
                    })
                    break;
                case 4:
                    this.setState({
                        locationAnswer1: this.props.currentData.content[0].answer,
                        locationContentID1: this.props.currentData.content[0]["content-id"],
                        locationCorrect1: this.props.currentData.content[0].correct,
                        locationAnswer2: this.props.currentData.content[1].answer,
                        locationContentID2: this.props.currentData.content[1]["content-id"],
                        locationCorrect2: this.props.currentData.content[1].correct,
                        locationAnswer3: this.props.currentData.content[2].answer,
                        locationContentID3: this.props.currentData.content[2]["content-id"],
                        locationCorrect3: this.props.currentData.content[2].correct,
                        locationAnswer4: this.props.currentData.content[3].answer,
                        locationContentID4: this.props.currentData.content[3]["content-id"],
                        locationCorrect4: this.props.currentData.content[3].correct
                    })
                    break;
                case 5:
                    this.setState({
                        locationAnswer1: this.props.currentData.content[0].answer,
                        locationContentID1: this.props.currentData.content[0]["content-id"],
                        locationCorrect1: this.props.currentData.content[0].correct,
                        locationAnswer2: this.props.currentData.content[1].answer,
                        locationContentID2: this.props.currentData.content[1]["content-id"],
                        locationCorrect2: this.props.currentData.content[1].correct,
                        locationAnswer3: this.props.currentData.content[2].answer,
                        locationContentID3: this.props.currentData.content[2]["content-id"],
                        locationCorrect3: this.props.currentData.content[2].correct,
                        locationAnswer4: this.props.currentData.content[3].answer,
                        locationContentID4: this.props.currentData.content[3]["content-id"],
                        locationCorrect4: this.props.currentData.content[3].correct,
                        locationAnswer5: this.props.currentData.content[4].answer,
                        locationContentID5: this.props.currentData.content[4]["content-id"],
                        locationCorrect5: this.props.currentData.content[4].correct
                    })
                    break;
                default:
                    console.log("Case not found");
                    break;
            }
        }
    }

    handleMediaOptions(mediaType, externalMedia) {
        console.log("HANDLE MEDIA OPTIONS")
        this.setState({
            locationMediaType: mediaType,
            locationMediaExternal: externalMedia
        })
    }

    handleContentIDState(content) {
        let i = 1;

        content.forEach(item => {
            let stateKey = "locationContentID" + i.toString();
            this.setState({ [stateKey]: item["content-id"] })
            i++
        });

    }

    handleAddAnswer(locationID) {
        /**
         * Add a new question
         */

        this.props.callAddAnswer(locationID);
    }
    handleRemoveAnswer(locationID, contentID) {
        this.props.callRemoveAnswer(locationID, contentID)
    }

    handleGeoLocation(long, lat) {
        /**
         * fetches geolocation data Longitude and Latitude and adds the coords to the current state-object
         */
        this.setState({
            locationLongitude: long,
            locationLatitude: lat
        });
        console.log("Denna knapp ska trigga hämtning av geodata");
    }

    handleInputChange(event) {
        function youtubeUrlToEmbedUrl(youtubeUrl) {
            // First, check if the URL is a valid YouTube URL
            var youtubeRegex = /^(https?:\/\/)?(www\.)?(m\.)?(youtube\.com|youtu\.be)\/.+$/;
            if (!youtubeRegex.test(youtubeUrl)) {
                return false;
            }

            // If the URL is a valid YouTube URL, then extract the video ID
            var videoId = youtubeUrl.split("v=")[1];
            var ampersandIndex = videoId.indexOf("&");
            if (ampersandIndex !== -1) {
                videoId = videoId.substring(0, ampersandIndex);
            }

            // Use the video ID to create the embed URL
            var embedUrl = "https://www.youtube.com/embed/" + videoId;

            return embedUrl;
        }
        console.log("HANDLE INPUT CHANGE I LocationForm.js")
        //här lyssnar vi på förändring [name] anpassar sig till name i varje inputfält. 
        //Lite annorlunda om man använder annat än type="text/number" men går att lösa förstås
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        //const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
        if ((name === "locationMediaExternal") || (name === "locationMediaType")) {
            this.setState({ locationMediaUrl: "" })
        }
        if (name === "locationName") {
            this.props.handleChange(value)
        }
        if (name === "locationMediaUrl") {
            if (this.state.locationMediaExternal === true) {
                if (this.state.locationMediaType === false) {
                    let youtubeURL = youtubeUrlToEmbedUrl(value)
                    if (youtubeURL !== false) {
                        this.setState({ [name]: youtubeURL })
                    }
                }
            }
        }

        console.log("INPUTCHANGE")
        console.log(name)
        console.log(value)
        //converts the image url in case faulty when url is copied from the internet
    }

    setParentMediaUrl(mediaPath) {
        console.log("PARENTMEDIAURL")
        if (this.state.locationMediaUrl === "") {
            this.setState({ locationMediaType: "video" })
        }
        this.setState({ locationMediaUrl: mediaPath })
    }

    handleSubmit(event) {
        event.preventDefault();

        /*
        här bygger vi objektet som ska till databasen 
        och anropar sedan funktionen där API-anropet ligger och skickar med objektet
        just nu får man bara en alert med de värden man fyllt i
        */


        if (this.state.locationUseQR === false) {
            this.setState({ locationDirections: "" })
        } else if (this.state.locationUseQR === true) {
            this.setState({ locationLatitude: "", locationLongitude: "" })
        }

        let mediaType;
        console.log("MEDIATYPE")
        console.log(this.state.locationMediaUrl.length)
        if (this.state.locationMediaType === false) {
            mediaType = "video"
        } else if (this.state.locationMediaType === true) {
            mediaType = "image"
        }
        if (this.state.locationMediaUrl.length === 0) {
            mediaType = "";
        }

        let tempContentArray = [{
            "content-id": this.state.locationContentID1,
            "answer": this.state.locationAnswer1,
            "correct": this.state.locationCorrect1
        },
        {
            "content-id": this.state.locationContentID2,
            "answer": this.state.locationAnswer2,
            "correct": this.state.locationCorrect2
        },
        {
            "content-id": this.state.locationContentID3,
            "answer": this.state.locationAnswer3,
            "correct": this.state.locationCorrect3
        },
        {
            "content-id": this.state.locationContentID4,
            "answer": this.state.locationAnswer4,
            "correct": this.state.locationCorrect4
        },
        {
            "content-id": this.state.locationContentID5,
            "answer": this.state.locationAnswer5,
            "correct": this.state.locationCorrect5
        }]
        let contentArray = []
        tempContentArray.forEach(item => {
            if (item["content-id"]) {
                contentArray.push(item);
            }
        });

        let data = {
            "location-update": {
                "location-id": this.props.currentData.location_id,
                "name": this.state.locationName,
                "text_info": this.state.locationInfo,
                "qr": this.state.locationUseQR,
                "x_coords": this.state.locationLongitude,
                "y_coords": this.state.locationLatitude,
                "directions": this.state.locationDirections,
                "media": [{
                    "mediaURL": this.state.locationMediaUrl,
                    "mediaType": mediaType,
                    "externalMedia": this.state.locationMediaExternal
                }],
                "content": contentArray
            }
        }
        console.log("CONTENTARRAY")
        console.log(data)
        this.props.callUpdateLocation(data);
        alert('A value was submitted: ' + this.state.locationMediaUrl + ' AND: ' + this.state.locationMediaType + ' AND: ' + this.state.locationMediaExternal)
        alert("from the data object: " + data["location-update"].media[0].mediaUrl + ' AND: ' + data["location-update"].media[0].mediaType + ' AND: ' + data["location-update"].media[0].externalMedia)
        //alert('A value was submitted: ' + this.state.locationName + ' AND: ' + this.state.locationInfo + ' AND: ' + this.state.locationImage + ' AND: ' + this.state.locationVideo + ' AND: ' + this.state.locationLongitude + ' AND: ' + this.state.locationLatitude + ' AND: ' + this.state.locationDirections + ' AND: ' + this.state.locationAnswer1 + ' AND: ' + this.state.locationAnswer2 + ' AND: ' + this.state.locationAnswer3);
    }



    render() {

        return (
            <div className='container col s12'>
                <h3>Redigera plats</h3>
                <form onSubmit={this.handleSubmit}>
                    <label>
                        Titel
                        <input
                            className='blue lighten-4'
                            name="locationName" type="text"
                            value={this.state.locationName}
                            onChange={this.handleInputChange}

                        />
                    </label>

                    <label>
                        Innehåll
                        <input
                            className='blue lighten-4'
                            name="locationInfo" type="text"
                            value={this.state.locationInfo}
                            onChange={this.handleInputChange} />
                    </label>
                    <LocationFormMedia
                        locationID={this.props.currentData.location_id}
                        locationMediaUrl={this.state.locationMediaUrl}
                        locationMediaType={this.state.locationMediaType}
                        locationMediaExternal={this.state.locationMediaExternal}
                        handleInputChange={this.handleInputChange}
                        handleMediaOptions={this.handleMediaOptions}
                        setParentMediaUrl={this.setParentMediaUrl}
                    />

                    <div className="switch row">
                        <label>
                            Använd koordinater
                            <input type="checkbox"
                                name="locationUseQR"
                                checked={this.state.locationUseQR}
                                onChange={this.handleInputChange}
                            />
                            <span className="lever"></span>
                            Använd QR

                        </label>
                    </div>

                    {(() => {
                        if (this.state.locationUseQR === true) {

                            return (
                                <LocationFormUseQR
                                    data={this.state}
                                    routeID={this.props.routeID}
                                    handleInputChange={this.handleInputChange}
                                />
                            )
                        } else if (this.state.locationUseQR === false) {

                            return (
                                <ModGeolocate
                                    data={this.state}
                                    handleGeoLocation={this.handleGeoLocation}
                                    handleInputChange={this.handleInputChange} />
                            )
                        }
                    })()}
                    <fieldset>
                        <div className=''>
                            <LocationFormAnswers
                                data={this.state}
                                content={this.props.currentData.content}
                                handleInputChange={this.handleInputChange}
                                handleAddAnswer={this.handleAddAnswer}
                                handleRemoveAnswer={this.handleRemoveAnswer}
                                handleContentIDState={this.handleContentIDState} />
                        </div>

                    </fieldset>
                    <input type="submit" value="Submit" />
                </form>
            </div>
        )
    }
}
