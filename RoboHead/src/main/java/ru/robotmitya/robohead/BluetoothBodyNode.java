/**
 * This node implements connection and communication functions with robo_body via Bluetooth.
 */
package ru.robotmitya.robohead;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitrydzz on 1/27/14.
 *
 */
public class BluetoothBodyNode implements NodeMain {
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothSocket mBluetoothSocket = null;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;
    private boolean mConnected;

    public BluetoothBodyNode(
            final Context context,
            final BluetoothAdapter bluetoothAdapter) {
        super();
        mContext = context;
        mBluetoothAdapter = bluetoothAdapter;
        mConnected = false;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("robot_mitya/bluetooth_body_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        connectedNode.executeCancellableLoop(new CancellableLoop() {
            private String mPreviousMessagesRest = "";

            @Override
            protected void loop() throws InterruptedException {
                if (mConnected) {
                    List<String> messageList = null;
                    try {
                        // Получить список принятых на данный момент команд:
                        messageList = getMessagesFromStream(mInputStream, SettingsActivity.MESSAGE_LENGTH);
                    } catch (Exception e) {
                        Log.e("BluetoothBodyNode input error: " + e.getMessage());
                        cancel();
                    }

                    if (messageList != null) {
                        // Выполнить каждую принятую команду:
                        for (String messageText : messageList) {
                            Log.d("BluetoothBodyNode has received from body: " + messageText);
                        }
                    }
                } else {
                    // Это - единственный метод подключиться напрямую, не используя поиска
                    // всех устройств в округе.
                    // createRfcommSocketToServiceRecord(), к сожалению, не работает
                    try {
                        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(SettingsActivity.getRoboBodyMac());
                        Method method = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                        mBluetoothSocket = (BluetoothSocket) method.invoke(bluetoothDevice, 1 /*Integer.valueOf(1)*/);

                        // Если контроллер робота недоступен, connect() вызывает исключение и тормозит работу
                        // приложения, несмотря на отдельный поток!
                        mBluetoothSocket.connect();

                        mInputStream = mBluetoothSocket.getInputStream();
                        mOutputStream = mBluetoothSocket.getOutputStream();
                        mConnected = true;
                        Log.d("BluetoothBodyNode: started");
                    } catch (Exception e) {
                        Log.e("BluetoothBodyNode connection error: " + e.getMessage());
                        cancel();
                    }
                }
            }

            private List<String> getMessagesFromStream(final InputStream inputStream, final int messageLength) throws IOException {
                List<String> result = new ArrayList<String>();

                DataInputStream dataInputStream = new DataInputStream(inputStream);

                int bytesAvailable = dataInputStream.available();
                if (bytesAvailable <= 0) {
                    return result;
                }

                byte[] buffer = new byte[bytesAvailable];
                dataInputStream.readFully(buffer);
                String messages = new String(buffer);

                // Если на предыдущей итерации часть команды была прочитана, дочитываю команду:
                if (!mPreviousMessagesRest.equals("")) {
                    messages = mPreviousMessagesRest + messages;
                }

                messages = MessageHelper.skipFirstBrokenMessage(messages);
                while (messages.length() >= messageLength) {
                    String newMessage = messages.substring(0, messageLength);
                    result.add(newMessage);
                    messages = messages.substring(messageLength);
                    messages = MessageHelper.skipFirstBrokenMessage(messages);
                }
                mPreviousMessagesRest = messages;

                return result;
            }
        });

        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("robot_mitya/body", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(final std_msgs.String message) {
                String messageBody = message.getData();

                if (mConnected) {
                    if (mBluetoothSocket != null) {
                        try {
                            mOutputStream.write(messageBody.getBytes());
                        } catch (IOException e) {
                            String errorText = String.format(
                                    mContext.getResources().getString(R.string.error_sending_message_through_bluetooth),
                                    message);
                            Log.e(errorText);
                        }
                    }
                }

                Log.d("BluetoothBodyNode has sent to body: " + messageBody);
            }
        });
    }

    @Override
    public void onShutdown(Node node) {
        if (mConnected) {
            try {
                if (mBluetoothSocket != null) {
                    mBluetoothSocket.close();
                    mBluetoothSocket = null;
                }
            } catch (IOException e) {
                mBluetoothSocket = null;
            }
        }
        mConnected = false;
        Log.d("BluetoothBodyNode: stopped");
    }

    @Override
    public void onShutdownComplete(Node node) {
    }

    @Override
    public void onError(Node node, Throwable throwable) {
    }
}
