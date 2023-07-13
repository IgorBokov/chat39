package com.example.chat39;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private DataOutputStream out;

    @FXML
    protected void onSendMessage() {
        String msg = inputMessage.getText();
        messageBox.appendText(msg + "\n");
        inputMessage.clear();
        try {
            out.writeUTF(msg);
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
                            response = is.readUTF();
                            JSONParser jsonParser = new JSONParser();
                            try {
                                JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
                                JSONArray jsonArray = (JSONArray) jsonObject.get("onlineusers");
                                messageBox.appendText(jsonArray.toJSONString());
                            }catch (ParseException e) {
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
        }
    }
}