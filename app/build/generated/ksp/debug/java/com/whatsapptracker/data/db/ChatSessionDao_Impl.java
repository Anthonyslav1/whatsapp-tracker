package com.whatsapptracker.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ChatSessionDao_Impl implements ChatSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChatSession> __insertionAdapterOfChatSession;

  private final EntityDeletionOrUpdateAdapter<ChatSession> __deletionAdapterOfChatSession;

  private final EntityDeletionOrUpdateAdapter<ChatSession> __updateAdapterOfChatSession;

  public ChatSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatSession = new EntityInsertionAdapter<ChatSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `chat_sessions` (`id`,`contactName`,`startTime`,`endTime`,`durationMs`,`sessionType`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatSession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContactName());
        statement.bindLong(3, entity.getStartTime());
        statement.bindLong(4, entity.getEndTime());
        statement.bindLong(5, entity.getDurationMs());
        statement.bindString(6, entity.getSessionType());
      }
    };
    this.__deletionAdapterOfChatSession = new EntityDeletionOrUpdateAdapter<ChatSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `chat_sessions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatSession entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfChatSession = new EntityDeletionOrUpdateAdapter<ChatSession>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `chat_sessions` SET `id` = ?,`contactName` = ?,`startTime` = ?,`endTime` = ?,`durationMs` = ?,`sessionType` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatSession entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContactName());
        statement.bindLong(3, entity.getStartTime());
        statement.bindLong(4, entity.getEndTime());
        statement.bindLong(5, entity.getDurationMs());
        statement.bindString(6, entity.getSessionType());
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final ChatSession session, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfChatSession.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ChatSession session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfChatSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ChatSession session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfChatSession.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super ChatSession> $completion) {
    final String _sql = "SELECT * FROM chat_sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ChatSession>() {
      @Override
      @Nullable
      public ChatSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfSessionType = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionType");
          final ChatSession _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpSessionType;
            _tmpSessionType = _cursor.getString(_cursorIndexOfSessionType);
            _result = new ChatSession(_tmpId,_tmpContactName,_tmpStartTime,_tmpEndTime,_tmpDurationMs,_tmpSessionType);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Long> getTotalDurationInRange(final long startTime, final long endTime) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(durationMs), 0) \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_sessions"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ContactDuration>> getTopContacts(final long startTime, final long endTime,
      final int limit) {
    final String _sql = "\n"
            + "        SELECT contactName, SUM(durationMs) as totalDuration \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        GROUP BY contactName \n"
            + "        ORDER BY totalDuration DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_sessions"}, new Callable<List<ContactDuration>>() {
      @Override
      @NonNull
      public List<ContactDuration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactName = 0;
          final int _cursorIndexOfTotalDuration = 1;
          final List<ContactDuration> _result = new ArrayList<ContactDuration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactDuration _item;
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _item = new ContactDuration(_tmpContactName,_tmpTotalDuration);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ContactDuration>> getTopContactsByRelationshipScore(final long startTime,
      final long endTime, final int limit) {
    final String _sql = "\n"
            + "        SELECT contactName, (SUM(durationMs) + (COUNT(id) * 60000)) as totalDuration \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        GROUP BY contactName \n"
            + "        ORDER BY totalDuration DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_sessions"}, new Callable<List<ContactDuration>>() {
      @Override
      @NonNull
      public List<ContactDuration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactName = 0;
          final int _cursorIndexOfTotalDuration = 1;
          final List<ContactDuration> _result = new ArrayList<ContactDuration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactDuration _item;
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _item = new ContactDuration(_tmpContactName,_tmpTotalDuration);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ChatSession>> getSessionsInRange(final long startTime, final long endTime) {
    final String _sql = "\n"
            + "        SELECT * FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        ORDER BY startTime ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_sessions"}, new Callable<List<ChatSession>>() {
      @Override
      @NonNull
      public List<ChatSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfSessionType = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionType");
          final List<ChatSession> _result = new ArrayList<ChatSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpSessionType;
            _tmpSessionType = _cursor.getString(_cursorIndexOfSessionType);
            _item = new ChatSession(_tmpId,_tmpContactName,_tmpStartTime,_tmpEndTime,_tmpDurationMs,_tmpSessionType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllSessionsInRange(final long startTime, final long endTime,
      final Continuation<? super List<ChatSession>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        ORDER BY startTime ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ChatSession>>() {
      @Override
      @NonNull
      public List<ChatSession> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfSessionType = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionType");
          final List<ChatSession> _result = new ArrayList<ChatSession>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatSession _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpSessionType;
            _tmpSessionType = _cursor.getString(_cursorIndexOfSessionType);
            _item = new ChatSession(_tmpId,_tmpContactName,_tmpStartTime,_tmpEndTime,_tmpDurationMs,_tmpSessionType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLongestSession(final long startTime, final long endTime,
      final Continuation<? super ChatSession> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        ORDER BY durationMs DESC LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ChatSession>() {
      @Override
      @Nullable
      public ChatSession call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfSessionType = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionType");
          final ChatSession _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpSessionType;
            _tmpSessionType = _cursor.getString(_cursorIndexOfSessionType);
            _result = new ChatSession(_tmpId,_tmpContactName,_tmpStartTime,_tmpEndTime,_tmpDurationMs,_tmpSessionType);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMonthlyDurations(final long startTime, final long endTime,
      final Continuation<? super List<MonthlyDuration>> $completion) {
    final String _sql = "\n"
            + "        SELECT cast(strftime('%m', startTime / 1000, 'unixepoch', 'localtime') as integer) as month, SUM(durationMs) as totalDuration \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        GROUP BY month \n"
            + "        ORDER BY month ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MonthlyDuration>>() {
      @Override
      @NonNull
      public List<MonthlyDuration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMonth = 0;
          final int _cursorIndexOfTotalDuration = 1;
          final List<MonthlyDuration> _result = new ArrayList<MonthlyDuration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonthlyDuration _item;
            final int _tmpMonth;
            _tmpMonth = _cursor.getInt(_cursorIndexOfMonth);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _item = new MonthlyDuration(_tmpMonth,_tmpTotalDuration);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMostActiveDayOfWeek(final long startTime, final long endTime,
      final Continuation<? super DayOfWeekDuration> $completion) {
    final String _sql = "\n"
            + "        SELECT cast(strftime('%w', startTime / 1000, 'unixepoch', 'localtime') as integer) as dayOfWeek, SUM(durationMs) as totalDuration\n"
            + "        FROM chat_sessions\n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        GROUP BY dayOfWeek\n"
            + "        ORDER BY totalDuration DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DayOfWeekDuration>() {
      @Override
      @Nullable
      public DayOfWeekDuration call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDayOfWeek = 0;
          final int _cursorIndexOfTotalDuration = 1;
          final DayOfWeekDuration _result;
          if (_cursor.moveToFirst()) {
            final int _tmpDayOfWeek;
            _tmpDayOfWeek = _cursor.getInt(_cursorIndexOfDayOfWeek);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _result = new DayOfWeekDuration(_tmpDayOfWeek,_tmpTotalDuration);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUniqueContactCount(final long startTime, final long endTime,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(DISTINCT contactName) \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalSessionCount(final long startTime, final long endTime,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTopContactsYearly(final long startTime, final long endTime, final int limit,
      final Continuation<? super List<ContactDuration>> $completion) {
    final String _sql = "\n"
            + "        SELECT contactName, SUM(durationMs) as totalDuration \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        GROUP BY contactName \n"
            + "        ORDER BY totalDuration DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContactDuration>>() {
      @Override
      @NonNull
      public List<ContactDuration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactName = 0;
          final int _cursorIndexOfTotalDuration = 1;
          final List<ContactDuration> _result = new ArrayList<ContactDuration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactDuration _item;
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _item = new ContactDuration(_tmpContactName,_tmpTotalDuration);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTopContactsByRelationshipScoreYearly(final long startTime, final long endTime,
      final int limit, final Continuation<? super List<ContactDuration>> $completion) {
    final String _sql = "\n"
            + "        SELECT contactName, (SUM(durationMs) + (COUNT(id) * 60000)) as totalDuration \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'CHAT'\n"
            + "        GROUP BY contactName \n"
            + "        ORDER BY totalDuration DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContactDuration>>() {
      @Override
      @NonNull
      public List<ContactDuration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactName = 0;
          final int _cursorIndexOfTotalDuration = 1;
          final List<ContactDuration> _result = new ArrayList<ContactDuration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactDuration _item;
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _item = new ContactDuration(_tmpContactName,_tmpTotalDuration);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ContactDuration>> getTopEntertainers(final long startTime, final long endTime,
      final int limit) {
    final String _sql = "\n"
            + "        SELECT contactName, SUM(durationMs) as totalDuration \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'STATUS'\n"
            + "        GROUP BY contactName \n"
            + "        ORDER BY totalDuration DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_sessions"}, new Callable<List<ContactDuration>>() {
      @Override
      @NonNull
      public List<ContactDuration> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactName = 0;
          final int _cursorIndexOfTotalDuration = 1;
          final List<ContactDuration> _result = new ArrayList<ContactDuration>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactDuration _item;
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _item = new ContactDuration(_tmpContactName,_tmpTotalDuration);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTopEntertainerYearly(final long startTime, final long endTime,
      final Continuation<? super ContactDuration> $completion) {
    final String _sql = "\n"
            + "        SELECT contactName, SUM(durationMs) as totalDuration \n"
            + "        FROM chat_sessions \n"
            + "        WHERE startTime >= ? AND startTime < ? AND endTime > 0 AND sessionType = 'STATUS'\n"
            + "        GROUP BY contactName \n"
            + "        ORDER BY totalDuration DESC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContactDuration>() {
      @Override
      @Nullable
      public ContactDuration call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfContactName = 0;
          final int _cursorIndexOfTotalDuration = 1;
          final ContactDuration _result;
          if (_cursor.moveToFirst()) {
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final long _tmpTotalDuration;
            _tmpTotalDuration = _cursor.getLong(_cursorIndexOfTotalDuration);
            _result = new ContactDuration(_tmpContactName,_tmpTotalDuration);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
