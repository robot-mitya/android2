package ru.robotmitya.robohead;

/**
 * Created by dmitrydzz on 1/28/14.
 * Класс для вывода в журнал трассировочных данных.
 * @author dmitrydzz
 *
 */
public final class Log {
    /**
     * Признак вывода трассировочных данных в журнал.
     */
    static final boolean ENABLE_LOG = true;

    /**
     * Тег для фильтрации в журнале.
     */
    static final String LOG_TAG = "Mitya";

    /**
     * Закрытый конструктор.
     */
    private Log() { }

    /**
     * Вывод подробностей.
     * @param msg текст сообщения.
     */
    public static void v(final String msg) {
        if (ENABLE_LOG) {
            android.util.Log.v(LOG_TAG, msg);
        }
    }

    /**
     * Вывод предупреждения.
     * @param msg текст предупреждения.
     */
    public static void w(final String msg) {
        if (ENABLE_LOG) {
            android.util.Log.w(LOG_TAG, msg);
        }
    }

    /**
     * Вывод информации.
     * @param msg информационное сообщение.
     */
    public static void i(final String msg) {
        if (ENABLE_LOG) {
            android.util.Log.i(LOG_TAG, msg);
        }
    }

    /**
     * Вывод отладочной информации.
     * @param msg текст ссобщения.
     */
    public static void d(final String msg) {
        if (ENABLE_LOG) {
            android.util.Log.d(LOG_TAG, msg);
        }
    }

    /**
     * Фиксация ошибки.
     * @param msg текст ошибки.
     */
    public static void e(final String msg) {
        android.util.Log.e(LOG_TAG, msg);
    }
}