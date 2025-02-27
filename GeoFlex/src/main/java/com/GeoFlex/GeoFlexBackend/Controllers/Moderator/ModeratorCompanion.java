package com.GeoFlex.GeoFlexBackend.Controllers.Moderator;

import com.GeoFlex.GeoFlexBackend.DatabaseAccess.AdminProcedures;
import com.GeoFlex.GeoFlexBackend.DatabaseAccess.ModeratorProcedures;
import com.GeoFlex.GeoFlexBackend.PoJo.LocationUpdate.RootLocationEdit;
import com.GeoFlex.GeoFlexBackend.PoJo.RouteUpdate.RootUpdate;
import com.GeoFlex.GeoFlexBackend.Process.FileHandler;
import com.google.gson.Gson;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public class ModeratorCompanion {

    private String userID = "";
    public ModeratorCompanion(String userID) {
        this.userID = userID;
    }

    /**
     * Returns all routes in the system as user is admin. (/moderator/routes) GET
     * @return Response entity containing json of all routes.
     */
    public ResponseEntity<String> routesGet() {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        HttpStatus responseStatus = HttpStatus.OK;
        String json = mp.getRoutes(userID);
        if (json == null) {
            json = "{\"error\" : \"Internal server error, contact administrator\"}";
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (json.equals("{}")) {
            json = "{\"no routes\" : \"No routes in system\"}";
            responseStatus = HttpStatus.NO_CONTENT;
        }
        response = new ResponseEntity<>(json, responseStatus);
        return response;
    }

    /**
     * Get one specific route for editing, this includes locations. (/moderator/route) GET
     * @param routeID The id of route to be edited.
     * @return Json of the route to be edited or Error json if not found.
     */
    public ResponseEntity<String> routeGet(String routeID) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        HttpStatus responseStatus = HttpStatus.OK;
        String json = mp.getRoute(routeID, userID);
        if (json == null) {
            json = "{\"error\" : \"Internal server error, contact administrator\"}";
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (json.equals("{}")) {
            json = "{\"no routes\" : \"Route could not be found\"}";
            responseStatus = HttpStatus.NO_CONTENT;
        }
        response = new ResponseEntity<>(json, responseStatus);
        return response;
    }

    /**
     * Patch to route, include the parts that should be updated. (/moderator/route) PATCH
     * @param body For getting Json string containing the id and requested changes to the route.
     * @return OK message body if sucessfull, error with details if not.
     */
    public ResponseEntity<String> routePatch(String body) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        response = new ResponseEntity<>("{\"error\" : \"Internal server error, contact the admin.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        Gson gson = new Gson();
        RootUpdate ru = gson.fromJson(body, RootUpdate.class);
        if(ru.routeUpdate.title != null){
            mp.routeUpdateTitle(ru.routeUpdate.routeId, ru.routeUpdate.title);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(ru.routeUpdate.description != null){
            mp.routeUpdateDescription(ru.routeUpdate.routeId, ru.routeUpdate.description);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(ru.routeUpdate.type != null){
            mp.routeUpdateType(ru.routeUpdate.routeId, ru.routeUpdate.type);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(ru.routeUpdate.image != null){
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(ru.routeUpdate.routeMedia != null){
            for (int i = 0; i < ru.routeUpdate.routeMedia.size(); i++) {
                if(ru.routeUpdate.routeMedia.get(i).mediaUrl != null){
                    mp.routeUploadFile(Integer.parseInt(ru.routeUpdate.routeId), ru.routeUpdate.routeMedia.get(i).mediaUrl, ru.routeUpdate.routeMedia.get(i).mediaType, true);
                    response = new ResponseEntity<>("", HttpStatus.OK);
                }
            }
        }
        if(ru.routeUpdate.location != null){
            for (int i = 0; i < ru.routeUpdate.location.size(); i++) {
                if(ru.routeUpdate.location.get(i).to != null){
                    try {
                        //System.out.println("swapping from: " + Integer.parseInt(ru.routeUpdate.location.get(i).from) + ", to :" +  Integer.parseInt(ru.routeUpdate.location.get(i).to));
                        mp.routeSwapLocation(Integer.parseInt(ru.routeUpdate.location.get(i).from), Integer.parseInt(ru.routeUpdate.location.get(i).to));
                        response = new ResponseEntity<>("", HttpStatus.OK);
                    } catch (NumberFormatException e) {
                        //System.out.println("excepting swap");
                        response = new ResponseEntity<>("{\"error\" : \"malformatted input\"}", HttpStatus.BAD_REQUEST);
                    }
                } else if (ru.routeUpdate.location.get(i).newLocation != null) {
                    try {
                        //System.out.println("addning: " + Integer.parseInt(ru.routeUpdate.location.get(i).newLocation));
                        mp.routeNewLocations(Integer.parseInt(ru.routeUpdate.location.get(i).newLocation), Integer.parseInt(ru.routeUpdate.routeId));
                        response = new ResponseEntity<>("", HttpStatus.OK);
                    } catch (NumberFormatException e) {
                        //System.out.println("excepting delete");
                        response = new ResponseEntity<>("{\"error\" : \"malformatted input\"}", HttpStatus.BAD_REQUEST);
                    }
                }
                else {
                    try {
                        //System.out.println("deleting: " + Integer.parseInt(ru.routeUpdate.location.get(i).delete));
                        mp.routeDeleteLocation(Integer.parseInt(ru.routeUpdate.routeId),Integer.parseInt(ru.routeUpdate.location.get(i).delete));
                        FileHandler fh = new FileHandler();
                        fh.deleteFileDirectory(Integer.parseInt(ru.routeUpdate.location.get(i).delete), "locations");
                        response = new ResponseEntity<>("", HttpStatus.OK);
                    } catch (NumberFormatException e) {
                        //System.out.println("excepting delete");
                        response = new ResponseEntity<>("{\"error\" : \"malformatted input\"}", HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return response;
    }

    /**
     * Gets all locations related to a route by its ID.
     * @param routeID The id of the route.
     * @return Json object containing all locations of a route.
     */
    public ResponseEntity<String> routeGetLocations(String routeID) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        if(routeID.isEmpty() || routeID == null){
            response = new ResponseEntity<>("{\"error\" : \"Internal Server Error.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            //String json = mp.getRouteLocations(routeID);
            String json = mp.getRouteLocationsExperimental(routeID);
            response = new ResponseEntity<>(json, HttpStatus.OK);
        }
        return response;
    }

    /**
     * Function to upload a file to the server and save the path to a route in the database.
     * @param routeId The id of the route.
     * @param file The file to be saved.
     * @return OK message body if sucessfull, error with details if not.
     */
    public ResponseEntity<String> uploadRouteFile(int routeId, MultipartFile file){
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        //FileHandler to directories and write file to folder.
        FileHandler fh = new FileHandler();

        //Upload image path to database.
        String fileType = file.getContentType();
        String path = "files/routes/"+routeId+"/"+file.getOriginalFilename();
        switch(fileType){
            case "image/jpeg":
            case "image/png":
            case "video/mp4":
            case "video/quicktime":
                fh.createDirectoriesAndSaveFile(routeId, file, "routes");
                mp.routeUploadFile(routeId, path, "video", false);
                response = new ResponseEntity<>("", HttpStatus.OK);
                break;
            case "image/heic":
                fh.createDirectoriesAndSaveFile(routeId, file, "routes");
                fh.heicToPng(routeId, file, "routes");
                mp.routeUploadFile(routeId, path.replace("heic", "png"), "image", false);
                response = new ResponseEntity<>("", HttpStatus.OK);
                break;
            default:
                response = new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
                break;
        }

        return response;
    }

    /**
     * Function to get filepath for a route from the database.
     * @param routeId
     * @return OK message if sucessfull, error with details if not.
     */
    public ResponseEntity<String> getRouteFile(int routeId) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        String filepath = mp.routeGetFile(routeId);
        if(filepath.isEmpty() || filepath.equals("")){
            response = new ResponseEntity<>("{\"error\" : \"Wrong request params.\"}", HttpStatus.BAD_REQUEST);
        }
        else {
            response = new ResponseEntity<>(filepath, HttpStatus.OK);
        }
        return response;
    }

    /**
     * Patch to location, include the parts that should be updated. (/moderator/location) PATCH
     * @param body For getting Json string containing the id and requested changes to the route.
     * @return OK message if sucessfull, error with details if not.
     */
    public ResponseEntity<String> locationPatch(String body) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        response = new ResponseEntity<>("{\"error\" : \"Internal server error, contact the admin.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        Gson gson = new Gson();
        RootLocationEdit rle = gson.fromJson(body, RootLocationEdit.class);
        if(rle.locationEdit.locationId == null){
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        if(rle.locationEdit.name != null && !rle.locationEdit.name.isEmpty()){
            mp.locationUpdateName(rle.locationEdit.locationId, rle.locationEdit.name);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(rle.locationEdit.textInfo != null && !rle.locationEdit.textInfo.isEmpty()){
            mp.locationUpdateTextInfo(rle.locationEdit.locationId, rle.locationEdit.textInfo);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(rle.locationEdit.qr != null){
            mp.setQr(rle.locationEdit.locationId, rle.locationEdit.qr);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(rle.locationEdit.xCoords != null && !rle.locationEdit.xCoords.isEmpty()){
            mp.locationPositionUpdateXcoords(rle.locationEdit.locationId, rle.locationEdit.xCoords);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(rle.locationEdit.yCoords != null && !rle.locationEdit.yCoords.isEmpty()){
            mp.locationPositionUpdateYcoords(rle.locationEdit.locationId, rle.locationEdit.yCoords);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(rle.locationEdit.directions != null && !rle.locationEdit.directions.isEmpty()){
            mp.locationPositionUpdateDirections(rle.locationEdit.locationId, rle.locationEdit.directions);
            response = new ResponseEntity<>("", HttpStatus.OK);
        }
        if(rle.locationEdit.media != null && !rle.locationEdit.media.isEmpty()){
            for (int i = 0; i < rle.locationEdit.media.size(); i++) {
                if(rle.locationEdit.media.get(i).mediaUrl != null && rle.locationEdit.media.get(i).mediaType != null){
                    mp.locationUploadFile(Integer.parseInt(rle.locationEdit.locationId), rle.locationEdit.media.get(i).mediaUrl,
                            rle.locationEdit.media.get(i).mediaType, true);
                    response = new ResponseEntity<>("", HttpStatus.OK);
                }
            }
        }
        if(rle.locationEdit.content != null && !rle.locationEdit.content.isEmpty()){
            for (int i = 0; i < rle.locationEdit.content.size(); i++) {
                if(rle.locationEdit.content.get(i).answer != null && rle.locationEdit.content.get(i).correct != null){
                    mp.createContent(rle.locationEdit.locationId, rle.locationEdit.content.get(i).answer,
                            rle.locationEdit.content.get(i).correct, rle.locationEdit.content.get(i).contentId);
                    response = new ResponseEntity<>("", HttpStatus.OK);
                }
                else if(rle.locationEdit.content.get(i).delete != null){
                    mp.deleteContent(rle.locationEdit.content.get(i).delete);
                    response = new ResponseEntity<>("", HttpStatus.OK);
                }
            }
        }

        return response;
    }

    /**
     * Function to upload a file to the server and save the path to a location in the database.
     * @param locationId The id of the route.
     * @param file The file to be saved.
     * @return OK message body if sucessfull, error with details if not.
     */
    public ResponseEntity<String> uploadLocationFile(int locationId, MultipartFile file){
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        //FileHandler to directories and write file to folder.
        FileHandler fh = new FileHandler();

        //Upload image path to database.
        String fileType = file.getContentType();
        String path = "files/locations/"+locationId+"/"+file.getOriginalFilename();
        switch(fileType){
            case "image/jpeg":
            case "image/png":
                fh.createDirectoriesAndSaveFile(locationId, file, "locations");
                mp.locationUploadFile(locationId, path, "image", false);
                response = new ResponseEntity<>("", HttpStatus.OK);
                break;
            case "video/mp4":
            case "video/quicktime":
                fh.createDirectoriesAndSaveFile(locationId, file, "locations");
                mp.locationUploadFile(locationId, path, "video", false);
                response = new ResponseEntity<>("", HttpStatus.OK);
                break;
            case "image/heic":
                fh.createDirectoriesAndSaveFile(locationId, file, "locations");
                fh.heicToPng(locationId, file, "locations");
                mp.locationUploadFile(locationId, path.replace("heic", "png"), "image", false);
                response = new ResponseEntity<>("", HttpStatus.OK);
                break;
            default:
                response = new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
                break;
        }

        return response;
    }

    /**
     * Function to get filepath for a location from the database.
     * @param locationId
     * @return OK message if sucessfull, error with details if not.
     */
    public ResponseEntity<String> getLocationFile(int locationId) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        String filepath = mp.locationGetFile(locationId);
        if(filepath.isEmpty() || filepath.equals("")){
            response = new ResponseEntity<>("{\"error\" : \"Wrong request params.\"}", HttpStatus.BAD_REQUEST);
        }
        else {
            response = new ResponseEntity<>(filepath, HttpStatus.OK);
        }
        return response;
    }

    /**
     * Function to get content for a location from the database.
     * @param locationId
     * @return OK message if sucessfull, error with details if not.
     */
    public ResponseEntity<String> locationGetContent(String locationId) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        String json = mp.locationGetContent(Integer.parseInt(locationId));
        if(json.isEmpty()){
            response = new ResponseEntity<>("{\"error\" : \"Wrong request params.\"}", HttpStatus.BAD_REQUEST);
        }
        else {
            response = new ResponseEntity<>(json, HttpStatus.OK);
        }
        return response;
    }

    /**
     * Function to get the position for a location from the database.
     * @param locationId
     * @return OK message if sucessfull, error with details if not.
     */
    public ResponseEntity<String> locationGetPosition(String locationId) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        String json = mp.locationGetPosition(Integer.parseInt(locationId));
        if(json.isEmpty()){
            response = new ResponseEntity<>("{\"error\" : \"Wrong request params.\"}", HttpStatus.BAD_REQUEST);
        }
        else {
            response = new ResponseEntity<>(json, HttpStatus.OK);
        }
        return response;
    }

    /**
     * Delete route if exists. (/moderator/route) DELETE
     * @param routeID ID for route to be deleted.
     * @return OK if deleted, Error if not found.
     */
    public ResponseEntity<String> routeDelete(String routeID) {
        ModeratorProcedures mp = new ModeratorProcedures();
        ResponseEntity<String> response;
        if(routeID.isEmpty() || routeID == null){
            response = new ResponseEntity<>("{\"error\" : \"Internal Server Error.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            response = new ResponseEntity<>("{\"OK\" : \"Request recieved by server.\"}", HttpStatus.OK);
            mp.deleteRoute(routeID);
            FileHandler fh = new FileHandler();
            fh.deleteFileDirectory(Integer.parseInt(routeID), "routes");
        }
        return response;
    }
}
