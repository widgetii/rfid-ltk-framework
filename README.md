Система обработки RFID
======================

Система состоит из множества компонентов, которые, в разных конфигурациях,
составляют несколько разных приложений. Каждый такой компонент - это пакет OSGi
(OSGi bundle).

Каждый компонент представлен отдельным проектом, находящимся в своей директории
внутри корня. Имя директории совпадает с именем проекта и именем пакета OSGi.


Компоненты системы (в алфавитном порядке)
-----------------------------------------

- `ru.aplix.ltk.collector.blackbox` - сервлет для автоматического тестирования
  накопителя тегов.
- `ru.aplix.ltk.collector.http` - общие классы, используемые клиентом и сервером
  накопителя тегов при взаимодействии через HTTP. Например, классы запросов и
  ответов.
- `ru.aplix.ltk.collector.http.client` - HTTP-клиент накопителя.
- `ru.aplix.ltk.collector.http.server` - HTTP-сервер накопителя.
- `ru.aplix.ltk.core` - основные программные интерфейсы и системы.
- `ru.aplix.ltk.core.test` - тесты основных программный интерфейсов.
- `ru.aplix.ltk.driver.blackbox` - драйвер для ручной отправки данных RFID.
  Используется для автоматического тестировании функциональности.
- `ru.aplix.ltk.driver.ctg` - драйвер непрерывного чтения RFID. Непосредственно
  взаимодействует с ридерами, не разбивая процесс чтения на этапы или
  транзакции.
- `ru.aplix.ltk.driver.ctg.ui` - элементы пользовательского интерфейса для
  использования драйвера непрерывного чтения в приложениях, основанных на Swing.
- `ru.aplix.ltk.driver.dummy` - драйвер-заглушка. Симулирует работу ридера,
  отправляя случайные данные RFID. Используется для ручного тестирования
  функциональности.
- `ru.aplix.ltk.driver.log` - лог отправленных тегов.
- `ru.aplix.ltk.driver.log.test` - тесты лога отправленных тегов.
- `ru.aplix.ltk.message` - библиотека для рассылки сообщений и управления
  подписками на них. Основа программных интерфейсов системы.
- `ru.aplix.ltk.message.test` - тесты библиотеки рассылки сообщений.
- `ru.aplix.ltk.monitor` - служба мониторинга.
- `ru.aplix.ltk.osgi` - различные утилиты для работы на платформе OSGi.
- `ru.aplix.ltk.osgi.log4j` - вывод сообщений служб OSGi в лог посредством
  log4j.
- `ru.aplix.ltk.osgi.shutdown` - перехватчик сигнала завершения приложения
  (`SIGTERM`), корректно завершающий работу платформы OSGi.
- `ru.aplix.ltk.store` - хранилище тегов. Основано на Spring и Eclipse Virgo.
- `ru.aplix.ltk.store.web` - веб-интерфейс хранилища тегов, а также реализация
  компонентов HTTP-клиента накопителя. Основан на Spring MVC и Eclipse Virgo.
- `ru.aplix.ltk.store.web.blackbox` - дополнение веб-интерфейса хранилища тегов
  для запуска автоматических тестов.
- `ru.aplix.ltk.tester` - приложение для тестирования. Основано на Swing.
- `ru.aplix.ltk.ui` - программные интерфейсы для построения пользовательского
  интерфейса взаимодействия с драйверами из приложений, основанных на Swing.

Компоненты с именами, отличными от `ru.aplix.ltk.*` - это внешние зависимости,
для которых не было найдено пакета OSGi.


Прочие директории
-----------------

В корне также присутствуют следующие директории:

- `app` содержит приложения. Каждая поддиректория - это директория приложения.
- `build` содержит скрипты сборки и её настройки.
- `cache` временная директория, используемая ivy в процессе работы, исключена из
  системы контроля версий.
- `doc` содержит документацию.
- `install` содержит скрипты, необходимые для работы приложений на целевых
  системах, например скрипт инициализации накопителя для Debian GNU/Linux.
- `target` директория с результатами сборки (кроме приложений), исключена из
  системы контроля версий.


Сборка
------

Для сборки используются ant и ivy.

При вызове сборки из корневой директории, выполняется сборка всех компонентов
системы, но не приложений.

Каждый компонент также можно собрать отдельно, вызвав сборку в его директории.
При этом будут (пере-) собраны все его зависимости.

Каждое приложение необходимо собирать отдельно, вызвав сборку в его директории.
При этом все необходимые ему компоненты будут (пере-) собраны.
