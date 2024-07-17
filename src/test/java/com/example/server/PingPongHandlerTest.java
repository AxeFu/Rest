package com.example.server;

import com.example.server.handlers.implementation.PingPongHandler;
import com.example.server.mock.BaseRequestMock;
import com.example.server.mock.DataBaseServiceMock;
import com.example.server.mock.HttpServerRequestMock;
import com.example.server.mock.ServerHandlerMock;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PingPongHandlerTest {



    @Test
    public void testingExecute() {
        // Arrange
        HttpServerRequestMock httpServerRequestMock = new HttpServerRequestMock();
        DataBaseServiceMock dataBaseServiceMock = new DataBaseServiceMock();
        ServerHandlerMock serverHandlerMock = new ServerHandlerMock("ping");
        BaseRequestMock baseRequestMock = new BaseRequestMock(httpServerRequestMock, new JsonObject().put("id", 5).toString());
        PingPongHandler pingPongHandler = new PingPongHandler(baseRequestMock, dataBaseServiceMock);

        // Act
        pingPongHandler.execute();

        // Assert
        assertEquals(baseRequestMock.endResult, "{\"id\":5}");
    }

}
