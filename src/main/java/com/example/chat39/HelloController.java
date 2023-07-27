package com.example.chat39;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HelloController {
    @FXML
    private TextArea messageBox;
    @FXML
    private TextField inputMessage;
    @FXML
    private VBox usersBox;
    private DataOutputStream out;
    private int toUser = 0; // отправить всем

    @FXML
    protected void onSendMessage() {
        JSONObject jsonObject = new JSONObject();

        String msg = inputMessage.getText();
        messageBox.appendText(msg + "\n");
        inputMessage.clear();
        jsonObject.put("msg", msg);
        jsonObject.put("toUser", msg);
        try {
            out.writeUTF(jsonObject.toJSONString()); // {msg: "/login",toUser:0}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void connect() {
        try {
            Socket socket = new Socket("127.0.0.1", 9123);
            DataInputStream is = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String response;
                        while (true) {
                            response = is.readUTF(); // "Hello" "{msg: 'hello'}"
                            JSONParser jsonParser = new JSONParser();
                            try {
                                JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
                                JSONArray jsonArray = (JSONArray) jsonObject.get("onlineUsers");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        usersBox.getChildren().clear();
                                        for (int i = 0; i < jsonArray.size(); i++) {
                                            String userName = (String) jsonArray.get(i);
                                            Button userBtn = new Button();
                                            userBtn.setText(userName);
                                            usersBox.getChildren().add(userBtn);
                                        }
                                    }
                                });

                                messageBox.appendText(jsonArray.toJSONString());
                            } catch (ParseException e) {
                                messageBox.appendText(response + "\n");
                            }


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}