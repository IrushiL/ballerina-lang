import ballerina/http;

public function main (string... args) {
    http:Client clientEndpoint = new("https://postman-echo.com");
    string param = "staticValue";
    string headerName = "staticValue";
    string headerValue = "staticValue";

    http:Request req = new;
    req.setHeader(headerName, headerValue);

    var response = clientEndpoint -> get("/get?test=" + param, message = req);
    match response {
        http:Response resp => {
            var msg = resp.getTextPayload();
            match msg {
                string stringPayload => {
                    normalFunction (stringPayload);
                }
                error payloadError => return;
            }
        }
        error err => return;
    }
}

function normalFunction (string insecureIn) {
}
