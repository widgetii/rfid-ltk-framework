Служба мониторинга
==================

Служба мониторинга предоставляет возможность другим службам и подсистемам
делать две вещи:

- сообщать о различных событиях, и
- на основе записей о событиях формировать отчёты о состоянии подсистемы.

События также записываются в лог (с различным уровнем важности).

События, а также логика построения отчётов на их основе, специфичны для каждой
подсистемы. Однако все отчёты можно получить в одном месте. На сервере
хранилища отчёт можно получить по адресу `http://<server>/rfid/status.do`.


Формат отчёта
-------------

Отчёт - это текстовый документ в кодировке UTF-8. Отчёт разбит по подсистемам,
названия которых приведены в заголовках разделов. Записи отчёта имеют вид:

> ` <severity> <time> <message> <trace> `

Здесь

- `<severity>` - важность сообщения, строго 5 символов, заключённых в квадратные
  скобки;
- `<time>` - это время события с точность до миллисекунды в формате
  `yyyy-MM-dd HH:mm:ss.SSS`;
- `<message>` - произвольное текстовое сообщение;
- `<trace>` - трассировка исключения, может занимать несколько строк или
  отсутствовать вовсе.

Важность сообщений в порядке возрастания:

- `[TRACE]` - след какого-то не очень важного события. Используется для отладки.
- `[INFO ]` - информация о несколько более важном событии.
- `[REM  ]` - напоминание. Используется, например, чтобы напомнить, что приёмник
  тегов отключён. Это может быть ошибкой оператора, так что требует внимания.
- `[NOTE ]` - замечание. Пока ещё не ошибка, но подозрительное событие.
  Используется, например, если произошло переподключение хранилища к накопителю.
- `[WARN ]` - предупреждение. Используется, например, чтобы сообщить об ошибке,
  произошедшей в прошлом, но уже исправленной. Например, если произошёл разрыв
  соединения, но соединение уже восстановлено.
- `[ERROR]` - ошибка. Обычная ошибка, произошедшая недавно. Она ещё может быть
  исправлена автоматически. Например, разорванное соединение может быть
  восстановлено). В этом случае ошибка заменяется на предупреждение. Если же
  ошибка не может быть исправлена в течение некоторого времени, то она
  становится критической.
- `[FATAL] - критическая ошибка. Скорее всего означает нечто действительно
  плохое. Например, что соединение не может быть восстановлено в течение
  длительного времени.


Параметры отчёта
----------------

На страницу отчёта можно передать дополнительные параметры в строке запроса:

- `severity` - наименьшая важность событий в отчёте. Значение не чувствительно
  к регистру. По умолчанию равно `REM`, то есть отчёт содержит лишь записи о
  напоминаниях и более важных событиях. Если нужно показать все события, то в
  качестве значения можно передать `TRACE` или `all`.
- `period` - период отчётности в часах или в виде `часы:минуты`. Отчёт будет
  содержать события, произошедшие лишь за последнее время, длительность которого
  и задаётся параметром. По умолчанию - 24 часа. Не влияет на напоминания и
  ошибки - они будут содержаться в отчёте в любом случае.