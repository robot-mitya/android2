package ru.robotmitya.robohead;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by dmitrydzz on 1/28/14.
 * Вспомогательный класс для обеспечения связи между Android-приложением и
 * Arduino-скетчем робота.
 * @author Дмитрий Дзахов
 *
 */
public final class BluetoothHelper {
    /**
     * Activity that create and use BluetoothHelper.
     */
    private static Context mContext;

    /**
     * Handler обрабатывающий все сообщения в Android-приложении робота.
     * Передаётся классу в методе Initialize.
     */
    private static Handler mMessageHandler = null;

    /**
     * Bluetooth-адаптер телефона.
     */
    private static BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Сокет.
     */
    private static BluetoothSocket mBluetoothSocket = null;

    /**
     * Входной поток сообщений. В него поступают сообщения от контроллера робота.
     */
    private static InputStream mInputStream = null;

    /**
     * Выходной поток сообщений. В него помещаются сообщения для контроллера робота.
     */
    private static OutputStream mOutputStream = null;

    /**
     * Признак установления соединения с Bluetooth-модулем контроллера робота.
     */
    private static boolean mConnected = false;

    /**
     * Признак того, что контроллер робота выключен. Устанавливается при
     * неудачной попытке установления соединения с удалённым bluetooth-модулем
     * робота.
     */
    //private static boolean mControllerIsTurnedOff = false;

    /**
     * При чтении сообщений, поступающих от контроллера робота последнее
     * сообщение может быть получео ещё не полностью. Тогда принятый неполный
     * кусок последней команды запоминаем в это поле. При обработке следующей
     * партии сообщений мы вспомним его и прибавим справа.
     */
    private static String mPreviousMessagesRest = "";

    /**
     * Thread that implements connection and data receiving.
     */
    private static Thread mReceiveThread = null;

    /**
     * Класс будет статическим, поэтому конструктор закрываю.
     */
    private BluetoothHelper() {
    }

    /**
     * Соединение с bluetooth-модулем робота. Приём от контроллера робота и
     * передача на обработку сообщений.
     * @param messageHandler handler, обрабатывающий все сообщения робота.
     * @return true, если соединение выполнено.
     */
    public static boolean start(
            final Context context,
            final BluetoothAdapter bluetoothAdapter,
            final Handler messageHandler) {

        mContext = context;
        mBluetoothAdapter = bluetoothAdapter;
        mMessageHandler = messageHandler;

        if (mBluetoothAdapter == null) {
            String errorText = mContext.getResources().getString(R.string.error_no_bluetooth_adapter);
            Log.e(errorText);
            return false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            String errorText = mContext.getResources().getString(R.string.error_bluetooth_adapter_is_not_activated);
            Log.e(errorText);
            return false;
        }

        mReceiveThread = new Thread(new Runnable() {
            public void run() {
                boolean isInterrupted = false;
                while (true) {
                    isInterrupted = isInterrupted || Thread.currentThread().isInterrupted();
                    if (isInterrupted) {
                        break;
                    }

                    if (!mConnected) {
                        // Это - единственный метод подключиться напрямую, не используя поиска
                        // всех устройств в округе.
                        // createRfcommSocketToServiceRecord(), к сожалению, не работает
                        try {
                            connect();
                            Log.d("BluetoothHelper: started");
                        } catch (Exception e) {
                            Log.e("BluetoothHelper connection error: " + e.getMessage());
                            break;
                        }
                    }

                    if (mConnected) {
                        while (true) {
                            isInterrupted = Thread.currentThread().isInterrupted();
                            if (isInterrupted) {
                                break;
                            }

                            List<String> messageList = null;
                            try {
                                // Получить список принятых на данный момент команд:
                                messageList = getMessagesFromStream(mInputStream, Settings.MESSAGE_LENGTH);
                            } catch (Exception e) {
                                Log.e("BluetoothHelper input error: " + e.getMessage());
                                disconnect();
                            }

                            if ((messageList != null) && (mMessageHandler != null)) {
                                // Выполнить каждую принятую команду:
                                for (String messageText : messageList) {
                                    // Команды передаются в RoboHeadActivity:
                                    Message message = new Message();
                                    message.obj = messageText;
                                    mMessageHandler.sendMessage(message);
                                }
                            }

                        }
                    }
                } // while

                disconnect();
                Log.d("BluetoothHelper: stopped");
            }
        });
        mReceiveThread.start();

        return true;
    }

    /**
     * Разрыв bluetooth-соединения.
     */
    public static void stop() {
        if (mReceiveThread != null) {
            mReceiveThread.interrupt();
            mReceiveThread = null;
        }
    }

    /**
     * Bluetooth connection.
     * @throws Exception on Bluetooth connection error.
     */
    private static void connect() throws Exception {
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(Settings.getRoboBodyMac());
        Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
        mBluetoothSocket = (BluetoothSocket) m.invoke(bluetoothDevice, Integer.valueOf(1));

        // Если контроллер робота недоступен, connect() вызывает исключение и тормозит работу
        // приложения, несмотря на отдельный поток!
        mBluetoothSocket.connect();

        mInputStream = mBluetoothSocket.getInputStream();
        mOutputStream = mBluetoothSocket.getOutputStream();
        mConnected = true;
    }

    /**
     * Closing Bluetooth connection.
     */
    private static void disconnect() {
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
    }

    /**
     * Отправка сообщения. Один вызов допускает отправку сразу нескольких сообщений.
     * @param message сообщение или последовательность сообщений без разделителей.
     */
    public static void send(final String message) {
        if (mConnected) {
            if (mBluetoothSocket != null) {
                try {
                    mOutputStream.write(message.getBytes());
                } catch (IOException e) {
                    String errorText = String.format(
                            mContext.getResources().getString(R.string.error_sending_message_through_bluetooth),
                            message);
                    Log.e(errorText);
                }
            }
        }
    }

    /**
     * Извлекает из входного потока пятибайтовые команды, разделённые символами #13, #10.
     * Команда, ещё неполностью попавшая во входной поток не возвращается в выходной
     * список команд. Попавший в выходной поток кусок команды откладывается до следующего
     * вызова метода.
     * @param inputStream поток ввода (поступает из сокета).
     * @param messageLength длина сообщения (константа).
     * @return список команд роботу.
     * @throws IOException ошибка чтения из потока ввода (из сокета).
     */
    public static List<String> getMessagesFromStream(final InputStream inputStream, final int messageLength) throws IOException {
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
}