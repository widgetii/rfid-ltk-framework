Сервер приложений Virgo
=======================

Сервер приложений [Eclipse Virgo][virgo] предназначен для запуска приложений
OSGi, использующий Spring Framework.

Для работы необходима сборка [Virgo Server for Apache Tomcat][download] версии
3.6.2.

После установки, Virgo необходимо модифицировать. Скрипты сборки в этой
директории предназначены для обновления версии компонентов, поставляемой в
составе Virgo, а также для установки компонентов, необходимых хранилищу тегов.

Общие инструкции по обновлению Spring Framework находятся
[здесь][spring_upgrade].

Вызов команды `ant` доставит все необходимые компоненты, а также настройки, в
директорию `target`. Их необходимо скопировать (возможно, с заменой) в
директорию, в которую установлен Virgo, затем следует удалить устаревшие
компоненты Spring и AspectJ, и запустить Virgo с параметром `-clean`.

В директории `install/debian/init.d` есть файл инициализации `virgo`, который
можно использовать для запуска сервера приложений, установленного в директорию
`/opt/virgo` от имени пользователя `virgo` в системе под управлением
Debian GNU/Linux (или Ubuntu).


[virgo]: http://www.eclipse.org/virgo/
[download]: http://www.eclipse.org/virgo/download/
[spring_upgrade]: http://wiki.eclipse.org/Virgo/FAQ#How_can_I_change_the_version_of_Spring_framework_in_the_user_region.3F
